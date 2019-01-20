package main.State;

import main.PackingObjects.Box;
import main.PackingObjects.FreeSpace3D;
import main.PackingObjects.Pallet;
import main.PlacementObjects.PositionedRectangle;
import main.PlacementObjects.Surface;
import main.PlacementObjects.Vector3D;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalletState extends State {
    ArrayList<Box> packedBoxes = new ArrayList<Box>();
    int totalWeight = 0;
    ArrayList<FreeSpace3D> freespaces = new ArrayList<FreeSpace3D>();
    public PalletState(Pallet pallet){
        freespaces.add(new FreeSpace3D(pallet.getWidth(), pallet.getDepth(), pallet.getHeight(), new Vector3D(0,0,0),
                pallet, new Surface(new PositionedRectangle(pallet.getWidth(), pallet.getDepth(),new Vector3D(0,0,0)))));
    }

    public void updateState(Box box) throws Exception {
        packedBoxes.add(box);
        totalWeight += box.getWeight();
        //update free spaces
        updateFreeSpaces(box);
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void updateFreeSpaces(Box box){
        //segment each free spaces if necessary
        ArrayList<FreeSpace3D> freeSpacesToAdd3D = new ArrayList<>();
        ArrayList<FreeSpace3D> freeSpacesToRemove3D = new ArrayList<>();
        for(FreeSpace3D fs: freespaces)
        {
            if(fs.isOverlapping(box)){
                List<FreeSpace3D> result = fs.segmentSpace(box);
                if(!result.isEmpty())
                {
                    freeSpacesToRemove3D.add(fs);
                    freeSpacesToAdd3D.addAll(result);
                }
            }
        }

        freespaces.removeAll(freeSpacesToRemove3D);
        //update supporting surface for each remaining freespaces if necessary
        for(FreeSpace3D fs: freespaces)
        {
            if(fs.getZBottom() == box.getZTop())
            {
                PositionedRectangle pr = fs.getPositionedBottom().getHorizontalIntersection(box.getTop());
                if(pr != null)
                {
                    Surface sf = new Surface(pr);
                    fs.addSurface(sf);
                }
            }
        }
        freespaces.addAll(freeSpacesToAdd3D);
    }

    public ArrayList<FreeSpace3D> getFeasibleFreeSpaces(Box box) {
        ArrayList<FreeSpace3D> feasible3DFreeSpaces = new ArrayList<>();
        for(FreeSpace3D fs:freespaces){
            if(fs.accomodate(box))
               feasible3DFreeSpaces.add(fs);
        }
        return feasible3DFreeSpaces;
    }

    public void outputState(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("packed_boxes.txt"));
            for(Box box: packedBoxes){
                writer.write(box.toString()+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Box, Box> findOverlappingBoxes(){
        Map<Box, Box> conflictBoxes = new HashMap<>();
        for(int i = 0; i < packedBoxes.size(); i++){
            for(int j = i+1; j < packedBoxes.size(); j++){
                if(packedBoxes.get(i).isOverlapping(packedBoxes.get(j))){
                    conflictBoxes.put(packedBoxes.get(i), packedBoxes.get(j));
                }
            }
        }
        return conflictBoxes;
    }
}
