package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDatabase<T> {
    private Map<String, T> db = new HashMap<>();

    public boolean exist(String id) {
        return db.containsKey(id);
    }

    public T get(String id) {
        return db.get(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(db.values());
    }

    public void add(String id, T value) {
        db.put(id, value);
    }

    public boolean remove(String id) {
        return db.remove(id) != null;
    }

    public void clear() {db.clear();}
}
