package pers.louisj.Zwm.Core.PluginMan;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IPlugin;

public class PluginManager {
    protected static Logger logger = LogManager.getLogger("PluginManager");
    protected HashMap<String, IPlugin> plugins = new HashMap<String, IPlugin>();
    protected Context context;

    public PluginManager(Context context) {
        this.context = context;
    }

    public void Init() {
        for (var pair : plugins.entrySet()) {
            logger.info("Init, {}", pair.getKey());
            pair.getValue().Init(context);
        }
    }

    public void BeforeRun() {
        for (var pair : plugins.entrySet()) {
            logger.info("BeforeRun, {}", pair.getKey());
            pair.getValue().BeforeRun();
        }
    }

    public <T extends IPlugin> T Add(T plugin) {
        var type = plugin.Type();
        if (plugins.get(type) != null) {
            throw new Error("PluginManager, Plugin Type Dublicated!");
        }
        plugins.put(type, plugin);
        logger.info("Add, {}", plugin.Name());
        return plugin;
    }

    public void Defer() {
        logger.info("PluginManager Defer Start");
        for (var pair : plugins.values()) {
            pair.Defer();
        }
        logger.info("PluginManager Defer End");
    }

}
