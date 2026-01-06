/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.menuentryswapper;

import java.util.function.Predicate;
import java.util.function.Supplier;

final class Swap {
    private final Predicate<String> optionPredicate;
    private final Predicate<String> targetPredicate;
    private final String swappedOption;
    private final Supplier<Boolean> enabled;
    private final boolean strict;

    public Swap(Predicate<String> optionPredicate, Predicate<String> targetPredicate, String swappedOption, Supplier<Boolean> enabled, boolean strict) {
        this.optionPredicate = optionPredicate;
        this.targetPredicate = targetPredicate;
        this.swappedOption = swappedOption;
        this.enabled = enabled;
        this.strict = strict;
    }

    public Predicate<String> getOptionPredicate() {
        return this.optionPredicate;
    }

    public Predicate<String> getTargetPredicate() {
        return this.targetPredicate;
    }

    public String getSwappedOption() {
        return this.swappedOption;
    }

    public Supplier<Boolean> getEnabled() {
        return this.enabled;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Swap)) {
            return false;
        }
        Swap other = (Swap)o;
        if (this.isStrict() != other.isStrict()) {
            return false;
        }
        Predicate<String> this$optionPredicate = this.getOptionPredicate();
        Predicate<String> other$optionPredicate = other.getOptionPredicate();
        if (this$optionPredicate == null ? other$optionPredicate != null : !this$optionPredicate.equals(other$optionPredicate)) {
            return false;
        }
        Predicate<String> this$targetPredicate = this.getTargetPredicate();
        Predicate<String> other$targetPredicate = other.getTargetPredicate();
        if (this$targetPredicate == null ? other$targetPredicate != null : !this$targetPredicate.equals(other$targetPredicate)) {
            return false;
        }
        String this$swappedOption = this.getSwappedOption();
        String other$swappedOption = other.getSwappedOption();
        if (this$swappedOption == null ? other$swappedOption != null : !this$swappedOption.equals(other$swappedOption)) {
            return false;
        }
        Supplier<Boolean> this$enabled = this.getEnabled();
        Supplier<Boolean> other$enabled = other.getEnabled();
        return !(this$enabled == null ? other$enabled != null : !this$enabled.equals(other$enabled));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isStrict() ? 79 : 97);
        Predicate<String> $optionPredicate = this.getOptionPredicate();
        result = result * 59 + ($optionPredicate == null ? 43 : $optionPredicate.hashCode());
        Predicate<String> $targetPredicate = this.getTargetPredicate();
        result = result * 59 + ($targetPredicate == null ? 43 : $targetPredicate.hashCode());
        String $swappedOption = this.getSwappedOption();
        result = result * 59 + ($swappedOption == null ? 43 : $swappedOption.hashCode());
        Supplier<Boolean> $enabled = this.getEnabled();
        result = result * 59 + ($enabled == null ? 43 : $enabled.hashCode());
        return result;
    }

    public String toString() {
        return "Swap(optionPredicate=" + String.valueOf(this.getOptionPredicate()) + ", targetPredicate=" + String.valueOf(this.getTargetPredicate()) + ", swappedOption=" + this.getSwappedOption() + ", enabled=" + String.valueOf(this.getEnabled()) + ", strict=" + this.isStrict() + ")";
    }
}

