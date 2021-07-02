package pers.louisj.Zwm.Core.Utils.MyQEvent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import io.qt.core.*;
import io.qt.widgets.QApplication;

public abstract class MyEventRet extends QEvent {
    final static int typeInt = QEvent.registerEventType();
    final static QEvent.Type type = QEvent.Type.resolve(typeInt);

    protected Lock lock = new ReentrantLock();
    protected Condition condition = lock.newCondition();
    protected Object value;

    protected Object GetValue() {
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return value;
    }

    public MyEventRet() {
        super(type);
        lock.lock();
    }

    public abstract Object Invoke();

    @SuppressWarnings("unchecked")
    public final static <T> T Exec(MyEventRet event) {
        QApplication.postEvent(MyEventFilter.qa, event);
        return (T) event.GetValue();
    }
}
