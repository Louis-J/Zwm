package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import pers.louisj.Zwm.Core.Global.Message.Message;

public class PluginMessage extends Message {
    public Type type;
    public PluginMessage(Type type) {
        this.type = type;
    }
}
