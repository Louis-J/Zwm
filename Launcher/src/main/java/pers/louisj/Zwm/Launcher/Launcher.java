package pers.louisj.Zwm.Launcher;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Message.MonitorMessage.MonitorMessage;
import pers.louisj.Zwm.Core.Message.VirtualDeskMessage.VirtualDeskEvent;
import pers.louisj.Zwm.Core.Message.VirtualDeskMessage.VirtualDeskMessage;

import org.louisj.Zwm.SysTray.MenuItem;
import org.louisj.Zwm.SysTray.SysTray;
import org.louisj.Zwm.SysTray.SysTrayDll;
// import pers.louisj.Zwm.Core.Context;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public class Launcher {
    private static Logger logger;

    static {
        System.setProperty("Time4File", String.valueOf(new Date().getTime()));
        logger = LogManager.getLogger("Main");
    }

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
        context.hookMan.Init();
        context.hookMan.Start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        context.vdMan.inputChan.put(new MonitorMessage());
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
                ByteBuffer iconBuf = iconPtr.getByteBuffer(0, iconArr.length);
                SysTray.SetIcon(iconBuf, iconArr.length);
                // Memory.disposeAll();

                SysTray.SetTooltip("Zwm");
                SysTray.AddMenu("Desk 1", "Go to Desk 1", (itemIndex) -> {
                    logger.info("Main Menu Click 1");
                    context.vdMan.inputChan
                            .put(new VirtualDeskMessage(VirtualDeskEvent.SwitchToVirtualDesk, Integer.valueOf(0)));
                    logger.info("Main Menu Click 1 End");
                });
                SysTray.AddMenu("Desk 2", "Go to Desk 2", (itemIndex) -> {
                    logger.info("Main Menu Click 2");
                    context.vdMan.inputChan
                            .put(new VirtualDeskMessage(VirtualDeskEvent.SwitchToVirtualDesk, Integer.valueOf(1)));
                    logger.info("Main Menu Click 2 End");
                });
                SysTray.AddMenu("Desk 3", "Go to Desk 3", (itemIndex) -> {
                    logger.info("Main Menu Click 3");
                    context.vdMan.inputChan
                            .put(new VirtualDeskMessage(VirtualDeskEvent.SwitchToVirtualDesk, Integer.valueOf(2)));
                    logger.info("Main Menu Click 3 End");
                });
                SysTray.AddMenu("Desk 4", "Go to Desk 4", (itemIndex) -> {
                    logger.info("Main Menu Click 4");
                    context.vdMan.inputChan
                            .put(new VirtualDeskMessage(VirtualDeskEvent.SwitchToVirtualDesk, Integer.valueOf(3)));
                    logger.info("Main Menu Click 4 End");
                });

                SysTray.AddMenu("Show Desks", "Show Information of Desks", (itemIndex) -> {
                    logger.info("Main Menu Click 5");
                    for (var vd : context.vdMan.virtualDesks) {
                        System.out
                                .println("Begin: " + vd.GetName() + ", size = " + String.valueOf(vd.allWindows.size()));
                        System.out.println("AllWindows: " + String.valueOf(vd.allWindows.size()));
                        for (var w : vd.allWindows) {
                            System.out.println("handle: " + Pointer.nativeValue(w.handle.getPointer()));
                            System.out.println("pid: " + w.processId);
                            System.out.println("name: " + w.processName);
                            System.out.println("class: " + w.windowClass);
                            System.out.println("title: " + w.windowTitle);
                            System.out.println();
                        }
                        System.out.println("End: " + vd.GetName() + "\n");
                    }
                    logger.info("Main Menu Click 5 End");
                });

                SysTray.AddMenu("Quit", "Quit the whole app", (itemIndex) -> {
                    logger.info("Main Menu Click 6");
                    SysTray.Quit();
                    context.Exit();
                    context.Defer();
                    logger.info("Main Menu Click 6 End");
                });
            }
        };
        SysTray.Run(onReady, null);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Main ShutdownHook Called");
            // context.Defer();
        }));
        // start message looper on main thread
        logger.info("Run message loop in Main");
        context.Start();
    }
}
