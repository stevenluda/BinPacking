package State;

import PackingObjects.Box;
import PackingObjects.FreeSpace;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Surface;

import java.util.ArrayList;

public class PalletState extends State {
    ArrayList<Box> packedBoxes = new ArrayList<Box>();
    int totalWeight = 0;
    ArrayList<Surface> surfaces = new ArrayList<Surface>();
    ArrayList<FreeSpace> freespaces = new ArrayList<FreeSpace>();
    public PalletState(){

    }

    public void updateState(Box box){
        packedBoxes.add(box);
        totalWeight += box.getWeight();
        //TODO: update free spaces

    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void updateFreeSpaces(Box box) throws Exception {
        //segment each free spaces if necessary
        ArrayList<FreeSpace> freeSpacesToAdd = new ArrayList<>();
        ArrayList<FreeSpace> freeSpacesToRemove = new ArrayList<>();
        for(FreeSpace fs: freespaces)
        {
            ArrayList<FreeSpace> result = fs.segmentSpace(box);
            if(!result.isEmpty())
            {
                freeSpacesToRemove.add(fs);
            }
            else
            {
                freeSpacesToAdd.addAll(result);
            }
        }

        freespaces.removeAll(freeSpacesToRemove);
        freespaces.addAll(freeSpacesToAdd);
        //add supporting surface for each space if necessary
        //don't forget to register the freespace as listener of surface covered event
        for(FreeSpace fs: freespaces)
        {
            if(fs.getZBottom() == box.getZTop())
            {
                PositionedRectangle pr = fs.getBottom().getHorizontalIntersection(box.getTop());
                if(pr != null)
                {
                    Surface sf = new Surface(pr);
                    sf.addSurfaceEventListener(fs);
                    fs.addSurface(sf);
                }
            }
        }

    }
}
