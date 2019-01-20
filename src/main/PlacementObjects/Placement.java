package main.PlacementObjects;

import main.PackingObjects.Box;
import main.PackingObjects.Cuboid;

public class Placement {
    private Box box;
    private Vector3D position;
    private Cuboid orientation;
    public Placement(Box box, Vector3D position, Cuboid orientation){
        this.box = box;
        this.position = position;
        this.orientation = orientation;
    }

    public Box getBox() {
        return box;
    }

    public Vector3D getPosition() {
        return position;
    }

    public Cuboid getOrientation() {
        return orientation;
    }
}
