package pers.louisj.Zwm.Core.Window;

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

import pers.louisj.Zwm.Core.Message.Message;
import pers.louisj.Zwm.Core.Message.WindowMessage.WindowEvent;
import pers.louisj.Zwm.Core.Message.WindowMessage.WindowMessage;
import pers.louisj.Zwm.Core.Utils.Channel;
import pers.louisj.Zwm.Core.Utils.Channel2;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

public class WindowHookManager {
    private class HookMessage {
        public HWND hwnd;
        public WindowEvent event;

        public HookMessage(HWND hwnd, WindowEvent event) {
            this.hwnd = hwnd;
            this.event = event;
        }
    }

    private static final int EVENT_OBJECT_DESTROY = 0x8001;
    private static final int EVENT_OBJECT_SHOW = 0x8002;

    private static final int EVENT_OBJECT_CLOAKED = 0x8017;
    private static final int EVENT_OBJECT_UNCLOAKED = 0x8018;

    private static final int EVENT_SYSTEM_MINIMIZESTART = 0x0016;
    private static final int EVENT_SYSTEM_MINIMIZEEND = 0x0017;

    // private static final int EVENT_SYSTEM_MOVESIZESTART = 0x000A;
    private static final int EVENT_SYSTEM_MOVESIZEEND = 0x000B;

    private static final int EVENT_SYSTEM_FOREGROUND = 0x0003;

    // private static final int EVENT_OBJECT_LOCATIONCHANGE = 0x800B;

    // private static final int WH_MOUSE_LL = 14;

    private static Logger logger = LogManager.getLogger("WindowHookManager");

    public List<Channel<Message>> eventChans = new ArrayList<>();

    public Map<HWND, Window> windows = new HashMap<>();

    private List<HANDLE> hooks = new ArrayList<>();
    private List<HHOOK> hookexs = new ArrayList<>();

    private Thread messageLoop = new MessageLoop();
    private Channel<HookMessage> channelIn = new Channel2<>();

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
            channelIn.put(new HookMessage(handle, WindowEvent.Add));
            return true;
        }, null);
    }

    public void Start() {
        messageLoop.start();
    }

    public void Defer() {
        logger.info("WindowHookManager Defer Start");
        channelIn.put(null);
        for (var h : hooks)
            WinHelper.MyUser32Inst.UnhookWinEvent(h);
        for (var h : hookexs)
            WinHelper.MyUser32Inst.UnhookWindowsHookEx(h);

        try {
            messageLoop.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("WindowHookManager Defer End");
    }

    public WinEventProc windowHook = new WinEventProc() {
        public void callback(HANDLE hWinEventHook, DWORD event, HWND hwnd, LONG idObject, LONG idChild,
                DWORD dwEventThread, DWORD dwmsEventTime) {
            // logger.info("WindowHook - , {}", Integer.toHexString(event.intValue()));
            if (EventWindowIsValid(idChild, idObject, hwnd)) {
                switch (event.intValue()) {
                    case EVENT_OBJECT_SHOW:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.Add));
                        break;
                    case EVENT_OBJECT_DESTROY:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.Remove));
                        break;
                    case EVENT_OBJECT_CLOAKED:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.Hide));
                        break;
                    case EVENT_OBJECT_UNCLOAKED:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.Show));
                        break;
                    case EVENT_SYSTEM_MINIMIZESTART:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.MinimizeStart));
                        break;
                    case EVENT_SYSTEM_MINIMIZEEND:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.MinimizeEnd));
                        break;
                    case EVENT_SYSTEM_FOREGROUND:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.Foreground));
                        break;
                    // case EVENT_SYSTEM_MOVESIZESTART:
                    // StartWindowMove(hwnd);
                    // break;
                    case EVENT_SYSTEM_MOVESIZEEND:
                        channelIn.put(new HookMessage(hwnd, WindowEvent.MoveEnd));
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
        if (windows.get(handle) == null && Window.QueryStatic.IsAppWindow(handle)) {
            int id = Window.QueryStatic.GetWindowPid(handle);
            logger.info("RegisterWindow, handle = {}, pid = {}", handle, id);

            if (id == 0)
                return;
            var window = new Window(handle, id);
            windows.put(handle, window);

            for (var e : eventChans)
                e.put(new WindowMessage(window, WindowEvent.Add));
        }
    }

    private void UnregisterWindow(HWND handle) {
        var window = windows.remove(handle);
        if (window != null) {
            logger.info("UnregisterWindow, window = {}", window);

            for (var e : eventChans)
                e.put(new WindowMessage(window, WindowEvent.Remove));
        }
    }

    private void UpdateWindow(HWND handle, WindowEvent updateType) {
        var window = windows.get(handle);
        if (window == null) {
            // RegisterWindow(handle);
            if (updateType == WindowEvent.Foreground)
                for (var e : eventChans)
                    e.put(new WindowMessage(null, updateType));
        } else {
            for (var e : eventChans)
                e.put(new WindowMessage(window, updateType));
        }
    }

    private void EndWindowMove(HWND handle) {
        var window = windows.get(handle);
        if (window != null) {
            logger.info("EndWindowMove, {}", window);

            for (var e : eventChans)
                e.put(new WindowMessage(window, WindowEvent.MoveEnd));
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

    private class MessageLoop extends Thread {
        public MessageLoop() {
            super();
            setName("WinHookMan Thread");
        }

        @Override
        public void run() {
            while (true) {
                HookMessage msg = channelIn.take();
                if (msg == null) {
                    // Exit
                    return;
                }
                switch (msg.event) {
                    case Add:
                        RegisterWindow(msg.hwnd);
                        break;
                    case Remove:
                        UnregisterWindow(msg.hwnd);
                        break;
                    case MoveEnd:
                        EndWindowMove(msg.hwnd);
                        break;
                    default:
                        UpdateWindow(msg.hwnd, msg.event);
                        break;
                }
            }
        }
    }
}
