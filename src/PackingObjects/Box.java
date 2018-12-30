package PackingObjects;


import PlacementObjects.Vector3D;

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

    public String getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public void resetOrientation(Vector3D vec){
        width = vec.getX();
        depth = vec.getY();
        height = vec.getZ();
    }

    public void placeTheBox(Vector3D pos, Vector3D orientation){
        this.setPosition(pos);
        this.resetOrientation(orientation);
    }

    public String toString(){
        return this.id +","+this.width+","+this.depth+","+this.height+","+position.getX()+","+position.getY()+","+position.getZ();
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
