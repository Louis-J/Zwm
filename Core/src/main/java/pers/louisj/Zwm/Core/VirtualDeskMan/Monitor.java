package pers.louisj.Zwm.Core.VirtualDeskMan;

import java.util.ArrayList;

import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;

// import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
// import com.sun.jna.platform.win32.WinDef.LPARAM;
// import com.sun.jna.platform.win32.WinDef.RECT;
// import com.sun.jna.platform.win32.WinDef.HDC;

import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
// import pers.louisj.Zwm.Core.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.WinApi.WinHelper;

public class Monitor {
    private HMONITOR handle;
    private Rectangle aeraAll;
    private Rectangle aeraWorking;
    // private VirtualDesk vd;

    public Monitor(HMONITOR handle) {
        this.handle = handle;
        Refresh();
    }

    public void Refresh() {
        MONITORINFOEX info = new MONITORINFOEX();
        WinHelper.MyUser32Inst.GetMonitorInfo(handle, info);
        aeraAll = new Rectangle(info.rcMonitor.left, info.rcMonitor.top, info.rcMonitor.right - info.rcMonitor.left,
                info.rcMonitor.bottom - info.rcMonitor.top);
        aeraWorking = new Rectangle(info.rcWork.left, info.rcWork.top, info.rcWork.right - info.rcWork.left,
                info.rcWork.bottom - info.rcWork.top);
    }

    public Rectangle GetWorkingRect() {
        return aeraWorking;
    }

    public boolean IsPointIn(int x, int y) {
        return x >= aeraAll.x && x <= (aeraAll.x + aeraAll.width) && y >= aeraAll.y
                && y <= (aeraAll.y + aeraAll.height);
    }

    public static ArrayList<Monitor> GetMonitors() {
        ArrayList<Monitor> monitors = new ArrayList<>();

        WinHelper.MyUser32Inst.EnumDisplayMonitors(null, null, (hMonitor, hdc, rect, lparam) -> {
            monitors.add(new Monitor(hMonitor));
            return 1;
        }, null);
        return monitors;
    }
}