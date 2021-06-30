package pers.louisj.Zwm.Core.L0.KeyBind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDEvent;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.WinApi.MyUser32;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.MSLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.LowLevelMouseProc;

import java.util.HashMap;

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
        hHookKey = WinHelper.MyUser32Inst.SetWindowsHookEx(MyUser32.WH_KEYBOARD_LL, keyHook,
                hModule, 0);
        hHookMouse = WinHelper.MyUser32Inst.SetWindowsHookEx(MyUser32.WH_MOUSE_LL, mouseHook,
                hModule, 0);
    }

    public void Defer() {
        if (hHookKey != null)
            WinHelper.MyUser32Inst.UnhookWindowsHookEx(hHookKey);
        if (hHookMouse != null)
            WinHelper.MyUser32Inst.UnhookWindowsHookEx(hHookMouse);
    }

    public void Register(String name, int funcKey, int key, CallBack callback) {
        Register(name, (byte) funcKey, (byte) key, callback);
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
            logger.info("DoKeyboardEvent, {}", GetKeybindString(funcKey, key));
            callback.Invoke();
            logger.info("DoKeyboardEvent End");
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
            return WinHelper.MyUser32Inst.CallNextHookEx(hHookKey, nCode, wParam,
                    lParam.getPointer());
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
        return WinHelper.MyUser32Inst.CallNextHookEx(hHookMouse, nCode, wParam,
                lParam.getPointer());
    }

    public void DefaultConfig() {
        Channel<Message> channelIn = context.mainloop.channelIn;

        Register("Turn Focused Window Left", KeyCode.FuncKey.LALT, KeyCode.VK_LEFT,
                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowLeft, null)));

        Register("Turn Focused Window Right", KeyCode.FuncKey.LALT, KeyCode.VK_RIGHT,
                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowRight, null)));

        Register("Turn Focused Window Up", KeyCode.FuncKey.LALT, KeyCode.VK_UP,
                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowUp, null)));

        Register("Turn Focused Window Down", KeyCode.FuncKey.LALT, KeyCode.VK_DOWN,
                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowDown, null)));

        Register("Close Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_ESCAPE,
                () -> WindowStaticAction.SendClose(WindowStaticAction.GetForegroundWindow()));

        Register("Minimize Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_A,
                () -> WindowStaticAction.ShowMinimized(WindowStaticAction.GetForegroundWindow()));

        Register("Maximize Focused Window and Minimize Others", KeyCode.FuncKey.LALT, KeyCode.VK_Z,
                () -> WindowStaticAction.ShowMaximized(WindowStaticAction.GetForegroundWindow()));

        for (byte i = 0; i < 9; i++) {
            var stri = String.valueOf(i + 1);
            var obji = Integer.valueOf(i);
            Register("Switch Focused Monitor to Virtual Desk " + stri, KeyCode.FuncKey.LALT,
                    KeyCode.VK_1 + i,
                    () -> channelIn.put(new VDManMessage(VDManEvent.SwitchToVD, obji)));
            Register("Move Focused Window to Virtual Desk " + stri,
                    KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL, KeyCode.VK_1 + i,
                    () -> channelIn.put(new VDManMessage(VDManEvent.FocusedWindowMoveTo, obji)));
        }

        Register("Switch Focused Monitor to Previous Virtual Desk",
                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL, KeyCode.VK_LEFT,
                () -> channelIn.put(new VDManMessage(VDManEvent.SwitchToVD, Integer.valueOf(-1))));

        Register("Switch Focused Monitor to Next Virtual Desk",
                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL, KeyCode.VK_RIGHT,
                () -> channelIn.put(new VDManMessage(VDManEvent.SwitchToVD, Integer.valueOf(-2))));

        Register("Move Focused Window to Previous Virtual Desk",
                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL | KeyCode.FuncKey.LWIN,
                KeyCode.VK_LEFT, () -> channelIn.put(
                        new VDManMessage(VDManEvent.FocusedWindowMoveTo, Integer.valueOf(-1))));

        Register("Move Focused Window to Next Virtual Desk",
                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL | KeyCode.FuncKey.LWIN,
                KeyCode.VK_RIGHT, () -> channelIn.put(
                        new VDManMessage(VDManEvent.FocusedWindowMoveTo, Integer.valueOf(-2))));

        Register("Reset Layout of Focused Virtual Desk", KeyCode.FuncKey.LALT, KeyCode.VK_R,
                () -> channelIn.put(new VDMessage(VDEvent.ResetLayout, null)));

        Register("Expand the Area of Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_OEM_PLUS,
                () -> channelIn.put(new VDMessage(VDEvent.AreaExpand, null)));

        Register("Shrink the Area of Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_OEM_MINUS,
                () -> channelIn.put(new VDMessage(VDEvent.AreaShrink, null)));

        Register("Toggle Tiling State for Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_T,
                () -> channelIn.put(new VDMessage(VDEvent.ToggleLayout, null)));

        Register("Exit The Program", KeyCode.FuncKey.LALT, KeyCode.VK_Q, () -> context.Exit());

        Register("Debug VD info", KeyCode.FuncKey.LALT, KeyCode.VK_X,
                () -> channelIn.put(new VDManMessage(VDManEvent.VDDebugInfo, null)));
    }
}
