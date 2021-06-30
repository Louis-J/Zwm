package pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts;

import java.util.HashMap;
import java.util.Set;

import pers.louisj.Zwm.Core.Utils.Types.Point;
import pers.louisj.Zwm.Core.Utils.Types.Rectangle;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

import pers.louisj.Zwm.Core.Derived.ILayout;
import pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts.WindowGrid.WindowColumn;
import pers.louisj.Zwm.Core.L2.VirtualDeskMan.Monitor;
import pers.louisj.Zwm.Core.L2.Window.Window;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction.HDWP4SetLoc;

class WindowUnit {
    public Window window = null;
    public float percent = 0;
    public float percentShowTo = 0; // UseForMinimized
    public WindowUnit prev, next;

    public WindowUnit(Window window) {
        this.window = window;
    }
}


class WindowGrid {
    static class WindowColumn {
        WindowUnit begin = null;
        WindowUnit end = null;
        int sizeAll = 0, sizeShow = 0;
        float percent = 0;
        float percentShowTo = 0; // UseForMinimized

        WindowColumn prev = null, next = null;

        public WindowColumn() {}

        public WindowColumn(float percent) {
            this.percent = percent;
        }

        void AddtoEnd(WindowUnit wu) {
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
            if (!wu.window.Query.IsMinimized()) {
                sizeShow++;
                CalclayoutShow();
            }
            CalclayoutAll();
        }

        void RemoveWindow(WindowUnit wu) { // Only Can Call When WC.sizeAll > 1!
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
            if (!wu.window.Query.IsMinimized()) {
                sizeShow--;
                CalclayoutShow();
            }
            CalclayoutAll();
        }

        void CalclayoutAll() {
            float percentAll = 0;
            for (var u = begin; u != null; u = u.next) {
                percentAll += u.percent;
            }
            float ratioAll = 1f / percentAll;
            for (var u = begin; u != null; u = u.next) {
                u.percent = u.percent * ratioAll;
            }
        }

        void CalclayoutShow() {
            float percentShow = 0;
            for (var u = begin; u != null; u = u.next) {
                if (!u.window.Query.IsMinimized())
                    percentShow += u.percent;
            }
            float ratioShow = 1f / percentShow;
            float realPercentSum = 0;
            for (var u = begin; u != null; u = u.next) {
                if (!u.window.Query.IsMinimized()) {
                    realPercentSum = u.percentShowTo = realPercentSum + u.percent * ratioShow;
                } else
                    u.percentShowTo = realPercentSum;
            }
        }

        public void ResetLayout() {
            var percent = 1f / sizeAll;
            for (var u = begin; u != null; u = u.next) {
                u.percent = percent;
            }
            CalclayoutShow();
        }
    }

    public WindowColumn begin = null;
    public WindowColumn end = null;
    public int sizeAll = 0, sizeShow = 0;
    public int sumAll = 0, sumShow = 0;
    public float setp = 0.03f;
    public byte RefreshState = 0; // 1: should Refresh Column; 2: should Refresh All Columns


    protected void InsertColumnBefore(WindowColumn wc, WindowColumn dest) {
        dest.prev.next = wc;
        wc.prev = dest.prev;
        dest.prev = wc;
        wc.next = dest;
        sizeAll++;
    }

    protected void InsertColumnAfter(WindowColumn wc, WindowColumn dest) {
        dest.next.prev = wc;
        wc.next = dest.next;
        dest.next = wc;
        wc.prev = dest;
        sizeAll++;
    }

    protected void InsertColumnToBegin(WindowColumn wc) { // Assert wc.prev == null
        wc.next = begin;
        if (begin != null)
            begin.prev = wc;
        else
            end = wc;
        begin = wc;
        sizeAll++;
    }

    protected void InsertColumnToEnd(WindowColumn wc) { // Assert wc.next == null
        wc.prev = end;
        if (end != null)
            end.next = wc;
        else
            begin = wc;
        end = wc;
        sizeAll++;
    }

    public GridPosi AddToColumn(WindowColumn wc, Window window) {
        WindowUnit wu = new WindowUnit(window);
        wc.AddtoEnd(wu);
        sumAll++;
        if (!window.Query.IsMinimized()) {
            sumShow++;
            if (wc.sizeShow == 1) {
                sizeShow++;
                CalclayoutShow();
                RefreshState = 2;
            } else
                RefreshState = 1;
        } else
            RefreshState = 0;
        return new GridPosi(wc, wu);
    }

    public GridPosi AddToNewColumn(Window window) {
        WindowUnit wu = new WindowUnit(window);
        float percent;
        if (sizeAll == 0)
            percent = 1f;
        else
            percent = 1f / sizeAll;
        var wc = new WindowColumn(percent);
        wc.AddtoEnd(wu);
        InsertColumnToEnd(wc);
        sumAll += wc.sizeAll;
        if (wc.sizeShow != 0) {
            sizeShow++;
            sumShow += wc.sizeShow;
            if (wc.sizeShow == 1) {
                CalclayoutShow();
                CalclayoutAll();
                RefreshState = 2;
                return new GridPosi(wc, wu);
            }
            CalclayoutAll();
            RefreshState = 1;
            return new GridPosi(wc, wu);
        }
        CalclayoutAll();
        RefreshState = 0;
        return new GridPosi(wc, wu);
    }

    private void RemoveColumn(WindowColumn wc) {
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
    }

    public void RemoveWindow(WindowColumn wc, WindowUnit wu) {
        if (wc.sizeAll == 1) {
            if (sizeAll == 1) {
                if (sizeShow != 0)
                    RefreshState = 2;
                else
                    RefreshState = 0;
                begin = end = null;
                sizeAll = 0;
                sizeShow = 0;
                sumAll = 0;
                sumShow = 0;
                return;
            }
            RemoveColumn(wc);
            sizeAll--;
            sumAll--;
            if (wc.sizeShow != 0) {
                sizeShow--;
                sumShow--;
                CalclayoutShow();
                CalclayoutAll();
                RefreshState = 2;
                return;
            }
            CalclayoutAll();
            RefreshState = 0;
            return;
        }
        wc.RemoveWindow(wu);
        sumAll--;
        if (!wu.window.Query.IsMinimized()) {
            sizeShow--;
            sumShow--;
            if (wc.sizeShow == 0) {
                CalclayoutShow();
                RefreshState = 2;
                return;
            }
            RefreshState = 1;
            return;
        }
        RefreshState = 0;
        return;
    }

    public void AreaExpand(WindowColumn wc, WindowUnit wu) {
        wc.percent = Math.min(wc.percent + setp, 1f);
        wu.percent = Math.min(wu.percent + setp, 1f);
        wc.CalclayoutAll();
        wc.CalclayoutShow();
        CalclayoutAll();
        CalclayoutShow();
    }

    public void AreaShrink(WindowColumn wc, WindowUnit wu) {
        wc.percent = Math.max(wc.percent - setp, 0f);
        wu.percent = Math.max(wu.percent - setp, 0f);
        wc.CalclayoutAll();
        wc.CalclayoutShow();
        CalclayoutAll();
        CalclayoutShow();
    }

    protected void ShiftColumn(WindowColumn wcOrig, WindowColumn wcDest, WindowUnit wu) {
        if (wcOrig.sizeAll == 1) {
            RemoveColumn(wcOrig);
            sizeAll--;
            if (wcOrig.sizeShow != 0)
                sizeShow--;
            wcDest.AddtoEnd(wu);
            CalclayoutAll();
            CalclayoutShow();
            RefreshState = 2;
        } else {
            wcOrig.RemoveWindow(wu);
            wu.next = null;
            wcDest.AddtoEnd(wu);
            if (!wu.window.Query.IsMinimized()) {
                CalclayoutAll();
                if (wcOrig.sizeShow == 0 || wcDest.sizeShow == 1) {
                    if (wcOrig.sizeShow == 0)
                        sizeShow--;
                    if (wcDest.sizeShow == 1)
                        sizeShow++;
                    CalclayoutShow();
                    RefreshState = 2;
                    return;
                }
                RefreshState = 1;
                return;
            }
            RefreshState = 0;
        }
    }

    public WindowColumn ShiftLeft(WindowColumn wc, WindowUnit wu) {
        WindowColumn dest = wc;
        if (wc.sizeShow == 1) {
            while (dest.prev != null) {
                dest = dest.prev;
                if (dest.sizeShow != 0)
                    break;
            }
            if (dest != wc) {
                ShiftColumn(wc, dest, wu);
                return dest;
            } else {
                RefreshState = 0;
                return null;
            }
        }
        while (dest.prev != null && dest.prev.sizeShow == 0)
            dest = dest.prev;
        if (dest != wc) {
            ShiftColumn(wc, dest, wu);
            return dest;
        } else if (dest.prev == null) { // wc == dest == begin
            var wcNew = new WindowColumn(1f / sizeAll);
            InsertColumnToBegin(wcNew);
            ShiftColumn(wc, wcNew, wu);
            return wcNew;
        } else {
            var wcNew = new WindowColumn(1f / sizeAll);
            InsertColumnBefore(wcNew, dest);
            ShiftColumn(wc, wcNew, wu);
            return wcNew;
        }
    }

    public WindowColumn ShiftRight(WindowColumn wc, WindowUnit wu) {
        WindowColumn dest = wc;
        if (wc.sizeShow == 1) {
            while (dest.next != null) {
                dest = dest.next;
                if (dest.sizeShow != 0)
                    break;
            }
            if (dest != wc) {
                ShiftColumn(wc, dest, wu);
                return dest;
            } else {
                RefreshState = 0;
                return null;
            }
        }
        while (dest.next != null && dest.next.sizeShow == 0)
            dest = dest.next;
        if (dest != wc) {
            ShiftColumn(wc, dest, wu);
            return dest;
        } else if (dest.next == null) { // wc == dest == end
            var wcNew = new WindowColumn(1f / sizeAll);
            InsertColumnToEnd(wcNew);
            ShiftColumn(wc, wcNew, wu);
            return wcNew;
        } else {
            var wcNew = new WindowColumn(1f / sizeAll);
            InsertColumnAfter(wcNew, dest);
            ShiftColumn(wc, wcNew, wu);
            return wcNew;
        }
    }

    protected void ShiftRow(WindowColumn wc, WindowUnit wuU, WindowUnit wuD) {
        if (wuU == null) {
            RefreshState = 0;
            return;
        }
        wuU.next = wuD.next;
        wuD.prev = wuU.prev;
        if (wc.begin == wuU)
            wc.begin = wuD;
        else
            wuU.prev.next = wuD;
        if (wc.end == wuD)
            wc.end = wuU;
        else
            wuD.next.prev = wuU;
        wuU.prev = wuD;
        wuD.next = wuU;
        wc.CalclayoutAll();
        if (!wuU.window.Query.IsMinimized() || !wuD.window.Query.IsMinimized()) {
            wc.CalclayoutShow();
            RefreshState = 1;
        } else {
            RefreshState = 0;
        }
    }

    protected void CalclayoutAll() {
        float percentAll = 0;
        for (var c = begin; c != null; c = c.next) {
            percentAll += c.percent;
        }
        float ratioAll = 1f / percentAll;
        for (var c = begin; c != null; c = c.next) {
            c.percent = c.percent * ratioAll;
        }
    }

    protected void CalclayoutShow() {
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

    public void ToggleMinimize(WindowColumn wc, WindowUnit wu) {
        if (wu.window.Query.IsMinimized()) {
            sumShow--;
            wc.sizeShow--;
            wc.CalclayoutShow();
            if (wc.sizeShow == 0) {
                sizeShow--;
                CalclayoutShow();
                RefreshState = 2;
                return;
            }
            RefreshState = 1;
            return;
        } else {
            sumShow++;
            wc.sizeShow++;
            wc.CalclayoutShow();
            if (wc.sizeShow == 1) {
                sizeShow++;
                CalclayoutShow();
                RefreshState = 2;
                return;
            }
            RefreshState = 1;
            return;
        }
    }

    public void ToggleMinimize(WindowColumn wc, WindowUnit wu, boolean isMinimize) {
        if (isMinimize) {
            sumShow--;
            wc.sizeShow--;
            wc.CalclayoutShow();
            if (wc.sizeShow == 0) {
                sizeShow--;
                CalclayoutShow();
                RefreshState = 2;
                return;
            }
            RefreshState = 1;
            return;
        } else {
            sumShow++;
            wc.sizeShow++;
            wc.CalclayoutShow();
            if (wc.sizeShow == 1) {
                sizeShow++;
                CalclayoutShow();
                RefreshState = 2;
                return;
            }
            RefreshState = 1;
            return;
        }
    }

    public void ResetLayout() {
        var percent = 1f / sizeAll;
        for (var c = begin; c != null; c = c.next) {
            c.percent = percent;
            c.ResetLayout();
        }
        CalclayoutShow();
    }

    public GridPosi ChangeLoc(WindowColumn wc, WindowUnit wu, float xf, float yf) {
        var c = begin;
        for (; c.percentShowTo < xf; c = c.next) {
        }
        var r = c.begin;
        for (; r.percentShowTo < yf; r = r.next) {
        }
        if (r != wu) {
            var temp = wu.window;
            wu.window = r.window;
            r.window = temp;
            return new GridPosi(c, r);
        }
        return null;
    }

    // TODO:
    public void ChangeSize(WindowColumn x, WindowUnit y) {}
}


class GridPosi {
    public WindowColumn x;
    public WindowUnit y;

    public GridPosi(WindowColumn x, WindowUnit y) {
        this.x = x;
        this.y = y;
    }
}


public class GridLayout implements ILayout {

    @Override
    public String Name() {
        return "grid";
    }

    private WindowGrid windowsGrid = new WindowGrid();
    private HashMap<Window, GridPosi> windowPosi = new HashMap<>();
    public Monitor monitor;
    private Rectangle screen;

    public GridLayout() {}

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
            windowPosi.put(window, windowsGrid.AddToNewColumn(window));
            if (!window.Query.IsMinimized())
                DoLayoutFull();
            return;
        }
        WindowColumn minNum = windowsGrid.begin;
        for (var c = minNum.next; c != null; c = c.next) {
            if (c.sizeAll < minNum.sizeAll && c.sizeAll < windowsGrid.sizeAll) {
                minNum = c;
            }
        }
        if (minNum.sizeAll < windowsGrid.sizeAll) {
            AddToColumn(window, minNum);
        } else {
            AddToNewColumn(window);
        }
    }

    public boolean RemoveWindow(Window window) {
        logger.info("GridLayout.RemoveWindow, {}", window);
        var gridPosi = windowPosi.remove(window);
        if (gridPosi == null)
            return false;

        windowsGrid.RemoveWindow(gridPosi.x, gridPosi.y);
        switch (windowsGrid.RefreshState) {
            case 1:
                DoLayoutColumn(gridPosi.x);
                break;
            case 2:
                DoLayoutFull();
                break;
        }
        return true;
    }

    public Set<Window> GetWindows() {
        return windowPosi.keySet();
    }

    public void DoLayoutFull() {
        if (screen == null)
            return;
        var hdwp = WindowStaticAction.HDWP4SetLoc.BeginSetLocation(windowsGrid.sumShow);
        for (var c = windowsGrid.begin; c != null; c = c.next) {
            if (c.sizeShow != 0) {
                DoLayoutColumnImpl0(hdwp, c);
                // DoLayoutColumnImpl1(c);
                // DoLayoutColumnImpl2(c);
            }
        }
        hdwp.EndSetLocation();
    }

    public void DoLayoutColumn(WindowColumn column) {
        if (screen == null)
            return;
        var hdwp = WindowStaticAction.HDWP4SetLoc.BeginSetLocation(column.sizeShow);
        DoLayoutColumnImpl0(hdwp, column);
        hdwp.EndSetLocation();

        // DoLayoutColumnImpl1(column);
        // DoLayoutColumnImpl2(column);
    }

    private void DoLayoutColumnImpl0(HDWP4SetLoc hdwp, WindowColumn column) {
        float percentFrom = column.prev == null ? 0f : column.prev.percentShowTo;
        float percentTo = column.percentShowTo;

        var rect = new Rectangle(screen.x + (int) (screen.width * percentFrom), screen.y,
                (int) (screen.width * (percentTo - percentFrom)), 0);
        float percentShowFrom = 0;
        for (var u = column.begin; u != null; u = u.next) {
            if (!u.window.Query.IsMinimized()) {
                rect.y += rect.height;
                rect.height = (int) (screen.height * (u.percentShowTo - percentShowFrom));
                percentShowFrom = u.percentShowTo;
                hdwp.SetLocation(u.window, rect);
            }
        }
    }

    private void DoLayoutColumnImpl1(WindowColumn column) {
        float percentFrom = column.prev == null ? 0f : column.prev.percentShowTo;
        float percentTo = column.percentShowTo;

        var rect = new Rectangle(screen.x + (int) (screen.width * percentFrom), screen.y,
                (int) (screen.width * (percentTo - percentFrom)), 0);
        float percentShowFrom = 0;
        for (var u = column.begin; u != null; u = u.next) {
            if (!u.window.Query.IsMinimized()) {
                rect.y += rect.height;
                rect.height = (int) (screen.height * (u.percentShowTo - percentShowFrom));
                percentShowFrom = u.percentShowTo;
                u.window.Action.SetLocation1(rect);
            }
        }
    }

    private void DoLayoutColumnImpl2(WindowColumn column) {
        float percentFrom = column.prev == null ? 0f : column.prev.percentShowTo;
        float percentTo = column.percentShowTo;

        var rect = new Rectangle(screen.x + (int) (screen.width * percentFrom), screen.y,
                (int) (screen.width * (percentTo - percentFrom)), 0);
        float percentShowFrom = 0;
        for (var u = column.begin; u != null; u = u.next) {
            if (!u.window.Query.IsMinimized()) {
                rect.y += rect.height;
                rect.height = (int) (screen.height * (u.percentShowTo - percentShowFrom));
                percentShowFrom = u.percentShowTo;
                WindowStaticAction.SetLocation2(u.window, rect);
            }
        }
    }

    private void AddToNewColumn(Window window) {
        var grid = windowsGrid.AddToNewColumn(window);
        windowPosi.put(window, grid);
        switch (windowsGrid.RefreshState) {
            case 1:
                DoLayoutColumn(grid.x);
                break;
            case 2:
                DoLayoutFull();
                break;
        }
    }

    private void AddToColumn(Window window, WindowColumn column) {
        windowPosi.put(window, windowsGrid.AddToColumn(column, window));
        switch (windowsGrid.RefreshState) {
            case 1:
                DoLayoutColumn(column);
                break;
            case 2:
                DoLayoutFull();
                break;
        }
    }

    public void AreaExpand(Window window) {
        logger.info("GridLayout.AreaExpand, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;

        windowsGrid.AreaExpand(gridPosi.x, gridPosi.y);
        DoLayoutFull();
    }

    public void AreaShrink(Window window) {
        logger.info("GridLayout.AreaShrink, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;

        windowsGrid.AreaShrink(gridPosi.x, gridPosi.y);
        DoLayoutFull();
    }

    public void ShiftLeft(Window window) {
        logger.info("GridLayout.ShiftLeft, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;

        var wcNew = windowsGrid.ShiftLeft(gridPosi.x, gridPosi.y);
        if (wcNew != null) {
            windowPosi.put(window, new GridPosi(wcNew, gridPosi.y));
            if (windowsGrid.RefreshState == 2)
                DoLayoutFull();
        }
    }

    public void ShiftRight(Window window) {
        logger.info("GridLayout.ShiftRight, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;

        var wcNew = windowsGrid.ShiftRight(gridPosi.x, gridPosi.y);
        if (wcNew != null) {
            windowPosi.put(window, new GridPosi(wcNew, gridPosi.y));
            if (windowsGrid.RefreshState == 2)
                DoLayoutFull();
        }
    }

    public void ShiftUp(Window window) {
        logger.info("GridLayout.ShiftLeft, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;

        if (gridPosi.y.prev == null)
            return;

        windowsGrid.ShiftRow(gridPosi.x, gridPosi.y.prev, gridPosi.y);
        if (windowsGrid.RefreshState == 1)
            DoLayoutColumn(gridPosi.x);
    }

    public void ShiftDown(Window window) {
        logger.info("GridLayout.ShiftLeft, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;

        if (gridPosi.y.next == null)
            return;

        windowsGrid.ShiftRow(gridPosi.x, gridPosi.y, gridPosi.y.next);
        if (windowsGrid.RefreshState == 1)
            DoLayoutColumn(gridPosi.x);
    }

    @Override
    public void ResetLayout() {
        windowsGrid.ResetLayout();
        DoLayoutFull();
    }

    @Override
    public boolean WindowAdd(Window window) {
        logger.info("GridLayout.WindowAdd, {}", window);
        if (windowsGrid.sizeAll == 0) {
            windowPosi.put(window, windowsGrid.AddToNewColumn(window));
            if (!window.Query.IsMinimized())
                DoLayoutFull();
            return true;
        }
        WindowColumn minNum = windowsGrid.begin;
        for (var c = minNum.next; c != null; c = c.next) {
            if (c.sizeAll < minNum.sizeAll && c.sizeAll < windowsGrid.sizeAll) {
                minNum = c;
            }
        }
        if (minNum.sizeAll < windowsGrid.sizeAll) {
            AddToColumn(window, minNum);
        } else {
            AddToNewColumn(window);
        }
        return true;
    }

    @Override
    public boolean WindowRemove(Window window) {
        logger.info("GridLayout.WindowRemove, {}", window);
        var gridPosi = windowPosi.remove(window);
        if (gridPosi == null)
            return false;

        windowsGrid.RemoveWindow(gridPosi.x, gridPosi.y);
        switch (windowsGrid.RefreshState) {
            case 1:
                DoLayoutColumn(gridPosi.x);
                break;
            case 2:
                DoLayoutFull();
                break;
        }
        return true;
    }

    @Override
    public boolean ToggleLayout(Window window) {
        logger.info("GridLayout.WindowToggleLayout, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null) {
            return WindowAdd(window);
        } else {
            return WindowRemove(window);
        }
    }

    @Override
    public void ToggleMinimize(Window window, boolean isMinimize) {
        logger.info("ToggleMinimize, {}, {}", window, isMinimize);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;
        windowsGrid.ToggleMinimize(gridPosi.x, gridPosi.y, isMinimize);

        switch (windowsGrid.RefreshState) {
            case 1:
                DoLayoutColumn(gridPosi.x);
                break;
            case 2:
                DoLayoutFull();
                break;
        }
    }

    // Use The Average of Mouse Loc and Window Center Loc
    @Override
    public void WindowMove(Window window, Point mouse) {
        logger.info("WindowMove, {}", window);
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;
        float xf = (float) (mouse.x - screen.x) / screen.width;
        float yf = (float) (mouse.y - screen.y) / screen.height;
        if (xf < 0)
            xf = 0;
        else if (xf > 1)
            xf = 1;
        if (yf < 0)
            yf = 0;
        else if (yf > 1)
            yf = 1;
        var swapPosi = windowsGrid.ChangeLoc(gridPosi.x, gridPosi.y, xf, yf);
        if (swapPosi != null) {
            windowPosi.put(window, swapPosi);
            windowPosi.put(gridPosi.y.window, gridPosi);
        }
        DoLayoutFull();
    }

    // TODO:
    @Override
    public void WindowResize(Window window) {
        var gridPosi = windowPosi.get(window);
        if (gridPosi == null)
            return;
        windowsGrid.ChangeSize(gridPosi.x, gridPosi.y);
        switch (windowsGrid.RefreshState) {
            case 1:
                DoLayoutColumn(gridPosi.x);
                break;
            case 2:
                DoLayoutFull();
                break;
        }
    }
}
