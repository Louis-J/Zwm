package pers.louisj.Zwm.Bar;

import io.qt.core.Qt.WindowType;
import io.qt.gui.*;
import io.qt.widgets.*;

public class DebugBarWindow extends QMainWindow {
    public final DebugBarUi ui;

    public DebugBarWindow() {
        super();
        ui = new DebugBarUi(this);
        setWindowFlags(WindowType.Tool, WindowType.WindowStaysOnTopHint,
                WindowType.FramelessWindowHint);

        setGeometry(600, 1000, 800, 50);
        var icon = new QIcon("./../icon/ZWMICO.png");
        ui.btnLogo.setIcon(icon);
    }
}
