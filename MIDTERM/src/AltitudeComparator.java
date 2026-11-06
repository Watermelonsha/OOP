import java.util.Comparator;

public class AltitudeComparator implements Comparator<Drone> {
    @Override
    public int compare(Drone a, Drone b) {
        int result = Double.compare(b.getMaxAltitude(), a.getMaxAltitude());
        if (result != 0) {
            return result;
        }
        return a.getModel().compareTo(b.getModel());
    }
}
