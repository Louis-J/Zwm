package pers.louisj.Zwm.Core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// import com.google.gson.Gson;
import com.sun.jna.platform.win32.User32Util.MessageLoopThread;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Win32Exception;

import pers.louisj.Zwm.Core.KeyBind.KeybindManager;
import pers.louisj.Zwm.Core.Message.Message;
import pers.louisj.Zwm.Core.Message.MonitorMessage.MonitorMessage;
import pers.louisj.Zwm.Core.PluginMan.PluginManager;
import pers.louisj.Zwm.Core.Utils.Channel;
import pers.louisj.Zwm.Core.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.VirtualDeskMan.VirtualDeskFilter;
import pers.louisj.Zwm.Core.VirtualDeskMan.VirtualDeskManager;
import pers.louisj.Zwm.Core.VirtualDeskMan.VirtualDeskState;
import pers.louisj.Zwm.Core.WinApi.WinHelper;
import pers.louisj.Zwm.Core.Window.WindowHookManager;

public class Context {
    private static Logger logger = LogManager.getLogger("Context");

    public KeybindManager keyBindMan = new KeybindManager(this);
    public VirtualDeskManager vdMan = new VirtualDeskManager(this);
    public VirtualDeskFilter vdFilter = new VirtualDeskFilter();

    public PluginManager pluginMan = new PluginManager();
    public WindowHookManager hookMan = new WindowHookManager();

    private MainLoop mainloop = new MainLoop(vdMan.inputChan);

    public Context() {
        hookMan.eventChans.add(vdMan.inputChan);
    }

    public void Start() {
        keyBindMan.Start();
        vdMan.Start();
        mainloop.run();
    }

    public void Exit() {
        mainloop.exit();
    }

    public void Defer() {
        keyBindMan.Defer();
        pluginMan.Defer();
        hookMan.Defer();
        vdMan.Defer();
        logger.info(Channel.writeWaitTimes.get());
    }

    public void DefaultConfig() {
        keyBindMan.DefaultConfig();
        vdFilter.DefaultConfig();

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
    private void SaveState() {
        // var filePath = FileHelper.GetStateFilePath();
        // var json = JsonConvert.SerializeObject(GetState());

        // File.WriteAllText(filePath, json);
    }

    // TODO:
    public VirtualDeskState LoadState() {
        // var filePath = FileHelper.GetStateFilePath();

        // if (!File.Exists(filePath))
        // {
        // return null;
        // }
        // var json = File.ReadAllText(filePath);
        // var state = JsonConvert.DeserializeObject<WorkspacerState>(json);
        // File.Delete(filePath);
        // return state;
        return null;
    }

    // TODO:
    private VirtualDeskState GetState() {
        // return new WorkspacerState() {
        // WorkspaceState = Workspaces.GetState()
        // };
        return null;
    }

    public static class MainLoop extends Thread {
        private volatile int nativeThreadId = 0;
        private Channel<Message> vdInputChan;

        public MainLoop(Channel<Message> vdInputChan) {
            this.vdInputChan = vdInputChan;
            setName("Zwm Main Loop");
        }

        @Override
        public void run() {
            final int WM_DISPLAYCHANGE = 0x007e;
            MSG msg = new WinUser.MSG();

            // Make sure message loop is prepared
            WinHelper.MyUser32Inst.PeekMessage(msg, null, 0, 0, 0);
            nativeThreadId = WinHelper.Kernel32Inst.GetCurrentThreadId();

            int getMessageReturn;
            while ((getMessageReturn = WinHelper.MyUser32Inst.GetMessage(msg, null, 0, 0)) != 0) {
                if (getMessageReturn != -1) {
                    logger.info("Main Loop Run, {}", getMessageReturn);
                    // Normal processing
                    if (msg.message == WM_DISPLAYCHANGE) {
                        vdInputChan.put(new MonitorMessage());
                        continue;
                    }
                    WinHelper.MyUser32Inst.TranslateMessage(msg);
                    WinHelper.MyUser32Inst.DispatchMessage(msg);
                } else {
                    // Error case
                    logger.error(new Win32Exception(WinHelper.Kernel32Inst.GetLastError()).toString());
                }
            }
            logger.info("Main Loop Run Over");
        }

        public void exit() {
            WinHelper.MyUser32Inst.PostThreadMessage(nativeThreadId, WinUser.WM_QUIT, null, null);
        }
    }

}
