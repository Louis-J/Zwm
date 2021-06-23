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

public class BarWindow extends QMainWindow {
    public final BarUi ui;
    public APPBARDATA appBarData;
    public DWORD abmCode = new DWORD();

    public BarWindow(Rectangle rect) {
        super();
        ui = new BarUi(this);
        setWindowFlags(WindowType.Tool, WindowType.WindowStaysOnTopHint, WindowType.FramelessWindowHint);

        setGeometry(rect.x, rect.y, rect.width, 30);
        appBarData = new APPBARDATA();
        appBarData.cbSize.setValue(appBarData.size());
        appBarData.hWnd.setPointer(new Pointer(winId()));
        appBarData.rc.left = rect.x;
        appBarData.rc.top = rect.y;
        appBarData.rc.right = rect.x + rect.width;
        appBarData.rc.bottom = rect.y + 30;
        appBarData.uEdge.setValue(ShellAPI.ABE_TOP);

        abmCode.setValue(ShellAPI.ABM_NEW);
        Shell32.INSTANCE.SHAppBarMessage(abmCode, appBarData);
        abmCode.setValue(ShellAPI.ABM_SETPOS);
        Shell32.INSTANCE.SHAppBarMessage(abmCode, appBarData);

        ui.btnLogo.setIcon(new QIcon("./../icon/ZWMICO.png"));
    }

    public void Defer() {
        abmCode.setValue(ShellAPI.ABM_REMOVE);
        Shell32.INSTANCE.SHAppBarMessage(abmCode, appBarData);
    }

    public void Resize(Rectangle rect) {
        setGeometry(rect.x, rect.y, rect.width, 30);

        appBarData.rc.left = rect.x;
        appBarData.rc.top = rect.y;
        appBarData.rc.right = rect.x + rect.width;
        appBarData.rc.bottom = rect.y + 30;
        abmCode.setValue(ShellAPI.ABM_SETPOS);
        Shell32.INSTANCE.SHAppBarMessage(abmCode, appBarData);
    }
}
