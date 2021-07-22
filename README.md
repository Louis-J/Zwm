<div align=center>
<img src="icon/ZWMICO.png" width="30%" height="30%"/>
</div>

# <div align=center>Zwm</div>
### An **Auto-Tiling** Window Manager for Windows OS




# Feature
+ [x] Virtual Desktop in Windows OS(use it not only in win 10)
  + [x] Multiple Monitor support

+ [x] Auto-Tiling!
  + [x] A Basic Grid Layout
  + [x] Float mode
  + [ ] More Layouts...

+ [x] Plug-in support
  + [x] State Save&Load Plug-in
  + [ ] Smart Gap
  + [x] i3-like Bar
  + [ ] More Plug-in...

+ [x] Very easy to use and customize
  + [x] Change your setting in config.java
  + [x] Or change the world in config.java(layouts, keybinds, ...)

# TODO
+ solve bugs in current grid layout
+ solve bugs in current systemtray plug-in

# Thanks to: 

## qtjambi

the qt binding for java

[qtjambi](https://github.com/OmixVisualization/qtjambi)

[License.GPLv3](https://github.com/OmixVisualization/qtjambi/blob/master/LICENSE.GPLv3)

[License.LGPLv21](https://github.com/OmixVisualization/qtjambi/blob/master/LICENSE.LGPLv21)

my patched version with nativeFilter feature:
[qtjambi-with-nativeFilter](https://github.com/Louis-J/qtjambi-with-nativeFilter)

## aho-corasick

a java implementation of the Aho-Corasick algorithm

used in my filters.

[aho-corasick](https://github.com/robert-bor/aho-corasick)

[License](https://github.com/robert-bor/aho-corasick/blob/master/LICENSE.md)

## workspacer

a tiling window manager for Windows 10

i read the source code and it inspired me a lot

[workspacer](https://github.com/rickbutton/workspacer)

[License](https://github.com/rickbutton/workspacer/blob/master/LICENSE)

## storm

i used the source code of the [CompilingClassLoader.java](https://github.com/apache/storm/blob/master/sql/storm-sql-core/src/jvm/org/apache/storm/sql/javac/CompilingClassLoader.java)

[storm](https://github.com/apache/storm)

[License](https://github.com/apache/storm/blob/master/LICENSE)

## Grid-Tiling-Kwin

a kwin script that automatically tiles windows

i use it in my manjaro KDE. i like the grid layout logic.

[Grid-Tiling-Kwin](https://github.com/lingtjien/Grid-Tiling-Kwin)

# Why do you use java?
I'm a "c++ boy" and it's slow to compile and a little boring to struggle with bugs in c++. I also want to try and study a new language.

# Default Config

## Default Virtual Desk Config

4 Virtual Desk, the layouts are grid layout

## Default ShortCuts

"Exit The Program": left Alt, Q

"Debug VD info": left Alt, X

"Turn Focused Window Left": left Alt, left Arrow, 

"Turn Focused Window Right": left Alt, right Arrow, 

"Turn Focused Window Up": left Alt, up Arrow, 

"Turn Focused Window Down": left Alt, down Arrow, 

"Close Focused Window": left Alt, Esc, 

"Minimize Focused Window": left Alt, A, 

"Switch Focused Monitor to Virtual Desk x(1-9)": left Alt, 1-9, 

"Move Focused Window to Virtual Desk x(1-9)": left Alt | left Ctrl, 1-9

"Switch Focused Monitor to Previous Virtual Desk": left Alt | left Ctrl, left Arrow, 

"Switch Focused Monitor to Next Virtual Desk": left Alt | left Ctrl, right Arrow, 

"Move Focused Window to Previous Virtual Desk": left Alt | left Ctrl | left Win, left Arrow, 

"Move Focused Window to Next Virtual Desk": left Alt | left Ctrl | left Win, right Arrow, 

"Reset Layout of Focused Virtual Desk": left Alt, R, 

"Expand the Area of Focused Window": left Alt, +, 

"Shrink the Area of Focused Window": left Alt, -, 
