package pers.louisj.Zwm.Core.L1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskManager;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.Async.Channel2;

public class MainLoop extends Thread {
    public static Logger logger = LogManager.getLogger("MainLoop");

    private final VirtualDeskManager virtualDeskManager;
    public Channel<Message> channelIn = new Channel2<>(1024);

    public MainLoop(VirtualDeskManager virtualDeskManager) {
        super();
        this.virtualDeskManager = virtualDeskManager;
        setName("MainLoop Thread");
    }

    public void Defer() {
        logger.info("MainLoop Defer Start");

        channelIn.put(null);

        logger.info("MainLoop Defer 1");
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("MainLoop Defer End");
    }

    public void Start() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            Message msg = channelIn.take();
            if (msg == null) {
                // Exit
                virtualDeskManager.Exit();
                return;
            } else if (msg instanceof VDManMessage) {
                var wmsg = (VDManMessage) msg;
                logger.info("MainLoop, VDManMessage, {}, {}", wmsg.event, wmsg.param);
                virtualDeskManager.Deal(wmsg);
            } else if (msg instanceof VDMessage) {
                var vdmsg = (VDMessage) msg;
                logger.info("MainLoop, VDMessage, {}, {}, focusedIndex = {}", vdmsg.event, vdmsg.param, virtualDeskManager.focusedIndex);
                virtualDeskManager.GetFocusdVD().Deal(vdmsg);
            } else {
                logger.info("MainLoop, UnknownMessage, {}", msg);
            }
        }
    }
}