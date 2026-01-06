/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
 *  net.runelite.api.ItemComposition
 *  org.apache.commons.text.similarity.JaroWinklerDistance
 *  org.apache.commons.text.similarity.LongestCommonSubsequence
 *  org.apache.commons.text.similarity.SimilarityScore
 */
package net.runelite.client.plugins.grandexchange;

import java.util.function.ToDoubleFunction;
import javax.inject.Singleton;
import net.runelite.api.ItemComposition;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.apache.commons.text.similarity.SimilarityScore;

@Singleton
public class FuzzySearchScorer {
    private final SimilarityScore<Double> baseAlgorithm = new JaroWinklerDistance();

    public Double score(String query, String itemName) {
        query = query.toLowerCase().replace('-', ' ');
        itemName = itemName.toLowerCase().replace('-', ' ');
        String[] queryWords = query.split(" ");
        String[] itemWords = itemName.split(" ");
        double lcsScore = 0.0;
        for (String queryWord : queryWords) {
            for (String itemWord : itemWords) {
                int lcsLen = new LongestCommonSubsequence().longestCommonSubsequence((CharSequence)queryWord, (CharSequence)itemWord).length();
                lcsScore = Math.max(lcsScore, (double)lcsLen / (double)queryWord.length());
            }
        }
        double proximityScore = Math.log10(10.0 * (Double)this.baseAlgorithm.apply((CharSequence)query, (CharSequence)itemName)) - 0.5;
        return lcsScore + proximityScore - 1.0;
    }

    public ToDoubleFunction<ItemComposition> comparator(String query) {
        return item -> this.score(query.toLowerCase().replace('-', ' '), item.getName().toLowerCase().replace('-', ' '));
    }
}

