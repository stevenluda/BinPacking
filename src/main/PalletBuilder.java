package main;

import main.PackingObjects.Box;
import main.PackingObjects.Cuboid;
import main.PackingObjects.FreeSpace3D;
import main.PackingObjects.Pallet;
import main.State.LayerState;
import main.utils.FreeSpaceComparator;
import main.utils.PackingConfigurationsSingleton;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import ilog.concert.*;
import ilog.cplex.*;

public class PalletBuilder {
    Map<String, Box> boxesToPack;
    Box box = null;
    LayerBuilder layerBuilder = new LayerBuilder();

    public PalletBuilder(Map<String, Box> boxes)
    {
        boxesToPack = boxes;
        layerBuilder.updateBoxesToPack(boxesToPack);
        //TODO: create pallet dimension object

    }

    //This method builds pallet with a greedy heuristic.
    //Every iteration, it finds a cluster of same height boxes, builds best possible layer within a given number of random shuffles
    public List<Pallet> buildPalletsGreedy(){
        BoxCluster boxCluster = new BoxCluster();
        LayerBuilder layerBuilder = new LayerBuilder();
        List<LayerState> layers = new ArrayList<>();
        while(boxesToPack.size() > 0){
            Map<String, Box> maxCluster = boxCluster.getMaxSizeCluster(boxesToPack);
            LayerState layer = layerBuilder.generateBestLayer(maxCluster.values().stream().collect(Collectors.toList()), 40000);
            layers.add(layer);
            boxesToPack.keySet().removeAll(layer.getBoxIds());
        }

        return null;

    }

    //TODO change the output to be a list of pallets
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
            LayerBuilder layerBuilder = new LayerBuilder();
            layerBuilder.updateBoxesToPack(boxesToPack);
            Map<Integer, List<LayerState>> layersGroupByHeight = layerBuilder.generateLayers(40000, 0.2);
            //solve a set covering problem to cover all boxes with the generated layers using the minimum number of layers
            List<LayerState> selectedLayers = solveSetCovering(layersGroupByHeight);
            //solve a 1-D bin packing problem with layer heights subject to pallet height constraint
            //List<Pallet> pallets = solveOneDimBinPacking(selectedLayers);

            /*ArrayList<LayerState> layers = new ArrayList<>();
            while(!boxesToPack.isEmpty()){
                LayerState layer = layerBuilder.getBestLayer(100000);
                //collect all constructed layers first,
                if(layer != null){
                    layers.add(layer);
                    boxesToPack.keySet().removeAll(layer.getPackedBoxes().stream().map(box1 -> box1.getId()).collect(Collectors.toList()));
                }
            }*/
            //then determine the placing sequence of layers
            //Option 1: rank layers by density, i.e. covered area
            selectedLayers.sort(new Comparator<LayerState>() {
                @Override
                public int compare(LayerState o1, LayerState o2) {
                    if(o1.getTotalUsedArea() > o2.getTotalUsedArea())
                        return -1;
                    else if(o1.getTotalUsedArea() < o2.getTotalUsedArea())
                        return 1;
                    return 0;
                }
            });

            writeLayers(selectedLayers);

            /*for(int i = 0, h = 0, totalWeight = 0; i < layers.size(); i++){
                LayerState layer = layers.get(i);
                if(h + layer.getLayerHeight() < Integer.parseInt(PackingConfigurationsSingleton.getProperty("height"))
                    && totalWeight + layer.getLayerHeight() < Integer.parseInt(PackingConfigurationsSingleton.getProperty("capacity"))
                ){
                    pallet.placeLayer(layer, h);
                    h += layer.getLayerHeight();
                    totalWeight += layer.getTotalWeight();
                }
            }*/

            //TODO option 2, shuffle layers and then stack, choose the best one with maximum density in 3D.


        }

        return pallet;
    }

    private Box findPlacement(Pallet pallet){
        for(Box box: boxesToPack.values()){
            ArrayList<FreeSpace3D> fss = pallet.getFeasibleFreeSpaces(box);
            if(!fss.isEmpty()){
                //add comparator for sorting free spaces
                fss.sort(new FreeSpaceComparator());
                //TODO: currently only placing at front bottom left point, add attemps for placing at other three points
                //TODO: currently returns when a first feasible box placement is found, add comparison of multiple feasible placements
                FreeSpace3D fs = fss.get(0);
                box.setPosition(fs.getPosition());
                //randomly finds a box orientation
                ArrayList<Cuboid> feasibleOrientations = new ArrayList<>();
                Cuboid tempOrientation = box.rotate(0,0,0);
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
                Cuboid chosenOrientation = feasibleOrientations.get(randint);
                box.setDims(chosenOrientation);
                return box;
            }
        }
        return null;
    }


    private List<LayerState> solveSetCovering(Map<Integer, List<LayerState>> layersGroupByHeight) throws IloException {
        List<LayerState> result = new ArrayList<>();
        try {
            IloCplex setCoveringSolver = new IloCplex();
            //define objective
            IloObjective layersUsed = setCoveringSolver.addMinimize();
            //define constraints
            Map<String, IloRange> covers = new HashMap<>();

            for(String boxId: boxesToPack.keySet()){
                covers.put(boxId, setCoveringSolver.addRange(1, 1, boxId));
            }
            for(String boxId: boxesToPack.keySet()){
                setCoveringSolver.intVar(setCoveringSolver.column(layersUsed, 1.0).and(setCoveringSolver.column(covers.get(boxId), 1)), 0, 1, boxId);
            }
            //define variables
            Map<String, IloIntVar> vars = new HashMap<>();
            //create model
            for(Map.Entry<Integer, List<LayerState>> pair: layersGroupByHeight.entrySet()){
                List<LayerState> layerList = pair.getValue();
                Integer h = pair.getKey();
                for(int i = 0; i < layerList.size(); i++){
                    LayerState layer = layerList.get(i);
                    //create a column
                    IloColumn col = setCoveringSolver.column(layersUsed, 1.0);
                    for(String boxId: layer.getBoxIds()){
                        col = col.and(setCoveringSolver.column(covers.get(boxId), 1));
                    }
                    String varName = "H"+h+"Id"+i;
                    vars.put(varName, setCoveringSolver.intVar(col, 0, 1, varName));
                }
            }
            //solve
            setCoveringSolver.setParam(	IloCplex.Param.TimeLimit, 3600);
            setCoveringSolver.solve();
            System.out.println("Solution status: " + setCoveringSolver.getStatus());

            for(Map.Entry<String, IloIntVar> pair: vars.entrySet()){
                IloIntVar var = pair.getValue();
                String varName = pair.getKey();
                double value = setCoveringSolver.getValue(var);
                if(value >= 0.9999){
                    List<String> digits = Arrays.asList(varName.replaceAll("[^-?0-9]+", " ").trim().split(" "));
                    Integer clusterHeight = Integer.parseInt(digits.get(0));
                    Integer layerIndex = Integer.parseInt(digits.get(1));
                    result.add(layersGroupByHeight.get(clusterHeight).get(layerIndex));
                }
            }
            setCoveringSolver.end();
        }
        catch ( IloException exc ) {
            System.err.println("Concert exception '" + exc + "' caught");
        }
        Map<String, Integer> boxIds = new HashMap<>();
        for(LayerState layer:result){
            for(String boxId: layer.getBoxIds()){
                    Integer count = boxIds.containsKey(boxId)?boxIds.get(boxId):0;
                    boxIds.put(boxId, count+1);
            }
        }
        Map<String, Integer> overCovered= boxIds.entrySet().stream().filter(entry -> entry.getValue() > 1).collect(Collectors.toMap(entry->entry.getKey(), entry->entry.getValue()));
        List<String> unCovered = new ArrayList<>();
        for(String boxId: boxesToPack.keySet()){
            if(!boxIds.containsKey(boxId)){
                unCovered.add(boxId);
            }
        }
        return result;
    }

    private List<Pallet> solveOneDimBinPacking(List<LayerState> selectedLayers){
        return null;
    }

    public void writeLayers(List<LayerState> layers) throws IOException {
        int i = 0;
        for(LayerState layer: layers){
            FileWriter fileWriter = new FileWriter("test\\layer"+i+".csv");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(layer.toString2D());
            printWriter.close();
            i++;
        }
    }
}
