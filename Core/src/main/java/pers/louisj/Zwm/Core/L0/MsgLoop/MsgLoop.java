package pers.louisj.Zwm.Core.L0.MsgLoop;

import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.MSG;

import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

public class MsgLoop extends Thread {
    private volatile int nativeThreadId = 0;
    private Channel<Message> mainLoopChan;

    public MsgLoop(Channel<Message> mainLoopChan) {
        this.mainLoopChan = mainLoopChan;
        setName("MsgLoop Thread");
    }

    @Override
    public void run() {
        final int WM_DISPLAYCHANGE = 0x007e;
        MSG msg = new WinUser.MSG();

        // Make sure message loop is prepared
        WinHelper.MyUser32Inst.PeekMessage(msg, null, 0, 0, 0);
        nativeThreadId = WinHelper.Kernel32Inst.GetCurrentThreadId();

        int getMessageReturn;
        while ((getMessageReturn = WinHelper.MyUser32Inst.GetMessage(msg, null, 0, 0)) != 0) {
            if (getMessageReturn != -1) {
                // Normal processing
                if (msg.message == WM_DISPLAYCHANGE) {
                    mainLoopChan.put(new VDManMessage(VDManEvent.RefreshMonitors, null));
                    continue;
                }
                WinHelper.MyUser32Inst.TranslateMessage(msg);
                WinHelper.MyUser32Inst.DispatchMessage(msg);
            } else {
                // Error case
                // throw new Win32Exception(WinHelper.Kernel32Inst.GetLastError());
                new Win32Exception(WinHelper.Kernel32Inst.GetLastError()).printStackTrace();
                // Context.logger.error(new Win32Exception(WinHelper.Kernel32Inst.GetLastError()).toString());
            }
        }
    }

    public void exit() {
        WinHelper.MyUser32Inst.PostThreadMessage(nativeThreadId, WinUser.WM_QUIT, null, null);
    }
}