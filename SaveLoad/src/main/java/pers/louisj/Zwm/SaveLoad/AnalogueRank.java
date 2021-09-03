package pers.louisj.Zwm.SaveLoad;

import java.util.*;

import com.sun.jna.Pointer;

import pers.louisj.Zwm.Core.L2.VirtualDesk.VirtualDesk;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.Utils.Types.Pair;

abstract class AnalogueRank {
    // static
    static List<AnalogueRank> ranks =
            new ArrayList<>(Arrays.asList(new AnalogueRankHwnd(), new AnalogueRankClass(),
                    new AnalogueRankTitle(), new AnalogueRankName(), new AnalogueRankPid()));
    static List<Pair<Character, StateValue>> pairs;
    static Map<Character, StateValue> dict;

    public static StateValue GetAnalogue(Window window, int threshold) {
        Map<Character, Integer> analogues = new HashMap<>();

        for (var r : ranks) {
            var ps = r.getAnalogue(window);
            if (ps != null)
                for (var p : ps) {
                    Integer val = analogues.get(p.t1);
                    if (val == null)
                        analogues.put(p.t1, r.weight);
                    else
                        analogues.put(p.t1, val + r.weight);
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
        if (vmax >= threshold) {
            var value = dict.get(cmax);
            value.ageStatus = -1;
            return value;
        }

        return null;
    }

    public static void Load(Pair<Character, StateValue>[] pairs) {
        AnalogueRank.pairs = new ArrayList<>(Arrays.asList(pairs));
        AnalogueRank.dict = new LinkedHashMap<>();
        for (var p : pairs) {
            dict.put(p.t1, p.t2);
            for (var d : ranks)
                d.putIntoMap(p);
        }
    }

    public static void LoadOne(List<VirtualDesk> vds, VirtualDesk vd, Window w) {
        Pair<Character, StateValue> p = new Pair<>(new Character(), new StateValue());

        Set<Window> layoutWindows = vd.GetLayoutWindows();
        var index = vds.indexOf(vd);

        for (var r : ranks)
            r.setValueWind2Char(w, p.t1);

        p.t1.hWndValue = null;
        p.t1.processId = null;
        p.t2.vdIndex = index;
        p.t2.isLayout = layoutWindows.contains(w);

        pairs.add(p);
        dict.put(p.t1, p.t2);
        for (var d : ranks)
            d.putIntoMap(p);
    }

    public static Pair<Character, StateValue>[] Save(List<VirtualDesk> virtualDesks) {
        LinkedHashMap<Character, StateValue> oldDict = new LinkedHashMap<>();
        if (pairs != null)
            for (var p : pairs) {
                p.t1.hWndValue = null;
                p.t1.processId = null;
                p.t2.ageStatus++;
                if (p.t2.ageStatus < 9 && !oldDict.containsKey(p.t1))
                    oldDict.put(p.t1, p.t2);
            }
        List<Pair<Character, StateValue>> ret = new ArrayList<>();
        int index = 0;
        for (var vd : virtualDesks) {
            Set<Window> layoutWindows = vd.GetLayoutWindows();
            Set<Window> windows = vd.GetAllWindows();
            for (var w : windows) {
                var c = new Character();
                var s = new StateValue();

                for (var r : ranks)
                    r.setValueWind2Char(w, c);

                s.vdIndex = index;
                s.isLayout = layoutWindows.contains(w);

                ret.add(new Pair<>(c, s));
            }
            index++;
        }
        for (var e : oldDict.entrySet())
            ret.add(new Pair<>(e.getKey(), e.getValue()));

        return ret.toArray(Pair.createArray(0, Character.class, StateValue.class));
    }

    // non-static
    public abstract Object getValueInWind(Window window);

    public abstract Object getValueInChar(Character character);

    public abstract void setValueWind2Char(Window window, Character character);

    public void putIntoMap(Pair<Character, StateValue> p) {
        Object key = getValueInChar(p.t1);
        if (key == null)
            return;
        else if (mapWind2Chars.containsKey(key))
            mapWind2Chars.get(key).add(p);
        else {
            var valList = new ArrayList<Pair<Character, StateValue>>();
            valList.add(p);
            mapWind2Chars.put(key, valList);
        }
    }

    public List<Pair<Character, StateValue>> getAnalogue(Window window) {
        return mapWind2Chars.get(getValueInWind(window));
    }

    public int weight;
    public Map<Object, List<Pair<Character, StateValue>>> mapWind2Chars = new HashMap<>();
}


class AnalogueRankHwnd extends AnalogueRank {
    {
        this.weight = 15;
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


class AnalogueRankClass extends AnalogueRank {
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


class AnalogueRankTitle extends AnalogueRank {
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


class AnalogueRankName extends AnalogueRank {
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


class AnalogueRankPid extends AnalogueRank {
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
