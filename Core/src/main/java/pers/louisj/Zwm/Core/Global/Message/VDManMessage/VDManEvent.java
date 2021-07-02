package pers.louisj.Zwm.Core.Global.Message.VDManMessage;

public enum VDManEvent {
    RefreshMonitors,
    SwitchToVD,
    WindowAddInit,
    WindowAdd,
    WindowRemove,
    WindowForeground,
    WindowTitleChange,
    MonitorForeground,
    FocusedWindowMoveTo,
    //Not Implemented
    VDAdd, VDRemove,
    SwitchMonitorToVD,

    WindowMoveResize,
    WindowMinimizeStart,
    WindowMinimizeEnd,
}
