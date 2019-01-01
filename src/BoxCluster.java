import PackingObjects.Box;
import PackingObjects.Cuboid;
import utils.InputReader;
import utils.OutputWriter;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

//This class finds a subset of boxes with the same height such that their total bottom areas are maximal
public class BoxCluster {
    public BoxCluster(){

    }

    public List<Box> findSameHeightBoxes(Map<String, Box> boxesToPack){
        return findSameHeightBoxes(boxesToPack, null);
    }

    public List<Box> findSameHeightBoxes(Map<String, Box> boxesToPack, Integer targetHeight){
        Map<Integer, List<Box>> boxClusterByHeight = getClusters(boxesToPack);

        Map<Integer, Integer> areaByHeight = boxClusterByHeight.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e ->e.getValue().stream().mapToInt(box -> box.getBottomArea()).sum()));
        Integer bestDimensionValue = targetHeight == null? Collections.max(areaByHeight.entrySet(), Comparator.comparingInt(Entry::getValue)).getKey(): targetHeight;

        //Return these boxes having this dimension value
        List<Box> boxList = boxClusterByHeight.get(bestDimensionValue);
        //boxOrientationList.forEach(b -> b.getBox().resetOrientation(b.getOrientation()));
        return boxList;
    }

    public Map<Integer, List<Box>> getClusters(Map<String, Box> boxesMap){
        //First find the unique dimension values for each box
        //For each unique dimension value, calculate the area of the other two dimensions
        List<Box> rotatedDuplicates = new ArrayList<>();
        for(Box box: boxesMap.values()){
            rotatedDuplicates.add(new Box(box, new Cuboid(box.getHeight(), box.getDepth(), box.getWidth())));
            if(box.getDepth() != box.getWidth()){
                rotatedDuplicates.add(new Box(box, new Cuboid(box.getWidth(), box.getHeight(), box.getDepth())));
            }
            if(box.getHeight() != box.getWidth() && box.getHeight() != box.getDepth()){
                rotatedDuplicates.add(new Box(box, new Cuboid(box.getWidth(), box.getDepth(), box.getHeight())));
            }
        }

        //Find the unique dimension value with the maximal total area
        Map<Integer, List<Box>> boxClusterByHeight= rotatedDuplicates.stream().collect(groupingBy(b -> b.getHeight()));
        return boxClusterByHeight;
    }

    public static void main(String[] args) throws IOException {
        InputReader reader = new InputReader();
        Map<String, Box> unpackedBoxes = reader.readData("src\\resources\\test_instance.txt");
        BoxCluster boxCluster = new BoxCluster();
        List<Box> sameHeightBoxes = boxCluster.findSameHeightBoxes(unpackedBoxes, 400);
        OutputWriter outputWriter = new OutputWriter();
        outputWriter.OutputBoxes(sameHeightBoxes, "SpaceDefragmentation_2Dtest.txt", false, true, true, false, false);
    }

}
