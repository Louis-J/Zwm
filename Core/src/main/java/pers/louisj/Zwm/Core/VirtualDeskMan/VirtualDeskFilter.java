package pers.louisj.Zwm.Core.VirtualDeskMan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import pers.louisj.Zwm.Core.Window.WindowFilter;

public class VirtualDeskFilter extends WindowFilter {

    private static Logger logger = LogManager.getLogger("VirtualDeskFilter");

    @Override
    public void DefaultConfig() {
        IgnoreClasses(new ArrayList<>(Arrays.asList(new String[] { "TaskManagerWindow", "MSCTFIME UI",
                "SHELLDLL_DefView", "LockScreenBackstopFrame", "Shell_TrayWnd", "WorkerW", "Progman", })));

        IgnoreClass("ApplicationFrameWindow"); // 设置, Microsoft Store, Realtek Audio Console
        IgnoreClass("Windows.UI.Core.CoreWindow");
        IgnoreClass("NotifyIconOverflowWindow"); // ?
        IgnoreClass("tooltips_class32"); // ?
        IgnoreClass("SunAwtWindow"); // prevents flickering
        IgnoreClass("MultitaskingViewFrame"); // Alt Tab Window
        IgnoreClass("TaskListThumbnailWnd"); // TaskList in Task Bar
        IgnoreClass("SysShadow"); // Some Shadow Window
        
        IgnoreClass("7ttRefreshSyncWnd"); // App: 7+ taskbar tweaker
        // IgnoreName("\\bin\\java.exe");
        
        IgnoreTitle("Visual Studio Code");

        IgnoreNames(new ArrayList<>(Arrays.asList(new String[] { "SearchUI", "ShellExperienceHost", "LockApp",
                "PeopleExperienceHost", "StartMenuExperienceHost", "SearchApp", "ScreenClippingHost", })));
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }
}