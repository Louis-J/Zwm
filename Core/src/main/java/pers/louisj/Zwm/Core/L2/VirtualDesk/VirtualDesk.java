package pers.louisj.Zwm.Core.L2.VirtualDesk;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Derived.ILayout;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouter;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

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

        public void WindowMoveResize(Window window) {
            if (layout == null)
                return;
            var rectOld = window.Query.GetRect();
            window.Refresh.RefreshRect();
            var rectNew = window.Query.GetRect();
            logger.info("WindowMoveResize, rectOld, {}", rectOld);
            logger.info("WindowMoveResize, rectNew, {}", rectNew);
            if (rectOld.height == rectNew.height && rectOld.width == rectNew.width) {
                layout.WindowMove(window, WinHelper.GetMousePoint());
            } else {
                layout.WindowResize(window);
            }
        }

        public void WindowToggleMinimize(Window window, boolean isMinimize) {
            window.Refresh.RefreshState();
            if (layout != null) {
                layout.ToggleMinimize(window, isMinimize);
            }
        }

        public void ResetLayout() {
            if (layout != null) {
                layout.ResetLayout();
            }
        }

        public void ToggleLayout() {
            if (lastFocused == null || layout == null)
                return;
            layout.ToggleLayout(lastFocused);
        }
    }

    public class ActionVDImpl {
        public void WindowAdd(Window window) {
            logger.info("WindowAdd, {}", window);
            allWindows.add(window);

            if (monitor == null)
                window.Action.Hide();

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
            case ToggleLayout: {
                ActionLayout.ToggleLayout();
                break;
            }
            // TODO:
            default:
                break;
        }
    }
}
