package PlacementObjects;

import java.util.ArrayList;

public class Surface{
    ArrayList<PositionedRectangle> maximalRectangles = new ArrayList<PositionedRectangle>(); // the set of possible maximal rectangles.
    int z = 0;
    public Surface(ArrayList<PositionedRectangle> maximalRectangles) {
        this.maximalRectangles.addAll(maximalRectangles);
        z = maximalRectangles.get(0).getPosition().getZ();
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
}
