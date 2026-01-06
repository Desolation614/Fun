/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.hiscore;

class HiscoreResponse {
    Skill[] skills;
    Activity[] activities;

    HiscoreResponse() {
    }

    static class Activity {
        String name;
        int rank;
        long score;

        Activity() {
        }
    }

    static class Skill {
        String name;
        int rank;
        int level;
        long xp;

        Skill() {
        }
    }
}

