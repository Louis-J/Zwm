package pers.louisj.Zwm.Core.L0.MsgLoop;

import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.MyQEvent.MyEventFilter;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;
import io.qt.QNativePointer;
import io.qt.core.*;
import io.qt.widgets.*;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinUser.MSG;

class WMEventFilter extends QAbstractNativeEventFilter {
    Channel<Message> mainLoopChan;

    public WMEventFilter(Channel<Message> mainLoopChan) {
        this.mainLoopChan = mainLoopChan;
    }

    public boolean nativeEventFilter(QByteArray arg0, QNativePointer arg1, QNativePointer arg2) {
        final int WM_DISPLAYCHANGE = 0x007e;
        String arg0Str = arg0.toString();
        if (arg0Str.equals("windows_generic_MSG") || arg0Str.equals("windows_dispatcher_MSG")) {
            var msg = Structure.newInstance(MSG.class, new Pointer(arg1.pointer()));
            msg.autoRead();
            if (msg.message == WM_DISPLAYCHANGE) {
                mainLoopChan.put(new VDManMessage(VDManEvent.RefreshMonitors, null));
                return true;
            }
        }
        return false;
    }
}

public class MsgLoopQT implements IMsgLoop {
    private static final int threadId = WinHelper.Kernel32Inst.GetCurrentThreadId();

    static {
        String qtDir = System.getProperty("user.dir") + "\\";
        String envPath = System.getProperty("java.library.path");
        System.setProperty("java.library.path", qtDir + "bin;" + envPath);
    }

    private QAbstractNativeEventFilter wmEventFilter;
    private QApplication app;

    public MsgLoopQT(Channel<Message> mainLoopChan) {
        var a = new String[] {};
        app = QApplication.initialize(a);
        wmEventFilter = new WMEventFilter(mainLoopChan);
        app.installNativeEventFilter(wmEventFilter);
        app.installEventFilter(new MyEventFilter());
    }

    public void run() {
        QApplication.exec();
    }

    public void exit() {
        QApplication.quit();
    }

    @Override
    public int GetThreadId() {
        return threadId;
    }

    @Override
    public void Defer() {
        app.removeNativeEventFilter(wmEventFilter);
        QApplication.shutdown();
    }
}