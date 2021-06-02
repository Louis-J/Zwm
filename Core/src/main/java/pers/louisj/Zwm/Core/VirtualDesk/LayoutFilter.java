package pers.louisj.Zwm.Core.VirtualDesk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Window.WindowFilter;

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
// public class VirtualDeskRouter {
// public interface RouterCallBack {
// // Return true to Router to this VirtualDesk
// public boolean Invoke(Window w);
// }

// private static Logger logger = LogManager.getLogger("VirtualDeskRouter");

// private ArrayList<String> ignoreClassStrs = new ArrayList<>();
// private ArrayList<String> ignoreTitleStrs = new ArrayList<>();
// private ArrayList<String> ignoreNameStrs = new ArrayList<>();

// private Trie ignoreClassTrie;
// private Trie ignoreTitleTrie;
// private Trie ignoreNameTrie;

// private List<RouterCallBack> customRouters = new ArrayList<>();

// public void IgnoreClass(String str) {
// ignoreClassStrs.add(str);
// }

// public void IgnoreTitle(String str) {
// ignoreTitleStrs.add(str);
// }

// public void IgnoreName(String str) {
// ignoreNameStrs.add(str);
// }

// public void IgnoreClasses(List<String> strs) {
// ignoreClassStrs.addAll(strs);
// }

// public void IgnoreTitles(List<String> strs) {
// ignoreTitleStrs.addAll(strs);
// }

// public void IgnoreNames(List<String> strs) {
// ignoreNameStrs.addAll(strs);
// }

// public void IgnoreCustom(RouterCallBack f) {
// customRouters.add(f);
// }

// public void Build() {
// ignoreClassTrie =
// Trie.builder().ignoreOverlaps().addKeywords(ignoreClassStrs).build();
// ignoreTitleTrie =
// Trie.builder().ignoreOverlaps().addKeywords(ignoreTitleStrs).build();
// ignoreNameTrie =
// Trie.builder().ignoreOverlaps().addKeywords(ignoreNameStrs).build();
// }

// public boolean CheckIgnore(Window w) {
// Emit ignoreMatch;

// ignoreMatch = ignoreClassTrie.firstMatch(w.windowClass);
// if (ignoreMatch != null) {
// logger.info("CheckIgnore, Ignore1, VD = {}, window = {}, match = {}", this,
// w, ignoreMatch);
// return true;
// }
// ignoreMatch = ignoreTitleTrie.firstMatch(w.windowTitle);
// if (ignoreMatch != null) {
// logger.info("CheckIgnore, Ignore2, VD = {}, window = {}, match = {}", this,
// w, ignoreMatch);
// return true;
// }
// ignoreMatch = ignoreNameTrie.firstMatch(w.processName);
// if (ignoreMatch != null) {
// logger.info("CheckIgnore, Ignore3, VD = {}, window = {}, match = {}", this,
// w, ignoreMatch);
// return true;
// }

// for (var r : customRouters) {
// if (r.Invoke(w)) {
// logger.info("CheckIgnore, Ignore4, VD = {}, window = {}, router = {}", this,
// w, r);
// return true;
// }
// }

// return false;
// }
// }
