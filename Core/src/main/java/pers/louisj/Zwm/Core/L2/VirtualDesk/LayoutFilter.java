package pers.louisj.Zwm.Core.L2.VirtualDesk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;

public class LayoutFilter extends WindowFilter {

    private static Logger logger = LogManager.getLogger("LayoutFilter");

    @Override
    public void DefaultConfig() {
        MatchClass("Shell_TrayWnd"); // -----------Task Bar
        MatchClass("ApplicationFrameWindow"); // --设置, Microsoft Store, Realtek Audio Console
        MatchClass("Windows.UI.Core.CoreWindow");
        MatchClass("NotifyIconOverflowWindow"); // ?
        MatchClass("tooltips_class32"); // --------?
        MatchClass("SunAwtWindow"); // ------------prevents flickering
        MatchClass("MultitaskingViewFrame"); // ---Alt Tab Window
        MatchClass("TaskListThumbnailWnd"); // ----TaskList in Task Bar
        MatchClass("SysShadow"); // ---------------Some Shadow Window
        MatchClass("OperationStatusWindow"); // ---explorer "file is in using" window

        MatchClass("7ttRefreshSyncWnd"); // -------App: 7+ taskbar tweaker
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }
}
