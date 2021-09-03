package pers.louisj.Zwm.Core.L2.Window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;

public class DelayedWindowFilter extends WindowFilter {
    private static Logger logger = LogManager.getLogger("DelayedWindowFilter");
    public int delayTime = 1500;

    @Override
    public void DefaultConfig() {
        MatchClass("Chrome_WidgetWin_1"); // App: Chrome
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }

    public void SetDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
}
