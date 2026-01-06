/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.outline;

import java.util.Arrays;

class IntBlockBuffer {
    public static final int BLOCK_BITS = 10;
    public static final int BLOCK_SIZE = 1024;
    private int[] memory = new int[0];
    private int[] unusedBlockIndices = new int[0];
    private int unusedBlockIndicesLength;

    IntBlockBuffer() {
    }

    private void increaseBlockCount() {
        int currBlockCount = this.memory.length >> 10;
        int newBlockCount = Math.max(1, currBlockCount * 2);
        this.memory = Arrays.copyOf(this.memory, newBlockCount * 1024);
        this.unusedBlockIndices = Arrays.copyOf(this.unusedBlockIndices, newBlockCount);
        int i = currBlockCount;
        while (i < newBlockCount) {
            this.unusedBlockIndices[this.unusedBlockIndicesLength++] = i++;
        }
    }

    public int[] getMemory() {
        return this.memory;
    }

    public int useNewBlock() {
        if (this.unusedBlockIndicesLength == 0) {
            this.increaseBlockCount();
        }
        return this.unusedBlockIndices[--this.unusedBlockIndicesLength];
    }

    public void freeBlock(int index) {
        this.unusedBlockIndices[this.unusedBlockIndicesLength++] = index;
    }
}

