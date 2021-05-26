package org.louisj.Zwm.VirtualDesk;

import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.louisj.Zwm.Context;
import org.louisj.Zwm.Window.Window;
import org.louisj.Zwm.Window.WindowUpdateType;

public class VirtualDeskManager {

    public interface VirtualDeskUpdatedCallBack {
        public void Invoke();
    }

    public interface WindowAddedCallBack {
        public void Invoke(Window window, VirtualDesk vd);
    }

    public interface WindowUpdatedCallBack {
        public void Invoke(Window window, VirtualDesk vd);
    }

    public interface WindowRemovedCallBack {
        public void Invoke(Window window, VirtualDesk vd);
    }

    public interface WindowMovedCallBack {
        public void Invoke(Window window, VirtualDesk oldVd, VirtualDesk newVd);
    }

    private static Logger logger = LogManager.getLogger("VirtualDeskManager");

    private Context context;

    private int focusedIndex;

    private Map<Window, VirtualDesk> windowsToVirtualDesk;
    private List<VirtualDesk> virtualDesks;

    public List<VirtualDeskUpdatedCallBack> eventVirtualDeskUpdated;
    public List<WindowAddedCallBack> eventWindowAdded;
    public List<WindowUpdatedCallBack> eventWindowUpdated;
    public List<WindowRemovedCallBack> eventWindowRemoved;
    public List<WindowMovedCallBack> eventWindowMoved;

    public WindowRouter router;

    public VirtualDeskManager(Context context) {
        this.context = context;
        windowsToVirtualDesk = new HashMap<Window, VirtualDesk>();
    }

    public void CreateVirtualDesks(String[] names) {
        for (var name : names) {
            CreateVirtualDesk(name);
        }
    }

    public void CreateVirtualDesk(String name) {
        virtualDesks.add(new VirtualDesk(context, name, null));
    }

    public byte RemoveVirtualDesk(VirtualDesk vd) {
        if (virtualDesks.size() == 1)
            return (byte) -1;
        var index = virtualDesks.indexOf(vd);
        if (index == -1)
            return (byte) -2;

        if (vd.allWindows.size() != 0)
            return (byte) -3;

        if (focusedIndex == index) {
            virtualDesks.remove(index);

            var nextVd = virtualDesks.get(index % virtualDesks.size());
            nextVd.ShowAll();
            nextVd.Focus();
        } else if (focusedIndex > index) {
            focusedIndex--;
        }

        for (var e : eventVirtualDeskUpdated)
            e.Invoke();
        return (byte) 0;
    }

    public void SwitchToVirtualDesk(int index) {
        logger.info("SwitchToVirtualDesk, {}", index);
        if (virtualDesks.size() > index && focusedIndex != index) {
            var source = virtualDesks.get(focusedIndex);
            var target = virtualDesks.get(index);

            source.HideAll();
            target.ShowAll();
            target.Focus();

            for (var e : eventVirtualDeskUpdated)
                e.Invoke();
        }
    }

    public void SwitchToVirtualDesk(VirtualDesk target) {
        logger.info("SwitchToVirtualDesk, {}", target);
        if (target == null)
            throw new RuntimeException("Error target!");
        var source = virtualDesks.get(focusedIndex);
        // int targetIndex = 0;
        // for (;; targetIndex++) {
        // if (targetIndex >= virtualDesks.size())
        // throw new RuntimeException("Error target!");
        // if (virtualDesks.get(focusedIndex) == target)
        // break;
        // }
        int targetIndex = virtualDesks.indexOf(target);
        if (targetIndex == -1)
            throw new RuntimeException("Error target!");

        if (focusedIndex != targetIndex) {
            source.HideAll();
            target.ShowAll();
            target.Focus();

            focusedIndex = targetIndex;

            for (var e : eventVirtualDeskUpdated)
                e.Invoke();
        }
    }

    public void SwitchToNextVirtualDesk() {
        SwitchToVirtualDesk((focusedIndex + 1) % virtualDesks.size());
    }

    public void SwitchToPreviousVirtualDesk() {
        SwitchToVirtualDesk((focusedIndex + virtualDesks.size() - 1) % virtualDesks.size());
    }

    public Window MoveFocusedWindowOut() {
        logger.info("MoveFocusedWindowToVirtualDesk");
        var focusedVd = virtualDesks.get(focusedIndex);
        var window = focusedVd.GetFocusedWindow();
        if (window == null)
            return null;

        focusedVd.RemoveWindow(window);
        windowsToVirtualDesk.remove(window);
        focusedVd.Focus();

        for (var e : eventWindowRemoved)
            e.Invoke(window, focusedVd);

        return window;
    }

    public void MoveAllWindows(VirtualDesk source, VirtualDesk target) {
        target.allWindows.addAll(source.allWindows);
        target.layoutWindows.addAll(source.layoutWindows);
        for (var w : source.allWindows)
            windowsToVirtualDesk.put(w, target);

        for (var e : eventVirtualDeskUpdated)
            e.Invoke();
    }

    public void AddWindow(Window window) {
        logger.info("AddWindow({0})", window);
        VirtualDesk target;
        target = virtualDesks.get(focusedIndex);
        // router
        // target = router.Query(window);
        // if(target != null) {
        // target = virtualDesks.get(focusedIndex);
        // SwitchToVirtualDesk(target);
        // }
        target.AddWindow(window);
        windowsToVirtualDesk.put(window, target);

        for (var e : eventWindowAdded)
            e.Invoke(window, target);
    }

    public void RemoveWindow(Window window) {
        logger.info("RemoveWindow({0})", window);
        VirtualDesk vd = windowsToVirtualDesk.get(window);
        if (vd != null) {
            vd.RemoveWindow(window);
            windowsToVirtualDesk.remove(window);

            for (var e : eventWindowRemoved)
                e.Invoke(window, vd);
        }
    }

    public void UpdateWindow(Window window, byte updateType) {
        logger.info("UpdateWindow({0}, {1})", window, updateType);
        VirtualDesk vd = windowsToVirtualDesk.get(window);
        if (vd != null) {
            if (vd == virtualDesks.get(focusedIndex)) {
                if (updateType == WindowUpdateType.Foreground) {
                    for (var e : eventVirtualDeskUpdated)
                        e.Invoke();
                } else if (updateType == WindowUpdateType.MoveEnd) {
                    // ResizeOrSwapWindow();
                }
                vd.UpdateWindow(window, updateType);
                for (var e : eventWindowUpdated)
                    e.Invoke(window, vd);
            }
        }
    }

    // private void TrySwapWindowToMouse(Window window)
    // {
    // var point = Control.MousePosition;
    // int x = point.X;
    // int y = point.Y;

    // var currentVirtualDesk = _windowsToVirtualDesks[window];

    // if (currentVirtualDesk.IsPointInside(x, y))
    // {
    // currentVirtualDesk.SwapWindowToPoint(window, x, y);
    // }
    // else
    // {
    // foreach (var workspace in _context.VirtualDeskContainer.GetAllVirtualDesks())
    // {
    // var monitor =
    // _context.VirtualDeskContainer.GetCurrentMonitorForVirtualDesk(workspace);
    // if (monitor != null && workspace.IsPointInside(x, y))
    // {
    // currentVirtualDesk.RemoveWindow(window, false);
    // workspace.AddWindow(window, false);
    // _windowsToVirtualDesks[window] = workspace;

    // workspace.SwapWindowToPoint(window, x, y);
    // currentVirtualDesk.DoLayout();
    // }
    // }
    // }
    // }

    public VirtualDeskState GetState() {
        return null;
    }

    public void SetState(VirtualDeskState state) {
    }

    public void Initialize(List<Window> windows) {
        logger.info("Initialize({0})", windows);

        for (var w : windows) {
            AddWindow(w);
        }
    }
}
