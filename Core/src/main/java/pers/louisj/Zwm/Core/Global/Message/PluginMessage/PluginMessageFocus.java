package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;

public class PluginMessageFocus extends PluginMessage {
    public VirtualDesk param;

    public PluginMessageFocus(VirtualDesk param) {
        super(Type.Focus);
        this.param = param;
    }
}
