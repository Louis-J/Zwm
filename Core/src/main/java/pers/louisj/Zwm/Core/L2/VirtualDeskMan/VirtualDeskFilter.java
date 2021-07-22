package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;

public class VirtualDeskFilter extends WindowFilter {

    private static Logger logger = LogManager.getLogger("VirtualDeskFilter");

    @Override
    public void DefaultConfig() {
        MatchClasses(new String[] {"TaskManagerWindow", // Sys App: TaskManagerWindow
                "MSCTFIME UI", // Sys App: For IME
                "LockScreenBackstopFrame",});

        MatchClass("VirtualConsoleClass"); // App: Conemu


        MatchNames(
                new String[] {"SearchUI", "ShellExperienceHost", "PeopleExperienceHost",
                        "StartMenuExperienceHost", "SearchApp", "ScreenClippingHost",});
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }
}
