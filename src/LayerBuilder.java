import PackingObjects.Box;
import PackingObjects.Cuboid;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Rectangle;
import State.LayerState;

import java.util.*;
import java.util.stream.Collectors;

public class LayerBuilder {
    Map<String, Box> boxesToPack = null;
    BoxCluster cluster = new BoxCluster();
    public LayerBuilder(){

    }

    public void updateBoxesToPack(Map<String, Box> boxesToPack){
        this.boxesToPack = boxesToPack;
    }

    public LayerState getBestLayer(int nbShuffles){
        List<Box> sameHeightBoxes = cluster.findSameHeightBoxes(boxesToPack);

        LayerState bestState = constructLayers(sameHeightBoxes, nbShuffles, true).iterator().next();
        if(bestState.getTotalUsedArea() == 0)
            return null;
        else
            return bestState;
    }
    public Set<LayerState> constructLayers(List<Box> sameHeightBoxes, int nbShuffles, boolean needBest){
        Map<String, Box> sameHeightBoxesMap = new HashMap<>();
        ArrayList<String> sameHeightBoxesIds = new ArrayList<>();
        for(Box box: sameHeightBoxes){
            sameHeightBoxesMap.put(box.getId(), box);
            sameHeightBoxesIds.add(box.getId());
        }
        Set<LayerState> layers = new HashSet<>();
        if(needBest)
            layers.add(new LayerState());
        for(int i = 0; i < nbShuffles; i++){
            ArrayList<String> shuffledSequence = new ArrayList<>(sameHeightBoxesIds);
            Collections.shuffle(shuffledSequence);
            LayerState state = new LayerState();
            Box box = sameHeightBoxesMap.get(shuffledSequence.get(0));
            state.setLayerHeight(box.getHeight());
            PositionedRectangle p = findPlacementPosition(box.getBottomRectangle(), state);
            while(!shuffledSequence.isEmpty()&& p!=null){
                Cuboid new_dims = randomlyChooseHorizontalOrientation(p, box.getDims());
                state.updateState(box, p.getPosition(), new_dims);
                shuffledSequence.remove(box.getId());
                for(String id: shuffledSequence){
                    box = sameHeightBoxesMap.get(id);
                    p = findPlacementPosition(box.getBottomRectangle(), state);
                    if(p!=null)
                        break;
                }
            }
            if(needBest){
                if(layers.iterator().next().getTotalUsedArea() < state.getTotalUsedArea()) {
                    layers.clear();
                    layers.add(state);
                }
            }else{
                //check if the layer is same to any layer generated before
                layers.add(state);
            }
        }
        return layers;
    }

    public List<LayerState> generateLayers(List<Box> sameHeightBoxes, int nbShuffles){
        Set<LayerState> layers = constructLayers(sameHeightBoxes, nbShuffles, false);
        return layers.stream().sorted(Comparator.comparing(LayerState::getTotalUsedArea).reversed()).collect(Collectors.toList());
    }

    public Map<Integer, List<LayerState>> generateLayers(int nbShuffles){
        Map<Integer, List<Box>> boxClusters = cluster.getClusters(boxesToPack);
        Map<Integer,List<LayerState>> layersByHeight = boxClusters.entrySet().stream()
                .collect(Collectors.toMap(cluster->cluster.getKey(), cluster ->generateLayers(cluster.getValue(), nbShuffles)));
        return layersByHeight;
    }

    public PositionedRectangle findPlacementPosition(Rectangle boxBottom, LayerState state){
        ArrayList<PositionedRectangle> feasibleFreeSpaces = state.getFeasibleFreeSpaces(boxBottom);
        if(feasibleFreeSpaces.isEmpty())
            return null;
        //This comparator gives priority to position towards left front of the plane
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
