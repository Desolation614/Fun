/*
 * Decompiled with CFR 0.152.
 */
package com.google.archivepatcher.shared;

public class TypedRange<T>
implements Comparable<TypedRange<T>> {
    private final long offset;
    private final long length;
    private final T metadata;

    public TypedRange(long offset, long length, T metadata) {
        this.offset = offset;
        this.length = length;
        this.metadata = metadata;
    }

    public String toString() {
        return "offset " + this.offset + ", length " + this.length + ", metadata " + this.metadata;
    }

    public long getOffset() {
        return this.offset;
    }

    public long getLength() {
        return this.length;
    }

    public T getMetadata() {
        return this.metadata;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.length ^ this.length >>> 32);
        result = 31 * result + (this.metadata == null ? 0 : this.metadata.hashCode());
        result = 31 * result + (int)(this.offset ^ this.offset >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TypedRange other = (TypedRange)obj;
        if (this.length != other.length) {
            return false;
        }
        if (this.metadata == null ? other.metadata != null : !this.metadata.equals(other.metadata)) {
            return false;
        }
        return this.offset == other.offset;
    }

    @Override
    public int compareTo(TypedRange<T> other) {
        if (this.getOffset() < other.getOffset()) {
            return -1;
        }
        if (this.getOffset() > other.getOffset()) {
            return 1;
        }
        return 0;
    }
}

