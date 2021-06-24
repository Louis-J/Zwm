package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import java.util.Set;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;

public class PluginMessageMonitors extends PluginMessage {
    public Set<Monitor> param;

    public PluginMessageMonitors(Set<Monitor> param) {
        super(Type.Monitors);
        this.param = param;
    }
}
