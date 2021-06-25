package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import java.util.List;
// import java.util.Set;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
// import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;

public class PluginMessageMonitors extends PluginMessage {
    public List<VirtualDesk> param;

    public PluginMessageMonitors(List<VirtualDesk> param) {
        super(Type.Monitors);
        this.param = param;
    }
}
