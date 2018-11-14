import PackingObjects.Box;
import PackingObjects.Pallet;
import utils.InputReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args)
    {
        InputReader reader = new InputReader();
        ArrayList<Box> unpackedBoxes = reader.readData("test_instance.txt");

        ArrayList<Pallet> pallets = new ArrayList<>();

        //TODO: how to recursively build pallets until all boxes are packed

    }




}
