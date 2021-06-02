package pers.louisj.Zwm.Core.Message.VirtualDeskMessage;

import pers.louisj.Zwm.Core.Message.Message;

public class VirtualDeskMessage extends Message {
    public VirtualDeskEvent event;
    public Object param;

    public VirtualDeskMessage(VirtualDeskEvent event, Object param) {
        this.event = event;
        this.param = param;
    }
}
