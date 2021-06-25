package pers.louisj.Zwm.Core.Global.Message.PluginMessage;

import pers.louisj.Zwm.Core.L2.Window.Window;

public class PluginMessageTitle extends PluginMessage {
    public Window param;

    public PluginMessageTitle(Window param) {
        super(Type.Title);
        this.param = param;
    }
}
