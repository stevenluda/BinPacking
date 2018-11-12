package PlacementObjects;

import utils.SurfaceEventListener;

import java.util.ArrayList;

public class Surface{
    ArrayList<PositionedRectangle> maximalRectangles = new ArrayList<PositionedRectangle>(); // the set of possible maximal rectangles.
    int z = 0;
    boolean isAllCovered;
    ArrayList<SurfaceEventListener> listeners = new ArrayList<>();
    public Surface(ArrayList<PositionedRectangle> maximalRectangles) {
        this.maximalRectangles.addAll(maximalRectangles);
        z = maximalRectangles.get(0).getPosition().getZ();
        isAllCovered = false;
    }

    public Surface(PositionedRectangle maximalRectangle) {
        this.maximalRectangles.add(maximalRectangle);
    }

    public ArrayList<PositionedRectangle> getMaximalRectangles() {
        return maximalRectangles;
    }

    public int getZ() {
        return z;
    }

    public boolean isAllCovered()
    {
        return isAllCovered;
    }

    public void setAllCovered()
    {
        isAllCovered = true;
    }

    public void addSurfaceEventListener(SurfaceEventListener l)
    {
        listeners.add(l);
    }

    public void removeSurfaceEventListener(SurfaceEventListener l)
    {
        listeners.remove(l);
    }

    protected void fireSurfaceCovered()
    {
        if(!listeners.isEmpty())
        {
            for(SurfaceEventListener l:listeners)
            {
                l.OnSurfaceCovered(this);
            }
        }
    }
}
