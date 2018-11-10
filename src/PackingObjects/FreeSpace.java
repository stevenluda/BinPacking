package PackingObjects;

import PlacementObjects.Point;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Rectangle;
import PlacementObjects.Surface;

import java.util.ArrayList;
import java.util.HashSet;

public class FreeSpace extends PositionedCuboid {
    ArrayList<Surface> supportingSurfaces = new ArrayList<Surface>();
    Pallet pallet;
    public FreeSpace(int width, int depth, int height, Point position, Pallet pallet, ArrayList<Surface> supportingSurfaces) {
        super(width, depth, height, position);
        this.pallet = pallet;
        supportingSurfaces.addAll(supportingSurfaces);
    }

    public FreeSpace(int width, int depth, int height, Point position, Pallet pallet, Surface supportingSurface) {
        super(width, depth, height, position);
        this.pallet = pallet;
        supportingSurfaces.add(supportingSurface);
    }

    public void segmentSpace(Box box){
        //The segmentation of the free space is based on the surfaces of the physical box not touching a virtual wall of
        //the free space.

        //find the non-touching surfaces of the physical box. Note that the bottom is not always touching because the
        //supporting surface may not be provided by the this free space
        ArrayList<FreeSpace> newFreeSpaces = new ArrayList<>();
        if(box.getZBottom() > position.getZ())
        {
            //the bottom of physical box is a separating plane
            FreeSpace fs = new FreeSpace(width, depth, box.getZBottom() - position.getZ(), position, this.pallet, supportingSurfaces);
            newFreeSpaces.add(fs);
        }
        else if(box.getZBottom() == position.getZ())
        {
            //TODO: check if any supporting surfaces of this free space overlaps with the bottom of the box and reduce them if necessary
            for(Surface sf: supportingSurfaces)
            {

            }
        }

        if(box.getZTop() < position.getZ() + height)
        {
            // the top of the physical box is a separating plane
            // determine the intersecting area
            PositionedRectangle intersectingArea = box.getTop().getHorizontalIntersection(this.getBottom());
            Surface sf = new Surface(intersectingArea);
            FreeSpace fs = new FreeSpace(width, depth,position.getZ() + height - box.getZTop(),
                    new Point(position.getX(), position.getY(),box.getZTop()), this.pallet, sf);
        }

        if(box.getXLeft() > position.getX())
        {
            //the left side of physical box is a separating plane
            FreeSpace fs = new FreeSpace(box.getXLeft()-position.getX(), depth, height, position, this.pallet, supportingSurfaces);
            newFreeSpaces.add(fs);
        }

        if(box.getXRight() < position.getZ() + width)
        {
            // the right of the physical box is a separating plane
            FreeSpace fs = new FreeSpace(position.getZ() + width - box.getXRight(), depth,
                    height, new Point(box.getXRight(), position.getY(), position.getZ()), this.pallet, supportingSurfaces);
            newFreeSpaces.add(fs);
        }

        if(box.getYFront() > position.getY())
        {
            // the front of the physical box is a separating plane
            FreeSpace fs = new FreeSpace(width, box.getYFront() - position.getY(), height, position, this.pallet, supportingSurfaces);
            newFreeSpaces.add(fs);
        }

        if(box.getYBack() < position.getY() + depth)
        {
            // the back of the physical box is a separating plane
            FreeSpace fs = new FreeSpace(width, position.getY() + depth - box.getYBack(),
                    height, new Point(position.getX(), box.getYBack(), position.getZ()), this.pallet, supportingSurfaces);
            newFreeSpaces.add(fs);
        }
        //TODO: what if box top is touching bottom of some other box
    }

    private ArrayList<Surface> getSurfaces(int y){
        ArrayList<Surface> result = new ArrayList<>();
        for(Surface sf: supportingSurfaces)
        {
            if(sf.getZ() == y)
            {
                result.add(sf);
            }
        }
        return result;
    }

    private void updateSurfaces(Box box) throws Exception {
        for(Surface sf: supportingSurfaces)
        {
            ArrayList<PositionedRectangle> rectanglesToUpdate = new ArrayList<>();
            ArrayList<PositionedRectangle> rectanglesToAdd = new ArrayList<>();
            for(PositionedRectangle rt: sf.getMaximalRectangles())
            {
                PositionedRectangle intersection = rt.getHorizontalIntersection(box.getBottom());
                if(intersection != null)
                {
                    rectanglesToAdd.addAll(rt.reduce(intersection));
                    rectanglesToUpdate.add(rt);
                }
            }
            sf.getMaximalRectangles().removeAll(new HashSet<PositionedRectangle>(rectanglesToUpdate));
            sf.getMaximalRectangles().addAll(rectanglesToAdd);
        }
    }
    //TODO: if the supporting surface is fully covered underneath physical boxes, it should be removed.
    //TODO: or marked as fully covered. So that when other freespaces will get a flag and remove it.
    //TODO: or use event listener for fully covered event.

}
