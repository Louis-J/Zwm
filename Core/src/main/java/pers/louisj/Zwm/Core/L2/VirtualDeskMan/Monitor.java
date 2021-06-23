package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;

import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.Utils.Types.Point;
import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

public class Monitor {
    private static Map<HMONITOR, Monitor> monitorMap = new HashMap<>();

    private HMONITOR handle;
    private Rectangle aeraAll;
    private Rectangle aeraWorking;
    public VirtualDesk vd = null;
    private String name;

    public Monitor(HMONITOR handle) {
        this.handle = handle;
        Refresh();
    }

    public void Refresh() {
        MONITORINFOEX info = new MONITORINFOEX();
        WinHelper.MyUser32Inst.GetMonitorInfo(handle, info);
        aeraAll = new Rectangle(info.rcMonitor.left, info.rcMonitor.top,
                info.rcMonitor.right - info.rcMonitor.left,
                info.rcMonitor.bottom - info.rcMonitor.top);
        aeraWorking = new Rectangle(info.rcWork.left, info.rcWork.top,
                info.rcWork.right - info.rcWork.left, info.rcWork.bottom - info.rcWork.top);
        name = new String(info.szDevice);
    }

    public Rectangle GetWorkingRect() {
        return aeraWorking;
    }

    public boolean IsPointIn(int x, int y) {
        return x >= aeraAll.x && x <= (aeraAll.x + aeraAll.width) && y >= aeraAll.y
                && y <= (aeraAll.y + aeraAll.height);
    }

    @Override
    public String toString() {
        return name;
    }

    public static Set<Monitor> GetMonitors() {
        Map<HMONITOR, Monitor> newMap = new LinkedHashMap<>();

        WinHelper.MyUser32Inst.EnumDisplayMonitors(null, null, (hMonitor, hdc, rect, lparam) -> {
            var m = monitorMap.get(hMonitor);
            if (m != null) {
                m.Refresh();
            } else {
                m = new Monitor(hMonitor);
            }
            newMap.put(hMonitor, m);
            return 1;
        }, null);
        monitorMap = newMap;
        return new HashSet<Monitor>(newMap.values());
    }

    public static Monitor GetMonitorByHwnd(HWND hwnd) {
        var handle = WinHelper.MyUser32Inst.MonitorFromWindow(hwnd, WinUser.MONITOR_DEFAULTTONEAREST);
        return monitorMap.get(handle);
    }

    public static Monitor GetMonitorByWindow(Window window) {
        var areaCenter = window.Query.GetRect().Center();
        return GetMonitorByPoint(areaCenter.x, areaCenter.y);
    }

    public static Monitor GetMonitorByPoint(int x, int y) {
        int dis2 = 10000000;
        Monitor mindis = null;
        for (var m : monitorMap.values()) {
            var area = m.aeraAll;
            if (area.x <= x && area.y <= y && area.x + area.width >= x && area.y + area.height >= y)
                return m;
            var areaCenter = area.Center();
            var thisdis2 = (areaCenter.x - x) * (areaCenter.x - x)
                    + (areaCenter.y - y) * (areaCenter.y - y);
            if (thisdis2 < dis2)
                mindis = m;
        }
        return mindis;
    }
}