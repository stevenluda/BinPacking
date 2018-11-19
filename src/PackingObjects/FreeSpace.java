package PackingObjects;

import PlacementObjects.Point;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Surface;

import java.util.ArrayList;
import java.util.HashSet;

public class FreeSpace extends PositionedCuboid{
    ArrayList<Surface> supportingSurfaces = new ArrayList<Surface>();
    Pallet pallet;
    int supportingArea = 0;
    public FreeSpace(int width, int depth, int height, Point position, Pallet pallet, ArrayList<Surface> supportingSurfaces) {
        super(width, depth, height, position);
        this.pallet = pallet;
        supportingSurfaces.addAll(supportingSurfaces);
        for(Surface sf: supportingSurfaces){
            supportingArea += sf.getArea();
        }
        //TODO: when adding or removing supporting surface, update solid supporting area
    }

    public FreeSpace(int width, int depth, int height, Point position, Pallet pallet, Surface supportingSurface) {
        super(width, depth, height, position);
        this.pallet = pallet;
        supportingSurfaces.add(supportingSurface);
    }

    public ArrayList<FreeSpace> segmentSpace(Box box) throws Exception {
        //The segmentation of the free space is based on the surfaces of the physical box
        //the free space.

        //find the non-touching surfaces of the physical box. Note that the bottom is not always touching because the
        //supporting surface may not be provided by the this free space
        ArrayList<FreeSpace> newFreeSpaces = new ArrayList<>();
        ArrayList<Surface> newSurfaces = new ArrayList();
        if(box.getZBottom() > position.getZ())
        {
            //the bottom of physical box is a separating plane
            FreeSpace fs = new FreeSpace(width, depth, box.getZBottom() - position.getZ(), position, this.pallet, supportingSurfaces);
            newFreeSpaces.add(fs);
        }else if(box.getZBottom() == position.getZ()) {
            //modify the current supoorting surfaces if any of them intersects with
            // check if any supporting surface of this free space overlaps with the bottom of the box and reduce them if necessary
            newSurfaces = updateSurfaces(box);
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
            FreeSpace fs = new FreeSpace(box.getXLeft()-position.getX(), depth, height, position, this.pallet, trimIrrelaventSurfaces(newSurfaces, box, true, false,false,false));
            newFreeSpaces.add(fs);
        }

        if(box.getXRight() < position.getZ() + width)
        {
            // the right of the physical box is a separating plane
            FreeSpace fs = new FreeSpace(position.getZ() + width - box.getXRight(), depth,
                    height, new Point(box.getXRight(), position.getY(), position.getZ()), this.pallet, trimIrrelaventSurfaces(newSurfaces, box, false, true,false,false));
            newFreeSpaces.add(fs);
        }

        if(box.getYFront() > position.getY())
        {
            // the front of the physical box is a separating plane
            FreeSpace fs = new FreeSpace(width, box.getYFront() - position.getY(), height, position, this.pallet, trimIrrelaventSurfaces(newSurfaces, box, false, false,true,false));
            newFreeSpaces.add(fs);
        }

        if(box.getYBack() < position.getY() + depth)
        {
            // the back of the physical box is a separating plane
            FreeSpace fs = new FreeSpace(width, position.getY() + depth - box.getYBack(),
                    height, new Point(position.getX(), box.getYBack(), position.getZ()), this.pallet, trimIrrelaventSurfaces(newSurfaces, box, false, false,false,true));
            newFreeSpaces.add(fs);
        }
        //TODO: what if box top is touching bottom of some other box

        return newFreeSpaces;
    }

    private ArrayList<Surface> trimIrrelaventSurfaces(ArrayList<Surface> surfacesToTrim, Box box, boolean TO_LEFT, boolean TO_RIGHT, boolean TO_FRONT, boolean TO_BACK ){
        ArrayList<Surface> result = (ArrayList) surfacesToTrim.clone();
        ArrayList<Surface> irrelavent = new ArrayList<>();
        if(TO_LEFT){
            for(Surface sf:result){
                if(sf.getXRight() > box.getXLeft())
                    irrelavent.add(sf);
            }
        }
        if(TO_RIGHT){
            for(Surface sf:result){
                if(sf.getXLeft() < box.getXRight())
                    irrelavent.add(sf);
            }
        }
        if(TO_FRONT){
            for(Surface sf:result){
                if(sf.getYBack() > box.getYFront())
                    irrelavent.add(sf);
            }
        }
        if(TO_BACK){
            for(Surface sf:result){
                if(sf.getYFront() < box.getYBack())
                    irrelavent.add(sf);
            }
        }

        return result;
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

    public int getSupportingSurfaceCount(){
        return supportingSurfaces.size();
    }

    private ArrayList<Surface> updateSurfaces(Box box) throws Exception {
        ArrayList<Surface> result = (ArrayList) supportingSurfaces.clone();
        ArrayList<Surface> surfacesToAdd = new ArrayList<>();
        ArrayList<Surface> surfacesToRemove = new ArrayList<>();
        for(Surface sf: result)
        {
            PositionedRectangle intersection = sf.getHorizontalIntersection(box.getBottom());
            if(intersection != null)
            {
                ArrayList<Surface> newSurfaces = sf.getReducedSurfaces(intersection);
                surfacesToAdd.addAll(newSurfaces);//Note: if rt.reduce returns an empty arraylist, it means intersection fully covers the supporting surface
                surfacesToRemove.add(sf);
            }
        }
        result.removeAll(new HashSet<Surface>(surfacesToRemove));
        result.addAll(surfacesToAdd);
        return result;
    }

    public void addSurface(Surface sf)
    {
        supportingSurfaces.add(sf);
        supportingArea += sf.getArea();
    }

    public int getVolume() {
        return width*depth*height;
    }

    public int getSupportingArea(){
        return supportingArea;
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
