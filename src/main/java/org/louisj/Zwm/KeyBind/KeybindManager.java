package org.louisj.Zwm.KeyBind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.louisj.Zwm.Context;
import org.louisj.Zwm.WinApi.MyUser32;

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
// // return MyUser32.INSTANCE.CallNextHookEx(hHook, nCode, wParam, outLParam);
// return MyUser32.INSTANCE.CallNextHookEx(hHook, nCode, wParam,
// lParam.getPointer());
// }
// };

// hHook = MyUser32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, lpfn, hMod,
// 0);
// if (hHook == null)
// return;
// // MyUser32.MSG msg = new MyUser32.MSG();
// // while (!quit) {
// // MyUser32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);
// // Thread.sleep(100);
// // }
// // if (User32.INSTANCE.UnhookWindowsHookEx(hHook))
// // System.out.println("Unhooked");

// int result;
// MyUser32.MSG msg = new MyUser32.MSG();
// while ((result = MyUser32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
// if (result == -1) {
// System.err.println("error in GetMessage");
// break;
// }

// MyUser32.INSTANCE.TranslateMessage(msg);
// MyUser32.INSTANCE.DispatchMessage(msg);
// }

// MyUser32.INSTANCE.UnhookWindowsHookEx(hHook);
// }
// }

public class KeybindManager {
    public interface CallBack {
        public void Invoke();
    };

    private static Logger logger = LogManager.getLogger("KeybindManager");
    // private Context context;

    private static MyUser32.HHOOK hHookKey = null;
    private static MyUser32.HHOOK hHookMouse = null;

    private MyUser32.LowLevelKeyboardProc keyHook = new MyUser32.LowLevelKeyboardProc() {
        public LRESULT callback(int nCode, WPARAM wParam, MyUser32.KBDLLHOOKSTRUCT lParam) {
            return KeyBindCallBack(nCode, wParam, lParam);
        }
    };
    private MyUser32.LowLevelMouseProc mouseHook = new MyUser32.LowLevelMouseProc() {
        public LRESULT callback(int nCode, WPARAM wParam, MSLLHOOKSTRUCT lParam) {
            return MouseBindCallBack(nCode, wParam, lParam);
        }
    };;

    private HashMap<Short, CallBack> keybinds = new HashMap<Short, CallBack>();
    private HashMap<Short, CallBack> mousebinds = new HashMap<Short, CallBack>();

    public KeybindManager(Context context) {
        // this.context = context;
    }

    public void Start() {
        logger.info("The KeybindMan Is Registed");
        HMODULE hModule = Kernel32.INSTANCE.GetModuleHandle(null);
        hHookKey = MyUser32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyHook, hModule, 0);
        hHookMouse = MyUser32.INSTANCE.SetWindowsHookEx(User32.WH_MOUSE_LL, mouseHook, hModule, 0);
    }

    public void Defer() {
        if (hHookKey != null)
            MyUser32.INSTANCE.UnhookWindowsHookEx(hHookKey);
        if (hHookMouse != null)
            MyUser32.INSTANCE.UnhookWindowsHookEx(hHookMouse);
    }

    public void Subscribe(byte key, byte funcKey, CallBack callback) {
        short realkeys = (short) ((short) 256 * key + funcKey);
        if (keybinds.containsKey(realkeys)) {
            logger.warn("The Keybind (" + GetKeybindString(key, funcKey) + ") Is Coverd");
        }
        keybinds.put(realkeys, callback);
    }

    public void Subscribe(short mouseEvent, CallBack callback) {
        if (mousebinds.containsKey(mouseEvent)) {
            logger.warn("The MouseBind (" + mouseEvent + ")  Is Coverd");
        }
        mousebinds.put(mouseEvent, callback);
    }

    public void Unsubscribe(byte key, byte funcKey) {
        short realkeys = (short) ((short) 256 * key + funcKey);
        if (!keybinds.containsKey(realkeys)) {
            logger.warn("The Keybind (" + GetKeybindString(key, funcKey) + ") Does Not Exist");
        }
        keybinds.remove(realkeys);
    }

    public void Unsubscribe(short mouseEvent) {
        if (!mousebinds.containsKey(mouseEvent)) {
            logger.warn("The MouseBind (" + mouseEvent + ") Does Not Exist");
        }
        mousebinds.remove(mouseEvent);
    }

    public void UnsubscribeAll() {
        keybinds.clear();
        mousebinds.clear();
    }

    private boolean DoKeyboardEvent(byte key, byte funcKey) {
        short realkeys = (short) ((short) 256 * key + funcKey);
        var callback = keybinds.get(realkeys);
        if (callback != null) {
            callback.Invoke();
            return true;
        }
        return false;
    }

    private boolean DoMouseEvent(short mouseEvent) {
        var callback = mousebinds.get(mouseEvent);
        if (callback != null) {
            callback.Invoke();
            return true;
        }
        return false;
    }

    private String GetKeybindString(byte key, byte funcKey) {
        String str = new String();

        if ((funcKey & KeyCode.VK_LCONTROL) != 0 && (funcKey & KeyCode.VK_RCONTROL) != 0)
            str += "Ctrl + ";
        else if ((funcKey & KeyCode.VK_LCONTROL) != 0)
            str += "LCtrl + ";
        else if ((funcKey & KeyCode.VK_RCONTROL) != 0)
            str += "RCtrl + ";

        if ((funcKey & KeyCode.VK_LWIN) != 0 && (funcKey & KeyCode.VK_RWIN) != 0)
            str += "Win + ";
        else if ((funcKey & KeyCode.VK_LWIN) != 0)
            str += "LWin";
        else if ((funcKey & KeyCode.VK_RWIN) != 0)
            str += "RWin";

        if ((funcKey & KeyCode.VK_LMENU) != 0 && (funcKey & KeyCode.VK_RMENU) != 0)
            str += "Alt";
        else if ((funcKey & KeyCode.VK_LMENU) != 0)
            str += "LAlt";
        else if ((funcKey & KeyCode.VK_RMENU) != 0)
            str += "RAlt";

        if ((funcKey & KeyCode.VK_LSHIFT) != 0 && (funcKey & KeyCode.VK_RSHIFT) != 0)
            str += "Shift";
        else if ((funcKey & KeyCode.VK_LSHIFT) != 0)
            str += "LShift";
        else if ((funcKey & KeyCode.VK_RSHIFT) != 0)
            str += "RShift";

        str += KeyCode.KeyToString(key);

        return str;
    }

    private LRESULT KeyBindCallBack(int nCode, WPARAM wParam, MyUser32.KBDLLHOOKSTRUCT lParam) {
        if (nCode == 0 && (wParam.intValue() == MyUser32.WM_KEYDOWN || wParam.intValue() == MyUser32.WM_SYSKEYDOWN)) {
            boolean ifFuncKey;
            byte key = (byte) lParam.vkCode;
            switch (key) {
                case KeyCode.VK_LSHIFT:
                case KeyCode.VK_RSHIFT:
                case KeyCode.VK_LCONTROL:
                case KeyCode.VK_RCONTROL:
                case KeyCode.VK_LMENU:
                case KeyCode.VK_RMENU:
                case KeyCode.VK_LWIN:
                case KeyCode.VK_RWIN:
                    ifFuncKey = true;
                default:
                    ifFuncKey = false;
            }
            if (!ifFuncKey) {
                byte pressdFuncKey = 0;
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_LSHIFT) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_LSHIFT);
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_RSHIFT) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_RSHIFT);
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_LCONTROL) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_LCONTROL);
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_RCONTROL) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_RCONTROL);
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_LMENU) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_LMENU);
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_RMENU) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_RMENU);
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_LWIN) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_LWIN);
                if ((MyUser32.INSTANCE.GetKeyState(KeyCode.VK_RWIN) & 0x8000) == 0x8000)
                    pressdFuncKey |= KeyCode.FuncKeyTrans(KeyCode.VK_RWIN);

                if (pressdFuncKey != 0 && DoKeyboardEvent(key, pressdFuncKey)) {
                    return new LRESULT(1);
                }
            }
        }
        // var outLParam = new LPARAM(Pointer.nativeValue(lParam.getPointer()));
        // return MyUser32.INSTANCE.CallNextHookEx(hHook, nCode, wParam, outLParam);
        return MyUser32.INSTANCE.CallNextHookEx(hHookKey, nCode, wParam, lParam.getPointer());
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
        return MyUser32.INSTANCE.CallNextHookEx(hHookMouse, nCode, wParam, lParam.getPointer());
    }
}
