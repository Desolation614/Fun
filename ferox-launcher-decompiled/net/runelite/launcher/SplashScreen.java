/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.launcher;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import net.runelite.launcher.LauncherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplashScreen
extends JFrame
implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(SplashScreen.class);
    private static final Color BRAND_ORANGE = new Color(220, 138, 0);
    private static final Color DARKER_GRAY_COLOR = new Color(30, 30, 30);
    private static final int WIDTH = 200;
    private static final int PAD = 10;
    private static SplashScreen INSTANCE;
    private final JLabel action = new JLabel("Loading");
    private final JProgressBar progress = new JProgressBar();
    private final JLabel subAction = new JLabel();
    private final Timer timer;
    private volatile double overallProgress = 0.0;
    private volatile String actionText = "Loading";
    private volatile String subActionText = "";
    private volatile String progressText = null;

    private SplashScreen() throws IOException {
        BufferedImage logo;
        this.setTitle("Ferox Launcher");
        this.setDefaultCloseOperation(3);
        this.setUndecorated(true);
        try (InputStream in = SplashScreen.class.getResourceAsStream(LauncherProperties.getRuneLite128());){
            this.setIconImage(ImageIO.read(in));
        }
        this.setLayout(null);
        Container pane = this.getContentPane();
        pane.setBackground(DARKER_GRAY_COLOR);
        Font font = new Font("Dialog", 0, 12);
        try (InputStream in = SplashScreen.class.getResourceAsStream(LauncherProperties.getRuneLiteSplash());){
            logo = ImageIO.read(in);
        }
        JLabel logoLabel = new JLabel(new ImageIcon(logo));
        pane.add(logoLabel);
        logoLabel.setBounds(0, 0, 200, 200);
        int y = 200;
        pane.add(this.action);
        this.action.setForeground(Color.WHITE);
        this.action.setBounds(0, y, 200, 16);
        this.action.setHorizontalAlignment(0);
        this.action.setFont(font);
        y += this.action.getHeight() + 10;
        pane.add(this.progress);
        this.progress.setForeground(BRAND_ORANGE);
        this.progress.setBackground(BRAND_ORANGE.darker().darker());
        this.progress.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.progress.setBounds(0, y, 200, 14);
        this.progress.setFont(font);
        this.progress.setUI(new BasicProgressBarUI(){

            @Override
            protected Color getSelectionBackground() {
                return Color.BLACK;
            }

            @Override
            protected Color getSelectionForeground() {
                return Color.BLACK;
            }
        });
        pane.add(this.subAction);
        this.subAction.setForeground(Color.LIGHT_GRAY);
        this.subAction.setBounds(0, y += 22, 200, 16);
        this.subAction.setHorizontalAlignment(0);
        this.subAction.setFont(font);
        this.setSize(200, y += this.subAction.getHeight() + 10);
        this.setLocationRelativeTo(null);
        this.timer = new Timer(100, this);
        this.timer.setRepeats(true);
        this.timer.start();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.action.setText(this.actionText);
        this.subAction.setText(this.subActionText);
        this.progress.setMaximum(1000);
        this.progress.setValue((int)(this.overallProgress * 1000.0));
        String progressText = this.progressText;
        if (progressText == null) {
            this.progress.setStringPainted(false);
        } else {
            this.progress.setStringPainted(true);
            this.progress.setString(progressText);
        }
    }

    public static void init() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                if (INSTANCE != null) {
                    return;
                }
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    INSTANCE = new SplashScreen();
                }
                catch (Exception e) {
                    log.warn("Unable to start splash screen", e);
                }
            });
        }
        catch (InterruptedException | InvocationTargetException bs) {
            throw new RuntimeException(bs);
        }
    }

    public static void stop() {
        SwingUtilities.invokeLater(() -> {
            if (INSTANCE == null) {
                return;
            }
            SplashScreen.INSTANCE.timer.stop();
            INSTANCE.setDefaultCloseOperation(0);
            INSTANCE.dispose();
            INSTANCE = null;
        });
    }

    public static void stage(double overallProgress, @Nullable String actionText, String subActionText) {
        SplashScreen.stage(overallProgress, actionText, subActionText, null);
    }

    public static void stage(double startProgress, double endProgress, @Nullable String actionText, String subActionText, int done, int total, boolean mib) {
        Object progress;
        if (mib) {
            double MiB = 1048576.0;
            double CEIL = 0.1;
            progress = String.format("%.1f / %.1f MiB", (double)done / 1048576.0, (double)total / 1048576.0 + 0.1);
        } else {
            progress = done + " / " + total;
        }
        SplashScreen.stage(startProgress + (endProgress - startProgress) * (double)done / (double)total, actionText, subActionText, (String)progress);
    }

    public static void stage(double overallProgress, @Nullable String actionText, String subActionText, @Nullable String progressText) {
        if (INSTANCE != null) {
            SplashScreen.INSTANCE.overallProgress = overallProgress;
            if (actionText != null) {
                SplashScreen.INSTANCE.actionText = actionText;
            }
            SplashScreen.INSTANCE.subActionText = subActionText;
            SplashScreen.INSTANCE.progressText = progressText;
        }
    }
}

