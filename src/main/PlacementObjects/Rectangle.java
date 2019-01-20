package main.PlacementObjects;

public class Rectangle{
    int width;
    int depth;

    public Rectangle(int width, int depth){
        this.width = width;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getArea(){
        return width*depth;
    }

    public boolean accomodate(Rectangle object) {
        if(this.width >= object.getWidth() && this.depth >= object.getDepth())
            return true;
        if(this.width >= object.getDepth() && this.depth >= object.getWidth())
            return true;
        return false;
    }
}
