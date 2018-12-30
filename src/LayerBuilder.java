import PackingObjects.Box;
import PlacementObjects.Vector3D;
import PlacementObjects.PositionedRectangle;
import State.LayerState;

import java.util.*;

public class LayerBuilder {
    List<Box> boxesToPack = null;
    BoxCluster cluster = new BoxCluster();
    public LayerBuilder(){

    }

    public void updateBoxesToPack(List<Box> boxesToPack){
        this.boxesToPack = boxesToPack;
    }

    public LayerState constructLayer(int nbShuffles){
        List<Box> sameHeightBoxes = cluster.findSameHeightBoxes(boxesToPack);
        Map<String, Box> sameHeightBoxesMap = new HashMap<>();
        ArrayList<String> sameHeightBoxesIds = new ArrayList<>();
        for(Box b: sameHeightBoxes){
            sameHeightBoxesMap.put(b.getId(), b);
            sameHeightBoxesIds.add(b.getId());
        }

        //How about randomly align boxes from left front corner to right corner
        LayerState bestState = new LayerState();
        for(int i = 0; i < nbShuffles; i++){
            ArrayList<String> shuffledSequence = new ArrayList<>(sameHeightBoxesIds);
            Collections.shuffle(shuffledSequence);
            LayerState state = new LayerState();
            Box box = sameHeightBoxesMap.get(shuffledSequence.get(0));
            PositionedRectangle p = findPlacement(box, state);
            while(!shuffledSequence.isEmpty()&& p!=null){
                Vector3D orientation = randomlyChooseAnOrientation(p, box);
                state.updateState(box, p.getPosition(), orientation);
                shuffledSequence.remove(box.getId());
                for(String id: shuffledSequence){
                    box = sameHeightBoxesMap.get(id);
                    p = findPlacement(box, state);
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

    public PositionedRectangle findPlacement(Box box, LayerState state){
        ArrayList<PositionedRectangle> feasibleFreeSpaces = state.getFeasibleFreeSpaces(box);
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

    public Vector3D randomlyChooseAnOrientation(PositionedRectangle pr, Box box){
        ArrayList<Vector3D> feasibleOrientations = new ArrayList<>();
        if(box.getWidth() <= pr.getWidth() && box.getDepth() <= pr.getDepth()){
            Vector3D tempOrientation = box.rotate(0,0,0);
            feasibleOrientations.add(tempOrientation);
        }
        if(box.getWidth() <= pr.getDepth() && box.getDepth() <= pr.getWidth()){
            Vector3D tempOrientation = box.rotate(0,0,90);
            feasibleOrientations.add(tempOrientation);
        }

        int randint = (int)(Math.random()*feasibleOrientations.size());
        Vector3D chosenOrientation = feasibleOrientations.get(randint);
        return chosenOrientation;
    }


}
