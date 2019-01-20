package PackingObjects;

import PlacementObjects.PositionedRectangle;
import PlacementObjects.Vector3D;

public class PositionedCuboid extends Cuboid {
    Vector3D position;
    public PositionedCuboid(int width, int depth, int height, Vector3D position)
    {
        super(width, depth, height);
        this.position = position;
    }

    public Vector3D getPosition()
    {
        return position;
    }

    public Vector3D getLeftFrontBottomPosition()
    {
        return position;
    }

    public Vector3D getRightFrontBottomPosition()
    {
        return new Vector3D(position, width, 0,0);
    }

    public Vector3D getLeftBackBottomPosition()
    {
        return new Vector3D(position, 0, depth, 0);
    }

    public Vector3D getRightBackBottomPosition()
    {
        return new Vector3D(getRightFrontBottomPosition(), 0, depth, 0);
    }

    public Vector3D getLeftFrontTopPosition()
    {
        return new Vector3D(position, 0, 0, height);
    }

    public Vector3D getRightFrontTopPosition()
    {
        return new Vector3D(getRightFrontBottomPosition(), 0, 0, height);
    }

    public Vector3D getLeftBackTopPosition()
    {
        return new Vector3D(getLeftBackBottomPosition(), 0,0, height);
    }

    public Vector3D getRightBackTopPosition()
    {
        return new Vector3D(getRightBackBottomPosition(), 0, 0, height);
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

    public PositionedRectangle getPositionedBottom()
    {
        return new PositionedRectangle(width, depth, position);
    }

    public PositionedRectangle getTop()
    {
        return new PositionedRectangle(width, depth, new Vector3D(position.getX(), position.getY(), position.getZ()+height));
    }

    public void setPosition(Vector3D pos){
        position = pos;
    }

    public boolean isOverlapping(PositionedCuboid cuboid){
        if(this.getZBottom() >= cuboid.getZTop())
            return false;
        if(this.getZTop() <= cuboid.getZBottom())
            return false;
        if(this.getXLeft() >= cuboid.getXRight())
            return false;
        if(this.getXRight() <= cuboid.getXLeft())
            return false;
        if(this.getYFront() >= cuboid.getYBack())
            return false;
        if(this.getYBack() <= cuboid.getYFront())
            return false;
        return true;
    }
}
