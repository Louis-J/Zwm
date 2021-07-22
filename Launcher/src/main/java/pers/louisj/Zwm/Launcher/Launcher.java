package pers.louisj.Zwm.Launcher;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.Utils.ConfigHelper.ConfigExecHelper;

public class Launcher {
    private static Logger logger;

    static {
        System.setProperty("Time4File", String.valueOf(new Date().getTime()));
        var log4j2ConfigFileName = ".\\log4j2.xml";
        if (new File(log4j2ConfigFileName).exists()) {
            System.out.println("use custom log4j2.xml");
            Configurator.initialize("Log4j2", log4j2ConfigFileName);
        }
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
        ConfigExecHelper ch = new ConfigExecHelper();
        Context context = ch.GetContext();
        if (context == null)
            throw new Error("context is null!");

        context.filterVirtualDesk.Build();
        context.vdMan.filterLayout.Build();

        // init windowhook
        context.pluginMan.Init();

        context.vdMan.Start();
        context.pluginMan.Start();

        logger.info("Run message loop in Main");
        context.Start();
        context.Defer();
        logger.info("Main Func Run Over");
    }
}
