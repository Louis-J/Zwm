package pers.louisj.Zwm.Core.Message.VirtualDeskMessage;

import pers.louisj.Zwm.Core.Message.Message;
import pers.louisj.Zwm.Core.Window.Window;

public enum VirtualDeskEvent {
    SwitchToNextVirtualDesk, SwitchToPreviousVirtualDesk,
    SwitchToVirtualDesk,

    VirtualDeskUpdated,

    CloseFocusedWindow, ResetLayout, TurnWindowLeft, TurnWindowRight, TurnWindowUp, TurnWindowDown, SwitchWindowToVirtualDesk, SwitchToPrevVirtualDesk, SwitchWindowToPrevVirtualDesk, SwitchWindowToNextVirtualDesk, ToggleTiling, FocusedWindowClose, FocusedWindowMinimize, FocusedWindowMaximize,
}
