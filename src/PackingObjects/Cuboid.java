package PackingObjects;

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
}
