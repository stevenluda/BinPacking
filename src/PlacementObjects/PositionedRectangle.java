package PlacementObjects;

import PackingObjects.PackingOperations;

import java.util.ArrayList;
import java.util.List;

public class PositionedRectangle extends Rectangle implements PackingOperations<PositionedRectangle, PositionedRectangle> {
    Vector3D position;
    public PositionedRectangle(int width, int depth){
        this(width, depth, new Vector3D(0,0,0));
    }

    public PositionedRectangle(int width, int depth, Vector3D position)
    {
        super(width, depth);
        this.position = position;
    }

    public PositionedRectangle(PositionedRectangle rt){
        this(rt.width, rt.depth, rt.position);
    }

    public Vector3D getPosition() {
        return position;
    }
    public int getXLeft(){return position.getX();}
    public int getXRight(){return position.getX() + width;}
    public int getYFront(){return position.getY();}
    public int getYBack(){return position.getY() + depth;}

    public boolean enclose(PositionedRectangle pr){
        if(pr.getXLeft() >= this.getXLeft()
        && pr.getXRight() <= this.getXRight()
        && pr.getYFront() >= this.getYFront()
        && pr.getYBack() <= this.getYBack())
            return true;
        return false;
    }

    public boolean isOverlapping(PositionedRectangle pr){
        if(pr.getPosition().getX() >= position.getX() + width || pr.getPosition().getX() + pr.getWidth() <= position.getX())
            return false;
        if(pr.getPosition().getY() >= position.getY() + depth || pr.getPosition().getY() + pr.getDepth() <= position.getY())
            return false;
        return true;
    }

    public PositionedRectangle getHorizontalIntersection(PositionedRectangle pr)
    {
        int x, y, w, d;
        if(pr.getPosition().getX() >= position.getX() + width || pr.getPosition().getX() + pr.getWidth() <= position.getX())
            return null;
        if(pr.getPosition().getY() >= position.getY() + depth || pr.getPosition().getY() + pr.getDepth() <= position.getY())
            return null;
        if(pr.getPosition().getX() > position.getX())
        {
            x = pr.getPosition().getX();
            if(pr.getPosition().getX() + pr.getWidth() <= position.getX() + width)
            {
                w = pr.getWidth();
            }
            else
            {
                w = position.getX() + width - x;
            }
        }
        else
        {
            x = position.getX();
            if(position.getX() + width <= pr.getPosition().getX() + pr.getWidth())
            {
                w = width;
            }
            else
            {
                w = pr.getPosition().getX() + pr.getWidth() - x;
            }
        }
        if(pr.getPosition().getY() > position.getY())
        {
            y = pr.getPosition().getY();
            if(pr.getPosition().getY() + pr.getDepth() <= position.getY() + depth)
            {
                d = pr.getDepth();
            }
            else
            {
                d = position.getY() + depth - y;
            }
        }
        else
        {
            y = position.getY();
            if(position.getY() + depth <= pr.getPosition().getY() + pr.getDepth())
            {
                d = depth;
            }
            else
            {
                d = pr.getPosition().getY() + pr.getDepth() - y;
            }
        }

        return new PositionedRectangle(w, d, new Vector3D(x, y, position.getZ()));
    }

    @Override
    public List<PositionedRectangle> segmentSpace(PositionedRectangle rect){
        List<PositionedRectangle> result = new ArrayList<PositionedRectangle>();
        //check if left edge is a separating line
        if(rect.getXLeft() > this.getXLeft())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(rect.getXLeft() - this.getXLeft(), this.getDepth(), this.position));
        }
        //check if right edge is a separating line
        if(rect.getXRight() < this.getXRight())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(this.getXRight() - rect.getXRight(),
                    this.getDepth(), new Vector3D(rect.getXRight(), this.getYFront(), this.position.getZ())));
        }
        //check if the front edge is a separating line
        if(rect.getPosition().getY() > this.position.getY())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(this.getWidth(), rect.getYFront() - this.getYFront(), this.position));
        }
        //check if the back edge is a separating line
        if(rect.getYBack() < this.getYBack())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(this.getWidth(), this.getYBack() - rect.getYBack(),
                    new Vector3D(this.getXLeft(), rect.getYBack(), this.position.getZ())));
        }
        return result;
    }

    @Override
    public boolean accomodate(PositionedRectangle object) {
        if(this.width >= object.getWidth() && this.depth >= object.getDepth())
            return true;
        if(this.width >= object.getDepth() && this.depth >= object.getWidth())
            return true;
        return false;
    }

    public int getArea(){
        return width*depth;
    }
}
