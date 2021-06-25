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

    public VdButtons(QWidget parent) {
        super(parent);
        setObjectName("scrollArea");
        setSizePolicy(new QSizePolicy(QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Fixed));
        setMaximumSize(300, BarUi.height - 3);
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
        // widgetContents.setGeometry(0, 0, 10 * 60, BarUi.height - 5);
        // widgetContents.adjustSize();
        setWidget(widgetContents);
    }

    void Refresh(List<VirtualDesk> vds, Context context, Monitor monitor) {
        QBoxLayout layout = new QHBoxLayout(this);
        layout.setContentsMargins(0, 0, 0, 0);
        final var minsize = new QSize(60, BarUi.height - 5);
        final var policy = new QSizePolicy(QSizePolicy.Policy.Ignored, QSizePolicy.Policy.Ignored);
        for(var c : widgetContents.findChildren()) {
            c.dispose();
        }
        // for(var c : btns) {
        //     c.dispose();
        // }
        btns = new ArrayList<>();
        for (int i = 0; i < vds.size(); i++) {
            var vd = vds.get(i);
            QPushButton pBtn = new QPushButton();
            pBtn.setText(vd.GetName());
            // pBtn.setMinimumSize(minsize);
            pBtn.setFlat(true);
            pBtn.setSizePolicy(policy);
            pBtn.setStyleSheet("font-size:" + 22 + "px;color:#F0F0F0;");
            layout.addWidget(pBtn);
            var msgParam = new Pair<>(monitor, Integer.valueOf(i));
            pBtn.clicked.connect(() -> context.mainloop.channelIn
                    .put(new VDManMessage(VDManEvent.SwitchMonitorToVD, msgParam)));
            btns.add(pBtn);
        }
        widgetContents.setGeometry(0, 0, vds.size() * 60, BarUi.height - 5);
        widgetContents.setLayout(layout);
    }

    public void HighLight(int index) {
        btns.get(index).setStyleSheet("font-size:" + 22 + "px;color:#f0B060;");
    }
}


public class BarUi {
    public QWidget widget;
    public QBoxLayout layout;
    public QPushButton btnLogo;
    // for chosing vd
    // public QScrollArea scrollArea;
    // public QWidget scrollAreaWidgetContents;
    // public QWidget horizontalLayoutWidget;
    // public QBoxLayout horizontalLayout;
    public VdButtons vdButtons;
    // for chosing vd end
    public QLabel labelTitle;
    // public QWidget btnWindows;
    public QLabel labelBattery;
    public QLabel labelTime;

    public static final int fontSize = 25;
    public static final String fontSizeStr = "font-size:" + fontSize + "px;";
    public static final String colorOn = fontSizeStr + "color:#f0B060;";
    public static final String colorOff = fontSizeStr + "color:#F0F0F0;";
    public static final int height = 35;

    public BarUi(QMainWindow mainWindow) {
        widget = new QWidget(mainWindow);
        widget.setObjectName("widget");
        widget.setStyleSheet("background:#363636;");
        // widget.setStyleSheet(
        // """
        // QWidget#widget{background:#363636;}
        // QLabel#labelTitle{color:white;padding:8px 0px 5px;}
        // QPushButton#btnMin,QPushButton#btnMax,QPushButton#btnExit{
        // border-radius:0px;
        // color: #F0F0F0;
        // background-color:rgba(0,0,0,0);
        // border-style:none;
        // }
        // QPushButton#btnMin:hover,QPushButton#btnMax:hover{
        // background-color: qlineargradient(spread:pad, x1:0, y1:1, x2:0, y2:0, stop:0 rgba(25,
        // 134, 199, 0), stop:1 #5CACEE);
        // }
        // QPushButton#btnExit:hover{
        // background-color: qlineargradient(spread:pad, x1:0, y1:1, x2:0, y2:0, stop:0 rgba(238, 0,
        // 0, 128), stop:1 rgba(238, 44, 44, 255));
        // }
        // """);

        layout = new QHBoxLayout(widget);
        layout.setObjectName("horizontalLayout");
        layout.setContentsMargins(0, 0, 0, 0);
        layout.setSpacing(0);

        btnLogo = new QPushButton(widget);
        btnLogo.setObjectName("btnLogo");
        btnLogo.setSizePolicy(
                new QSizePolicy(QSizePolicy.Policy.Preferred, QSizePolicy.Policy.Preferred));
        btnLogo.setMaximumSize(height, height);
        btnLogo.setAutoDefault(true);
        btnLogo.setFlat(true);

        layout.addWidget(btnLogo);

        // scrollArea = new QScrollArea(widget);
        // scrollArea.setObjectName("scrollArea");
        // scrollArea
        // .setSizePolicy(new QSizePolicy(QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Fixed));
        // scrollArea.setMaximumSize(300, height - 3);
        // scrollArea.setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        // scrollArea.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        // scrollArea.installEventFilter(new QObject() {
        // @Override
        // public boolean eventFilter(QObject watched, QEvent event) {
        // if (event instanceof QWheelEvent) {
        // var wheelevent = (QWheelEvent) event;
        // var add = wheelevent.angleDelta().y();
        // var value = scrollArea.horizontalScrollBar().value();
        // scrollArea.horizontalScrollBar().setValue(value - add / 5);
        // return true;
        // }
        // return super.eventFilter(watched, event);
        // }
        // });
        // QBoxLayout pLayout = new QHBoxLayout();
        // pLayout.setContentsMargins(0, 0, 0, 0);
        // var minsize = new QSize(60, height - 5);
        // for (int i = 0; i < 10; i++) {
        // QPushButton pBtn = new QPushButton();
        // pBtn.setText("Button" + i);
        // pBtn.setMinimumSize(minsize); // width height
        // pBtn.setFlat(true);
        // pBtn.setSizePolicy(
        // new QSizePolicy(QSizePolicy.Policy.Ignored, QSizePolicy.Policy.Ignored));
        // pLayout.addWidget(pBtn);// 把按钮添加到布局控件中
        // }
        // scrollAreaWidgetContents = new QWidget();
        // scrollAreaWidgetContents.setObjectName("scrollAreaWidgetContents");
        // scrollAreaWidgetContents.setGeometry(0, 0, 10 * 60, height - 5);
        // scrollAreaWidgetContents.adjustSize();
        // scrollArea.setWidget(scrollAreaWidgetContents);
        // scrollArea.widget().setLayout(pLayout);// 把布局放置到QScrollArea的内部QWidget中
        // layout.addWidget(scrollArea);
        vdButtons = new VdButtons(widget);
        layout.addWidget(vdButtons);

        labelTitle = new QLabel(widget);
        labelTitle.setObjectName("labelTitle");
        labelTitle.setStyleSheet(BarUi.colorOff);
        // QFont font = new QFont();
        // font.setPointSize(9);
        // font.setBold(true);
        // labelTitle.setFont(font);
        // labelTitle.setStyleSheet("");

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
