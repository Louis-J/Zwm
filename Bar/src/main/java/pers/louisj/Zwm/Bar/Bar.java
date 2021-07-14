package pers.louisj.Zwm.Bar;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IPlugin;

public class Bar implements IPlugin {
    private MsgLoop msgLoop;

    @Override
    public void Init(Context context) {
        msgLoop = new MsgLoop(logger, this, context);
        context.vdMan.channelOutFocus.add(msgLoop.channelIn);
        context.vdMan.channelOutRefresh.add(msgLoop.channelIn);
        context.vdMan.channelOutMonitors.add(msgLoop.channelIn);
        context.vdMan.channelOutVDs.add(msgLoop.channelIn);
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
    public void Start() {
        msgLoop.Start();
    }

    @Override
    public void Defer() {
        msgLoop.Defer();
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
