package pers.louisj.Zwm.Core.L0.MsgLoop;

public interface IMsgLoop {
    public int GetThreadId();

    public void run();

    public void exit();
}