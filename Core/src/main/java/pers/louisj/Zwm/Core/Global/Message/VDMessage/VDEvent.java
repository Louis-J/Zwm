package pers.louisj.Zwm.Core.Global.Message.VDMessage;

// not mention to a specific window, but mention to the focused window or focused virtual desk
public enum VDEvent {
    TurnWindowLeft, TurnWindowRight, TurnWindowUp, TurnWindowDown,
    ResetLayout,
    AreaShrink, AreaExpand,
    ToggleLayout, 
}
