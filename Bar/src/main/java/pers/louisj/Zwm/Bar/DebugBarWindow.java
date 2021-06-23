package pers.louisj.Zwm.Bar;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShellAPI;
import com.sun.jna.platform.win32.ShellAPI.APPBARDATA;
// import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT;
import pers.louisj.Zwm.Core.Utils.MyQEvent.MyEventBlock;
import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
// import io.qt.core.*;
import io.qt.core.Qt.WindowType;
import io.qt.gui.*;
import io.qt.widgets.*;

public class DebugBarWindow extends QMainWindow {
    public final DebugBarUi ui;
    public APPBARDATA appBarData;
    public DWORD abmCode = new DWORD();

    public DebugBarWindow() {
        super();
        ui = new DebugBarUi(this);
        setWindowFlags(WindowType.Tool, WindowType.WindowStaysOnTopHint, WindowType.FramelessWindowHint);
        // setWindowFlags(WindowType.FramelessWindowHint, WindowType.WindowSystemMenuHint,
        // WindowType.WindowMinimizeButtonHint, WindowType.WindowStaysOnTopHint);

        setGeometry(600, 1000, 800, 50);
        var icon = new QIcon("./../icon/ZWMICO.png");
        ui.btnLogo.setIcon(icon);
        // setWindowIcon(icon);
    }
    
    public void Close() {
        abmCode.setValue(ShellAPI.ABM_REMOVE);
        Shell32.INSTANCE.SHAppBarMessage(abmCode, appBarData);
    }
}
