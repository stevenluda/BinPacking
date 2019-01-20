package main.PackingObjects;

import main.PlacementObjects.Rectangle;

public class Cuboid{
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
    public int getBottomArea(){ return width*depth; }

    public Rectangle getBottomRectangle(){
        return new Rectangle(width, depth);
    }

    public Cuboid rotate(int degree_along_x, int degree_along_y, int degree_along_z) {
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
        return new Cuboid(w, d, h);

    }


    public boolean dimensionFits(Cuboid cuboid){
        if(width >= cuboid.getWidth() && depth >= cuboid.getDepth() && height >= cuboid.getHeight())
            return true;
        return false;
    }

    public boolean accomodate(Cuboid object) {
        if(getVolume() < object.getVolume())
            return false;
        if(!(dimensionFits(object.rotate(0,0,0))||dimensionFits(object.rotate(0,0,90))||
                dimensionFits(object.rotate(0,90,0))||dimensionFits(object.rotate(0,90,90))||
                dimensionFits(object.rotate(90,0,0))||dimensionFits(object.rotate(90,0,90))))
            return false;
        return true;
    }
}
