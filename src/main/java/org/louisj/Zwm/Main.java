package org.louisj.Zwm;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import com.sun.jna.Callback;
// import com.sun.jna.Platform;
// import com.sun.jna.win32;

public class Main {
    private static Logger logger;

    static {
        System.setProperty("Time4File", String.valueOf(new Date().getTime()));
        logger = LogManager.getLogger("Main");
    }

    public static void main(String[] args) {

        // // init config
        // Context context = ConfigHelper.Config();
        // if(context == null)
        // context = new Context();
        Context context;
        context = new Context();
        context.DefaultConfig();

        // // init windowhook
        // context.winhookMan.Init();
        // context.winhookMan.Start();

        // // load state after restart
        // var state = context.LoadState();
        // if (state != null) {
        // context.Workspaces.InitializeWithState(state.WorkspaceState,
        // context.Windows.Windows);
        // context.Enabled = true;
        // } else {
        // context.Workspaces.Initialize(context.Windows.Windows);
        // context.Enabled = true;
        // context.Workspaces.SwitchToWorkspace(0);
        // }

        // // force first layout
        // for (var workspace : context.WorkspaceContainer.GetAllWorkspaces()) {
        // workspace.DoLayout();
        // }

        // // notify plugins that config is done
        // context.Plugins.BeforeRun();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Main ShutdownHook Called");
            context.Defer();
        }));
        // start message looper on main thread
        logger.info("Run message loop in Main");
        context.mainloop.run();
    }
}
