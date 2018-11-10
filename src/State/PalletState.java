package State;

import PackingObjects.Box;
import PackingObjects.FreeSpace;
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

    public void updateFreeSpaces(Box box){
        //TODO: segment each free spaces if necessary

        //TODO: add supporting surface for each space if necessary
    }
}
