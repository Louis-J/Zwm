package pers.louisj.Zwm.Core.Global.Message.VDMessage;

public enum VDEvent {
    // SwitchToNextVirtualDesk, SwitchToVirtualDesk,

    VirtualDeskUpdated,

    // MoveWindowToVirtualDesk,
    // SwitchToPrevVirtualDesk, MoveWindowToPrevVirtualDesk, MoveWindowToNextVirtualDesk, FocusedWindowClose,
    // FocusedWindowMinimize, FocusedWindowMaximize, VDDebugInfo,

    // WindowUpdateState,
    WindowUpdateLocation,
    TurnWindowLeft, TurnWindowRight, TurnWindowUp, TurnWindowDown,
    ResetLayout,
    AreaShrink, AreaExpand,
    ToggleLayout, 
    WindowMinimizeStart, WindowMinimizeEnd,
}
