package pers.louisj.Zwm.Core.WinApi;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;

public interface MyUser32 extends User32 {

    LRESULT CallNextHookEx(HHOOK hhk, int nCode, WPARAM wParam, Pointer lParam);

    boolean IsIconic(HWND hWnd);

    boolean IsZoomed(HWND hWnd);

    HWND GetWindow(HWND hWnd, int uCmd);

    boolean BringWindowToTop(HWND hWnd);

    boolean SendNotifyMessage(HWND hwnd, int msg, WPARAM wParam, LPARAM lParam);

    Pointer BeginDeferWindowPos(int nNumWindows);

    BOOL EndDeferWindowPos(Pointer hWinPosInfo);

    Pointer DeferWindowPos(Pointer hWinPosInfo, HWND hWnd, HWND hWndInsertAfter, int x, int y, int cx, int cy,
            int uFlags);
}