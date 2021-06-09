package pers.louisj.Zwm.Core.L2.VirtualDesk;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouterMan;

public class LayoutFilter extends WindowFilter {

    private static Logger logger = LogManager.getLogger("LayoutFilter");
    
    @Override
    public void DefaultConfig() {
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }
}