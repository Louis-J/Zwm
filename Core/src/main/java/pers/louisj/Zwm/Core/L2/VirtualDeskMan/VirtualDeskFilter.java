package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;

import java.util.ArrayList;
import java.util.Arrays;

public class VirtualDeskFilter extends WindowFilter {

    private static Logger logger = LogManager.getLogger("VirtualDeskFilter");

    @Override
    public void DefaultConfig() {
        MatchClasses(new ArrayList<>(Arrays.asList(new String[] {"TaskManagerWindow", "MSCTFIME UI",
                "SHELLDLL_DefView", "LockScreenBackstopFrame", "WorkerW", "Progman",})));

        MatchClass("VirtualConsoleClass"); // -----App: Conemu

        // For Debug
        MatchClass("MozillaWindowClass"); // Ignore Firefox for Debug
        MatchClass("Chrome_WidgetWin_1"); // Ignore Visual Studio Code for Debug
        MatchClass("SunAwtFrame"); // Ignore IDEA for Debug

        MatchName("\\bin\\java.exe");
        MatchNames(new ArrayList<>(Arrays.asList(
                new String[] {"SearchUI", "ShellExperienceHost", "LockApp", "PeopleExperienceHost",
                        "StartMenuExperienceHost", "SearchApp", "ScreenClippingHost",})));
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }
}
