package pers.louisj.Zwm.Core.L2.VirtualDesk;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Derived.ILayout;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.Global.Message.WindowMessage.WindowEvent;
import pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts.GridLayout;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouter;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.Utils.Types.Rectangle;

public class VirtualDesk {
    private static Logger logger = LogManager.getLogger("VirtualDesk");

    private String name;

    public HashSet<Window> allWindows = new HashSet<>();
    private Monitor monitor = null;
    private ILayout layout;
    public Window lastFocused = null;

    public VirtualDeskRouter router;

    public ActionImpl Action = new ActionImpl();

    public class ActionImpl {
        public void TurnWindowLeft() {
            if (lastFocused == null || layout == null)
                return;
            layout.ShiftLeft(lastFocused);
        }

        public void TurnWindowRight() {
            if (lastFocused == null || layout == null)
                return;
            layout.ShiftRight(lastFocused);
        }

        public void TurnWindowUp() {
            if (lastFocused == null || layout == null)
                return;
            layout.ShiftUp(lastFocused);
        }

        public void TurnWindowDown() {
            if (lastFocused == null || layout == null)
                return;
            layout.ShiftDown(lastFocused);
        }

        public void AreaShrink() {
            if (lastFocused == null || layout == null)
                return;
            layout.AreaShrink(lastFocused);
        }

        public void AreaExpand() {
            if (lastFocused == null || layout == null)
                return;
            layout.AreaExpand(lastFocused);
        }

        public void WindowMoveResize() {
            if (lastFocused == null || layout == null)
                return;
            layout.WindowMoveResize(lastFocused);
        }
    }

    public VirtualDesk(String name, VirtualDeskRouter router, ILayout layout) {
        this.name = name;
        this.router = router;
        // this.layout = new GridLayout(3, (float) 0.05, false);
        // this.layout = new GridLayout();
        this.layout = layout;
    }

    public String GetName() {
        return name;
    }

    public HashSet<Window> GetAllWindows() {
        HashSet<Window> copy = new HashSet<Window>();
        copy.addAll(allWindows);
        return copy;
    }

    public HashSet<Window> GetLayoutWindows() {
        if (layout == null)
            return new HashSet<Window>();
        HashSet<Window> copy = new HashSet<Window>();
        copy.addAll(layout.GetWindows());
        return copy;
    }

    public boolean isEmpty() {
        return allWindows.size() == 0;
    }
    // public Window GetLastFocusedWindow() {
    // return allWindows.iterator().next();
    // }

    public void AddWindow(Window window) {
        logger.info("AddWindow, {}", window);
        allWindows.add(window);

        if (monitor == null)
            window.Action.Hide();
        else
            window.Action.ShowNoActive();

        if (layout != null && window.Query.CanLayout()) {
            layout.AddWindow(window);
            // window.Action.DecorateEnable();
        }
    }

    public void AddWindow(Window window, boolean isLayout) {
        logger.info("AddWindow, {}, {}", window, isLayout);
        allWindows.add(window);

        if (layout != null && isLayout) {
            layout.AddWindow(window);
            // window.Action.DecorateEnable();
        }
    }

    public void RemoveWindow(Window window) {
        logger.info("RemoveWindow, {}", window);
        allWindows.remove(window);

        if (layout != null && window.Query.CanLayout()) {
            layout.RemoveWindow(window);
            // window.Action.DecorateDisable();
        }
        if (lastFocused == window)
            lastFocused = null;
    }

    public void UpdateWindow(Window window, WindowEvent updateType) {
        // layout.DoLayoutFull();
        // if (type == WindowUpdateType.Foreground)
        // _lastFocused = window;

        // if (layout)
        // synchronized (layoutWindows) {
        // if (layoutWindows.contains(window))
        // DoLayout();
        // }
    }

    public void WindowToggleMinimize(Window window) {
        if (layout != null) {
            window.Refresh.RefreshState();
            layout.ToggleMinimize(window);
        }
    }

    public void ResetLayout() {
        // GetLayoutEngine().ResetPrimaryArea();
        if (layout != null) {
            layout.ResetLayout();
        }
    }

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

    public void Focus() {
        if (lastFocused != null)
            lastFocused.Action.Focus();
    }

    public void Enable(Monitor monitor) {
        for (var w : allWindows)
            w.Action.ShowNoActive();
        this.monitor = monitor;
        if (layout != null)
            layout.Enable(monitor.GetWorkingRect());
    }

    public void Disable() {
        this.monitor = null;
        for (var w : allWindows)
            w.Action.Hide();
        if (layout != null)
            layout.Disable();
    }

    public void Deal(VDMessage msg) {
        switch (msg.event) {
            // TODO: Impl
            case WindowUpdateState: {
                WindowToggleMinimize((Window) msg.param);
                break;
            }
            case TurnWindowLeft: {
                Action.TurnWindowLeft();
                break;
            }
            case TurnWindowRight: {
                Action.TurnWindowRight();
                break;
            }
            case TurnWindowUp: {
                Action.TurnWindowUp();
                break;
            }
            case TurnWindowDown: {
                Action.TurnWindowDown();
                break;
            }
            // TODO: NOTTESTED
            case ResetLayout: {
                ResetLayout();
                break;
            }
            // TODO: NOTTESTED
            case AreaShrink: {
                Action.AreaShrink();
                break;
            }
            // TODO: NOTTESTED
            case AreaExpand: {
                Action.AreaExpand();
                break;
            }
            // TODO: NOTTESTED
            case WindowUpdateLocation: {
                Action.WindowMoveResize();
                break;
            }
            // TODO:
            case ToggleTiling:
            case DisplayInMonitor:
            default:
                break;
        }
    }
}
