package pers.louisj.Zwm.Core.KeyBind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Message.Message;
import pers.louisj.Zwm.Core.Message.VirtualDeskMessage.VirtualDeskEvent;
import pers.louisj.Zwm.Core.Message.VirtualDeskMessage.VirtualDeskMessage;
import pers.louisj.Zwm.Core.Utils.Channel;
import pers.louisj.Zwm.Core.WinApi.MyUser32;
import pers.louisj.Zwm.Core.WinApi.WinHelper;

// import com.sun.jna.platform.win32.COM.;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.Pointer;

// import java.util.ArrayList;
import java.util.HashMap;

import com.sun.jna.Native;
// import com.sun.jna.W32API;
import com.sun.jna.platform.win32.WinDef.HMODULE;
// import com.sun.jna.platform.win32;

// import com.sun.jna.platform.win32.WinDef.HINSTANCE;
// import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
// import com.sun.jna.platform.win32.WinUser.HOOKPROC;
// import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinUser.MSLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.LowLevelMouseProc;

// class Callback {
// public static MyUser32.HHOOK hHook;
// public static MyUser32.LowLevelKeyboardProc lpfn;
// public static volatile boolean quit = false;

// public static void main(String[] args) throws Exception {
// HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
// lpfn = new MyUser32.LowLevelKeyboardProc() {
// public LRESULT callback(int nCode, WPARAM wParam, MyUser32.KBDLLHOOKSTRUCT
// lParam) {
// System.out.println("here");
// quit = true;

// // var outLParam = new LPARAM(Pointer.nativeValue(lParam.getPointer()));
// // return WinHelper.MyUser32Inst.CallNextHookEx(hHook, nCode, wParam, outLParam);
// return WinHelper.MyUser32Inst.CallNextHookEx(hHook, nCode, wParam,
// lParam.getPointer());
// }
// };

// hHook = WinHelper.MyUser32Inst.SetWindowsHookEx(User32.WH_KEYBOARD_LL, lpfn, hMod,
// 0);
// if (hHook == null)
// return;
// // MyUser32.MSG msg = new MyUser32.MSG();
// // while (!quit) {
// // WinHelper.MyUser32Inst.PeekMessage(msg, null, 0, 0, 0);
// // Thread.sleep(100);
// // }
// // if (User32.INSTANCE.UnhookWindowsHookEx(hHook))
// // System.out.println("Unhooked");

// int result;
// MyUser32.MSG msg = new MyUser32.MSG();
// while ((result = WinHelper.MyUser32Inst.GetMessage(msg, null, 0, 0)) != 0) {
// if (result == -1) {
// System.err.println("error in GetMessage");
// break;
// }

// WinHelper.MyUser32Inst.TranslateMessage(msg);
// WinHelper.MyUser32Inst.DispatchMessage(msg);
// }

// WinHelper.MyUser32Inst.UnhookWindowsHookEx(hHook);
// }
// }

public class KeybindManager {
    public interface CallBack {
        public void Invoke();
    };

    private static Logger logger = LogManager.getLogger("KeybindManager");
    private Context context;

    private static HHOOK hHookKey = null;
    private static HHOOK hHookMouse = null;

    private LowLevelKeyboardProc keyHook = new MyKeyHookProc();
    private LowLevelMouseProc mouseHook = new MyUser32.LowLevelMouseProc() {
        public LRESULT callback(int nCode, WPARAM wParam, MSLLHOOKSTRUCT lParam) {
            return MouseBindCallBack(nCode, wParam, lParam);
        }
    };;

    private HashMap<Short, CallBack> keybinds = new HashMap<>();
    private HashMap<Short, CallBack> mousebinds = new HashMap<>();

    private HashMap<Short, String> keybindMap = new HashMap<>();
    private HashMap<Short, String> mousebindMap = new HashMap<>();

    public KeybindManager(Context context) {
        this.context = context;
    }

    public void Start() {
        logger.info("The KeybindMan Is Registed");
        HMODULE hModule = WinHelper.Kernel32Inst.GetModuleHandle(null);
        hHookKey = WinHelper.MyUser32Inst.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyHook, hModule, 0);
        hHookMouse = WinHelper.MyUser32Inst.SetWindowsHookEx(User32.WH_MOUSE_LL, mouseHook, hModule, 0);
    }

    public void Defer() {
        if (hHookKey != null)
            WinHelper.MyUser32Inst.UnhookWindowsHookEx(hHookKey);
        if (hHookMouse != null)
            WinHelper.MyUser32Inst.UnhookWindowsHookEx(hHookMouse);
    }

    public void Register(String name, byte funcKey, byte key, CallBack callback) {
        short realkeys = (short) ((short) 256 * funcKey + key);
        if (keybinds.containsKey(realkeys)) {
            logger.warn("The Keybind (" + GetKeybindString(funcKey, key) + ") Is Dublicated");
        }
        keybinds.put(realkeys, callback);
        keybindMap.put(realkeys, name);
    }

    public void Register(String name, short mouseEvent, CallBack callback) {
        if (mousebinds.containsKey(mouseEvent)) {
            logger.warn("The MouseBind (" + mouseEvent + ") Is Dublicated");
        }
        mousebinds.put(mouseEvent, callback);
        mousebindMap.put(mouseEvent, name);
    }

    public void Unregister(byte funcKey, byte key) {
        short realkeys = (short) ((short) 256 * funcKey + key);
        if (!keybinds.containsKey(realkeys)) {
            logger.warn("The Keybind (" + GetKeybindString(funcKey, key) + ") Does Not Exist");
        }
        keybinds.remove(realkeys);
    }

    public void Unregister(short mouseEvent) {
        if (!mousebinds.containsKey(mouseEvent)) {
            logger.warn("The MouseBind (" + mouseEvent + ") Does Not Exist");
        }
        mousebinds.remove(mouseEvent);
    }

    public void UnsubscribeAll() {
        keybinds.clear();
        mousebinds.clear();
    }

    private boolean DoKeyboardEvent(byte funcKey, byte key) {
        short realkeys = (short) ((short) 256 * funcKey + key);
        var callback = keybinds.get(realkeys);
        if (callback != null) {
            // callback.Invoke();
            logger.info("DoKeyboardEvent, {}", GetKeybindString(funcKey, key));
            return true;
        }
        return false;
    }

    private boolean DoMouseEvent(short mouseEvent) {
        var callback = mousebinds.get(mouseEvent);
        if (callback != null) {
            // callback.Invoke();
            logger.info("DoMouseEvent, {}", mouseEvent);
            return true;
        }
        return false;
    }

    private String GetKeybindString(byte funcKey, byte key) {
        String str = new String();

        if ((funcKey & KeyCode.FuncKey.LCONTROL) != 0 && (funcKey & KeyCode.FuncKey.RCONTROL) != 0)
            str += "Ctrl + ";
        else if ((funcKey & KeyCode.FuncKey.LCONTROL) != 0)
            str += "LCtrl + ";
        else if ((funcKey & KeyCode.FuncKey.RCONTROL) != 0)
            str += "RCtrl + ";

        if ((funcKey & KeyCode.FuncKey.LWIN) != 0 && (funcKey & KeyCode.FuncKey.RWIN) != 0)
            str += "Win + ";
        else if ((funcKey & KeyCode.FuncKey.LWIN) != 0)
            str += "LWin + ";
        else if ((funcKey & KeyCode.FuncKey.RWIN) != 0)
            str += "RWin + ";

        if ((funcKey & KeyCode.FuncKey.LALT) != 0 && (funcKey & KeyCode.FuncKey.RALT) != 0)
            str += "Alt + ";
        else if ((funcKey & KeyCode.FuncKey.LALT) != 0)
            str += "LAlt + ";
        else if ((funcKey & KeyCode.FuncKey.RALT) != 0)
            str += "RAlt + ";

        if ((funcKey & KeyCode.FuncKey.LSHIFT) != 0 && (funcKey & KeyCode.FuncKey.RSHIFT) != 0)
            str += "Shift + ";
        else if ((funcKey & KeyCode.FuncKey.LSHIFT) != 0)
            str += "LShift + ";
        else if ((funcKey & KeyCode.FuncKey.RSHIFT) != 0)
            str += "RShift + ";

        str += KeyCode.KeyToString(key);

        return str;
    }

    protected class MyKeyHookProc implements LowLevelKeyboardProc {
        byte funcKeyState = 0;
        public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT lParam) {
            if (nCode == 0) {
                int event = wParam.intValue();
                if (event == MyUser32.WM_KEYUP || event == MyUser32.WM_SYSKEYUP) {
                    byte key = (byte) lParam.vkCode;
                    switch (key) {
                        case KeyCode.VK_LSHIFT -> funcKeyState &= ~KeyCode.FuncKey.LSHIFT;
                        case KeyCode.VK_RSHIFT -> funcKeyState &= ~KeyCode.FuncKey.RSHIFT;
                        case KeyCode.VK_LCONTROL -> funcKeyState &= ~KeyCode.FuncKey.LCONTROL;
                        case KeyCode.VK_RCONTROL -> funcKeyState &= ~KeyCode.FuncKey.RCONTROL;
                        case KeyCode.VK_LMENU -> funcKeyState &= ~KeyCode.FuncKey.LALT;
                        case KeyCode.VK_RMENU -> funcKeyState &= ~KeyCode.FuncKey.RALT;
                        case KeyCode.VK_LWIN -> funcKeyState &= ~KeyCode.FuncKey.LWIN;
                        case KeyCode.VK_RWIN -> funcKeyState &= ~KeyCode.FuncKey.RWIN;
                    }
                    // logger.info("KeyBindCallBack, {}", funcKeyState);

                } else if (event == MyUser32.WM_KEYDOWN || event == MyUser32.WM_SYSKEYDOWN) {
                    boolean ifFuncKey = true;
                    byte key = (byte) lParam.vkCode;
                    switch (key) {
                        case KeyCode.VK_LSHIFT -> funcKeyState |= KeyCode.FuncKey.LSHIFT;
                        case KeyCode.VK_RSHIFT -> funcKeyState |= KeyCode.FuncKey.RSHIFT;
                        case KeyCode.VK_LCONTROL -> funcKeyState |= KeyCode.FuncKey.LCONTROL;
                        case KeyCode.VK_RCONTROL -> funcKeyState |= KeyCode.FuncKey.RCONTROL;
                        case KeyCode.VK_LMENU -> funcKeyState |= KeyCode.FuncKey.LALT;
                        case KeyCode.VK_RMENU -> funcKeyState |= KeyCode.FuncKey.RALT;
                        case KeyCode.VK_LWIN -> funcKeyState |= KeyCode.FuncKey.LWIN;
                        case KeyCode.VK_RWIN -> funcKeyState |= KeyCode.FuncKey.RWIN;
                        default -> ifFuncKey = false;
                    }
                    if (!ifFuncKey) {
                        // logger.info("KeyBindCallBack, {}, {}", funcKeyState,
                        // GetKeybindString(funcKeyState, key));
                        if (funcKeyState != 0 && DoKeyboardEvent(funcKeyState, key)) {
                            return new LRESULT(1);
                        }
                    }
                }
            }
            return WinHelper.MyUser32Inst.CallNextHookEx(hHookKey, nCode, wParam, lParam.getPointer());
        }
    }

    private LRESULT MouseBindCallBack(int nCode, WPARAM wParam, MyUser32.MSLLHOOKSTRUCT lParam) {
        if (nCode == 0) {
            boolean hasdo;
            switch (wParam.shortValue()) {
                case MouseCode.WM_LBUTTONDOWN:
                case MouseCode.WM_LBUTTONUP:
                case MouseCode.WM_MOUSEMOVE:
                case MouseCode.WM_MOUSEWHEEL:
                case MouseCode.WM_MOUSEHWHEEL:
                case MouseCode.WM_RBUTTONDOWN:
                case MouseCode.WM_RBUTTONUP:
                    hasdo = DoMouseEvent(wParam.shortValue());
                default:
                    hasdo = false;
            }
            if (hasdo)
                return new LRESULT(1);
        }
        return WinHelper.MyUser32Inst.CallNextHookEx(hHookMouse, nCode, wParam, lParam.getPointer());
    }

    public void DefaultConfig() {
        Channel<Message> eventChanForVDM = context.vdMan.inputChan;

        Register("Turn Focused Window Left", KeyCode.FuncKey.LALT, KeyCode.VK_LEFT,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.TurnWindowLeft, null)));

        Register("Turn Focused Window Right", KeyCode.FuncKey.LALT, KeyCode.VK_RIGHT,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.TurnWindowRight, null)));

        Register("Turn Focused Window Up", KeyCode.FuncKey.LALT, KeyCode.VK_UP,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.TurnWindowUp, null)));

        Register("Turn Focused Window Down", KeyCode.FuncKey.LALT, KeyCode.VK_DOWN,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.TurnWindowDown, null)));

        Register("Close Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_ESCAPE,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.FocusedWindowClose, null)));

        Register("Minimize Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_A,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.FocusedWindowMinimize, null)));

        Register("Maximize Focused Window and Minimize Others", KeyCode.FuncKey.LALT, KeyCode.VK_Z,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.FocusedWindowMaximize, null)));

        for (byte i = 1; i <= 9; i++) {
            var obji = Integer.valueOf(i);
            Register("Switch Focused Monitor to Virtual Desk " + String.valueOf(i), KeyCode.FuncKey.LALT,
                    (byte) (KeyCode.VK_0 + i),
                    () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.SwitchToVirtualDesk, obji)));
            Register("Move Focused Window to Virtual Desk " + String.valueOf(i),
                    (byte) (KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL), (byte) (KeyCode.VK_0 + i),
                    () -> eventChanForVDM
                            .put(new VirtualDeskMessage(VirtualDeskEvent.SwitchWindowToVirtualDesk, obji)));
        }

        Register("Switch Focused Monitor to Previous Virtual Desk",
                (byte) (KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL), KeyCode.VK_LEFT,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.SwitchToPrevVirtualDesk, null)));

        Register("Switch Focused Monitor to Next Virtual Desk",
                (byte) (KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL), KeyCode.VK_RIGHT,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.SwitchToNextVirtualDesk, null)));

        Register("Move Focused Window to Previous Virtual Desk",
                (byte) (KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL | KeyCode.FuncKey.LWIN), KeyCode.VK_LEFT,
                () -> eventChanForVDM
                        .put(new VirtualDeskMessage(VirtualDeskEvent.SwitchWindowToPrevVirtualDesk, null)));

        Register("Move Focused Window to Next Virtual Desk",
                (byte) (KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL | KeyCode.FuncKey.LWIN), KeyCode.VK_RIGHT,
                () -> eventChanForVDM
                        .put(new VirtualDeskMessage(VirtualDeskEvent.SwitchWindowToNextVirtualDesk, null)));

        Register("Reset Layout of Focused Virtual Desk", KeyCode.FuncKey.LALT, KeyCode.VK_R,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.ResetLayout, null)));

        Register("Toggle Tiling State for Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_T,
                () -> eventChanForVDM.put(new VirtualDeskMessage(VirtualDeskEvent.ToggleTiling, null)));
    }
}
