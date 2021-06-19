package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;

import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;

// import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
// import com.sun.jna.platform.win32.WinDef.LPARAM;
// import com.sun.jna.platform.win32.WinDef.RECT;
// import com.sun.jna.platform.win32.WinDef.HDC;

import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

public class Monitor {
    private static Map<HMONITOR, Monitor> monitorMap = new HashMap<>();

    private HMONITOR handle;
    private Rectangle aeraAll;
    private Rectangle aeraWorking;
    public VirtualDesk vd = null;

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
        Map<HMONITOR, Monitor> newMap = new HashMap<>();

        WinHelper.MyUser32Inst.EnumDisplayMonitors(null, null, (hMonitor, hdc, rect, lparam) -> {
            var m = monitorMap.get(hMonitor);
            if (m != null) {
                m.Refresh();
            } else {
                m = new Monitor(hMonitor);
            }
            monitors.add(m);
            newMap.put(hMonitor, m);
            return 1;
        }, null);
        monitorMap = newMap;
        return monitors;
    }

    public static Monitor GetMonitorByHwnd(HWND hwnd) {
        var handle = WinHelper.MyUser32Inst.MonitorFromWindow(hwnd, WinUser.MONITOR_DEFAULTTONEAREST);
        return monitorMap.get(handle);
    }
}