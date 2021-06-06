package pers.louisj.Zwm.Core.Utils.WinApi;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;

import com.sun.jna.Native;

import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.ptr.IntByReference;

public class WinHelper {
    public final static boolean Is64Bit = true;
    public final static MyUser32 MyUser32Inst = Native.load("user32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);
    public final static Kernel32 Kernel32Inst = Native.load("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);
    public final static DWMApi DWMApiInst = Native.load("dwmapi", DWMApi.class, W32APIOptions.DEFAULT_OPTIONS);

    public interface CallBackWithBuffer1 {
        public int Invoke(char[] buffer, int size);
    };

    public interface CallBackWithBuffer2 {
        public boolean Invoke(char[] buffer, IntByReference pSize);
    };

    public static final String QueryWithBuffer1(CallBackWithBuffer1 callback, int initsize) {
        int size = initsize;
        do {
            char[] buffer = new char[size];
            int retSize = callback.Invoke(buffer, size);
            if (retSize < size) {
                return new String(buffer, 0, retSize);
            }
            size *= 2;
        } while (Kernel32Inst.GetLastError() == Kernel32.ERROR_INSUFFICIENT_BUFFER);
        throw new Win32Exception(Kernel32Inst.GetLastError());
    }

    public static final String QueryWithBuffer2(CallBackWithBuffer2 callback, int initsize) {
        int size = initsize;
        IntByReference pSize = new IntByReference();
        do {
            char[] buffer = new char[size];
            pSize.setValue(size);
            if (callback.Invoke(buffer, pSize)) {
                return new String(buffer, 0, pSize.getValue());
            }
            size *= 2;
        } while (Kernel32Inst.GetLastError() == Kernel32.ERROR_INSUFFICIENT_BUFFER);
        throw new Win32Exception(Kernel32Inst.GetLastError());
    }
}
