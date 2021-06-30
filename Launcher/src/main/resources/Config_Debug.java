import pers.louisj.Zwm.Bar.Bar;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IConfig;
import pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts.GridLayout;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouter;

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
}