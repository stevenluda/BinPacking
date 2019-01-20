package PlacementObjects;

import java.util.ArrayList;
import java.util.List;

public class Surface extends PositionedRectangle{
    int z = 0;
    boolean isCovered;

    public Surface(PositionedRectangle rt) {
        super(rt);
        z = position.getZ();
        isCovered = false;
    }

    public int getZ() {
        return z;
    }

    public boolean isCovered()
    {
        return isCovered;
    }

    public void setCovered()
    {
        isCovered = true;
    }

    public ArrayList<Surface> getReducedSurfaces(PositionedRectangle rt) throws Exception {
        List<PositionedRectangle> result_rts = segmentSpace(rt);
        ArrayList<Surface> result = new ArrayList<>();
        if(!result_rts.isEmpty()){
            for(PositionedRectangle r: result_rts){
                result.add(new Surface(r));
            }
        }
        return result;
    }
}
