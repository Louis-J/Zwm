package org.louisj.Zwm;

// import com.google.gson.Gson;
import com.sun.jna.platform.win32.User32Util.MessageLoopThread;

import org.louisj.Zwm.KeyBind.KeybindManager;
import org.louisj.Zwm.Plugin.PluginManager;
import org.louisj.Zwm.VirtualDesk.IMonitorContainer;
import org.louisj.Zwm.VirtualDesk.VirtualDeskContainer;
import org.louisj.Zwm.VirtualDesk.NativeMonitorContainer;
import org.louisj.Zwm.VirtualDesk.VirtualDeskManager;
import org.louisj.Zwm.VirtualDesk.VirtualDeskFilter;
import org.louisj.Zwm.Window.WindowHookManager;

public class Context {

    // static {
    //     gson = new Gson();
    // }
    
    // public KeybindManager Keybinds;
    public VirtualDeskManager vdMan = new VirtualDeskManager(this);
    public VirtualDeskFilter vdFilter = new VirtualDeskFilter();
    public PluginManager pluginMan = new PluginManager();
    public WindowHookManager winhookMan = new WindowHookManager();
    // public WindowsManager windowMan = new WindowsManager();
    public VirtualDeskContainer vdContainer;
    // public WindowRouter WindowRouter;
    // public IMonitorContainer MonitorContainer = new NativeMonitorContainer();

    private MessageLoopThread mainloop = new MessageLoopThread();

    // private System.Timers.Timer _timer = new System.Timers.Timer();
    // private Func<ILayoutEngine[]> _defaultLayouts;
    // private List<Func<ILayoutEngine, ILayoutEngine>> _layoutProxies = new
    // List<Func<ILayoutEngine, ILayoutEngine>>();

    // public Action<string> pipesender;
    // public boolean CanMinimizeWindows = false;

    public Context() {
        // SystemEvents.DisplaySettingsChanged += HandleDisplaySettingsChanged;

        // Workspaces = new WorkspaceManager(this);
        // Keybinds = new KeybindManager(this);

        // WorkspaceContainer = new WorkspaceContainer(this);
        // WindowRouter = new WindowRouter(this);

        // Windows.WindowCreated += Workspaces.AddWindow;
        // Windows.WindowDestroyed += Workspaces.RemoveWindow;
        // Windows.WindowUpdated += Workspaces.UpdateWindow;

        
    }

    public void Start() {
        mainloop.run();
    }

    public void Exit() {
        mainloop.exit();
    }

    public void Defer() {
        pluginMan.Defer();
        winhookMan.Defer();
        vdMan.Defer();
    }

    public void DefaultConfig() {
        vdFilter.DefaultConfig();
        
        winhookMan.eventWindowCreate.add((window)->vdMan.AddWindow(window));
        winhookMan.eventWindowDestroy.add((window)->vdMan.RemoveWindow(window));
        winhookMan.eventWindowUpdate.add((window, updateType)->vdMan.UpdateWindow(window, updateType));

        // vdMan.CreateVirtualDesks(new String[]{"1", "2", "3", "4"});


    // context.WorkspaceContainer.CreateWorkspaces("1", "2", "3", "4", "5");
        // _timer.Elapsed += (s, e) => UpdateActiveHandles();
        // _timer.Interval = 5000;
        // _timer.Enabled = true;

        // _defaultLayouts = () => new ILayoutEngine[] {
        // new TallLayoutEngine(),
        // new FullLayoutEngine(),
        // };

        // // // ignore watcher windows in workspace
        // // WindowRouter.AddFilter((window) => window.ProcessId !=
        // _pipeServer.WatcherProcess.Id);

        // // ignore SunAwtWindows (common in some Sun AWT programs such at JetBrains
        // products), prevents flickering
        // WindowRouter.AddFilter((window) => !window.Class.Contains("SunAwtWindow"));

        // SystemTray.AddToContextMenu("enable/disable workspacer", () => Enabled =
        // !Enabled);
        // SystemTray.AddToContextMenu("show/hide keybind help", () =>
        // Keybinds.ShowKeybindDialog());

    }

    // public Func<ILayoutEngine[]> DefaultLayouts;

    // public void AddLayoutProxy(Func<ILayoutEngine, ILayoutEngine> proxy) {
    // _layoutProxies.Add(proxy);
    // }

    // public IEnumerable<ILayoutEngine> ProxyLayouts(IEnumerable<ILayoutEngine>
    // layouts)
    // {
    // for (var i = 0; i < _layoutProxies.Count; i++)
    // {
    // layouts = layouts.Select(layout => _layoutProxies[i](layout)).ToArray();
    // }
    // return layouts;
    // }

    // public void ToggleConsoleWindow()
    // {
    // var response = new LauncherResponse()
    // {
    // Action = LauncherAction.ToggleConsole,
    // };
    // SendResponse(response);
    // }

    // public void SendLogToConsole(string message)
    // {
    // var response = new LauncherResponse()
    // {
    // Action = LauncherAction.Log,
    // Message = message,
    // };
    // SendResponse(response);
    // }

    // private void SendResponse(LauncherResponse response) {
    // var str = JsonConvert.SerializeObject(response);
    // pipesender(str);
    // }

    // public void Restart()
    // {
    // SaveState();
    // var response = new LauncherResponse()
    // {
    // Action = LauncherAction.Restart,
    // };
    // SendResponse(response);

    // Defer();
    // }

    // public void Quit()
    // {
    // var response = new LauncherResponse()
    // {
    // Action = LauncherAction.Quit,
    // };
    // SendResponse(response);

    // Defer();
    // }

    // public void QuitWithException(Exception e)
    // {
    // var message = e.ToString();
    // var response = new LauncherResponse()
    // {
    // Action = LauncherAction.QuitWithException,
    // Message = message,
    // };
    // SendResponse(response);

    // Defer();
    // }

    // public void Defer()
    // {
    // SystemTray.Defer();
    // Application.Exit();
    // Environment.Exit(0);
    // }

    // private void UpdateActiveHandles()
    // {
    // var response = new LauncherResponse()
    // {
    // Action = LauncherAction.UpdateHandles,
    // ActiveHandles = GetActiveHandles().Select(h => h.ToInt64()).ToList(),
    // };
    // SendResponse(response);
    // }

    // private List<IntPtr> GetActiveHandles()
    // {
    // var list = new List<IntPtr>();
    // if (WorkspaceContainer == null) return list;

    // foreach (var ws in WorkspaceContainer.GetAllWorkspaces())
    // {
    // var handles = ws.ManagedWindows.Select(i => i.Handle);
    // list.AddRange(handles);
    // }
    // return list;
    // }

    // private void HandleDisplaySettingsChanged(object sender, EventArgs e)
    // {
    // SaveState();
    // var response = new LauncherResponse()
    // {
    // Action = LauncherAction.RestartWithMessage,
    // Message = "A display settings change has been detected, which has
    // automatically disabled workspacer. Press 'restart' when ready.",
    // };
    // SendResponse(response);

    // Defer();
    // }

    // public bool Enabled { get; set; }

    // private void SaveState()
    // {
    // var filePath = FileHelper.GetStateFilePath();
    // var json = JsonConvert.SerializeObject(GetState());

    // File.WriteAllText(filePath, json);
    // }

    // public WorkspacerState LoadState()
    // {
    // var filePath = FileHelper.GetStateFilePath();

    // if (!File.Exists(filePath))
    // {
    // return null;
    // }
    // var json = File.ReadAllText(filePath);
    // var state = JsonConvert.DeserializeObject<WorkspacerState>(json);
    // File.Delete(filePath);
    // return state;
    // }

    // private WorkspacerState GetState()
    // {
    // return new WorkspacerState() {
    // WorkspaceState = Workspaces.GetState()
    // };
    // }
}
