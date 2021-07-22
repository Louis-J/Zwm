package pers.louisj.Zwm.SaveLoad;

import java.io.File;
import java.util.List;
import com.google.gson.Gson;
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
import pers.louisj.Zwm.Core.Utils.ConfigHelper.ConfigPathHelper;

public class SaveLoad implements IPlugin {
    protected Gson gson = new Gson();
    protected File file = ConfigPathHelper.GetFile("State.json");

    @Override
    public void Init(Context context) {
        // java.lang.Class.typeVarBounds()
        // java.lang.ref.Reference.clear0()
        // java.lang.ClassLoader.getResources("haha");
        context.mainloop.hooks.add(new MessageHook() {
            @Override
            public boolean Invoke(Message msg) {
                if (msg == null) { // Before Exit
                    // Save to State.json
                    Character[] stateSet = Character.Save(context.vdMan.virtualDesks);
                    ConfigPathHelper.SetFileContent(file, gson.toJson(stateSet));
                }
                return false;
            }
        });
        if (file.exists()) {
            Character[] stateGet =
                    gson.fromJson(ConfigPathHelper.GetFileContent(file), Character[].class);
            Character.Load(stateGet);
            context.mainloop.hooks.add(new MessageHook() {
                // Hook and re-write WindowAddInit method
                @Override
                @SuppressWarnings("unchecked")
                public boolean Invoke(Message msg) {
                    if (msg instanceof VDManMessage
                            && ((VDManMessage) msg).event == VDManEvent.WindowAddInit) {
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

                        // re-writed WindowAddInit method
                        var focusedVD = Query.GetFocusedVD();
                        for (var window : windows) {
                            var canLayout = !filterLayout.CheckMatch(window);
                            logger.info("New WindowAddInit, {}, {}", canLayout, window);
                            window.Action.SetCanLayout(canLayout);

                            VirtualDesk target = null;
                            Boolean isLayout = true;
                            var character = Character.GetAnalogue(window);
                            if (character != null) {
                                target = virtualDesks.get(character.vdIndex);
                                isLayout = character.isLayout;
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
                        context.mainloop.hooks.remove(this);
                        return true;
                    }
                    return false;
                }
            });
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
