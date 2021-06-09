package pers.louisj.Zwm.Core.Global.Message.VDManMessage;

public enum VDManEvent {
    // SwitchToNextVirtualDesk, SwitchToVirtualDesk,

    // VirtualDeskUpdated,

    // ResetLayout, TurnWindowLeft, TurnWindowRight, TurnWindowUp, TurnWindowDown, MoveWindowToVirtualDesk,
    // SwitchToPrevVirtualDesk, MoveWindowToPrevVirtualDesk, MoveWindowToNextVirtualDesk, ToggleTiling, FocusedWindowClose,
    // FocusedWindowMinimize, FocusedWindowMaximize, 
    VDDebugInfo,

    RefreshMonitors,
    VDAdd, VDRemove,
    SwitchToVD,
    SwitchMonitorToVD,
    Foreground,
    WindowAdd, WindowRemove,
    MoveWindowToVD,
}
