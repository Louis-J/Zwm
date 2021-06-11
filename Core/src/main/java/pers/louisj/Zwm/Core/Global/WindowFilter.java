package pers.louisj.Zwm.Core.Global;

import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.L2.Window.Window;

import java.util.ArrayList;
import java.util.List;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

public abstract class WindowFilter {
    public interface FilterCallBack {
        public boolean Invoke(Window w);
    }

    protected ArrayList<String> matchClassStrs = new ArrayList<>();
    // private ArrayList<String> matchTitleStrs = new ArrayList<>();
    protected ArrayList<String> matchNameStrs = new ArrayList<>();

    private Trie matchClassTrie;
    // private Trie matchTitleTrie;
    private Trie matchNameTrie;

    protected ArrayList<FilterCallBack> customFilters = new ArrayList<>();

    public void MatchClass(String str) {
        matchClassStrs.add(str);
    }

    // public void MatchTitle(String str) {
    //     matchTitleStrs.add(str);
    // }

    public void MatchName(String str) {
        matchNameStrs.add(str);
    }

    public void MatchClasses(List<String> strs) {
        matchClassStrs.addAll(strs);
    }

    // public void MatchTitles(List<String> strs) {
    //     matchTitleStrs.addAll(strs);
    // }

    public void MatchNames(List<String> strs) {
        matchNameStrs.addAll(strs);
    }

    public void MatchCustom(FilterCallBack f) {
        customFilters.add(f);
    }

    public void Build() {
        matchClassTrie = Trie.builder().ignoreOverlaps().addKeywords(matchClassStrs).build();
        // matchTitleTrie = Trie.builder().ignoreOverlaps().addKeywords(matchTitleStrs).build();
        matchNameTrie = Trie.builder().ignoreOverlaps().addKeywords(matchNameStrs).build();
    }

    public abstract void DefaultConfig();

    protected abstract Logger GetLogger();

    public boolean CheckMatch(Window w) {
        Emit match;

        match = matchClassTrie.firstMatch(w.windowClass);
        if (match != null) {
            GetLogger().info("CheckMatch, Match1, {}", match);
            return true;
        }
        match = matchNameTrie.firstMatch(w.processName);
        if (match != null) {
            GetLogger().info("CheckMatch, Match3, {}", match);
            return true;
        }

        for (var f : customFilters) {
            if (f.Invoke(w)) {
                GetLogger().info("CheckMatch, Match4, {}", f);
                return true;
            }
        }

        return false;
    }
}
