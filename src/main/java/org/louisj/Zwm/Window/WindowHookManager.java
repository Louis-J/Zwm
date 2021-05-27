package org.louisj.Zwm.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.louisj.Zwm.WinApi.WinHelper;
import com.sun.jna.platform.win32.WinUser.WinEventProc;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.User32Util.MessageLoopThread;

import com.sun.jna.Pointer;

public class WindowHookManager {
    public interface WindowFocusCallBack {
        void Invoke(Window window);
    }

    public interface WindowDestroyCallBack {
        void Invoke(Window window);
    }

    public interface WindowCreateCallBack {
        void Invoke(Window window);
    }

    public interface WindowUpdateCallBack {
        void Invoke(Window window, byte updateType);
    }

    final int EVENT_OBJECT_DESTROY = 0x8001;
    final int EVENT_OBJECT_SHOW = 0x8002;

    final int EVENT_OBJECT_CLOAKED = 0x8017;
    final int EVENT_OBJECT_UNCLOAKED = 0x8018;

    final int EVENT_SYSTEM_MINIMIZESTART = 0x0016;
    final int EVENT_SYSTEM_MINIMIZEEND = 0x0017;

    final int EVENT_SYSTEM_MOVESIZESTART = 0x000A;
    final int EVENT_SYSTEM_MOVESIZEEND = 0x000B;

    final int EVENT_SYSTEM_FOREGROUND = 0x0003;

    final int EVENT_OBJECT_LOCATIONCHANGE = 0x800B;

    final int WH_MOUSE_LL = 14;

    private static Logger logger = LogManager.getLogger("WindowHookManager");

    public List<WindowFocusCallBack> eventWindowFocus = new ArrayList<>();
    public List<WindowDestroyCallBack> eventWindowDestroy = new ArrayList<>();
    public List<WindowCreateCallBack> eventWindowCreate = new ArrayList<>();
    public List<WindowUpdateCallBack> eventWindowUpdate = new ArrayList<>();

    public Map<HWND, Window> windows = new HashMap<>();

    private List<HANDLE> hooks = new ArrayList<>();
    private List<HHOOK> hookexs = new ArrayList<>();

    private MessageLoopThread mainloop = new MessageLoopThread();

    public WindowHookManager() {
    }

    public void Init() {
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_OBJECT_DESTROY, EVENT_OBJECT_SHOW, new HMODULE(),
                windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_OBJECT_CLOAKED, EVENT_OBJECT_UNCLOAKED, new HMODULE(),
                windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_SYSTEM_MINIMIZESTART, EVENT_SYSTEM_MINIMIZEEND,
                new HMODULE(), windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_SYSTEM_MOVESIZEEND, EVENT_SYSTEM_MOVESIZEEND,
                new HMODULE(), windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_SYSTEM_FOREGROUND,
                new HMODULE(), windowHook, 0, 0, 0));
        // hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_OBJECT_LOCATIONCHANGE,
        // EVENT_OBJECT_LOCATIONCHANGE,
        // new HMODULE(), windowHook, 0, 0, 0));

        // hookexs.add(WinHelper.MyUser32Inst.SetWindowsHookEx(WH_MOUSE_LL, mouseHook,
        // new HMODULE(), 0));

        WinHelper.MyUser32Inst.EnumWindows((handle, param) -> {
            // long hvalue = Pointer.nativeValue(handle.getPointer());
            // logger.info("EnumWindows, handle = {}", hvalue);
            // if (Window.IsAppWindow(handle))
                RegisterWindow(handle);
            return true;
        }, null);

        mainloop.setName("WindowHookManager Loop in " + mainloop.getName());
    }

    public void Start() {
        // logger.info("Run message loop in WindowHookManager");
        // mainloop.run();
    }

    public void Defer() {
        logger.info("WindowHookManager Defer Called");
        // mainloop.exit();

        for (var h : hooks)
            WinHelper.MyUser32Inst.UnhookWinEvent(h);
        for (var h : hookexs)
            WinHelper.MyUser32Inst.UnhookWindowsHookEx(h);
    }

    public WinEventProc windowHook = new WinEventProc() {
        public void callback(HANDLE hWinEventHook, DWORD event, HWND hwnd, LONG idObject, LONG idChild,
                DWORD dwEventThread, DWORD dwmsEventTime) {
            // logger.info("WindowHook - , {}", Integer.toHexString(event.intValue()));
            if (EventWindowIsValid(idChild, idObject, hwnd)) {
                switch (event.intValue()) {
                    case EVENT_OBJECT_SHOW:
                        RegisterWindow(hwnd);
                        break;
                    case EVENT_OBJECT_DESTROY:
                        UnregisterWindow(hwnd);
                        break;
                    case EVENT_OBJECT_CLOAKED:
                        UpdateWindow(hwnd, WindowUpdateType.Hide);
                        break;
                    case EVENT_OBJECT_UNCLOAKED:
                        UpdateWindow(hwnd, WindowUpdateType.Show);
                        break;
                    case EVENT_SYSTEM_MINIMIZESTART:
                        UpdateWindow(hwnd, WindowUpdateType.MinimizeStart);
                        break;
                    case EVENT_SYSTEM_MINIMIZEEND:
                        UpdateWindow(hwnd, WindowUpdateType.MinimizeEnd);
                        break;
                    case EVENT_SYSTEM_FOREGROUND:
                        UpdateWindow(hwnd, WindowUpdateType.Foreground);
                        break;
                    // case EVENT_SYSTEM_MOVESIZESTART:
                    // StartWindowMove(hwnd);
                    // break;
                    case EVENT_SYSTEM_MOVESIZEEND:
                        EndWindowMove(hwnd);
                        break;
                    // case EVENT_OBJECT_LOCATIONCHANGE:
                    // WindowMove(hwnd);
                    // break;
                }
            }

        }
    };

    private boolean EventWindowIsValid(LONG idChild, LONG idObject, HWND hwnd) {
        final long CHILDID_SELF = 0;
        final long OBJID_WINDOW = 0;
        return idChild.longValue() == CHILDID_SELF && idObject.longValue() == OBJID_WINDOW
                && Pointer.nativeValue(hwnd.getPointer()) != 0;
    }

    private void RegisterWindow(HWND handle) {
        if (windows.get(handle) == null && Window.IsAppWindow(handle)) {
            int id = Window.GetWindowPid(handle);

            logger.info("RegisterWindow, handle = {}, pid = {}", handle, id);
            if (id == 0)
                return;
            var window = new Window(handle, id);
            windows.put(handle, window);

            for (var e : eventWindowCreate)
                e.Invoke(window);
        }
    }

    private void UnregisterWindow(HWND handle) {
        var window = windows.remove(handle);
        if (window != null) {
            for (var e : eventWindowDestroy)
                e.Invoke(window);
        }
    }

    private void UpdateWindow(HWND handle, byte updateType) {
        var window = windows.get(handle);
        if (updateType == WindowUpdateType.Show) {
            if (windows != null)
                for (var e : eventWindowUpdate)
                    e.Invoke(window, updateType);
            else
                RegisterWindow(handle);
        } else if (updateType == WindowUpdateType.Hide) {
            if (windows != null) {
                // TODO:
                // if (!window.DidManualHide)
                // {
                // UnregisterWindow(handle);
                // } else
                // {
                // for (var e : eventWindowUpdate)
                // e.Invoke(window, updateType);
                // }
                for (var e : eventWindowUpdate)
                    e.Invoke(window, updateType);
            } else {
                for (var e : eventWindowUpdate)
                    e.Invoke(window, updateType);
            }
        }
    }

    // private void StartWindowMove(HWND handle) {
    // var window = windows.get(handle);
    // if (window != null) {
    // logger.info("StartWindowMove, {}", window);

    // for (var e : eventWindowUpdate)
    // e.Invoke(window, WindowUpdateType.MoveStart);
    // }
    // }

    private void EndWindowMove(HWND handle) {
        var window = windows.get(handle);
        if (window != null) {
            logger.info("EndWindowMove, {}", window);

            for (var e : eventWindowUpdate)
                e.Invoke(window, WindowUpdateType.MoveEnd);
        }
    }

    private void WindowMove(HWND handle) {
        var window = windows.get(handle);
        if (window != null) {
            for (var e : eventWindowUpdate)
                e.Invoke(window, WindowUpdateType.Move);
        }
    }

    HOOKPROC mouseHook = new HOOKPROC() {
        public LRESULT callback(int nCode, WPARAM wParam, LPARAM lParam) {
            final int WM_LBUTTONUP = 0x0202;
            if (nCode == 0 && wParam.intValue() == WM_LBUTTONUP) {
                logger.info("MouseHook - WM_LBUTTONUP, {}, {}", wParam, lParam);

                return new LRESULT(0);
                // HandleWindowMoveEnd();
            }
            return WinHelper.MyUser32Inst.CallNextHookEx(null, nCode, wParam, lParam);
        }
    };
}
