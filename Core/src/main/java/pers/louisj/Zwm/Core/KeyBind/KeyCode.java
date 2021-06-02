package pers.louisj.Zwm.Core.KeyBind;

public class KeyCode {
    public static class FuncKey {
        public final static byte LSHIFT = (byte) (0x01);
        public final static byte RSHIFT = (byte) (0x02);
        public final static byte LCONTROL = (byte) (0x04);
        public final static byte RCONTROL = (byte) (0x08);
        public final static byte LALT = (byte) (0x10);
        public final static byte RALT = (byte) (0x20);
        public final static byte LWIN = (byte) (0x40);
        public final static byte RWIN = (byte) (0x80);
    }

    public static byte FuncKeyTrans(byte vkcode) {
        switch (vkcode) {
            case VK_LSHIFT:
                return (byte) (0x01);
            case VK_RSHIFT:
                return (byte) (0x02);
            case VK_LCONTROL:
                return (byte) (0x04);
            case VK_RCONTROL:
                return (byte) (0x08);
            case VK_LMENU: // LAlt
                return (byte) (0x10);
            case VK_RMENU: // RAlt
                return (byte) (0x20);
            case VK_LWIN:
                return (byte) (0x40);
            case VK_RWIN:
                return (byte) (0x80);

            // Dont Use VK_CONTROL, VK_SHIFT, or VK_MENU
            default:
                return 0;
        }
    }

    public final static byte VK_UNDEFINED = (byte) 0x00;

    /*
     * Virtual Keys, Standard Set
     */
    public final static byte VK_LBUTTON = (byte) 0x01;
    public final static byte VK_RBUTTON = (byte) 0x02;
    public final static byte VK_CANCEL = (byte) 0x03;
    public final static byte VK_MBUTTON = (byte) 0x04; /* NOT contiguous with L & RBUTTON */

    public final static byte VK_XBUTTON1 = (byte) 0x05; /* NOT contiguous with L & RBUTTON */
    public final static byte VK_XBUTTON2 = (byte) 0x06; /* NOT contiguous with L & RBUTTON */

    /*
     * 0x07 : reserved
     */
    public final static byte VK_RESERVED_07 = (byte) 0x07;

    public final static byte VK_BACK = (byte) 0x08;
    public final static byte VK_TAB = (byte) 0x09;

    /*
     * 0x0A - 0x0B : reserved
     */
    public final static byte VK_RESERVED_0A = (byte) 0x0A;
    public final static byte VK_RESERVED_0B = (byte) 0x0B;

    public final static byte VK_CLEAR = (byte) 0x0C;
    public final static byte VK_RETURN = (byte) 0x0D;

    /*
     * 0x0E - 0x0F : unassigned
     */
    public final static byte VK_UNASSIGNED_0E = (byte) 0x0E;
    public final static byte VK_UNASSIGNED_0F = (byte) 0x0F;

    public final static byte VK_SHIFT = (byte) 0x10;
    public final static byte VK_CONTROL = (byte) 0x11;
    public final static byte VK_MENU = (byte) 0x12;
    public final static byte VK_PAUSE = (byte) 0x13;
    public final static byte VK_CAPITAL = (byte) 0x14;

    public final static byte VK_KANA = (byte) 0x15;
    public final static byte VK_HANGEUL = (byte) 0x15; /* old name - should be here for compatibility */
    public final static byte VK_HANGUL = (byte) 0x15;

    /*
     * 0x16 : unassigned
     */
    public final static byte VK_UNASSIGNED_16 = (byte) 0x16;

    public final static byte VK_JUNJA = (byte) 0x17;
    public final static byte VK_FINAL = (byte) 0x18;
    public final static byte VK_HANJA = (byte) 0x19;
    public final static byte VK_KANJI = (byte) 0x19;

    /*
     * 0x1A : unassigned
     */
    public final static byte VK_UNASSIGNED_1A = (byte) 0x1A;

    public final static byte VK_ESCAPE = (byte) 0x1B;

    public final static byte VK_CONVERT = (byte) 0x1C;
    public final static byte VK_NONCONVERT = (byte) 0x1D;
    public final static byte VK_ACCEPT = (byte) 0x1E;
    public final static byte VK_MODECHANGE = (byte) 0x1F;

    public final static byte VK_SPACE = (byte) 0x20;
    public final static byte VK_PRIOR = (byte) 0x21;
    public final static byte VK_NEXT = (byte) 0x22;
    public final static byte VK_END = (byte) 0x23;
    public final static byte VK_HOME = (byte) 0x24;
    public final static byte VK_LEFT = (byte) 0x25;
    public final static byte VK_UP = (byte) 0x26;
    public final static byte VK_RIGHT = (byte) 0x27;
    public final static byte VK_DOWN = (byte) 0x28;
    public final static byte VK_SELECT = (byte) 0x29;
    public final static byte VK_PRINT = (byte) 0x2A;
    public final static byte VK_EXECUTE = (byte) 0x2B;
    public final static byte VK_SNAPSHOT = (byte) 0x2C;
    public final static byte VK_INSERT = (byte) 0x2D;
    public final static byte VK_DELETE = (byte) 0x2E;
    public final static byte VK_HELP = (byte) 0x2F;

    /*
     * VK_0 - VK_9 are the same as ASCII '0' - '9' (0x30 - 0x39)
     */
    public final static byte VK_0 = (byte) 0x30;
    public final static byte VK_1 = (byte) 0x31;
    public final static byte VK_2 = (byte) 0x32;
    public final static byte VK_3 = (byte) 0x33;
    public final static byte VK_4 = (byte) 0x34;
    public final static byte VK_5 = (byte) 0x35;
    public final static byte VK_6 = (byte) 0x36;
    public final static byte VK_7 = (byte) 0x37;
    public final static byte VK_8 = (byte) 0x38;
    public final static byte VK_9 = (byte) 0x39;

    /*
     * 0x3A - 0x40 : unassigned
     */
    public final static byte VK_UNASSIGNED_3A = (byte) 0x3A;
    public final static byte VK_UNASSIGNED_3B = (byte) 0x3B;
    public final static byte VK_UNASSIGNED_3C = (byte) 0x3C;
    public final static byte VK_UNASSIGNED_3D = (byte) 0x3D;
    public final static byte VK_UNASSIGNED_3E = (byte) 0x3E;
    public final static byte VK_UNASSIGNED_3F = (byte) 0x3F;
    public final static byte VK_UNASSIGNED_40 = (byte) 0x40;

    /*
     * VK_A - VK_Z are the same as ASCII 'A' - 'Z' (0x41 - 0x5A)
     */
    public final static byte VK_A = (byte) 0x41;
    public final static byte VK_B = (byte) 0x42;
    public final static byte VK_C = (byte) 0x43;
    public final static byte VK_D = (byte) 0x44;
    public final static byte VK_E = (byte) 0x45;
    public final static byte VK_F = (byte) 0x46;
    public final static byte VK_G = (byte) 0x47;
    public final static byte VK_H = (byte) 0x48;
    public final static byte VK_I = (byte) 0x49;
    public final static byte VK_J = (byte) 0x4A;
    public final static byte VK_K = (byte) 0x4B;
    public final static byte VK_L = (byte) 0x4C;
    public final static byte VK_M = (byte) 0x4D;
    public final static byte VK_N = (byte) 0x4E;
    public final static byte VK_O = (byte) 0x4F;
    public final static byte VK_P = (byte) 0x50;
    public final static byte VK_Q = (byte) 0x51;
    public final static byte VK_R = (byte) 0x52;
    public final static byte VK_S = (byte) 0x53;
    public final static byte VK_T = (byte) 0x54;
    public final static byte VK_U = (byte) 0x55;
    public final static byte VK_V = (byte) 0x56;
    public final static byte VK_W = (byte) 0x57;
    public final static byte VK_X = (byte) 0x58;
    public final static byte VK_Y = (byte) 0x59;
    public final static byte VK_Z = (byte) 0x5A;

    /** Left Windows */
    public final static byte VK_LWIN = (byte) 0x5B;
    /** Right Windows */
    public final static byte VK_RWIN = (byte) 0x5C;
    /** Application */
    public final static byte VK_APPS = (byte) 0x5D;

    /*
     * 0x5E : reserved
     */
    public final static byte VK_RESERVED_5E = (byte) 0x5E;
    public final static byte VK_SLEEP = (byte) 0x5F;

    public final static byte VK_NUMPAD0 = (byte) 0x60;
    public final static byte VK_NUMPAD1 = (byte) 0x61;
    public final static byte VK_NUMPAD2 = (byte) 0x62;
    public final static byte VK_NUMPAD3 = (byte) 0x63;
    public final static byte VK_NUMPAD4 = (byte) 0x64;
    public final static byte VK_NUMPAD5 = (byte) 0x65;
    public final static byte VK_NUMPAD6 = (byte) 0x66;
    public final static byte VK_NUMPAD7 = (byte) 0x67;
    public final static byte VK_NUMPAD8 = (byte) 0x68;
    public final static byte VK_NUMPAD9 = (byte) 0x69;
    public final static byte VK_MULTIPLY = (byte) 0x6A;
    public final static byte VK_ADD = (byte) 0x6B;
    public final static byte VK_SEPARATOR = (byte) 0x6C;
    public final static byte VK_SUBTRACT = (byte) 0x6D;
    public final static byte VK_DECIMAL = (byte) 0x6E;
    public final static byte VK_DIVIDE = (byte) 0x6F;
    public final static byte VK_F1 = (byte) 0x70;
    public final static byte VK_F2 = (byte) 0x71;
    public final static byte VK_F3 = (byte) 0x72;
    public final static byte VK_F4 = (byte) 0x73;
    public final static byte VK_F5 = (byte) 0x74;
    public final static byte VK_F6 = (byte) 0x75;
    public final static byte VK_F7 = (byte) 0x76;
    public final static byte VK_F8 = (byte) 0x77;
    public final static byte VK_F9 = (byte) 0x78;
    public final static byte VK_F10 = (byte) 0x79;
    public final static byte VK_F11 = (byte) 0x7A;
    public final static byte VK_F12 = (byte) 0x7B;
    public final static byte VK_F13 = (byte) 0x7C;
    public final static byte VK_F14 = (byte) 0x7D;
    public final static byte VK_F15 = (byte) 0x7E;
    public final static byte VK_F16 = (byte) 0x7F;
    public final static byte VK_F17 = (byte) 0x80;
    public final static byte VK_F18 = (byte) 0x81;
    public final static byte VK_F19 = (byte) 0x82;
    public final static byte VK_F20 = (byte) 0x83;
    public final static byte VK_F21 = (byte) 0x84;
    public final static byte VK_F22 = (byte) 0x85;
    public final static byte VK_F23 = (byte) 0x86;
    public final static byte VK_F24 = (byte) 0x87;

    /*
     * 0x88 - 0x8F : UI navigation
     */

    public final static byte VK_NAVIGATION_VIEW = (byte) 0x88; // reserved
    public final static byte VK_NAVIGATION_MENU = (byte) 0x89; // reserved
    public final static byte VK_NAVIGATION_UP = (byte) 0x8A; // reserved
    public final static byte VK_NAVIGATION_DOWN = (byte) 0x8B; // reserved
    public final static byte VK_NAVIGATION_LEFT = (byte) 0x8C; // reserved
    public final static byte VK_NAVIGATION_RIGHT = (byte) 0x8D; // reserved
    public final static byte VK_NAVIGATION_ACCEPT = (byte) 0x8E; // reserved
    public final static byte VK_NAVIGATION_CANCEL = (byte) 0x8F; // reserved

    public final static byte VK_NUMLOCK = (byte) 0x90;
    public final static byte VK_SCROLL = (byte) 0x91;

    /*
     * NEC PC-9800 kbd definitions
     */
    public final static byte VK_OEM_NEC_EQUAL = (byte) 0x92; // '=' key on numpad

    /*
     * Fujitsu/OASYS kbd definitions
     */
    public final static byte VK_OEM_FJ_JISHO = (byte) 0x92; // 'Dictionary' key
    public final static byte VK_OEM_FJ_MASSHOU = (byte) 0x93; // 'Unregister word' key
    public final static byte VK_OEM_FJ_TOUROKU = (byte) 0x94; // 'Register word' key
    public final static byte VK_OEM_FJ_LOYA = (byte) 0x95; // 'Left OYAYUBI' key
    public final static byte VK_OEM_FJ_ROYA = (byte) 0x96; // 'Right OYAYUBI' key

    /*
     * 0x97 - 0x9F : unassigned
     */
    public final static byte VK_UNASSIGNED_97 = (byte) 0x97;
    public final static byte VK_UNASSIGNED_98 = (byte) 0x98;
    public final static byte VK_UNASSIGNED_99 = (byte) 0x99;
    public final static byte VK_UNASSIGNED_9A = (byte) 0x9A;
    public final static byte VK_UNASSIGNED_9B = (byte) 0x9B;
    public final static byte VK_UNASSIGNED_9C = (byte) 0x9C;
    public final static byte VK_UNASSIGNED_9D = (byte) 0x9D;
    public final static byte VK_UNASSIGNED_9E = (byte) 0x9E;
    public final static byte VK_UNASSIGNED_9F = (byte) 0x9F;

    /*
     * VK_L* & VK_R* - left and right Alt, Ctrl and Shift virtual keys. Used only as
     * parameters to GetAsyncKeyState() and GetKeyState(). No other API or message
     * will distinguish left and right keys in this way.
     */
    public final static byte VK_LSHIFT = (byte) 0xA0;
    public final static byte VK_RSHIFT = (byte) 0xA1;
    public final static byte VK_LCONTROL = (byte) 0xA2;
    public final static byte VK_RCONTROL = (byte) 0xA3;
    public final static byte VK_LMENU = (byte) 0xA4;
    public final static byte VK_RMENU = (byte) 0xA5;

    public final static byte VK_BROWSER_BACK = (byte) 0xA6;
    public final static byte VK_BROWSER_FORWARD = (byte) 0xA7;
    public final static byte VK_BROWSER_REFRESH = (byte) 0xA8;
    public final static byte VK_BROWSER_STOP = (byte) 0xA9;
    public final static byte VK_BROWSER_SEARCH = (byte) 0xAA;
    public final static byte VK_BROWSER_FAVORITES = (byte) 0xAB;
    public final static byte VK_BROWSER_HOME = (byte) 0xAC;

    public final static byte VK_VOLUME_MUTE = (byte) 0xAD;
    public final static byte VK_VOLUME_DOWN = (byte) 0xAE;
    public final static byte VK_VOLUME_UP = (byte) 0xAF;
    public final static byte VK_MEDIA_NEXT_TRACK = (byte) 0xB0;
    public final static byte VK_MEDIA_PREV_TRACK = (byte) 0xB1;
    public final static byte VK_MEDIA_STOP = (byte) 0xB2;
    public final static byte VK_MEDIA_PLAY_PAUSE = (byte) 0xB3;
    public final static byte VK_LAUNCH_MAIL = (byte) 0xB4;
    public final static byte VK_LAUNCH_MEDIA_SELECT = (byte) 0xB5;
    public final static byte VK_LAUNCH_APP1 = (byte) 0xB6;
    public final static byte VK_LAUNCH_APP2 = (byte) 0xB7;

    /*
     * 0xB8 - 0xB9 : reserved
     */
    public final static byte VK_RESERVED_B8 = (byte) 0xB8;
    public final static byte VK_RESERVED_B9 = (byte) 0xB9;

    public final static byte VK_OEM_1 = (byte) 0xBA; // ';:' for US
    public final static byte VK_OEM_PLUS = (byte) 0xBB; // '+' any country
    public final static byte VK_OEM_COMMA = (byte) 0xBC; // ',' any country
    public final static byte VK_OEM_MINUS = (byte) 0xBD; // '-' any country
    public final static byte VK_OEM_PERIOD = (byte) 0xBE; // '.' any country
    public final static byte VK_OEM_2 = (byte) 0xBF; // '/?' for US
    public final static byte VK_OEM_3 = (byte) 0xC0; // '`~' for US

    /*
     * 0xC1 - 0xC2 : reserved
     */
    public final static byte VK_RESERVED_C1 = (byte) 0xC1;
    public final static byte VK_RESERVED_C2 = (byte) 0xC2;

    /*
     * 0xC3 - 0xDA : Gamepad input
     */

    public final static byte VK_GAMEPAD_A = (byte) 0xC3; // reserved
    public final static byte VK_GAMEPAD_B = (byte) 0xC4; // reserved
    public final static byte VK_GAMEPAD_X = (byte) 0xC5; // reserved
    public final static byte VK_GAMEPAD_Y = (byte) 0xC6; // reserved
    public final static byte VK_GAMEPAD_RIGHT_SHOULDER = (byte) 0xC7; // reserved
    public final static byte VK_GAMEPAD_LEFT_SHOULDER = (byte) 0xC8; // reserved
    public final static byte VK_GAMEPAD_LEFT_TRIGGER = (byte) 0xC9; // reserved
    public final static byte VK_GAMEPAD_RIGHT_TRIGGER = (byte) 0xCA; // reserved
    public final static byte VK_GAMEPAD_DPAD_UP = (byte) 0xCB; // reserved
    public final static byte VK_GAMEPAD_DPAD_DOWN = (byte) 0xCC; // reserved
    public final static byte VK_GAMEPAD_DPAD_LEFT = (byte) 0xCD; // reserved
    public final static byte VK_GAMEPAD_DPAD_RIGHT = (byte) 0xCE; // reserved
    public final static byte VK_GAMEPAD_MENU = (byte) 0xCF; // reserved
    public final static byte VK_GAMEPAD_VIEW = (byte) 0xD0; // reserved
    public final static byte VK_GAMEPAD_LEFT_THUMBSTICK_BUTTON = (byte) 0xD1; // reserved
    public final static byte VK_GAMEPAD_RIGHT_THUMBSTICK_BUTTON = (byte) 0xD2; // reserved
    public final static byte VK_GAMEPAD_LEFT_THUMBSTICK_UP = (byte) 0xD3; // reserved
    public final static byte VK_GAMEPAD_LEFT_THUMBSTICK_DOWN = (byte) 0xD4; // reserved
    public final static byte VK_GAMEPAD_LEFT_THUMBSTICK_RIGHT = (byte) 0xD5; // reserved
    public final static byte VK_GAMEPAD_LEFT_THUMBSTICK_LEFT = (byte) 0xD6; // reserved
    public final static byte VK_GAMEPAD_RIGHT_THUMBSTICK_UP = (byte) 0xD7; // reserved
    public final static byte VK_GAMEPAD_RIGHT_THUMBSTICK_DOWN = (byte) 0xD8; // reserved
    public final static byte VK_GAMEPAD_RIGHT_THUMBSTICK_RIGHT = (byte) 0xD9; // reserved
    public final static byte VK_GAMEPAD_RIGHT_THUMBSTICK_LEFT = (byte) 0xDA; // reserved

    public final static byte VK_OEM_4 = (byte) 0xDB; // '[{' for US
    public final static byte VK_OEM_5 = (byte) 0xDC; // '\|' for US
    public final static byte VK_OEM_6 = (byte) 0xDD; // ']}' for US
    public final static byte VK_OEM_7 = (byte) 0xDE; // ''"' for US
    public final static byte VK_OEM_8 = (byte) 0xDF;

    /*
     * 0xE0 : reserved
     */
    public final static byte VK_RESERVED_E0 = (byte) 0xE0;

    /*
     * Various extended or enhanced keyboards
     */
    public final static byte VK_OEM_AX = (byte) 0xE1; // 'AX' key on Japanese AX kbd
    public final static byte VK_OEM_102 = (byte) 0xE2; // "<>" or "\|" on RT 102-key kbd.
    public final static byte VK_ICO_HELP = (byte) 0xE3; // Help key on ICO
    public final static byte VK_ICO_00 = (byte) 0xE4; // 00 key on ICO, produces "00"

    public final static byte VK_PROCESSKEY = (byte) 0xE5;

    public final static byte VK_ICO_CLEAR = (byte) 0xE6;

    public final static byte VK_PACKET = (byte) 0xE7;

    /*
     * 0xE8 : unassigned
     */
    public final static byte VK_UNASSIGNED_E8 = (byte) 0xE8;

    /*
     * Nokia/Ericsson definitions
     */
    public final static byte VK_OEM_RESET = (byte) 0xE9;
    public final static byte VK_OEM_JUMP = (byte) 0xEA;
    public final static byte VK_OEM_PA1 = (byte) 0xEB;
    public final static byte VK_OEM_PA2 = (byte) 0xEC;
    public final static byte VK_OEM_PA3 = (byte) 0xED;
    public final static byte VK_OEM_WSCTRL = (byte) 0xEE;
    public final static byte VK_OEM_CUSEL = (byte) 0xEF;
    public final static byte VK_OEM_ATTN = (byte) 0xF0;
    public final static byte VK_OEM_FINISH = (byte) 0xF1;
    public final static byte VK_OEM_COPY = (byte) 0xF2;
    public final static byte VK_OEM_AUTO = (byte) 0xF3;
    public final static byte VK_OEM_ENLW = (byte) 0xF4;
    public final static byte VK_OEM_BACKTAB = (byte) 0xF5;

    public final static byte VK_ATTN = (byte) 0xF6;
    public final static byte VK_CRSEL = (byte) 0xF7;
    public final static byte VK_EXSEL = (byte) 0xF8;
    public final static byte VK_EREOF = (byte) 0xF9;
    public final static byte VK_PLAY = (byte) 0xFA;
    public final static byte VK_ZOOM = (byte) 0xFB;
    public final static byte VK_NONAME = (byte) 0xFC;
    public final static byte VK_PA1 = (byte) 0xFD;
    public final static byte VK_OEM_CLEAR = (byte) 0xFE;

    /*
     * 0xFF : reserved
     */
    public final static byte VK_RESERVED_FF = (byte) 0xFF;

    public static String KeyToString(byte vkcode) {
        switch (vkcode) {
            case VK_LBUTTON:
                return "LeftMouse";
            case VK_RBUTTON:
                return "RightMouse";
            case VK_MBUTTON:
                return "MiddleMouse";
            case VK_XBUTTON1:
                return "X1Mouse";
            case VK_XBUTTON2:
                return "X2Mouse";
            case VK_BACK:
                return "BACKSPACE";
            case VK_TAB:
                return "TAB";
            case VK_CLEAR:
                return "CLEAR";
            case VK_RETURN:
                return "ENTER";
            case VK_SHIFT:
                return "SHIFT";
            case VK_CONTROL:
                return "CTRL";
            case VK_MENU:
                return "ALT";
            case VK_PAUSE:
                return "PAUSE";
            case VK_CAPITAL:
                return "CAPS LOCK";
            case VK_ESCAPE:
                return "ESC";
            case VK_SPACE:
                return "SPACEBAR";
            case VK_PRIOR:
                return "PAGE UP";
            case VK_NEXT:
                return "PAGE DOWN";
            case VK_END:
                return "END";
            case VK_HOME:
                return "HOME";
            case VK_LEFT:
                return "LEFT ARROW";
            case VK_UP:
                return "UP ARROW";
            case VK_RIGHT:
                return "RIGHT ARROW";
            case VK_DOWN:
                return "DOWN ARROW";
            case VK_SNAPSHOT:
                return "PRINT SCREEN";
            case VK_INSERT:
                return "INS";
            case VK_DELETE:
                return "DEL";
            case VK_0:
                return "0";
            case VK_1:
                return "1";
            case VK_2:
                return "2";
            case VK_3:
                return "3";
            case VK_4:
                return "4";
            case VK_5:
                return "5";
            case VK_6:
                return "6";
            case VK_7:
                return "7";
            case VK_8:
                return "8";
            case VK_9:
                return "9";
            case VK_A:
                return "A";
            case VK_B:
                return "B";
            case VK_C:
                return "C";
            case VK_D:
                return "D";
            case VK_E:
                return "E";
            case VK_F:
                return "F";
            case VK_G:
                return "G";
            case VK_H:
                return "H";
            case VK_I:
                return "I";
            case VK_J:
                return "J";
            case VK_K:
                return "K";
            case VK_L:
                return "L";
            case VK_M:
                return "M";
            case VK_N:
                return "N";
            case VK_O:
                return "O";
            case VK_P:
                return "P";
            case VK_Q:
                return "Q";
            case VK_R:
                return "R";
            case VK_S:
                return "S";
            case VK_T:
                return "T";
            case VK_U:
                return "U";
            case VK_V:
                return "V";
            case VK_W:
                return "W";
            case VK_X:
                return "X";
            case VK_Y:
                return "Y";
            case VK_Z:
                return "Z";
            case VK_LWIN:
                return "LWIN";
            case VK_RWIN:
                return "RWIN";
            case VK_NUMPAD0:
                return "NUMPAD0";
            case VK_NUMPAD1:
                return "NUMPAD1";
            case VK_NUMPAD2:
                return "NUMPAD2";
            case VK_NUMPAD3:
                return "NUMPAD3";
            case VK_NUMPAD4:
                return "NUMPAD4";
            case VK_NUMPAD5:
                return "NUMPAD5";
            case VK_NUMPAD6:
                return "NUMPAD6";
            case VK_NUMPAD7:
                return "NUMPAD7";
            case VK_NUMPAD8:
                return "NUMPAD8";
            case VK_NUMPAD9:
                return "NUMPAD9";
            case VK_MULTIPLY:
                return "Multiply";
            case VK_ADD:
                return "Add";
            case VK_SEPARATOR:
                return "Separator";
            case VK_SUBTRACT:
                return "Subtract";
            case VK_DECIMAL:
                return "Decimal";
            case VK_DIVIDE:
                return "Divide";
            case VK_F1:
                return "F1";
            case VK_F2:
                return "F2";
            case VK_F3:
                return "F3";
            case VK_F4:
                return "F4";
            case VK_F5:
                return "F5";
            case VK_F6:
                return "F6";
            case VK_F7:
                return "F7";
            case VK_F8:
                return "F8";
            case VK_F9:
                return "F9";
            case VK_F10:
                return "F10";
            case VK_F11:
                return "F11";
            case VK_F12:
                return "F12";
            case VK_F13:
                return "F13";
            case VK_F14:
                return "F14";
            case VK_F15:
                return "F15";
            case VK_F16:
                return "F16";
            case VK_F17:
                return "F17";
            case VK_F18:
                return "F18";
            case VK_F19:
                return "F19";
            case VK_F20:
                return "F20";
            case VK_F21:
                return "F21";
            case VK_F22:
                return "F22";
            case VK_F23:
                return "F23";
            case VK_F24:
                return "F24";
            case VK_LSHIFT:
                return "LSHIFT";
            case VK_RSHIFT:
                return "RSHIFT";
            case VK_LCONTROL:
                return "LCONTROL";
            case VK_RCONTROL:
                return "RCONTROL";
            case VK_LMENU:
                return "LAlt";
            case VK_RMENU:
                return "RAlt";
            case VK_VOLUME_MUTE:
                return "Volume Mute";
            case VK_VOLUME_DOWN:
                return "Volume Down";
            case VK_VOLUME_UP:
                return "Volume Up";
            case VK_MEDIA_NEXT_TRACK:
                return "Next Track";
            case VK_MEDIA_PREV_TRACK:
                return "Previous Track";
            case VK_MEDIA_STOP:
                return "Stop Media";
            case VK_MEDIA_PLAY_PAUSE:
                return "Play/Pause Media";
            case VK_OEM_1:
                return ";";
            case VK_OEM_PLUS:
                return "+";
            case VK_OEM_COMMA:
                return ",";
            case VK_OEM_MINUS:
                return "-";
            case VK_OEM_PERIOD:
                return ".";
            case VK_OEM_2:
                return "/";
            case VK_OEM_3:
                return "~";
            case VK_OEM_4:
                return "[";
            case VK_OEM_5:
                return "\\";
            case VK_OEM_6:
                return "]";
            case VK_OEM_7:
                return "'";
            default:
                return "";
        }
    }
}
