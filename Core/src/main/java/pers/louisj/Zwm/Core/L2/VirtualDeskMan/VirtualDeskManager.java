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
import pers.louisj.Zwm.Core.Global.Message.WindowMessage.WindowMessage;
import pers.louisj.Zwm.Core.L1.MainLoop;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.Async.Channel2;
import pers.louisj.Zwm.Core.Utils.Async.ChannelList;

public class VirtualDeskManager {
    interface DefaultVDFunc {
        VirtualDesk Invoke();
    }

    protected static Logger logger = LogManager.getLogger("VirtualDeskManager");

    Context context;

    private Map<Window, VirtualDesk> windowsToVirtualDesk = new HashMap<Window, VirtualDesk>();;
    public List<VirtualDesk> virtualDesks = new ArrayList<>();
    private DefaultVDFunc defaultVDFunc = () -> {
        return new VirtualDesk("more", null, null);
    };

    public int focusedIndex = 0; // Set as public for Test
    public VirtualDeskFilter filterIgnore = new VirtualDeskFilter();
    public VirtualDeskRouterMan routerMan = new VirtualDeskRouterMan();

    public ChannelList<Message> channelOut = new ChannelList<>();

    public ArrayList<Monitor> monitors;

    public VirtualDeskManager(Context context) {
        this.context = context;
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

    public ActionWindowImpl ActionWindow = new ActionWindowImpl();

    public ActionVDImpl ActionVD = new ActionVDImpl();

    public class ActionVDImpl {
        public void VDCreate(String name, VirtualDeskRouter router, ILayout layout) {
            var vd = new VirtualDesk(name, router, layout);
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
        public void VDRemove(String name) {
        }

        // TODO:
        public VirtualDeskState GetState() {
            return null;
        }

        // TODO:
        public void SetState(VirtualDeskState state) {
        }
    }

    // TODO:
    class ActionWindowImpl {
        private Window MoveFocusedWindowOut() {
            logger.info("MoveFocusedWindowToVirtualDesk");
            var focusedVd = virtualDesks.get(focusedIndex);
            var window = focusedVd.lastFocused;
            if (window == null)
                return null;

            focusedVd.RemoveWindow(window);
            windowsToVirtualDesk.remove(window);
            focusedVd.Focus();

            // eventWindowRemoved
            channelOut.put(new WindowMessage(WindowEvent.Remove, window));

            return window;
        }

        void MoveAllWindows(VirtualDesk source, VirtualDesk target) {
            target.allWindows.addAll(source.allWindows);
            for (var w : source.GetLayoutWindows()) {
                target.AddWindow(w, true);
                w.Action.ShowNoActive();
            }
            for (var w : source.allWindows)
                windowsToVirtualDesk.put(w, target);

            // eventVirtualDeskUpdated
            channelOut.put(new VDMessage(VDEvent.VirtualDeskUpdated, null));
        }

        public void WindowAdd(Window window) {
            if (filterIgnore.CheckMatch(window)) {
                logger.info("WindowAdd, Ignored, {}", window);
                return;
            }

            logger.info("WindowAdd, {}", window);
            var forew = WindowStaticAction.GetForegroundWindow();
            boolean isForeground = window.hWnd.equals(forew);

            VirtualDesk target;
            // router
            target = routerMan.CheckRouter(window);
            if (target == null)
                target = virtualDesks.get(focusedIndex);
            target.AddWindow(window);
            target.lastFocused = window;
            windowsToVirtualDesk.put(window, target);

            if (isForeground && target != virtualDesks.get(focusedIndex))
                Display.SwitchToVD(virtualDesks.indexOf(target));
            // eventWindowAdded
            channelOut.put(new WindowMessage(WindowEvent.Add, window));
        }

        public void WindowRemove(Window window) {
            logger.info("WindowRemove, {}", window);
            VirtualDesk vd = windowsToVirtualDesk.get(window);
            if (vd != null) {
                vd.RemoveWindow(window);
                windowsToVirtualDesk.remove(window);

                // eventWindowRemoved
                channelOut.put(new WindowMessage(WindowEvent.Remove, window));
            }
        }

        public void MoveFocusedWindowToVD(int index) {
            if (virtualDesks.size() <= index || focusedIndex == index)
                return;

            var focusedVd = virtualDesks.get(focusedIndex);
            var window = focusedVd.lastFocused;
            if (window == null)
                return;

            boolean isForeground = focusedVd.lastFocused == window;
            focusedVd.RemoveWindow(window);
            var targetVD = virtualDesks.get(index);

            targetVD.AddWindow(window);
            if (isForeground)
                targetVD.lastFocused = window;
            windowsToVirtualDesk.put(window, targetVD);
        }
    }

    // TODO:
    class DisplayInner {
        public Monitor focusedMonitor;

        public Monitor GetMonitorAtPoint(int x, int y) {
            return null;
        }
    }

    DisplayImpl Display = new DisplayImpl();

    // TODO:
    class DisplayImpl {
        public void SwitchToVD(int index) {
            logger.info("SwitchToVD, {}", index);
            if (virtualDesks.size() > index && focusedIndex != index) {
                var source = virtualDesks.get(focusedIndex);
                var target = virtualDesks.get(index);

                source.Disable();
                target.Enable(monitors.get(0));
                target.Focus();

                focusedIndex = index;

                channelOut.put(new VDMessage(VDEvent.VirtualDeskUpdated, null));
            }
        }

        public void SwitchMonitorToVD(int mindex, int vdindex) {

        }

        public void FocusToPoint(int x, int y) {
        }

        public void FocusedWindowUpdate(int event) {

        }

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
        // public static
    }

    public void Foreground(Window window) {
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

    public void Exit() {
        VirtualDeskManager.logger.info("VDMan, Exit");
        var target = virtualDesks.get(0);
        for (int i = 1; i < virtualDesks.size(); i++)
            ActionWindow.MoveAllWindows(virtualDesks.get(i), target);
        Display.SwitchToVD(0);
        for (var w : target.GetLayoutWindows()) {
            // w.Action.DecorateDisable();
        }
    }

    public void Deal(VDManMessage msg) {
        switch (msg.event) {
            case WindowAdd: {
                ActionWindow.WindowAdd((Window) msg.param);
                break;
            }
            case WindowRemove: {
                ActionWindow.WindowRemove((Window) msg.param);
                break;
            }

            // TODO: Update For Muti Monitors
            case RefreshMonitors: {
                monitors = Monitor.GetMonitors();
                virtualDesks.get(0).Enable(monitors.get(0));
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
                Display.SwitchToVD(index);
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
                ActionWindow.MoveFocusedWindowToVD(index);
                break;
            }
            // TODO: Update For Muti Monitors
            case Foreground:
                Foreground((Window) msg.param);
                break;
            // TODO:FOR TEST
            case VDDebugInfo: {
                logger.info("VDDebugInfo Start");
                for (var vd : context.vdMan.virtualDesks) {
                    System.out.println("Begin: " + vd.GetName() + ", size = " + String.valueOf(vd.allWindows.size()));
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
