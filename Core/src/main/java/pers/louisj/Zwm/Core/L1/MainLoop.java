package pers.louisj.Zwm.Core.L1;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Global.Message.CustomMessage;
import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskManager;
import pers.louisj.Zwm.Core.Utils.Async.Channel;

public class MainLoop extends Thread {
    public interface MessageHook {
        public boolean Invoke(Message msg);
    };

    public static Logger logger = LogManager.getLogger("MainLoop");

    protected Context context;
    private final VirtualDeskManager virtualDeskManager;
    public Channel<Message> channelIn = new Channel<>(1024);
    public List<MessageHook> hooks = new ArrayList<>();

    public MainLoop(Context context, VirtualDeskManager virtualDeskManager) {
        super();
        this.context = context;
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
            }
            for (var h : hooks) {
                if (h.Invoke(msg) == true)
                    continue;
            }
            if (msg instanceof VDManMessage) {
                var wmsg = (VDManMessage) msg;
                logger.info("MainLoop, VDManMessage, {}, {}", wmsg.event, wmsg.param);
                virtualDeskManager.Deal(wmsg);
            } else if (msg instanceof VDMessage) {
                var vdmsg = (VDMessage) msg;
                logger.info("MainLoop, VDMessage, {}, {}", vdmsg.event, vdmsg.param);
                virtualDeskManager.Query.GetFocusedVD().Deal(vdmsg);
            } else {
                var cmsg = (CustomMessage) msg;
                logger.info("MainLoop, CustomMessage, {}", msg);
                if (cmsg.callback != null)
                    cmsg.callback.Invoke(context);
            }
        }
    }
}
