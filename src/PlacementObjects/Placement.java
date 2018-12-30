package PlacementObjects;

import PackingObjects.Box;

public class Placement {
    private Box box;
    private Vector3D position;
    private Vector3D orientation;
    public Placement(Box box, Vector3D position, Vector3D orientation){
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

    public Vector3D getOrientation() {
        return orientation;
    }
}
