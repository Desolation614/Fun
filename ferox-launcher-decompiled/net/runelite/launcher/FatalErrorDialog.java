/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.launcher;

import com.google.common.base.MoreObjects;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.net.ssl.SSLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import net.runelite.launcher.Launcher;
import net.runelite.launcher.LauncherProperties;
import net.runelite.launcher.LinkBrowser;
import net.runelite.launcher.SplashScreen;
import net.runelite.launcher.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FatalErrorDialog
extends JDialog {
    private static final Logger log = LoggerFactory.getLogger(FatalErrorDialog.class);
    private static final AtomicBoolean alreadyOpen = new AtomicBoolean(false);
    private static final Color DARKER_GRAY_COLOR = new Color(30, 30, 30);
    private static final Color DARK_GRAY_COLOR = new Color(40, 40, 40);
    private static final Color DARK_GRAY_HOVER_COLOR = new Color(35, 35, 35);
    private final JPanel rightColumn = new JPanel();
    private final Font font = new Font("Dialog", 0, 12);

    public FatalErrorDialog(String message) {
        InputStream in2;
        if (alreadyOpen.getAndSet(true)) {
            throw new IllegalStateException("Fatal error during fatal error: " + message);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception exception) {
            // empty catch block
        }
        UIManager.put("Button.select", DARKER_GRAY_COLOR);
        try {
            in2 = FatalErrorDialog.class.getResourceAsStream(LauncherProperties.getRuneLite128());
            try {
                this.setIconImage(ImageIO.read(in2));
            }
            finally {
                if (in2 != null) {
                    in2.close();
                }
            }
        }
        catch (IOException in2) {
            // empty catch block
        }
        try {
            in2 = FatalErrorDialog.class.getResourceAsStream(LauncherProperties.getRuneLiteSplash());
            try {
                BufferedImage logo = ImageIO.read(in2);
                JLabel runelite = new JLabel();
                runelite.setIcon(new ImageIcon(logo));
                runelite.setAlignmentX(0.5f);
                runelite.setBackground(DARK_GRAY_COLOR);
                runelite.setOpaque(true);
                this.rightColumn.add(runelite);
            }
            finally {
                if (in2 != null) {
                    in2.close();
                }
            }
        }
        catch (IOException in3) {
            // empty catch block
        }
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(-1);
            }
        });
        this.setTitle("Fatal error starting Ferox");
        this.setLayout(new BorderLayout());
        Container pane = this.getContentPane();
        pane.setBackground(DARKER_GRAY_COLOR);
        JPanel leftPane = new JPanel();
        leftPane.setBackground(DARKER_GRAY_COLOR);
        leftPane.setLayout(new BorderLayout());
        JLabel title = new JLabel("There was a fatal error starting Ferox");
        title.setForeground(Color.WHITE);
        title.setFont(this.font.deriveFont(16.0f));
        title.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPane.add((Component)title, "North");
        leftPane.setPreferredSize(new Dimension(400, 200));
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(this.font);
        textArea.setBackground(DARKER_GRAY_COLOR);
        textArea.setForeground(Color.LIGHT_GRAY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        textArea.setEditable(false);
        leftPane.add((Component)textArea, "Center");
        pane.add((Component)leftPane, "Center");
        this.rightColumn.setLayout(new BoxLayout(this.rightColumn, 1));
        this.rightColumn.setBackground(DARK_GRAY_COLOR);
        this.rightColumn.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
        this.addButton("Open logs folder", () -> LinkBrowser.open(Launcher.LOGS_DIR.toString()));
        this.addButton("Get help on Discord", () -> LinkBrowser.browse(LauncherProperties.getDiscordInvite()));
        this.addButton("Troubleshooting steps", () -> LinkBrowser.browse(LauncherProperties.getTroubleshootingLink()));
        pane.add((Component)this.rightColumn, "East");
    }

    public void open() {
        this.addButton("Exit", () -> System.exit(-1));
        this.pack();
        SplashScreen.stop();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public FatalErrorDialog addButton(String message, Runnable action) {
        JButton button = new JButton(message);
        button.addActionListener(e -> action.run());
        button.setFont(this.font);
        button.setBackground(DARK_GRAY_COLOR);
        button.setForeground(Color.LIGHT_GRAY);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DARK_GRAY_COLOR.brighter()), new EmptyBorder(4, 4, 4, 4)));
        button.setAlignmentX(0.5f);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        button.setFocusPainted(false);
        button.addChangeListener(ev -> {
            if (button.getModel().isPressed()) {
                button.setBackground(DARKER_GRAY_COLOR);
            } else if (button.getModel().isRollover()) {
                button.setBackground(DARK_GRAY_HOVER_COLOR);
            } else {
                button.setBackground(DARK_GRAY_COLOR);
            }
        });
        this.rightColumn.add(button);
        this.rightColumn.revalidate();
        return this;
    }

    public static void showNetErrorWindow(String action, Throwable error) {
        Throwable err;
        assert (error != null);
        Stack<Throwable> exceptionStack = new Stack<Throwable>();
        int cnt = 1;
        for (err = error; err != null; err = err.getCause()) {
            log.debug("Exception #{}: {}", (Object)cnt++, (Object)err.getClass().getName());
            exceptionStack.push(err);
        }
        while (!exceptionStack.isEmpty()) {
            err = (Throwable)exceptionStack.pop();
            if (err instanceof VerificationException) {
                new FatalErrorDialog(FatalErrorDialog.formatExceptionMessage("Ferox was unable to verify the security of its connection to the internet while " + action + ". You may have a misbehaving antivirus, internet service provider, a proxy, or an incomplete java installation.", err)).open();
                return;
            }
            if (err instanceof SocketException) {
                String message = "Ferox is unable to connect to a required server while " + action + ".";
                message = err.getMessage() != null && err.getMessage().equals("connect: Address is invalid on local machine, or port is not valid on remote machine") ? message + " Cannot assign requested address. This error is most commonly caused by \"split tunneling\" support in VPN software. If you are using a VPN, try turning \"split tunneling\" off." : (err.getMessage() != null && err.getMessage().equals("Permission denied: connect") ? message + " Your internet access is blocked. Firewall or antivirus software may have blocked the connection." : (err instanceof ConnectException && err.getMessage() != null && err.getMessage().equals("Permission denied: no further information") ? message + " Your internet access is blocked. Firewall or antivirus software may have blocked the connection." : message + " Please check your internet connection."));
                new FatalErrorDialog(FatalErrorDialog.formatExceptionMessage(message, err)).open();
                return;
            }
            if (err instanceof UnknownHostException || err instanceof UnresolvedAddressException) {
                new FatalErrorDialog(FatalErrorDialog.formatExceptionMessage("Ferox is unable to resolve the address of a required server while " + action + ". Your DNS resolver may be misconfigured, pointing to an inaccurate resolver, or your internet connection may be down.", err)).addButton("Change your DNS resolver", () -> LinkBrowser.browse(LauncherProperties.getDNSChangeLink())).open();
                return;
            }
            if (err instanceof CertificateException) {
                if (err instanceof CertificateNotYetValidException || err instanceof CertificateExpiredException) {
                    new FatalErrorDialog(FatalErrorDialog.formatExceptionMessage("Ferox was unable to verify the certificate of a required server while " + action + ". Check your system clock is correct.", err)).open();
                    return;
                }
                new FatalErrorDialog(FatalErrorDialog.formatExceptionMessage("Ferox was unable to verify the certificate of a required server while " + action + ". This can be caused by a firewall, antivirus, malware, misbehaving internet service provider, or a proxy.", err)).open();
                return;
            }
            if (!(err instanceof SSLException)) continue;
            new FatalErrorDialog(FatalErrorDialog.formatExceptionMessage("Ferox was unable to establish a SSL/TLS connection with a required server while " + action + ". This can be caused by a firewall, antivirus, malware, misbehaving internet service provider, or a proxy.", err)).open();
            return;
        }
        new FatalErrorDialog(FatalErrorDialog.formatExceptionMessage("Ferox encountered a fatal error while " + action + ".", error)).open();
    }

    private static String formatExceptionMessage(String message, Throwable err) {
        String nl = System.getProperty("line.separator");
        return message + nl + nl + "Exception: " + err.getClass().getSimpleName() + nl + "Message: " + MoreObjects.firstNonNull(err.getMessage(), "n/a");
    }
}

