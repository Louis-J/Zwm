package pers.louisj.Zwm.Core.L0.MsgLoop;

import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;
import io.qt.QNativePointer;
import io.qt.core.*;
// import io.qt.gui.*;
// import io.qt.gui.QIcon.Mode;
// import io.qt.gui.QIcon.State;
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
        if (arg0.equals("windows_generic_MSG") || arg0.equals("windows_dispatcher_MSG")) {
            var msg = Structure.newInstance(MSG.class, new Pointer(arg1.pointer()));
            msg.autoRead();
            if (msg.message == WM_DISPLAYCHANGE) {
                mainLoopChan.put(new VDManMessage(VDManEvent.RefreshMonitors, null));
                // System.out.println("nativeEventFilter, WM_DISPLAYCHANGE, 666");
                return true;
            }
        }
        return false;
    }
}

public class MsgLoopQT implements IMsgLoop {
    private static final int threadId = WinHelper.Kernel32Inst.GetCurrentThreadId();

    static {
        String qtDir = System.getProperty("user.dir") + "\\..\\min-lib-6.1\\";
        String envPath = System.getProperty("java.library.path");
        System.setProperty("java.library.path", qtDir + "bin;" + envPath);

        // String qtDir = System.getProperty("user.dir") + "\\..\\qt-6.1.0\\";
        // String envPath = System.getProperty("java.library.path");
        // System.setProperty("java.library.path", qtDir + "qtjambi-6.1.0;" + qtDir +
        // "qtbin-6.1.0;" + envPath);
    }

    private QAbstractNativeEventFilter wmEventFilter;
    private QApplication app;

    public MsgLoopQT(Channel<Message> mainLoopChan) {
        wmEventFilter = new WMEventFilter(mainLoopChan);
        // var a = new String[] { "-platformpluginpath",
        // System.getProperty("user.dir") + "\\..\\qt-6.1.0\\plugins-6.1.0", };
        var a = new String[] {};
        app = QApplication.initialize(a);
        app.installNativeEventFilter(wmEventFilter);
    }

    public void run() {
        QApplication.exec();
        QApplication.shutdown();
    }

    public void exit() {
        app.removeNativeEventFilter(wmEventFilter);
        QApplication.quit();
    }

    @Override
    public int GetThreadId() {
        return threadId;
    }
}