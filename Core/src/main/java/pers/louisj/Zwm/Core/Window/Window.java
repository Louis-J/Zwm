package pers.louisj.Zwm.Core.Window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.WinApi.DWMApi;
import pers.louisj.Zwm.Core.WinApi.MyUser32;
import pers.louisj.Zwm.Core.WinApi.WinHelper;

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

    public HWND handle;
    public int processId;

    public String processName;
    public String windowClass;
    public String windowTitle;

    // private WindowLocation location;
    private Rectangle rect;
    private Rectangle offset;
    private boolean canLayout;

    private byte state;
    private boolean isLayout = true;
    private boolean isCloaked;
    private int winStyle;
    private int winStyleEx;

    public static int GetWindowPid(HWND handle) {
        IntByReference pProcessId = new IntByReference();
        WinHelper.MyUser32Inst.GetWindowThreadProcessId(handle, pProcessId);
        return pProcessId.getValue();
    }

    public ActionImpl Action = new ActionImpl();
    public RefreshImpl Refresh = new RefreshImpl();
    public QueryImpl Query = new QueryImpl();

    public class ActionImpl {
        public void Focus() {
            logger.info("Focus, {}", this);
            WinHelper.MyUser32Inst.SetForegroundWindow(handle);
            Refresh.RefreshState();
        }

        public void Hide() {
            final int SW_HIDE = 0;
            logger.info("Hide, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(handle, SW_HIDE);
            Refresh.RefreshState();
        }

        public void ShowNormal() {
            final int SW_SHOWNOACTIVATE = 4;
            logger.info("ShowNormal, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(handle, SW_SHOWNOACTIVATE);
            Refresh.RefreshState();
        }

        public void ShowMaximized() {
            final int SW_SHOWMAXIMIZED = 3;
            logger.info("ShowMaximized, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(handle, SW_SHOWMAXIMIZED);
            Refresh.RefreshState();
        }

        public void ShowMinimized() {
            final int SW_SHOWMINIMIZED = 2;
            logger.info("ShowMinimized, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(handle, SW_SHOWMINIMIZED);
            Refresh.RefreshState();
        }

        public void ShowInCurrentState() {
            if (Query.IsMinimized()) {
                ShowMinimized();
            } else if (Query.IsMaximized()) {
                ShowMaximized();
            } else {
                ShowNormal();
            }
        }

        public void BringToTop() {
            logger.info("BringToTop, {}", this);
            WinHelper.MyUser32Inst.BringWindowToTop(handle);
        }

        public void SendClose() {
            final int WM_SYSCOMMAND = 0x0112;
            final int SC_CLOSE = 0xF060;
            logger.info("SendClose, {}", this);
            WinHelper.MyUser32Inst.SendNotifyMessage(handle, WM_SYSCOMMAND, new WPARAM(SC_CLOSE), new LPARAM(0));
        }

        // TODO:
        public void SetLocation(Rectangle rect) {
        }
    }

    public class RefreshImpl {
        public void RefreshTitle() {
            windowTitle = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
                @Override
                public int Invoke(char[] buffer, int size) {
                    return WinHelper.MyUser32Inst.GetWindowText(handle, buffer, size);
                }
            }, 128);
        }

        public void RefreshRectangle() {
            RECT crect = new RECT();
            WinHelper.MyUser32Inst.GetWindowRect(handle, crect);
            rect = new Rectangle(crect.left, crect.top, crect.right - crect.left, crect.bottom - crect.top);
        }

        public void RefreshState() {
            if (WinHelper.MyUser32Inst.IsIconic(handle))
                state = 1;
            else if (WinHelper.MyUser32Inst.IsZoomed(handle))
                state = 2;
            else
                state = 0;
            if (WinHelper.MyUser32Inst.GetForegroundWindow() == handle)
                state |= 4;
        }

        // TODO:
        public void RefreshOffset() {
        }

        public void RefreshCanLayout() {
            canLayout = (!IsCloaked() && IsAppWindow() && IsAltTabWindow());
        }

        public void RefreshWindowStyles() {
            if (WinHelper.Is64Bit) {
                winStyle = WinHelper.MyUser32Inst.GetWindowLongPtr(handle, WinUser.GWL_STYLE).intValue();
                winStyleEx = WinHelper.MyUser32Inst.GetWindowLongPtr(handle, WinUser.GWL_EXSTYLE).intValue();
            } else {
                winStyle = WinHelper.MyUser32Inst.GetWindowLong(handle, WinUser.GWL_STYLE);
                winStyleEx = WinHelper.MyUser32Inst.GetWindowLong(handle, WinUser.GWL_EXSTYLE);
            }
        }

        // TODO:
        public void RefreshIsAppWindow() {
        }

        public void RefreshIsCloaked() {
            ByteByReference refIsCloaked = new ByteByReference();
            WinHelper.DWMApiInst.DwmGetWindowAttribute(handle, DWMApi.DWMWA_CLOAK, refIsCloaked, 1);
            isCloaked = (refIsCloaked.getValue() != (byte) 0);
        }
    }

    // TODO:
    public class QueryImpl {
        public boolean IsFocused() {
            return (state & 0x4) != 0;
        }

        public boolean IsMinimized() {
            return (state & 0x3) == 1;
        }

        public boolean IsMaximized() {
            return (state & 0x3) == 2;
        }

        public boolean CanLayout() {
            return canLayout;
        }
    }

    public static class QueryStatic {
        // TODO:
        public static int GetWindowPid(HWND handle) {
            return 0;
        }

        private static boolean IsAppWindow(HWND handle) {
            final int WS_EX_NOACTIVATE = 0x08000000;
            final int WS_CHILD = 0x40000000;

            return WinHelper.MyUser32Inst.IsWindowVisible(handle)
                    && (GetWindowExStyleLongPtr(handle) & WS_EX_NOACTIVATE) == 0
                    && (GetWindowStyleLongPtr(handle) & WS_CHILD) == 0;
        }
    }

    public Window(HWND handle, int processId) {
        logger.info("Window, handle = {}", handle);
        this.handle = handle;
        this.processId = processId;

        logger.info("Window, processId = {}", processId);
        try {
            processName = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
                @Override
                public int Invoke(char[] buffer, int size) {
                    return WinHelper.MyUser32Inst.GetWindowModuleFileName(handle, buffer, size);
                }
            }, WinDef.MAX_PATH);
        } catch (Win32Exception e) {
            processName = "--NA--";
        }

        windowClass = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
            @Override
            public int Invoke(char[] buffer, int size) {
                return WinHelper.MyUser32Inst.GetClassName(handle, buffer, size);
            }
        }, 128);

        Refresh.RefreshTitle();
        Refresh.RefreshRectangle();
        Refresh.RefreshState();
        Refresh.RefreshCanLayout();
    }

    // public Rectangle GetOffset() {
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
    // return null;
    // }

    public boolean IsLayout() {
        return isLayout;
    }

    public void BringToTop() {
        WinHelper.MyUser32Inst.BringWindowToTop(handle);
    }

    @Override
    public String toString() {
        return handle.toString() + '|' + windowTitle + '|' + windowClass + '|' + processName;
    }

    public static boolean IsAppWindow(HWND handle) {
        final int WS_EX_NOACTIVATE = 0x08000000;
        final int WS_CHILD = 0x40000000;

        return WinHelper.MyUser32Inst.IsWindowVisible(handle)
                && (GetWindowExStyleLongPtr(handle) & WS_EX_NOACTIVATE) == 0
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

        return WinHelper.MyUser32Inst.IsWindowVisible(handle)
                && (GetWindowExStyleLongPtr(handle) & WS_EX_NOACTIVATE) == 0
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
        if (h != null && Pointer.nativeValue(h.getPointer()) != 0)
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
