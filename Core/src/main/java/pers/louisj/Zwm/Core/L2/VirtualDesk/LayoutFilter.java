package pers.louisj.Zwm.Core.L2.VirtualDesk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;

public class LayoutFilter extends WindowFilter {

    private static Logger logger = LogManager.getLogger("LayoutFilter");

    @Override
    public void DefaultConfig() {
        MatchClass("ApplicationFrameWindow"); // ----Sys App: SysSetting, 设置, Microsoft Store, Realtek Audio Console
        MatchClass("Windows.UI.Core.CoreWindow"); // Sys App: UWP windows, such as Calc
        MatchClass("MultitaskingViewFrame"); // -----Sys App: Alt Tab Window
        MatchClass("TaskListThumbnailWnd"); // ------Sys App: TaskList in Task Bar
        MatchClass("OperationStatusWindow"); // -----Sys App: explorer "file is in using" window
        MatchClass("SysShadow"); // -----------------Some Shadow Window
        MatchClass("NotifyIconOverflowWindow"); //---?
        MatchClass("tooltips_class32"); // ----------?
        MatchClass("SunAwtWindow"); // --------------Apps: Java Awt apps
        MatchClass("7ttRefreshSyncWnd"); // ---------App: 7+ taskbar tweaker
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }
}
