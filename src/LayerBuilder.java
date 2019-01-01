import PackingObjects.Box;
import PackingObjects.Cuboid;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Rectangle;
import State.LayerState;

import java.util.*;

public class LayerBuilder {
    Map<String, Box> boxesToPack = null;
    BoxCluster cluster = new BoxCluster();
    public LayerBuilder(){

    }

    public void updateBoxesToPack(Map<String, Box> boxesToPack){
        this.boxesToPack = boxesToPack;
    }

    public LayerState constructLayer(int nbShuffles){
        List<Box> sameHeightBoxes = cluster.findSameHeightBoxes(boxesToPack);

        //TODO see if it's better to use stream?
        Map<String, Box> sameHeightBoxesMap = new HashMap<>();
        ArrayList<String> sameHeightBoxesIds = new ArrayList<>();
        for(Box box: sameHeightBoxes){
            sameHeightBoxesMap.put(box.getId(), box);
            sameHeightBoxesIds.add(box.getId());
        }

        //How about randomly align boxes from left front corner to right corner
        LayerState bestState = new LayerState();
        for(int i = 0; i < nbShuffles; i++){
            ArrayList<String> shuffledSequence = new ArrayList<>(sameHeightBoxesIds);
            Collections.shuffle(shuffledSequence);
            LayerState state = new LayerState();
            Box box = sameHeightBoxesMap.get(shuffledSequence.get(0));
            state.setLayerHeight(box.getHeight());
            PositionedRectangle p = findPlacement(box.getBottomRectangle(), state);
            while(!shuffledSequence.isEmpty()&& p!=null){
                Cuboid new_dims = randomlyChooseHorizontalOrientation(p, box.getDims());
                state.updateState(box, p.getPosition(), new_dims);
                shuffledSequence.remove(box.getId());
                for(String id: shuffledSequence){
                    box = sameHeightBoxesMap.get(id);
                    p = findPlacement(box.getBottomRectangle(), state);
                    if(p!=null)
                        break;
                }
            }
            if(bestState.getTotalUsedArea() < state.getTotalUsedArea())
                bestState = state;
        }
        if(bestState.getTotalUsedArea() == 0)
            return null;
        else
            return bestState;
    }

    public PositionedRectangle findPlacement(Rectangle boxBottom, LayerState state){
        ArrayList<PositionedRectangle> feasibleFreeSpaces = state.getFeasibleFreeSpaces(boxBottom);
        if(feasibleFreeSpaces.isEmpty())
            return null;
        feasibleFreeSpaces.sort(new Comparator<PositionedRectangle>() {
            @Override
            public int compare(PositionedRectangle o1, PositionedRectangle o2) {
                if(o1.getXLeft() < o2.getXLeft())
                    return -1;
                else if(o1.getXLeft() > o2.getXLeft())
                    return 1;
                if(o1.getYFront() < o2.getYFront())
                    return -1;
                else if(o1.getYFront() > o2.getYFront())
                    return 1;
                return 0;
            }
        });

        return feasibleFreeSpaces.get(0);
    }

    public Cuboid randomlyChooseHorizontalOrientation(PositionedRectangle pr, Cuboid originalOrientation){
        ArrayList<Cuboid> feasibleOrientations = new ArrayList<>();
        if(originalOrientation.getWidth() <= pr.getWidth() && originalOrientation.getDepth() <= pr.getDepth()){
            Cuboid tempOrientation = originalOrientation.rotate(0,0,0);
            feasibleOrientations.add(tempOrientation);
        }
        if(originalOrientation.getWidth() <= pr.getDepth() && originalOrientation.getDepth() <= pr.getWidth()){
            Cuboid tempOrientation = originalOrientation.rotate(0,0,90);
            feasibleOrientations.add(tempOrientation);
        }

        int randint = (int)(Math.random()*feasibleOrientations.size());
        Cuboid chosenOrientation = feasibleOrientations.get(randint);
        return chosenOrientation;
    }


}
