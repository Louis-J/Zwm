package pers.louisj.Zwm.Core.Utils.MyQEvent;

import io.qt.core.*;
import io.qt.widgets.*;

public abstract class MyEvent extends QEvent {
    public final static int typeInt = QEvent.registerEventType();
    public final static QEvent.Type type = QEvent.Type.resolve(typeInt);

    public MyEvent() {
        super(type);
    }

    public abstract void Invoke();

    public final static void Exec(MyEvent event) {
        QApplication.postEvent(MyEventFilter.qa, event);
    }
}
