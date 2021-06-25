package pers.louisj.Zwm.Core.Utils.MyQEvent;

import io.qt.core.*;
import io.qt.widgets.*;

public class MyEventFilter extends QObject {
    public final static QApplication qa = QApplication.instance();

    @Override
    public boolean eventFilter(QObject watched, QEvent event) {
        if (event.type().value() == MyEvent.typeInt) {
            var barEvent = (MyEvent) event;
            barEvent.Invoke();
            return true;
        }
        if (event.type().value() == MyEventBlock.typeInt) {
            MyEventBlock.lock.lock();
            var bbEvent = (MyEventBlock) event;
            bbEvent.Invoke();
            MyEventBlock.condition.signal();
            MyEventBlock.lock.unlock();
            return true;
        }
        if (event.type().value() == MyEventRet.typeInt) {
            var brEvent = (MyEventRet) event;
            brEvent.lock.lock();
            brEvent.value = brEvent.Invoke();
            brEvent.condition.signal();
            brEvent.lock.unlock();
            return true;
        }
        return false;
    }
}
