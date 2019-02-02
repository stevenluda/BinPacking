package main.State;

import main.PackingObjects.Box;
import main.PackingObjects.Cuboid;
import main.PlacementObjects.Placement;
import main.PlacementObjects.PositionedRectangle;
import main.PlacementObjects.Rectangle;
import main.PlacementObjects.Vector3D;
import main.utils.PackingConfigurationsSingleton;

import java.util.*;
import java.util.stream.Collectors;

public class LayerState extends State {
    HashMap<String, Placement> placements;
    int numberOfBoxes;
    int layerHeight;
    int totalWeight;
    int totalUsedArea;
    int totalFreeArea;
    Set<String> boxIds = new HashSet<>();
    ArrayList<PositionedRectangle> freespaces = new ArrayList<PositionedRectangle>();

    public LayerState(){
        freespaces.add(new PositionedRectangle(Integer.parseInt(PackingConfigurationsSingleton.getProperty("width")),
                Integer.parseInt(PackingConfigurationsSingleton.getProperty("depth"))));
        numberOfBoxes = 0;
        layerHeight = 0;
        totalWeight = 0;
        totalUsedArea = 0;
        totalFreeArea = freespaces.get(0).getArea();
        placements = new HashMap<>();
    }

    public int getTotalUsedArea() {
        return totalUsedArea;
    }

    public int getTotalFreeArea() {
        return totalFreeArea;
    }

    public ArrayList<Box> getPackedBoxes() {
        ArrayList<Box> boxes = new ArrayList<>();
        for(Placement p: placements.values()){
            boxes.add(p.getBox());
        }
        return boxes;
    }

    public int getNumberOfBoxes() {
        return numberOfBoxes;
    }

    public Set<String> getBoxIds() {
        return boxIds;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public int getLayerHeight() {
        return layerHeight;
    }

    public void setLayerHeight(int layerHeight) {
        this.layerHeight = layerHeight;
    }

    public void updateState(Box box, Vector3D position, Cuboid cuboid){
        placements.put(box.getId(), new Placement(box, position, cuboid));
        boxIds.add(box.getId());
        numberOfBoxes++;
        totalWeight += box.getWeight();
        totalUsedArea += cuboid.getBottomArea();
        totalFreeArea -= cuboid.getBottomArea();
        updateFreeSpaces(new PositionedRectangle(cuboid.getWidth(), cuboid.getDepth(), position));
        if(box.getHeight() > getLayerHeight()){
            setLayerHeight(box.getHeight());
        }
    }

    public void updateFreeSpaces(PositionedRectangle rectangle){
        //segment each free spaces if necessary
        ArrayList<PositionedRectangle> freeSpacesToAdd = new ArrayList<>();
        ArrayList<PositionedRectangle> freeSpacesToRemove = new ArrayList<>();
        for(PositionedRectangle fs: freespaces)
        {
            if(fs.isOverlapping(rectangle)){
                freeSpacesToRemove.add(fs);
                List<PositionedRectangle> result = fs.segmentSpace(rectangle);
                if(!result.isEmpty())
                {
                    ArrayList<PositionedRectangle> enclosedFreeSpacesToAdd = new ArrayList<>();
                    ArrayList<PositionedRectangle> enclosedFreeSpacesInResult = new ArrayList<>();
                    for(PositionedRectangle fs_to_add: freeSpacesToAdd){
                        for(PositionedRectangle fs_of_result: result){
                            if(fs_to_add.enclose(fs_of_result)){
                                enclosedFreeSpacesInResult.add(fs_of_result);
                            }
                            else{
                                if(fs_of_result.enclose(fs_to_add)){
                                    enclosedFreeSpacesToAdd.add(fs_to_add);
                                }
                            }
                        }
                    }
                    freeSpacesToAdd.removeAll(enclosedFreeSpacesToAdd);
                    result.removeAll(enclosedFreeSpacesInResult);
                    freeSpacesToAdd.addAll(result);
                }
            }
        }

        freespaces.removeAll(freeSpacesToRemove);
        //final check on freeSpacesToAdd, remove any enclosed ones: this may happen even the added box does not overlap with a freespace
        ArrayList<PositionedRectangle> enclosedFreeSpacesToAdd = new ArrayList<>();
        for(PositionedRectangle fs: freespaces)
        {
            for(PositionedRectangle fs_to_add: freeSpacesToAdd){
                if(fs.enclose(fs_to_add)){
                    enclosedFreeSpacesToAdd.add(fs_to_add);
                }
            }
        }
        freeSpacesToAdd.removeAll(enclosedFreeSpacesToAdd);
        freespaces.addAll(freeSpacesToAdd);
    }

    public ArrayList<PositionedRectangle> getFeasibleFreeSpaces(Rectangle boxBottom){
        ArrayList<PositionedRectangle> feasible2DFreeSpaces = new ArrayList<>();
        for(PositionedRectangle fs:freespaces){
            if(fs.accomodate(boxBottom))
                feasible2DFreeSpaces.add(fs);
        }
        return feasible2DFreeSpaces;
    }


    public HashMap<String, Placement> getPlacements() {
        return placements;
    }

    public String toString2D(){
        String result = "";
        for(Placement p: placements.values()){
            result += p.getBox().getId()+","+p.getOrientation().getWidth()+","+p.getOrientation().getDepth()+","+p.getOrientation().getHeight()+","+p.getPosition().getX()+","+p.getPosition().getY()+"\r\n";
        }
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(!(obj instanceof LayerState))
            return false;
        if(obj == this) return true;
        if(this.getLayerHeight() != ((LayerState)obj).getLayerHeight())
            return false;
        if(this.getNumberOfBoxes() != ((LayerState)obj).getNumberOfBoxes())
            return false;
        if(this.getTotalUsedArea() != ((LayerState)obj).getTotalUsedArea())
            return false;
        if(this.getTotalWeight() != ((LayerState)obj).getTotalWeight())
            return false;
        if(this.freespaces.size() != ((LayerState)obj).freespaces.size())
            return false;
        if(!this.getBoxIds().equals(((LayerState)obj).getBoxIds()))
            return false;
        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.getLayerHeight(), this.getNumberOfBoxes(), this.getTotalUsedArea(),
                                       this.getTotalWeight(), this.freespaces.size(),
                                        Arrays.hashCode(this.getBoxIds().toArray()));
    }

    public void removeBoxes(List<String> boxIds) {
        this.boxIds.removeAll(boxIds);
        placements.keySet().removeAll(boxIds);
        numberOfBoxes -= boxIds.size();
        //TODO: Since the layer is corrupted, don't use other information except boxIds and placements, may need an update to the free space, totalAreaUsed, totalWeight
    }
}
