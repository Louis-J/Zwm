package pers.louisj.Zwm.Core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.L0.KeyBind.KeybindManager;
import pers.louisj.Zwm.Core.L0.MsgLoop.IMsgLoop;
import pers.louisj.Zwm.Core.L0.MsgLoop.MsgLoopQT;
import pers.louisj.Zwm.Core.L0.SysHook.SysHookManager;
import pers.louisj.Zwm.Core.L1.MainLoop;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskFilter;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskManager;
import pers.louisj.Zwm.Core.L2.Window.DelayedWindowFilter;
import pers.louisj.Zwm.Core.PluginMan.PluginManager;
import pers.louisj.Zwm.Core.Utils.Async.Channel;

public class Context {
    public static Logger logger = LogManager.getLogger("Context");

    public PluginManager pluginMan = new PluginManager(this);
    public VirtualDeskManager vdMan = new VirtualDeskManager(this);
    public VirtualDeskFilter filterVirtualDesk = new VirtualDeskFilter();
    public DelayedWindowFilter filterDelayedWindow = new DelayedWindowFilter();

    public MainLoop mainloop = new MainLoop(this, vdMan);

    public KeybindManager keyBindMan = new KeybindManager(this);
    public SysHookManager hookMan = new SysHookManager(this);
    public IMsgLoop msgloop = new MsgLoopQT(mainloop.channelIn);

    public Context() {
        hookMan.eventChans.add(mainloop.channelIn);
    }

    public void Start() {
        hookMan.Start();
        keyBindMan.Start();
        mainloop.Start();
        msgloop.run();
    }

    public void Exit() {
        msgloop.exit();
    }

    public void Defer() {
        keyBindMan.Defer();
        pluginMan.Defer();
        hookMan.Defer();
        mainloop.Defer();
        msgloop.Defer();
        logger.info(Channel.writeWaitTimes.get());
    }

    public void DefaultConfig() {
        keyBindMan.DefaultConfig();
        filterVirtualDesk.DefaultConfig();

        // SystemTray.AddToContextMenu("enable/disable workspacer", () => Enabled =
        // !Enabled);
        // SystemTray.AddToContextMenu("show/hide keybind help", () =>
        // Keybinds.ShowKeybindDialog());
    }

    // TODO:
    public void Restart() {
        // SaveState();
        // var response = new LauncherResponse()
        // {
        // Action = LauncherAction.Restart,
        // };
        // SendResponse(response);

        // Defer();
    }

    // TODO:
    // private void SaveState() {
    // // var filePath = FileHelper.GetStateFilePath();
    // // var json = JsonConvert.SerializeObject(GetState());

    // // File.WriteAllText(filePath, json);
    // }

    // TODO:
    // public VirtualDeskState LoadState() {
    // // var filePath = FileHelper.GetStateFilePath();

    // // if (!File.Exists(filePath))
    // // {
    // // return null;
    // // }
    // // var json = File.ReadAllText(filePath);
    // // var state = JsonConvert.DeserializeObject<WorkspacerState>(json);
    // // File.Delete(filePath);
    // // return state;
    // return null;
    // }

    // TODO:
    // private VirtualDeskState GetState() {
    // // return new WorkspacerState() {
    // // WorkspaceState = Workspaces.GetState()
    // // };
    // return null;
    // }


}
