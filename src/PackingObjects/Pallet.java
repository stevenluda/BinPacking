package PackingObjects;

import State.PalletState;
import java.util.ArrayList;

public class Pallet extends Cuboid {
    int maxWeight;
    PalletState state;
    String id;

    public Pallet(String id, int width, int depth, int height, int maxWeight) {
        super(width, depth, height);
        this.id = id;
        this.maxWeight = maxWeight;
    }

    public void placeBox(Box box){
        state.updateState(box);
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

}
