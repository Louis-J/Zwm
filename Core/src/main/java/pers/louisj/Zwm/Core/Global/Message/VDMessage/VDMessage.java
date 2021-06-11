package pers.louisj.Zwm.Core.Global.Message.VDMessage;

import pers.louisj.Zwm.Core.Global.Message.Message;

public class VDMessage extends Message {
    public VDEvent event;
    public Object param;

    public VDMessage(VDEvent event, Object param) {
        this.event = event;
        this.param = param;
    }
}
