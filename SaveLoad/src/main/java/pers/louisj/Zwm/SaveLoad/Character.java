package pers.louisj.Zwm.SaveLoad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sun.jna.Pointer;
import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.Window.Window;

abstract class DictCharWind {
    public abstract Object getValueInWind(Window window);

    public abstract Object getValueInChar(Character character);

    public abstract void setValueWind2Char(Window window, Character character);

    public void putIntoMap(Character character) {
        mapWind2Chars.put(getValueInChar(character), character);
    }

    public Character getAnalogue(Window window) {
        return mapWind2Chars.get(getValueInWind(window));
    }

    public int weight;
    public Map<Object, Character> mapWind2Chars = new HashMap<>();
}


class DictCharWindHwnd extends DictCharWind {
    {
        this.weight = 20;
    }

    public Long getValueInWind(Window window) {
        return Pointer.nativeValue(window.hWnd.getPointer());
    }

    public Object getValueInChar(Character character) {
        return character.hWndValue;
    }

    public void setValueWind2Char(Window window, Character character) {
        character.hWndValue = Pointer.nativeValue(window.hWnd.getPointer());
    }
};


class DictCharWindClass extends DictCharWind {
    {
        this.weight = 10;
    }

    public String getValueInWind(Window window) {
        return window.windowClass;
    }

    public Object getValueInChar(Character character) {
        return character.windowClass;
    }

    public void setValueWind2Char(Window window, Character character) {
        character.windowClass = window.windowClass;
    }
};


class DictCharWindTitle extends DictCharWind {
    {
        this.weight = 10;
    }

    public String getValueInWind(Window window) {
        return window.windowTitle;
    }

    public Object getValueInChar(Character character) {
        return character.windowTitle;
    }

    public void setValueWind2Char(Window window, Character character) {
        character.windowTitle = window.windowTitle;
    }
};


class DictCharWindName extends DictCharWind {
    {
        this.weight = 10;
    }

    public String getValueInWind(Window window) {
        return window.processName;
    }

    public Object getValueInChar(Character character) {
        return character.processName;
    }

    public void setValueWind2Char(Window window, Character character) {
        character.processName = window.processName;
    }
};


class DictCharWindPid extends DictCharWind {
    {
        this.weight = 15;
    }

    public Integer getValueInWind(Window window) {
        return window.processId;
    }

    public Object getValueInChar(Character character) {
        return character.processId;
    }

    public void setValueWind2Char(Window window, Character character) {
        character.processId = window.processId;
    }
};


public class Character {
    // Individal Characters
    public long hWndValue;
    public String windowClass;
    public String windowTitle;

    // Group Characters
    public String processName;
    public int processId;

    // State
    public int vdIndex;
    public boolean isLayout;
    // public Map<String, Object> More;
    public static DictCharWind dictHwnd = new DictCharWindHwnd();
    public static DictCharWind dictClass = new DictCharWindClass();
    public static DictCharWind dictTitle = new DictCharWindTitle();
    public static DictCharWind dictName = new DictCharWindName();
    public static DictCharWind dictPid = new DictCharWindPid();

    public static int threshold = 30;
    public static List<DictCharWind> dictionaries =
            Arrays.asList(dictHwnd, dictClass, dictTitle, dictName, dictPid);

    public static Character GetAnalogue(Window window) {
        Map<Character, Integer> analogues = new HashMap<>();

        for (var d : dictionaries) {
            var c = d.getAnalogue(window);
            if (c != null) {
                Integer val = analogues.get(c);
                if (val == null)
                    analogues.put(c, d.weight);
                else
                    analogues.put(c, val + d.weight);
            }
        }
        Character cmax = null;
        int vmax = 0;
        for (var a : analogues.entrySet()) {
            if (a.getValue() > vmax) {
                vmax = a.getValue();
                cmax = a.getKey();
            }
        }
        if (vmax > threshold)
            return cmax;

        return null;
    }

    public static void Load(Character[] chars) {
        for (var c : chars) {
            for (var d : dictionaries)
                d.putIntoMap(c);
        }
    }

    public static Character[] Save(List<VirtualDesk> virtualDesks) {
        List<Character> ret = new ArrayList<>();
        int index = 0;
        for (var vd : virtualDesks) {
            Set<Window> layoutWindows = vd.GetLayoutWindows();
            Set<Window> windows = vd.GetAllWindows();
            for (var w : windows) {
                var c = new Character();

                for (var d : dictionaries)
                    d.setValueWind2Char(w, c);

                c.vdIndex = index;
                c.isLayout = layoutWindows.contains(w);

                ret.add(c);
            }
            index++;
        }
        return ret.toArray(new Character[ret.size()]);
    }
}
