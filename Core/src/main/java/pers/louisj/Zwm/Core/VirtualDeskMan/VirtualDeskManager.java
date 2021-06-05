package pers.louisj.Zwm.Core.VirtualDeskMan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.ILayout;
import pers.louisj.Zwm.Core.Message.Message;
import pers.louisj.Zwm.Core.Message.MonitorMessage.MonitorMessage;
import pers.louisj.Zwm.Core.Message.VirtualDeskMessage.VirtualDeskEvent;
import pers.louisj.Zwm.Core.Message.VirtualDeskMessage.VirtualDeskMessage;
import pers.louisj.Zwm.Core.Message.WindowMessage.WindowEvent;
import pers.louisj.Zwm.Core.Message.WindowMessage.WindowMessage;
import pers.louisj.Zwm.Core.Utils.Channel;
import pers.louisj.Zwm.Core.Utils.Channel2;
import pers.louisj.Zwm.Core.Utils.ChannelList;
import pers.louisj.Zwm.Core.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.VirtualDesk.VirtualDeskRouter;
import pers.louisj.Zwm.Core.Window.Window;
import pers.louisj.Zwm.Core.Window.WindowUpdateType;

public class VirtualDeskManager {
    interface DefaultVDFunc {
        VirtualDesk Invoke();
    }

    private static Logger logger = LogManager.getLogger("VirtualDeskManager");

    private Context context;

    private Map<Window, VirtualDesk> windowsToVirtualDesk = new HashMap<Window, VirtualDesk>();;
    public List<VirtualDesk> virtualDesks = new ArrayList<>();
    private DefaultVDFunc defaultVDFunc = () -> {
        return new VirtualDesk("more", null, null);
    };

    private int focusedIndex = 0;
    public VirtualDeskFilter router;

    public Channel<Message> channelIn = new Channel2<>(1024);
    public ChannelList<Message> channelOut = new ChannelList<>();

    private Thread messageLoop = new MessageLoop();

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

    public void Defer() {
        logger.info("VirtualDeskManager Defer Start");

        channelIn.put(null);

        logger.info("VirtualDeskManager Defer 1");
        try {
            messageLoop.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("VirtualDeskManager Defer End");
    }

    public void Start() {
        messageLoop.start();
    }

    private ActionWindowImpl ActionWindow = new ActionWindowImpl();

    public ActionVDImpl ActionVD = new ActionVDImpl();

    public class ActionVDImpl {
        public void VDCreate(String name, VirtualDeskRouter router, ILayout layout) {
            virtualDesks.add(new VirtualDesk(name, router, layout));
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
                nextVd.ShowAll();
                nextVd.Focus();
            } else if (focusedIndex > index) {
                focusedIndex--;
            }

            // for (var e : eventVirtualDeskUpdated)
            // e.Invoke();
            channelOut.put(new VirtualDeskMessage(VirtualDeskEvent.VirtualDeskUpdated, null));
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
            channelOut.put(new WindowMessage(window, WindowEvent.Remove));

            return window;
        }

        private void MoveAllWindows(VirtualDesk source, VirtualDesk target) {
            target.allWindows.addAll(source.allWindows);
            for (var w : source.GetLayoutWindows()) {
                target.AddWindow(w, true);
                w.Action.ShowInCurrentState();
            }
            for (var w : source.allWindows)
                windowsToVirtualDesk.put(w, target);

            // eventVirtualDeskUpdated
            channelOut.put(new VirtualDeskMessage(VirtualDeskEvent.VirtualDeskUpdated, null));
        }

        public void WindowAdd(Window window) {
            if (context.vdFilter.CheckIgnore(window))
                return;

            logger.info("WindowAdd, {}", window);

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

            // eventWindowAdded
            channelOut.put(new WindowMessage(window, WindowEvent.Add));
        }

        public void WindowRemove(Window window) {
            logger.info("WindowRemove, {}", window);
            VirtualDesk vd = windowsToVirtualDesk.get(window);
            if (vd != null) {
                vd.RemoveWindow(window);
                windowsToVirtualDesk.remove(window);

                // eventWindowRemoved
                channelOut.put(new WindowMessage(window, WindowEvent.Remove));
            }
        }

        public void MoveFocusedWindowToVD(int index) {
            if (virtualDesks.size() <= index || focusedIndex == index)
                return;

            var focusedVd = virtualDesks.get(focusedIndex);
            var window = focusedVd.lastFocused;
            if (window == null)
                return;

            focusedVd.RemoveWindow(window);
            var targetVD = virtualDesks.get(index);

            targetVD.AddWindow(window);
            window.Action.Hide();
            windowsToVirtualDesk.put(window, targetVD);
        }

        public void MoveFocusedWindowToVDNext() {
            MoveFocusedWindowToVD((focusedIndex + 1) % virtualDesks.size());
        }

        public void MoveFocusedWindowToVDPrev() {
            MoveFocusedWindowToVD((focusedIndex + virtualDesks.size() - 1) % virtualDesks.size());
        }
    }

    // TODO:
    class DisplayInner {
        public Monitor focusedMonitor;

        public Monitor GetMonitorAtPoint(int x, int y) {
            return null;
        }
    }

    private DisplayImpl Display = new DisplayImpl();

    // TODO:
    class DisplayImpl {
        public void UpdateWindow(Window window, WindowEvent event) {
        }

        public void SwitchToVD(int index) {
            logger.info("SwitchToVD, {}", index);
            if (virtualDesks.size() > index && focusedIndex != index) {
                var source = virtualDesks.get(focusedIndex);
                var target = virtualDesks.get(index);

                source.HideAll();
                target.ShowAll();
                target.Focus();

                focusedIndex = index;

                channelOut.put(new VirtualDeskMessage(VirtualDeskEvent.VirtualDeskUpdated, null));
            }
        }

        public void SwitchMonitorToVD(int mindex, int vdindex) {

        }

        public void SwitchToVDNext() {
            SwitchToVD((focusedIndex + 1) % virtualDesks.size());
        }

        public void SwitchToVDPrev() {
            SwitchToVD((focusedIndex + virtualDesks.size() - 1) % virtualDesks.size());
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
            // channelOut.put(new WindowMessage(window, event));
            // } else if (event == WindowEvent.MoveEnd) {
            // // ResizeOrSwapWindow();
            // }
            // vd.UpdateWindow(window, event);
            // // eventWindowUpdated
            //
            // channelOut.put(new WindowMessage(window, event));
            // }
            // }
        }
        // public static
    }

    private class MessageLoop extends Thread {
        public MessageLoop() {
            super();
            setName("VDMan Thread");
        }

        @Override
        public void run() {
            while (true) {
                Message msg = channelIn.take();
                if (msg == null) {
                    // Exit
                    logger.info("VDMan.MessageLoop, Exit");
                    var source = virtualDesks.get(0);
                    for (int i = 1; i < virtualDesks.size(); i++)
                        ActionWindow.MoveAllWindows(virtualDesks.get(i), source);
                    Display.SwitchToVD(0);
                    return;
                } else if (msg instanceof WindowMessage) {
                    var wmsg = (WindowMessage) msg;
                    logger.info("VDMan.MessageLoop, WindowMessage, {}, {}", wmsg.event, wmsg.window);
                    switch (wmsg.event) {
                        case Add:
                            ActionWindow.WindowAdd(wmsg.window);
                            break;
                        case Remove:
                            ActionWindow.WindowRemove(wmsg.window);
                            break;
                        default:
                            Display.WindowUpdate(wmsg.window, wmsg.event);
                            break;
                    }
                } else if (msg instanceof VirtualDeskMessage) {
                    var vdmsg = (VirtualDeskMessage) msg;
                    logger.info("VDMan.MessageLoop, VirtualDeskMessage, {}", vdmsg.event);
                    switch (vdmsg.event) {
                        case SwitchToVirtualDesk: {
                            int index = ((Integer) vdmsg.param).intValue();
                            Display.SwitchToVD(index);
                            break;
                        }
                        case SwitchToNextVirtualDesk: {
                            Display.SwitchToVDNext();
                            break;
                        }
                        case SwitchToPrevVirtualDesk: {
                            Display.SwitchToVDPrev();
                            break;
                        }
                        case FocusedWindowClose: {
                            var lastFocused = virtualDesks.get(focusedIndex).lastFocused;
                            if (lastFocused != null)
                                lastFocused.Action.SendClose();
                            break;
                        }
                        case FocusedWindowMinimize: {
                            var lastFocused = virtualDesks.get(focusedIndex).lastFocused;
                            if (lastFocused != null)
                                lastFocused.Action.ShowMinimized();
                            break;
                        }
                        case TurnWindowLeft: {
                            var focusdVD = virtualDesks.get(focusedIndex);
                            focusdVD.Action.TurnWindowLeft();
                            break;
                        }
                        case TurnWindowRight: {
                            var focusdVD = virtualDesks.get(focusedIndex);
                            focusdVD.Action.TurnWindowRight();
                            break;
                        }
                        case TurnWindowUp: {
                            var focusdVD = virtualDesks.get(focusedIndex);
                            focusdVD.Action.TurnWindowUp();
                            break;
                        }
                        case TurnWindowDown: {
                            var focusdVD = virtualDesks.get(focusedIndex);
                            focusdVD.Action.TurnWindowDown();
                            break;
                        }
                        // TODO:NOTTESTED
                        case ResetLayout: {
                            var focusdVD = virtualDesks.get(focusedIndex);
                            focusdVD.ResetLayout();
                            break;
                        }
                        // TODO:NOTTESTED
                        case MoveWindowToVirtualDesk: {
                            int index = ((Integer) vdmsg.param).intValue();
                            ActionWindow.MoveFocusedWindowToVD(index);
                            break;
                        }
                        // TODO:NOTTESTED
                        case MoveWindowToPrevVirtualDesk: {
                            ActionWindow.MoveFocusedWindowToVDPrev();
                            break;
                        }
                        // TODO:NOTTESTED
                        case MoveWindowToNextVirtualDesk: {
                            ActionWindow.MoveFocusedWindowToVDNext();
                            break;
                        }
                        // TODO:FOR TEST
                        case VDDebugInfo: {
                            logger.info("VDDebugInfo Start");
                            for (var vd : context.vdMan.virtualDesks) {
                                System.out.println(
                                        "Begin: " + vd.GetName() + ", size = " + String.valueOf(vd.allWindows.size()));
                                System.out.println("AllWindows: " + String.valueOf(vd.allWindows.size()));
                                for (var w : vd.allWindows) {
                                    System.out.println("handle: " + w.handle);
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
                        // TODO:
                        // ,,
                        // , , ToggleTiling,
                        // FocusedWindowMaximize,
                        default:
                            break;
                    }
                } else if (msg instanceof MonitorMessage) {
                    monitors = Monitor.GetMonitors();
                    virtualDesks.get(0).SetScreen(monitors.get(0).GetWorkingRect());
                }
            }
        }
    }
}
