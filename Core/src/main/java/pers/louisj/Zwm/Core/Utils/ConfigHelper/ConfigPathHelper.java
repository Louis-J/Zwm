package pers.louisj.Zwm.Core.Utils.ConfigHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class ConfigPathHelper {
    protected static String DefaultConfigFile = "Config.java";
    protected static String DirectName = "\\.Zwm";

    static File configPath;
    static String configString;

    static {
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
        File configFile = new File(path);

        if (configFile.exists() && (!configFile.isFile() || !configFile.canRead())) {
            configFile.delete();
            if (!configFile.isFile()) {
                throw new Error("Config Error 3, the path is NOT a file, and can't be deleted");
            } else if (!configFile.canRead()) {
                throw new Error("Config Error 4, the file can NOT be read, and can't be deleted");
            }
        }
        if (!configFile.exists()) {
            // first run
            try {
                InputStream fins =
                        Object.class.getClassLoader().getResourceAsStream(DefaultConfigFile);
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

    public static File GetConfigPath() {
        return configPath;
    }

    public static File GetFile(String filename) {
        var file = new File(configPath, filename);
        if (file.exists() && (!file.isFile() || !file.canRead())) {
            file.delete();
        }
        return file;
    }

    public static String GetFileContent(File file) {
        try {
            FileInputStream fins = new FileInputStream(file);
            byte[] buffer = new byte[fins.available()];
            fins.read(buffer);
            fins.close();
            return new String(buffer);
        } catch (IOException e) {
            throw new Error("GetFileContent Error, " + e);
        }
    }

    public static void SetFileContent(File file, String obj) {
        try {
            FileWriter fw = new FileWriter(file, false);
            fw.write(obj);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new Error("SetFileContent Error, " + e);
        }
    }
}
