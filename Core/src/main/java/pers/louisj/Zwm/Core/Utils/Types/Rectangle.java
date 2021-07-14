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

    public boolean equals(Rectangle r) {
        return x == r.x && y == r.y && width == r.width && height == r.height;
    }

    @Override
    public String toString() {
        return "Size: " + x + ", " + y + ", " + width + ", " + height;
    }
}
