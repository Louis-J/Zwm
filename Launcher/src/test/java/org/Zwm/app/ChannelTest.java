// package org.Zwm.app;

// import org.junit.Test;

// import pers.louisj.Zwm.Core.Global.Message.Message;
// import pers.louisj.Zwm.Core.Utils.Async.Channel;
// import pers.louisj.Zwm.Core.Utils.Async.Channel2;

// import static org.junit.Assert.*;

// import java.sql.Time;
// import java.util.Date;

// abstract class Msg {
//     public static Msg GetMsg(int i) {
//         switch (i % 4) {
//             case 0:
//                 return new Msg1(i);
//             case 1:
//                 return new Msg2(i);
//             case 2:
//                 return new Msg3(i);
//             case 3:
//                 return new Msg4(i);
//         }
//         return null;
//     }

//     abstract public int GetVal();
// };

// class Msg1 extends Msg {
//     int id;

//     public Msg1(int i) {
//         id = i;
//     }

//     public int GetVal() {
//         return id;
//     }
// }

// class Msg2 extends Msg {
//     float id;

//     public Msg2(int i) {
//         id = (float) i;
//     }

//     public int GetVal() {
//         return (int) id;
//     }
// }

// class Msg3 extends Msg {
//     String id;

//     public Msg3(int i) {
//         id = String.valueOf(i);
//     }

//     public int GetVal() {
//         return Integer.parseInt(id);
//     }
// }

// class Msg4 extends Msg {
//     Msg3 msg;

//     public Msg4(int i) {
//         msg = new Msg3(i);
//     }

//     public int GetVal() {
//         return msg.GetVal();
//     }
// }

// public class ChannelTest {
//     // @Test
//     public void Test1() {
//         Channel<Msg> channelA = new Channel2<>(1024);
//         var tA0 = new Thread() {
//             @Override
//             public void run() {
//                 var msg = channelA.take();
//                 int inc = 0;
//                 if (msg == null) {
//                     return;
//                 } else if (msg instanceof Msg1) {
//                     var msg1 = (Msg1) msg;
//                     if (inc++ != msg1.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else if (msg instanceof Msg2) {
//                     var msg2 = (Msg2) msg;
//                     if (inc++ != msg2.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else if (msg instanceof Msg3) {
//                     var msg3 = (Msg3) msg;
//                     if (inc++ != msg3.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else if (msg instanceof Msg4) {
//                     var msg4 = (Msg4) msg;
//                     if (inc++ != msg4.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else {
//                     System.out.print("Bad Type");
//                 }
//             }
//         };
//         var tA1 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 0; i < 10000000; i++)
//                     channelA.put(Msg.GetMsg(i));
//                 channelA.put(null);
//             }
//         };

//         Channel<Msg> channelB = new Channel<>(1024);
//         var tB0 = new Thread() {
//             @Override
//             public void run() {
//                 var msg = channelB.take();
//                 int inc = 0;
//                 if (msg == null) {
//                     return;
//                 } else if (msg instanceof Msg1) {
//                     var msg1 = (Msg1) msg;
//                     if (inc++ != msg1.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else if (msg instanceof Msg2) {
//                     var msg2 = (Msg2) msg;
//                     if (inc++ != msg2.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else if (msg instanceof Msg3) {
//                     var msg3 = (Msg3) msg;
//                     if (inc++ != msg3.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else if (msg instanceof Msg4) {
//                     var msg4 = (Msg4) msg;
//                     if (inc++ != msg4.GetVal()) {
//                         System.out.print("Bad: ");
//                         System.out.println(inc);
//                     }
//                 } else {
//                     System.out.print("Bad Type");
//                 }
//             }
//         };
//         var tB1 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 0; i < 10000000; i++)
//                     channelB.put(Msg.GetMsg(i));
//                 channelB.put(null);
//             }
//         };

//         var timeAbegin = new Date().getTime();
//         tA0.start();
//         tA1.start();
//         try {
//             tA0.join();
//             tA1.join();
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//         var timeA = new Date().getTime() - timeAbegin;

//         var timeBbegin = new Date().getTime();
//         tB0.start();
//         tB1.start();
//         try {
//             tB0.join();
//             tB1.join();
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//         var timeB = new Date().getTime() - timeBbegin;

//         System.out.println("timeA: " + timeA);
//         System.out.println("timeB: " + timeB);
//         System.out.println("WWaitTimesA: " + channelA.writeWaitTimes);
//         System.out.println("WWaitTimesB: " + channelB.writeWaitTimes);
//         System.out.println("channelA size: " + channelA.GetCapacity());
//         System.out.println("channelB size: " + channelB.GetCapacity());
//     }

//     // @Test
//     public void Test2() {
//         final int LOOP_TIMES = 10000000;
//         Channel<Msg> channelA = new Channel2<>(1024);
//         var tA0 = new Thread() {
//             @Override
//             public void run() {
//                 var msg = channelA.take();
//                 int result = 0;
//                 if (msg == null) {
//                     return;
//                 } else if (msg instanceof Msg1) {
//                     var msg1 = (Msg1) msg;
//                     var val = msg1.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else if (msg instanceof Msg2) {
//                     var msg2 = (Msg2) msg;
//                     var val = msg2.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else if (msg instanceof Msg3) {
//                     var msg3 = (Msg3) msg;
//                     var val = msg3.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else if (msg instanceof Msg4) {
//                     var msg4 = (Msg4) msg;
//                     var val = msg4.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else {
//                     System.out.print("Bad Type");
//                 }
//             }
//         };
//         var tA1 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 0; i < LOOP_TIMES; i++)
//                     channelA.put(Msg.GetMsg(i));
//                 channelA.put(null);
//             }
//         };
//         var tA2 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 1; i <= LOOP_TIMES; i++)
//                     channelA.put(Msg.GetMsg(i));
//                 channelA.put(null);
//             }
//         };
//         var tA3 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 0; i < LOOP_TIMES; i++)
//                     channelA.put(Msg.GetMsg(i));
//                 channelA.put(null);
//             }
//         };
//         var tA4 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 1; i <= LOOP_TIMES; i++)
//                     channelA.put(Msg.GetMsg(i));
//                 channelA.put(null);
//             }
//         };

//         Channel<Msg> channelB = new Channel<>(1024);
//         var tB0 = new Thread() {
//             @Override
//             public void run() {
//                 var msg = channelB.take();
//                 int result = 0;
//                 if (msg == null) {
//                     return;
//                 } else if (msg instanceof Msg1) {
//                     var msg1 = (Msg1) msg;
//                     var val = msg1.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else if (msg instanceof Msg2) {
//                     var msg2 = (Msg2) msg;
//                     var val = msg2.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else if (msg instanceof Msg3) {
//                     var msg3 = (Msg3) msg;
//                     var val = msg3.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else if (msg instanceof Msg4) {
//                     var msg4 = (Msg4) msg;
//                     var val = msg4.GetVal();
//                     if (val % 2 == 1) {
//                         result += val;
//                     } else {
//                         result -= val;
//                     }
//                 } else {
//                     System.out.print("Bad Type");
//                 }
//             }
//         };
//         var tB1 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 0; i < LOOP_TIMES; i++)
//                     channelB.put(Msg.GetMsg(i));
//                 channelB.put(null);
//             }
//         };
//         var tB2 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 1; i <= LOOP_TIMES; i++)
//                     channelB.put(Msg.GetMsg(i));
//                 channelB.put(null);
//             }
//         };
//         var tB3 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 0; i < LOOP_TIMES; i++)
//                     channelB.put(Msg.GetMsg(i));
//                 channelB.put(null);
//             }
//         };
//         var tB4 = new Thread() {
//             @Override
//             public void run() {
//                 for (int i = 1; i <= LOOP_TIMES; i++)
//                     channelB.put(Msg.GetMsg(i));
//                 channelB.put(null);
//             }
//         };

//         var timeAbegin = new Date().getTime();
//         tA0.start();
//         tA1.start();
//         tA2.start();
//         tA3.start();
//         tA4.start();
//         try {
//             tA0.join();
//             tA1.join();
//             tA2.join();
//             tA3.join();
//             tA4.join();
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//         var timeA = new Date().getTime() - timeAbegin;

//         var timeBbegin = new Date().getTime();
//         tB0.start();
//         tB1.start();
//         tB2.start();
//         tB3.start();
//         tB4.start();
//         try {
//             tB0.join();
//             tB1.join();
//             tB2.join();
//             tB3.join();
//             tB4.join();
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//         var timeB = new Date().getTime() - timeBbegin;

//         System.out.println("timeA: " + timeA);
//         System.out.println("timeB: " + timeB);
//         System.out.println("WWaitTimesA: " + channelA.writeWaitTimes);
//         System.out.println("WWaitTimesB: " + channelB.writeWaitTimes);
//         System.out.println("channelA size: " + channelA.GetCapacity());
//         System.out.println("channelB size: " + channelB.GetCapacity());
//     }
// }
