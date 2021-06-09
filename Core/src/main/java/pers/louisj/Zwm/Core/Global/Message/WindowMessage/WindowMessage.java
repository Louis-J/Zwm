package pers.louisj.Zwm.Core.Global.Message.WindowMessage;

import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.L2.Window.Window;

@Deprecated
public class WindowMessage extends Message {
    public WindowEvent event;
    public Window window;

    public WindowMessage(WindowEvent event, Window window) {
        this.event = event;
        this.window = window;
    }
}
