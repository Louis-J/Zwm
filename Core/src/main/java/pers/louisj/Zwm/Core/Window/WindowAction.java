package pers.louisj.Zwm.Core.Window;

import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.WinApi.WinHelper;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Win32Exception;

public abstract class WindowAction {
    private static final int flagNormal = WinUser.SWP_FRAMECHANGED | WinUser.SWP_NOACTIVATE | WinUser.SWP_NOCOPYBITS
            | WinUser.SWP_NOZORDER | WinUser.SWP_NOOWNERZORDER;
    private static final int flagMini = flagNormal | WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE;

    public static class HDWP4SetLoc extends PointerType {
        HDWP4SetLoc(Pointer p) {
            super(p);
        }

        public static HDWP4SetLoc BeginSetLocation(int num) {
            return new HDWP4SetLoc(WinHelper.MyUser32Inst.BeginDeferWindowPos(num));
        }

        public void SetLocation(Window window, Rectangle location) {
            // var offset = window.Offset;
            // int X = location.X + offset.X;
            // int Y = location.Y + offset.Y;
            // int Width = location.Width + offset.Width;
            // int Height = location.Height + offset.Height;
            var ret = WinHelper.MyUser32Inst.DeferWindowPos(getPointer(), window.handle, null, location.x, location.y,
                    location.width, location.height, flagNormal);
            if (ret == null || Pointer.nativeValue(ret) == 0) {
                var errCode = WinHelper.Kernel32Inst.GetLastError();
                if (errCode == 1400) // handle err, means the window closed, so ignore it
                    return;
                throw new Win32Exception(errCode);
            }
            setPointer(ret);
        }

        public void EndSetLocation() {
            WinHelper.MyUser32Inst.EndDeferWindowPos(getPointer());
        }
    }

    public static void SetLocation2(Window window, Rectangle location) {
        if (!WinHelper.MyUser32Inst.SetWindowPos(window.handle, null, location.x, location.y, location.width,
                location.height, flagNormal)) {
            var errCode = WinHelper.Kernel32Inst.GetLastError();
            if (errCode == 1400) // handle err, means the window closed, so ignore it
                return;
            throw new Win32Exception(errCode);
        }
    }
}
