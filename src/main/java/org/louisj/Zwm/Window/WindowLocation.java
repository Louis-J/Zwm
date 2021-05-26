package org.louisj.Zwm.Window;


public class WindowLocation {
    public WindowLocation(int x, int y, int width, int height, byte state) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.state = state;
    }
    public int x;
    public int y;
    public int width;
    public int height;
    public byte state; // 0: normal, 1: minimized, 2: maximized

    public boolean IsPointInside(int x, int y) {
        return this.x <= x && x <= (this.x + this.width) && this.y <= y && y <= (this.y + this.height);
    }
}