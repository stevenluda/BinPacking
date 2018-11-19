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

    public Point getLeftFrontBottomPosition()
    {
        return position;
    }

    public Point getRightFrontBottomPosition()
    {
        return new Point(position, width, 0,0);
    }

    public Point getLeftBackBottomPosition()
    {
        return new Point(position, 0, depth, 0);
    }

    public Point getRightBackBottomPosition()
    {
        return new Point(getRightFrontBottomPosition(), 0, depth, 0);
    }

    public Point getLeftFrontTopPosition()
    {
        return new Point(position, 0, 0, height);
    }

    public Point getRightFrontTopPosition()
    {
        return new Point(getRightFrontBottomPosition(), 0, 0, height);
    }

    public Point getLeftBackTopPosition()
    {
        return new Point(getLeftBackBottomPosition(), 0,0, height);
    }

    public Point getRightBackTopPosition()
    {
        return new Point(getRightBackBottomPosition(), 0, 0, height);
    }

    public int getXLeft()
    {
        return position.getX();
    }

    public int getXRight()
    {
        return position.getX() + width;
    }

    public int getYFront()
    {
        return position.getY();
    }

    public int getYBack()
    {
        return position.getY() + depth;
    }

    public int getZBottom()
    {
        return position.getZ();
    }

    public int getZTop()
    {
        return position.getZ() + height;
    }

    public PositionedRectangle getBottom()
    {
        return new PositionedRectangle(width, depth, position);
    }

    public PositionedRectangle getTop()
    {
        return new PositionedRectangle(width, depth, new Point(position.getX(), position.getY(), position.getZ()+height));
    }

    public void setPosition(Point pos){
        position = pos;
    }
}
