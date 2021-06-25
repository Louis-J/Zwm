package pers.louisj.Zwm.Launcher;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;

public class Launcher {
    private static Logger logger;

    static {
        System.setProperty("Time4File", String.valueOf(new Date().getTime()));
        logger = LogManager.getLogger("Main");
        WindowStaticAction.glogger = logger;
    }

    public static void main(String[] args) {
        // try {
        // System.setOut(new PrintStream(System.out, true, "UTF-8"));
        // } catch (UnsupportedEncodingException e) {
        // e.printStackTrace();
        // }
        // init config
        ConfigHelper ch = new ConfigHelper();
        Context context = ch.GetContext();
        if (context == null)
            throw new Error("context is null!");
            
        context.mainloop.channelIn.put(new VDManMessage(VDManEvent.RefreshMonitors, null));

        context.filterVirtualDesk.Build();
        context.vdMan.filterLayout.Build();

        // init windowhook
        context.hookMan.Init();
        context.pluginMan.Init();

        context.pluginMan.BeforeRun();
        context.hookMan.Start();

        logger.info("Run message loop in Main");
        context.Start();
        context.Defer();
        logger.info("Main Func Run Over");
    }
}
