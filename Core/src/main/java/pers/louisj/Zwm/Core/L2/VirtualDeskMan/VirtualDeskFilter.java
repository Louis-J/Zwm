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
        MatchClasses(new ArrayList<>(Arrays.asList(new String[] { "TaskManagerWindow", "MSCTFIME UI",
                "SHELLDLL_DefView", "LockScreenBackstopFrame", "Shell_TrayWnd", "WorkerW", "Progman", })));

        MatchClass("ApplicationFrameWindow"); // 设置, Microsoft Store, Realtek Audio Console
        MatchClass("Windows.UI.Core.CoreWindow");
        MatchClass("NotifyIconOverflowWindow"); // ?
        MatchClass("tooltips_class32"); // ?
        MatchClass("SunAwtWindow"); // prevents flickering
        MatchClass("MultitaskingViewFrame"); // Alt Tab Window
        MatchClass("TaskListThumbnailWnd"); // TaskList in Task Bar
        MatchClass("SysShadow"); // Some Shadow Window
        MatchClass("VirtualConsoleClass"); // Conemu
        MatchClass("OperationStatusWindow"); // explorer "file is in using" window
        

        MatchClass("7ttRefreshSyncWnd"); // App: 7+ taskbar tweaker
        // IgnoreName("\\bin\\java.exe");
        
        MatchClass("Chrome_WidgetWin_1"); // Ignore Visual Studio Code for Debug
        MatchName("Mozilla Firefox"); // Ignore Mozilla Firefox for Debug

        MatchNames(new ArrayList<>(Arrays.asList(new String[] { "SearchUI", "ShellExperienceHost", "LockApp",
                "PeopleExperienceHost", "StartMenuExperienceHost", "SearchApp", "ScreenClippingHost", })));
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }
}