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

    public Channel<Message> inputChan = new Channel<>(512);
    public List<Channel<Message>> eventChans = new ArrayList<>();
    private Thread messageLoop = new MessageLoop();

    public ArrayList<Monitor> monitors;

    public VirtualDeskManager(Context context) {
        this.context = context;
    }

    public void CreateVirtualDesks(String[] names) {
        for (var name : names) {
            CreateVirtualDesk(name);
        }
    }

    public void CreateVirtualDesk(String name) {
        virtualDesks.add(new VirtualDesk(name, null, null));
    }

    public byte RemoveVirtualDesk(VirtualDesk vd) {
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
        for (var e : eventChans)
            e.put(new VirtualDeskMessage(VirtualDeskEvent.VirtualDeskUpdated, null));
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

            focusedIndex = index;

            // eventVirtualDeskUpdated
            for (var e : eventChans)
                e.put(new VirtualDeskMessage(VirtualDeskEvent.VirtualDeskUpdated, null));
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

            // eventVirtualDeskUpdated
            for (var e : eventChans)
                e.put(new VirtualDeskMessage(VirtualDeskEvent.VirtualDeskUpdated, null));
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

        // eventWindowRemoved
        for (var e : eventChans)
            e.put(new WindowMessage(window, WindowEvent.Remove));

        return window;
    }

    public void MoveAllWindows(VirtualDesk source, VirtualDesk target) {
        target.allWindows.addAll(source.allWindows);
        for (var w : source.GetLayoutWindows())
            target.AddWindow(w, true);
        for (var w : source.allWindows)
            windowsToVirtualDesk.put(w, target);

        // eventVirtualDeskUpdated
        for (var e : eventChans)
            e.put(new VirtualDeskMessage(VirtualDeskEvent.VirtualDeskUpdated, null));
    }

    public void AddWindow(Window window) {
        // logger.info("AddWindow, {}", window);
        if (context.vdFilter.CheckIgnore(window))
            return;

        logger.info("AddWindow, {}", window);

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
        for (var e : eventChans)
            e.put(new WindowMessage(window, WindowEvent.Add));
    }

    public void RemoveWindow(Window window) {
        logger.info("RemoveWindow, {}", window);
        VirtualDesk vd = windowsToVirtualDesk.get(window);
        if (vd != null) {
            vd.RemoveWindow(window);
            windowsToVirtualDesk.remove(window);

            // eventWindowRemoved
            for (var e : eventChans)
                e.put(new WindowMessage(window, WindowEvent.Remove));
        }
    }

    public void UpdateWindow(Window window, WindowEvent event) {
        logger.info("UpdateWindow, {}, {}", window, event);
        VirtualDesk vd = windowsToVirtualDesk.get(window);
        if (vd != null) {
            if (vd == virtualDesks.get(focusedIndex)) {
                if (event == WindowEvent.Foreground) {
                    // eventVirtualDeskUpdated
                    for (var e : eventChans)
                        e.put(new WindowMessage(window, event));
                } else if (event == WindowEvent.MoveEnd) {
                    // ResizeOrSwapWindow();
                }
                vd.UpdateWindow(window, event);
                // eventWindowUpdated
                for (var e : eventChans)
                    e.put(new WindowMessage(window, event));
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

    public void Init(Collection<Window> collection) {
        // logger.info("Init, {}", collection);

        for (var w : collection) {
            AddWindow(w);
        }
    }

    public void Defer() {
        logger.info("VirtualDeskManager Defer Start");

        inputChan.put(null);

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

    class ModelInner {
        Window MoveFocusedWindowOut() {
            return null;
        }

        void MoveAllWindows(VirtualDesk source, VirtualDesk target) {
        }

        public void AddWindowToVD(Window window, VirtualDesk target) {
        }
    }

    class Model {
        public void VDCreate(String name, VirtualDeskRouter router, ILayout layout) {

        }

        public void VDRemove(String name) {
        }

        public void WindowAdd(Window window) {
        }

        public void WindowRemove(Window window) {
        }

        public void MoveFocusedWindowToVD(int index) {
        }

        public void MoveFocusedWindowToVDNext(int index) {
        }

        public void MoveFocusedWindowToVDPrev(int index) {
        }

        public VirtualDeskState GetState() {
            return null;
        }

    }

    class DisplayInner {
        public Monitor focusedMonitor;

        public Monitor GetMonitorAtPoint(int x, int y) {
            return null;
        }
    }

    class Display {
        public void UpdateWindow(Window window, WindowEvent event) {
        }

        public void SwitchToVD(int index) {

        }

        public void SwitchMonitorToVD(int mindex, int vdindex) {

        }

        public void SwitchToVDNext() {
            // SwitchTo((focusedIndex + 1) % virtualDesks.size());
        }

        public void SwitchToVDPrev() {
        }

        public void FocusToPoint(int x, int y) {
        }

        public void FocusedWindowUpdate(int event) {

        }

        public void WindowUpdate(Window window, int event) {

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
                Message msg = inputChan.take();
                if (msg == null) {
                    logger.info("VDMan.MessageLoop, Exit");
                    var source = virtualDesks.get(0);
                    for (int i = 1; i < virtualDesks.size(); i++)
                        MoveAllWindows(virtualDesks.get(i), source);
                    SwitchToVirtualDesk(0);
                    // Exit
                    return;
                } else if (msg instanceof WindowMessage) {
                    var wmsg = (WindowMessage) msg;
                    logger.info("VDMan.MessageLoop, WindowMessage, {}", wmsg.event);
                    switch (wmsg.event) {
                        case Add:
                            AddWindow(wmsg.window);
                            break;
                        case Remove:
                            RemoveWindow(wmsg.window);
                            break;
                        // case MoveEnd:
                        // EndWindowMove(wmsg.window);
                        // break;
                        default:
                            UpdateWindow(wmsg.window, wmsg.event);
                            break;
                    }
                } else if (msg instanceof VirtualDeskMessage) {
                    logger.info("VDMan.MessageLoop, VirtualDeskMessage");
                    var vdmsg = (VirtualDeskMessage) msg;
                    switch (vdmsg.event) {
                        case SwitchToVirtualDesk:
                            int index = ((Integer) vdmsg.param).intValue();
                            SwitchToVirtualDesk(index);
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
