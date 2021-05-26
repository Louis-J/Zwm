package org.louisj.Zwm.WinApi;

// package ch.njol.betterdesktop.win32;

import java.awt.Window;
import java.util.Arrays;
import java.util.List;

// import org.eclipse.jdt.annotation.Nullable;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

public interface DWMApi extends Library {
    public final static int DWMWA_NCRENDERING_ENABLED = 1;
    public final static int DWMWA_NCRENDERING_POLICY = 2;
    public final static int DWMWA_TRANSITIONS_FORCEDISABLED = 3;
    public final static int DWMWA_ALLOW_NCPAINT = 4;
    public final static int DWMWA_CAPTION_BUTTON_BOUNDS = 5;
    public final static int DWMWA_NONCLIENT_RTL_LAYOUT = 6;
    public final static int DWMWA_FORCE_ICONIC_REPRESENTATION = 7;
    public final static int DWMWA_FLIP3D_POLICY = 8;
    public final static int DWMWA_EXTENDED_FRAME_BOUNDS = 9;
    public final static int DWMWA_HAS_ICONIC_BITMAP = 10;
    public final static int DWMWA_DISALLOW_PEEK = 11;
    public final static int DWMWA_EXCLUDED_FROM_PEEK = 12;
    public final static int DWMWA_CLOAK = 13;
    public final static int DWMWA_CLOAKED = 14;
    public final static int DWMWA_FREEZE_REPRESENTATION = 15;
    public final static int DWMWA_LAS = 16;
    // int DwmEnableBlurBehindWindow(HWND hWnd, DWM_BLURBEHIND[] pBlurBehind);

    // public static class DWM_BLURBEHIND extends Structure {
    //     public long dwFlags; // bitflags: which of the following options are set
    //     public boolean fEnable;
    //     public Pointer hRgnBlur = null;
    //     public boolean fTransitionOnMaximized;

    //     public DWM_BLURBEHIND(final boolean fEnable) {
    //         dwFlags = 1;
    //         this.fEnable = fEnable;
    //     }

    //     @Override
    //     protected List<String> getFieldOrder() {
    //         return Arrays.asList("dwFlags", "fEnable", "hRgnBlur", "fTransitionOnMaximized");
    //     }
    // }

    // public static void setBlurBehind(final Window w, final boolean blur) {
    //     final HWND hwnd = new HWND();
    //     hwnd.setPointer(Native.getComponentPointer(w));
    //     // final int error =
    //     INSTANCE.DwmEnableBlurBehindWindow(hwnd, new Dwmapi.DWM_BLURBEHIND[] { new DWM_BLURBEHIND(blur) });
    //     // System.out.println(error);
    // }

    int DwmSetWindowAttribute(HWND hwnd, int dwAttribute, PointerType pvAttribute, int cbAttribute);

    // int DWMWA_EXCLUDED_FROM_PEEK = 12;

    // public static void setExcludedFromPeek(final Window w, final boolean excluded) {
    //     final HWND hwnd = new HWND();
    //     hwnd.setPointer(Native.getComponentPointer(w));
    //     // final int error =
    //     INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_EXCLUDED_FROM_PEEK, new IntByReference(excluded ? 1 : 0), 4);
    //     // System.out.println(error);
    // }

    int DwmGetWindowAttribute(HWND hwnd, int dwAttribute, PointerType pvAttribute, int cbAttribute);
}
