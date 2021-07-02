package pers.louisj.Zwm.Core.Global.Message;

import pers.louisj.Zwm.Core.Context;

public class CustomMessage extends Message {
    public static interface CallBack {
        public void Invoke(Context context);
    }

    public CallBack callback;

    public CustomMessage(CallBack callback) {
        this.callback = callback;
    }
}
