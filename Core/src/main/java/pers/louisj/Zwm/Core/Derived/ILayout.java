package pers.louisj.Zwm.Core.Derived;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

import pers.louisj.Zwm.Core.Window.Window;

public abstract class ILayout {
    protected static Logger logger = LogManager.getLogger("ILayout");

    public abstract String Name();
}
