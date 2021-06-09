package pers.louisj.Zwm.Core.Global.Message.VDManMessage;

import pers.louisj.Zwm.Core.Global.Message.Message;

public class VDManMessage extends Message {
    public VDManEvent event;
    public Object param;

    public VDManMessage(VDManEvent event, Object param) {
        this.event = event;
        this.param = param;
    }
}
