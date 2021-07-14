package pers.louisj.Zwm.Launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IConfig;

public class ConfigHelper {
    File configPath;
    File configFile;
    String configString;

    public ConfigHelper() {
        var path = System.getenv("USERPROFILE");
        if (path == null) {
            var hdrive = System.getenv("HOMEDRIVE");
            var hpath = System.getenv("HOMEPATH");
            if (hdrive != null && hpath != null) {
                path = hdrive + hpath;
            } else {
                throw new Error("HOMEDRIVE, HOMEPATH, and USERPROFILE are blank");
            }
        }
        path += DirectName;
        configPath = new File(path);

        if (!configPath.exists()) {
            if (!configPath.mkdir())
                throw new Error("Config Error 1");
        }
        path += "\\Config.java";
        configFile = new File(path);

        if (!configFile.exists()) {
            // first run
            try {
                InputStream fins =
                        this.getClass().getClassLoader().getResourceAsStream(DefaultConfigFile);
                byte[] buffer = new byte[fins.available()];
                fins.read(buffer);
                fins.close();
                configString = new String(buffer);

                FileWriter fw = new FileWriter(configFile, false);
                fw.write(configString);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                throw new Error("Config Error 2, " + e);
            }
        } else if (!configFile.isFile()) {
            throw new Error("Config Error 3, the path is NOT a file");
        } else if (!configFile.canRead()) {
            throw new Error("Config Error 4, the file can NOT be read");
        } else {
            try {
                FileInputStream fins = new FileInputStream(configFile);
                byte[] buffer = new byte[fins.available()];
                fins.read(buffer);
                fins.close();
                configString = new String(buffer);
            } catch (IOException e) {
                throw new Error("Config Error 5, " + e);
            }
        }
    }

    public Context GetContext() {
        MemCompiler mc = new MemCompiler();
        List<String> options = new ArrayList<>();
        options.add("-XDuseJavaUtilZip");
        Writer writer = new StringWriter();// Null: log any unhandled errors to stderr.
        if (!mc.Compile("Config", configString, options, writer)) {
            throw new Error("Compile Config Error: " + writer.toString());
        }
        Class<?> configClass;
        try {
            ClassLoader loader = new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    ByteArrayOutputStream byteCode = mc.GetClasses().get(name);
                    if (byteCode == null) {
                        throw new Error(
                                "Execute Config Error, can not find the class of \"" + name + '\"');
                    }
                    return defineClass(name, byteCode.toByteArray(), 0, byteCode.size());
                }
            };
            configClass = loader.loadClass("Config");
        } catch (ClassNotFoundException e) {
            throw new Error("Execute Config Error 2, " + e);
        }
        if (!IConfig.class.isAssignableFrom(configClass)) {
            throw new Error("Execute Config Error 3");
        }
        IConfig instance;
        try {
            instance = (IConfig) configClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new Error("Execute Config Error 4, " + e);
        } catch (IllegalAccessException e) {
            throw new Error("Execute Config Error 5, " + e);
        } catch (IllegalArgumentException e) {
            throw new Error("Execute Config Error 6, " + e);
        } catch (InvocationTargetException e) {
            throw new Error("Execute Config Error 7, " + e);
        } catch (NoSuchMethodException e) {
            throw new Error("Execute Config Error 8, " + e);
        } catch (SecurityException e) {
            throw new Error("Execute Config Error 9, " + e);
        }
        return instance.GetContext();
    }

    protected static String DefaultConfigFile = "Config.java";
    protected static String DirectName = "\\.Zwm";
}
