import java.util.Comparator;

public class FlightTimeComparator implements Comparator<Drone> {
    @Override
    public int compare(Drone a, Drone b) {
        return Double.compare(a.calculateFlightTime(), b.calculateFlightTime());
    }
}
