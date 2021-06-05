package pers.louisj.Zwm.Core.Window;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

public abstract class WindowFilter {
    public interface FilterCallBack {
        public boolean Invoke(Window w);
    }

    private ArrayList<String> ignoreClassStrs = new ArrayList<>();
    private ArrayList<String> ignoreTitleStrs = new ArrayList<>();
    private ArrayList<String> ignoreNameStrs = new ArrayList<>();

    private Trie ignoreClassTrie;
    private Trie ignoreTitleTrie;
    private Trie ignoreNameTrie;

    private List<FilterCallBack> customFilters = new ArrayList<>();

    public void IgnoreClass(String str) {
        ignoreClassStrs.add(str);
    }

    public void IgnoreTitle(String str) {
        ignoreTitleStrs.add(str);
    }

    public void IgnoreName(String str) {
        ignoreNameStrs.add(str);
    }

    public void IgnoreClasses(List<String> strs) {
        ignoreClassStrs.addAll(strs);
    }

    public void IgnoreTitles(List<String> strs) {
        ignoreTitleStrs.addAll(strs);
    }

    public void IgnoreNames(List<String> strs) {
        ignoreNameStrs.addAll(strs);
    }

    public void IgnoreCustom(FilterCallBack f) {
        customFilters.add(f);
    }

    public void Build() {
        ignoreClassTrie = Trie.builder().ignoreOverlaps().addKeywords(ignoreClassStrs).build();
        ignoreTitleTrie = Trie.builder().ignoreOverlaps().addKeywords(ignoreTitleStrs).build();
        ignoreNameTrie = Trie.builder().ignoreOverlaps().addKeywords(ignoreNameStrs).build();
    }

    public abstract void DefaultConfig();

    protected abstract Logger GetLogger();

    public boolean CheckIgnore(Window w) {
        Emit ignoreMatch;

        ignoreMatch = ignoreClassTrie.firstMatch(w.windowClass);
        if (ignoreMatch != null) {
            GetLogger().info("CheckIgnore, Ignore1, {}", ignoreMatch);
            return true;
        }
        ignoreMatch = ignoreTitleTrie.firstMatch(w.windowTitle);
        if (ignoreMatch != null) {
            GetLogger().info("CheckIgnore, Ignore2, {}", ignoreMatch);
            return true;
        }
        ignoreMatch = ignoreNameTrie.firstMatch(w.processName);
        if (ignoreMatch != null) {
            GetLogger().info("CheckIgnore, Ignore3, {}", ignoreMatch);
            return true;
        }

        for (var f : customFilters) {
            if (f.Invoke(w)) {
                GetLogger().info("CheckIgnore, Ignore4, {}", f);
                return true;
            }
        }

        return false;
    }
}
