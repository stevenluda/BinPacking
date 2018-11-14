package PackingObjects;

import PlacementObjects.Point;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Surface;
import utils.SurfaceEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class FreeSpace extends PositionedCuboid implements SurfaceEventListener {
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

    public ArrayList<FreeSpace> segmentSpace(Box box) throws Exception {
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
            // check if any supporting surface of this free space overlaps with the bottom of the box and reduce them if necessary
            updateSurfaces(box);
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

        return newFreeSpaces;
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
            ArrayList<PositionedRectangle> rectanglesToRemove = new ArrayList<>();
            ArrayList<PositionedRectangle> rectanglesToAdd = new ArrayList<>();
            for(PositionedRectangle rt: sf.getMaximalRectangles())
            {
                PositionedRectangle intersection = rt.getHorizontalIntersection(box.getBottom());
                if(intersection != null)
                {
                    rectanglesToAdd.addAll(rt.reduce(intersection));//Note: if rt.reduce returns an empty arraylist, it means intersection fully covers the supporting surface
                    rectanglesToRemove.add(rt);
                }
            }
            sf.getMaximalRectangles().removeAll(new HashSet<PositionedRectangle>(rectanglesToRemove));
            sf.getMaximalRectangles().addAll(rectanglesToAdd);
            if(sf.getMaximalRectangles().isEmpty())
            {
                sf.setAllCovered();
            }
        }
    }

    public void addSurface(Surface sf)
    {
        supportingSurfaces.add(sf);
    }

    @Override
    public void OnSurfaceCovered(Surface eventSource) {
        supportingSurfaces.remove(eventSource);
    }

    public int getVolume() {
        return width*depth*height;
    }

    public boolean accomadate(Box box) {
        if(getVolume() < box.getVolume())
            return false;
        if(!(dimensionFits(box.rotate(0,0,0))||dimensionFits(box.rotate(0,0,90))||
            dimensionFits(box.rotate(0,90,0))||dimensionFits(box.rotate(0,90,90))||
            dimensionFits(box.rotate(90,0,0))||dimensionFits(box.rotate(90,0,90))))
            return false;
        return true;
    }

    public boolean dimensionFits(Point vector){
        if(width >= vector.getX() && depth >= vector.getY() && height >= vector.getZ())
            return true;
        return false;
    }
}
