import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

public class DroneService {

    private Set<Drone> drones = new LinkedHashSet<>();
    private Map<Integer, Drone> droneById = new HashMap<>();
    private Map<String, List<Drone>> dronesByType = new HashMap<>();
    private Map<Integer, Pilot> pilotById = new HashMap<>();
    private Queue<Request> requests = new LinkedList<>();

    private int doneCount = 0;
    private int failedCount = 0;

    public void addDrone(Drone drone) {
        if (drone == null) {
            throw new IllegalArgumentException("дрон не может быть null");
        }
        if (drones.contains(drone) || droneById.containsKey(drone.getId())) {
            throw new IllegalArgumentException("дрон с id " + drone.getId() + " уже есть");
        }
        drones.add(drone);
        droneById.put(drone.getId(), drone);

        String type = drone.getDroneType();
        if (!dronesByType.containsKey(type)) {
            dronesByType.put(type, new ArrayList<>());
        }
        dronesByType.get(type).add(drone);
    }

    public Drone findById(int id) {
        Drone drone = droneById.get(id);
        if (drone == null) {
            throw new NoSuchElementException("дрон с id " + id + " не найден");
        }
        return drone;
    }

    public void updateBattery(int id, double level) {
        Drone drone = findById(id);
        drone.setBatteryLevel(level);
    }

    public void removeDrone(int id) {
        Drone drone = findById(id);
        drones.remove(drone);
        droneById.remove(id);
        dronesByType.get(drone.getDroneType()).remove(drone);
        for (Pilot pilot : pilotById.values()) {
            pilot.getDrones().remove(drone);
        }
    }

    public void addPilot(Pilot pilot) {
        if (pilotById.containsKey(pilot.getId())) {
            throw new IllegalArgumentException("пилот с id " + pilot.getId() + " уже есть");
        }
        pilotById.put(pilot.getId(), pilot);
    }

    public void assignDrone(int pilotId, int droneId) {
        Pilot pilot = pilotById.get(pilotId);
        if (pilot == null) {
            throw new NoSuchElementException("пилот с id " + pilotId + " не найден");
        }
        Drone drone = findById(droneId);
        if (pilot.getDrones().contains(drone)) {
            throw new IllegalArgumentException("дрон уже закреплён за этим пилотом");
        }
        pilot.getDrones().add(drone);
    }

    public List<Drone> getByType(String type) {
        List<Drone> list = dronesByType.get(type);
        if (list == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(list);
    }

    public List<Drone> filterByBattery(double minLevel) {
        List<Drone> result = new ArrayList<>();
        for (Drone drone : drones) {
            if (drone.getBatteryLevel() >= minLevel) {
                result.add(drone);
            }
        }
        return result;
    }

    public List<Drone> getSorted() {
        List<Drone> list = new ArrayList<>(drones);
        Collections.sort(list);
        return list;
    }

    public List<Drone> getSorted(Comparator<Drone> comparator) {
        List<Drone> list = new ArrayList<>(drones);
        Collections.sort(list, comparator);
        return list;
    }

    public void submitRequest(Request request) {
        addRequest(request);
        System.out.println("подана " + request);
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

    public void processRequests() {
        if (requests.isEmpty()) {
            System.out.println("очередь пустая");
            return;
        }
        while (!requests.isEmpty()) {
            Request request = requests.poll();
            System.out.println("берём " + request);
            processOne(request);
        }
    }

    private void processOne(Request request) {
        String type = Drone.CAMERA_TYPE;
        if (request.isDelivery()) {
            type = Drone.DELIVERY_TYPE;
        }

        Drone found = null;
        for (Drone drone : getByType(type)) {
            boolean suitable = drone.getBatteryLevel() >= 20;
            if (suitable && request.isDelivery()) {
                DeliveryDrone deliveryDrone = (DeliveryDrone) drone;
                if (deliveryDrone.getMaxPayload() < request.getValue()) {
                    suitable = false;
                }
            }
            if (suitable) {
                found = drone;
                break;
            }
        }

        if (found == null) {
            System.out.println("нет подходящего дрона, заявка не выполнена");
            failedCount++;
            return;
        }

        if (request.isDelivery()) {
            ((Deliverable) found).deliverPackage(request.getValue());
        } else {
            ((Recordable) found).recordVideo((int) request.getValue());
        }
        found.setBatteryLevel(found.getBatteryLevel() - 15);
        doneCount++;
    }

    // методы для сохранения в файл
    public List<Drone> getAllDrones() {
        return new ArrayList<>(drones);
    }

    public List<Pilot> getPilots() {
        return new ArrayList<>(pilotById.values());
    }

    public List<Request> getRequests() {
        return new ArrayList<>(requests);
    }

    public int getDoneCount() {
        return doneCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setCounters(int done, int failed) {
        doneCount = done;
        failedCount = failed;
    }

    public void printReport() {
        System.out.println("отчёт");
        System.out.println("всего дронов: " + drones.size());
        for (String type : dronesByType.keySet()) {
            System.out.println(type + ": " + dronesByType.get(type).size());
        }

        if (!drones.isEmpty()) {
            double sum = 0;
            Drone best = null;
            for (Drone drone : drones) {
                sum = sum + drone.getBatteryLevel();
                if (best == null || drone.getBatteryLevel() > best.getBatteryLevel()) {
                    best = drone;
                }
            }
            System.out.println("средний заряд: " + sum / drones.size() + "%");
            System.out.println("самый заряженный: " + best);
        }

        System.out.println("выполнено заявок: " + doneCount);
        System.out.println("не выполнено заявок: " + failedCount);
        for (Pilot pilot : pilotById.values()) {
            System.out.println(pilot);
        }
    }
}
