package PackingObjects;

import PlacementObjects.Vector3D;

public class Cuboid {
    protected int width, depth, height;
    public Cuboid(int width, int depth, int height){
        this.width = width;
        this.depth = depth;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public int getHeight() {
        return height;
    }

    public int getVolume() {
        return width*depth*height;
    }

    public Vector3D rotate(int degree_along_x, int degree_along_y, int degree_along_z) {
        int w = width;
        int d = depth;
        int h = height;
        if(degree_along_x == 90){
            int temp = h;
            h = d;
            d = temp;
        }
        if(degree_along_y == 90){
            int temp = w;
            w = h;
            h = temp;
        }
        if(degree_along_z == 90){
            int temp = w;
            w = d;
            d = temp;
        }
        return new Vector3D(w, d, h);

    }
}
