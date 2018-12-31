package PlacementObjects;

public class Vector3D {
    int x, y, z;

    public Vector3D(){
        this(0,0,0);
    }

    public Vector3D(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D base)
    {
        this.x = base.getX();
        this.y = base.getY();
        this.z = base.getZ();
    }

    public Vector3D(Vector3D base, int x_diff, int y_diff, int z_diff)
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String toString(){
        return x+","+y+","+z;
    }
}
