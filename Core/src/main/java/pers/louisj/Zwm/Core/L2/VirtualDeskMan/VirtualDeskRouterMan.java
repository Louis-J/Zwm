package pers.louisj.Zwm.Core.L2.VirtualDeskMan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.WindowFilter;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.Window.Window;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

public class VirtualDeskRouterMan {
    private static Logger logger = LogManager.getLogger("VirtualDeskRouter");
    private Map<VirtualDesk, VirtualDeskRouter> filters = new HashMap<>();
    private boolean needRefresh = false;

    private Trie matchClassTrie;
    private Trie matchNameTrie;
    private List<WindowFilter.FilterCallBack> customFilters;

    public void Build() {
        needRefresh = false;

        var builderClass = Trie.builder().ignoreOverlaps();
        var builderName = Trie.builder().ignoreOverlaps();
        customFilters = new ArrayList<>();

        for (var f : filters.values()) {
            builderClass.addKeywords(f.GetMatchClassStrs());
            builderName.addKeywords(f.GetMatchNameStrs());
            customFilters.addAll(f.GetCustomFilters());
        }

        matchClassTrie = builderClass.build();
        matchNameTrie = builderName.build();
    }

    private VirtualDesk CheckWhich(Window w) {
        for (var f : filters.entrySet()) {
            if (f.getValue().CheckMatch(w))
                return f.getKey();
        }
        assert false;
        return null;
    }

    public VirtualDesk CheckRouter(Window w) {
        if (needRefresh)
            Build();

        Emit match;

        match = matchClassTrie.firstMatch(w.windowClass);
        if (match != null) {
            logger.info("CheckRouter, Ignore1, {}", match);
            return CheckWhich(w);
        }
        match = matchNameTrie.firstMatch(w.processName);
        if (match != null) {
            logger.info("CheckRouter, Ignore3, {}", match);
            return CheckWhich(w);
        }

        for (var f : customFilters) {
            if (f.Invoke(w)) {
                return CheckWhich(w);
            }
        }

        return null;
    }

    public void Add(VirtualDesk vd, VirtualDeskRouter f) {
        needRefresh = true;
        if (f != null)
            filters.put(vd, f);
    }

    public void Remove(VirtualDesk vd) {
        needRefresh = true;
        filters.remove(vd);
    }
}