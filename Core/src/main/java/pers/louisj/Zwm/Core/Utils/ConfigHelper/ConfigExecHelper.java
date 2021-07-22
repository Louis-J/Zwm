package pers.louisj.Zwm.Core.Utils.ConfigHelper;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IConfig;

public class ConfigExecHelper {
    public Context GetContext() {
        MemCompiler mc = new MemCompiler();
        List<String> options = new ArrayList<>();
        options.add("-XDuseJavaUtilZip");
        Writer writer = new StringWriter();// Null: log any unhandled errors to stderr.
        if (!mc.Compile("Config", ConfigPathHelper.configString, options, writer)) {
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
}
