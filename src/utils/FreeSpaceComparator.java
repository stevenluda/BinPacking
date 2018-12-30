package utils;

import PackingObjects.FreeSpace3D;

import java.util.Comparator;

public class FreeSpaceComparator implements Comparator<FreeSpace3D> {
    @Override
    public int compare(FreeSpace3D o1, FreeSpace3D o2) {
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
