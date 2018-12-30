import PackingObjects.Box;
import PlacementObjects.Vector3D;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

//This class finds a subset of boxes with the same height such that their total bottom areas are maximal
public class BoxCluster {
    public BoxCluster(){

    }

    private class BoxOrientation{
        Box box;
        int height;
        int area;
        Vector3D dims;
        public BoxOrientation(Box box, int height, int area, Vector3D dimsValues){
            this.box = box;
            this.height = height;
            this.area = area;
            this.dims = dimsValues;
        }
    }

    public List<Box> findSameHeightBoxes(List<Box> boxesToPack){
        //First find the unique dimension values for each box
        //For each unique dimension value, calculate the area of the other two dimensions
        List<BoxOrientation> boxOrientations = new ArrayList<>();
        for(Box box: boxesToPack){
            boxOrientations.add(new BoxOrientation(box, box.getWidth(), box.getDepth()*box.getHeight(), new Vector3D(box.getHeight(), box.getDepth(), box.getWidth())));
            if(box.getDepth() != box.getWidth()){
                boxOrientations.add(new BoxOrientation(box, box.getDepth(), box.getWidth()*box.getHeight(), new Vector3D(box.getWidth(), box.getHeight(), box.getDepth())));
            }
            if(box.getHeight() != box.getWidth() && box.getHeight() != box.getDepth()){
                boxOrientations.add(new BoxOrientation(box, box.getHeight(), box.getWidth()*box.getDepth(), new Vector3D(box.getWidth(), box.getDepth(), box.getHeight())));
            }
        }

        //Find the unique dimension value with the maximal total area
        Map<Integer, List<BoxOrientation>>  boxByHeight= boxOrientations.stream().collect(groupingBy(b -> b.height));
        Map<Integer, Integer> areaByHeight = boxOrientations.stream().collect(groupingBy(b->b.height, summingInt(b->b.area)));
        Integer bestDimensionValue = Collections.max(areaByHeight.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

        //Return these boxes having this dimension value
        List<BoxOrientation> boxOrientationList = boxByHeight.get(bestDimensionValue);
        boxOrientationList.forEach(b -> b.box.resetOrientation(b.dims));
        List<Box> bestHeightBoxes = boxOrientationList.stream().map(b -> b.box).collect(Collectors.toList());
        return bestHeightBoxes;
    }

}
