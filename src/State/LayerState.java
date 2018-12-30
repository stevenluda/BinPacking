package State;

import PackingObjects.Box;
import PlacementObjects.Placement;
import PlacementObjects.PositionedRectangle;
import PlacementObjects.Vector3D;
import utils.PackingConfigurationsSingleton;

import java.util.ArrayList;
import java.util.List;

public class LayerState extends State {
    ArrayList<Placement> placements;
    int totalWeight;
    int totalUsedArea;
    ArrayList<PositionedRectangle> freespaces = new ArrayList<PositionedRectangle>();

    public LayerState(){
        freespaces.add(new PositionedRectangle(Integer.parseInt(PackingConfigurationsSingleton.getProperty("width")),
                Integer.parseInt(PackingConfigurationsSingleton.getProperty("depth"))));
        totalWeight = 0;
        totalUsedArea = 0;
        placements = new ArrayList<>();
    }

    public int getTotalUsedArea() {
        return totalUsedArea;
    }

    public ArrayList<Box> getPackedBoxes() {
        ArrayList<Box> boxes = new ArrayList<>();
        for(Placement p: placements){
            boxes.add(p.getBox());
        }
        return boxes;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void updateState(Box box, Vector3D position, Vector3D orientation){
        placements.add(new Placement(box, position, orientation));
        totalWeight += box.getWeight();
        totalUsedArea += box.getBottom().getArea();
        updateFreeSpaces(new PositionedRectangle(orientation.getX(), orientation.getY(), position));
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

    public ArrayList<PositionedRectangle> getFeasibleFreeSpaces(Box box){
        ArrayList<PositionedRectangle> feasible2DFreeSpaces = new ArrayList<>();
        for(PositionedRectangle fs:freespaces){
            if(fs.accomodate(box.getBottom()))
                feasible2DFreeSpaces.add(fs);
        }
        return feasible2DFreeSpaces;
    }


    public ArrayList<Placement> getPlacements() {
        return placements;
    }
}
