import pers.louisj.Zwm.Bar.Bar;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IConfig;
import pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts.GridLayout;

public class Config implements IConfig {
    @Override
    public Context GetContext() {
        Context context = new Context();

        context.keyBindMan.DefaultConfig();
        context.filterVirtualDesk.DefaultConfig();
        context.vdMan.filterLayout.DefaultConfig();

        context.vdMan.ActionGlobal.VDCreate("1", null, new GridLayout());
        context.vdMan.ActionGlobal.VDCreate("2", null, new GridLayout());
        context.vdMan.ActionGlobal.VDCreate("3", null, new GridLayout());
        context.vdMan.ActionGlobal.VDCreate("4", null, new GridLayout());

        context.pluginMan.Add(new Bar());

        return context;
    };
}
