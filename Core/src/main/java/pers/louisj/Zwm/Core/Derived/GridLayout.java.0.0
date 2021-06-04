package pers.louisj.Zwm.Core.Derived;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import pers.louisj.Zwm.Core.Utils.Types.Point;
import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.WinApi.WinHelper;
import pers.louisj.Zwm.Core.Window.Window;
import pers.louisj.Zwm.Core.Window.WindowAction;
import com.sun.jna.platform.win32.WinUser;

class WindowUnit {
    public Window window = null;
    public float percent = 0;
    public float percentShowTo = 0; // UseForMinimized
    public WindowUnit prev, next;

    public WindowUnit(Window window) {
        this.window = window;
    }
}

class WindowColumn {
    public WindowUnit begin = null;
    public WindowUnit end = null;
    public int sizeAll = 0, sizeShow = 0;
    public float percent = 0;
    public float percentShowTo = 0; // UseForMinimized

    public WindowColumn prev = null, next = null;

    public WindowUnit AddtoEnd(Window window) {
        WindowUnit wu = new WindowUnit(window);
        if (end == null)
            begin = wu;
        else
            end.next = wu;
        wu.prev = end;
        end = wu;
        if (sizeAll == 0) {
            wu.percent = 1f;
        } else {
            wu.percent = 1f / sizeAll;
        }
        sizeAll++;
        if (!wu.window.IsMinimized()) {
            sizeShow++;
            CalclayoutShow();
        }
        CalclayoutAll();

        return wu;
    }

    public void RemoveWindow(WindowUnit wu) {
        // if (sizeAll == 1) {
        // // sizeAll = sizeShow = 0;
        // sizeAll = 0;
        // }
        if (wu == begin) {
            begin = wu.next;
            begin.prev = null;
        } else if (wu == end) {
            end = wu.prev;
            end.next = null;
        } else {
            wu.prev.next = wu.next;
            wu.next.prev = wu.prev;
        }
        sizeAll--;
        if (!wu.window.IsMinimized()) {
            sizeShow--;
            CalclayoutShow();
        }
        CalclayoutAll();
    }

    private void CalclayoutAll() {
        float percentAll = 0;
        for (var u = begin; u != null; u = u.next) {
            percentAll += u.percent;
        }
        float ratioAll = 1f / percentAll;
        for (var u = begin; u != null; u = u.next) {
            u.percent = u.percent * ratioAll;
        }
    }

    private void CalclayoutShow() {
        float percentShow = 0;
        for (var u = begin; u != null; u = u.next) {
            if (!u.window.IsMinimized())
                percentShow += u.percent;
        }
        float ratioShow = 1f / percentShow;
        float realPercentSum = 0;
        for (var u = begin; u != null; u = u.next) {
            if (!u.window.IsMinimized()) {
                realPercentSum = u.percentShowTo = realPercentSum + u.percent * ratioShow;
            } else
                u.percentShowTo = realPercentSum;
        }
    }
}

class WindowGrid {
    private float minPercent;

    public WindowColumn begin = null;
    public WindowColumn end = null;
    public int sizeAll = 0, sizeShow = 0;

    public int sumAll = 0, sumShow = 0;

    public WindowGrid(float minPercent) {
        this.minPercent = minPercent;
    }

    public WindowUnit AddToColumn(WindowColumn wc, Window window) {
        var wu = wc.AddtoEnd(window);
        sumAll++;
        if (!window.IsMinimized()) {
            sumShow++;
            if (wc.sizeShow == 1) {
                sizeShow++;
                CalclayoutShow();
            }
        }
        return wu;
    }

    public boolean AddNewColumnToEnd(WindowColumn wc) {
        if (end == null)
            begin = wc;
        else
            end.next = wc;
        wc.prev = end;
        end = wc;
        if (sizeAll == 0) {
            wc.percent = 1f;
        } else {
            wc.percent = 1f / sizeAll;
        }
        sizeAll++;
        sumAll += wc.sizeAll;
        if (wc.sizeShow != 0) {
            sizeShow++;
            sumShow += wc.sizeShow;
            CalclayoutShow();
            CalclayoutAll();
            return true;
        }
        CalclayoutAll();
        return false;
    }

    public boolean RemoveWindow(WindowColumn wc, WindowUnit wu) {
        if (wc.sizeAll == 1) {
            if (sizeAll == 1) {
                var ret = sizeShow != 0;
                begin = end = null;
                sizeAll = 0;
                sizeShow = 0;
                sumAll = 0;
                sumShow = 0;
                return ret;
            }
            if (wc == begin) {
                begin = wc.next;
                begin.prev = null;
            } else if (wc == end) {
                end = wc.prev;
                end.next = null;
            } else {
                wc.prev.next = wc.next;
                wc.next.prev = wc.prev;
            }
            sizeAll--;
            sumAll--;
            if (wc.sizeShow != 0) {
                sizeShow--;
                sumShow--;
                CalclayoutShow();
            }
            CalclayoutAll();
            return true;
        }
        wc.RemoveWindow(wu);
        sizeAll--;
        sumAll--;
        if (wc.sizeShow == 0 && !wu.window.IsMinimized()) {
            sizeShow--;
            sumShow--;
            CalclayoutAll();
            CalclayoutShow();
        }
        return !wu.window.IsMinimized();
    }

    private void CalclayoutAll() {
        float percentAll = 0;
        for (var c = begin; c != null; c = c.next) {
            percentAll += c.percent - minPercent;
        }
        float ratioAll = (1f - sizeAll * minPercent) / percentAll;
        for (var c = begin; c != null; c = c.next) {
            c.percent = (c.percent - minPercent) * ratioAll + minPercent;
        }
    }

    private void CalclayoutShow() {
        float percentShow = 0;
        for (var c = begin; c != null; c = c.next) {
            if (c.sizeShow != 0)
                percentShow += c.percent;
        }
        float ratioShow = 1f / percentShow;
        float realPercentSum = 0;
        for (var c = begin; c != null; c = c.next) {
            if (c.sizeShow != 0) {
                realPercentSum = c.percentShowTo = realPercentSum + c.percent * ratioShow;
            } else
                c.percentShowTo = realPercentSum;
        }
    }
}

class GridPosi {
    public WindowColumn x;
    public WindowUnit y;

    public GridPosi(WindowColumn x, WindowUnit y) {
        this.x = x;
        this.y = y;
    }
}

public class GridLayout extends ILayout {

    @Override
    public String Name() {
        return "grid";
    }

    private WindowGrid windowsGrid;
    private HashMap<Window, GridPosi> windowPosi = new HashMap<>();
    private boolean betterAdjust;
    private int maxColumn;
    private Rectangle screen;

    public GridLayout(int maxColumn, float minPercent, boolean betterAdjust) {
        this.maxColumn = Math.max(maxColumn, (int) (1.0f / minPercent));
        windowsGrid = new WindowGrid(minPercent);
        this.betterAdjust = betterAdjust;
    }

    public void Enable(Rectangle screen) {
        this.screen = screen;
        DoLayoutFull();
    }

    public void Disable() {
        screen = null;
    }

    public void AddWindow(Window window) {
        logger.info("GridLayout.AddWindow, {}", window);
        if (windowsGrid.sizeAll == 0) {
            var newColumn = new WindowColumn();
            var unit = newColumn.AddtoEnd(window);
            windowsGrid.AddNewColumnToEnd(newColumn);
            windowPosi.put(window, new GridPosi(newColumn, unit));

            if (!window.IsMinimized())
                DoLayoutFull();
            return;
        }
        if (betterAdjust)
            AddWindowAdjust(window);
        else
            AddWindowNotAdjust(window);
    }

    public void AddWindowAdjust(Window window) {
        WindowColumn minNum = windowsGrid.begin;
        for (var c = minNum.next; c != null; c = c.next) {
            if (c.sizeShow != 0 && c.sizeShow < minNum.sizeShow) {
                minNum = c;
            }
        }
        if (minNum.sizeShow < windowsGrid.sizeShow || windowsGrid.sizeShow == maxColumn) {
            AddToColumn(window, minNum);
        } else if (windowsGrid.end.sizeShow == 0) {
            AddToColumn(window, windowsGrid.end);
        } else {
            AddToNewColumn(window);
        }
    }

    public void AddWindowNotAdjust(Window window) {
        var averageRowSize = windowsGrid.sumAll / windowsGrid.sizeAll;
        if (windowsGrid.sizeShow < maxColumn
                && (windowsGrid.end.sizeShow > averageRowSize || averageRowSize > windowsGrid.sumShow)) {
            AddToNewColumn(window);

        } else {
            AddToColumn(window, windowsGrid.end);
        }
    }

    public boolean RemoveWindow(Window window) {
        logger.info("GridLayout.RemoveWindow, {}", window);
        // var gridPosi = windowPosi.get(window);
        var gridPosi = windowPosi.remove(window);
        if (gridPosi == null)
            return false;

        if (windowsGrid.RemoveWindow(gridPosi.x, gridPosi.y)) {
            DoLayoutFull();
        }
        return true;
    }

    public Set<Window> GetWindows() {
        return windowPosi.keySet();
    }

    public void DoLayoutFull() {
        if (screen == null)
            return;
        for (var c = windowsGrid.begin; c != null; c = c.next) {
            if (c.sizeShow != 0) {
                DoLayoutColumnImpl(c);
            }
        }
    }

    public void DoLayoutColumn(WindowColumn column) {
        if (screen == null)
            return;
        DoLayoutColumnImpl(column);
    }

    private void DoLayoutColumnImpl(WindowColumn column) {
        float percentFrom = column.prev == null ? 0f : column.prev.percentShowTo;
        float percentTo = column.percentShowTo;

        // var hdwp = WindowAction.HDWP4SetLoc.BeginSetLocation(column.sizeShow);
        // var rect = new Rectangle(screen.x + (int) (screen.width * percentFrom),
        // screen.y,
        // (int) (screen.width * (percentTo - percentFrom)), 0);
        // float percentSum = 0;
        // for (var u = column.begin; u != null; u = u.next) {
        // if (!u.window.IsMinimized()) {
        // rect.y += rect.height;
        // rect.height = (int) (screen.height * (u.percentShowTo - percentSum));
        // hdwp.SetLocation(u.window, rect);
        // }
        // }
        // hdwp.EndSetLocation();

        var rect = new Rectangle(screen.x + (int) (screen.width * percentFrom), screen.y,
                (int) (screen.width * (percentTo - percentFrom)), 0);
        float percentShowFrom = 0;
        for (var u = column.begin; u != null; u = u.next) {
            if (!u.window.IsMinimized()) {
                rect.y += rect.height;
                rect.height = (int) (screen.height * (u.percentShowTo - percentShowFrom));
                percentShowFrom = u.percentShowTo;
                WindowAction.SetLocation2(u.window, rect);
            }
        }
    }

    private void AddToNewColumn(Window window) {
        var newColumn = new WindowColumn();
        var unit = newColumn.AddtoEnd(window);
        if (windowsGrid.AddNewColumnToEnd(newColumn))
            DoLayoutFull();

        windowPosi.put(window, new GridPosi(newColumn, unit));
    }

    private void AddToColumn(Window window, WindowColumn column) {
        var unit = windowsGrid.AddToColumn(column, window);

        windowPosi.put(window, new GridPosi(column, unit));

        if (!window.IsMinimized()) {
            if (column.sizeShow == 1)
                DoLayoutFull();
            else
                DoLayoutColumn(column);
        }
    }

    public void ExpandArea(Window window) {
    }

    public void ShrinkArea(Window window) {
    }

    public void ShiftLeft(Window window) {
    }

    public void ShiftRight(Window window) {
    }

    public void ShiftUp(Window window) {
    }

    public void ShiftDown(Window window) {
    }

    public void ChangeSize(Window window) {
    }

    public void UpdateState(Window window) {
    }
}