package pers.louisj.Zwm.Bar;

import java.util.ArrayList;
import java.util.List;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;
import pers.louisj.Zwm.Core.Utils.Types.Pair;
import io.qt.core.*;
import io.qt.gui.*;
import io.qt.widgets.*;

class VdButtons extends QScrollArea {
    public QWidget widgetContents;
    public List<QPushButton> btns = new ArrayList<>();
    protected String styleSheetOn = "font-size:" + Bar.config.vdButtonFontSize + "px;color:"
            + Bar.config.vdButtonFontColorOn + ";";
    protected String styleSheetOff = "font-size:" + Bar.config.vdButtonFontSize + "px;color:"
            + Bar.config.vdButtonFontColorOff + ";";

    public VdButtons(QWidget parent) {
        super(parent);
        setObjectName("scrollArea");
        setSizePolicy(new QSizePolicy(QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Fixed));
        setMaximumSize(Bar.config.vdButtonMaxLen, Bar.config.BarHeight - 3);
        // setFixedSize(Bar.config.vdButtonMaxLen, Bar.config.BarHeight - 3);
        setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        installEventFilter(new QObject() {
            @Override
            public boolean eventFilter(QObject watched, QEvent event) {
                if (event instanceof QWheelEvent) {
                    var wheelevent = (QWheelEvent) event;
                    var add = wheelevent.angleDelta().y();
                    var value = horizontalScrollBar().value();
                    horizontalScrollBar().setValue(value - add / 5);
                    return true;
                }
                return super.eventFilter(watched, event);
            }
        });
        widgetContents = new QWidget(this);
        widgetContents.setObjectName("scrollAreaWidgetContents");
        widgetContents.setContentsMargins(0, 0, 0, 0);
        setWidget(widgetContents);
    }

    void Refresh(List<VirtualDesk> vds, Context context, Monitor monitor) {
        QBoxLayout layout = new QHBoxLayout(this);
        layout.setContentsMargins(0, 0, 0, 0);
        // final var minsize = new QSize(60, Bar.config.BarHeight - 5);
        final var policy = new QSizePolicy(QSizePolicy.Policy.Ignored, QSizePolicy.Policy.Ignored);

        for (var c : widgetContents.findChildren()) {
            c.dispose();
        }
        btns = new ArrayList<>();
        int widthall = 0;

        QFont font = new QFont();
        font.setPixelSize(Bar.config.vdButtonFontSize);
        QFontMetrics fmwelcome = new QFontMetrics(font);
        for (int i = 0; i < vds.size(); i++) {
            var vd = vds.get(i);
            QPushButton pBtn = new QPushButton();
            String text = vd.GetName();
            // pBtn.setMinimumSize(minsize);
            pBtn.setFlat(true);
            pBtn.setSizePolicy(policy);
            pBtn.setStyleSheet(styleSheetOff);
            int width = fmwelcome.boundingRect(text).width() + 10;
            // pBtn.setMinimumWidth(width);
            // pBtn.setMaximumWidth(width);
            pBtn.setFixedWidth(width);
            pBtn.setText(text);
            widthall += width;
            layout.addWidget(pBtn);
            var msgParam = new Pair<>(monitor, Integer.valueOf(i));
            pBtn.clicked.connect(() -> context.mainloop.channelIn
                    .put(new VDManMessage(VDManEvent.SwitchMonitorToVD, msgParam)));
            btns.add(pBtn);
        }
        widgetContents.setGeometry(0, 0,
                Math.max(Bar.config.vdButtonMaxLen - 2, widthall + vds.size() * 5),
                Bar.config.BarHeight - 5);
        widgetContents.setLayout(layout);
    }

    public void HighLight(int index) {
        btns.get(index).setStyleSheet(styleSheetOn);
    }
}


public class BarUi {
    public QWidget widget;
    public QBoxLayout layout;
    public QPushButton btnLogo;
    // for chosing vd
    public VdButtons vdButtons;
    // for chosing vd end
    public QLabel labelTitle;
    // public QWidget btnWindows;
    public QLabel labelBattery;
    public QLabel labelTime;

    // public static final String fontSizeStr =
    // "font-size:" + Bar.config.labelTitleFontSize + "px;color:";
    // public static final String colorOn = fontSizeStr + Bar.config.labelTitleFontColorOn + ";";
    // public static final String colorOff = fontSizeStr + Bar.config.labelTitleFontColorOff + ";";
    // public static final int BarHeight = 35;

    public BarUi(QMainWindow mainWindow) {
        widget = new QWidget(mainWindow);
        widget.setObjectName("widget");
        widget.setStyleSheet("background:#363636;");

        layout = new QHBoxLayout(widget);
        layout.setObjectName("horizontalLayout");
        layout.setContentsMargins(0, 0, 0, 0);
        layout.setSpacing(0);

        btnLogo = new QPushButton(widget);
        btnLogo.setObjectName("btnLogo");
        btnLogo.setSizePolicy(
                new QSizePolicy(QSizePolicy.Policy.Preferred, QSizePolicy.Policy.Preferred));
        btnLogo.setMaximumSize(Bar.config.BarHeight, Bar.config.BarHeight);
        btnLogo.setAutoDefault(true);
        btnLogo.setFlat(true);

        layout.addWidget(btnLogo);

        vdButtons = new VdButtons(widget);
        layout.addWidget(vdButtons);

        labelTitle = new QLabel(widget);
        labelTitle.setObjectName("labelTitle");
        labelTitle.setStyleSheet(Bar.labelTitleStyleSheetOff);

        layout.addWidget(labelTitle);

        labelBattery = new QLabel(widget);
        labelBattery.setObjectName("labelBattery");
        layout.addWidget(labelBattery);

        labelTime = new QLabel(widget);
        labelTime.setObjectName("labelTime");
        layout.addWidget(labelTime);


        mainWindow.setCentralWidget(widget);
        QMetaObject.connectSlotsByName(mainWindow);
    }
}
