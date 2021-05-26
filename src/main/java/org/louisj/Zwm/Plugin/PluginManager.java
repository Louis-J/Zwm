package org.louisj.Zwm.Plugin;

import java.util.HashMap;
// import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.louisj.Zwm.Derived.IPlugin;

public class PluginManager {
    private static Logger logger = LogManager.getLogger("PluginManager");
    private HashMap<String, IPlugin> plugins = new HashMap<String, IPlugin>();

    public PluginManager() {
    }

    public void BeforeRun() {
        // for (var plugin : plugins.values()) {
        //     plugin.BeforeRun();
        // }
        for (var pair : plugins.entrySet()) {
            logger.info("RegisterPlugin[{0}]", pair.getKey());
            pair.getValue().BeforeRun();
        }
    }

    public <T extends IPlugin> T RegPlugin(T plugin) {
        plugins.put(plugin.Type(), plugin);
        logger.info("RegisterPlugin[{0}]", plugin.Name());
        return plugin;
    }

    public void Defer() {
        logger.info("PluginManager Defer Called");
        for (var pair : plugins.values()) {
            pair.Defer();
        }
    }
}
