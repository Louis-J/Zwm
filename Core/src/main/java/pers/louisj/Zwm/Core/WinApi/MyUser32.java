package pers.louisj.Zwm.Core.WinApi;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
// import com.sun.jna.platform.win32.WinDef.LRESULT;

public interface MyUser32 extends User32 {
    // public static MyUser32 INSTANCE = Native.load("user32", MyUser32.class,
    // com.sun.jna.win32.W32APIOptions.DEFAULT_OPTIONS);

    short GetKeyState(int key);

    // short GetAsyncKeyState(int vKey);

    LRESULT CallNextHookEx(HHOOK hhk, int nCode, WPARAM wParam, Pointer lParam);

    boolean IsIconic(HWND hWnd);

    boolean IsZoomed(HWND hWnd);

    HWND GetWindow(HWND hWnd, int uCmd);

    boolean BringWindowToTop(HWND hWnd);

    // void SendNotifyMessage(HWND handle, int wM_SYSCOMMAND, int sC_CLOSE, int i);
    boolean SendNotifyMessage(HWND hwnd, int msg, WPARAM wParam, LPARAM lParam);

    Pointer BeginDeferWindowPos(int nNumWindows);

    BOOL EndDeferWindowPos(Pointer hWinPosInfo);

    Pointer DeferWindowPos(Pointer hWinPosInfo, HWND hWnd, HWND hWndInsertAfter, int x, int y, int cx, int cy,
            int uFlags);
}