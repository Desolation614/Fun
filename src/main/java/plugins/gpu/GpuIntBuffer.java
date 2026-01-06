/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

class GpuIntBuffer {
    private IntBuffer buffer = GpuIntBuffer.allocateDirect(65536);

    GpuIntBuffer() {
    }

    void put(float x, float y, float z, int w) {
        this.buffer.put(Float.floatToIntBits(x)).put(Float.floatToIntBits(y)).put(Float.floatToIntBits(z)).put(w);
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
            IntBuffer newB = GpuIntBuffer.allocateDirect(capacity);
            this.buffer.flip();
            newB.put(this.buffer);
            this.buffer = newB;
        }
    }

    IntBuffer getBuffer() {
        return this.buffer;
    }

    static IntBuffer allocateDirect(int size) {
        return ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    }
}

