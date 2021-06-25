package pers.louisj.Zwm.Bar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessage;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageCustom;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageFocus;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageMonitors;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageRefresh;
import pers.louisj.Zwm.Core.Global.Message.PluginMessage.PluginMessageVDs;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.L1.MainLoop.MessageHook;
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
    Channel<PluginMessage> channelIn = new Channel<>(1024);
    protected Logger logger;
    // protected DebugBarWindow debugBarWindow = new DebugBarWindow();
    protected Map<Monitor, BarWindow> barMap = new HashMap<>();
    protected Monitor lastFocused;
    protected List<VirtualDesk> vds;
    protected Bar bar;
    protected QApplication qa;
    protected Context context;

    public MsgLoop(Logger logger, Bar bar, Context context) {
        super();
        setName("Bar Plugin MainLoop Thread");
        this.logger = logger;
        this.bar = bar;
        this.context = context;

        context.mainloop.hooks.add(new MessageHook() {
            Object obj = new Object();

            public boolean Invoke(Message msg) {
                if (msg instanceof VDManMessage
                        && ((VDManMessage) msg).event == VDManEvent.RefreshMonitors) {
                    synchronized (obj) {
                        channelIn.put(new PluginMessageCustom(obj));
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });
    }

    public void Defer() {
        logger.info("Bar Plugin MainLoop, Defer, Start");
        channelIn.put(null);
        this.join();
        // debugBarWindow.close();
        for (var barW : barMap.values()) {
            barW.Defer();
            barW.close();
        }
        logger.info("Bar Plugin MainLoop, Defer, End");
    }

    public void Start() {
        qa = QApplication.instance();
        // debugBarWindow.show();
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            PluginMessage msg = channelIn.take();
            if (msg == null) {
                // Exit
                return;
            }
            logger.info("Bar Plugin MainLoop, PluginMessage, Start, {}", msg.type);
            switch (msg.type) {
                case Focus: {
                    var pmsg = (PluginMessageFocus) msg;
                    VirtualDesk vd = pmsg.param;
                    Monitor monitor = vd.monitor;
                    Window window = vd.lastFocused;

                    if (lastFocused != monitor) {
                        if (lastFocused != null) {
                            var lastBar = barMap.get(lastFocused);
                            MyEvent.Exec(new MyEvent() {
                                public void Invoke() {
                                    lastBar.ui.labelTitle.setStyleSheet(BarUi.colorOff);
                                }
                            });
                        }
                        var thisBar = barMap.get(monitor);
                        MyEvent.Exec(new MyEvent() {
                            public void Invoke() {
                                thisBar.ui.labelTitle.setStyleSheet(BarUi.colorOn);
                            }
                        });
                        lastFocused = monitor;

                        if (window == null)
                            MyEvent.Exec(new MyEvent() {
                                public void Invoke() {
                                    thisBar.ui.labelTitle.setText("null");
                                }
                            });
                        else {
                            window.Refresh.RefreshTitle();
                            MyEvent.Exec(new MyEvent() {
                                public void Invoke() {
                                    thisBar.ui.labelTitle.setText(window.windowTitle);
                                }
                            });
                        }
                    }
                    break;
                }
                case Refresh: {
                    var pmsg = (PluginMessageRefresh) msg;
                    VirtualDesk vd = pmsg.param;
                    Monitor monitor = vd.monitor;
                    if (monitor == null)
                        break;
                    Window window = vd.lastFocused;

                    var thisBar = barMap.get(monitor);

                    if (window == null)
                        MyEvent.Exec(new MyEvent() {
                            public void Invoke() {
                                thisBar.ui.labelTitle.setText("null");
                            }
                        });
                    else {
                        window.Refresh.RefreshTitle();
                        MyEvent.Exec(new MyEvent() {
                            public void Invoke() {
                                thisBar.ui.labelTitle.setText(window.windowTitle);
                            }
                        });
                    }
                    break;
                }
                case Monitors: {
                    var pmsg = (PluginMessageMonitors) msg;
                    vds = pmsg.param;
                    MyEventBlock.Exec(new MyEventBlock() {
                        public void Invoke() {
                            for (var p : barMap.entrySet()) {
                                var k = p.getKey();
                                var v = p.getValue();
                                var window = k.vd.lastFocused;
                                if (window == null)
                                    v.ui.labelTitle.setText("null");
                                else {
                                    window.Refresh.RefreshTitle();
                                    v.ui.labelTitle.setText(window.windowTitle);
                                }
                                v.RefreshVDs(vds, context, k);
                                v.HighLightVD(vds.indexOf(k.vd));
                            }
                        }
                    });
                    break;
                }
                case VDs: {
                    var pmsg = (PluginMessageVDs) msg;
                    MyEventBlock.Exec(new MyEventBlock() {
                        public void Invoke() {
                            var bar1 = barMap.get(pmsg.vd1.monitor);
                            if (bar1 != null) {
                                Window window1 = pmsg.vd1.lastFocused;
                                if (window1 == null)
                                    bar1.ui.labelTitle.setText("null");
                                else {
                                    window1.Refresh.RefreshTitle();
                                    bar1.ui.labelTitle.setText(window1.windowTitle);
                                }
                                bar1.RefreshVDs(vds, context, pmsg.vd1.monitor);
                                bar1.HighLightVD(vds.indexOf(pmsg.vd1));
                            }
                            if (pmsg.vd2 != null) {
                                var bar2 = barMap.get(pmsg.vd2.monitor);
                                if (bar2 != null) {
                                    Window window2 = pmsg.vd2.lastFocused;
                                    if (window2 == null)
                                        bar2.ui.labelTitle.setText("null");
                                    else {
                                        window2.Refresh.RefreshTitle();
                                        bar2.ui.labelTitle.setText(window2.windowTitle);
                                    }
                                    bar2.RefreshVDs(vds, context, pmsg.vd2.monitor);
                                    bar2.HighLightVD(vds.indexOf(pmsg.vd2));
                                }
                            }
                        }
                    });
                    break;
                }
                case Custom: {
                    var pmsg = (PluginMessageCustom) msg;
                    synchronized (pmsg.obj) {
                        Set<Monitor> monitors = Monitor.GetMonitors();
                        Set<BarWindow> removeBarWindows = new HashSet<>();
                        for (var p : barMap.entrySet()) {
                            if (!monitors.contains(p.getKey())) {
                                removeBarWindows.add(barMap.remove(p.getKey()));
                            }
                        }
                        MyEventBlock.Exec(new MyEventBlock() {
                            public void Invoke() {
                                for (var w : removeBarWindows) {
                                    w.Defer();
                                }
                                for (var p : barMap.entrySet()) {
                                    p.getValue().Resize(p.getKey().GetWorkingRect());
                                }
                            }
                        });
                        Map<Monitor, BarWindow> newMap = MyEventRet.Exec(new MyEventRet() {
                            public Map<Monitor, BarWindow> Invoke() {
                                Map<Monitor, BarWindow> newMap = new HashMap<>();
                                for (var m : monitors) {
                                    if (!barMap.containsKey(m)) {
                                        var barW = new BarWindow(m.GetWorkingRect());
                                        barW.show();
                                        newMap.put(m, barW);
                                    }
                                }
                                return newMap;
                            }
                        });
                        barMap.putAll(newMap);
                        pmsg.obj.notify();
                    }
                    break;
                }
                default:
                    break;
            }
            logger.info("Bar Plugin MainLoop, PluginMessage, End");
        }
    }
}
