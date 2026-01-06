/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.runelite.client.RuneLite;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.SplashScreen;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FatalErrorDialog
extends JDialog {
    private static final Logger log = LoggerFactory.getLogger(FatalErrorDialog.class);
    private static final AtomicBoolean alreadyOpen = new AtomicBoolean(false);
    private final JPanel rightColumn = new JPanel();
    private final Font font = new Font("Dialog", 0, 12);
    private final JLabel title;

    public FatalErrorDialog(String message) {
        if (alreadyOpen.getAndSet(true)) {
            throw new IllegalStateException("Fatal error during fatal error: " + message);
        }
        try {
            this.setIconImages(Arrays.asList(ClientUI.ICON_128, ClientUI.ICON_16));
            BufferedImage logo = ImageUtil.loadImageResource(FatalErrorDialog.class, "runelite_splash.png");
            JLabel runelite = new JLabel();
            runelite.setIcon(new ImageIcon(logo));
            runelite.setAlignmentX(0.5f);
            runelite.setBackground(ColorScheme.DARK_GRAY_COLOR);
            runelite.setOpaque(true);
            this.rightColumn.add(runelite);
        }
        catch (RuntimeException logo) {
            // empty catch block
        }
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(-1);
            }
        });
        this.setTitle("Fatal error starting RuneLite");
        this.setLayout(new BorderLayout());
        JPanel pane = (JPanel)this.getContentPane();
        pane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel leftPane = new JPanel();
        leftPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        leftPane.setLayout(new BorderLayout());
        this.title = new JLabel("There was a fatal error starting RuneLite");
        this.title.setForeground(Color.WHITE);
        this.title.setFont(this.font.deriveFont(16.0f));
        this.title.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPane.add((Component)this.title, "North");
        leftPane.setPreferredSize(new Dimension(400, 200));
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(this.font);
        textArea.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textArea.setForeground(Color.LIGHT_GRAY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        textArea.setEditable(false);
        textArea.setOpaque(false);
        leftPane.add((Component)textArea, "Center");
        pane.add((Component)leftPane, "Center");
        this.rightColumn.setLayout(new BoxLayout(this.rightColumn, 1));
        this.rightColumn.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.rightColumn.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
        pane.add((Component)this.rightColumn, "East");
    }

    public void open() {
        this.addButton("Exit", () -> System.exit(-1));
        this.pack();
        this.setLocationRelativeTo(null);
        SplashScreen.stop();
        this.setVisible(true);
    }

    public FatalErrorDialog addButton(String message, Runnable action) {
        JButton button = new JButton(message);
        button.addActionListener(e -> action.run());
        button.setFont(this.font);
        button.setBackground(ColorScheme.DARK_GRAY_COLOR);
        button.setForeground(Color.LIGHT_GRAY);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR.brighter()), new EmptyBorder(4, 4, 4, 4)));
        button.setAlignmentX(0.5f);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        button.setFocusPainted(false);
        button.addChangeListener(ev -> {
            if (button.getModel().isPressed()) {
                button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            } else if (button.getModel().isRollover()) {
                button.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
            } else {
                button.setBackground(ColorScheme.DARK_GRAY_COLOR);
            }
        });
        this.rightColumn.add(button);
        this.rightColumn.revalidate();
        return this;
    }

    public FatalErrorDialog setTitle(String windowTitle, String header) {
        super.setTitle(windowTitle);
        this.title.setText(header);
        return this;
    }

    public FatalErrorDialog addHelpButtons() {
        return this.addButton("Open logs folder", () -> LinkBrowser.open(RuneLite.LOGS_DIR.toString())).addButton("Get help on Discord", () -> LinkBrowser.browse(RuneLiteProperties.getDiscordInvite())).addButton("Troubleshooting steps", () -> LinkBrowser.browse(RuneLiteProperties.getTroubleshootingLink()));
    }

    public FatalErrorDialog addBuildingGuide() {
        return this.addButton("Building guide", () -> LinkBrowser.browse(RuneLiteProperties.getBuildingLink()));
    }

    public static void showNetErrorWindow(String action, Throwable err) {
        if (err instanceof VerificationException || err instanceof GeneralSecurityException) {
            new FatalErrorDialog("RuneLite was unable to verify the security of its connection to the internet while " + action + ". You may have a misbehaving antivirus, internet service provider, a proxy, or an incomplete java installation.").addHelpButtons().open();
            return;
        }
        if (err instanceof ConnectException) {
            new FatalErrorDialog("RuneLite is unable to connect to a required server while " + action + ". Please check your internet connection").addHelpButtons().open();
            return;
        }
        if (err instanceof UnknownHostException) {
            new FatalErrorDialog("RuneLite is unable to resolve the address of a required server while " + action + ". Your DNS resolver may be misconfigured, pointing to an inaccurate resolver, or your internet connection may be down. ").addHelpButtons().addButton("Change your DNS resolver", () -> LinkBrowser.browse(RuneLiteProperties.getDNSChangeLink())).open();
            return;
        }
        new FatalErrorDialog("RuneLite encountered a fatal error while " + action + ".").addHelpButtons().open();
    }
}

