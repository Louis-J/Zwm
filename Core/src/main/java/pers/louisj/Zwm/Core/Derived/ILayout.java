package pers.louisj.Zwm.Core.Derived;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.Utils.Types.Rectangle;

import java.util.Set;

public interface ILayout {
    static Logger logger = LogManager.getLogger("ILayout");

    public String Name();

    public void Enable(Rectangle screen);

    public void Disable();

    @Deprecated
    public void AddWindow(Window window);

    @Deprecated
    public boolean RemoveWindow(Window window);

    public boolean WindowAdd(Window window);

    public boolean WindowRemove(Window window);

    public boolean WindowToggleLayout(Window window);

    public void AreaExpand(Window window);

    public void AreaShrink(Window window);

    public void ShiftLeft(Window window);

    public void ShiftRight(Window window);

    public void ShiftUp(Window window);

    public void ShiftDown(Window window);

    @Deprecated
    public void ToggleMinimize(Window window);

    public void ToggleMinimize(Window window, boolean isMinimize);

    public void ResetLayout();

    public Set<Window> GetWindows();

    public void WindowMoveResize(Window window);
}
