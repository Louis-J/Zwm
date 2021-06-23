package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import pers.louisj.Zwm.Core.Global.Message.Message;

public class PluginMessage extends Message {
    public PluginEvent event;
    public Object param1;
    public Object param2;
    public Object param3;

    public PluginMessage(PluginEvent event, Object param1, Object param2, Object param3) {
        this.event = event;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
    }
}
