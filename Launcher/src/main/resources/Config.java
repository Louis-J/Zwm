import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import pers.louisj.Zwm.Bar.Bar;
import pers.louisj.Zwm.Core.Context;
import pers.louisj.Zwm.Core.Derived.IConfig;
import pers.louisj.Zwm.Core.Global.Message.Message;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManEvent;
import pers.louisj.Zwm.Core.Global.Message.VDManMessage.VDManMessage;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDEvent;
import pers.louisj.Zwm.Core.Global.Message.VDMessage.VDMessage;
import pers.louisj.Zwm.Core.L0.KeyBind.KeyCode;
import pers.louisj.Zwm.Core.L2.VirtualDesk.Layouts.GridLayout;
import pers.louisj.Zwm.Core.L2.Window.WindowStaticAction;
import pers.louisj.Zwm.Core.Utils.Async.Channel;
import pers.louisj.Zwm.Core.Utils.WinApi.WinHelper;

public class Config implements IConfig {
        @Override
        public Context GetContext() {
                Context context = new Context();

                ConfigKey(context);
                // context.keyBindMan.DefaultConfig();
                context.filterIgnore.DefaultConfig();
                context.vdMan.filterLayout.DefaultConfig();

                context.vdMan.ActionGlobal.VDCreate("1", null, new GridLayout());
                context.vdMan.ActionGlobal.VDCreate("2", null, new GridLayout());
                context.vdMan.ActionGlobal.VDCreate("3", null, new GridLayout());
                context.vdMan.ActionGlobal.VDCreate("4", null, new GridLayout());

                context.pluginMan.Add(new Bar());

                return context;
        };

        private void ConfigKey(Context context) {
                Channel<Message> channelIn = context.mainloop.channelIn;

                context.keyBindMan.Register("Turn Focused Window Left", KeyCode.FuncKey.LALT, KeyCode.VK_LEFT,
                                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowLeft, null)));

                context.keyBindMan.Register("Turn Focused Window Right", KeyCode.FuncKey.LALT, KeyCode.VK_RIGHT,
                                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowRight, null)));

                context.keyBindMan.Register("Turn Focused Window Up", KeyCode.FuncKey.LALT, KeyCode.VK_UP,
                                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowUp, null)));

                context.keyBindMan.Register("Turn Focused Window Down", KeyCode.FuncKey.LALT, KeyCode.VK_DOWN,
                                () -> channelIn.put(new VDMessage(VDEvent.TurnWindowDown, null)));

                context.keyBindMan.Register("Close Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_ESCAPE,
                                () -> WindowStaticAction.SendClose(WindowStaticAction.GetForegroundWindow()));

                context.keyBindMan.Register("Minimize Focused Window", KeyCode.FuncKey.LALT, KeyCode.VK_A,
                                () -> WindowStaticAction.ShowMinimized(WindowStaticAction.GetForegroundWindow()));

                context.keyBindMan.Register("Maximize Focused Window and Minimize Others", KeyCode.FuncKey.LALT,
                                KeyCode.VK_Z,
                                () -> WindowStaticAction.ShowMaximized(WindowStaticAction.GetForegroundWindow()));

                for (byte i = 0; i < 9; i++) {
                        var stri = String.valueOf(i + 1);
                        var obji = Integer.valueOf(i);
                        context.keyBindMan.Register("Switch Focused Monitor to Virtual Desk " + stri,
                                        KeyCode.FuncKey.LALT, KeyCode.VK_1 + i,
                                        () -> channelIn.put(new VDManMessage(VDManEvent.SwitchToVD, obji)));
                        context.keyBindMan.Register("Move Focused Window to Virtual Desk " + stri,
                                        KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL, KeyCode.VK_1 + i,
                                        () -> channelIn.put(new VDManMessage(VDManEvent.MoveWindowToVD, obji)));
                }

                context.keyBindMan.Register("Switch Focused Monitor to Previous Virtual Desk",
                                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL, KeyCode.VK_LEFT,
                                () -> channelIn.put(new VDManMessage(VDManEvent.SwitchToVD, Integer.valueOf(-1))));

                context.keyBindMan.Register("Switch Focused Monitor to Next Virtual Desk",
                                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL, KeyCode.VK_RIGHT,
                                () -> channelIn.put(new VDManMessage(VDManEvent.SwitchToVD, Integer.valueOf(-2))));

                context.keyBindMan.Register("Move Focused Window to Previous Virtual Desk",
                                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL | KeyCode.FuncKey.LWIN, KeyCode.VK_LEFT,
                                () -> channelIn.put(new VDManMessage(VDManEvent.MoveWindowToVD, Integer.valueOf(-1))));

                context.keyBindMan.Register("Move Focused Window to Next Virtual Desk",
                                KeyCode.FuncKey.LALT | KeyCode.FuncKey.LCONTROL | KeyCode.FuncKey.LWIN,
                                KeyCode.VK_RIGHT,
                                () -> channelIn.put(new VDManMessage(VDManEvent.MoveWindowToVD, Integer.valueOf(-2))));

                context.keyBindMan.Register("Reset Layout of Focused Virtual Desk", KeyCode.FuncKey.LALT, KeyCode.VK_R,
                                () -> channelIn.put(new VDMessage(VDEvent.ResetLayout, null)));

                context.keyBindMan.Register("Expand the Area of Focused Window", KeyCode.FuncKey.LALT,
                                KeyCode.VK_OEM_PLUS, () -> channelIn.put(new VDMessage(VDEvent.AreaExpand, null)));

                context.keyBindMan.Register("Shrink the Area of Focused Window", KeyCode.FuncKey.LALT,
                                KeyCode.VK_OEM_MINUS, () -> channelIn.put(new VDMessage(VDEvent.AreaShrink, null)));

                context.keyBindMan.Register("Toggle Tiling State for Focused Window", KeyCode.FuncKey.LALT,
                                KeyCode.VK_T, () -> channelIn.put(new VDMessage(VDEvent.ToggleTiling, null)));

                context.keyBindMan.Register("Exit The Program", KeyCode.FuncKey.LALT, KeyCode.VK_Q,
                                () -> context.Exit());

                context.keyBindMan.Register("Debug VD info", KeyCode.FuncKey.LALT, KeyCode.VK_X,
                                () -> channelIn.put(new VDManMessage(VDManEvent.VDDebugInfo, null)));

                final int WM_DISPLAYCHANGE = 0x007e;
                context.keyBindMan.Register("Send Message To Me", KeyCode.FuncKey.LALT, KeyCode.VK_S,
                                () -> WinHelper.MyUser32Inst.PostThreadMessage((int) context.msgloop.GetThreadId(),
                                                WM_DISPLAYCHANGE, new WPARAM(0), new LPARAM(0)));

        }
}