package pers.louisj.Zwm.Bar;

import io.qt.core.*;
import io.qt.gui.*;
import io.qt.widgets.*;

public class DebugBarUi {
    public QWidget widget;
    public QBoxLayout layout;
    public QPushButton btnLogo;
    public QLabel labelTitle;
    public QLabel label1;
    public QLabel label2;
    public QLabel label3;
    // public QWidget btnWindows;
    public QLabel labelBattery;
    public QLabel labelTime;

    protected DebugBarUi() {}

    public DebugBarUi(QMainWindow mainWindow) {
        widget = new QWidget(mainWindow);
        widget.setObjectName("widget");
        // widget.setMinimumSize(new QSize(0, 30));
        // widget.setMaximumSize(new QSize(16777215, 30));
        widget.setStyleSheet(
                """
                        QWidget#widget{background:#363636;}
                        QLabel#labelTitle{color:white;padding:8px 0px 5px;}
                        QPushButton#btnMin,QPushButton#btnMax,QPushButton#btnExit{
                                border-radius:0px;
                                color: #F0F0F0;
                                background-color:rgba(0,0,0,0);
                                border-style:none;
                            }
                        QPushButton#btnMin:hover,QPushButton#btnMax:hover{
                                background-color: qlineargradient(spread:pad, x1:0, y1:1, x2:0, y2:0, stop:0 rgba(25, 134, 199, 0), stop:1 #5CACEE);
                            }
                        QPushButton#btnExit:hover{
                                background-color: qlineargradient(spread:pad, x1:0, y1:1, x2:0, y2:0, stop:0 rgba(238, 0, 0, 128), stop:1 rgba(238, 44, 44, 255));
                            }
                        """);

        layout = new QHBoxLayout(widget);
        layout.setObjectName("horizontalLayout");
        layout.setContentsMargins(0, 0, 0, 0);
        layout.setSpacing(0);

        btnLogo = new QPushButton(widget);
        btnLogo.setObjectName("btnLogo");
        QSizePolicy sizePolicy =
                new QSizePolicy(QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Fixed);
        // sizePolicy.setHorizontalStretch(0);
        // sizePolicy.setVerticalStretch(0);
        sizePolicy.setHeightForWidth(btnLogo.sizePolicy().hasHeightForWidth());
        btnLogo.setSizePolicy(sizePolicy);
        // btnLogo.setMinimumSize(new QSize(30, 30));
        // btnLogo.setMaximumSize(new QSize(30, 30));
        btnLogo.setAutoDefault(true);
        btnLogo.setFlat(true);

        layout.addWidget(btnLogo);


        labelTitle = new QLabel(widget);
        labelTitle.setObjectName("labelTitle");
        // QFont font = new QFont();
        // font.setPointSize(9);
        // font.setBold(true);
        // labelTitle.setFont(font);
        // labelTitle.setStyleSheet("");

        layout.addWidget(labelTitle);


        QFont font = new QFont();
        font.setPointSize(9);
        font.setBold(true);
        label1 = new QLabel(widget);
        label1.setFont(font);

        layout.addWidget(label1);

        label2 = new QLabel(widget);
        label2.setFont(font);

        layout.addWidget(label2);

        label3 = new QLabel(widget);
        label3.setFont(font);

        layout.addWidget(label3);

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
