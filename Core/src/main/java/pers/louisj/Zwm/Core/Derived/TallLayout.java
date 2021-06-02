package pers.louisj.Zwm.Core.Derived;

import java.util.ArrayList;
import java.util.Collection;

import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.Window.Window;

public class TallLayout extends ILayout {

    private final int _numInPrimary;
    private final double _primaryPercent;
    private final double _primaryPercentIncrement;

    private int _numInPrimaryOffset = 0;
    private double _primaryPercentOffset = 0;

    public TallLayout() {
        this(1, 0.5, 0.03);
    }

    public TallLayout(int numInPrimary, double primaryPercent, double primaryPercentIncrement) {
        _numInPrimary = numInPrimary;
        _primaryPercent = primaryPercent;
        _primaryPercentIncrement = primaryPercentIncrement;
    }

    @Override
    public String Name() {
        return "tall";
    }

    // @Override
    public Collection<Rectangle> CalcLayout(Collection<Window> windows, int spaceWidth, int spaceHeight) {
        var list = new ArrayList<Rectangle>();
        var numWindows = windows.size();

        if (numWindows == 0)
            return list;

        int numInPrimary = Math.min(GetNumInPrimary(), numWindows);

        int primaryWidth = (int) (spaceWidth * (_primaryPercent + _primaryPercentOffset));
        int primaryHeight = spaceHeight / numInPrimary;
        int height = spaceHeight / Math.max(numWindows - numInPrimary, 1);

        // if there are more "primary" windows than actual windows,
        // then we want the pane to actually spread the entire width
        // of the working area
        if (numInPrimary >= numWindows) {
            primaryWidth = spaceWidth;
        }

        int secondaryWidth = spaceWidth - primaryWidth;

        for (var i = 0; i < numWindows; i++) {
            if (i < numInPrimary) {
                list.add(new Rectangle(0, i * primaryHeight, primaryWidth, primaryHeight));
            } else {
                list.add(new Rectangle(primaryWidth, (i - numInPrimary) * height, secondaryWidth, height));
            }
        }
        return list;
    }

    private int GetNumInPrimary() {
        return _numInPrimary + _numInPrimaryOffset;
    }
}
