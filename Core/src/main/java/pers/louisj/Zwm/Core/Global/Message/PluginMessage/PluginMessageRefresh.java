package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;

public class PluginMessageRefresh extends PluginMessage {
    public VirtualDesk param;

    public PluginMessageRefresh(VirtualDesk param) {
        super(Type.Refresh);
        this.param = param;
    }
}
