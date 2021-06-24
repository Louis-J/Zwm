package pers.louisj.Zwm.Core.L2.VirtualDesk;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Derived.ILayout;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouter;
import pers.louisj.Zwm.Core.L2.Window.Window;

public class VirtualDesk {
    private static Logger logger = LogManager.getLogger("VirtualDesk");

    private String name;

    public HashSet<Window> allWindows = new HashSet<>();
    public Monitor monitor = null;
    private ILayout layout;
    public Window lastFocused = null;

    public VirtualDeskRouter router;


    public ActionLayoutImpl ActionLayout = new ActionLayoutImpl();
    public ActionVDImpl ActionVD = new ActionVDImpl();

    public class ActionLayoutImpl {
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

        public void WindowToggleMinimize(Window window, boolean isMinimize) {
            window.Refresh.RefreshState();
            if (layout != null) {
                layout.ToggleMinimize(window, isMinimize);
            }
            if (!isMinimize && window.Query.IsFocused())
                lastFocused = window;
        }

        public void ResetLayout() {
            if (layout != null) {
                layout.ResetLayout();
            }
        }

        public void ToggleTiling() {
            if (lastFocused == null || layout == null)
                return;
            layout.WindowToggleLayout(lastFocused);
        }
    }

    public class ActionVDImpl {
        public void WindowAdd(Window window) {
            logger.info("WindowAdd, {}", window);
            allWindows.add(window);

            if (monitor == null)
                window.Action.Hide();
            else
                window.Action.ShowNoActive();

            if (layout != null && window.Query.CanLayout()) {
                layout.WindowAdd(window);
            }
        }

        public void WindowRemove(Window window) {
            logger.info("WindowRemove, {}", window);
            allWindows.remove(window);

            if (layout != null && window.Query.CanLayout()) {
                layout.WindowRemove(window);
                // window.Action.DecorateDisable();
            }
            if (lastFocused == window)
                lastFocused = null;
        }

        public void Focus() {
            if (lastFocused != null)
                lastFocused.Action.Focus();
        }

        public void Enable(Monitor m) {
            for (var w : allWindows) {
                if (!w.Query.IsCloaked())
                    w.Action.ShowNoActive();
            }
            monitor = m;
            if (layout != null)
                layout.Enable(m.GetWorkingRect());
        }

        public void Disable() {
            monitor = null;
            for (var w : allWindows)
                w.Action.Hide();
            if (layout != null)
                layout.Disable();
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

    public void WindowAdd(Window window) {
        logger.info("AddWindow, {}", window);
        allWindows.add(window);

        if (monitor == null)
            window.Action.Hide();
        else
            window.Action.ShowNoActive();

        if (layout != null && window.Query.CanLayout()) {
            layout.WindowAdd(window);
            // window.Action.DecorateEnable();
        }
    }

    public void WindowAdd(Window window, boolean isLayout) {
        logger.info("AddWindow, {}, {}", window, isLayout);
        allWindows.add(window);

        if (layout != null && isLayout) {
            layout.WindowAdd(window);
            // window.Action.DecorateEnable();
        }
    }

    public void WindowRemove(Window window) {
        logger.info("RemoveWindow, {}", window);
        allWindows.remove(window);

        if (layout != null && window.Query.CanLayout()) {
            layout.WindowRemove(window);
        }
        if (lastFocused == window)
            lastFocused = null;
    }

    public void ResetLayout() {
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
            case TurnWindowLeft: {
                ActionLayout.TurnWindowLeft();
                break;
            }
            case TurnWindowRight: {
                ActionLayout.TurnWindowRight();
                break;
            }
            case TurnWindowUp: {
                ActionLayout.TurnWindowUp();
                break;
            }
            case TurnWindowDown: {
                ActionLayout.TurnWindowDown();
                break;
            }
            case ResetLayout: {
                ActionLayout.ResetLayout();
                break;
            }
            case AreaShrink: {
                ActionLayout.AreaShrink();
                break;
            }
            case AreaExpand: {
                ActionLayout.AreaExpand();
                break;
            }
            case WindowUpdateLocation: {
                ActionLayout.WindowMoveResize();
                break;
            }
            case ToggleTiling: {
                ActionLayout.ToggleTiling();
                break;
            }
            case WindowMinimizeStart: {
                ActionLayout.WindowToggleMinimize((Window) msg.param, true);
                break;
            }
            case WindowMinimizeEnd: {
                ActionLayout.WindowToggleMinimize((Window) msg.param, false);
                break;
            }
            // TODO:
            default:
                break;
        }
    }
}
