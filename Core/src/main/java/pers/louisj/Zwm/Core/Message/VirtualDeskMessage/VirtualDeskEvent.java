package pers.louisj.Zwm.Core.Message.VirtualDeskMessage;

import pers.louisj.Zwm.Core.Message.Message;
import pers.louisj.Zwm.Core.Window.Window;

public enum VirtualDeskEvent {
    SwitchToNextVirtualDesk, SwitchToVirtualDesk,

    VirtualDeskUpdated,

    ResetLayout, TurnWindowLeft, TurnWindowRight, TurnWindowUp, TurnWindowDown, MoveWindowToVirtualDesk,
    SwitchToPrevVirtualDesk, MoveWindowToPrevVirtualDesk, MoveWindowToNextVirtualDesk, ToggleTiling, FocusedWindowClose,
    FocusedWindowMinimize, FocusedWindowMaximize, VDDebugInfo;
}
