package pers.louisj.Zwm.Bar;

import io.qt.core.*;
import io.qt.gui.*;
import io.qt.widgets.*;

public class BarUi {
    public QWidget widget;
    public QBoxLayout layout;
    public QPushButton btnLogo;
    public QLabel labelTitle;
    // public QWidget btnWindows;
    public QLabel labelBattery;
    public QLabel labelTime;

    public BarUi(QMainWindow mainWindow) {
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
        // sizePolicy.setHeightForWidth(btnLogo.sizePolicy().hasHeightForWidth());
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
