package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.ILayout;
import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.WindowUpdateType;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDEvent;
import pers.louisj.Zwm.Core.Global.Message.WindowMessage.WindowEvent;
import pers.louisj.Zwm.Core.L1.MainLoop;
import pers.louisj.Zwm.Core.L2.VirtualDesk.LayoutFilter;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.Async.ChannelList;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

public class VirtualDeskManager {
    interface DefaultVDFunc {
        VirtualDesk Invoke();
    }

    protected static Logger logger = LogManager.getLogger("VirtualDeskManager");

    Context context;

    private Map<Window, VirtualDesk> windowsToVirtualDesk = new HashMap<Window, VirtualDesk>();

    public List<Monitor> monitors = new ArrayList<>();
    public List<VirtualDesk> virtualDesks = new ArrayList<>();
    public int focusedIndex = 0; // Set as public for Test

    private DefaultVDFunc defaultVDFunc = () -> {
        return new VirtualDesk("more", null, null);
    };

    public VirtualDeskFilter filterIgnore = new VirtualDeskFilter();
    public LayoutFilter filterLayout = new LayoutFilter();
    public VirtualDeskRouterMan routerMan = new VirtualDeskRouterMan();

    public ChannelList<Message> channelOut = new ChannelList<>();

    // Action Packages
    public ActionInVDImpl ActionInVD = new ActionInVDImpl();

    public ActionGlobalImpl ActionGlobal = new ActionGlobalImpl();

    protected ActionInnerImpl ActionInner = new ActionInnerImpl();

    protected class ActionInnerImpl {
        public Monitor GetFocusdMonitor() {
            return virtualDesks.get(focusedIndex).monitor;
        }

        public VirtualDesk GetFocusdVD() {
            return virtualDesks.get(focusedIndex);
        }

        public Window GetFocusdWindow() {
            return virtualDesks.get(focusedIndex).lastFocused;
        }
    }

    public class ActionInVDImpl {
        private Window MoveFocusedWindowOut() {
            logger.info("MoveFocusedWindowToVirtualDesk");
            var focusedVd = ActionInner.GetFocusdVD();
            var window = ActionInner.GetFocusdWindow();
            if (window == null)
                return null;

            focusedVd.ActionOther.WindowRemove(window);
            windowsToVirtualDesk.remove(window);
            focusedVd.ActionOther.Focus();

            // TODO:
            // eventWindowRemoved
            // channelOut.put(new WindowMessage(WindowEvent.Remove, window));

            return window;
        }

        // TODO:
        private void MoveAllWindowsTo(VirtualDesk source, VirtualDesk target) {
            target.allWindows.addAll(source.allWindows);
            for (var w : source.GetLayoutWindows()) {
                target.AddWindow(w, true);
                w.Action.ShowNoActive();
            }
            for (var w : source.allWindows)
                windowsToVirtualDesk.put(w, target);

            // TODO:
            // eventVirtualDeskUpdated
            // channelOut.put(new VDMessage(VDEvent.VirtualDeskUpdated, null));
        }

        public void WindowAddInit(List<Window> windows) {
            for (var window : windows) {
                if (filterIgnore.CheckMatch(window)) {
                    logger.info("WindowAddInit, Ignored, {}", window);
                    continue;
                }

                logger.info("WindowAddInit, {}", window);
                window.Action.SetCanLayout(!filterLayout.CheckMatch(window));
                var forew = WindowStaticAction.GetForegroundWindow();
                boolean isForeground = window.hWnd.equals(forew);

                VirtualDesk target;
                var monitor = Monitor.GetMonitorByHwnd(window.hWnd);
                if (monitor != null)
                    target = monitor.vd;
                else
                    target = virtualDesks.get(focusedIndex);
                target.AddWindow(window);
                target.lastFocused = window;
                windowsToVirtualDesk.put(window, target);

                if (isForeground && target != virtualDesks.get(focusedIndex))
                    ActionGlobal.VDSwitchTo(virtualDesks.indexOf(target));
            }

            // TODO:
            // eventWindowAddedInit
            // channelOut.put(new WindowMessage(WindowEvent.Add, window));
        }

        public void WindowAdd(Window window) {
            if (filterIgnore.CheckMatch(window)) {
                logger.info("WindowAdd, Ignored, {}", window);
                return;
            }

            logger.info("WindowAdd, {}", window);
            window.Action.SetCanLayout(!filterLayout.CheckMatch(window));
            var forew = WindowStaticAction.GetForegroundWindow();
            boolean isForeground = window.hWnd.equals(forew);

            Monitor focusedMonitor = ActionInner.GetFocusdMonitor();
            VirtualDesk focusedVD = ActionInner.GetFocusdVD();
            VirtualDesk target;
            // router
            target = routerMan.CheckRouter(window);
            if (target == null)
                target = focusedVD;
            target.ActionOther.WindowAdd(window);
            target.lastFocused = window;
            windowsToVirtualDesk.put(window, target);

            if (isForeground && target != focusedVD)
                ActionGlobal.VDSwitchTo(monitors.indexOf(focusedMonitor), virtualDesks.indexOf(target));

            // TODO:
            // eventWindowAdded
            // channelOut.put(new WindowMessage(WindowEvent.Add, window));
        }

        public void WindowRemove(Window window) {
            logger.info("WindowRemove, {}", window);
            VirtualDesk vd = GetFocusdVD();
            if (vd != null) { // TODO: VD MUSTNOT BE NULL
                vd.ActionOther.WindowRemove(window);
                windowsToVirtualDesk.remove(window);

                // TODO:
                // eventWindowRemoved
                // channelOut.put(new WindowMessage(WindowEvent.Remove, window));
            }
        }

        // TODO: Test For MULTI MONITORS
        public void WindowForeground(Window window) {
            if (window == null) {
                VirtualDesk vd = virtualDesks.get(focusedIndex);
                vd.lastFocused = null;
            } else {
                VirtualDesk vd = windowsToVirtualDesk.get(window);
                if (vd == null) {
                    logger.info("Foreground, 1");
                    return;
                }
                int index = virtualDesks.indexOf(vd);
                if (index == -1) {
                    logger.error("Foreground, 2");
                    return;
                }
                if (focusedIndex != index) {
                    virtualDesks.get(focusedIndex).Disable();
                    vd.Enable(monitors.get(0));
                    focusedIndex = index;
                }
                vd.lastFocused = window;
                logger.info("Foreground, 3");
            }
        }

        public void FocusdWindowMoveTo(VirtualDesk target) {
            var window = MoveFocusedWindowOut();
            if (window == null)
                return;

            target.ActionOther.WindowAdd(window);
            target.lastFocused = window;
            windowsToVirtualDesk.put(window, target);
            // TODO:
            // eventWindowRemoved
        }

        public void FocusdWindowMoveTo(int vdIndex) {
            var target = virtualDesks.get(vdIndex);
            if (target != null)
                FocusdWindowMoveTo(target);
        }

        // TODO:
        public void WindowUpdate(Window window, WindowEvent event) {
            logger.info("UpdateWindow, {}, {}", window, event);
            if (event == WindowEvent.Foreground) {
                if (window == null) {
                    VirtualDesk vd = virtualDesks.get(focusedIndex);
                    vd.lastFocused = null;
                } else {
                    VirtualDesk vd = windowsToVirtualDesk.get(window);
                    if (vd == null) {
                        logger.info("UpdateWindow, Foreground, 1");
                        return;
                    }
                    int index = virtualDesks.indexOf(vd);
                    if (index == -1) {
                        logger.error("UpdateWindow, Foreground, 2");
                        return;
                    }
                    focusedIndex = index;
                    vd.lastFocused = window;
                    logger.info("UpdateWindow, Foreground, 3");
                }
            }
            if (window == null) {
                return;
            }
            switch (event) {
                case MinimizeStart:
                case MinimizeEnd: {
                    VirtualDesk vd = windowsToVirtualDesk.get(window);
                    if (vd == null) {
                        logger.error("UpdateWindow, MinimizeToggle, 1");
                        return;
                    }
                    vd.UpdateWindow(window, event);
                }
                default:
                    break;
            }
            // VirtualDesk vd = windowsToVirtualDesk.get(window);
            // if (vd != null) {
            // if (vd == virtualDesks.get(focusedIndex)) {
            // if (event == WindowEvent.Foreground) {
            // if(window == null) {

            // }
            // // eventVirtualDeskUpdated
            //
            // channelOut.put(new WindowMessage(event, window));
            // } else if (event == WindowEvent.MoveEnd) {
            // // ResizeOrSwapWindow();
            // }
            // vd.UpdateWindow(window, event);
            // // eventWindowUpdated
            //
            // channelOut.put(new WindowMessage(event, window));
            // }
            // }
        }
    }

    public class ActionGlobalImpl {
        public void VDCreate(String name, VirtualDeskRouter router, ILayout layout) {
            var vd = new VirtualDesk(name, router, layout);
            virtualDesks.add(vd);
            routerMan.Add(vd, vd.router);
        }

        public void VDAdd(VirtualDesk vd) {
            virtualDesks.add(vd);
            routerMan.Add(vd, vd.router);
        }

        // TODO:
        public byte VDRemove(VirtualDesk vd) {
            if (virtualDesks.size() == 1)
                return (byte) -1;
            var index = virtualDesks.indexOf(vd);
            if (index == -1)
                return (byte) -2;

            if (vd.isEmpty())
                return (byte) -3;

            if (focusedIndex == index) {
                virtualDesks.remove(index);

                var nextVd = virtualDesks.get(index % virtualDesks.size());
                nextVd.Enable(monitors.get(0));
                nextVd.Focus();
            } else if (focusedIndex > index) {
                focusedIndex--;
            }
            routerMan.Remove(vd);

            // for (var e : eventVirtualDeskUpdated)
            // e.Invoke();
            channelOut.put(new VDMessage(VDEvent.VirtualDeskUpdated, null));
            return (byte) 0;
        }

        // TODO:
        public byte VDRemove(String name) {
            if (virtualDesks.size() == 1)
                return (byte) -1;
            int index = 0;
            VirtualDesk targetVd = null;
            for (var vd : virtualDesks) {
                if (vd.GetName().equals(name)) {
                    targetVd = vd;
                    break;
                }
                index++;
            }
            if (index == virtualDesks.size())
                return (byte) -2;

            if (targetVd.isEmpty())
                return (byte) -3;

            if (focusedIndex == index) {
                virtualDesks.remove(index);

                var nextVd = virtualDesks.get(index % virtualDesks.size());
                nextVd.Enable(monitors.get(0));
                nextVd.Focus();
            } else if (focusedIndex > index) {
                focusedIndex--;
            }
            routerMan.Remove(targetVd);

            // TODO: event VirtualDeskUpdated
            // channelOut.put(new VDMessage(VDEvent.VirtualDeskUpdated, null));
            return (byte) 0;
        }

        // TODO:
        public void VDSwitchTo(int monitorindex, int vdindex) {
            logger.info("VDSwitchTo, {}, {}", monitorindex, vdindex);
            var monitor = monitors.get(monitorindex);
            var targetVd = virtualDesks.get(vdindex);
            if (monitor.vd != targetVd && focusedIndex != vdindex) {
                var sourceVd = virtualDesks.get(focusedIndex);

                var targetVdMointor = targetVd.monitor;
                if (targetVdMointor != null)
                    sourceVd.Enable(targetVdMointor);
                else
                    sourceVd.Disable();
                targetVd.Enable(monitor);
                targetVd.Focus();

                focusedIndex = vdindex;

                // TODO: event VirtualDeskUpdated
                // channelOut.put(new VDMessage(VDEvent.VirtualDeskUpdated, null));
            }
        }

        // TODO:
        public void VDSwitchTo(int vdindex) {
            logger.info("VDSwitchTo, {}", vdindex);
            var monitor = ActionInner.GetFocusdMonitor();
            var targetVd = virtualDesks.get(vdindex);
            if (monitor.vd != targetVd && focusedIndex != vdindex) {
                var sourceVd = virtualDesks.get(focusedIndex);

                var targetVdMointor = targetVd.monitor;
                if (targetVdMointor != null)
                    sourceVd.Enable(targetVdMointor);
                else
                    sourceVd.Disable();
                targetVd.Enable(monitor);
                targetVd.Focus();

                focusedIndex = vdindex;

                // TODO: event VirtualDeskUpdated
                // channelOut.put(new VDMessage(VDEvent.VirtualDeskUpdated, null));
            }
        }

        // TODO:
        public VirtualDeskState StateGet() {
            return null;
        }

        // TODO:
        public void StateSet(VirtualDeskState state) {
        }

        // TODO:
        public void FocusToPoint(int x, int y) {
        }

        // TODO:
        public void FocusToWindow(Window window) {
        }
    }

    public VirtualDeskManager(Context context) {
        this.context = context;
    }

    public void Exit() {
        VirtualDeskManager.logger.info("VDMan, Exit");
        var target = virtualDesks.get(0);
        for (int i = 1; i < virtualDesks.size(); i++)
            ActionInVD.MoveAllWindowsTo(virtualDesks.get(i), target);
        ActionGlobal.VDSwitchTo(0);
        for (var w : target.GetLayoutWindows()) {
            // w.Action.DecorateDisable();
        }
    }

    public void Deal(VDManMessage msg) {
        switch (msg.event) {
            case WindowAddInit: {
                ActionInVD.WindowAddInit((List<Window>) msg.param);
                break;
            }
            case WindowAdd: {
                ActionInVD.WindowAdd((Window) msg.param);
                break;
            }
            case WindowRemove: {
                ActionInVD.WindowRemove((Window) msg.param);
                break;
            }

            // TODO: Test For Muti Monitors
            case RefreshMonitors: {
                monitors = Monitor.GetMonitors();
                while (monitors.size() > virtualDesks.size()) {
                    ActionGlobal.VDAdd(defaultVDFunc.Invoke());
                }
                var it = virtualDesks.iterator();
                for (var m : monitors) {
                    if (m.vd == null) {
                        VirtualDesk vd;
                        for (vd = it.next(); vd.monitor != null; vd = it.next()) {
                        }
                        vd.ActionOther.Enable(m);
                        m.vd = vd;
                    }
                }
                break;
            }
            // TODO: Impl
            case VDAdd:
            case VDRemove:
                break;

            case SwitchToVD: {
                int index = ((Integer) msg.param).intValue();
                if (index == -2) // Next VD
                    index = (focusedIndex + 1) % virtualDesks.size();
                else if (index == -1) // Prev VD
                    index = (focusedIndex + virtualDesks.size() - 1) % virtualDesks.size();
                // Display.SwitchToVD(index);
                ActionGlobal.VDSwitchTo(index);
                break;
            }

            // TODO: Update For Muti Monitors
            case SwitchMonitorToVD:
                break;

            // TODO: NOTTESTED
            case MoveWindowToVD: {
                int index = ((Integer) msg.param).intValue();
                if (index == -2) // Next VD
                    index = (focusedIndex + 1) % virtualDesks.size();
                else if (index == -1) // Prev VD
                    index = (focusedIndex + virtualDesks.size() - 1) % virtualDesks.size();
                // ActionWindow.MoveFocusedWindowToVD(index);
                ActionInVD.FocusdWindowMoveTo(index);
                break;
            }
            // TODO: Update For Muti Monitors
            case Foreground:
                ActionInVD.WindowForeground((Window) msg.param);
                break;
            // TODO:FOR TEST
            case VDDebugInfo: {
                logger.info("VDDebugInfo Start");
                for (var vd : context.vdMan.virtualDesks) {
                    System.out.println("Begin: " + vd.GetName() + ", size = " + String.valueOf(vd.allWindows.size()));
                    System.out.println("monitor: " + monitors.indexOf(vd.monitor));
                    System.out.println("AllWindows: " + String.valueOf(vd.allWindows.size()));
                    for (var w : vd.allWindows) {
                        System.out.println("handle: " + w.hWnd);
                        System.out.println("pid: " + w.processId);
                        System.out.println("name: " + w.processName);
                        System.out.println("class: " + w.windowClass);
                        System.out.println("title: " + w.windowTitle);
                        System.out.println();
                    }
                    System.out.println("End: " + vd.GetName() + "\n");
                }
                logger.info("VDDebugInfo End");
                break;
            }
        }
    }

    // TODO: Update For Muti Monitors
    public VirtualDesk GetFocusdVD() {
        return virtualDesks.get(focusedIndex);
    }
}
