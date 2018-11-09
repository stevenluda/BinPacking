package PackingObjects;


import PlacementObjects.Point;

import java.util.ArrayList;

public class Box extends PositionedCuboid {
    String id;
    int weight;
    ArrayList<Box> aboveBoxes = new ArrayList<Box>();
    ArrayList<Box> underneathBoxes = new ArrayList<Box>();

    public Box(String id, int width, int depth, int height, int weight, Point position) {
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


    public boolean isOverlapping(Point p, Box box){
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
    }

}
