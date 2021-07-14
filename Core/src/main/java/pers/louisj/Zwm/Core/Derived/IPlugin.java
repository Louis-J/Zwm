package pers.louisj.Zwm.Core.Derived;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Context;

public interface IPlugin {
    public static Gson gson = new Gson();
    public static Logger logger = LogManager.getLogger("Plugin");

    public void Init(Context context);

    public String Name();

    public String Type();

    public void DefultConfig();

    public void Start();

    public void Defer(); // for some languages using deconstractor to release resources

    public String OperateJson(String str);

    public Object Operate(Object obj);
}
