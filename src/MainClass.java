import PackingObjects.Box;
import PackingObjects.Pallet;
import utils.InputReader;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args)
    {
        InputReader reader = new InputReader();
        ArrayList<Box> unpackedBoxes = reader.readData("E:\\Projects\\BinPacking\\src\\test_instance.txt");

        ArrayList<Pallet> pallets = new ArrayList<>();

        PalletBuilder builder = new PalletBuilder(unpackedBoxes);
        try {
            Pallet pallet = builder.buildPallet(true);
            if(pallet != null){
                pallets.add(pallet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO: how to recursively build pallets until all boxes are packed
        
    }




}
