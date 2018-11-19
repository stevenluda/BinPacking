package utils;

import PackingObjects.FreeSpace;

import java.util.Comparator;

public class FreeSpaceComparator implements Comparator<FreeSpace> {
    @Override
    public int compare(FreeSpace o1, FreeSpace o2) {
        if(o1.getZBottom() < o2.getZBottom())
            return -1;
        else if(o1.getZBottom() > o2.getZBottom())
            return 1;

        if(o1.getSupportingArea()< o2.getSupportingArea())
            return 1;
        else if(o1.getSupportingArea() > o2.getSupportingArea())
            return -1;

        if(o1.getSupportingSurfaceCount() < o2.getSupportingSurfaceCount())
            return -1;
        else if(o1.getSupportingSurfaceCount() > o2.getSupportingSurfaceCount())
            return 1;

        return 0;
    }
}
