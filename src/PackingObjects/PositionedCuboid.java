package PackingObjects;

import PlacementObjects.Point;
import PlacementObjects.PositionedRectangle;

import javax.swing.text.Position;

public class PositionedCuboid extends Cuboid {
    Point position;
    public PositionedCuboid(int width, int depth, int height, Point position)
    {
        super(width, depth, height);
        this.position = position;
    }

    public Point getPosition()
    {
        return position;
    }

    public PositionedRectangle getBottom()
    {
        return new PositionedRectangle(width, depth, position);
    }

    public PositionedRectangle getTop()
    {
        return new PositionedRectangle(width, depth, new Point(position.getX(), position.getY(), position.getZ()+height));
    }
}
