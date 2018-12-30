package PackingObjects;

import PlacementObjects.Placement;
import State.LayerState;
import State.PalletState;
import javafx.util.Pair;

import java.util.ArrayList;

public class Pallet extends Cuboid {
    int maxWeight;
    PalletState state;
    String id;

    public Pallet(String id, int width, int depth, int height, int maxWeight) {
        super(width, depth, height);
        this.id = id;
        this.maxWeight = maxWeight;
        state = new PalletState(this);
    }

    public void placeBox(Box box) throws Exception {
        state.updateState(box);
        state.outputState();
        ArrayList<Pair<Box,Box>> conflicts = state.findOverlappingBoxes();
        if(!conflicts.isEmpty()){
            System.out.print("Box overlaps");
        }
    }

    public void placeLayer(LayerState layerState, int z_position){
        for(Placement placement: layerState.getPlacements()){
            placement.getPosition().setZ(z_position);
            placement.getBox().placeTheBox(placement.getPosition(), placement.getOrientation());
            try {
                state.updateState(placement.getBox());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        state.outputState();
    }

    public String getId() {
        return id;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public PalletState getState() {
        return state;
    }

    public boolean hasEnoughWeightCapacity(int additionalWeight){
        return state.getTotalWeight() + additionalWeight <= maxWeight?true:false;
    }

    public ArrayList<FreeSpace3D> getFeasibleFreeSpaces(Box box){
        return state.getFeasibleFreeSpaces(box);
    }

}
