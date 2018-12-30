package PackingObjects;

import java.util.List;

public interface PackingOperations<T, K> {

    public List<T> segmentSpace(K object);
    public boolean accomodate(K object);
}
