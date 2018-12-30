import PackingObjects.Box;
import PackingObjects.FreeSpace3D;
import PackingObjects.Pallet;
import PlacementObjects.Vector3D;
import State.LayerState;
import utils.FreeSpaceComparator;
import utils.PackingConfigurationsSingleton;

import java.util.ArrayList;

public class PalletBuilder {
    ArrayList<Box> boxesToPack;
    Box box = null;
    LayerBuilder layerBuilder = new LayerBuilder();

    public PalletBuilder(ArrayList<Box> boxes)
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
            while(true){
                LayerState layer = layerBuilder.constructLayer(10000);
                if(layer != null){
                    pallet.placeLayer(layer);
                    boxesToPack.removeAll(layer.getPackedBoxes());
                }else{
                    break;
                }
            }


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
