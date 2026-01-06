/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.util;

import com.google.common.base.Strings;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Singleton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.runelite.client.util.OSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LinkBrowser {
    private static final Logger log = LoggerFactory.getLogger(LinkBrowser.class);
    private static boolean shouldAttemptXdg = OSType.getOSType() == OSType.Linux;

    public static void browse(String url) {
        new Thread(() -> {
            if (Strings.isNullOrEmpty((String)url)) {
                log.warn("LinkBrowser.browse() called with invalid input");
                return;
            }
            if (shouldAttemptXdg && LinkBrowser.attemptXdgOpen(url)) {
                log.debug("Opened url through xdg-open to {}", (Object)url);
                return;
            }
            if (LinkBrowser.attemptDesktopBrowse(url)) {
                log.debug("Opened url through Desktop#browse to {}", (Object)url);
                return;
            }
            log.warn("LinkBrowser.browse() could not open {}", (Object)url);
            LinkBrowser.showMessageBox("Unable to open link. Press 'OK' and the link will be copied to your clipboard.", url);
        }).start();
    }

    public static void open(String directory) {
        new Thread(() -> {
            if (Strings.isNullOrEmpty((String)directory)) {
                log.warn("LinkBrowser.open() called with invalid input");
                return;
            }
            if (shouldAttemptXdg && LinkBrowser.attemptXdgOpen(directory)) {
                log.debug("Opened directory through xdg-open to {}", (Object)directory);
                return;
            }
            if (LinkBrowser.attemptDesktopOpen(directory)) {
                log.debug("Opened directory through Desktop#open to {}", (Object)directory);
                return;
            }
            log.warn("LinkBrowser.open() could not open {}", (Object)directory);
            LinkBrowser.showMessageBox("Unable to open folder. Press 'OK' and the folder directory will be copied to your clipboard.", directory);
        }).start();
    }

    private static boolean attemptXdgOpen(String resource) {
        try {
            Process exec = Runtime.getRuntime().exec(new String[]{"xdg-open", resource});
            exec.waitFor();
            int ret = exec.exitValue();
            if (ret == 0) {
                return true;
            }
            log.warn("xdg-open {} returned with error code {}", (Object)resource, (Object)ret);
            return false;
        }
        catch (IOException ex) {
            shouldAttemptXdg = false;
            return false;
        }
        catch (InterruptedException ex) {
            log.warn("Interrupted while waiting for xdg-open {} to execute", (Object)resource);
            return false;
        }
    }

    private static boolean attemptDesktopBrowse(String url) {
        if (!Desktop.isDesktopSupported()) {
            return false;
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            return false;
        }
        try {
            desktop.browse(new URI(url));
            return true;
        }
        catch (IOException | URISyntaxException ex) {
            log.warn("Failed to open Desktop#browse {}", (Object)url, (Object)ex);
            return false;
        }
    }

    private static boolean attemptDesktopOpen(String directory) {
        if (!Desktop.isDesktopSupported()) {
            return false;
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            return false;
        }
        try {
            desktop.open(new File(directory));
            return true;
        }
        catch (IOException ex) {
            log.warn("Failed to open Desktop#open {}", (Object)directory, (Object)ex);
            return false;
        }
    }

    private static void showMessageBox(String message, String data) {
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.showConfirmDialog(null, message, "Message", 2);
            if (result == 0) {
                StringSelection stringSelection = new StringSelection(data);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });
    }
}

