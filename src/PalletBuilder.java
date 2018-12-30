import PackingObjects.Box;
import PackingObjects.FreeSpace3D;
import PackingObjects.Pallet;
import PlacementObjects.Vector3D;
import State.LayerState;
import utils.FreeSpaceComparator;
import utils.PackingConfigurationsSingleton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PalletBuilder {
    List<Box> boxesToPack;
    Box box = null;
    LayerBuilder layerBuilder = new LayerBuilder();

    public PalletBuilder(List<Box> boxes)
    {
        boxesToPack = boxes;
        layerBuilder.updateBoxesToPack(boxesToPack);
        //TODO: create pallet dimension object

    }

    public Pallet buildPallet(boolean buildByLayer) throws Exception {
        Pallet pallet = new Pallet("Pallet", Integer.parseInt(PackingConfigurationsSingleton.getProperty("width")),
                Integer.parseInt(PackingConfigurationsSingleton.getProperty("depth")),
                Integer.parseInt(PackingConfigurationsSingleton.getProperty("height")),
                Integer.parseInt(PackingConfigurationsSingleton.getProperty("capacity")));// while still packable
        if(!buildByLayer){
            //TODO: finish while loop to pack boxes
            box = findPlacement(pallet);
            while(box != null)
            {
                pallet.placeBox(box);
                boxesToPack.remove(box);
                box = findPlacement(pallet);
            }
        }

        if(buildByLayer){
            //TODO how to avoid multiple boxToPack in many classes
            //TODO how to build multiple layers
            LayerBuilder layerBuilder = new LayerBuilder();
            layerBuilder.updateBoxesToPack(boxesToPack);
            ArrayList<LayerState> layers = new ArrayList<>();
            while(!boxesToPack.isEmpty()){
                LayerState layer = layerBuilder.constructLayer(10000);
                //collect all constructed layers first,
                if(layer != null){
                    layers.add(layer);
                    boxesToPack.removeAll(layer.getPackedBoxes());
                }
            }
            //then determine the placing sequence of layers
            //Option 1: rank layers by density, i.e. covered area
            layers.sort(new Comparator<LayerState>() {
                @Override
                public int compare(LayerState o1, LayerState o2) {
                    if(o1.getTotalUsedArea() > o2.getTotalUsedArea())
                        return -1;
                    else if(o1.getTotalUsedArea() < o2.getTotalUsedArea())
                        return 1;
                    return 0;
                }
            });

            for(int i = 0, h = 0, totalWeight = 0; i < layers.size(); i++){
                LayerState layer = layers.get(i);
                if(h + layer.getLayerHeight() < Integer.parseInt(PackingConfigurationsSingleton.getProperty("height"))
                    && totalWeight + layer.getLayerHeight() < Integer.parseInt(PackingConfigurationsSingleton.getProperty("capacity"))
                ){
                    pallet.placeLayer(layer, h);
                    h += layer.getLayerHeight();
                    totalWeight += layer.getTotalWeight();
                }
            }

            //TODO option 2, shuffle layers and then stack, choose the best one with maximum density in 3D.


        }

        return pallet;
    }

    private Box findPlacement(Pallet pallet){
        for(Box box: boxesToPack){
            ArrayList<FreeSpace3D> fss = pallet.getFeasibleFreeSpaces(box);
            if(!fss.isEmpty()){
                //add comparator for sorting free spaces
                fss.sort(new FreeSpaceComparator());
                //TODO: currently only placing at front bottom left point, add attemps for placing at other three points
                //TODO: currently returns when a first feasible box placement is found, add comparison of multiple feasible placements
                FreeSpace3D fs = fss.get(0);
                box.setPosition(fs.getPosition());
                //randomly finds a box orientation
                ArrayList<Vector3D> feasibleOrientations = new ArrayList<>();
                Vector3D tempOrientation = box.rotate(0,0,0);
                if(fs.dimensionFits(tempOrientation))
                    feasibleOrientations.add(tempOrientation);
                tempOrientation = box.rotate(0,0,90);
                if(fs.dimensionFits(tempOrientation))
                    feasibleOrientations.add(tempOrientation);
                tempOrientation = box.rotate(0,90,0);
                if(fs.dimensionFits(tempOrientation))
                    feasibleOrientations.add(tempOrientation);
                tempOrientation = box.rotate(0,90,90);
                if(fs.dimensionFits(tempOrientation))
                    feasibleOrientations.add(tempOrientation);
                tempOrientation = box.rotate(90,0,0);
                if(fs.dimensionFits(tempOrientation))
                    feasibleOrientations.add(tempOrientation);
                tempOrientation = box.rotate(90,0,90);
                if(fs.dimensionFits(tempOrientation))
                    feasibleOrientations.add(tempOrientation);
                int randint = (int)(Math.random()*feasibleOrientations.size());
                /*if(box.getId().equals("267984"))
                    randint = 3;
                if(box.getId().equals("1501402"))
                    randint = 4;*/
                Vector3D chosenOrientation = feasibleOrientations.get(randint);
                box.resetOrientation(chosenOrientation);
                return box;
            }
        }
        return null;
    }

}
