/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu;

class GLBuffer {
    String name;
    int glBufferId = -1;
    int size = -1;
    long clBuffer = -1L;

    GLBuffer(String name) {
        this.name = name;
    }
}

