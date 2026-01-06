/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
 *  net.runelite.api.Texture
 *  net.runelite.api.TextureProvider
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL43C
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.gpu;

import java.nio.ByteBuffer;
import javax.inject.Singleton;
import net.runelite.api.Texture;
import net.runelite.api.TextureProvider;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class TextureManager {
    private static final Logger log = LoggerFactory.getLogger(TextureManager.class);
    static final int TEXTURE_COUNT = 256;
    private static final int TEXTURE_SIZE = 128;

    TextureManager() {
    }

    int initTextureArray(TextureProvider textureProvider) {
        if (!this.allTexturesLoaded(textureProvider)) {
            return -1;
        }
        Texture[] textures = textureProvider.getTextures();
        int textureArrayId = GL43C.glGenTextures();
        GL43C.glBindTexture((int)35866, (int)textureArrayId);
        if (GL.getCapabilities().glTexStorage3D != 0L) {
            GL43C.glTexStorage3D((int)35866, (int)8, (int)32856, (int)128, (int)128, (int)textures.length);
        } else {
            int size = 128;
            for (int i = 0; i < 8; ++i) {
                GL43C.glTexImage3D((int)35866, (int)i, (int)32856, (int)size, (int)size, (int)textures.length, (int)0, (int)6408, (int)5121, (long)0L);
                size /= 2;
            }
        }
        GL43C.glTexParameteri((int)35866, (int)10241, (int)9728);
        GL43C.glTexParameteri((int)35866, (int)10240, (int)9728);
        GL43C.glTexParameteri((int)35866, (int)10242, (int)33071);
        double save = textureProvider.getBrightness();
        textureProvider.setBrightness(1.0);
        this.updateTextures(textureProvider, textureArrayId);
        textureProvider.setBrightness(save);
        GL43C.glActiveTexture((int)33985);
        GL43C.glBindTexture((int)35866, (int)textureArrayId);
        GL43C.glGenerateMipmap((int)35866);
        GL43C.glActiveTexture((int)33984);
        return textureArrayId;
    }

    void setAnisotropicFilteringLevel(int textureArrayId, int level) {
        GL43C.glBindTexture((int)35866, (int)textureArrayId);
        if (level == 0) {
            GL43C.glTexParameteri((int)35866, (int)10241, (int)9728);
        } else {
            GL43C.glTexParameteri((int)35866, (int)10241, (int)9986);
        }
        if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            float maxSamples = GL43C.glGetFloat((int)34047);
            float anisoLevel = Math.max(1.0f, Math.min(maxSamples, (float)level));
            GL43C.glTexParameterf((int)35866, (int)34046, (float)anisoLevel);
        }
    }

    void freeTextureArray(int textureArrayId) {
        GL43C.glDeleteTextures((int)textureArrayId);
    }

    private boolean allTexturesLoaded(TextureProvider textureProvider) {
        Texture[] textures = textureProvider.getTextures();
        if (textures == null || textures.length == 0) {
            return false;
        }
        for (int textureId = 0; textureId < textures.length; ++textureId) {
            int[] pixels;
            Texture texture = textures[textureId];
            if (texture == null || (pixels = textureProvider.load(textureId)) != null) continue;
            return false;
        }
        return true;
    }

    private void updateTextures(TextureProvider textureProvider, int textureArrayId) {
        Texture[] textures = textureProvider.getTextures();
        GL43C.glBindTexture((int)35866, (int)textureArrayId);
        int cnt = 0;
        for (int textureId = 0; textureId < textures.length; ++textureId) {
            Texture texture = textures[textureId];
            if (texture == null) continue;
            int[] srcPixels = textureProvider.load(textureId);
            if (srcPixels == null) {
                log.warn("No pixels for texture {}!", (Object)textureId);
                continue;
            }
            ++cnt;
            if (srcPixels.length != 16384) {
                log.warn("Texture size for {} is {}!", (Object)textureId, (Object)srcPixels.length);
                continue;
            }
            byte[] pixels = TextureManager.convertPixels(srcPixels, 128, 128, 128, 128);
            ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(pixels.length);
            pixelBuffer.put(pixels);
            pixelBuffer.flip();
            GL43C.glTexSubImage3D((int)35866, (int)0, (int)0, (int)0, (int)textureId, (int)128, (int)128, (int)1, (int)6408, (int)5121, (ByteBuffer)pixelBuffer);
        }
        log.debug("Uploaded textures {}", (Object)cnt);
    }

    private static byte[] convertPixels(int[] srcPixels, int width, int height, int textureWidth, int textureHeight) {
        byte[] pixels = new byte[textureWidth * textureHeight * 4];
        int pixelIdx = 0;
        int srcPixelIdx = 0;
        int offset = (textureWidth - width) * 4;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int rgb;
                if ((rgb = srcPixels[srcPixelIdx++]) != 0) {
                    pixels[pixelIdx++] = (byte)(rgb >> 16);
                    pixels[pixelIdx++] = (byte)(rgb >> 8);
                    pixels[pixelIdx++] = (byte)rgb;
                    pixels[pixelIdx++] = -1;
                    continue;
                }
                pixelIdx += 4;
            }
            pixelIdx += offset;
        }
        return pixels;
    }

    float[] computeTextureAnimations(TextureProvider textureProvider) {
        Texture[] textures = textureProvider.getTextures();
        if (textures.length > 256) {
            log.warn("texture limit exceeded: {} > {}", (Object)textures.length, (Object)256);
        }
        float[] anims = new float[512];
        for (int i = 0; i < Math.min(256, textures.length); ++i) {
            Texture texture = textures[i];
            if (texture == null) continue;
            float u = 0.0f;
            float v = 0.0f;
            switch (texture.getAnimationDirection()) {
                case 1: {
                    v = -1.0f;
                    break;
                }
                case 3: {
                    v = 1.0f;
                    break;
                }
                case 2: {
                    u = -1.0f;
                    break;
                }
                case 4: {
                    u = 1.0f;
                }
            }
            int speed = texture.getAnimationSpeed();
            anims[i * 2] = u *= (float)speed;
            anims[i * 2 + 1] = v *= (float)speed;
        }
        return anims;
    }
}

