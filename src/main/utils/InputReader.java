package main.utils;

import main.PackingObjects.Box;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InputReader {

    public Map<String, Box> readData(String filename)
    {
        BufferedReader br = null;
        String line = "";
        String splitBy = "\t";
        Map<String, Box> boxes = new HashMap<>();
        try {
            br = new BufferedReader(new FileReader(filename));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(splitBy);
                String id = attributes[0];
                int depth = Integer.parseInt(attributes[1]);
                int width = Integer.parseInt(attributes[2]);
                int height = Integer.parseInt(attributes[3]);
                int weight = Integer.parseInt(attributes[4]);
                int loadCapacity = Integer.parseInt(attributes[5]);
                String supportType = attributes[6];
                boxes.put(id, new Box(id, width, depth, height, weight, null));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return boxes;
    }
}
