/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.xptracker;

final class XpSnapshotSingle {
    private final int startLevel;
    private final int endLevel;
    private final int startGoalXp;
    private final int endGoalXp;
    private final int xpGainedInSession;
    private final int xpRemainingToGoal;
    private final int xpPerHour;
    private final double skillProgressToGoal;
    private final int actionsInSession;
    private final int actionsRemainingToGoal;
    private final int actionsPerHour;
    private final String timeTillGoal;
    private final String timeTillGoalHours;
    private final String timeTillGoalShort;
    private final boolean compactView;

    XpSnapshotSingle(int startLevel, int endLevel, int startGoalXp, int endGoalXp, int xpGainedInSession, int xpRemainingToGoal, int xpPerHour, double skillProgressToGoal, int actionsInSession, int actionsRemainingToGoal, int actionsPerHour, String timeTillGoal, String timeTillGoalHours, String timeTillGoalShort, boolean compactView) {
        this.startLevel = startLevel;
        this.endLevel = endLevel;
        this.startGoalXp = startGoalXp;
        this.endGoalXp = endGoalXp;
        this.xpGainedInSession = xpGainedInSession;
        this.xpRemainingToGoal = xpRemainingToGoal;
        this.xpPerHour = xpPerHour;
        this.skillProgressToGoal = skillProgressToGoal;
        this.actionsInSession = actionsInSession;
        this.actionsRemainingToGoal = actionsRemainingToGoal;
        this.actionsPerHour = actionsPerHour;
        this.timeTillGoal = timeTillGoal;
        this.timeTillGoalHours = timeTillGoalHours;
        this.timeTillGoalShort = timeTillGoalShort;
        this.compactView = compactView;
    }

    public static XpSnapshotSingleBuilder builder() {
        return new XpSnapshotSingleBuilder();
    }

    public int getStartLevel() {
        return this.startLevel;
    }

    public int getEndLevel() {
        return this.endLevel;
    }

    public int getStartGoalXp() {
        return this.startGoalXp;
    }

    public int getEndGoalXp() {
        return this.endGoalXp;
    }

    public int getXpGainedInSession() {
        return this.xpGainedInSession;
    }

    public int getXpRemainingToGoal() {
        return this.xpRemainingToGoal;
    }

    public int getXpPerHour() {
        return this.xpPerHour;
    }

    public double getSkillProgressToGoal() {
        return this.skillProgressToGoal;
    }

    public int getActionsInSession() {
        return this.actionsInSession;
    }

    public int getActionsRemainingToGoal() {
        return this.actionsRemainingToGoal;
    }

    public int getActionsPerHour() {
        return this.actionsPerHour;
    }

    public String getTimeTillGoal() {
        return this.timeTillGoal;
    }

    public String getTimeTillGoalHours() {
        return this.timeTillGoalHours;
    }

    public String getTimeTillGoalShort() {
        return this.timeTillGoalShort;
    }

    public boolean isCompactView() {
        return this.compactView;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof XpSnapshotSingle)) {
            return false;
        }
        XpSnapshotSingle other = (XpSnapshotSingle)o;
        if (this.getStartLevel() != other.getStartLevel()) {
            return false;
        }
        if (this.getEndLevel() != other.getEndLevel()) {
            return false;
        }
        if (this.getStartGoalXp() != other.getStartGoalXp()) {
            return false;
        }
        if (this.getEndGoalXp() != other.getEndGoalXp()) {
            return false;
        }
        if (this.getXpGainedInSession() != other.getXpGainedInSession()) {
            return false;
        }
        if (this.getXpRemainingToGoal() != other.getXpRemainingToGoal()) {
            return false;
        }
        if (this.getXpPerHour() != other.getXpPerHour()) {
            return false;
        }
        if (Double.compare(this.getSkillProgressToGoal(), other.getSkillProgressToGoal()) != 0) {
            return false;
        }
        if (this.getActionsInSession() != other.getActionsInSession()) {
            return false;
        }
        if (this.getActionsRemainingToGoal() != other.getActionsRemainingToGoal()) {
            return false;
        }
        if (this.getActionsPerHour() != other.getActionsPerHour()) {
            return false;
        }
        if (this.isCompactView() != other.isCompactView()) {
            return false;
        }
        String this$timeTillGoal = this.getTimeTillGoal();
        String other$timeTillGoal = other.getTimeTillGoal();
        if (this$timeTillGoal == null ? other$timeTillGoal != null : !this$timeTillGoal.equals(other$timeTillGoal)) {
            return false;
        }
        String this$timeTillGoalHours = this.getTimeTillGoalHours();
        String other$timeTillGoalHours = other.getTimeTillGoalHours();
        if (this$timeTillGoalHours == null ? other$timeTillGoalHours != null : !this$timeTillGoalHours.equals(other$timeTillGoalHours)) {
            return false;
        }
        String this$timeTillGoalShort = this.getTimeTillGoalShort();
        String other$timeTillGoalShort = other.getTimeTillGoalShort();
        return !(this$timeTillGoalShort == null ? other$timeTillGoalShort != null : !this$timeTillGoalShort.equals(other$timeTillGoalShort));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getStartLevel();
        result = result * 59 + this.getEndLevel();
        result = result * 59 + this.getStartGoalXp();
        result = result * 59 + this.getEndGoalXp();
        result = result * 59 + this.getXpGainedInSession();
        result = result * 59 + this.getXpRemainingToGoal();
        result = result * 59 + this.getXpPerHour();
        long $skillProgressToGoal = Double.doubleToLongBits(this.getSkillProgressToGoal());
        result = result * 59 + (int)($skillProgressToGoal >>> 32 ^ $skillProgressToGoal);
        result = result * 59 + this.getActionsInSession();
        result = result * 59 + this.getActionsRemainingToGoal();
        result = result * 59 + this.getActionsPerHour();
        result = result * 59 + (this.isCompactView() ? 79 : 97);
        String $timeTillGoal = this.getTimeTillGoal();
        result = result * 59 + ($timeTillGoal == null ? 43 : $timeTillGoal.hashCode());
        String $timeTillGoalHours = this.getTimeTillGoalHours();
        result = result * 59 + ($timeTillGoalHours == null ? 43 : $timeTillGoalHours.hashCode());
        String $timeTillGoalShort = this.getTimeTillGoalShort();
        result = result * 59 + ($timeTillGoalShort == null ? 43 : $timeTillGoalShort.hashCode());
        return result;
    }

    public String toString() {
        return "XpSnapshotSingle(startLevel=" + this.getStartLevel() + ", endLevel=" + this.getEndLevel() + ", startGoalXp=" + this.getStartGoalXp() + ", endGoalXp=" + this.getEndGoalXp() + ", xpGainedInSession=" + this.getXpGainedInSession() + ", xpRemainingToGoal=" + this.getXpRemainingToGoal() + ", xpPerHour=" + this.getXpPerHour() + ", skillProgressToGoal=" + this.getSkillProgressToGoal() + ", actionsInSession=" + this.getActionsInSession() + ", actionsRemainingToGoal=" + this.getActionsRemainingToGoal() + ", actionsPerHour=" + this.getActionsPerHour() + ", timeTillGoal=" + this.getTimeTillGoal() + ", timeTillGoalHours=" + this.getTimeTillGoalHours() + ", timeTillGoalShort=" + this.getTimeTillGoalShort() + ", compactView=" + this.isCompactView() + ")";
    }

    public static class XpSnapshotSingleBuilder {
        private int startLevel;
        private int endLevel;
        private int startGoalXp;
        private int endGoalXp;
        private int xpGainedInSession;
        private int xpRemainingToGoal;
        private int xpPerHour;
        private double skillProgressToGoal;
        private int actionsInSession;
        private int actionsRemainingToGoal;
        private int actionsPerHour;
        private String timeTillGoal;
        private String timeTillGoalHours;
        private String timeTillGoalShort;
        private boolean compactView;

        XpSnapshotSingleBuilder() {
        }

        public XpSnapshotSingleBuilder startLevel(int startLevel) {
            this.startLevel = startLevel;
            return this;
        }

        public XpSnapshotSingleBuilder endLevel(int endLevel) {
            this.endLevel = endLevel;
            return this;
        }

        public XpSnapshotSingleBuilder startGoalXp(int startGoalXp) {
            this.startGoalXp = startGoalXp;
            return this;
        }

        public XpSnapshotSingleBuilder endGoalXp(int endGoalXp) {
            this.endGoalXp = endGoalXp;
            return this;
        }

        public XpSnapshotSingleBuilder xpGainedInSession(int xpGainedInSession) {
            this.xpGainedInSession = xpGainedInSession;
            return this;
        }

        public XpSnapshotSingleBuilder xpRemainingToGoal(int xpRemainingToGoal) {
            this.xpRemainingToGoal = xpRemainingToGoal;
            return this;
        }

        public XpSnapshotSingleBuilder xpPerHour(int xpPerHour) {
            this.xpPerHour = xpPerHour;
            return this;
        }

        public XpSnapshotSingleBuilder skillProgressToGoal(double skillProgressToGoal) {
            this.skillProgressToGoal = skillProgressToGoal;
            return this;
        }

        public XpSnapshotSingleBuilder actionsInSession(int actionsInSession) {
            this.actionsInSession = actionsInSession;
            return this;
        }

        public XpSnapshotSingleBuilder actionsRemainingToGoal(int actionsRemainingToGoal) {
            this.actionsRemainingToGoal = actionsRemainingToGoal;
            return this;
        }

        public XpSnapshotSingleBuilder actionsPerHour(int actionsPerHour) {
            this.actionsPerHour = actionsPerHour;
            return this;
        }

        public XpSnapshotSingleBuilder timeTillGoal(String timeTillGoal) {
            this.timeTillGoal = timeTillGoal;
            return this;
        }

        public XpSnapshotSingleBuilder timeTillGoalHours(String timeTillGoalHours) {
            this.timeTillGoalHours = timeTillGoalHours;
            return this;
        }

        public XpSnapshotSingleBuilder timeTillGoalShort(String timeTillGoalShort) {
            this.timeTillGoalShort = timeTillGoalShort;
            return this;
        }

        public XpSnapshotSingleBuilder compactView(boolean compactView) {
            this.compactView = compactView;
            return this;
        }

        public XpSnapshotSingle build() {
            return new XpSnapshotSingle(this.startLevel, this.endLevel, this.startGoalXp, this.endGoalXp, this.xpGainedInSession, this.xpRemainingToGoal, this.xpPerHour, this.skillProgressToGoal, this.actionsInSession, this.actionsRemainingToGoal, this.actionsPerHour, this.timeTillGoal, this.timeTillGoalHours, this.timeTillGoalShort, this.compactView);
        }

        public String toString() {
            return "XpSnapshotSingle.XpSnapshotSingleBuilder(startLevel=" + this.startLevel + ", endLevel=" + this.endLevel + ", startGoalXp=" + this.startGoalXp + ", endGoalXp=" + this.endGoalXp + ", xpGainedInSession=" + this.xpGainedInSession + ", xpRemainingToGoal=" + this.xpRemainingToGoal + ", xpPerHour=" + this.xpPerHour + ", skillProgressToGoal=" + this.skillProgressToGoal + ", actionsInSession=" + this.actionsInSession + ", actionsRemainingToGoal=" + this.actionsRemainingToGoal + ", actionsPerHour=" + this.actionsPerHour + ", timeTillGoal=" + this.timeTillGoal + ", timeTillGoalHours=" + this.timeTillGoalHours + ", timeTillGoalShort=" + this.timeTillGoalShort + ", compactView=" + this.compactView + ")";
        }
    }
}

