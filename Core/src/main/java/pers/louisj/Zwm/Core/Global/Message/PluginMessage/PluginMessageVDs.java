package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;

public class PluginMessageVDs extends PluginMessage {
    public VirtualDesk vd1;
    public VirtualDesk vd2;

    public PluginMessageVDs(VirtualDesk vd1, VirtualDesk vd2) {
        super(Type.VDs);
        this.vd1 = vd1;
        this.vd2 = vd2;
    }
}
