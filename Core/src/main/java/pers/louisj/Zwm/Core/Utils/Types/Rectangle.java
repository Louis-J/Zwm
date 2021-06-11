package pers.louisj.Zwm.Core.Utils.Types;

public class Rectangle {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // public boolean IsPointInside(int x, int y) {
    //     return this.x <= x && x <= (this.x + this.width) && this.y <= y && y <= (this.y + this.height);
    // }

    // public boolean IsPointInside(Point p) {
    //     return x <= p.x && p.x <= (x + width) && y <= p.y && p.y <= (y + height);
    // }

    public Point Center() {
        return new Point(x + width / 2, y + height / 2);
    }
}
