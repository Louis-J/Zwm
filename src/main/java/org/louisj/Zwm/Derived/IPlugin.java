package org.louisj.Zwm.Derived;

import org.louisj.Zwm.Context;

public interface IPlugin {
    public void Init(Context context);
    public String Name();
    public String Type();
    public void DefultConfig();
    public void BeforeRun();
    public void Defer(); // for some languages using deconstractor to release resources
    public String Operate(String str);
}
