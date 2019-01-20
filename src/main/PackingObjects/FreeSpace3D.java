package main.PackingObjects;

import main.PlacementObjects.PositionedRectangle;
import main.PlacementObjects.Surface;
import main.PlacementObjects.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class FreeSpace3D extends PositionedCuboid implements PackingOperations<FreeSpace3D, Box>{
    ArrayList<Surface> supportingSurfaces = new ArrayList<Surface>();
    Pallet pallet;
    int supportingArea = 0;
    public FreeSpace3D(int width, int depth, int height, Vector3D position, Pallet pallet, ArrayList<Surface> supportingSurfaces) {
        super(width, depth, height, position);
        this.pallet = pallet;
        this.supportingSurfaces.addAll(supportingSurfaces);
        for(Surface sf: this.supportingSurfaces){
            supportingArea += sf.getArea();
        }
        //TODO: when adding or removing supporting surface, update solid supporting area
    }

    public FreeSpace3D(int width, int depth, int height, Vector3D position, Pallet pallet, Surface supportingSurface) {
        super(width, depth, height, position);
        this.pallet = pallet;
        supportingSurfaces.add(supportingSurface);
        supportingArea += supportingSurface.getArea();
    }

    @Override
    public List<FreeSpace3D> segmentSpace(Box box){
        //The segmentation of the free space is based on the surfaces of the physical box
        //the free space.

        //find the non-touching surfaces of the physical box. Note that the bottom is not always touching because the
        //supporting surface may not be provided by this free space
        ArrayList<FreeSpace3D> newFreeSpace3DS = new ArrayList<>();
        ArrayList<Surface> newSurfaces = new ArrayList();
        if(box.getZBottom() > position.getZ())
        {
            //the bottom of physical box is a separating plane
            FreeSpace3D fs = new FreeSpace3D(width, depth, box.getZBottom() - position.getZ(), position, this.pallet, supportingSurfaces);
            newFreeSpace3DS.add(fs);
        }

        if(box.getZTop() < getZTop())
        {
            // the top of the physical box is a separating plane
            // determine the intersecting area
            PositionedRectangle intersectingArea = box.getTop().getHorizontalIntersection(this.getPositionedBottom());
            Surface sf = new Surface(intersectingArea);
            FreeSpace3D fs = new FreeSpace3D(width, depth,position.getZ() + height - box.getZTop(),
                    new Vector3D(position.getX(), position.getY(),box.getZTop()), this.pallet, sf);
            newFreeSpace3DS.add(fs);
        }

        if(box.getXLeft() > getXLeft())
        {
            //the left side of physical box is a separating plane
            ArrayList<Surface> remainingSupportingSurfaces = updateSurfaces(new PositionedRectangle(box.getXLeft() - getXLeft(), depth, position));
            if(!remainingSupportingSurfaces.isEmpty()){
                FreeSpace3D fs = new FreeSpace3D(box.getXLeft() - getXLeft(), depth, height, position, this.pallet, remainingSupportingSurfaces);
                newFreeSpace3DS.add(fs);
            }
        }

        if(box.getXRight() < getXRight())
        {
            // the right of the physical box is a separating plane
            ArrayList<Surface> remainingSupportingSurfaces = updateSurfaces(new PositionedRectangle(getXRight() - box.getXRight(), depth, new Vector3D(box.getXRight(), position.getY(), position.getZ())));
            if(!remainingSupportingSurfaces.isEmpty()){
                FreeSpace3D fs = new FreeSpace3D(getXRight() - box.getXRight(), depth,
                        height, new Vector3D(box.getXRight(), position.getY(), position.getZ()), this.pallet, remainingSupportingSurfaces);
                newFreeSpace3DS.add(fs);
            }
        }

        if(box.getYFront() > getYFront())
        {
            // the front of the physical box is a separating plane
            ArrayList<Surface> remainingSupportingSurfaces = updateSurfaces(new PositionedRectangle(width, box.getYFront() - getYFront(), position));
            if(!remainingSupportingSurfaces.isEmpty()){
                FreeSpace3D fs = new FreeSpace3D(width, box.getYFront() - getYFront(), height, position, this.pallet, remainingSupportingSurfaces);
                newFreeSpace3DS.add(fs);
            }
        }

        if(box.getYBack() < getYBack())
        {
            // the back of the physical box is a separating plane
            ArrayList<Surface> remainingSupportingSurfaces = updateSurfaces(new PositionedRectangle(width, getYBack() - box.getYBack(), new Vector3D(position.getX(), box.getYBack(), position.getZ())));
            if(!remainingSupportingSurfaces.isEmpty()){
                FreeSpace3D fs = new FreeSpace3D(width, getYBack() - box.getYBack(),
                        height, new Vector3D(position.getX(), box.getYBack(), position.getZ()), this.pallet, remainingSupportingSurfaces);
                newFreeSpace3DS.add(fs);
            }
        }

        return newFreeSpace3DS;
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

    private ArrayList<Surface> updateSurfaces(PositionedRectangle newFreeSpaceBottom){
        ArrayList<Surface> result = new ArrayList<>();
        for(Surface sf: supportingSurfaces)
        {
            PositionedRectangle intersection = sf.getHorizontalIntersection(newFreeSpaceBottom);
            if(intersection != null)
            {
                result.add(new Surface(intersection));
            }
        }
        return result;
    }

    public void addSurface(Surface sf)
    {
        supportingSurfaces.add(sf);
        supportingArea += sf.getArea();
    }

    public int getSupportingArea(){
        return supportingArea;
    }

}
