package pers.louisj.Zwm.Bar;


import java.util.HashMap;
import java.util.Map;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IPlugin;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;

public class Bar implements IPlugin {
    // private Context context;
    private DebugBarWindow debugBarWindow = new DebugBarWindow();
    public Map<Monitor, BarWindow> barMap = new HashMap<>();

    private MsgLoop msgLoop = new MsgLoop(logger, this, debugBarWindow);

    @Override
    public void Init(Context context) {
        // this.context = context;
        context.vdMan.channelOut.add(msgLoop.channelIn);
    }

    @Override
    public String Name() {
        return "Zwm-BarPlugin-0.0.1";
    }

    @Override
    public String Type() {
        return "BarPlugin";
    }

    @Override
    public void DefultConfig() {}

    @Override
    public void BeforeRun() {
        debugBarWindow.show();
        msgLoop.Start();
    }
    
    @Override
    public void Defer() {
        System.out.println("Bar Plugin Defer Start");
        msgLoop.Defer();
        System.out.println("Bar Plugin Defer 1");
        debugBarWindow.close();
        System.out.println("Bar Plugin Defer 2");
        for (var barW : barMap.values()) {
            barW.Defer();
            barW.close();
            System.out.println("Bar Plugin Defer Event Doing");
        }
        System.out.println("Bar Plugin Defer End");
    }

    @Override
    public String OperateJson(String str) {
        return null;
    }

    @Override
    public Object Operate(Object obj) {
        return null;
    }
}
