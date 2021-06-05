package pers.louisj.Zwm.Core.WinApi;

import com.sun.jna.Library;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;

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

    int DwmSetWindowAttribute(HWND hwnd, int dwAttribute, PointerType pvAttribute, int cbAttribute);

    int DwmGetWindowAttribute(HWND hwnd, int dwAttribute, PointerType pvAttribute, int cbAttribute);

    int DwmGetWindowAttribute(HWND hwnd, int dwAttribute, Structure pvAttribute, int cbAttribute);

}
