package pers.louisj.Zwm.Bar;

import io.qt.core.*;
import io.qt.gui.*;
import io.qt.widgets.*;

public class Ui {
    public QWidget widgetTitle;
    public QHBoxLayout horizontalLayout;
    public QLabel labelIcon;
    public QLabel labelTitle;
    public QPushButton btnMin;
    public QPushButton btnMax;
    public QPushButton btnExit;
    public QWidget widget;

    void setupUi(QMainWindow MainWindow) {
        if (MainWindow.objectName().isEmpty())
            MainWindow.setObjectName("MainWindow");
        MainWindow.resize(800, 200);
        widgetTitle = new QWidget(MainWindow);
        widgetTitle.setObjectName("widgetTitle");
        widgetTitle.setMinimumSize(new QSize(0, 30));
        widgetTitle.setMaximumSize(new QSize(16777215, 30));
        widgetTitle.setStyleSheet(
                """
                        QWidget#widgetTitle{background:#363636;}
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
        horizontalLayout = new QHBoxLayout(widgetTitle);
        horizontalLayout.setSpacing(0);
        horizontalLayout.setContentsMargins(11, 11, 11, 11);
        horizontalLayout.setObjectName("horizontalLayout");
        horizontalLayout.setContentsMargins(5, 0, 0, 2);
        labelIcon = new QLabel(widgetTitle);
        labelIcon.setObjectName("labelIcon");
        labelIcon.setMinimumSize(new QSize(26, 26));
        labelIcon.setMaximumSize(new QSize(26, 26));
        labelIcon.setStyleSheet("");
        labelIcon.setPixmap(new QPixmap(":/image/app.jpg"));
        labelIcon.setScaledContents(true);

        horizontalLayout.addWidget(labelIcon);

        labelTitle = new QLabel(widgetTitle);
        labelTitle.setObjectName("labelTitle");
        QFont font = new QFont();
        font.setPointSize(9);
        font.setBold(true);
        labelTitle.setFont(font);
        labelTitle.setStyleSheet("");

        horizontalLayout.addWidget(labelTitle);

        btnMin = new QPushButton(widgetTitle);
        btnMin.setObjectName("btnMin");
        QSizePolicy sizePolicy =
                new QSizePolicy(QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Fixed);
        sizePolicy.setHorizontalStretch(0);
        sizePolicy.setVerticalStretch(0);
        sizePolicy.setHeightForWidth(btnMin.sizePolicy().hasHeightForWidth());
        btnMin.setSizePolicy(sizePolicy);
        btnMin.setMinimumSize(new QSize(30, 30));
        btnMin.setMaximumSize(new QSize(30, 30));
        btnMin.setAutoDefault(true);
        btnMin.setFlat(true);

        horizontalLayout.addWidget(btnMin);

        btnMax = new QPushButton(widgetTitle);
        btnMax.setObjectName("btnMax");
        sizePolicy.setHeightForWidth(btnMax.sizePolicy().hasHeightForWidth());
        btnMax.setSizePolicy(sizePolicy);
        btnMax.setMinimumSize(new QSize(30, 30));
        btnMax.setMaximumSize(new QSize(30, 30));
        btnMax.setAutoDefault(true);
        btnMax.setFlat(true);

        horizontalLayout.addWidget(btnMax);

        btnExit = new QPushButton(widgetTitle);
        btnExit.setObjectName("btnExit");
        sizePolicy.setHeightForWidth(btnExit.sizePolicy().hasHeightForWidth());
        btnExit.setSizePolicy(sizePolicy);
        btnExit.setMinimumSize(new QSize(30, 30));
        btnExit.setMaximumSize(new QSize(30, 30));
        btnExit.setAutoDefault(true);
        btnExit.setFlat(true);

        horizontalLayout.addWidget(btnExit);

        MainWindow.setCentralWidget(widgetTitle);

        retranslateUi(MainWindow);

        QMetaObject.connectSlotsByName(MainWindow);
    }

    void retranslateUi(QMainWindow MainWindow) {
        MainWindow.setWindowTitle(QCoreApplication.translate("MainWindow", "MainWindow", null));
        labelIcon.setText(new String());
        labelTitle.setText(QCoreApplication.translate("MainWindow",
                "Qt\u65e0\u8fb9\u6846\u7a97\u53e3\u7ec8\u6781\u7248\uff0c\u81ea\u7531\u7f29\u653e\uff08\u5404\u4e2a\u89d2+\u8fb9\u6846\uff09(Qt\u5b66\u4e60\u7fa4\uff1a1149411109)",
                null));

        btnMin.setToolTip(QCoreApplication.translate("MainWindow", "\u6700\u5c0f\u5316", null));
        btnMin.setText("");
        btnMax.setToolTip(QCoreApplication.translate("MainWindow", "\u6700\u5927\u5316", null));
        btnMax.setText("");
        btnExit.setToolTip(QCoreApplication.translate("MainWindow", "\u9000\u51fa", null));

        btnExit.setText("");
    }
}
