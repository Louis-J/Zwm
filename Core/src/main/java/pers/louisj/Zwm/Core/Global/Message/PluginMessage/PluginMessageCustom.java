package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;

public class PluginMessageCustom extends PluginMessage {
    public Object obj;

    public PluginMessageCustom(Object obj) {
        super(Type.Custom);
        this.obj = obj;
    }
}
