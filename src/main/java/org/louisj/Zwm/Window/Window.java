package org.louisj.Zwm.Window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.louisj.Zwm.WinApi.DWMApi;
import org.louisj.Zwm.WinApi.MyUser32;
import org.louisj.Zwm.WinApi.WinHelper;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.User32Util;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;

import com.sun.jna.platform.win32.WinUser;

import com.sun.jna.Pointer;

// import com.sun.jna.ptr.;

import com.sun.jna.platform.win32.Win32Exception;
// import com.sun.jna.platform.win32.WinDef.Handle;

public class Window {
    private static Logger logger = LogManager.getLogger("Window");

    private HWND handle;
    private int processId;

    public String processName;
    public String windowClass;
    public String windowTitle;

    // private WindowLocation location;
    private boolean _didManualHide;
    // public WindowFocusedDelegate WindowFocused;

    public static int GetWindowPid(HWND handle) {
        IntByReference pProcessId = new IntByReference();
        MyUser32.INSTANCE.GetWindowThreadProcessId(handle, pProcessId);
        return pProcessId.getValue();
    }

    public Window(HWND handle, int processId) {
        logger.info("Window, handle = {}", handle);
        this.handle = handle;
        this.processId = processId;

        // com.sun.jna.ptr.IntByReference pProcessId;
        // MyUser32.INSTANCE.GetWindowThreadProcessId(handle, pProcessId);
        // processId = pProcessId.getValue();
        logger.info("Window, processId = {}", processId);
        try {
            processName = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
                @Override
                public int Invoke(char[] buffer, int size) {
                    return WinHelper.MyUser32Inst.GetWindowModuleFileName(handle, buffer, size);
                }
            }, WinDef.MAX_PATH);
        } catch (com.sun.jna.platform.win32.Win32Exception e) {
            processName = "--NA--";
        }
        // processName = Kernel32Util.QueryFullProcessImageName(handle, 0);
        // processName = WinHelper.QueryWithBuffer2(new WinHelper.CallBackWithBuffer2()
        // {
        // @Override
        // public boolean Invoke(char[] buffer, IntByReference pSize) {
        // return WinHelper.Kernel32Inst.QueryFullProcessImageName(handle, 1, buffer,
        // pSize);
        // }
        // }, WinDef.MAX_PATH);

        windowTitle = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
            @Override
            public int Invoke(char[] buffer, int size) {
                return WinHelper.MyUser32Inst.GetWindowText(handle, buffer, size);
            }
        }, 128);

        windowClass = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
            @Override
            public int Invoke(char[] buffer, int size) {
                return WinHelper.MyUser32Inst.GetClassName(handle, buffer, size);
            }
        }, 128);

        // MyUser32.INSTANCE.GetWindowText()
        // location = new WindowLocation(rect.left, rect.top, rect.right - rect.left,
        // rect.bottom - rect.top, state);
    }

    public WindowLocation GetLocation() {
        RECT rect = new RECT();
        WinHelper.MyUser32Inst.GetWindowRect(handle, rect);
        byte state = 0;
        if (IsMinimized())
            state = 1;
        else if (IsMaximized())
            state = 2;
        return new WindowLocation(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, state);
    }

    public Rectangle GetOffset() {
        // TODO:
        // get offset between Window Rect via DwmGetWindowAttribute and Window Rect via
        // GetWindowRect

        // // Window Rect via GetWindowRect
        // Win32.Rect rect1 = new Win32.Rect();
        // Win32.GetWindowRect(_handle, ref rect1);

        // int X1 = rect1.Left;
        // int Y1 = rect1.Top;
        // int Width1 = rect1.Right - rect1.Left;
        // int Height1 = rect1.Bottom - rect1.Top;

        // // Window Rect via DwmGetWindowAttribute
        // Win32.Rect rect2 = new Win32.Rect();
        // int size = Marshal.SizeOf(typeof(Win32.Rect));
        // Win32.DwmGetWindowAttribute(_handle,
        // (int)Win32.DwmWindowAttribute.DWMWA_EXTENDED_FRAME_BOUNDS, out rect2, size);

        // int X2 = rect2.Left;
        // int Y2 = rect2.Top;
        // int Width2 = rect2.Right - rect2.Left;
        // int Height2 = rect2.Bottom - rect2.Top;

        // // Calculate offset
        // int X = X1 - X2;
        // int Y = Y1 - Y2;
        // int Width = Width1 - Width2;
        // int Height = Height1 - Height2;

        // return new Rectangle(X, Y, Width, Height);
        return null;
    }

    public boolean CanLayout() {
        return _didManualHide || (!IsCloaked() && IsAppWindow() && IsAltTabWindow());
    }

    public boolean IsFocused() {
        return WinHelper.MyUser32Inst.GetForegroundWindow() == handle;
    }

    public boolean IsMinimized() {
        return WinHelper.MyUser32Inst.IsIconic(handle);
    }

    public boolean IsMaximized() {
        return WinHelper.MyUser32Inst.IsZoomed(handle);
    }

    public void Focus() {
        if (!IsFocused()) {
            logger.info("Focus", this);
            // Win32Helper.ForceForegroundWindow(_handle);
            // WindowFocused?.Invoke();

            WinHelper.MyUser32Inst.SetForegroundWindow(handle);
            // WindowFocused?.Invoke();
        }
    }

    public void Hide() {
        final int SW_HIDE = 0;

        logger.info("Hide, {}", this);
        if (CanLayout()) {
            _didManualHide = true;
        }
        WinHelper.MyUser32Inst.ShowWindow(handle, SW_HIDE);
    }

    public void ShowNormal() {
        final int SW_SHOWNOACTIVATE = 4;

        logger.info("ShowNormal, {}", this);
        _didManualHide = false;
        WinHelper.MyUser32Inst.ShowWindow(handle, SW_SHOWNOACTIVATE);
    }

    public void ShowMaximized() {
        final int SW_SHOWMAXIMIZED = 3;

        logger.info("ShowMaximized, {}", this);
        _didManualHide = false;
        WinHelper.MyUser32Inst.ShowWindow(handle, SW_SHOWMAXIMIZED);
    }

    public void ShowMinimized() {
        final int SW_SHOWMINIMIZED = 2;

        logger.info("ShowMinimized, {}", this);
        _didManualHide = false;
        WinHelper.MyUser32Inst.ShowWindow(handle, SW_SHOWMINIMIZED);
    }

    public void ShowInCurrentState() {
        if (IsMinimized()) {
            ShowMinimized();
        } else if (IsMaximized()) {
            ShowMaximized();
        } else {
            ShowNormal();
        }
    }

    public void BringToTop() {
        WinHelper.MyUser32Inst.BringWindowToTop(handle);
    }

    public void Close() {
        final int WM_SYSCOMMAND = 0x0112;
        final int SC_CLOSE = 0xF060;

        logger.info("Close", this);
        WinHelper.MyUser32Inst.SendNotifyMessage(handle, WM_SYSCOMMAND, new WPARAM(SC_CLOSE), new LPARAM(0));
    }

    @Override
    public String toString() {
        return handle.toString() + '|' + windowTitle + '|' + windowClass + '|' + processName;
    }


    public static boolean IsAppWindow(HWND handle) {
        final int WS_EX_NOACTIVATE = 0x08000000;
        final int WS_CHILD = 0x40000000;

        return WinHelper.MyUser32Inst.IsWindowVisible(handle) && (GetWindowExStyleLongPtr(handle) & WS_EX_NOACTIVATE) == 0
                && (GetWindowStyleLongPtr(handle) & WS_CHILD) == 0;
    }

    // privates

    private boolean IsCloaked() {
        ByteByReference isCloaked = new ByteByReference();
        WinHelper.DWMApiInst.DwmGetWindowAttribute(handle, DWMApi.DWMWA_CLOAK, isCloaked, 1);
        return (isCloaked.getValue() != (byte) 0);
    }

    private static int GetWindowStyleLongPtr(HWND handle) {
        if (WinHelper.Is64Bit) {
            return WinHelper.MyUser32Inst.GetWindowLongPtr(handle, WinUser.GWL_STYLE).intValue();
        } else {
            return WinHelper.MyUser32Inst.GetWindowLong(handle, WinUser.GWL_STYLE);
        }
    }

    private static int GetWindowExStyleLongPtr(HWND handle) {
        if (WinHelper.Is64Bit) {
            return WinHelper.MyUser32Inst.GetWindowLongPtr(handle, WinUser.GWL_EXSTYLE).intValue();
        } else {
            return WinHelper.MyUser32Inst.GetWindowLong(handle, WinUser.GWL_EXSTYLE);
        }
    }

    private boolean IsAppWindow() {
        final int WS_EX_NOACTIVATE = 0x08000000;
        final int WS_CHILD = 0x40000000;

        return WinHelper.MyUser32Inst.IsWindowVisible(handle) && (GetWindowExStyleLongPtr(handle) & WS_EX_NOACTIVATE) == 0
                && (GetWindowStyleLongPtr(handle) & WS_CHILD) == 0;
    }

    private boolean IsAltTabWindow() {
        final int WS_EX_TOOLWINDOW = 0x0080;
        final int WS_EX_APPWINDOW = 0x40000;
        final int GW_OWNER = 4;

        int exStyle = GetWindowExStyleLongPtr(handle);
        if ((exStyle & WS_EX_TOOLWINDOW) != 0)
            return false;

        var h = WinHelper.MyUser32Inst.GetWindow(handle, GW_OWNER);
        if (h == null || Pointer.nativeValue(h.getPointer()) != 0)
            return false;

        if ((exStyle & WS_EX_APPWINDOW) != 0)
            return true;

        return true;
        // I am leaving this code here for testing purposes, but I don't think I need
        // it.
        // the old-school alt-tab implementation clearly doesn't 100% line up with the
        // aforementioned
        // blog post, or the below implementation in C# is wrong, because some windows
        // are hidden when
        // popups are created. For my purposes, I want to layout them anyway, so always
        // return true;
        /*
         * // Start at the root owner var hWndTry = Win32.GetAncestor(hWnd,
         * Win32.GA.GA_ROOTOWNER); IntPtr oldHWnd;
         * 
         * // See if we are the last active visible popup do { oldHWnd = hWndTry;
         * hWndTry = Win32.GetLastActivePopup(hWndTry); } while (oldHWnd != hWndTry &&
         * !Win32.IsWindowVisible(hWndTry));
         * 
         * return hWndTry == hWnd;
         */
    }

    // the handle is the only const val there, other should be mutable
    @Override
    public int hashCode() {
        return handle.hashCode();
    }
}
