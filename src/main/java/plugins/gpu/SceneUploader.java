/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.GameObject
 *  net.runelite.api.GroundObject
 *  net.runelite.api.Model
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.Projection
 *  net.runelite.api.Renderable
 *  net.runelite.api.Scene
 *  net.runelite.api.SceneTileModel
 *  net.runelite.api.SceneTilePaint
 *  net.runelite.api.Tile
 *  net.runelite.api.WallObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.gpu;

import com.google.common.base.Stopwatch;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Model;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Projection;
import net.runelite.api.Renderable;
import net.runelite.api.Scene;
import net.runelite.api.SceneTileModel;
import net.runelite.api.SceneTilePaint;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.client.plugins.gpu.GpuFloatBuffer;
import net.runelite.client.plugins.gpu.GpuIntBuffer;
import net.runelite.client.plugins.gpu.GpuPluginConfig;
import net.runelite.client.plugins.gpu.regions.Regions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class SceneUploader {
    private static final Logger log = LoggerFactory.getLogger(SceneUploader.class);
    private final Client client;
    private final GpuPluginConfig gpuConfig;
    private final Regions regions;
    int sceneId = (int)System.nanoTime();
    private int offset;
    private int uvoffset;
    private int uniqueModels;
    private static int[] distances;
    private static char[] distanceFaceCount;
    private static char[][] distanceToFaces;
    private static float[] modelCanvasX;
    private static float[] modelCanvasY;
    private static float[] modelLocalX;
    private static float[] modelLocalY;
    private static float[] modelLocalZ;
    private static int[] numOfPriority;
    private static int[] eq10;
    private static int[] eq11;
    private static int[] lt10;
    private static int[][] orderedFaces;

    @Inject
    SceneUploader(Client client, GpuPluginConfig config) {
        this.client = client;
        this.gpuConfig = config;
        try (InputStream in = SceneUploader.class.getResourceAsStream("regions/regions.txt");){
            this.regions = new Regions(in, "regions.txt");
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    void upload(Scene scene, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer) {
        ++this.sceneId;
        this.offset = 0;
        this.uvoffset = 0;
        this.uniqueModels = 0;
        vertexBuffer.clear();
        uvBuffer.clear();
        Stopwatch stopwatch = Stopwatch.createStarted();
        this.prepare(scene);
        stopwatch.stop();
        log.debug("Scene preparation time: {}", (Object)stopwatch);
        stopwatch = Stopwatch.createStarted();
        for (int z = 0; z < 4; ++z) {
            for (int x = 0; x < 184; ++x) {
                for (int y = 0; y < 184; ++y) {
                    Tile tile = scene.getExtendedTiles()[z][x][y];
                    if (tile == null) continue;
                    this.upload(scene, tile, vertexBuffer, uvBuffer);
                }
            }
        }
        stopwatch.stop();
        log.debug("Scene upload time: {} unique models: {} length: {}KB", new Object[]{stopwatch, this.uniqueModels, this.offset * 16 / 1024});
    }

    private void upload(Scene scene, Tile tile, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer) {
        GameObject[] gameObjects;
        DecorativeObject decorativeObject;
        Renderable renderable;
        GroundObject groundObject;
        WallObject wallObject;
        SceneTileModel sceneTileModel;
        SceneTilePaint sceneTilePaint;
        Tile bridge = tile.getBridge();
        if (bridge != null) {
            this.upload(scene, bridge, vertexBuffer, uvBuffer);
        }
        if ((sceneTilePaint = tile.getSceneTilePaint()) != null) {
            sceneTilePaint.setBufferOffset(this.offset);
            if (sceneTilePaint.getTexture() != -1) {
                sceneTilePaint.setUvBufferOffset(this.uvoffset);
            } else {
                sceneTilePaint.setUvBufferOffset(-1);
            }
            Point tilePoint = tile.getSceneLocation();
            int len = this.upload(scene, sceneTilePaint, tile.getRenderLevel(), tilePoint.getX(), tilePoint.getY(), vertexBuffer, uvBuffer, 0, 0, false);
            sceneTilePaint.setBufferLen(len);
            this.offset += len;
            if (sceneTilePaint.getTexture() != -1) {
                this.uvoffset += len;
            }
        }
        if ((sceneTileModel = tile.getSceneTileModel()) != null) {
            sceneTileModel.setBufferOffset(this.offset);
            if (sceneTileModel.getTriangleTextureId() != null) {
                sceneTileModel.setUvBufferOffset(this.uvoffset);
            } else {
                sceneTileModel.setUvBufferOffset(-1);
            }
            Point tilePoint = tile.getSceneLocation();
            int len = this.upload(sceneTileModel, tilePoint.getX() << 7, tilePoint.getY() << 7, vertexBuffer, uvBuffer, false);
            sceneTileModel.setBufferLen(len);
            this.offset += len;
            if (sceneTileModel.getTriangleTextureId() != null) {
                this.uvoffset += len;
            }
        }
        if ((wallObject = tile.getWallObject()) != null) {
            Renderable renderable2;
            Renderable renderable1 = wallObject.getRenderable1();
            if (renderable1 instanceof Model) {
                this.uploadSceneModel((Model)renderable1, vertexBuffer, uvBuffer);
            }
            if ((renderable2 = wallObject.getRenderable2()) instanceof Model) {
                this.uploadSceneModel((Model)renderable2, vertexBuffer, uvBuffer);
            }
        }
        if ((groundObject = tile.getGroundObject()) != null && (renderable = groundObject.getRenderable()) instanceof Model) {
            this.uploadSceneModel((Model)renderable, vertexBuffer, uvBuffer);
        }
        if ((decorativeObject = tile.getDecorativeObject()) != null) {
            Renderable renderable2;
            Renderable renderable3 = decorativeObject.getRenderable();
            if (renderable3 instanceof Model) {
                this.uploadSceneModel((Model)renderable3, vertexBuffer, uvBuffer);
            }
            if ((renderable2 = decorativeObject.getRenderable2()) instanceof Model) {
                this.uploadSceneModel((Model)renderable2, vertexBuffer, uvBuffer);
            }
        }
        for (GameObject gameObject : gameObjects = tile.getGameObjects()) {
            Renderable renderable4;
            if (gameObject == null || !((renderable4 = gameObject.getRenderable()) instanceof Model)) continue;
            this.uploadSceneModel((Model)gameObject.getRenderable(), vertexBuffer, uvBuffer);
        }
    }

    int upload(Scene scene, SceneTilePaint tile, int tileZ, int tileX, int tileY, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer, int lx, int lz, boolean stream) {
        int[][][] tileHeights = scene.getTileHeights();
        int swHeight = tileHeights[tileZ][tileX += 40][tileY += 40];
        int seHeight = tileHeights[tileZ][tileX + 1][tileY];
        int neHeight = tileHeights[tileZ][tileX + 1][tileY + 1];
        int nwHeight = tileHeights[tileZ][tileX][tileY + 1];
        int swColor = tile.getSwColor();
        int seColor = tile.getSeColor();
        int neColor = tile.getNeColor();
        int nwColor = tile.getNwColor();
        if (neColor == 12345678) {
            return 0;
        }
        vertexBuffer.ensureCapacity(24);
        uvBuffer.ensureCapacity(24);
        int lx0 = lx;
        int ly0 = swHeight;
        int lz0 = lz;
        int hsl0 = swColor;
        int lx1 = lx + 128;
        int ly1 = seHeight;
        int lz1 = lz;
        int hsl1 = seColor;
        int lx2 = lx + 128;
        int ly2 = neHeight;
        int lz2 = lz + 128;
        int hsl2 = neColor;
        int lx3 = lx;
        int ly3 = nwHeight;
        int lz3 = lz + 128;
        int hsl3 = nwColor;
        vertexBuffer.put(lx2, ly2, lz2, hsl2);
        vertexBuffer.put(lx3, ly3, lz3, hsl3);
        vertexBuffer.put(lx1, ly1, lz1, hsl1);
        vertexBuffer.put(lx0, ly0, lz0, hsl0);
        vertexBuffer.put(lx1, ly1, lz1, hsl1);
        vertexBuffer.put(lx3, ly3, lz3, hsl3);
        if (stream || tile.getTexture() != -1) {
            int tex = tile.getTexture() + 1;
            if (tile.isFlat()) {
                uvBuffer.put(tex, lx0, ly0, lz0);
                uvBuffer.put(tex, lx1, ly1, lz1);
                uvBuffer.put(tex, lx3, ly3, lz3);
            } else {
                uvBuffer.put(tex, lx2, ly2, lz2);
                uvBuffer.put(tex, lx3, ly3, lz3);
                uvBuffer.put(tex, lx1, ly1, lz1);
            }
            uvBuffer.put(tex, lx0, ly0, lz0);
            uvBuffer.put(tex, lx1, ly1, lz1);
            uvBuffer.put(tex, lx3, ly3, lz3);
        }
        return 6;
    }

    int upload(SceneTileModel sceneTileModel, int lx, int lz, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer, boolean stream) {
        int[] faceX = sceneTileModel.getFaceX();
        int[] faceY = sceneTileModel.getFaceY();
        int[] faceZ = sceneTileModel.getFaceZ();
        int[] vertexX = sceneTileModel.getVertexX();
        int[] vertexY = sceneTileModel.getVertexY();
        int[] vertexZ = sceneTileModel.getVertexZ();
        int[] triangleColorA = sceneTileModel.getTriangleColorA();
        int[] triangleColorB = sceneTileModel.getTriangleColorB();
        int[] triangleColorC = sceneTileModel.getTriangleColorC();
        int[] triangleTextures = sceneTileModel.getTriangleTextureId();
        int faceCount = faceX.length;
        vertexBuffer.ensureCapacity(faceCount * 12);
        uvBuffer.ensureCapacity(faceCount * 12);
        int cnt = 0;
        for (int i = 0; i < faceCount; ++i) {
            int vertex0 = faceX[i];
            int vertex1 = faceY[i];
            int vertex2 = faceZ[i];
            int hsl0 = triangleColorA[i];
            int hsl1 = triangleColorB[i];
            int hsl2 = triangleColorC[i];
            if (hsl0 == 12345678) continue;
            cnt += 3;
            int lx0 = vertexX[vertex0] - lx;
            int ly0 = vertexY[vertex0];
            int lz0 = vertexZ[vertex0] - lz;
            int lx1 = vertexX[vertex1] - lx;
            int ly1 = vertexY[vertex1];
            int lz1 = vertexZ[vertex1] - lz;
            int lx2 = vertexX[vertex2] - lx;
            int ly2 = vertexY[vertex2];
            int lz2 = vertexZ[vertex2] - lz;
            vertexBuffer.put(lx0, ly0, lz0, hsl0);
            vertexBuffer.put(lx1, ly1, lz1, hsl1);
            vertexBuffer.put(lx2, ly2, lz2, hsl2);
            if (!stream && triangleTextures == null) continue;
            if (triangleTextures != null && triangleTextures[i] != -1) {
                int tex = triangleTextures[i] + 1;
                if (sceneTileModel.isFlat()) {
                    uvBuffer.put(tex, vertexX[0] - lx, vertexY[0], vertexZ[0] - lz);
                    uvBuffer.put(tex, vertexX[1] - lx, vertexY[1], vertexZ[1] - lz);
                    uvBuffer.put(tex, vertexX[3] - lx, vertexY[3], vertexZ[3] - lz);
                    continue;
                }
                uvBuffer.put(tex, vertexX[vertex0] - lx, vertexY[vertex0], vertexZ[vertex0] - lz);
                uvBuffer.put(tex, vertexX[vertex1] - lx, vertexY[vertex1], vertexZ[vertex1] - lz);
                uvBuffer.put(tex, vertexX[vertex2] - lx, vertexY[vertex2], vertexZ[vertex2] - lz);
                continue;
            }
            uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
            uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
            uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
        }
        return cnt;
    }

    private void uploadSceneModel(Model model, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer) {
        if (model.getUnskewedModel() != null) {
            model = model.getUnskewedModel();
        }
        if (model.getSceneId() == this.sceneId) {
            return;
        }
        model.setBufferOffset(this.offset);
        if (model.getFaceTextures() != null) {
            model.setUvBufferOffset(this.uvoffset);
        } else {
            model.setUvBufferOffset(-1);
        }
        model.setSceneId(this.sceneId);
        ++this.uniqueModels;
        int len = this.pushModel(model, vertexBuffer, uvBuffer);
        this.offset += len;
        if (model.getFaceTextures() != null) {
            this.uvoffset += len;
        }
    }

    public int pushModel(Model model, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer) {
        int triangleCount = Math.min(model.getFaceCount(), 6144);
        vertexBuffer.ensureCapacity(triangleCount * 12);
        uvBuffer.ensureCapacity(triangleCount * 12);
        float[] vertexX = model.getVerticesX();
        float[] vertexY = model.getVerticesY();
        float[] vertexZ = model.getVerticesZ();
        int[] indices1 = model.getFaceIndices1();
        int[] indices2 = model.getFaceIndices2();
        int[] indices3 = model.getFaceIndices3();
        int[] color1s = model.getFaceColors1();
        int[] color2s = model.getFaceColors2();
        int[] color3s = model.getFaceColors3();
        short[] faceTextures = model.getFaceTextures();
        byte[] textureFaces = model.getTextureFaces();
        int[] texIndices1 = model.getTexIndices1();
        int[] texIndices2 = model.getTexIndices2();
        int[] texIndices3 = model.getTexIndices3();
        byte[] transparencies = model.getFaceTransparencies();
        byte[] facePriorities = model.getFaceRenderPriorities();
        byte overrideAmount = model.getOverrideAmount();
        byte overrideHue = model.getOverrideHue();
        byte overrideSat = model.getOverrideSaturation();
        byte overrideLum = model.getOverrideLuminance();
        int len = 0;
        for (int face = 0; face < triangleCount; ++face) {
            int color1 = color1s[face];
            int color2 = color2s[face];
            int color3 = color3s[face];
            if (color3 == -1) {
                color2 = color3 = color1;
            } else if (color3 == -2) {
                vertexBuffer.put(0.0f, 0.0f, 0.0f, 0);
                vertexBuffer.put(0.0f, 0.0f, 0.0f, 0);
                vertexBuffer.put(0.0f, 0.0f, 0.0f, 0);
                if (faceTextures != null) {
                    uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
                    uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
                    uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
                }
                len += 3;
                continue;
            }
            if ((faceTextures == null || faceTextures[face] == -1) && overrideAmount > 0) {
                color1 = SceneUploader.interpolateHSL(color1, overrideHue, overrideSat, overrideLum, overrideAmount);
                color2 = SceneUploader.interpolateHSL(color2, overrideHue, overrideSat, overrideLum, overrideAmount);
                color3 = SceneUploader.interpolateHSL(color3, overrideHue, overrideSat, overrideLum, overrideAmount);
            }
            int packAlphaPriority = SceneUploader.packAlphaPriority(faceTextures, transparencies, facePriorities, face);
            int triangleA = indices1[face];
            int triangleB = indices2[face];
            int triangleC = indices3[face];
            vertexBuffer.put(vertexX[triangleA], vertexY[triangleA], vertexZ[triangleA], packAlphaPriority | color1);
            vertexBuffer.put(vertexX[triangleB], vertexY[triangleB], vertexZ[triangleB], packAlphaPriority | color2);
            vertexBuffer.put(vertexX[triangleC], vertexY[triangleC], vertexZ[triangleC], packAlphaPriority | color3);
            if (faceTextures != null) {
                if (faceTextures[face] != -1) {
                    int texC;
                    int texB;
                    int texA;
                    if (textureFaces != null && textureFaces[face] != -1) {
                        int tface = textureFaces[face] & 0xFF;
                        texA = texIndices1[tface];
                        texB = texIndices2[tface];
                        texC = texIndices3[tface];
                    } else {
                        texA = triangleA;
                        texB = triangleB;
                        texC = triangleC;
                    }
                    int texture = faceTextures[face] + 1;
                    uvBuffer.put(texture, vertexX[texA], vertexY[texA], vertexZ[texA]);
                    uvBuffer.put(texture, vertexX[texB], vertexY[texB], vertexZ[texB]);
                    uvBuffer.put(texture, vertexX[texC], vertexY[texC], vertexZ[texC]);
                } else {
                    uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
                    uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
                    uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
                }
            }
            len += 3;
        }
        return len;
    }

    void initSortingBuffers() {
        int MAX_VERTEX_COUNT = 6500;
        int MAX_DIAMETER = 6000;
        int ZSORT_GROUP_SIZE = 1024;
        int MAX_FACES_PER_PRIORITY = 4000;
        distances = new int[MAX_VERTEX_COUNT];
        distanceFaceCount = new char[MAX_DIAMETER];
        distanceToFaces = new char[MAX_DIAMETER][ZSORT_GROUP_SIZE];
        modelCanvasX = new float[MAX_VERTEX_COUNT];
        modelCanvasY = new float[MAX_VERTEX_COUNT];
        modelLocalX = new float[MAX_VERTEX_COUNT];
        modelLocalY = new float[MAX_VERTEX_COUNT];
        modelLocalZ = new float[MAX_VERTEX_COUNT];
        numOfPriority = new int[12];
        eq10 = new int[MAX_FACES_PER_PRIORITY];
        eq11 = new int[MAX_FACES_PER_PRIORITY];
        lt10 = new int[12];
        orderedFaces = new int[12][MAX_FACES_PER_PRIORITY];
    }

    void releaseSortingBuffers() {
        distances = null;
        distanceFaceCount = null;
        distanceToFaces = null;
        modelCanvasX = null;
        modelCanvasY = null;
        modelLocalX = null;
        modelLocalY = null;
        modelLocalZ = null;
        numOfPriority = null;
        eq10 = null;
        eq11 = null;
        lt10 = null;
        orderedFaces = null;
    }

    int pushSortedModel(Projection proj, Model model, int orientation, int x, int y, int z, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer) {
        int vertexCount = model.getVerticesCount();
        float[] verticesX = model.getVerticesX();
        float[] verticesY = model.getVerticesY();
        float[] verticesZ = model.getVerticesZ();
        int faceCount = model.getFaceCount();
        int[] indices1 = model.getFaceIndices1();
        int[] indices2 = model.getFaceIndices2();
        int[] indices3 = model.getFaceIndices3();
        int[] faceColors3 = model.getFaceColors3();
        byte[] faceRenderPriorities = model.getFaceRenderPriorities();
        int centerX = this.client.getCenterX();
        int centerY = this.client.getCenterY();
        int zoom = this.client.get3dZoom();
        float orientSine = 0.0f;
        float orientCosine = 0.0f;
        if (orientation != 0) {
            orientSine = (float)Perspective.SINE[orientation] / 65536.0f;
            orientCosine = (float)Perspective.COSINE[orientation] / 65536.0f;
        }
        float[] p = proj.project((float)x, (float)y, (float)z);
        int zero = (int)p[2];
        for (int v = 0; v < vertexCount; ++v) {
            float vertexX = verticesX[v];
            float vertexY = verticesY[v];
            float vertexZ = verticesZ[v];
            if (orientation != 0) {
                float x0 = vertexX;
                vertexX = vertexZ * orientSine + x0 * orientCosine;
                vertexZ = vertexZ * orientCosine - x0 * orientSine;
            }
            SceneUploader.modelLocalX[v] = vertexX += (float)x;
            SceneUploader.modelLocalY[v] = vertexY += (float)y;
            SceneUploader.modelLocalZ[v] = vertexZ += (float)z;
            p = proj.project(vertexX, vertexY, vertexZ);
            SceneUploader.modelCanvasX[v] = (float)centerX + p[0] * (float)zoom / p[2];
            SceneUploader.modelCanvasY[v] = (float)centerY + p[1] * (float)zoom / p[2];
            SceneUploader.distances[v] = (int)p[2] - zero;
        }
        int diameter = model.getDiameter();
        int radius = model.getRadius();
        if (diameter >= 6000) {
            return 0;
        }
        Arrays.fill(distanceFaceCount, 0, diameter, '\u0000');
        for (int i = 0; i < faceCount; i = (int)((char)(i + 1))) {
            float aY;
            float cX;
            float bY;
            int v3;
            float cY;
            int v2;
            float bX;
            int v1;
            float aX;
            if (faceColors3[i] == -2 || !(((aX = modelCanvasX[v1 = indices1[i]]) - (bX = modelCanvasX[v2 = indices2[i]])) * ((cY = modelCanvasY[v3 = indices3[i]]) - (bY = modelCanvasY[v2])) - ((cX = modelCanvasX[v3]) - bX) * ((aY = modelCanvasY[v1]) - bY) > 0.0f)) continue;
            int distance = radius + (distances[v1] + distances[v2] + distances[v3]) / 3;
            assert (distance >= 0 && distance < diameter);
            int n = distance;
            char c = distanceFaceCount[n];
            distanceFaceCount[n] = (char)(c + '\u0001');
            SceneUploader.distanceToFaces[distance][c] = i;
        }
        vertexBuffer.ensureCapacity(12 * faceCount);
        uvBuffer.ensureCapacity(12 * faceCount);
        int len = 0;
        if (faceRenderPriorities == null) {
            for (int i = diameter - 1; i >= 0; --i) {
                int cnt = distanceFaceCount[i];
                if (cnt <= 0) continue;
                char[] faces = distanceToFaces[i];
                for (int faceIdx = 0; faceIdx < cnt; ++faceIdx) {
                    char face = faces[faceIdx];
                    len += this.pushFace(model, face, vertexBuffer, uvBuffer);
                }
            }
        } else {
            Arrays.fill(numOfPriority, 0);
            Arrays.fill(lt10, 0);
            for (int i = diameter - 1; i >= 0; --i) {
                int cnt = distanceFaceCount[i];
                if (cnt <= 0) continue;
                char[] faces = distanceToFaces[i];
                for (int faceIdx = 0; faceIdx < cnt; ++faceIdx) {
                    byte pri;
                    int face = faces[faceIdx];
                    byte by = pri = faceRenderPriorities[face];
                    numOfPriority[by] = numOfPriority[by] + 1;
                    SceneUploader.orderedFaces[pri][distIdx] = face;
                    if (pri < 10) {
                        byte by2 = pri;
                        lt10[by2] = lt10[by2] + i;
                        continue;
                    }
                    if (pri == 10) {
                        SceneUploader.eq10[distIdx] = i;
                        continue;
                    }
                    SceneUploader.eq11[distIdx] = i;
                }
            }
            int avg12 = 0;
            if (numOfPriority[1] > 0 || numOfPriority[2] > 0) {
                avg12 = (lt10[1] + lt10[2]) / (numOfPriority[1] + numOfPriority[2]);
            }
            int avg34 = 0;
            if (numOfPriority[3] > 0 || numOfPriority[4] > 0) {
                avg34 = (lt10[3] + lt10[4]) / (numOfPriority[3] + numOfPriority[4]);
            }
            int avg68 = 0;
            if (numOfPriority[6] > 0 || numOfPriority[8] > 0) {
                avg68 = (lt10[8] + lt10[6]) / (numOfPriority[8] + numOfPriority[6]);
            }
            int drawnFaces = 0;
            int numDynFaces = numOfPriority[10];
            int[] dynFaces = orderedFaces[10];
            int[] dynFaceDistances = eq10;
            if (drawnFaces == numDynFaces) {
                drawnFaces = 0;
                numDynFaces = numOfPriority[11];
                dynFaces = orderedFaces[11];
                dynFaceDistances = eq11;
            }
            int currFaceDistance = drawnFaces < numDynFaces ? dynFaceDistances[drawnFaces] : -1000;
            for (int pri = 0; pri < 10; ++pri) {
                int face;
                while (pri == 0 && currFaceDistance > avg12) {
                    face = dynFaces[drawnFaces++];
                    len += this.pushFace(model, face, vertexBuffer, uvBuffer);
                    if (drawnFaces == numDynFaces && dynFaces != orderedFaces[11]) {
                        drawnFaces = 0;
                        numDynFaces = numOfPriority[11];
                        dynFaces = orderedFaces[11];
                        dynFaceDistances = eq11;
                    }
                    if (drawnFaces < numDynFaces) {
                        currFaceDistance = dynFaceDistances[drawnFaces];
                        continue;
                    }
                    currFaceDistance = -1000;
                }
                while (pri == 3 && currFaceDistance > avg34) {
                    face = dynFaces[drawnFaces++];
                    len += this.pushFace(model, face, vertexBuffer, uvBuffer);
                    if (drawnFaces == numDynFaces && dynFaces != orderedFaces[11]) {
                        drawnFaces = 0;
                        numDynFaces = numOfPriority[11];
                        dynFaces = orderedFaces[11];
                        dynFaceDistances = eq11;
                    }
                    if (drawnFaces < numDynFaces) {
                        currFaceDistance = dynFaceDistances[drawnFaces];
                        continue;
                    }
                    currFaceDistance = -1000;
                }
                while (pri == 5 && currFaceDistance > avg68) {
                    face = dynFaces[drawnFaces++];
                    len += this.pushFace(model, face, vertexBuffer, uvBuffer);
                    if (drawnFaces == numDynFaces && dynFaces != orderedFaces[11]) {
                        drawnFaces = 0;
                        numDynFaces = numOfPriority[11];
                        dynFaces = orderedFaces[11];
                        dynFaceDistances = eq11;
                    }
                    if (drawnFaces < numDynFaces) {
                        currFaceDistance = dynFaceDistances[drawnFaces];
                        continue;
                    }
                    currFaceDistance = -1000;
                }
                int priNum = numOfPriority[pri];
                int[] priFaces = orderedFaces[pri];
                for (int faceIdx = 0; faceIdx < priNum; ++faceIdx) {
                    int face2 = priFaces[faceIdx];
                    len += this.pushFace(model, face2, vertexBuffer, uvBuffer);
                }
            }
            while (currFaceDistance != -1000) {
                int face = dynFaces[drawnFaces++];
                len += this.pushFace(model, face, vertexBuffer, uvBuffer);
                if (drawnFaces == numDynFaces && dynFaces != orderedFaces[11]) {
                    drawnFaces = 0;
                    dynFaces = orderedFaces[11];
                    numDynFaces = numOfPriority[11];
                    dynFaceDistances = eq11;
                }
                if (drawnFaces < numDynFaces) {
                    currFaceDistance = dynFaceDistances[drawnFaces];
                    continue;
                }
                currFaceDistance = -1000;
            }
        }
        return len;
    }

    private int pushFace(Model model, int face, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer) {
        int[] indices1 = model.getFaceIndices1();
        int[] indices2 = model.getFaceIndices2();
        int[] indices3 = model.getFaceIndices3();
        int[] faceColors1 = model.getFaceColors1();
        int[] faceColors2 = model.getFaceColors2();
        int[] faceColors3 = model.getFaceColors3();
        byte overrideAmount = model.getOverrideAmount();
        byte overrideHue = model.getOverrideHue();
        byte overrideSat = model.getOverrideSaturation();
        byte overrideLum = model.getOverrideLuminance();
        short[] faceTextures = model.getFaceTextures();
        byte[] textureFaces = model.getTextureFaces();
        int[] texIndices1 = model.getTexIndices1();
        int[] texIndices2 = model.getTexIndices2();
        int[] texIndices3 = model.getTexIndices3();
        byte[] faceRenderPriorities = model.getFaceRenderPriorities();
        byte[] transparencies = model.getFaceTransparencies();
        int packAlphaPriority = SceneUploader.packAlphaPriority(faceTextures, transparencies, faceRenderPriorities, face);
        int triangleA = indices1[face];
        int triangleB = indices2[face];
        int triangleC = indices3[face];
        int color1 = faceColors1[face];
        int color2 = faceColors2[face];
        int color3 = faceColors3[face];
        if (color3 == -1) {
            color2 = color3 = color1;
        }
        if ((faceTextures == null || faceTextures[face] == -1) && overrideAmount > 0) {
            color1 = SceneUploader.interpolateHSL(color1, overrideHue, overrideSat, overrideLum, overrideAmount);
            color2 = SceneUploader.interpolateHSL(color2, overrideHue, overrideSat, overrideLum, overrideAmount);
            color3 = SceneUploader.interpolateHSL(color3, overrideHue, overrideSat, overrideLum, overrideAmount);
        }
        vertexBuffer.put(modelLocalX[triangleA], modelLocalY[triangleA], modelLocalZ[triangleA], packAlphaPriority | color1);
        vertexBuffer.put(modelLocalX[triangleB], modelLocalY[triangleB], modelLocalZ[triangleB], packAlphaPriority | color2);
        vertexBuffer.put(modelLocalX[triangleC], modelLocalY[triangleC], modelLocalZ[triangleC], packAlphaPriority | color3);
        if (faceTextures != null && faceTextures[face] != -1) {
            int texC;
            int texB;
            int texA;
            if (textureFaces != null && textureFaces[face] != -1) {
                int tfaceIdx = textureFaces[face] & 0xFF;
                texA = texIndices1[tfaceIdx];
                texB = texIndices2[tfaceIdx];
                texC = texIndices3[tfaceIdx];
            } else {
                texA = triangleA;
                texB = triangleB;
                texC = triangleC;
            }
            int texture = faceTextures[face] + 1;
            uvBuffer.put(texture, modelLocalX[texA], modelLocalY[texA], modelLocalZ[texA]);
            uvBuffer.put(texture, modelLocalX[texB], modelLocalY[texB], modelLocalZ[texB]);
            uvBuffer.put(texture, modelLocalX[texC], modelLocalY[texC], modelLocalZ[texC]);
        } else {
            uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
            uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
            uvBuffer.put(0.0f, 0.0f, 0.0f, 0.0f);
        }
        return 3;
    }

    private static int packAlphaPriority(short[] faceTextures, byte[] faceTransparencies, byte[] facePriorities, int face) {
        int alpha = 0;
        if (faceTransparencies != null) {
            alpha = (faceTransparencies[face] & 0xFF) << 24;
        }
        int priority = 0;
        if (facePriorities != null) {
            priority = (facePriorities[face] & 0xFF) << 16;
        }
        return alpha | priority;
    }

    private static int interpolateHSL(int hsl, byte hue2, byte sat2, byte lum2, byte lerp) {
        int hue = hsl >> 10 & 0x3F;
        int sat = hsl >> 7 & 7;
        int lum = hsl & 0x7F;
        int var9 = lerp & 0xFF;
        if (hue2 != -1) {
            hue += var9 * (hue2 - hue) >> 7;
        }
        if (sat2 != -1) {
            sat += var9 * (sat2 - sat) >> 7;
        }
        if (lum2 != -1) {
            lum += var9 * (lum2 - lum) >> 7;
        }
        return (hue << 10 | sat << 7 | lum) & 0xFFFF;
    }

    private void prepare(Scene scene) {
        if (scene.isInstance() || !this.gpuConfig.hideUnrelatedMaps()) {
            return;
        }
        int baseX = scene.getBaseX() / 8;
        int baseY = scene.getBaseY() / 8;
        int centerX = baseX + 6;
        int centerY = baseY + 6;
        int centerId = this.regions.getRegionId(centerX, centerY);
        int r = 11;
        for (int offx = -r; offx <= r; ++offx) {
            for (int offy = -r; offy <= r; ++offy) {
                int cx = centerX + offx;
                int cy = centerY + offy;
                int id = this.regions.getRegionId(cx, cy);
                if (id == centerId) continue;
                SceneUploader.removeChunk(scene, cx, cy);
            }
        }
    }

    private static void removeChunk(Scene scene, int cx, int cy) {
        int wx = cx * 8;
        int wy = cy * 8;
        int sx = wx - scene.getBaseX();
        int sy = wy - scene.getBaseY();
        int cmsx = sx + 40;
        int cmsy = sy + 40;
        Tile[][][] tiles = scene.getExtendedTiles();
        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 8; ++y) {
                int msx = cmsx + x;
                int msy = cmsy + y;
                if (msx < 0 || msx >= 184 || msy < 0 || msy >= 184) continue;
                for (int z = 0; z < 4; ++z) {
                    Tile tile = tiles[z][msx][msy];
                    if (tile == null) continue;
                    scene.removeTile(tile);
                }
            }
        }
    }
}

