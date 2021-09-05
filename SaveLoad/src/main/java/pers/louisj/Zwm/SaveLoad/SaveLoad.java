package pers.louisj.Zwm.SaveLoad;

import java.io.File;
import java.util.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IPlugin;
import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageFocus;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageRefresh;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.L1.MainLoop.MessageHook;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.Utils.ConfigHelper.ConfigPathHelper;
import pers.louisj.Zwm.Core.Utils.Types.Pair;

class WindowAddInitHook implements MessageHook {
    final Context context;
    final Logger logger;
    final int threshold;

    public WindowAddInitHook(Context context, Logger logger, int threshold) {
        this.context = context;
        this.logger = logger;
        this.threshold = threshold;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean Invoke(Message msg) {
        if (msg instanceof VDManMessage && ((VDManMessage) msg).event == VDManEvent.WindowAddInit) {
            // Get Data in vdMan
            var windows = (List<Window>) (((VDManMessage) msg).param);
            var vdMan = context.vdMan;
            var Query = vdMan.Query;
            var filterLayout = vdMan.filterLayout;
            var routerMan = vdMan.routerMan;
            var windowsToVirtualDesk = vdMan.windowsToVirtualDesk;
            var virtualDesks = vdMan.virtualDesks;
            var channelOutRefresh = vdMan.channelOutRefresh;
            var channelOutFocus = vdMan.channelOutFocus;

            // re-writed WindowAddInit action
            var focusedVD = Query.GetFocusedVD();
            for (var window : windows) {
                window.Refresh.RefreshTitle();
                var canLayout = !filterLayout.CheckMatch(window);
                logger.info("New WindowAddInit, {}, {}", canLayout, window);
                window.Action.SetCanLayout(canLayout);

                VirtualDesk target = null;
                Boolean isLayout = true;
                var stateValue = AnalogueRank.GetAnalogue(window, threshold);
                if (stateValue != null) {
                    target = virtualDesks.get(stateValue.vdIndex);
                    isLayout = stateValue.isLayout;
                }

                if (target == null)
                    target = routerMan.CheckRouter(window);
                if (target == null) {
                    var monitor = Monitor.GetMonitorByWindow(window);
                    if (monitor != null)
                        target = monitor.vd;
                    else
                        target = focusedVD;
                }
                target.ActionVD.WindowAdd(window, isLayout);
                target.lastFocused = window;
                windowsToVirtualDesk.put(window, target);
            }
            for (var vd : virtualDesks)
                channelOutRefresh.put(new PluginMessageRefresh(vd));
            channelOutFocus.put(new PluginMessageFocus(focusedVD));

            // Delete this after the first run
            context.mainloop.hookRemove(this);
            return true;
        }
        return false;
    }
}


class WindowAddHook implements MessageHook {
    final Context context;
    final Logger logger;
    final int threshold;

    public WindowAddHook(Context context, Logger logger, int threshold) {
        this.context = context;
        this.logger = logger;
        this.threshold = threshold;
    }

    @Override
    public boolean Invoke(Message msg) {
        if (msg instanceof VDManMessage && ((VDManMessage) msg).event == VDManEvent.WindowAdd) {
            // Get Data in vdMan
            var window = (Window) (((VDManMessage) msg).param);
            window.Refresh.RefreshTitle();
            var vdMan = context.vdMan;
            var Query = vdMan.Query;
            var filterLayout = vdMan.filterLayout;
            var routerMan = vdMan.routerMan;
            var windowsToVirtualDesk = vdMan.windowsToVirtualDesk;
            var virtualDesks = vdMan.virtualDesks;
            var channelOutRefresh = vdMan.channelOutRefresh;
            var ActionGlobal = vdMan.ActionGlobal;

            // re-writed WindowAdd action
            var focusedVD = Query.GetFocusedVD();

            var canLayout = !filterLayout.CheckMatch(window);
            logger.info("New_WindowAdd, {}, {}", canLayout, window);
            window.Action.SetCanLayout(canLayout);
            var forew = WindowStaticAction.GetForegroundWindow();
            boolean isForeground = window.hWnd.equals(forew);

            VirtualDesk target = null;
            Boolean isLayout = true;
            var stateValue = AnalogueRank.GetAnalogue(window, threshold);
            if (stateValue != null) {
                target = virtualDesks.get(stateValue.vdIndex);
                isLayout = stateValue.isLayout;
            }

            if (target == null)
                target = routerMan.CheckRouter(window);
            if (target == null)
                target = focusedVD;
            target.ActionVD.WindowAdd(window, isLayout);
            target.lastFocused = window;
            windowsToVirtualDesk.put(window, target);

            if (isForeground && target.monitor == null)
                ActionGlobal.VDSwitchTo(virtualDesks.indexOf(target));

            channelOutRefresh.put(new PluginMessageRefresh(focusedVD));

            return true;
        }
        return false;
    }
}


class WindowRemoveHook implements MessageHook {
    final Context context;
    final Logger logger;

    public WindowRemoveHook(Context context, Logger logger) {
        this.context = context;
        this.logger = logger;
    }

    @Override
    public boolean Invoke(Message msg) {
        if (msg instanceof VDManMessage && ((VDManMessage) msg).event == VDManEvent.WindowRemove) {
            // Get Data in vdMan
            var window = (Window) (((VDManMessage) msg).param);
            window.Refresh.RefreshTitle();
            var vdMan = context.vdMan;
            var windowsToVirtualDesk = vdMan.windowsToVirtualDesk;
            var virtualDesks = vdMan.virtualDesks;

            // re-writed WindowRemove action
            logger.info("Plus_WindowRemove, {}", window);
            VirtualDesk vd = windowsToVirtualDesk.get(window);
            AnalogueRank.LoadOne(virtualDesks, vd, window);

            return false;
        }
        return false;
    }
}


class DeferHook implements MessageHook {
    final Context context;
    final Gson gson;
    final File file;

    public DeferHook(Context context, Gson gson, File file) {
        this.context = context;
        this.gson = gson;
        this.file = file;
    }

    @Override
    public boolean Invoke(Message msg) {
        if (msg == null) { // Before Exit
            // Save to State.json
            Pair<Character, StateValue>[] stateSet = AnalogueRank.Save(context.vdMan.virtualDesks);
            ConfigPathHelper.SetFileContent(file, gson.toJson(stateSet));

            return false;
        }
        return false;
    }
}


public class SaveLoad implements IPlugin {
    static final int thresholdFirst = 25;
    static final int thresholdThen = 30;

    // protected Gson gson = new Gson();
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    protected File file = ConfigPathHelper.GetFile("State.json");


    @Override
    public void Init(Context context) {
        context.mainloop.hookAdd(new DeferHook(context, gson, file));

        if (file.exists()) {
            Pair<Character, StateValue>[] stateGet =
                    gson.fromJson(ConfigPathHelper.GetFileContent(file),
                            new TypeToken<Pair<Character, StateValue>[]>() {}.getType());
            AnalogueRank.Load(stateGet);

            // Hook and re-write WindowAddInit action
            context.mainloop.hookAdd(new WindowAddInitHook(context, logger, thresholdFirst));

            // Hook and re-write WindowAdd action
            context.mainloop.hookAdd(new WindowAddHook(context, logger, thresholdThen));

            // Hook and re-write WindowRemove action
            context.mainloop.hookAdd(new WindowRemoveHook(context, logger));
        }
    }

    @Override
    public String Name() {
        return "Zwm-SaveLoad-0.0.1";
    }

    @Override
    public String Type() {
        return "SaveLoad";
    }

    @Override
    public void DefultConfig() {}

    @Override
    public void Start() {}

    @Override
    public void Defer() {}

    @Override
    public String OperateJson(String str) {
        return null;
    }

    @Override
    public Object Operate(Object obj) {
        return null;
    }
}
