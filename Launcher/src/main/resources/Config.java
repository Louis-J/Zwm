import pers.louisj.Zwm.Bar.Bar;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IConfig;
import pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts.GridLayout;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouter;

public class Config implements IConfig {
    @Override
    public Context GetContext() {
        Context context = new Context();

        context.keyBindMan.DefaultConfig();
        context.filterVirtualDesk.DefaultConfig();
        context.vdMan.filterLayout.DefaultConfig();
        context.vdMan.filterLayout.MatchClass("screenClass"); // powerpoint fullscreen

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
