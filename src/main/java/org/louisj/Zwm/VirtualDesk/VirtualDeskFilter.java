package org.louisj.Zwm.VirtualDesk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.louisj.Zwm.Window.Window;

public class VirtualDeskFilter {
    public interface FilterCallBack {
        // Return true to AVOID be managered by Zwm
        public boolean Invoke(Window w);
    }

    private static Logger logger = LogManager.getLogger("VirtualDeskFilter");

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

    public void DefaultConfig() {
        IgnoreClasses(new ArrayList<>(Arrays.asList(new String[] { "TaskManagerWindow", "MSCTFIME UI",
                "SHELLDLL_DefView", "LockScreenBackstopFrame", "Shell_TrayWnd", "Progman", })));

        // IgnoreTitle("Visual Studio Code");

        IgnoreNames(new ArrayList<>(Arrays.asList(new String[] { "SearchUI", "ShellExperienceHost", "LockApp",
                "PeopleExperienceHost", "StartMenuExperienceHost", "SearchApp", "ScreenClippingHost", })));
    }

    public boolean CheckIgnore(Window w) {
        Emit ignoreMatch;

        ignoreMatch = ignoreClassTrie.firstMatch(w.windowClass);
        if (ignoreMatch != null) {
            logger.info("CheckIgnore, Ignore1, {}", ignoreMatch);
            return true;
        }
        ignoreMatch = ignoreTitleTrie.firstMatch(w.windowTitle);
        if (ignoreMatch != null) {
            logger.info("CheckIgnore, Ignore2, {}", ignoreMatch);
            return true;
        }
        ignoreMatch = ignoreNameTrie.firstMatch(w.processName);
        if (ignoreMatch != null) {
            logger.info("CheckIgnore, Ignore3, {}", ignoreMatch);
            return true;
        }

        for (var f : customFilters) {
            if (f.Invoke(w)) {
                logger.info("CheckIgnore, Ignore4, {}", f);
                return true;
            }
        }

        return false;
    }
}
