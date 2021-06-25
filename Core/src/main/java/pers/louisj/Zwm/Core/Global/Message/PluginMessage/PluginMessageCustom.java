package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

public class PluginMessageCustom extends PluginMessage {
    public Object obj;

    public PluginMessageCustom(Object obj) {
        super(Type.Custom);
        this.obj = obj;
    }
}
