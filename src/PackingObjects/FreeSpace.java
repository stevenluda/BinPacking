package PackingObjects;

import PlacementObjects.Point;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Rectangle;
import PlacementObjects.Surface;

import java.util.ArrayList;

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
        if(box.getPosition().getZ()>position.getZ())
        {
            //the bottom of physical box is a separating plane
            FreeSpace fs = new FreeSpace(width, depth, box.getPosition().getZ() - position.getZ(), position, this.pallet, supportingSurfaces);
            newFreeSpaces.add(fs);
        }
        else
        {
            //TODO: check if any supporting surfaces of this free space overlaps with the bottom of the box and reduce them if necessary

        }

        if(box.getPosition().getZ() + box.getHeight() < position.getZ() + height)
        {
            // the top of the physical box is a separating plane
            // determine the intersecting area
            PositionedRectangle intersectingArea = box.getTop().getHorizontalIntersection(this.getBottom());
            Surface sf = new Surface(intersectingArea);
            FreeSpace fs = new FreeSpace(width, depth,box.getPosition().getZ() - position.getZ(),
                    new Point(box.getPosition().getX(), box.getPosition().getY(),box.getPosition().getZ() + box.getHeight()), this.pallet, sf);
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

    private void updateSurfaces(Box box)
    {
        ArrayList<Surface> sameLevelSurfaces = getSurfaces(box.getPosition().getZ());
        for(Surface sf: sameLevelSurfaces)
        {
            for(PositionedRectangle rt: sf.getMaximalRectangles())
            {
                if(rt.getHorizontalIntersection(box.getBottom()) != null)
                {
                    //TODO: create a reduce method in PositionedRectangle
                    rt.reduce();
                }
            }
        }
    }

}
