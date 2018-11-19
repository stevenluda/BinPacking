import PackingObjects.Box;
import PackingObjects.FreeSpace;
import PackingObjects.Pallet;
import PlacementObjects.Point;
import utils.FreeSpaceComparator;

import java.util.ArrayList;

public class PalletBuilder {
    ArrayList<Box> boxesToPack;
    Box box = null;
    public PalletBuilder(ArrayList<Box> boxes)
    {
        boxesToPack = boxes;
        //TODO: create pallet dimension object

    }

    public Pallet buildPallet(){
        Pallet pallet = new Pallet("Pallet", 800, 1200, 2055, 1200000);// while still packable
        //TODO: finish while loop to pack boxes
        box = findPlacement(pallet);
        while(box != null)
        {
            pallet.placeBox(box);
            box = findPlacement(pallet);
        }
        return pallet;
    }

    private Box findPlacement(Pallet pallet){
        for(Box box: boxesToPack){
            ArrayList<FreeSpace> fss = pallet.getFeasibleFreeSpaces(box);
            if(!fss.isEmpty()){
                //add comparator for sorting free spaces
                fss.sort(new FreeSpaceComparator());
                //TODO: currently only placing at front bottom left point, add attemps for placing at other three points
                //TODO: currently returns when a first feasible box placement is found, add comparison of multiple feasible placements
                box.setPosition(fss.get(0).getPosition());
                return box;
            }
        }
        return null;
    }

}
