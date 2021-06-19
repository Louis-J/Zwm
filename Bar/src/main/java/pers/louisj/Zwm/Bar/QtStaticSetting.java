package pers.louisj.Zwm.Bar;

public abstract class QtStaticSetting {
    static {
        String qtDir = System.getProperty("user.dir") + "\\qt-6.1.0\\";
        String envPath = System.getProperty("java.library.path");
        System.setProperty("java.library.path", qtDir + "qtjambi-6.1.0;" + qtDir + "qtbin-6.1.0;" + envPath);
    }

    public static void Init() {
        // Must Run to load <cinit>
    }

    public static String[] PluginArg() {
        return new String[] { "-platformpluginpath", System.getProperty("user.dir") + "\\qt-6.1.0\\plugins-6.1.0", };
    }
}
