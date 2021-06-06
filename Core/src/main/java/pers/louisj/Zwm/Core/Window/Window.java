package pers.louisj.Zwm.Core.Window;

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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;

import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Win32Exception;

public class Window {
    private static Logger logger = LogManager.getLogger("Window");

    public HWND hwnd;
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
    // private int winStyle;
    // private int winStyleEx;

    public ActionImpl Action = new ActionImpl();
    public RefreshImpl Refresh = new RefreshImpl();
    public QueryImpl Query = new QueryImpl();

    public class ActionImpl {
        public void Focus() {
            logger.info("Focus, {}", this);
            WinHelper.MyUser32Inst.SetForegroundWindow(hwnd);
            Refresh.RefreshState();
        }

        public void Hide() {
            final int SW_HIDE = 0;
            logger.info("Hide, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(hwnd, SW_HIDE);
            Refresh.RefreshState();
        }

        public void ShowNormal() {
            final int SW_SHOWNOACTIVATE = 4;
            logger.info("ShowNormal, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(hwnd, SW_SHOWNOACTIVATE);
            Refresh.RefreshState();
        }

        public void ShowMaximized() {
            final int SW_SHOWMAXIMIZED = 3;
            logger.info("ShowMaximized, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(hwnd, SW_SHOWMAXIMIZED);
            Refresh.RefreshState();
        }

        public void ShowMinimized() {
            // final int SW_SHOWMINIMIZED = 2;
            final int SW_MINIMIZE = 6;

            logger.info("ShowMinimized, {}", this);
            WinHelper.MyUser32Inst.ShowWindow(hwnd, SW_MINIMIZE);
            // Refresh.RefreshState();
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
            WinHelper.MyUser32Inst.BringWindowToTop(hwnd);
        }

        public void SendClose() {
            final int WM_SYSCOMMAND = 0x0112;
            final int SC_CLOSE = 0xF060;
            logger.info("SendClose, {}", this);
            WinHelper.MyUser32Inst.SendNotifyMessage(hwnd, WM_SYSCOMMAND, new WPARAM(SC_CLOSE), new LPARAM(0));
        }

        public void SetLocation1(Rectangle rect) {
            if (rectOff == null) {
                Refresh.RefreshRectangle();
                Refresh.RefreshOffset();
            }
            final int flagNormal = WinUser.SWP_FRAMECHANGED | WinUser.SWP_NOACTIVATE | WinUser.SWP_NOCOPYBITS
                    | WinUser.SWP_NOZORDER | WinUser.SWP_NOOWNERZORDER;
            // final int flagMini = flagNormal | WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE;

            if (!WinHelper.MyUser32Inst.SetWindowPos(hwnd, null, rect.x + rectOff.x, rect.y + rectOff.y,
                    rect.width + rectOff.width, rect.height + rectOff.height, flagNormal)) {
                var errCode = WinHelper.Kernel32Inst.GetLastError();
                if (errCode == 1400) // handle err, means the window closed, so ignore it
                    return;
                throw new Win32Exception(errCode);
            }
        }
    }

    public class RefreshImpl {
        public void RefreshTitle() {
            windowTitle = WinHelper.QueryWithBuffer1(new WinHelper.CallBackWithBuffer1() {
                @Override
                public int Invoke(char[] buffer, int size) {
                    return WinHelper.MyUser32Inst.GetWindowText(hwnd, buffer, size);
                }
            }, 128);
        }

        public void RefreshRectangle() {
            RECT crect = new RECT();
            WinHelper.MyUser32Inst.GetWindowRect(hwnd, crect);
            rect = new Rectangle(crect.left, crect.top, crect.right - crect.left, crect.bottom - crect.top);
        }

        public void RefreshState() {
            if (WinHelper.MyUser32Inst.IsIconic(hwnd))
                state = 1;
            else if (WinHelper.MyUser32Inst.IsZoomed(hwnd))
                state = 2;
            else
                state = 0;
            if (WinHelper.MyUser32Inst.GetForegroundWindow() == hwnd)
                state |= 4;
        }

        public void RefreshOffset() {
            if (Query.IsMinimized())
                return;
            // get offset between Window Rect via DwmGetWindowAttribute and Window Rect via
            // the offset is the size of shadow
            RECT crect = new RECT();
            WinHelper.DWMApiInst.DwmGetWindowAttribute(hwnd, DWMApi.DWMWA_EXTENDED_FRAME_BOUNDS, crect, crect.size());
            rectOff = new Rectangle(rect.x - crect.left, rect.y - crect.top, rect.width - (crect.right - crect.left),
                    rect.height - (crect.bottom - crect.top));
        }

        public void RefreshCanLayout() {
            canLayout = !Query.IsCloaked();
        }

        // public void RefreshWindowStyles() {
        // if (WinHelper.Is64Bit) {
        // winStyle = WinHelper.MyUser32Inst.GetWindowLongPtr(handle,
        // WinUser.GWL_STYLE).intValue();
        // winStyleEx = WinHelper.MyUser32Inst.GetWindowLongPtr(handle,
        // WinUser.GWL_EXSTYLE).intValue();
        // } else {
        // winStyle = WinHelper.MyUser32Inst.GetWindowLong(handle, WinUser.GWL_STYLE);
        // winStyleEx = WinHelper.MyUser32Inst.GetWindowLong(handle,
        // WinUser.GWL_EXSTYLE);
        // }
        // }

        public void RefreshIsCloaked() {
            ByteByReference refIsCloaked = new ByteByReference();
            WinHelper.DWMApiInst.DwmGetWindowAttribute(hwnd, DWMApi.DWMWA_CLOAK, refIsCloaked, 1);
            isCloaked = (refIsCloaked.getValue() != (byte) 0);
        }
    }

    // TODO:
    public class QueryImpl {
        public boolean IsFocused() {
            return (state & 0x4) != 0;
        }

        private boolean IsCloaked() {
            return isCloaked;
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
    }

    public Window(HWND handle, int processId) {
        logger.info("Window, handle = {}", handle);
        this.hwnd = handle;
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

        Refresh.RefreshState();

        // Refresh.RefreshWindowStyles();

        Refresh.RefreshRectangle();
        Refresh.RefreshOffset();

        Refresh.RefreshIsCloaked();
        Refresh.RefreshCanLayout();
    }

    @Override
    public String toString() {
        return hwnd.toString() + '|' + windowTitle + '|' + windowClass + '|' + processName;
    }

    // the handle is the only const val there, other should be mutable
    @Override
    public int hashCode() {
        return hwnd.hashCode();
    }
}
