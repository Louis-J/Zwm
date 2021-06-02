package pers.louisj.Zwm.Core.Message.WindowMessage;

import pers.louisj.Zwm.Core.Message.Message;
import pers.louisj.Zwm.Core.Window.Window;

public class WindowMessage extends Message {
    public Window window;
    public WindowEvent event;

    public WindowMessage(Window window, WindowEvent event) {
        this.window = window;
        this.event = event;
    }
}
