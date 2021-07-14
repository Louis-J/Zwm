package pers.louisj.Zwm.Core.L2.Window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.Utils.WinApi.DWMApi;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.Structure;

import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Win32Exception;

public class Window {
    private static Logger logger = LogManager.getLogger("Window");

    public HWND hWnd;
    public int processId;

    public String processName;
    public String windowClass;
    public String windowTitle;

    public boolean isLayout = true;

    private Rectangle rect;
    private Rectangle rectOff;
    private boolean canLayout;

    private byte state;
    private boolean isCloaked;
    private int winStyle;
    private int winStyleEx;

    private volatile int predictHideTime = 0;
    private volatile int predictFocusTime = 0;

    public ActionImpl Action = new ActionImpl();
    public RefreshImpl Refresh = new RefreshImpl();
    public QueryImpl Query = new QueryImpl();

    public class ActionImpl {
        public void Focus() {
            logger.info("Focus, {}", Window.this);
            predictFocusTime++;
            WinHelper.MyUser32Inst.SetForegroundWindow(hWnd);
        }

        public boolean Unfocus() {
            logger.info("Unfocus, {}", Window.this);
            final int GW_HWNDNEXT = 2;
            var hWndNext = WinHelper.MyUser32Inst.GetWindow(hWnd, GW_HWNDNEXT);
            if (hWndNext == null || hWndNext.getPointer() == null
                    || Pointer.nativeValue(hWndNext.getPointer()) == 0)
                return false;
            WinHelper.MyUser32Inst.SetForegroundWindow(hWndNext);
            return true;
        }

        public void Hide() {
            final int SW_HIDE = 0;
            logger.info("Hide, {}", Window.this);
            // this line should run before ShowWindow, or event hook will run prior and get a number
            // '0'
            predictHideTime++;
            WinHelper.MyUser32Inst.ShowWindow(hWnd, SW_HIDE);
        }

        public void ShowNormal() {
            final int SW_SHOWNOACTIVATE = 1;
            logger.info("ShowNormal, {}", Window.this);
            WinHelper.MyUser32Inst.ShowWindow(hWnd, SW_SHOWNOACTIVATE);
        }

        public void ShowMaximized() {
            final int SW_SHOWMAXIMIZED = 3;
            logger.info("ShowMaximized, {}", Window.this);
            WinHelper.MyUser32Inst.ShowWindow(hWnd, SW_SHOWMAXIMIZED);
        }

        public void ShowMinimized() {
            // final int SW_SHOWMINIMIZED = 2;
            // final int SW_MINIMIZE = 6;
            final int SW_SHOWMINNOACTIVE = 7;

            logger.info("ShowMinimized, {}", Window.this);
            WinHelper.MyUser32Inst.ShowWindow(hWnd, SW_SHOWMINNOACTIVE);
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

        public void ShowNoActive() {
            final int SW_SHOWNOACTIVATE = 4;

            logger.info("Show, {}", Window.this);
            WinHelper.MyUser32Inst.ShowWindow(hWnd, SW_SHOWNOACTIVATE);
        }

        public void BringToTop() {
            logger.info("BringToTop, {}", Window.this);
            WinHelper.MyUser32Inst.BringWindowToTop(hWnd);
        }

        public void SendClose() {
            final int WM_SYSCOMMAND = 0x0112;
            final int SC_CLOSE = 0xF060;
            logger.info("SendClose, {}", Window.this);
            WinHelper.MyUser32Inst.SendNotifyMessage(hWnd, WM_SYSCOMMAND, new WPARAM(SC_CLOSE),
                    new LPARAM(0));
        }

        public void SetLocation0(Rectangle rect) {
            if (rectOff == null) {
                Refresh.RefreshRect();
                Refresh.RefreshOffset();
            }
            final int flagNormal = WinUser.SWP_FRAMECHANGED | WinUser.SWP_NOACTIVATE
                    | WinUser.SWP_NOCOPYBITS | WinUser.SWP_NOZORDER | WinUser.SWP_NOOWNERZORDER;
            // final int flagMini = flagNormal | WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE;

            if (!WinHelper.MyUser32Inst.SetWindowPos(hWnd, null, rect.x + rectOff.x,
                    rect.y + rectOff.y, rect.width + rectOff.width, rect.height + rectOff.height,
                    flagNormal)) {
                var errCode = WinHelper.Kernel32Inst.GetLastError();
                if (errCode == 1400) // handle err, means the window closed, so ignore it
                    return;
                throw new Win32Exception(errCode);
            }
        }

        @Structure.FieldOrder({"cbSize", "rcMonitor", "rcWork", "dwFlags", "szDevice"})
        public class WINDOWPOS extends Structure {
            public HWND hwnd;
            public HWND hwndInsertAfter;
            public int x;
            public int y;
            public int cx;
            public int cy;
            public int flags;
        }

        public void SetLocation1(Rectangle rect) {
            if (rectOff == null) {
                Refresh.RefreshRect();
                Refresh.RefreshOffset();
            }
            final int flagNormal = WinUser.SWP_FRAMECHANGED | WinUser.SWP_NOACTIVATE
                    | WinUser.SWP_NOCOPYBITS | WinUser.SWP_NOZORDER | WinUser.SWP_NOOWNERZORDER;
            // final int flagMini = flagNormal | WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE;

            final int WM_WINDOWPOSCHANGED = 0x0047;
            WINDOWPOS winpos = new WINDOWPOS();
            winpos.hwnd = hWnd;
            winpos.hwndInsertAfter = null;
            winpos.flags = flagNormal;

            winpos.x = rect.x + rectOff.x;
            winpos.y = rect.y + rectOff.y;
            winpos.cx = rect.width + rectOff.width;
            winpos.cy = rect.height + rectOff.height;

            if (!WinHelper.MyUser32Inst.PostMessage(hWnd, WM_WINDOWPOSCHANGED, null, winpos)) {
                var errCode = WinHelper.Kernel32Inst.GetLastError();
                if (errCode == 1400) // handle err, means the window closed, so ignore it
                    return;
                throw new Win32Exception(errCode);
            }
        }

        public void DecorateEnable() {
            final int WS_CAPTION = 0x00C00000;
            // final int WS_THICKFRAME = 0x00040000;

            // int newWinStyle = winStyle & ~WS_CAPTION & ~WS_THICKFRAME;
            int newWinStyle = winStyle & ~WS_CAPTION;
            // int newWinStyle = winStyle & ~WS_THICKFRAME;
            boolean isFail;
            if (WinHelper.Is64Bit) {
                var ret = WinHelper.MyUser32Inst.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE,
                        new Pointer(newWinStyle));
                isFail = ret == null || Pointer.nativeValue(ret) == 0;
            } else {
                var ret =
                        WinHelper.MyUser32Inst.SetWindowLong(hWnd, WinUser.GWL_STYLE, newWinStyle);
                isFail = ret == 0;
            }
            if (isFail) {
                var errCode = WinHelper.Kernel32Inst.GetLastError();
                if (errCode == 1400) // handle err, means the window closed, so ignore it
                    return;
                throw new Win32Exception(errCode);
            }
            Refresh.RefreshRect();
            Refresh.RefreshOffset();
        }

        public void DecorateDisable() {
            boolean isFail;
            if (WinHelper.Is64Bit) {
                var ret = WinHelper.MyUser32Inst.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE,
                        new Pointer(winStyle));
                isFail = ret == null || Pointer.nativeValue(ret) == 0;
            } else {
                var ret = WinHelper.MyUser32Inst.SetWindowLong(hWnd, WinUser.GWL_STYLE, winStyle);
                isFail = ret == 0;
            }
            if (isFail) {
                var errCode = WinHelper.Kernel32Inst.GetLastError();
                if (errCode == 1400) // handle err, means the window closed, so ignore it
                    return;
                throw new Win32Exception(errCode);
            }
            Refresh.RefreshRect();
            Refresh.RefreshOffset();
        }

        public void SetCanLayout(boolean canLayout) {
            Window.this.canLayout &= canLayout;
        }

        public boolean IsPredictHide() {
            return (--predictHideTime) >= 0;
        }

        public boolean IsPredictFocus() {
            return (--predictFocusTime) >= 0;
        }
    }

    public class RefreshImpl {
        public void RefreshTitle() {
            windowTitle = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
                @Override
                public int Invoke(char[] buffer, int size) {
                    return WinHelper.MyUser32Inst.GetWindowText(hWnd, buffer, size);
                }
            }, 128);
        }

        public void RefreshRect() {
            RECT crect = new RECT();
            WinHelper.MyUser32Inst.GetWindowRect(hWnd, crect);
            rect = new Rectangle(crect.left, crect.top, crect.right - crect.left,
                    crect.bottom - crect.top);
        }

        public void RefreshRect(Rectangle rect) {
            // Window.this.rect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
            // RefreshRect();
        }

        public void RefreshState() {
            if (WinHelper.MyUser32Inst.IsIconic(hWnd))
                state = 1;
            else if (WinHelper.MyUser32Inst.IsZoomed(hWnd))
                state = 2;
            else
                state = 0;
        }

        public void RefreshOffset() {
            if (Query.IsMinimized())
                return;
            // get offset between Window Rect via DwmGetWindowAttribute and Window Rect via
            // the offset is the size of shadow
            RECT crect = new RECT();
            WinHelper.DWMApiInst.DwmGetWindowAttribute(hWnd, DWMApi.DWMWA_EXTENDED_FRAME_BOUNDS,
                    crect, crect.size());
            rectOff = new Rectangle(rect.x - crect.left, rect.y - crect.top,
                    rect.width - (crect.right - crect.left),
                    rect.height - (crect.bottom - crect.top));
        }

        public void RefreshCanLayout() {
            canLayout = !Query.IsCloaked();
        }

        public void RefreshWindowStyles() {
            if (WinHelper.Is64Bit) {
                winStyle =
                        WinHelper.MyUser32Inst.GetWindowLongPtr(hWnd, WinUser.GWL_STYLE).intValue();
                winStyleEx = WinHelper.MyUser32Inst.GetWindowLongPtr(hWnd, WinUser.GWL_EXSTYLE)
                        .intValue();
            } else {
                winStyle = WinHelper.MyUser32Inst.GetWindowLong(hWnd, WinUser.GWL_STYLE);
                winStyleEx = WinHelper.MyUser32Inst.GetWindowLong(hWnd, WinUser.GWL_EXSTYLE);
            }
        }

        public void RefreshIsCloaked() {
            ByteByReference refIsCloaked = new ByteByReference();
            WinHelper.DWMApiInst.DwmGetWindowAttribute(hWnd, DWMApi.DWMWA_CLOAK, refIsCloaked, 1);
            isCloaked = (refIsCloaked.getValue() != (byte) 0);
        }
    }

    public class QueryImpl {
        public boolean IsFocused() {
            return hWnd.equals(WinHelper.MyUser32Inst.GetForegroundWindow());
        }

        public boolean IsCloaked() {
            return isCloaked;
        }

        public boolean IsMinimized() {
            return state == 1;
        }

        public boolean IsMaximized() {
            return state == 2;
        }

        public boolean CanLayout() {
            return canLayout;
        }

        public boolean CanDecorate() {
            // return canLayout;
            return true;
        }

        public Rectangle GetOffset() {
            return rectOff;
        }

        public Rectangle GetRect() {
            return rect;
        }

        public int GetWindowStyle() {
            return winStyle;
        }

        public int GetWindowStyleEx() {
            return winStyleEx;
        }
    }

    public static class QueryStatic {
        public static int GetWindowPid(HWND handle) {
            IntByReference pProcessId = new IntByReference();
            WinHelper.MyUser32Inst.GetWindowThreadProcessId(handle, pProcessId);
            return pProcessId.getValue();
        }

        public static boolean IsAppWindow(HWND handle) {
            final int WS_CHILD = 0x40000000;
            final int WS_EX_NOACTIVATE = 0x08000000;
            final int WS_EX_TOOLWINDOW = 0x0080;
            final int WS_EX_APPWINDOW = 0x40000;
            final int GW_OWNER = 4;

            int styleEx = GetWindowExStyleLongPtr(handle);
            int style = GetWindowStyleLongPtr(handle);
            var owner = WinHelper.MyUser32Inst.GetWindow(handle, GW_OWNER);
            return WinHelper.MyUser32Inst.IsWindowVisible(handle)
                    && (owner == null || Pointer.nativeValue(owner.getPointer()) == 0)
                    && (styleEx & WS_EX_NOACTIVATE) == 0 && (style & WS_CHILD) == 0
                    && ((styleEx & WS_EX_TOOLWINDOW) == 0 || (styleEx & WS_EX_APPWINDOW) != 0);
            // var b1 = WinHelper.MyUser32Inst.IsWindowVisible(handle);
            // var b2 = (owner == null || Pointer.nativeValue(owner.getPointer()) == 0);
            // var b3 = ((styleEx & WS_EX_NOACTIVATE) == 0 && (style & WS_CHILD) == 0);
            // var b4 = (styleEx & WS_EX_TOOLWINDOW) == 0 || (styleEx & WS_EX_APPWINDOW) !=
            // 0;

            // int intV = (b1 ? 10000 : 0) + (b2 ? 1000 : 0) + (b3 ? 100 : 0) + (b4 ? 10 :
            // 0);
            // logger.info("IsAppWindow static, {}, {}", handle, intV);
            // return intV == 11110;
        }

        private static int GetWindowStyleLongPtr(HWND handle) {
            if (WinHelper.Is64Bit) {
                return WinHelper.MyUser32Inst.GetWindowLongPtr(handle, WinUser.GWL_STYLE)
                        .intValue();
            } else {
                return WinHelper.MyUser32Inst.GetWindowLong(handle, WinUser.GWL_STYLE);
            }
        }

        private static int GetWindowExStyleLongPtr(HWND handle) {
            if (WinHelper.Is64Bit) {
                return WinHelper.MyUser32Inst.GetWindowLongPtr(handle, WinUser.GWL_EXSTYLE)
                        .intValue();
            } else {
                return WinHelper.MyUser32Inst.GetWindowLong(handle, WinUser.GWL_EXSTYLE);
            }
        }
    }

    public Window(HWND handle, int processId) {
        logger.info("Window, handle = {}", Pointer.nativeValue(handle.getPointer()));
        this.hWnd = handle;
        this.processId = processId;

        logger.info("Window, processId = {}", processId);
        try {
            final int PROCESS_QUERY_INFORMATION = 0x0400;
            final int PROCESS_VM_READ = 0x0010;
            HANDLE hProc = WinHelper.Kernel32Inst
                    .OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, processId);
            processName = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
                @Override
                public int Invoke(char[] buffer, int size) {
                    return WinHelper.PsapiInst.GetModuleFileNameExW(hProc, null, buffer, size);
                }
            }, WinDef.MAX_PATH);
            WinHelper.Kernel32Inst.CloseHandle(hProc);
        } catch (Win32Exception e) {
            processName = "";
        }

        windowClass = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
            @Override
            public int Invoke(char[] buffer, int size) {
                return WinHelper.MyUser32Inst.GetClassName(handle, buffer, size);
            }
        }, 128);

        Refresh.RefreshTitle();

        Refresh.RefreshState();

        Refresh.RefreshWindowStyles();

        Refresh.RefreshRect();
        Refresh.RefreshOffset();

        Refresh.RefreshIsCloaked();
        Refresh.RefreshCanLayout();
    }

    public Window(HWND handle) {
        this.hWnd = handle;
        processId = Window.QueryStatic.GetWindowPid(handle);;

        logger.info("Window New, handle = {}, processId = {}",
                Pointer.nativeValue(handle.getPointer()), processId);
        try {
            final int PROCESS_QUERY_INFORMATION = 0x0400;
            final int PROCESS_VM_READ = 0x0010;
            HANDLE hProc = WinHelper.Kernel32Inst
                    .OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, processId);
            processName = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
                @Override
                public int Invoke(char[] buffer, int size) {
                    return WinHelper.PsapiInst.GetModuleFileNameExW(hProc, null, buffer, size);
                }
            }, WinDef.MAX_PATH);
            WinHelper.Kernel32Inst.CloseHandle(hProc);
        } catch (Win32Exception e) {
            processName = "";
        }

        windowClass = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
            @Override
            public int Invoke(char[] buffer, int size) {
                return WinHelper.MyUser32Inst.GetClassName(handle, buffer, size);
            }
        }, 128);

        Refresh.RefreshTitle();

        Refresh.RefreshState();

        Refresh.RefreshWindowStyles();

        Refresh.RefreshRect();
        Refresh.RefreshOffset();

        Refresh.RefreshIsCloaked();
        Refresh.RefreshCanLayout();
    }

    @Override
    public String toString() {
        return "" + Pointer.nativeValue(hWnd.getPointer()) + '|' + processId + '|' + windowTitle
                + '|' + windowClass + '|' + processName;
    }

    // the handle is the only const val there, other should be mutable
    @Override
    public int hashCode() {
        return hWnd.hashCode();
    }
}
