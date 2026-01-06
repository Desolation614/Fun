/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.hiscore;

public final class Skill {
    private final int rank;
    private final int level;
    private final long experience;

    public Skill(int rank, int level, long experience) {
        this.rank = rank;
        this.level = level;
        this.experience = experience;
    }

    public int getRank() {
        return this.rank;
    }

    public int getLevel() {
        return this.level;
    }

    public long getExperience() {
        return this.experience;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Skill)) {
            return false;
        }
        Skill other = (Skill)o;
        if (this.getRank() != other.getRank()) {
            return false;
        }
        if (this.getLevel() != other.getLevel()) {
            return false;
        }
        return this.getExperience() == other.getExperience();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getRank();
        result = result * 59 + this.getLevel();
        long $experience = this.getExperience();
        result = result * 59 + (int)($experience >>> 32 ^ $experience);
        return result;
    }

    public String toString() {
        return "Skill(rank=" + this.getRank() + ", level=" + this.getLevel() + ", experience=" + this.getExperience() + ")";
    }
}

