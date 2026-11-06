import java.util.Comparator;

public class BatteryComparator implements Comparator<Drone> {
    @Override
    public int compare(Drone a, Drone b) {
        return Double.compare(a.getBatteryLevel(), b.getBatteryLevel());
    }
}
