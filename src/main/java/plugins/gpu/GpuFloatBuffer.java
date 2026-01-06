/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class GpuFloatBuffer {
    private FloatBuffer buffer = GpuFloatBuffer.allocateDirect(65536);

    GpuFloatBuffer() {
    }

    void put(float s, float t, float p, float q) {
        this.buffer.put(s).put(t).put(p).put(q);
    }

    void flip() {
        this.buffer.flip();
    }

    void clear() {
        this.buffer.clear();
    }

    void ensureCapacity(int size) {
        int position;
        int capacity = this.buffer.capacity();
        if (capacity - (position = this.buffer.position()) < size) {
            while ((capacity *= 2) - position < size) {
            }
            FloatBuffer newB = GpuFloatBuffer.allocateDirect(capacity);
            this.buffer.flip();
            newB.put(this.buffer);
            this.buffer = newB;
        }
    }

    FloatBuffer getBuffer() {
        return this.buffer;
    }

    static FloatBuffer allocateDirect(int size) {
        return ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
}

