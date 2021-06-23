package pers.louisj.Zwm.Core.Utils.MyQEvent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import io.qt.core.*;
import io.qt.widgets.QApplication;

public abstract class MyEventBlock extends QEvent {
    public final static int typeInt = QEvent.registerEventType();
    public final static QEvent.Type type = QEvent.Type.resolve(typeInt);

    public static Lock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    public MyEventBlock() {
        super(type);
        lock.lock();
    }

    public abstract void Invoke();

    public void Join() {
        try {
            MyEventBlock.condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MyEventBlock.lock.unlock();
    }
    

    public final static void Exec(MyEventBlock event) {
        QApplication.postEvent(MyEventFilter.qa, event);
        event.Join();
    }
}
