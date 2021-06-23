package pers.louisj.Zwm.Bar;

import java.lang.ref.Reference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessage;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.MyQEvent.MyEvent;
import pers.louisj.Zwm.Core.Utils.MyQEvent.MyEventBlock;
import pers.louisj.Zwm.Core.Utils.MyQEvent.MyEventRet;
import io.qt.core.*;
import io.qt.widgets.QApplication;

// public class MsgLoop extends Thread {
public class MsgLoop extends QThread {
    Channel<Message> channelIn = new Channel<>(1024);
    protected Logger logger;
    protected DebugBarWindow debugBarWindow;
    protected Bar bar;
    protected QApplication qa;

    public MsgLoop(Logger logger, Bar bar, DebugBarWindow debugBarWindow) {
        super();
        setName("Bar Plugin MainLoop Thread");
        this.logger = logger;
        this.bar = bar;
        this.debugBarWindow = debugBarWindow;
    }

    public void Defer() {
        logger.info("Bar Plugin MainLoop Defer Start");
        channelIn.put(null);
        logger.info("Bar Plugin MainLoop Defer 1");
        this.join();
        logger.info("Bar Plugin MainLoop Defer End");
    }

    public void Start() {
        qa = QApplication.instance();
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            Message msg = channelIn.take();
            if (msg == null) {
                // Exit
                return;
            } else if (msg instanceof VDManMessage) {
                var wmsg = (VDManMessage) msg;
                logger.error("Bar Plugin MainLoop, VDManMessage, {}, {}", wmsg.event, wmsg.param);
            } else if (msg instanceof VDMessage) {
                var vdmsg = (VDMessage) msg;
                logger.error("Bar Plugin MainLoop, VDMessage, {}, {}", vdmsg.event, vdmsg.param);
            } else if (msg instanceof PluginMessage) {
                var pmsg = (PluginMessage) msg;
                logger.info("Bar Plugin MainLoop, PluginMessage, {}", pmsg.event);
                switch (pmsg.event) {
                    case Foreground: {
                        VirtualDesk vd = (VirtualDesk) pmsg.param1;
                        Monitor monitor = (Monitor) pmsg.param2;
                        Window window = (Window) pmsg.param3;
                        debugBarWindow.ui.label1.setText(vd.GetName());
                        debugBarWindow.ui.label2.setText(monitor.toString());

                        if (window == null)
                            debugBarWindow.ui.label3.setText("null");
                        else {
                            window.Refresh.RefreshTitle();
                            debugBarWindow.ui.label3.setText(window.toString());
                        }
                        break;
                    }
                    case DebugForeground: {
                        String s1 = "|" + pmsg.param1;
                        String s2 = "|" + pmsg.param2;
                        String s3 = "|" + pmsg.param3;
                        // to guarantee the string is in thread-safety
                        MyEventBlock.Exec(new MyEventBlock() {
                            @Override
                            public void Invoke() {
                                debugBarWindow.ui.label1.setText(s1);
                                debugBarWindow.ui.label2.setText(s2);
                                debugBarWindow.ui.label3.setText(s3);
                            }
                        });
                        break;
                    }
                    case RefreshMonitors: {
                        Set<Monitor> monitors = (Set<Monitor>) pmsg.param1;
                        Set<BarWindow> removeBarWindows = new HashSet<>();
                        for (var p : bar.barMap.entrySet()) {
                            if (!monitors.contains(p.getKey())) {
                                removeBarWindows.add(bar.barMap.remove(p.getKey()));
                                // MyEventBlock.Exec(new MyEventBlock() {
                                // public void Invoke() {
                                // p.getValue().Defer();
                                // }
                                // });
                                // bar.barMap.remove(p.getKey());
                            }
                        }
                        MyEventBlock.Exec(new MyEventBlock() {
                            public void Invoke() {
                                for (var w : removeBarWindows) {
                                    w.Defer();
                                }
                                for (var p : bar.barMap.entrySet()) {
                                    p.getValue().Resize(p.getKey().GetWorkingRect());
                                }
                            }
                        });
                        for (var m : monitors) {
                            if (!bar.barMap.containsKey(m)) {
                                bar.barMap.put(m, MyEventRet.Exec(new MyEventRet() {
                                    public BarWindow Invoke() {
                                        // var barW = new BarWindow(bar.barMap.size());
                                        var barW = new BarWindow(m.GetWorkingRect());
                                        barW.show();
                                        return barW;
                                    }
                                }));
                            }
                        }
                        break;
                    }
                }
                logger.info("Bar Plugin MainLoop, PluginMessage, End");
            } else {
                logger.error("Bar Plugin MainLoop, UnknownMessage, {}", msg);
            }
        }
    }
}
