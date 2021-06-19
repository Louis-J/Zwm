package pers.louisj.Zwm.Core.L0.SysHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.WinEventProc;
import com.sun.jna.platform.win32.WinUser.HHOOK;

// import com.sun.jna.platform.win32.WinUser.HOOKPROC;
// import com.sun.jna.platform.win32.WinDef.LRESULT;
// import com.sun.jna.platform.win32.WinDef.WPARAM;
// import com.sun.jna.platform.win32.WinDef.LPARAM;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDEvent;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L2.Window.Window;
// import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.Async.ChannelList;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

public class SysHookManager {

    private static final int EVENT_OBJECT_DESTROY = 0x8001;
    private static final int EVENT_OBJECT_SHOW = 0x8002;

    private static final int EVENT_OBJECT_CLOAKED = 0x8017;
    private static final int EVENT_OBJECT_UNCLOAKED = 0x8018;

    private static final int EVENT_SYSTEM_MINIMIZESTART = 0x0016;
    private static final int EVENT_SYSTEM_MINIMIZEEND = 0x0017;

    // private static final int EVENT_SYSTEM_MOVESIZESTART = 0x000A;
    private static final int EVENT_SYSTEM_MOVESIZEEND = 0x000B;

    private static final int EVENT_SYSTEM_FOREGROUND = 0x0003;

    private static final int EVENT_OBJECT_NAMECHANGE = 0x800C;

    // private static final int WH_MOUSE_LL = 14;

    private static Logger logger = LogManager.getLogger("WindowHookManager");

    // public List<Channel<Message>> eventChans = new ArrayList<>();
    public ChannelList<Message> eventChans = new ChannelList<>();

    public Map<HWND, Window> windows = new HashMap<>();

    private List<HANDLE> hooks = new ArrayList<>();
    private List<HHOOK> hookexs = new ArrayList<>();

    public SysHookManager() {
    }

    public void Init() {
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_OBJECT_DESTROY, EVENT_OBJECT_SHOW, new HMODULE(),
                windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_OBJECT_CLOAKED, EVENT_OBJECT_UNCLOAKED, new HMODULE(),
                windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_OBJECT_NAMECHANGE, EVENT_OBJECT_NAMECHANGE,
                new HMODULE(), windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_SYSTEM_MINIMIZESTART, EVENT_SYSTEM_MINIMIZEEND,
                new HMODULE(), windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_SYSTEM_MOVESIZEEND, EVENT_SYSTEM_MOVESIZEEND,
                new HMODULE(), windowHook, 0, 0, 0));
        hooks.add(WinHelper.MyUser32Inst.SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_SYSTEM_FOREGROUND,
                new HMODULE(), windowHook, 0, 0, 0));

        // hookexs.add(WinHelper.MyUser32Inst.SetWindowsHookEx(WH_MOUSE_LL, mouseHook,
        // new HMODULE(), 0));

        List<HWND> hwnds = new ArrayList<>();
        WinHelper.MyUser32Inst.EnumWindows((hwnd, param) -> {
            hwnds.add(hwnd);
            return true;
        }, null);
        WindowRegisterInit(hwnds);
    }

    public void Start() {
    }

    public void Defer() {
        logger.info("WindowHookManager Defer Start");
        for (var h : hooks)
            WinHelper.MyUser32Inst.UnhookWinEvent(h);
        for (var h : hookexs)
            WinHelper.MyUser32Inst.UnhookWindowsHookEx(h);

        logger.info("WindowHookManager Defer End");
    }

    public WinEventProc windowHook = new WinEventProc() {
        public void callback(HANDLE hWinEventHook, DWORD event, HWND hwnd, LONG idObject, LONG idChild,
                DWORD dwEventThread, DWORD dwmsEventTime) {
            // logger.info("WindowHook - , {}", Integer.toHexString(event.intValue()));
            if (EventWindowIsValid(idChild, idObject, hwnd)) {
                switch (event.intValue()) {
                    case EVENT_OBJECT_SHOW:
                        WindowRegister(hwnd);
                        break;
                    case EVENT_OBJECT_DESTROY:
                        WindowUnregister(hwnd);
                        break;
                    // TODO: For Debug, Never See
                    case EVENT_OBJECT_CLOAKED: {
                        var window = windows.get(hwnd);
                        logger.error("WinEventProc, event = EVENT_OBJECT_CLOAKED, window = {}", window);
                        break;
                    }
                    case EVENT_OBJECT_UNCLOAKED: {
                        var window = windows.get(hwnd);
                        logger.error("WinEventProc, event = EVENT_OBJECT_UNCLOAKED, window = {}", window);
                        break;
                    }
                    case EVENT_SYSTEM_MINIMIZESTART: {
                        var window = windows.get(hwnd);
                        if (window != null)
                            eventChans.put(new VDMessage(VDEvent.WindowMinimizeStart, window));
                        break;
                    }
                    case EVENT_SYSTEM_MINIMIZEEND: {
                        var window = windows.get(hwnd);
                        if (window != null)
                            eventChans.put(new VDMessage(VDEvent.WindowMinimizeEnd, window));
                        break;
                    }
                    case EVENT_SYSTEM_FOREGROUND:
                        eventChans.put(new VDManMessage(VDManEvent.Foreground, windows.get(hwnd)));
                        break;
                    case EVENT_SYSTEM_MOVESIZEEND: {
                        var window = windows.get(hwnd);
                        if (window != null) {
                            eventChans.put(new VDMessage(VDEvent.WindowUpdateLocation, window));
                        }
                        break;
                    }
                    case EVENT_OBJECT_NAMECHANGE:
                        // TODO: Channel for Plugin
                        break;
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

    private void WindowRegisterInit(List<HWND> hwnds) {
        logger.info("WindowRegisterInit, hwnds = {}", hwnds);
        List<Window> registered = new ArrayList<>();
        for (var hwnd : hwnds) {
            if (windows.get(hwnd) == null && Window.QueryStatic.IsAppWindow(hwnd)) {
                int id = Window.QueryStatic.GetWindowPid(hwnd);

                // TODO: Why?
                if (id == 0) {
                    logger.error("WindowRegisterInit, ID == 0, hwnd = {}", hwnd);
                    return;
                }
                var window = new Window(hwnd, id);
                windows.put(hwnd, window);
                registered.add(window);
            }
        }
        eventChans.put(new VDManMessage(VDManEvent.WindowAddInit, registered));
    }

    private void WindowRegister(HWND hwnd) {
        logger.info("WindowRegister, hwnd = {}", hwnd);
        if (windows.get(hwnd) == null && Window.QueryStatic.IsAppWindow(hwnd)) {
            int id = Window.QueryStatic.GetWindowPid(hwnd);

            // TODO: Why?
            if (id == 0) {
                logger.error("WindowRegister, ID == 0, hwnd = {}", hwnd);
                return;
            }
            var window = new Window(hwnd, id);
            windows.put(hwnd, window);

            eventChans.put(new VDManMessage(VDManEvent.WindowAdd, window));
        }
    }

    private void WindowUnregister(HWND hwnd) {
        var window = windows.remove(hwnd);
        if (window != null) {
            logger.info("WindowUnregister, window = {}", window);
            eventChans.put(new VDManMessage(VDManEvent.WindowRemove, window));
        }
    }

    // HOOKPROC mouseHook = new HOOKPROC() {
    // public LRESULT callback(int nCode, WPARAM wParam, LPARAM lParam) {
    // final int WM_LBUTTONUP = 0x0202;
    // if (nCode == 0 && wParam.intValue() == WM_LBUTTONUP) {
    // logger.info("MouseHook - WM_LBUTTONUP, {}, {}", wParam, lParam);

    // return new LRESULT(0);
    // // HandleWindowMoveEnd();
    // }
    // return WinHelper.MyUser32Inst.CallNextHookEx(null, nCode, wParam, lParam);
    // }
    // };

}
