import java.util.ArrayList;
import java.util.List;

public class Pilot {

    private int id;
    private String name;
    private List<Drone> drones = new ArrayList<>();

    public Pilot(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Drone> getDrones() {
        return drones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pilot other = (Pilot) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "пилот " + name + ", дронов: " + drones.size();
    }
}
