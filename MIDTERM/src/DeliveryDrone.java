public class DeliveryDrone extends Drone implements Chargeable, Deliverable {

    private double maxPayload;

    public DeliveryDrone(int id, String model, double batteryLevel, double maxAltitude, double maxPayload) {
        super(id, model, batteryLevel, maxAltitude);
        setMaxPayload(maxPayload);
    }

    public double getMaxPayload() {
        return maxPayload;
    }

    public void setMaxPayload(double maxPayload) {
        if (maxPayload < 0) {
            this.maxPayload = 0;
        } else {
            this.maxPayload = maxPayload;
        }
    }

    @Override
    public double calculateFlightTime() {
        // дрон-доставщик тяжелее, летает меньше
        return batteryLevel * 0.2;
    }

    @Override
    public String getDroneType() {
        return DELIVERY_TYPE;
    }

    @Override
    public void chargeBattery(double percent) {
        setBatteryLevel(getBatteryLevel() + percent);
        System.out.println(getModel() + " заряжен до " + getBatteryLevel() + "%");
    }

    @Override
    public void deliverPackage(double weightKg) {
        if (weightKg > maxPayload) {
            System.out.println(getModel() + " не может доставить груз " + weightKg + " кг, превышен лимит " + maxPayload + " кг");
        } else {
            System.out.println(getModel() + " доставляет груз весом " + weightKg + " кг");
        }
    }

    // собственный метод, только у DeliveryDrone
    public void trackPackage() {
        System.out.println(getModel() + " отслеживает местоположение груза");
    }
}
