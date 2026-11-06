public class CameraDrone extends Drone implements Chargeable, Recordable {

    private int cameraResolution;

    public CameraDrone(int id, String model, double batteryLevel, double maxAltitude, int cameraResolution) {
        super(id, model, batteryLevel, maxAltitude);
        setCameraResolution(cameraResolution);
    }

    public int getCameraResolution() {
        return cameraResolution;
    }

    public void setCameraResolution(int cameraResolution) {
        if (cameraResolution < 0) {
            this.cameraResolution = 0;
        } else {
            this.cameraResolution = cameraResolution;
        }
    }

    @Override
    public double calculateFlightTime() {
        // камера дрон
        return batteryLevel * 0.3;
    }

    @Override
    public String getDroneType() {
        return CAMERA_TYPE;
    }

    @Override
    public void chargeBattery(double percent) {
        setBatteryLevel(getBatteryLevel() + percent);
        System.out.println(getModel() + " заряжен до " + getBatteryLevel() + "%");
    }

    @Override
    public void recordVideo(int minutes) {
        System.out.println(getModel() + " снимает видео " + minutes + " мин в разрешении " + cameraResolution + " МП");
    }

    // собственный метод, только у CameraDrone
    public void zoomIn() {
        System.out.println(getModel() + " приближает камеру");
    }
}
