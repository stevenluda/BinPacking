package PlacementObjects;

public class Point {
    int x, y, z;

    public Point(){
        this(0,0,0);
    }

    public Point(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Point base)
    {
        this.x = base.getX();
        this.y = base.getY();
        this.z = base.getZ();
    }

    public Point(Point base, int x_diff, int y_diff, int z_diff)
    {
        this(base);
        this.x += x_diff;
        this.y += y_diff;
        this.z += z_diff;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
