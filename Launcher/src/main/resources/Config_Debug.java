import pers.louisj.Zwm.Bar.Bar;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IConfig;
import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.CustomMessage;
import pers.louisj.Zwm.Core.Global.Message.CustomMessage.CallBack;
import pers.louisj.Zwm.Core.L0.KeyBind.KeyCode;
import pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts.GridLayout;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouter;
import pers.louisj.Zwm.Core.Utils.Async.Channel;

public class Config implements IConfig {
    static {
        String qtDir = System.getProperty("user.dir") + "\\..\\mymin-lib-6.1\\";
        String envPath = System.getProperty("java.library.path");
        System.setProperty("java.library.path", qtDir + "bin;" + envPath);
    }
    @Override
    public Context GetContext() {
        Context context = new Context();

        context.keyBindMan.DefaultConfig();
        ConfigCustomKey(context);
        // filterVirtualDesk
        context.filterVirtualDesk.DefaultConfig();
        // For Debug
        context.filterVirtualDesk.MatchClass("MozillaWindowClass"); // Ignore Firefox for Debug
        context.filterVirtualDesk.MatchClass("Chrome_WidgetWin_1"); // Ignore Visual Studio Code for
                                                                    // // Debug
        context.filterVirtualDesk.MatchClass("SunAwtFrame"); // Ignore IDEA for Debug
        context.filterVirtualDesk.MatchName("\\bin\\java.exe");

        context.filterVirtualDesk.MatchClass("RCLIENT"); // App: Lol

        // filterLayout
        context.vdMan.filterLayout.DefaultConfig();
        context.vdMan.filterLayout.MatchClass("screenClass"); // App: powerpoint fullscreen


        context.vdMan.ActionGlobal.VDCreate("1", null, new GridLayout());
        context.vdMan.ActionGlobal.VDCreate("2", null, new GridLayout());
        context.vdMan.ActionGlobal.VDCreate("3", null, new GridLayout());
        VirtualDeskRouter router4 = new VirtualDeskRouter();
        router4.MatchClasses(new String[] {"WeChatMainWndForPC", // App: 微信
                "WeWorkWindow", // App: 企业微信
                "TXGuiFoundation", // App: Tim
        });
        router4.Build();
        context.vdMan.ActionGlobal.VDCreate("聊天", router4, new GridLayout());

        context.pluginMan.Add(new Bar());

        return context;
    };

    private void ConfigCustomKey(Context context) {
        Channel<Message> channelIn = context.mainloop.channelIn;

        context.keyBindMan.Register("Debug VD info", KeyCode.FuncKey.LALT, KeyCode.VK_X,
                () -> channelIn.put(new CustomMessage(new CallBack() {
                    public void Invoke(Context context) {
                        var logger = Context.logger;
                        logger.info("DebugInfo Start");
                        for (var vd : context.vdMan.virtualDesks) {
                            System.out.println("Begin: " + vd.GetName() + ", size = "
                                    + String.valueOf(vd.allWindows.size()));
                            System.out.println("monitor: " + vd.monitor);
                            System.out
                                    .println("AllWindows: " + String.valueOf(vd.allWindows.size()));
                            for (var w : vd.allWindows) {
                                System.out.println("handle: " + w.hWnd);
                                System.out.println("pid: " + w.processId);
                                System.out.println("name: " + w.processName);
                                System.out.println("class: " + w.windowClass);
                                System.out.println("title: " + w.windowTitle);
                                System.out.println();
                            }
                            System.out.println("End: " + vd.GetName() + "\n");
                        }
                        logger.info("DebugInfo End");
                    }
                })));
    }
}
