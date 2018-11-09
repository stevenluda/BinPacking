package PlacementObjects;

import javax.swing.text.Position;
import java.util.ArrayList;

public class PositionedRectangle extends Rectangle {
    Point position;
    public PositionedRectangle(int width, int depth, Point position)
    {
        super(width, depth);
        this.position = position;
    }

    public Point getPosition() {
        return position;
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

        return new PositionedRectangle(w, d, new Point(x, y, position.getZ()));
    }

    public ArrayList<PositionedRectangle> reduce(PositionedRectangle rect) throws Exception
    {
        //check if rect is within the boundaries of this rectangle
        if(!(rect.position.getX() >= this.position.getX() && rect.position.getX() + rect.getWidth() <= this.position.getX() + this.getWidth()))
            throw new Exception("Along width, the rect exceed the boundaries of the rect being reduced.");
        if(!(rect.position.getY() >= this.position.getY() && rect.position.getY() + rect.getDepth() <= this.position.getY() + this.getDepth()))
            throw new Exception("Along depth, the rect exceed the boundaries of the rect being reduced.");
        ArrayList<PositionedRectangle> result = new ArrayList<PositionedRectangle>();
        //check if left edge is a separating line
        if(rect.getPosition().getX() > this.position.getX())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(rect.getPosition().getX() - this.position.getX(), this.getDepth(), this.position));
        }
        //check if right edge is a separating line
        if(rect.getPosition().getX() + rect.getWidth() < this.position.getX() + this.getWidth())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(this.position.getX()+ this.getWidth() - rect.getPosition().getX() - rect.getWidth(),
                    this.getDepth(), new Point(rect.getPosition().getX()+rect.getWidth(), this.position.getY(), this.position.getZ())));
        }
        //check if the front edge is a separating line
        if(rect.getPosition().getY() > this.position.getY())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(this.getWidth(), rect.getPosition().getY() - this.position.getY(), this.position));
        }
        //check if the back edge is a separating line
        if(rect.getPosition().getY() + rect.getDepth() < this.position.getY() + this.getDepth())
        {
            //create one new positioned rectangle
            result.add(new PositionedRectangle(this.getWidth(), this.getPosition().getY() + this.getDepth() - rect.getPosition().getY() - rect.getDepth(),
                    this.position));
        }
        return result;
    }
}
