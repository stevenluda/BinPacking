import PackingObjects.Box;
import PackingObjects.Pallet;

import java.util.ArrayList;

public class PalletBuilder {
    ArrayList<Box> boxesToPack;
    public PalletBuilder(ArrayList<Box> boxes)
    {
        boxesToPack = boxes;
        //TODO: create pallet dimension object

    }

    public Pallet buildPallet(){
        Pallet pallet = new Pallet("Pallet", 800, 1200, 2055, 1200000);// while still packable
        //TODO: finish while loop to pack boxes
        /*while()
        {

        }*/
        return pallet;
    }
}
