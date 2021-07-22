package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.ILayout;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessage;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageFocus;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageMonitors;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageRefresh;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageTitle;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageVDs;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.L2.VirtualDesk.LayoutFilter;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.Utils.Async.ChannelList;
import pers.louisj.Zwm.Core.Utils.Types.Pair;
import pers.louisj.Zwm.Core.Utils.Types.Point;

interface DefaultVDFunc {
    VirtualDesk Invoke();
}


public class VirtualDeskManager {
    protected static Logger logger = LogManager.getLogger("VirtualDeskManager");
    protected Context context;

    public Map<Window, VirtualDesk> windowsToVirtualDesk = new HashMap<Window, VirtualDesk>();

    public List<VirtualDesk> virtualDesks = new ArrayList<>();
    protected int focusedIndex = 0;

    protected DefaultVDFunc defaultVDFunc = () -> {
        return new VirtualDesk("more", null, null);
    };

    public LayoutFilter filterLayout = new LayoutFilter();
    public VirtualDeskRouterMan routerMan = new VirtualDeskRouterMan();

    public ChannelList<PluginMessage> channelOutFocus = new ChannelList<>();
    public ChannelList<PluginMessage> channelOutRefresh = new ChannelList<>();
    public ChannelList<PluginMessage> channelOutMonitors = new ChannelList<>();
    public ChannelList<PluginMessage> channelOutTitle = new ChannelList<>();
    public ChannelList<PluginMessage> channelOutVDs = new ChannelList<>();

    // Action Packages
    public ActionInVDImpl ActionInVD = new ActionInVDImpl();

    public ActionGlobalImpl ActionGlobal = new ActionGlobalImpl();

    public QueryImpl Query = new QueryImpl();

    public VirtualDeskManager(Context context) {
        this.context = context;
    }

    public void Start() {
        ActionGlobal.RefreshMonitors();
    }

    public void Exit() {
        logger.info("VDMan, Exit");
        var target = Query.GetFocusedVD();
        for (var vd : virtualDesks) {
            if (vd.monitor == null)
                ActionInVD.MoveAllWindowsTo(vd, target);
        }
    }

    @SuppressWarnings("unchecked")
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

            case RefreshMonitors: {
                ActionGlobal.RefreshMonitors();
                break;
            }

            case SwitchToVD: {
                int index = ((Integer) msg.param).intValue();
                if (index == -2) // Next VD
                    index = (focusedIndex + 1) % virtualDesks.size();
                else if (index == -1) // Prev VD
                    index = (focusedIndex + virtualDesks.size() - 1) % virtualDesks.size();
                ActionGlobal.VDSwitchTo(index);
                break;
            }
            case SwitchMonitorToVD: {
                var param = (Pair<Monitor, Integer>) msg.param;
                var monitor = param.t1;
                int index = param.t2.intValue();

                ActionGlobal.VDSwitchToMonitor(monitor, index);
                break;
            }

            case FocusedWindowMoveTo: {
                int index = ((Integer) msg.param).intValue();
                if (index == -2) // Next VD
                    index = (focusedIndex + 1) % virtualDesks.size();
                else if (index == -1) // Prev VD
                    index = (focusedIndex + virtualDesks.size() - 1) % virtualDesks.size();
                ActionInVD.FocusedWindowMoveTo(index);
                break;
            }

            case WindowTitleChange: {
                var window = (Window) msg.param;
                channelOutTitle.put(new PluginMessageTitle(window));

                VirtualDesk vd = windowsToVirtualDesk.get(window);
                if (vd.lastFocused != window)
                    break;
                channelOutRefresh.put(new PluginMessageRefresh(vd));
                break;
            }

            case WindowForeground: {
                ActionInVD.WindowForeground((Window) msg.param);
                break;
            }

            case MonitorForeground: {
                ActionInVD.MonitorForeground((Point) msg.param);
                break;
            }

            case WindowMoveResize: {
                ActionInVD.WindowMoveResize((Window) msg.param);
                break;
            }
            case WindowMinimizeStart: {
                ActionInVD.WindowToggleMinimize((Window) msg.param, true);
                break;
            }
            case WindowMinimizeEnd: {
                ActionInVD.WindowToggleMinimize((Window) msg.param, false);
                break;
            }
            // TODO: Impl
            case VDAdd:
            case VDRemove:
        }
    }

    public class QueryImpl {
        public Monitor GetFocusedMonitor() {
            return virtualDesks.get(focusedIndex).monitor;
        }

        public VirtualDesk GetFocusedVD() {
            return virtualDesks.get(focusedIndex);
        }

        public Window GetFocusedWindow() {
            return virtualDesks.get(focusedIndex).lastFocused;
        }
    }

    public class ActionInVDImpl {
        private Window MoveFocusedWindowOut() {
            logger.info("MoveFocusedWindowOut");
            var focusedVd = Query.GetFocusedVD();
            var window = Query.GetFocusedWindow();
            if (window == null)
                return null;

            focusedVd.ActionVD.WindowRemove(window);
            windowsToVirtualDesk.remove(window);

            return window;
        }

        public void WindowToggleMinimize(Window window, boolean isMinimize) {
            VirtualDesk vd = windowsToVirtualDesk.get(window);
            vd.ActionLayout.WindowToggleMinimize(window, isMinimize);
            if (!isMinimize && window.Query.IsFocused()) {
                vd.lastFocused = window;
                if (vd.monitor != null) {
                    if (vd != Query.GetFocusedVD())
                        ActionGlobal.VDSwitchTo(virtualDesks.indexOf(vd));
                    else
                        channelOutRefresh.put(new PluginMessageRefresh(vd));
                }
            }
        }

        public void WindowMoveResize(Window window) {
            VirtualDesk vd = windowsToVirtualDesk.get(window);
            if (vd.monitor == null) {
                logger.info("WindowMoveResize, Window in a cloaked vd, window = {}", window);
                return;
            }
            vd.ActionLayout.WindowMoveResize(window);
        }

        private void MoveAllWindowsTo(VirtualDesk source, VirtualDesk target) {
            target.allWindows.addAll(source.allWindows);
            for (var w : source.GetLayoutWindows()) {
                target.ActionVD.WindowAdd(w);
                w.Action.ShowNoActive();
            }
            for (var w : source.allWindows)
                windowsToVirtualDesk.put(w, target);
        }

        public void WindowAddInit(List<Window> windows) {
            var focusedVD = Query.GetFocusedVD();
            for (var window : windows) {
                var canLayout = !filterLayout.CheckMatch(window);
                logger.info("WindowAddInit, {}, {}", canLayout, window);
                window.Action.SetCanLayout(canLayout);

                VirtualDesk target = routerMan.CheckRouter(window);
                if (target == null) {
                    var monitor = Monitor.GetMonitorByWindow(window);
                    if (monitor != null)
                        target = monitor.vd;
                    else
                        target = focusedVD;
                }
                target.ActionVD.WindowAdd(window);
                target.lastFocused = window;
                windowsToVirtualDesk.put(window, target);
            }
            for (var vd : virtualDesks)
                channelOutRefresh.put(new PluginMessageRefresh(vd));
            channelOutFocus.put(new PluginMessageFocus(focusedVD));
        }

        public void WindowAdd(Window window) {
            var canLayout = !filterLayout.CheckMatch(window);
            logger.info("WindowAdd, {}, {}", canLayout, window);
            window.Action.SetCanLayout(canLayout);
            var forew = WindowStaticAction.GetForegroundWindow();
            boolean isForeground = window.hWnd.equals(forew);

            VirtualDesk focusedVD = Query.GetFocusedVD();
            VirtualDesk target = routerMan.CheckRouter(window);
            if (target == null)
                target = focusedVD;
            target.ActionVD.WindowAdd(window);
            target.lastFocused = window;
            windowsToVirtualDesk.put(window, target);

            if (isForeground && target != focusedVD)
                ActionGlobal.VDSwitchTo(virtualDesks.indexOf(target));

            channelOutRefresh.put(new PluginMessageRefresh(focusedVD));
        }

        public void WindowRemove(Window window) {
            logger.info("WindowRemove, {}", window);
            VirtualDesk vd = windowsToVirtualDesk.get(window);
            vd.ActionVD.WindowRemove(window);
            windowsToVirtualDesk.remove(window);

            if (vd.monitor != null)
                channelOutRefresh.put(new PluginMessageRefresh(vd));
        }

        public void WindowForeground(Window window) {
            VirtualDesk vd = windowsToVirtualDesk.get(window);
            int index = virtualDesks.indexOf(vd);
            vd.lastFocused = window;
            if (vd.monitor == null) {
                logger.info("WindowForeground, Window in a cloaked vd, window = {}", window);
                ActionGlobal.VDSwitchTo(index);
                return;
            }
            if (focusedIndex != index) {
                focusedIndex = index;
                channelOutFocus.put(new PluginMessageFocus(vd));
            } else
                channelOutRefresh.put(new PluginMessageRefresh(vd));
        }

        public void MonitorForeground(Point point) {
            var m = Monitor.GetMonitorByPoint(point.x, point.y);
            var index = virtualDesks.indexOf(m.vd);
            if (focusedIndex == index) {
                Query.GetFocusedVD().lastFocused = null;
                channelOutRefresh.put(new PluginMessageRefresh(m.vd));
            } else {
                focusedIndex = index;
                m.vd.lastFocused = null;
                channelOutFocus.put(new PluginMessageFocus(m.vd));
            }
        }

        public void FocusedWindowMoveTo(int vdIndex) {
            var target = virtualDesks.get(vdIndex);
            if (target != null) {
                var window = MoveFocusedWindowOut();
                if (window == null)
                    return;

                target.ActionVD.WindowAdd(window);
                target.lastFocused = window;
                windowsToVirtualDesk.put(window, target);

                if (target.monitor == null) {
                    if (window.Action.Unfocus() == false)
                        ActionGlobal.VDSwitchTo(vdIndex);
                    else
                        window.Action.Hide();
                } else {
                    focusedIndex = vdIndex;
                    logger.info("FocusedWindowMoveTo, PluginMessageFocus");
                    channelOutFocus.put(new PluginMessageFocus(target));
                }
            }
        }
    }

    public class ActionGlobalImpl {
        protected void VDAdd(VirtualDesk vd) {
            virtualDesks.add(vd);
            routerMan.Add(vd, vd.router);
        }

        public void VDCreate(String name, VirtualDeskRouter router, ILayout layout) {
            var vd = new VirtualDesk(name, router, layout);
            virtualDesks.add(vd);
            routerMan.Add(vd, vd.router);
        }

        public void VDSwitchTo(int vdindex) {
            logger.info("VDSwitchTo, {}", vdindex);
            if (focusedIndex != vdindex) {
                var sourceVd = Query.GetFocusedVD();
                var sourceM = sourceVd.monitor;
                var targetVd = virtualDesks.get(vdindex);
                var targetM = targetVd.monitor;

                if (targetM != null) {
                    sourceVd.ActionVD.Enable(targetM);
                    targetM.vd = sourceVd;
                } else
                    sourceVd.ActionVD.Disable();
                targetVd.ActionVD.Enable(sourceM);
                sourceM.vd = targetVd;
                targetVd.ActionVD.Focus();

                focusedIndex = vdindex;

                if (targetM == null)
                    channelOutVDs.put(new PluginMessageVDs(targetVd, null));
                else
                    channelOutVDs.put(new PluginMessageVDs(targetVd, sourceVd));
            }
            logger.info("VDSwitchTo, END");
        }

        // TODO:
        public void VDSwitchToMonitor(Monitor monitor, int vdindex) {
            logger.info("VDSwitchToMonitor, {}", vdindex);
            var sourceM = monitor;
            var sourceVd = monitor.vd;
            var targetVd = virtualDesks.get(vdindex);
            var targetM = targetVd.monitor;

            if (sourceVd != targetVd) {
                if (targetM != null && targetM != sourceM) {
                    sourceVd.ActionVD.Enable(targetM);
                    targetM.vd = sourceVd;
                } else
                    sourceVd.ActionVD.Disable();
                targetVd.ActionVD.Enable(sourceM);
                sourceM.vd = targetVd;
                if (Query.GetFocusedVD() == sourceVd) {
                    focusedIndex = vdindex;
                    targetVd.ActionVD.Focus();
                }

                if (targetM == null)
                    channelOutVDs.put(new PluginMessageVDs(targetVd, null));
                else
                    channelOutVDs.put(new PluginMessageVDs(targetVd, sourceVd));
            }
            logger.info("VDSwitchTo, END");
        }

        public void RefreshMonitors() {
            Monitor.RefreshMonitors();
            var monitors = Monitor.GetMonitors();
            while (monitors.size() > virtualDesks.size()) {
                ActionGlobal.VDAdd(defaultVDFunc.Invoke());
            }
            for (var vd : virtualDesks) {
                if (!monitors.contains(vd.monitor))
                    vd.ActionVD.Disable();
                else
                    vd.ActionVD.Enable(vd.monitor);
            }
            var it = virtualDesks.iterator();
            for (var m : monitors) {
                if (m.vd == null) {
                    VirtualDesk vd;
                    for (vd = it.next(); vd.monitor != null; vd = it.next()) {
                    }
                    vd.ActionVD.Enable(m);
                    m.vd = vd;
                }
            }
            channelOutMonitors
                    .put(new PluginMessageMonitors(new ArrayList<VirtualDesk>(virtualDesks)));
        }
    }
}
