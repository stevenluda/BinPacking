package utils;

import java.util.EventListener;
import PlacementObjects.Surface;

public interface SurfaceEventListener extends EventListener {
    public void OnSurfaceCovered(Surface eventSource);
}
