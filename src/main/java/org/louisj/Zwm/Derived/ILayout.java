package org.louisj.Zwm.Derived;

import java.util.Collection;

import org.louisj.Zwm.Window.Window;
import org.louisj.Zwm.Window.WindowLocation;

public interface ILayout {

    public String Name();

    Collection<WindowLocation> CalcLayout(Collection<Window> windows, int spaceWidth, int spaceHeight);

    // void ShrinkPrimaryArea();
}
