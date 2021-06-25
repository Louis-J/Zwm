package pers.louisj.Zwm.Core.L2.Window;

import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

public abstract class WindowStaticAction {
    private static final int flagNormal = WinUser.SWP_FRAMECHANGED | WinUser.SWP_NOACTIVATE | WinUser.SWP_NOCOPYBITS
            | WinUser.SWP_NOZORDER | WinUser.SWP_NOOWNERZORDER;
    // private static final int flagMini = flagNormal | WinUser.SWP_NOMOVE |
    // WinUser.SWP_NOSIZE;

    public static Logger glogger = null;

    public static class HDWP4SetLoc extends PointerType {
        HDWP4SetLoc(Pointer p) {
            super(p);
        }

        public static HDWP4SetLoc BeginSetLocation(int num) {
            return new HDWP4SetLoc(WinHelper.MyUser32Inst.BeginDeferWindowPos(num));
        }

        public void SetLocation(Window window, Rectangle rect) {
            var rectOff = window.Query.GetOffset();
            if (rectOff == null) {
                window.Refresh.RefreshRect();
                window.Refresh.RefreshOffset();
                rectOff = window.Query.GetOffset();
            }
            var ret = WinHelper.MyUser32Inst.DeferWindowPos(getPointer(), window.hWnd, null, rect.x + rectOff.x,
                    rect.y + rectOff.y, rect.width + rectOff.width, rect.height + rectOff.height, flagNormal);
            if (ret == null || Pointer.nativeValue(ret) == 0) {
                var errCode = WinHelper.Kernel32Inst.GetLastError();
                if (errCode == 1400) // handle err, means the window closed, so ignore it
                    return;
                var e = new Win32Exception(errCode);
                System.out.println(e);
                if (glogger != null)
                    glogger.fatal(e);
                throw e;
            }
            setPointer(ret);
            window.Refresh.RefreshRect(rect);
        }

        public void EndSetLocation() {
            WinHelper.MyUser32Inst.EndDeferWindowPos(getPointer());
        }
    }

    public static void SetLocation2(Window window, Rectangle location) {
        if (!WinHelper.MyUser32Inst.SetWindowPos(window.hWnd, null, location.x, location.y, location.width,
                location.height, flagNormal)) {
            var errCode = WinHelper.Kernel32Inst.GetLastError();
            if (errCode == 1400) // handle err, means the window closed, so ignore it
                return;
            throw new Win32Exception(errCode);
        }
    }

    public static HWND GetForegroundWindow() {
        return WinHelper.MyUser32Inst.GetForegroundWindow();
    }

    public static void ShowMinimized(HWND hWnd) {
        // ShowWindow will NOT activate the next window!
        // SO ues PostMessage!
        final int WM_SYSCOMMAND = 0x0112;
        final int SC_MINIMIZE = 0xF020;

        if (hWnd == null || Pointer.nativeValue(hWnd.getPointer()) == 0)
            return;
        WinHelper.MyUser32Inst.PostMessage(hWnd, WM_SYSCOMMAND, new WPARAM(SC_MINIMIZE), null);
    }

    public static void ShowMaximized(HWND hWnd) {
        final int SW_SHOWMAXIMIZED = 3;
        if (hWnd == null || Pointer.nativeValue(hWnd.getPointer()) == 0)
            return;
        WinHelper.MyUser32Inst.ShowWindow(hWnd, SW_SHOWMAXIMIZED);
    }

    public static void SendClose(HWND hWnd) {
        final int WM_SYSCOMMAND = 0x0112;
        final int SC_CLOSE = 0xF060;

        if (hWnd == null || Pointer.nativeValue(hWnd.getPointer()) == 0)
            return;

        WinHelper.MyUser32Inst.SendNotifyMessage(hWnd, WM_SYSCOMMAND, new WPARAM(SC_CLOSE), new LPARAM(0));
    }
}
