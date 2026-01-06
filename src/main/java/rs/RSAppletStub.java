/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.rs;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import net.runelite.client.RuntimeConfig;
import net.runelite.client.RuntimeConfigLoader;
import net.runelite.client.rs.RSConfig;
import net.runelite.client.ui.FatalErrorDialog;

class RSAppletStub
implements AppletStub {
    private final RSConfig config;
    private final RuntimeConfigLoader runtimeConfigLoader;

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public URL getDocumentBase() {
        return this.getCodeBase();
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URL(this.config.getCodeBase());
        }
        catch (MalformedURLException ex) {
            return null;
        }
    }

    @Override
    public String getParameter(String name) {
        return this.config.getAppletProperties().get(name);
    }

    @Override
    public AppletContext getAppletContext() {
        return new AppletContext(){

            @Override
            public AudioClip getAudioClip(URL url) {
                return null;
            }

            @Override
            public Image getImage(URL url) {
                return null;
            }

            @Override
            public Applet getApplet(String name) {
                return null;
            }

            @Override
            public Enumeration<Applet> getApplets() {
                return null;
            }

            @Override
            public void showDocument(URL url) {
                if (url.getPath().startsWith("/error_game_")) {
                    try {
                        RuntimeConfig rtc = RSAppletStub.this.runtimeConfigLoader.get();
                        if (rtc.showOutageMessage()) {
                            return;
                        }
                    }
                    catch (Exception rtc) {
                        // empty catch block
                    }
                    String code = url.getPath().replace("/", "").replace(".ws", "");
                    if (code.equals("error_game_js5connect")) {
                        SwingUtilities.invokeLater(() -> new FatalErrorDialog("RuneLite is unable to connect to the Ferox update server. Ferox might be offline for an update, check the game status page. If the game is online, then either a firewall is blocking RuneLite, or you don't have internet access.").setTitle("Ferox", "Unable to connect to update server").open());
                    } else if (code.equals("error_game_js5io")) {
                        SwingUtilities.invokeLater(() -> new FatalErrorDialog("Ferox is unable to retrieve updates from its update server. This is likely due to a firewall blocking the Ferox server. Try disabling your firewall, or use a different network.").setTitle("Ferox", "Unable to connect to update server").addHelpButtons().open());
                    } else if (code.equals("error_game_crash")) {
                        SwingUtilities.invokeLater(() -> new FatalErrorDialog("Ferox has crashed. Crashes are most commonly caused by plugin hub plugins, but can also be caused by RuneLite or Jagex client bugs. If you receive this message commonly, try playing in safe mode to eliminate the potential of plugins causing the crash. The client log file will contain additional information about the crash.").setTitle("Ferox", "Ferox has crashed").addHelpButtons().open());
                    } else {
                        SwingUtilities.invokeLater(() -> new FatalErrorDialog("Ferox has crashed with the message: " + code).setTitle("Ferox", "Ferox has crashed").addHelpButtons().open());
                    }
                }
            }

            @Override
            public void showDocument(URL url, String target) {
                this.showDocument(url);
            }

            @Override
            public void showStatus(String status) {
            }

            @Override
            public void setStream(String key, InputStream stream) throws IOException {
            }

            @Override
            public InputStream getStream(String key) {
                return null;
            }

            @Override
            public Iterator<String> getStreamKeys() {
                return null;
            }
        };
    }

    @Override
    public void appletResize(int width, int height) {
    }

    public RSAppletStub(RSConfig config, RuntimeConfigLoader runtimeConfigLoader) {
        this.config = config;
        this.runtimeConfigLoader = runtimeConfigLoader;
    }
}

