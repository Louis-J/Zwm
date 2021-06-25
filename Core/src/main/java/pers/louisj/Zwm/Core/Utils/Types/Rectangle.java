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

    public Point Center() {
        return new Point(x + width / 2, y + height / 2);
    }

    @Override
    public String toString() {
        return "Size: " + x + ", " + y + ", " + width + ", " + height;
    }
}
