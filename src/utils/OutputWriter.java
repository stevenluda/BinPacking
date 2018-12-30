package utils;

import PackingObjects.Box;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class OutputWriter {
    public void OutputBoxes(List<Box> boxes, String output_filename, boolean writeBoxId, boolean writeWidth, boolean writeDepth, boolean writeHeight, boolean writePosition) throws IOException {
        FileWriter fileWriter = new FileWriter("src\\resources\\"+output_filename);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(Box box: boxes){
            if(writeBoxId)
                printWriter.printf("%10s", box.getId());
            if(writeWidth)
                printWriter.printf("%5d", box.getWidth());
            if(writeDepth)
                printWriter.printf("%5d", box.getDepth());
            if(writeHeight)
                printWriter.printf("%5d", box.getHeight());
            if(writePosition)
                printWriter.printf("%5d%5d%5d", box.getXLeft(), box.getYFront(), box.getZBottom());
            printWriter.print("\r\n");
        }
        printWriter.close();
    }
}
