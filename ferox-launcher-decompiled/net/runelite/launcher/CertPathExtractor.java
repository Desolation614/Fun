/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import sun.security.provider.certpath.AdjacencyList;
import sun.security.provider.certpath.SunCertPathBuilderException;

class CertPathExtractor {
    CertPathExtractor() {
    }

    static String extract(Throwable ex) {
        try {
            SunCertPathBuilderException pathBuilderEx = (SunCertPathBuilderException)ex.getCause().getCause().getCause();
            AdjacencyList adjList = pathBuilderEx.getAdjacencyList();
            return adjList.toString();
        }
        catch (Throwable throwable) {
            return null;
        }
    }
}

