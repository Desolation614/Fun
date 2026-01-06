/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import net.runelite.launcher.OS;

class TrustManagerUtil {
    TrustManagerUtil() {
    }

    private static TrustManager[] loadTrustManagers(String trustStoreType) throws KeyStoreException, NoSuchAlgorithmException {
        String old = trustStoreType != null ? System.setProperty("javax.net.ssl.trustStoreType", trustStoreType) : System.clearProperty("javax.net.ssl.trustStoreType");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore)null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (old == null) {
            System.clearProperty("javax.net.ssl.trustStoreType");
        } else {
            System.setProperty("javax.net.ssl.trustStoreType", old);
        }
        return trustManagers;
    }

    static void setupTrustManager() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (OS.getOs() != OS.OSType.Windows) {
            return;
        }
        TrustManager[] jreTms = TrustManagerUtil.loadTrustManagers(null);
        TrustManager[] windowsTms = TrustManagerUtil.loadTrustManagers("Windows-ROOT");
        final TrustManager[] trustManagers = new TrustManager[jreTms.length + windowsTms.length];
        System.arraycopy(jreTms, 0, trustManagers, 0, jreTms.length);
        System.arraycopy(windowsTms, 0, trustManagers, jreTms.length, windowsTms.length);
        X509TrustManager combiningTrustManager = new X509TrustManager(){

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                CertificateException exception = null;
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) continue;
                    try {
                        ((X509TrustManager)trustManager).checkClientTrusted(chain, authType);
                        return;
                    }
                    catch (CertificateException ex) {
                        exception = ex;
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                throw new CertificateException("no X509TrustManagers present");
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                CertificateException exception = null;
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) continue;
                    try {
                        ((X509TrustManager)trustManager).checkServerTrusted(chain, authType);
                        return;
                    }
                    catch (CertificateException ex) {
                        exception = ex;
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                throw new CertificateException("no X509TrustManagers present");
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) continue;
                    certificates.addAll(Arrays.asList(((X509TrustManager)trustManager).getAcceptedIssuers()));
                }
                return certificates.toArray(new X509Certificate[0]);
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{combiningTrustManager}, new SecureRandom());
        SSLContext.setDefault(sc);
    }

    static void setupInsecureTrustManager() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager trustManager = new X509TrustManager(){

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        SSLContext.setDefault(sc);
    }
}

