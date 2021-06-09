package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.VirtualDeskRouterMan;

public class VirtualDeskRouter extends WindowFilter {

    private static Logger logger = LogManager.getLogger("LayoutFilter");
    private VirtualDeskRouterMan vdr;

    public VirtualDeskRouter(VirtualDeskRouterMan vdr) {
        this.vdr = vdr;
    }

    @Override
    public void DefaultConfig() {
    }

    @Override
    protected Logger GetLogger() {
        return logger;
    }

    public ArrayList<String> GetMatchClassStrs() {
        return matchClassStrs;
    }

    public ArrayList<String> GetMatchNameStrs() {
        return matchNameStrs;
    }

    public ArrayList<WindowFilter.FilterCallBack> GetCustomFilters() {
        return customFilters;
    }
}