/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.User32
 *  com.sun.jna.platform.win32.WinDef$DWORD
 *  com.sun.jna.platform.win32.WinDef$HWND
 *  com.sun.jna.platform.win32.WinDef$WORD
 *  com.sun.jna.platform.win32.WinUser$INPUT
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package net.runelite.client.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.util.Map;
import net.runelite.client.util.OSType;

public class WinUtil {
    private static boolean isWindowArrangedSupported = OSType.getOSType() == OSType.Windows;

    public static void requestForeground(Frame frame) {
        frame.setState(0);
        User32 user32 = User32.INSTANCE;
        WinUser.INPUT input = new WinUser.INPUT();
        input.type = new WinDef.DWORD(1L);
        input.input.ki.wVk = new WinDef.WORD(133L);
        user32.SendInput(new WinDef.DWORD(1L), (WinUser.INPUT[])input.toArray(1), input.size());
        WinDef.HWND hwnd = new WinDef.HWND(Native.getComponentPointer((Component)frame));
        user32.SetForegroundWindow(hwnd);
    }

    public static boolean isWindowArranged(Window window) {
        if (!isWindowArrangedSupported || !window.isDisplayable()) {
            return false;
        }
        try {
            Pointer hwnd = Native.getComponentPointer((Component)window);
            return RLUser32.INSTANCE.IsWindowArranged(hwnd);
        }
        catch (LinkageError ignored) {
            isWindowArrangedSupported = false;
            return false;
        }
    }

    static interface RLUser32
    extends StdCallLibrary {
        public static final RLUser32 INSTANCE = (RLUser32)Native.load((String)"user32", RLUser32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);

        public boolean IsWindowArranged(Pointer var1);
    }
}

