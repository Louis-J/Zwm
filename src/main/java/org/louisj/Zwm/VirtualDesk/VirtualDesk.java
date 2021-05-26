package org.louisj.Zwm.VirtualDesk;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.louisj.Zwm.Context;
import org.louisj.Zwm.Derived.ILayout;
import org.louisj.Zwm.Window.Window;

public class VirtualDesk {
    private static Logger logger = LogManager.getLogger("VirtualDesk");

    // public Collection<Window> allWindows;
    // public Collection<Window> layoutWindows;

    public String name;
    public String layoutName;

    public boolean isIndicating;

    private Context context;
    public Collection<Window> allWindows;
    public Collection<Window> layoutWindows;
    private ILayout[] layouts;
    private int layoutIndex = 0;

    public Collection<Window> GetAllWindows() {
        synchronized (allWindows) {
            LinkedHashSet<Window> copy = new LinkedHashSet<Window>();
            copy.addAll(allWindows);
            return copy;
        }
    }

    public synchronized Collection<Window> GetLayoutWindows() {
        synchronized (layoutWindows) {
            LinkedHashSet<Window> copy = new LinkedHashSet<Window>();
            copy.addAll(layoutWindows);
            return copy;
        }
    }

    public Window GetFocusedWindow() {
        return allWindows.iterator().next();
    }

    // public Window GetLastFocusedWindow() {
    // return allWindows.iterator().next();
    // }

    public VirtualDesk(Context context, String name, ILayout[] layouts) {
        this.context = context;
        this.name = name;
        this.layouts = layouts;
        // this.windows = new List<Window>(); //TODO:
    }

    public void AddWindow(Window window) {
        synchronized (allWindows) {
            allWindows.add(window);
        }
        // if (layout)
        // synchronized (layoutWindows) {
        // layoutWindows.add(window);
        // DoLayout();
        // }
    }

    public void RemoveWindow(Window window) {
        synchronized (allWindows) {
            allWindows.remove(window);
        }
        // if (layout)
        // synchronized (layoutWindows) {
        // if (layoutWindows.remove(window))
        // DoLayout();
        // }

    }

    public void UpdateWindow(Window window, byte updateType) {
        // if (type == WindowUpdateType.Foreground)
        // _lastFocused = window;

        // if (layout)
        // synchronized (layoutWindows) {
        // if (layoutWindows.contains(window))
        // DoLayout();
        // }
    }

    // public void NextLayoutEngine() {
    // layoutIndex = (layoutIndex + 1) % layouts.length;
    // DoLayout();
    // }

    // public void ResetLayout() {
    // // GetLayoutEngine().ResetPrimaryArea();
    // DoLayout();
    // }

    // public void SwapWindowToPoint(Window window, int x, int y) {
    // if (allWindows.contains(window)) {
    // var swapWindow = GetLayoutSlotWindowForPoint(x, y);
    // if (swapWindow != null && window != swapWindow) {
    // logger.info("SwapWindowToPoint[{0},{1} - {2}]", x, y, window);
    // SwapWindows(window, swapWindow);
    // }
    // }
    // }

    // public boolean IsPointInside(int x, int y)
    // {
    // var monitor =
    // _context.WorkspaceContainer.GetCurrentMonitorForWorkspace(this);

    // if (monitor != null)
    // {
    // return monitor.X <= x && x <= (monitor.X + monitor.Width) && monitor.Y <= y
    // && y <= (monitor.Y + monitor.Height);
    // } else
    // {
    // return false;
    // }
    // }

    // private int GetLayoutSlotIndexForPoint(int x, int y)
    // {
    // var locations = CalcLayout();
    // if (locations == null)
    // return -1;
    // var monitor =
    // _context.WorkspaceContainer.GetCurrentMonitorForWorkspace(this);
    // if (monitor == null)
    // return -1;

    // var adjustedLocations = locations.Select(loc => new WindowLocation(loc.X +
    // monitor.X, loc.Y + monitor.Y,
    // loc.Width, loc.Height, loc.State)).ToList();

    // var firstFit = adjustedLocations.FindIndex(l => l.IsPointInside(x, y));
    // return firstFit;
    // }

    // private Collection<WindowLocation> CalcLayout() {
    // var windows = ManagedWindows;
    // var monitor =
    // _context.WorkspaceContainer.GetCurrentMonitorForWorkspace(this);
    // if (monitor != null) {
    // return GetLayoutEngine().CalcLayout(windows, monitor.Width, monitor.Height);
    // }
    // return null;
    // }

    // public void DoLayout()
    // {
    // var windows = ManagedWindows.ToList();
    // if (_context.Enabled)
    // {
    // var monitor =
    // _context.WorkspaceContainer.GetCurrentMonitorForWorkspace(this);
    // if (monitor != null)
    // {
    // windows.ForEach(w => w.ShowInCurrentState());

    // var locations = GetLayoutEngine().CalcLayout(windows, monitor.Width,
    // monitor.Height)
    // .ToArray();

    // using (var handle = _context.Windows.DeferWindowsPos(windows.Count))
    // {
    // for (var i = 0; i < locations.Length; i++)
    // {
    // var window = windows[i];
    // var loc = locations[i];

    // var adjustedLoc = new WindowLocation(loc.X + monitor.X, loc.Y + monitor.Y,
    // loc.Width, loc.Height, loc.State);

    // if (!window.IsMouseMoving)
    // {
    // handle.DeferWindowPos(window, adjustedLoc);
    // }
    // }
    // }
    // }
    // else
    // {
    // windows.ForEach(w => w.Hide());
    // }
    // }
    // else
    // {
    // windows.ForEach(w => w.ShowInCurrentState());
    // }
    // }

    // private void SwapWindows(Window left, Window right)
    // {
    // lock (_windows)
    // {
    // _Logger.Trace("SwapWindows[{0},{1}]", left, right);
    // var leftIdx = _windows.FindIndex(w => w == left);
    // var rightIdx = _windows.FindIndex(w => w == right);

    // _windows[leftIdx] = right;
    // _windows[rightIdx] = left;
    // }

    // DoLayout();
    // }

    private ILayout GetLayout() {
        return layouts[layoutIndex];
    }

    public void HideAll() {
        for (var w : allWindows)
            w.Hide();
    }

    public void ShowAll() {
        for (var w : allWindows)
            w.ShowInCurrentState();
    }

    public void Focus() {
        var firstFocused = GetFocusedWindow();
        if (firstFocused != null)
            firstFocused.Focus();
    }
}
