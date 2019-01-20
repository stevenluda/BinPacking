package main.PackingObjects;


import main.PlacementObjects.Vector3D;

import java.util.ArrayList;

public class Box extends PositionedCuboid {
    String id;
    int weight;
    ArrayList<Box> aboveBoxes = new ArrayList<Box>();
    ArrayList<Box> underneathBoxes = new ArrayList<Box>();

    public Box(String id, int width, int depth, int height, int weight, Vector3D position) {
        super(width, depth, height, position);
        this.weight = weight;
        this.id = id;
    }

    public Box(Box box){
        this(box.getId(), box.getWidth(), box.getDepth(), box.getHeight(), box.getWeight(), box.getPosition());
    }

    public Box(Box box, Cuboid new_dims){
        this(box);
        this.setDims(new_dims);
    }

    public String getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public void setDims(Cuboid vec){
        width = vec.getWidth();
        depth = vec.getDepth();
        height = vec.getHeight();
    }

    public Cuboid getDims(){
        return new Cuboid(width, depth, height);
    }

    public void placeTheBox(Vector3D pos, Cuboid orientation){
        this.setPosition(pos);
        this.setDims(orientation);
    }

    public String toString(){
        if(position != null)
            return this.id +","+this.width+","+this.depth+","+this.height+","+position.getX()+","+position.getY()+","+position.getZ();
        else
            return this.id +","+this.width+","+this.depth+","+this.height;
    }
    /*public boolean isOverlapping(Vector3D p, Box box){
        if(p.getX() + this.getWidth() >= box.getPosition().getX()||
                p.getX() <= box.getPosition().getX() + box.getWidth())
            return true;
        if(p.getY() + this.getDepth() >= box.getPosition().getY()||
                p.getY() <= box.getPosition().getY() + box.getDepth())
            return true;
        if(p.getZ() >= box.getPosition().getZ() + box.getHeight()||
                p.getZ() + this.getHeight() <= box.getPosition().getZ())
            return true;
        return false;
    }*/


}
