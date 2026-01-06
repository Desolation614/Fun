/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.info;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JRichTextPane
extends JEditorPane {
    private static final Logger log = LoggerFactory.getLogger(JRichTextPane.class);
    private HyperlinkListener linkHandler;

    public JRichTextPane() {
        this.setHighlighter(null);
        this.setEditable(false);
        this.setOpaque(false);
        this.enableAutoLinkHandler(true);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HTMLEditorKit ek = (HTMLEditorKit)this.getEditorKitForContentType("text/html");
        ek.getStyleSheet().addRule("a {color: #DDDDDD }");
    }

    public JRichTextPane(String type, String text) {
        this();
        this.setContentType(type);
        this.setText(text);
    }

    public void enableAutoLinkHandler(boolean enable) {
        if (enable == (this.linkHandler == null)) {
            if (enable) {
                this.linkHandler = e -> {
                    if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()) && e.getURL() != null && Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        }
                        catch (IOException | URISyntaxException ex) {
                            log.warn("Error opening link", (Throwable)ex);
                        }
                    }
                };
                this.addHyperlinkListener(this.linkHandler);
            } else {
                this.removeHyperlinkListener(this.linkHandler);
                this.linkHandler = null;
            }
        }
    }

    public boolean getAutoLinkHandlerEnabled() {
        return this.linkHandler != null;
    }
}

