/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.google.inject.Provides
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  net.runelite.api.BufferProvider
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.IntProjection
 *  net.runelite.api.Model
 *  net.runelite.api.Projection
 *  net.runelite.api.Renderable
 *  net.runelite.api.Scene
 *  net.runelite.api.SceneTileModel
 *  net.runelite.api.SceneTilePaint
 *  net.runelite.api.Texture
 *  net.runelite.api.TextureProvider
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.hooks.DrawCallbacks
 *  net.runelite.rlawt.AWTContext
 *  org.lwjgl.opencl.CL10
 *  org.lwjgl.opencl.CL10GL
 *  org.lwjgl.opencl.CL12
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL43C
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLUtil
 *  org.lwjgl.system.Callback
 *  org.lwjgl.system.Configuration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.gpu;

import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.BufferProvider;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IntProjection;
import net.runelite.api.Model;
import net.runelite.api.Projection;
import net.runelite.api.Renderable;
import net.runelite.api.Scene;
import net.runelite.api.SceneTileModel;
import net.runelite.api.SceneTilePaint;
import net.runelite.api.Texture;
import net.runelite.api.TextureProvider;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.gpu.GLBuffer;
import net.runelite.client.plugins.gpu.GpuFloatBuffer;
import net.runelite.client.plugins.gpu.GpuIntBuffer;
import net.runelite.client.plugins.gpu.GpuPluginConfig;
import net.runelite.client.plugins.gpu.Mat4;
import net.runelite.client.plugins.gpu.OpenCLManager;
import net.runelite.client.plugins.gpu.SceneUploader;
import net.runelite.client.plugins.gpu.Shader;
import net.runelite.client.plugins.gpu.ShaderException;
import net.runelite.client.plugins.gpu.TextureManager;
import net.runelite.client.plugins.gpu.config.AntiAliasingMode;
import net.runelite.client.plugins.gpu.config.UIScalingMode;
import net.runelite.client.plugins.gpu.template.Template;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.util.OSType;
import net.runelite.rlawt.AWTContext;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL10GL;
import org.lwjgl.opencl.CL12;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="GPU", description="Utilizes the GPU", enabledByDefault=false, tags={"fog", "draw distance"}, loadInSafeMode=false)
public class GpuPlugin
extends Plugin
implements DrawCallbacks {
    private static final Logger log = LoggerFactory.getLogger(GpuPlugin.class);
    static final int MAX_TRIANGLE = 6144;
    static final int SMALL_TRIANGLE_COUNT = 512;
    private static final int FLAG_SCENE_BUFFER = Integer.MIN_VALUE;
    static final int MAX_DISTANCE = 184;
    static final int MAX_FOG_DEPTH = 100;
    static final int SCENE_OFFSET = 40;
    private static final int GROUND_MIN_Y = 350;
    @Inject
    private Client client;
    @Inject
    private ClientUI clientUI;
    @Inject
    private OpenCLManager openCLManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private GpuPluginConfig config;
    @Inject
    private TextureManager textureManager;
    @Inject
    private SceneUploader sceneUploader;
    @Inject
    private DrawManager drawManager;
    @Inject
    private PluginManager pluginManager;
    private ComputeMode computeMode = ComputeMode.NONE;
    private Canvas canvas;
    private AWTContext awtContext;
    private Callback debugCallback;
    private GLCapabilities glCapabilities;
    static final String LINUX_VERSION_HEADER = "#version 420\n#extension GL_ARB_compute_shader : require\n#extension GL_ARB_shader_storage_buffer_object : require\n#extension GL_ARB_explicit_attrib_location : require\n";
    static final String WINDOWS_VERSION_HEADER = "#version 430\n";
    static final Shader PROGRAM = new Shader().add(35633, "vert.glsl").add(36313, "geom.glsl").add(35632, "frag.glsl");
    static final Shader COMPUTE_PROGRAM = new Shader().add(37305, "comp.glsl");
    static final Shader SMALL_COMPUTE_PROGRAM = new Shader().add(37305, "comp.glsl");
    static final Shader UNORDERED_COMPUTE_PROGRAM = new Shader().add(37305, "comp_unordered.glsl");
    static final Shader UI_PROGRAM = new Shader().add(35633, "vertui.glsl").add(35632, "fragui.glsl");
    private int glProgram;
    private int glComputeProgram;
    private int glSmallComputeProgram;
    private int glUnorderedComputeProgram;
    private int glUiProgram;
    private int vaoCompute;
    private int vaoTemp;
    private int interfaceTexture;
    private int interfacePbo;
    private int vaoUiHandle;
    private int vboUiHandle;
    private int fboScene;
    private int rboColorBuffer;
    private final GLBuffer sceneVertexBuffer = new GLBuffer("scene vertex buffer");
    private final GLBuffer sceneUvBuffer = new GLBuffer("scene tex buffer");
    private final GLBuffer tmpVertexBuffer = new GLBuffer("tmp vertex buffer");
    private final GLBuffer tmpUvBuffer = new GLBuffer("tmp tex buffer");
    private final GLBuffer tmpModelBufferLarge = new GLBuffer("model buffer large");
    private final GLBuffer tmpModelBufferSmall = new GLBuffer("model buffer small");
    private final GLBuffer tmpModelBufferUnordered = new GLBuffer("model buffer unordered");
    private final GLBuffer tmpOutBuffer = new GLBuffer("out vertex buffer");
    private final GLBuffer tmpOutUvBuffer = new GLBuffer("out tex buffer");
    private int textureArrayId;
    private int tileHeightTex;
    private final GLBuffer uniformBuffer = new GLBuffer("uniform buffer");
    private GpuIntBuffer vertexBuffer;
    private GpuFloatBuffer uvBuffer;
    private GpuIntBuffer modelBufferUnordered;
    private GpuIntBuffer modelBufferSmall;
    private GpuIntBuffer modelBuffer;
    private int unorderedModels;
    private int smallModels;
    private int largeModels;
    private int targetBufferOffset;
    private int tempOffset;
    private int tempUvOffset;
    private int lastCanvasWidth;
    private int lastCanvasHeight;
    private int lastStretchedCanvasWidth;
    private int lastStretchedCanvasHeight;
    private AntiAliasingMode lastAntiAliasingMode;
    private int lastAnisotropicFilteringLevel = -1;
    private double cameraX;
    private double cameraY;
    private double cameraZ;
    private double cameraYaw;
    private double cameraPitch;
    private int viewportOffsetX;
    private int viewportOffsetY;
    private int viewportWidth;
    private int viewportHeight;
    private int uniColorBlindMode;
    private int uniUiColorBlindMode;
    private int uniUseFog;
    private int uniFogColor;
    private int uniFogDepth;
    private int uniDrawDistance;
    private int uniExpandedMapLoadingChunks;
    private int uniProjectionMatrix;
    private int uniBrightness;
    private int uniTex;
    private int uniTexSamplingMode;
    private int uniTexSourceDimensions;
    private int uniTexTargetDimensions;
    private int uniUiAlphaOverlay;
    private int uniTextures;
    private int uniTextureAnimations;
    private int uniBlockSmall;
    private int uniBlockLarge;
    private int uniBlockMain;
    private int uniSmoothBanding;
    private int uniTextureLightMode;
    private int uniTick;
    private boolean lwjglInitted = false;
    private int sceneId;
    private int nextSceneId;
    private GpuIntBuffer nextSceneVertexBuffer;
    private GpuFloatBuffer nextSceneTexBuffer;

    @Override
    protected void startUp() {
        this.clientThread.invoke(() -> {
            try {
                this.rboColorBuffer = -1;
                this.fboScene = -1;
                this.targetBufferOffset = 0;
                this.largeModels = 0;
                this.smallModels = 0;
                this.unorderedModels = 0;
                AWTContext.loadNatives();
                this.canvas = this.client.getCanvas();
                Object object = this.canvas.getTreeLock();
                synchronized (object) {
                    if (!this.canvas.isValid()) {
                        return false;
                    }
                    this.awtContext = new AWTContext((Component)this.canvas);
                    this.awtContext.configurePixelFormat(0, 0, 0);
                }
                this.awtContext.createGLContext();
                this.canvas.setIgnoreRepaint(true);
                this.computeMode = this.config.useComputeShaders() ? (OSType.getOSType() == OSType.MacOS ? ComputeMode.OPENCL : ComputeMode.OPENGL) : ComputeMode.NONE;
                Configuration.SHARED_LIBRARY_EXTRACT_DIRECTORY.set((Object)"lwjgl-rl");
                this.glCapabilities = GL.createCapabilities();
                log.info("Using device: {}", (Object)GL43C.glGetString((int)7937));
                log.info("Using driver: {}", (Object)GL43C.glGetString((int)7938));
                if (!this.glCapabilities.OpenGL31) {
                    throw new RuntimeException("OpenGL 3.1 is required but not available");
                }
                if (!this.glCapabilities.OpenGL43 && this.computeMode == ComputeMode.OPENGL) {
                    log.info("disabling compute shaders because OpenGL 4.3 is not available");
                    this.computeMode = ComputeMode.NONE;
                }
                if (this.computeMode == ComputeMode.NONE) {
                    this.sceneUploader.initSortingBuffers();
                }
                this.lwjglInitted = true;
                this.checkGLErrors();
                if (log.isDebugEnabled() && this.glCapabilities.glDebugMessageControl != 0L) {
                    this.debugCallback = GLUtil.setupDebugMessageCallback();
                    if (this.debugCallback != null) {
                        GL43C.glDebugMessageControl((int)33350, (int)33361, (int)4352, (int)131185, (boolean)false);
                        GL43C.glDebugMessageControl((int)33350, (int)33360, (int)4352, (int)131154, (boolean)false);
                    }
                }
                this.vertexBuffer = new GpuIntBuffer();
                this.uvBuffer = new GpuFloatBuffer();
                this.modelBufferUnordered = new GpuIntBuffer();
                this.modelBufferSmall = new GpuIntBuffer();
                this.modelBuffer = new GpuIntBuffer();
                this.setupSyncMode();
                this.initBuffers();
                this.initVao();
                try {
                    this.initProgram();
                }
                catch (ShaderException ex) {
                    throw new RuntimeException(ex);
                }
                this.initInterfaceTexture();
                this.initUniformBuffer();
                this.client.setDrawCallbacks((DrawCallbacks)this);
                this.client.setGpuFlags(1 | (this.computeMode == ComputeMode.NONE ? 0 : 2) | (this.config.removeVertexSnapping() ? 8 : 0));
                this.client.setExpandedMapLoading(this.config.expandedMapLoadingChunks());
                this.client.resizeCanvas();
                this.lastCanvasHeight = -1;
                this.lastCanvasWidth = -1;
                this.lastStretchedCanvasHeight = -1;
                this.lastStretchedCanvasWidth = -1;
                this.lastAntiAliasingMode = null;
                this.textureArrayId = -1;
                if (this.client.getGameState() == GameState.LOGGED_IN) {
                    Scene scene = this.client.getScene();
                    this.loadScene(scene);
                    this.swapScene(scene);
                }
                this.checkGLErrors();
            }
            catch (Throwable e) {
                log.error("Error starting GPU plugin", e);
                SwingUtilities.invokeLater(() -> {
                    try {
                        this.pluginManager.setPluginEnabled(this, false);
                        this.pluginManager.stopPlugin(this);
                    }
                    catch (PluginInstantiationException ex) {
                        log.error("error stopping plugin", (Throwable)ex);
                    }
                });
                this.shutDown();
            }
            return true;
        });
    }

    @Override
    protected void shutDown() {
        this.clientThread.invoke(() -> {
            this.client.setGpuFlags(0);
            this.client.setDrawCallbacks(null);
            this.client.setUnlockedFps(false);
            this.client.setExpandedMapLoading(0);
            this.sceneUploader.releaseSortingBuffers();
            if (this.lwjglInitted) {
                if (this.textureArrayId != -1) {
                    this.textureManager.freeTextureArray(this.textureArrayId);
                    this.textureArrayId = -1;
                }
                if (this.tileHeightTex != 0) {
                    GL43C.glDeleteTextures((int)this.tileHeightTex);
                    this.tileHeightTex = 0;
                }
                this.destroyGlBuffer(this.uniformBuffer);
                this.shutdownInterfaceTexture();
                this.shutdownProgram();
                this.shutdownVao();
                this.shutdownBuffers();
                this.shutdownFbo();
            }
            this.openCLManager.cleanup();
            if (this.awtContext != null) {
                this.awtContext.destroy();
                this.awtContext = null;
            }
            if (this.debugCallback != null) {
                this.debugCallback.free();
                this.debugCallback = null;
            }
            this.glCapabilities = null;
            this.vertexBuffer = null;
            this.uvBuffer = null;
            this.modelBufferSmall = null;
            this.modelBuffer = null;
            this.modelBufferUnordered = null;
            this.lastAnisotropicFilteringLevel = -1;
            this.client.resizeCanvas();
        });
    }

    @Provides
    GpuPluginConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GpuPluginConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("gpu")) {
            if (configChanged.getKey().equals("unlockFps") || configChanged.getKey().equals("vsyncMode") || configChanged.getKey().equals("fpsTarget")) {
                log.debug("Rebuilding sync mode");
                this.clientThread.invokeLater(this::setupSyncMode);
            } else if (configChanged.getKey().equals("expandedMapLoadingChunks")) {
                this.clientThread.invokeLater(() -> {
                    this.client.setExpandedMapLoading(this.config.expandedMapLoadingChunks());
                    if (this.client.getGameState() == GameState.LOGGED_IN) {
                        this.client.setGameState(GameState.LOADING);
                    }
                });
            } else if (configChanged.getKey().equals("removeVertexSnapping")) {
                log.debug("Toggle {}", (Object)configChanged.getKey());
                this.client.setGpuFlags(1 | (this.computeMode == ComputeMode.NONE ? 0 : 2) | (this.config.removeVertexSnapping() ? 8 : 0));
            }
        }
    }

    private void setupSyncMode() {
        boolean unlockFps = this.config.unlockFps();
        this.client.setUnlockedFps(unlockFps);
        GpuPluginConfig.SyncMode syncMode = unlockFps ? this.config.syncMode() : GpuPluginConfig.SyncMode.OFF;
        int swapInterval = 0;
        switch (syncMode) {
            case ON: {
                swapInterval = 1;
                break;
            }
            case OFF: {
                swapInterval = 0;
                break;
            }
            case ADAPTIVE: {
                swapInterval = -1;
            }
        }
        int actualSwapInterval = this.awtContext.setSwapInterval(swapInterval);
        if (actualSwapInterval != swapInterval) {
            log.info("unsupported swap interval {}, got {}", (Object)swapInterval, (Object)actualSwapInterval);
        }
        this.client.setUnlockedFpsTarget(actualSwapInterval == 0 ? this.config.fpsTarget() : 0);
        this.checkGLErrors();
    }

    private Template createTemplate(int threadCount, int facesPerThread) {
        String versionHeader = OSType.getOSType() == OSType.Linux ? LINUX_VERSION_HEADER : WINDOWS_VERSION_HEADER;
        Template template = new Template();
        template.add(key -> {
            if ("version_header".equals(key)) {
                return versionHeader;
            }
            if ("thread_config".equals(key)) {
                return "#define THREAD_COUNT " + threadCount + "\n#define FACES_PER_THREAD " + facesPerThread + "\n";
            }
            if ("texture_config".equals(key)) {
                return "#define TEXTURE_COUNT 256\n";
            }
            return null;
        });
        template.addInclude(GpuPlugin.class);
        return template;
    }

    private void initProgram() throws ShaderException {
        Template template = this.createTemplate(-1, -1);
        this.glProgram = PROGRAM.compile(template);
        this.glUiProgram = UI_PROGRAM.compile(template);
        if (this.computeMode == ComputeMode.OPENGL) {
            this.glComputeProgram = COMPUTE_PROGRAM.compile(this.createTemplate(1024, 6));
            this.glSmallComputeProgram = SMALL_COMPUTE_PROGRAM.compile(this.createTemplate(512, 1));
            this.glUnorderedComputeProgram = UNORDERED_COMPUTE_PROGRAM.compile(template);
        } else if (this.computeMode == ComputeMode.OPENCL) {
            this.openCLManager.init(this.awtContext);
        }
        this.initUniforms();
    }

    private void initUniforms() {
        this.uniProjectionMatrix = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"projectionMatrix");
        this.uniBrightness = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"brightness");
        this.uniSmoothBanding = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"smoothBanding");
        this.uniUseFog = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"useFog");
        this.uniFogColor = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"fogColor");
        this.uniFogDepth = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"fogDepth");
        this.uniDrawDistance = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"drawDistance");
        this.uniExpandedMapLoadingChunks = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"expandedMapLoadingChunks");
        this.uniColorBlindMode = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"colorBlindMode");
        this.uniTextureLightMode = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"textureLightMode");
        this.uniTick = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"tick");
        this.uniBlockMain = GL43C.glGetUniformBlockIndex((int)this.glProgram, (CharSequence)"uniforms");
        this.uniTextures = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"textures");
        this.uniTextureAnimations = GL43C.glGetUniformLocation((int)this.glProgram, (CharSequence)"textureAnimations");
        this.uniTex = GL43C.glGetUniformLocation((int)this.glUiProgram, (CharSequence)"tex");
        this.uniTexSamplingMode = GL43C.glGetUniformLocation((int)this.glUiProgram, (CharSequence)"samplingMode");
        this.uniTexTargetDimensions = GL43C.glGetUniformLocation((int)this.glUiProgram, (CharSequence)"targetDimensions");
        this.uniTexSourceDimensions = GL43C.glGetUniformLocation((int)this.glUiProgram, (CharSequence)"sourceDimensions");
        this.uniUiColorBlindMode = GL43C.glGetUniformLocation((int)this.glUiProgram, (CharSequence)"colorBlindMode");
        this.uniUiAlphaOverlay = GL43C.glGetUniformLocation((int)this.glUiProgram, (CharSequence)"alphaOverlay");
        if (this.computeMode == ComputeMode.OPENGL) {
            this.uniBlockSmall = GL43C.glGetUniformBlockIndex((int)this.glSmallComputeProgram, (CharSequence)"uniforms");
            this.uniBlockLarge = GL43C.glGetUniformBlockIndex((int)this.glComputeProgram, (CharSequence)"uniforms");
        }
    }

    private void shutdownProgram() {
        GL43C.glDeleteProgram((int)this.glProgram);
        this.glProgram = -1;
        GL43C.glDeleteProgram((int)this.glComputeProgram);
        this.glComputeProgram = -1;
        GL43C.glDeleteProgram((int)this.glSmallComputeProgram);
        this.glSmallComputeProgram = -1;
        GL43C.glDeleteProgram((int)this.glUnorderedComputeProgram);
        this.glUnorderedComputeProgram = -1;
        GL43C.glDeleteProgram((int)this.glUiProgram);
        this.glUiProgram = -1;
    }

    private void initVao() {
        this.vaoCompute = GL43C.glGenVertexArrays();
        GL43C.glBindVertexArray((int)this.vaoCompute);
        GL43C.glEnableVertexAttribArray((int)0);
        GL43C.glBindBuffer((int)34962, (int)this.tmpOutBuffer.glBufferId);
        GL43C.glVertexAttribPointer((int)0, (int)3, (int)5126, (boolean)false, (int)16, (long)0L);
        GL43C.glEnableVertexAttribArray((int)1);
        GL43C.glBindBuffer((int)34962, (int)this.tmpOutBuffer.glBufferId);
        GL43C.glVertexAttribIPointer((int)1, (int)1, (int)5124, (int)16, (long)12L);
        GL43C.glEnableVertexAttribArray((int)2);
        GL43C.glBindBuffer((int)34962, (int)this.tmpOutUvBuffer.glBufferId);
        GL43C.glVertexAttribPointer((int)2, (int)4, (int)5126, (boolean)false, (int)0, (long)0L);
        this.vaoTemp = GL43C.glGenVertexArrays();
        GL43C.glBindVertexArray((int)this.vaoTemp);
        GL43C.glEnableVertexAttribArray((int)0);
        GL43C.glBindBuffer((int)34962, (int)this.tmpVertexBuffer.glBufferId);
        GL43C.glVertexAttribPointer((int)0, (int)3, (int)5126, (boolean)false, (int)16, (long)0L);
        GL43C.glEnableVertexAttribArray((int)1);
        GL43C.glBindBuffer((int)34962, (int)this.tmpVertexBuffer.glBufferId);
        GL43C.glVertexAttribIPointer((int)1, (int)1, (int)5124, (int)16, (long)12L);
        GL43C.glEnableVertexAttribArray((int)2);
        GL43C.glBindBuffer((int)34962, (int)this.tmpUvBuffer.glBufferId);
        GL43C.glVertexAttribPointer((int)2, (int)4, (int)5126, (boolean)false, (int)0, (long)0L);
        this.vaoUiHandle = GL43C.glGenVertexArrays();
        this.vboUiHandle = GL43C.glGenBuffers();
        GL43C.glBindVertexArray((int)this.vaoUiHandle);
        FloatBuffer vboUiBuf = GpuFloatBuffer.allocateDirect(20);
        vboUiBuf.put(new float[]{1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f});
        vboUiBuf.rewind();
        GL43C.glBindBuffer((int)34962, (int)this.vboUiHandle);
        GL43C.glBufferData((int)34962, (FloatBuffer)vboUiBuf, (int)35044);
        GL43C.glVertexAttribPointer((int)0, (int)3, (int)5126, (boolean)false, (int)20, (long)0L);
        GL43C.glEnableVertexAttribArray((int)0);
        GL43C.glVertexAttribPointer((int)1, (int)2, (int)5126, (boolean)false, (int)20, (long)12L);
        GL43C.glEnableVertexAttribArray((int)1);
        GL43C.glBindBuffer((int)34962, (int)0);
    }

    private void shutdownVao() {
        GL43C.glDeleteVertexArrays((int)this.vaoCompute);
        this.vaoCompute = -1;
        GL43C.glDeleteVertexArrays((int)this.vaoTemp);
        this.vaoTemp = -1;
        GL43C.glDeleteBuffers((int)this.vboUiHandle);
        this.vboUiHandle = -1;
        GL43C.glDeleteVertexArrays((int)this.vaoUiHandle);
        this.vaoUiHandle = -1;
    }

    private void initBuffers() {
        this.initGlBuffer(this.sceneVertexBuffer);
        this.initGlBuffer(this.sceneUvBuffer);
        this.initGlBuffer(this.tmpVertexBuffer);
        this.initGlBuffer(this.tmpUvBuffer);
        this.initGlBuffer(this.tmpModelBufferLarge);
        this.initGlBuffer(this.tmpModelBufferSmall);
        this.initGlBuffer(this.tmpModelBufferUnordered);
        this.initGlBuffer(this.tmpOutBuffer);
        this.initGlBuffer(this.tmpOutUvBuffer);
    }

    private void initGlBuffer(GLBuffer glBuffer) {
        glBuffer.glBufferId = GL43C.glGenBuffers();
    }

    private void shutdownBuffers() {
        this.destroyGlBuffer(this.sceneVertexBuffer);
        this.destroyGlBuffer(this.sceneUvBuffer);
        this.destroyGlBuffer(this.tmpVertexBuffer);
        this.destroyGlBuffer(this.tmpUvBuffer);
        this.destroyGlBuffer(this.tmpModelBufferLarge);
        this.destroyGlBuffer(this.tmpModelBufferSmall);
        this.destroyGlBuffer(this.tmpModelBufferUnordered);
        this.destroyGlBuffer(this.tmpOutBuffer);
        this.destroyGlBuffer(this.tmpOutUvBuffer);
    }

    private void destroyGlBuffer(GLBuffer glBuffer) {
        if (glBuffer.glBufferId != -1) {
            GL43C.glDeleteBuffers((int)glBuffer.glBufferId);
            glBuffer.glBufferId = -1;
        }
        glBuffer.size = -1;
        if (glBuffer.clBuffer != -1L) {
            CL12.clReleaseMemObject((long)glBuffer.clBuffer);
            glBuffer.clBuffer = -1L;
        }
    }

    private void initInterfaceTexture() {
        this.interfacePbo = GL43C.glGenBuffers();
        this.interfaceTexture = GL43C.glGenTextures();
        GL43C.glBindTexture((int)3553, (int)this.interfaceTexture);
        GL43C.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL43C.glTexParameteri((int)3553, (int)10243, (int)33071);
        GL43C.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL43C.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL43C.glBindTexture((int)3553, (int)0);
    }

    private void shutdownInterfaceTexture() {
        GL43C.glDeleteBuffers((int)this.interfacePbo);
        GL43C.glDeleteTextures((int)this.interfaceTexture);
        this.interfaceTexture = -1;
    }

    private void initUniformBuffer() {
        this.initGlBuffer(this.uniformBuffer);
        this.updateBuffer(this.uniformBuffer, 35345, 32, 35048, 4L);
        GL43C.glBindBuffer((int)35345, (int)0);
    }

    private void initFbo(int width, int height, int aaSamples) {
        GraphicsConfiguration graphicsConfiguration = this.clientUI.getGraphicsConfiguration();
        AffineTransform transform = graphicsConfiguration.getDefaultTransform();
        width = this.getScaledValue(transform.getScaleX(), width);
        height = this.getScaledValue(transform.getScaleY(), height);
        if (aaSamples > 0) {
            GL43C.glEnable((int)32925);
        } else {
            GL43C.glDisable((int)32925);
        }
        this.fboScene = GL43C.glGenFramebuffers();
        GL43C.glBindFramebuffer((int)36160, (int)this.fboScene);
        this.rboColorBuffer = GL43C.glGenRenderbuffers();
        GL43C.glBindRenderbuffer((int)36161, (int)this.rboColorBuffer);
        GL43C.glRenderbufferStorageMultisample((int)36161, (int)aaSamples, (int)6408, (int)width, (int)height);
        GL43C.glFramebufferRenderbuffer((int)36160, (int)36064, (int)36161, (int)this.rboColorBuffer);
        int status = GL43C.glCheckFramebufferStatus((int)36160);
        if (status != 36053) {
            throw new RuntimeException("FBO is incomplete. status: " + status);
        }
        GL43C.glBindFramebuffer((int)36160, (int)this.awtContext.getFramebuffer(false));
        GL43C.glBindRenderbuffer((int)36161, (int)0);
    }

    private void shutdownFbo() {
        if (this.fboScene != -1) {
            GL43C.glDeleteFramebuffers((int)this.fboScene);
            this.fboScene = -1;
        }
        if (this.rboColorBuffer != -1) {
            GL43C.glDeleteRenderbuffers((int)this.rboColorBuffer);
            this.rboColorBuffer = -1;
        }
    }

    public void drawScene(double cameraX, double cameraY, double cameraZ, double cameraPitch, double cameraYaw, int plane) {
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
        this.cameraPitch = cameraPitch;
        this.cameraYaw = cameraYaw;
        this.viewportOffsetX = this.client.getViewportXOffset();
        this.viewportOffsetY = this.client.getViewportYOffset();
        this.viewportWidth = this.client.getViewportWidth();
        this.viewportHeight = this.client.getViewportHeight();
        Scene scene = this.client.getScene();
        scene.setDrawDistance(this.getDrawDistance());
        this.targetBufferOffset = 0;
        this.vertexBuffer.clear();
        this.vertexBuffer.ensureCapacity(32);
        IntBuffer uniformBuf = this.vertexBuffer.getBuffer();
        uniformBuf.put(Float.floatToIntBits((float)cameraYaw)).put(Float.floatToIntBits((float)cameraPitch)).put(this.client.getCenterX()).put(this.client.getCenterY()).put(this.client.getScale()).put(Float.floatToIntBits((float)cameraX)).put(Float.floatToIntBits((float)cameraY)).put(Float.floatToIntBits((float)cameraZ));
        uniformBuf.flip();
        this.updateBuffer(this.uniformBuffer, 35345, uniformBuf, 35048, 4L);
        GL43C.glBindBuffer((int)35345, (int)0);
        GL43C.glBindBufferBase((int)35345, (int)0, (int)this.uniformBuffer.glBufferId);
        uniformBuf.clear();
        this.checkGLErrors();
    }

    public void postDrawScene() {
        if (this.computeMode == ComputeMode.NONE) {
            this.vertexBuffer.flip();
            this.uvBuffer.flip();
            IntBuffer vertexBuffer = this.vertexBuffer.getBuffer();
            FloatBuffer uvBuffer = this.uvBuffer.getBuffer();
            this.updateBuffer(this.tmpVertexBuffer, 34962, vertexBuffer, 35048, 0L);
            this.updateBuffer(this.tmpUvBuffer, 34962, uvBuffer, 35048, 0L);
            this.checkGLErrors();
            return;
        }
        this.vertexBuffer.flip();
        this.uvBuffer.flip();
        this.modelBuffer.flip();
        this.modelBufferSmall.flip();
        this.modelBufferUnordered.flip();
        IntBuffer vertexBuffer = this.vertexBuffer.getBuffer();
        FloatBuffer uvBuffer = this.uvBuffer.getBuffer();
        IntBuffer modelBuffer = this.modelBuffer.getBuffer();
        IntBuffer modelBufferSmall = this.modelBufferSmall.getBuffer();
        IntBuffer modelBufferUnordered = this.modelBufferUnordered.getBuffer();
        this.updateBuffer(this.tmpVertexBuffer, 34962, vertexBuffer, 35048, 4L);
        this.updateBuffer(this.tmpUvBuffer, 34962, uvBuffer, 35048, 4L);
        this.updateBuffer(this.tmpModelBufferLarge, 34962, modelBuffer, 35048, 4L);
        this.updateBuffer(this.tmpModelBufferSmall, 34962, modelBufferSmall, 35048, 4L);
        this.updateBuffer(this.tmpModelBufferUnordered, 34962, modelBufferUnordered, 35048, 4L);
        this.updateBuffer(this.tmpOutBuffer, 34962, this.targetBufferOffset * 16, 35040, 2L);
        this.updateBuffer(this.tmpOutUvBuffer, 34962, this.targetBufferOffset * 16, 35040, 2L);
        if (this.computeMode == ComputeMode.OPENCL) {
            this.openCLManager.compute(this.unorderedModels, this.smallModels, this.largeModels, this.sceneVertexBuffer, this.sceneUvBuffer, this.tmpVertexBuffer, this.tmpUvBuffer, this.tmpModelBufferUnordered, this.tmpModelBufferSmall, this.tmpModelBufferLarge, this.tmpOutBuffer, this.tmpOutUvBuffer, this.uniformBuffer);
            this.checkGLErrors();
            return;
        }
        GL43C.glUniformBlockBinding((int)this.glSmallComputeProgram, (int)this.uniBlockSmall, (int)0);
        GL43C.glUniformBlockBinding((int)this.glComputeProgram, (int)this.uniBlockLarge, (int)0);
        GL43C.glUseProgram((int)this.glUnorderedComputeProgram);
        GL43C.glBindBufferBase((int)37074, (int)0, (int)this.tmpModelBufferUnordered.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)1, (int)this.sceneVertexBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)2, (int)this.tmpVertexBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)3, (int)this.tmpOutBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)4, (int)this.tmpOutUvBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)5, (int)this.sceneUvBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)6, (int)this.tmpUvBuffer.glBufferId);
        GL43C.glDispatchCompute((int)this.unorderedModels, (int)1, (int)1);
        GL43C.glUseProgram((int)this.glSmallComputeProgram);
        GL43C.glBindBufferBase((int)37074, (int)0, (int)this.tmpModelBufferSmall.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)1, (int)this.sceneVertexBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)2, (int)this.tmpVertexBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)3, (int)this.tmpOutBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)4, (int)this.tmpOutUvBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)5, (int)this.sceneUvBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)6, (int)this.tmpUvBuffer.glBufferId);
        GL43C.glDispatchCompute((int)this.smallModels, (int)1, (int)1);
        GL43C.glUseProgram((int)this.glComputeProgram);
        GL43C.glBindBufferBase((int)37074, (int)0, (int)this.tmpModelBufferLarge.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)1, (int)this.sceneVertexBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)2, (int)this.tmpVertexBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)3, (int)this.tmpOutBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)4, (int)this.tmpOutUvBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)5, (int)this.sceneUvBuffer.glBufferId);
        GL43C.glBindBufferBase((int)37074, (int)6, (int)this.tmpUvBuffer.glBufferId);
        GL43C.glDispatchCompute((int)this.largeModels, (int)1, (int)1);
        this.checkGLErrors();
    }

    public void drawScenePaint(Scene scene, SceneTilePaint paint, int plane, int tileX, int tileY) {
        if (this.computeMode == ComputeMode.NONE) {
            this.targetBufferOffset += this.sceneUploader.upload(scene, paint, plane, tileX, tileY, this.vertexBuffer, this.uvBuffer, tileX << 7, tileY << 7, true);
        } else if (paint.getBufferLen() > 0) {
            int localX = tileX << 7;
            boolean localY = false;
            int localZ = tileY << 7;
            GpuIntBuffer b = this.modelBufferUnordered;
            ++this.unorderedModels;
            b.ensureCapacity(8);
            IntBuffer buffer = b.getBuffer();
            buffer.put(paint.getBufferOffset());
            buffer.put(paint.getUvBufferOffset());
            buffer.put(2);
            buffer.put(this.targetBufferOffset);
            buffer.put(Integer.MIN_VALUE);
            buffer.put(localX).put(0).put(localZ);
            this.targetBufferOffset += 6;
        }
    }

    public void drawSceneTileModel(Scene scene, SceneTileModel model, int tileX, int tileY) {
        if (this.computeMode == ComputeMode.NONE) {
            this.targetBufferOffset += this.sceneUploader.upload(model, 0, 0, this.vertexBuffer, this.uvBuffer, true);
        } else if (model.getBufferLen() > 0) {
            int localX = tileX << 7;
            boolean localY = false;
            int localZ = tileY << 7;
            GpuIntBuffer b = this.modelBufferUnordered;
            ++this.unorderedModels;
            b.ensureCapacity(8);
            IntBuffer buffer = b.getBuffer();
            buffer.put(model.getBufferOffset());
            buffer.put(model.getUvBufferOffset());
            buffer.put(model.getBufferLen() / 3);
            buffer.put(this.targetBufferOffset);
            buffer.put(Integer.MIN_VALUE);
            buffer.put(localX).put(0).put(localZ);
            this.targetBufferOffset += model.getBufferLen();
        }
    }

    private void prepareInterfaceTexture(int canvasWidth, int canvasHeight) {
        if (canvasWidth != this.lastCanvasWidth || canvasHeight != this.lastCanvasHeight) {
            this.lastCanvasWidth = canvasWidth;
            this.lastCanvasHeight = canvasHeight;
            GL43C.glBindBuffer((int)35052, (int)this.interfacePbo);
            GL43C.glBufferData((int)35052, (long)((long)(canvasWidth * canvasHeight) * 4L), (int)35040);
            GL43C.glBindBuffer((int)35052, (int)0);
            GL43C.glBindTexture((int)3553, (int)this.interfaceTexture);
            GL43C.glTexImage2D((int)3553, (int)0, (int)6408, (int)canvasWidth, (int)canvasHeight, (int)0, (int)32993, (int)5121, (long)0L);
            GL43C.glBindTexture((int)3553, (int)0);
        }
        BufferProvider bufferProvider = this.client.getBufferProvider();
        int[] pixels = bufferProvider.getPixels();
        int width = bufferProvider.getWidth();
        int height = bufferProvider.getHeight();
        GL43C.glBindBuffer((int)35052, (int)this.interfacePbo);
        ByteBuffer interfaceBuf = GL43C.glMapBuffer((int)35052, (int)35001);
        if (interfaceBuf != null) {
            interfaceBuf.asIntBuffer().put(pixels, 0, width * height);
            GL43C.glUnmapBuffer((int)35052);
        }
        GL43C.glBindTexture((int)3553, (int)this.interfaceTexture);
        GL43C.glTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)width, (int)height, (int)32993, (int)33639, (long)0L);
        GL43C.glBindBuffer((int)35052, (int)0);
        GL43C.glBindTexture((int)3553, (int)0);
    }

    public void draw(int overlayColor) {
        int stretchedCanvasHeight;
        GameState gameState = this.client.getGameState();
        if (gameState == GameState.STARTING) {
            return;
        }
        int canvasHeight = this.client.getCanvasHeight();
        int canvasWidth = this.client.getCanvasWidth();
        this.prepareInterfaceTexture(canvasWidth, canvasHeight);
        AntiAliasingMode antiAliasingMode = this.config.antiAliasingMode();
        Dimension stretchedDimensions = this.client.getStretchedDimensions();
        int stretchedCanvasWidth = this.client.isStretchedEnabled() ? stretchedDimensions.width : canvasWidth;
        int n = stretchedCanvasHeight = this.client.isStretchedEnabled() ? stretchedDimensions.height : canvasHeight;
        if (this.lastStretchedCanvasWidth != stretchedCanvasWidth || this.lastStretchedCanvasHeight != stretchedCanvasHeight || this.lastAntiAliasingMode != antiAliasingMode) {
            this.shutdownFbo();
            GL43C.glBindFramebuffer((int)36160, (int)this.awtContext.getFramebuffer(false));
            int forcedAASamples = GL43C.glGetInteger((int)32937);
            int maxSamples = GL43C.glGetInteger((int)36183);
            int samples = forcedAASamples != 0 ? forcedAASamples : Math.min(antiAliasingMode.getSamples(), maxSamples);
            log.debug("AA samples: {}, max samples: {}, forced samples: {}", new Object[]{samples, maxSamples, forcedAASamples});
            this.initFbo(stretchedCanvasWidth, stretchedCanvasHeight, samples);
            this.lastStretchedCanvasWidth = stretchedCanvasWidth;
            this.lastStretchedCanvasHeight = stretchedCanvasHeight;
            this.lastAntiAliasingMode = antiAliasingMode;
        }
        GL43C.glBindFramebuffer((int)36009, (int)this.fboScene);
        int sky = this.client.getSkyboxColor();
        GL43C.glClearColor((float)((float)(sky >> 16 & 0xFF) / 255.0f), (float)((float)(sky >> 8 & 0xFF) / 255.0f), (float)((float)(sky & 0xFF) / 255.0f), (float)1.0f);
        GL43C.glClear((int)16384);
        if (gameState.getState() >= GameState.LOADING.getState()) {
            TextureProvider textureProvider = this.client.getTextureProvider();
            if (this.textureArrayId == -1) {
                this.textureArrayId = this.textureManager.initTextureArray(textureProvider);
                if (this.textureArrayId > -1) {
                    float[] texAnims = this.textureManager.computeTextureAnimations(textureProvider);
                    GL43C.glUseProgram((int)this.glProgram);
                    GL43C.glUniform2fv((int)this.uniTextureAnimations, (float[])texAnims);
                    GL43C.glUseProgram((int)0);
                }
            }
            int renderWidthOff = this.viewportOffsetX;
            int renderHeightOff = this.viewportOffsetY;
            int renderCanvasHeight = canvasHeight;
            int renderViewportHeight = this.viewportHeight;
            int renderViewportWidth = this.viewportWidth;
            int anisotropicFilteringLevel = this.config.anisotropicFilteringLevel();
            if (this.textureArrayId != -1 && this.lastAnisotropicFilteringLevel != anisotropicFilteringLevel) {
                this.textureManager.setAnisotropicFilteringLevel(this.textureArrayId, anisotropicFilteringLevel);
                this.lastAnisotropicFilteringLevel = anisotropicFilteringLevel;
            }
            if (this.client.isStretchedEnabled()) {
                Dimension dim = this.client.getStretchedDimensions();
                renderCanvasHeight = dim.height;
                double scaleFactorY = dim.getHeight() / (double)canvasHeight;
                double scaleFactorX = dim.getWidth() / (double)canvasWidth;
                boolean padding = true;
                renderViewportHeight = (int)Math.ceil(scaleFactorY * (double)renderViewportHeight) + 2;
                renderViewportWidth = (int)Math.ceil(scaleFactorX * (double)renderViewportWidth) + 2;
                renderHeightOff = (int)Math.floor(scaleFactorY * (double)renderHeightOff) - 1;
                renderWidthOff = (int)Math.floor(scaleFactorX * (double)renderWidthOff) - 1;
            }
            this.glDpiAwareViewport(renderWidthOff, renderCanvasHeight - renderViewportHeight - renderHeightOff, renderViewportWidth, renderViewportHeight);
            GL43C.glUseProgram((int)this.glProgram);
            int drawDistance = this.getDrawDistance();
            int fogDepth = this.config.fogDepth();
            GL43C.glUniform1i((int)this.uniUseFog, (int)(fogDepth > 0 ? 1 : 0));
            GL43C.glUniform4f((int)this.uniFogColor, (float)((float)(sky >> 16 & 0xFF) / 255.0f), (float)((float)(sky >> 8 & 0xFF) / 255.0f), (float)((float)(sky & 0xFF) / 255.0f), (float)1.0f);
            GL43C.glUniform1i((int)this.uniFogDepth, (int)fogDepth);
            GL43C.glUniform1i((int)this.uniDrawDistance, (int)(drawDistance * 128));
            GL43C.glUniform1i((int)this.uniExpandedMapLoadingChunks, (int)this.client.getExpandedMapLoading());
            GL43C.glUniform1f((int)this.uniBrightness, (float)((float)textureProvider.getBrightness()));
            GL43C.glUniform1f((int)this.uniSmoothBanding, (float)(this.config.smoothBanding() ? 0.0f : 1.0f));
            GL43C.glUniform1i((int)this.uniColorBlindMode, (int)this.config.colorBlindMode().ordinal());
            GL43C.glUniform1f((int)this.uniTextureLightMode, (float)(this.config.brightTextures() ? 1.0f : 0.0f));
            if (gameState == GameState.LOGGED_IN) {
                GL43C.glUniform1i((int)this.uniTick, (int)(this.client.getGameCycle() & 0x7F));
            }
            float[] projectionMatrix = Mat4.scale(this.client.getScale(), this.client.getScale(), 1.0f);
            Mat4.mul(projectionMatrix, Mat4.projection(this.viewportWidth, this.viewportHeight, 50.0f));
            Mat4.mul(projectionMatrix, Mat4.rotateX((float)this.cameraPitch));
            Mat4.mul(projectionMatrix, Mat4.rotateY((float)this.cameraYaw));
            Mat4.mul(projectionMatrix, Mat4.translate((float)(-this.cameraX), (float)(-this.cameraY), (float)(-this.cameraZ)));
            GL43C.glUniformMatrix4fv((int)this.uniProjectionMatrix, (boolean)false, (float[])projectionMatrix);
            GL43C.glUniformBlockBinding((int)this.glProgram, (int)this.uniBlockMain, (int)0);
            GL43C.glUniform1i((int)this.uniTextures, (int)1);
            GL43C.glEnable((int)2884);
            GL43C.glEnable((int)3042);
            GL43C.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)1);
            if (this.computeMode != ComputeMode.NONE) {
                if (this.computeMode == ComputeMode.OPENGL) {
                    GL43C.glMemoryBarrier((int)8192);
                } else {
                    this.openCLManager.finish();
                }
                GL43C.glBindVertexArray((int)this.vaoCompute);
            } else {
                GL43C.glBindVertexArray((int)this.vaoTemp);
            }
            GL43C.glDrawArrays((int)4, (int)0, (int)this.targetBufferOffset);
            GL43C.glDisable((int)3042);
            GL43C.glDisable((int)2884);
            GL43C.glUseProgram((int)0);
        }
        int width = this.lastStretchedCanvasWidth;
        int height = this.lastStretchedCanvasHeight;
        GraphicsConfiguration graphicsConfiguration = this.clientUI.getGraphicsConfiguration();
        AffineTransform transform = graphicsConfiguration.getDefaultTransform();
        width = this.getScaledValue(transform.getScaleX(), width);
        height = this.getScaledValue(transform.getScaleY(), height);
        GL43C.glBindFramebuffer((int)36008, (int)this.fboScene);
        GL43C.glBindFramebuffer((int)36009, (int)this.awtContext.getFramebuffer(false));
        GL43C.glBlitFramebuffer((int)0, (int)0, (int)width, (int)height, (int)0, (int)0, (int)width, (int)height, (int)16384, (int)9728);
        GL43C.glBindFramebuffer((int)36008, (int)this.awtContext.getFramebuffer(false));
        this.vertexBuffer.clear();
        this.uvBuffer.clear();
        this.modelBuffer.clear();
        this.modelBufferSmall.clear();
        this.modelBufferUnordered.clear();
        this.unorderedModels = 0;
        this.largeModels = 0;
        this.smallModels = 0;
        this.tempOffset = 0;
        this.tempUvOffset = 0;
        this.drawUi(overlayColor, canvasHeight, canvasWidth);
        try {
            this.awtContext.swapBuffers();
        }
        catch (RuntimeException ex) {
            if (!this.canvas.isValid()) {
                return;
            }
            log.error("error swapping buffers", (Throwable)ex);
            SwingUtilities.invokeLater(() -> {
                try {
                    this.pluginManager.stopPlugin(this);
                }
                catch (PluginInstantiationException ex2) {
                    log.error("error stopping plugin", (Throwable)ex2);
                }
            });
            return;
        }
        this.drawManager.processDrawComplete(this::screenshot);
        GL43C.glBindFramebuffer((int)36160, (int)this.awtContext.getFramebuffer(false));
        this.checkGLErrors();
    }

    private void drawUi(int overlayColor, int canvasHeight, int canvasWidth) {
        GL43C.glEnable((int)3042);
        GL43C.glBlendFunc((int)1, (int)771);
        GL43C.glBindTexture((int)3553, (int)this.interfaceTexture);
        UIScalingMode uiScalingMode = this.config.uiScalingMode();
        GL43C.glUseProgram((int)this.glUiProgram);
        GL43C.glUniform1i((int)this.uniTex, (int)0);
        GL43C.glUniform1i((int)this.uniTexSamplingMode, (int)uiScalingMode.getMode());
        GL43C.glUniform2i((int)this.uniTexSourceDimensions, (int)canvasWidth, (int)canvasHeight);
        GL43C.glUniform1i((int)this.uniUiColorBlindMode, (int)this.config.colorBlindMode().ordinal());
        GL43C.glUniform4f((int)this.uniUiAlphaOverlay, (float)((float)(overlayColor >> 16 & 0xFF) / 255.0f), (float)((float)(overlayColor >> 8 & 0xFF) / 255.0f), (float)((float)(overlayColor & 0xFF) / 255.0f), (float)((float)(overlayColor >>> 24) / 255.0f));
        if (this.client.isStretchedEnabled()) {
            Dimension dim = this.client.getStretchedDimensions();
            this.glDpiAwareViewport(0, 0, dim.width, dim.height);
            GL43C.glUniform2i((int)this.uniTexTargetDimensions, (int)dim.width, (int)dim.height);
        } else {
            this.glDpiAwareViewport(0, 0, canvasWidth, canvasHeight);
            GL43C.glUniform2i((int)this.uniTexTargetDimensions, (int)canvasWidth, (int)canvasHeight);
        }
        int function = uiScalingMode == UIScalingMode.LINEAR ? 9729 : 9728;
        GL43C.glTexParameteri((int)3553, (int)10241, (int)function);
        GL43C.glTexParameteri((int)3553, (int)10240, (int)function);
        GL43C.glBindVertexArray((int)this.vaoUiHandle);
        GL43C.glDrawArrays((int)6, (int)0, (int)4);
        GL43C.glBindTexture((int)3553, (int)0);
        GL43C.glBindVertexArray((int)0);
        GL43C.glUseProgram((int)0);
        GL43C.glBlendFunc((int)770, (int)771);
        GL43C.glDisable((int)3042);
    }

    private Image screenshot() {
        int width = this.client.getCanvasWidth();
        int height = this.client.getCanvasHeight();
        if (this.client.isStretchedEnabled()) {
            Dimension dim = this.client.getStretchedDimensions();
            width = dim.width;
            height = dim.height;
        }
        GraphicsConfiguration graphicsConfiguration = this.clientUI.getGraphicsConfiguration();
        AffineTransform t = graphicsConfiguration.getDefaultTransform();
        width = this.getScaledValue(t.getScaleX(), width);
        height = this.getScaledValue(t.getScaleY(), height);
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
        GL43C.glReadBuffer((int)this.awtContext.getBufferMode());
        GL43C.glReadPixels((int)0, (int)0, (int)width, (int)height, (int)6408, (int)5121, (ByteBuffer)buffer);
        BufferedImage image = new BufferedImage(width, height, 1);
        int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = buffer.get() & 0xFF;
                int g = buffer.get() & 0xFF;
                int b = buffer.get() & 0xFF;
                buffer.get();
                pixels[(height - y - 1) * width + x] = r << 16 | g << 8 | b;
            }
        }
        return image;
    }

    public void animate(Texture texture, int diff) {
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            this.targetBufferOffset = 0;
        }
        if (gameStateChanged.getGameState() == GameState.STARTING) {
            if (this.textureArrayId != -1) {
                this.textureManager.freeTextureArray(this.textureArrayId);
            }
            this.textureArrayId = -1;
            this.lastAnisotropicFilteringLevel = -1;
        }
    }

    public void loadScene(Scene scene) {
        if (this.computeMode == ComputeMode.NONE) {
            return;
        }
        GpuIntBuffer vertexBuffer = new GpuIntBuffer();
        GpuFloatBuffer uvBuffer = new GpuFloatBuffer();
        this.sceneUploader.upload(scene, vertexBuffer, uvBuffer);
        vertexBuffer.flip();
        uvBuffer.flip();
        this.nextSceneVertexBuffer = vertexBuffer;
        this.nextSceneTexBuffer = uvBuffer;
        this.nextSceneId = this.sceneUploader.sceneId;
    }

    private void uploadTileHeights(Scene scene) {
        if (this.tileHeightTex != 0) {
            GL43C.glDeleteTextures((int)this.tileHeightTex);
            this.tileHeightTex = 0;
        }
        int TILEHEIGHT_BUFFER_SIZE = 270848;
        ShortBuffer tileBuffer = ByteBuffer.allocateDirect(270848).order(ByteOrder.nativeOrder()).asShortBuffer();
        int[][][] tileHeights = scene.getTileHeights();
        for (int z = 0; z < 4; ++z) {
            for (int y = 0; y < 184; ++y) {
                for (int x = 0; x < 184; ++x) {
                    int h = tileHeights[z][x][y];
                    assert ((h & 7) == 0);
                    tileBuffer.put((short)(h >>= 3));
                }
            }
        }
        tileBuffer.flip();
        this.tileHeightTex = GL43C.glGenTextures();
        GL43C.glBindTexture((int)32879, (int)this.tileHeightTex);
        GL43C.glTexParameteri((int)32879, (int)10241, (int)9728);
        GL43C.glTexParameteri((int)32879, (int)10240, (int)9728);
        GL43C.glTexParameteri((int)32879, (int)10242, (int)33071);
        GL43C.glTexParameteri((int)32879, (int)10243, (int)33071);
        GL43C.glTexImage3D((int)32879, (int)0, (int)33331, (int)184, (int)184, (int)4, (int)0, (int)36244, (int)5122, (ShortBuffer)tileBuffer);
        GL43C.glBindTexture((int)32879, (int)0);
        GL43C.glActiveTexture((int)33986);
        GL43C.glBindTexture((int)32879, (int)this.tileHeightTex);
        GL43C.glActiveTexture((int)33984);
    }

    public void swapScene(Scene scene) {
        if (this.computeMode == ComputeMode.NONE) {
            return;
        }
        if (this.computeMode == ComputeMode.OPENCL) {
            this.openCLManager.uploadTileHeights(scene);
        } else {
            assert (this.computeMode == ComputeMode.OPENGL);
            this.uploadTileHeights(scene);
        }
        this.sceneId = this.nextSceneId;
        this.updateBuffer(this.sceneVertexBuffer, 34962, this.nextSceneVertexBuffer.getBuffer(), 35046, 4L);
        this.updateBuffer(this.sceneUvBuffer, 34962, this.nextSceneTexBuffer.getBuffer(), 35046, 4L);
        this.nextSceneVertexBuffer = null;
        this.nextSceneTexBuffer = null;
        this.nextSceneId = -1;
        this.checkGLErrors();
    }

    public boolean tileInFrustum(Scene scene, int pitchSin, int pitchCos, int yawSin, int yawCos, int cameraX, int cameraY, int cameraZ, int plane, int msx, int msy) {
        int[][][] tileHeights = scene.getTileHeights();
        int x = (msx - 40 << 7) + 64 - cameraX;
        int z = (msy - 40 << 7) + 64 - cameraZ;
        int y = Math.max(Math.max(tileHeights[plane][msx][msy], tileHeights[plane][msx][msy + 1]), Math.max(tileHeights[plane][msx + 1][msy], tileHeights[plane][msx + 1][msy + 1])) + 350 - cameraY;
        int radius = 96;
        int zoom = this.client.get3dZoom();
        int Rasterizer3D_clipMidX2 = this.client.getRasterizer3D_clipMidX2();
        int Rasterizer3D_clipNegativeMidX = this.client.getRasterizer3D_clipNegativeMidX();
        int Rasterizer3D_clipNegativeMidY = this.client.getRasterizer3D_clipNegativeMidY();
        int var11 = yawCos * z - yawSin * x >> 16;
        int var12 = pitchSin * y + pitchCos * var11 >> 16;
        int var13 = pitchCos * radius >> 16;
        int depth = var12 + var13;
        if (depth > 50) {
            int ybottom;
            int ry;
            int var20;
            int rx = z * yawSin + yawCos * x >> 16;
            int var16 = (rx - radius) * zoom;
            int var17 = (rx + radius) * zoom;
            if (var16 < Rasterizer3D_clipMidX2 * depth && var17 > Rasterizer3D_clipNegativeMidX * depth && (var20 = ((ry = pitchCos * y - var11 * pitchSin >> 16) + (ybottom = pitchSin * radius >> 16)) * zoom) > Rasterizer3D_clipNegativeMidY * depth) {
                return true;
            }
        }
        return false;
    }

    private boolean isVisible(Model model, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z) {
        int yheight;
        int ybottom;
        int ry;
        int var20;
        int var17;
        int rx;
        int var16;
        int xzMag = model.getXYZMag();
        int bottomY = model.getBottomY();
        int zoom = this.client.get3dZoom();
        int modelHeight = model.getModelHeight();
        int Rasterizer3D_clipMidX2 = this.client.getRasterizer3D_clipMidX2();
        int Rasterizer3D_clipNegativeMidX = this.client.getRasterizer3D_clipNegativeMidX();
        int Rasterizer3D_clipNegativeMidY = this.client.getRasterizer3D_clipNegativeMidY();
        int Rasterizer3D_clipMidY2 = this.client.getRasterizer3D_clipMidY2();
        int var11 = yawCos * z - yawSin * x >> 16;
        int var12 = pitchSin * y + pitchCos * var11 >> 16;
        int var13 = pitchCos * xzMag >> 16;
        int depth = var12 + var13;
        if (depth > 50 && (var16 = ((rx = z * yawSin + yawCos * x >> 16) - xzMag) * zoom) / depth < Rasterizer3D_clipMidX2 && (var17 = (rx + xzMag) * zoom) / depth > Rasterizer3D_clipNegativeMidX && (var20 = ((ry = pitchCos * y - var11 * pitchSin >> 16) + (ybottom = (pitchCos * bottomY >> 16) + (yheight = pitchSin * xzMag >> 16))) * zoom) / depth > Rasterizer3D_clipNegativeMidY) {
            int ytop = (pitchCos * modelHeight >> 16) + yheight;
            int var22 = (ry - ytop) * zoom;
            return var22 / depth < Rasterizer3D_clipMidY2;
        }
        return false;
    }

    public void draw(Projection projection, Scene scene, Renderable renderable, int orientation, int x, int y, int z, long hash) {
        Model offsetModel;
        Model model;
        if (renderable instanceof Model) {
            model = (Model)renderable;
            offsetModel = model.getUnskewedModel();
            if (offsetModel == null) {
                offsetModel = model;
            }
        } else {
            model = renderable.getModel();
            if (model == null) {
                return;
            }
            offsetModel = model;
        }
        if (this.computeMode == ComputeMode.NONE) {
            IntProjection p;
            if (model != renderable) {
                renderable.setModelHeight(model.getModelHeight());
            }
            model.calculateBoundsCylinder();
            if (projection instanceof IntProjection && !this.isVisible(model, (p = (IntProjection)projection).getPitchSin(), p.getPitchCos(), p.getYawSin(), p.getYawCos(), x - p.getCameraX(), y - p.getCameraY(), z - p.getCameraZ())) {
                return;
            }
            this.client.checkClickbox(projection, model, orientation, x, y, z, hash);
            this.targetBufferOffset += this.sceneUploader.pushSortedModel(projection, model, orientation, x, y, z, this.vertexBuffer, this.uvBuffer);
        } else if (offsetModel.getSceneId() == this.sceneId) {
            IntProjection p;
            assert (model == renderable);
            model.calculateBoundsCylinder();
            if (projection instanceof IntProjection && !this.isVisible(model, (p = (IntProjection)projection).getPitchSin(), p.getPitchCos(), p.getYawSin(), p.getYawCos(), x - p.getCameraX(), y - p.getCameraY(), z - p.getCameraZ())) {
                return;
            }
            this.client.checkClickbox(projection, model, orientation, x, y, z, hash);
            int tc = Math.min(6144, offsetModel.getFaceCount());
            int uvOffset = offsetModel.getUvBufferOffset();
            int plane = (int)(hash >> 14 & 3L);
            boolean hillskew = offsetModel != model;
            GpuIntBuffer b = this.bufferForTriangles(tc);
            b.ensureCapacity(8);
            IntBuffer buffer = b.getBuffer();
            buffer.put(offsetModel.getBufferOffset());
            buffer.put(uvOffset);
            buffer.put(tc);
            buffer.put(this.targetBufferOffset);
            buffer.put(Integer.MIN_VALUE | (hillskew ? 0x4000000 : 0) | plane << 24 | orientation);
            buffer.put(x).put(y).put(z);
            this.targetBufferOffset += tc * 3;
        } else {
            IntProjection p;
            if (model != renderable) {
                renderable.setModelHeight(model.getModelHeight());
            }
            model.calculateBoundsCylinder();
            if (projection instanceof IntProjection && !this.isVisible(model, (p = (IntProjection)projection).getPitchSin(), p.getPitchCos(), p.getYawSin(), p.getYawCos(), x - p.getCameraX(), y - p.getCameraY(), z - p.getCameraZ())) {
                return;
            }
            this.client.checkClickbox(projection, model, orientation, x, y, z, hash);
            boolean hasUv = model.getFaceTextures() != null;
            int len = this.sceneUploader.pushModel(model, this.vertexBuffer, this.uvBuffer);
            GpuIntBuffer b = this.bufferForTriangles(len / 3);
            b.ensureCapacity(8);
            IntBuffer buffer = b.getBuffer();
            buffer.put(this.tempOffset);
            buffer.put(hasUv ? this.tempUvOffset : -1);
            buffer.put(len / 3);
            buffer.put(this.targetBufferOffset);
            buffer.put(orientation);
            buffer.put(x).put(y).put(z);
            this.tempOffset += len;
            if (hasUv) {
                this.tempUvOffset += len;
            }
            this.targetBufferOffset += len;
        }
    }

    private GpuIntBuffer bufferForTriangles(int triangles) {
        if (triangles <= 512) {
            ++this.smallModels;
            return this.modelBufferSmall;
        }
        ++this.largeModels;
        return this.modelBuffer;
    }

    private int getScaledValue(double scale, int value) {
        return (int)((double)value * scale);
    }

    private void glDpiAwareViewport(int x, int y, int width, int height) {
        GraphicsConfiguration graphicsConfiguration = this.clientUI.getGraphicsConfiguration();
        AffineTransform t = graphicsConfiguration.getDefaultTransform();
        GL43C.glViewport((int)this.getScaledValue(t.getScaleX(), x), (int)this.getScaledValue(t.getScaleY(), y), (int)this.getScaledValue(t.getScaleX(), width), (int)this.getScaledValue(t.getScaleY(), height));
    }

    private int getDrawDistance() {
        return Ints.constrainToRange((int)this.config.drawDistance(), (int)0, (int)184);
    }

    private void updateBuffer(@Nonnull GLBuffer glBuffer, int target, @Nonnull IntBuffer data, int usage, long clFlags) {
        int size = data.remaining() << 2;
        this.updateBuffer(glBuffer, target, size, usage, clFlags);
        GL43C.glBufferSubData((int)target, (long)0L, (IntBuffer)data);
    }

    private void updateBuffer(@Nonnull GLBuffer glBuffer, int target, @Nonnull FloatBuffer data, int usage, long clFlags) {
        int size = data.remaining() << 2;
        this.updateBuffer(glBuffer, target, size, usage, clFlags);
        GL43C.glBufferSubData((int)target, (long)0L, (FloatBuffer)data);
    }

    private void updateBuffer(@Nonnull GLBuffer glBuffer, int target, int size, int usage, long clFlags) {
        GL43C.glBindBuffer((int)target, (int)glBuffer.glBufferId);
        if (this.glCapabilities.glInvalidateBufferData != 0L) {
            GL43C.glInvalidateBufferData((int)glBuffer.glBufferId);
        }
        if (size > glBuffer.size) {
            int newSize = Math.max(1024, GpuPlugin.nextPowerOfTwo(size));
            log.trace("Buffer resize: {} {} -> {}", new Object[]{glBuffer.name, glBuffer.size, newSize});
            glBuffer.size = newSize;
            GL43C.glBufferData((int)target, (long)newSize, (int)usage);
            this.recreateCLBuffer(glBuffer, clFlags);
        }
    }

    private static int nextPowerOfTwo(int v) {
        --v;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return ++v;
    }

    private void recreateCLBuffer(GLBuffer glBuffer, long clFlags) {
        if (this.computeMode == ComputeMode.OPENCL) {
            if (glBuffer.clBuffer != -1L) {
                CL10.clReleaseMemObject((long)glBuffer.clBuffer);
            }
            glBuffer.clBuffer = glBuffer.size == 0 ? -1L : CL10GL.clCreateFromGLBuffer((long)this.openCLManager.context, (long)clFlags, (int)glBuffer.glBufferId, (int[])null);
        }
    }

    private void checkGLErrors() {
        if (!log.isDebugEnabled()) {
            return;
        }
        int err;
        while ((err = GL43C.glGetError()) != 0) {
            Object errStr;
            switch (err) {
                case 1280: {
                    errStr = "INVALID_ENUM";
                    break;
                }
                case 1281: {
                    errStr = "INVALID_VALUE";
                    break;
                }
                case 1282: {
                    errStr = "INVALID_OPERATION";
                    break;
                }
                case 1286: {
                    errStr = "INVALID_FRAMEBUFFER_OPERATION";
                    break;
                }
                default: {
                    errStr = "" + err;
                }
            }
            log.debug("glGetError:", (Throwable)new Exception((String)errStr));
        }
        return;
    }

    static enum ComputeMode {
        NONE,
        OPENGL,
        OPENCL;

    }
}

