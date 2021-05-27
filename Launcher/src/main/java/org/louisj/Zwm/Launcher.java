package org.louisj.Zwm;

import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.louisj.Zwm.SysTray.MenuItem;
// import org.louisj.Zwm.Context;
import org.louisj.Zwm.SysTray.SysTray;
import org.louisj.Zwm.SysTray.SysTrayDll;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public class Launcher {
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
        SysTray.Init();

        Context context;
        context = new Context();
        context.DefaultConfig();

        context.vdMan.CreateVirtualDesks(new String[] { "1", "2", "3", "4" });

        context.vdFilter.Build();

        // // init windowhook
        context.winhookMan.Init();
        context.winhookMan.Start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        context.vdMan.Init(context.winhookMan.windows.values());
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

        // SysTray.Init();
        var onReady = new SysTrayDll.SigFunc0() {
            public void Invoke() {
                var iconArr = IconData.GetIconData();
                Memory iconPtr = new Memory(iconArr.length);
                iconPtr.write(0, iconArr, 0, iconArr.length);
                // ByteBuffer iconBuf = ByteBuffer.allocateDirect(iconArr.length);
                // iconBuf.clean()
                ByteBuffer iconBuf = iconPtr.getByteBuffer(0, iconArr.length);
                SysTray.SetIcon(iconBuf, iconArr.length);
                // iconPtr.dispose();

                SysTray.SetTooltip("Zwm");
                SysTray.AddMenu("Desk 1", "Go to Desk 1", (itemIndex) -> {
                    // var item = new MenuItem(itemIndex);
                    context.vdMan.SwitchToVirtualDesk(0);
                    logger.info("Main Menu Click 1");
                });
                SysTray.AddMenu("Desk 2", "Go to Desk 2", (itemIndex) -> {
                    context.vdMan.SwitchToVirtualDesk(1);
                    logger.info("Main Menu Click 2");
                });
                SysTray.AddMenu("Desk 3", "Go to Desk 3", (itemIndex) -> {
                    context.vdMan.SwitchToVirtualDesk(2);
                    logger.info("Main Menu Click 3");
                });
                SysTray.AddMenu("Desk 4", "Go to Desk 4", (itemIndex) -> {
                    context.vdMan.SwitchToVirtualDesk(3);
                    logger.info("Main Menu Click 4");
                });

                SysTray.AddMenu("Show Desks", "Show Information of Desks", (itemIndex) -> {
                    for (var vd : context.vdMan.virtualDesks) {
                        System.out.println("Begin: " + vd.name + ", size = " + String.valueOf(vd.allWindows.size()));
                        // System.out.println("AllWindows: " + String.valueOf(vd.allWindows.size()));
                        // for (var w : vd.allWindows) {
                        // System.out.println("name: " + w.processName);
                        // System.out.println("class: " + w.windowClass);
                        // System.out.println("title: " + w.windowTitle);
                        // System.out.println();
                        // }
                        // System.out.println("End: " + vd.name);
                    }
                    logger.info("Main Menu Click 4");
                });

                // System.out.println("onReady invoked 2");
                SysTray.AddMenu("Quit", "Quit the whole app", (itemIndex) -> {
                    SysTray.Quit();
                    context.Exit();
                    logger.info("Main Menu Click 5");
                });
                // System.out.println("onReady invoked 3");
            }
        };
        SysTray.Run(onReady, null);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Main ShutdownHook Called");
            context.Defer();
        }));
        // start message looper on main thread
        logger.info("Run message loop in Main");
        context.Start();
    }
}
