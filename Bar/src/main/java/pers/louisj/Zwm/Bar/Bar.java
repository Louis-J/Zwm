package pers.louisj.Zwm.Bar;

import com.google.gson.Gson;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IPlugin;

public class Bar implements IPlugin {
    public class Config {
        public int vdButtonMaxLen = 300;
        public int vdButtonFontSize = 22;
        public String vdButtonFontColorOn = "#f0B060";
        public String vdButtonFontColorOff = "#F0F0F0";
        public int labelTitleFontSize = 25;
        public String labelTitleFontColorOn = "#f0B060";
        public String labelTitleFontColorOff = "#F0F0F0";
        public int BarHeight = 35;
    }

    protected Gson gson = new Gson();
    protected MsgLoop msgLoop;
    static Config config = null;
    static String labelTitleStyleSheetOn;
    static String labelTitleStyleSheetOff;

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
        if (config == null)
            config = new Config();
        String fontSizeStr = "font-size:" + Bar.config.labelTitleFontSize + "px;color:";
        labelTitleStyleSheetOn = fontSizeStr + Bar.config.labelTitleFontColorOn + ";";
        labelTitleStyleSheetOff = fontSizeStr + Bar.config.labelTitleFontColorOff + ";";
        msgLoop.Start();
    }

    @Override
    public void Defer() {
        msgLoop.Defer();
    }

    @Override
    public String OperateJson(String str) {
        config = gson.fromJson(str, Config.class);
        return gson.toJson("OK");
    }

    @Override
    public Object Operate(Object obj) {
        if (obj instanceof Config) {
            config = (Config) obj;
            return "OK";
        }
        return null;
    }
}
