/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.loottracker;

class NpcMetadata {
    int id;
    int r1;
    int r2;
    int r3;
    int r4;
    int r5;
    int r6;
    int r7;
    int r8;

    public int getId() {
        return this.id;
    }

    public int getR1() {
        return this.r1;
    }

    public int getR2() {
        return this.r2;
    }

    public int getR3() {
        return this.r3;
    }

    public int getR4() {
        return this.r4;
    }

    public int getR5() {
        return this.r5;
    }

    public int getR6() {
        return this.r6;
    }

    public int getR7() {
        return this.r7;
    }

    public int getR8() {
        return this.r8;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setR1(int r1) {
        this.r1 = r1;
    }

    public void setR2(int r2) {
        this.r2 = r2;
    }

    public void setR3(int r3) {
        this.r3 = r3;
    }

    public void setR4(int r4) {
        this.r4 = r4;
    }

    public void setR5(int r5) {
        this.r5 = r5;
    }

    public void setR6(int r6) {
        this.r6 = r6;
    }

    public void setR7(int r7) {
        this.r7 = r7;
    }

    public void setR8(int r8) {
        this.r8 = r8;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NpcMetadata)) {
            return false;
        }
        NpcMetadata other = (NpcMetadata)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getR1() != other.getR1()) {
            return false;
        }
        if (this.getR2() != other.getR2()) {
            return false;
        }
        if (this.getR3() != other.getR3()) {
            return false;
        }
        if (this.getR4() != other.getR4()) {
            return false;
        }
        if (this.getR5() != other.getR5()) {
            return false;
        }
        if (this.getR6() != other.getR6()) {
            return false;
        }
        if (this.getR7() != other.getR7()) {
            return false;
        }
        return this.getR8() == other.getR8();
    }

    protected boolean canEqual(Object other) {
        return other instanceof NpcMetadata;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        result = result * 59 + this.getR1();
        result = result * 59 + this.getR2();
        result = result * 59 + this.getR3();
        result = result * 59 + this.getR4();
        result = result * 59 + this.getR5();
        result = result * 59 + this.getR6();
        result = result * 59 + this.getR7();
        result = result * 59 + this.getR8();
        return result;
    }

    public String toString() {
        return "NpcMetadata(id=" + this.getId() + ", r1=" + this.getR1() + ", r2=" + this.getR2() + ", r3=" + this.getR3() + ", r4=" + this.getR4() + ", r5=" + this.getR5() + ", r6=" + this.getR6() + ", r7=" + this.getR7() + ", r8=" + this.getR8() + ")";
    }
}

