public abstract class Drone implements Comparable<Drone> {

    public static final String CAMERA_TYPE = "камера-дрон";
    public static final String DELIVERY_TYPE = "дрон-доставщик";

    private int id;
    private String model;
    protected double batteryLevel;
    private double maxAltitude;

    public Drone(int id, String model, double batteryLevel, double maxAltitude) {
        this.id = id;
        this.model = model;
        setBatteryLevel(batteryLevel);
        setMaxAltitude(maxAltitude);
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        if (batteryLevel < 0) {
            this.batteryLevel = 0;
        } else if (batteryLevel > 100) {
            this.batteryLevel = 100;
        } else {
            this.batteryLevel = batteryLevel;
        }
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        if (maxAltitude < 0) {
            this.maxAltitude = 0;
        } else {
            this.maxAltitude = maxAltitude;
        }
    }
//абстр
    public abstract double calculateFlightTime();
    public abstract String getDroneType();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Drone other = (Drone) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public int compareTo(Drone other) {
        int result = model.compareTo(other.model);
        if (result != 0) {
            return result;
        }
        return Integer.compare(id, other.id);
    }
    public void printInfo() {
        System.out.println("id: " + id);
        System.out.println("модель: " + model);
        System.out.println("заряд батареи: " + batteryLevel + "%");
        System.out.println("максимальная высота: " + maxAltitude + " м");
        System.out.println("тип: " + getDroneType());
        System.out.println("время полёта: " + calculateFlightTime() + " мин");
    }

    @Override
    public String toString() {
        return getDroneType() + " #" + id + " " + model + " (" + batteryLevel + "%)";
    }
}
