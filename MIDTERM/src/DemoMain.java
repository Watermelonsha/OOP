import java.util.List;

public class DemoMain {

    public static void main(String[] args) {
        DroneService service = new DroneService();

        service.addDrone(new CameraDrone(1, "Mavic", 80, 500, 20));
        service.addDrone(new CameraDrone(2, "Air", 15, 400, 12));
        service.addDrone(new DeliveryDrone(3, "Cargo", 90, 300, 5));
        service.addDrone(new DeliveryDrone(4, "Heavy", 50, 200, 10));

        try {
            service.addDrone(new CameraDrone(1, "Копия", 70, 100, 8));
        } catch (IllegalArgumentException e) {
            System.out.println("ошибка: " + e.getMessage());
        }

        service.addPilot(new Pilot(1, "Айгуль"));
        service.addPilot(new Pilot(2, "Бексултан"));
        service.assignDrone(1, 1);
        service.assignDrone(1, 3);
        service.assignDrone(2, 4);

        System.out.println();
        System.out.println("поиск по id 3: " + service.findById(3));
        try {
            service.findById(99);
        } catch (Exception e) {
            System.out.println("ошибка: " + e.getMessage());
        }

        System.out.println();
        System.out.println("по модели (compareTo)");
        printList(service.getSorted());
        System.out.println("по заряду");
        printList(service.getSorted(new BatteryComparator()));
        System.out.println("по времени полёта");
        printList(service.getSorted(new FlightTimeComparator()));
        System.out.println("по высоте, потом по модели");
        printList(service.getSorted(new AltitudeComparator()));
        System.out.println("заряд от 50");
        printList(service.filterByBattery(50));

        System.out.println();
        System.out.println("клиенты подают заявки");
        service.submitRequest(new Request(1, "Асель", false, 10));
        service.submitRequest(new Request(2, "Тимур", true, 4));
        service.submitRequest(new Request(3, "Нурлан", true, 12));
        service.submitRequest(new Request(4, "Мээрим", false, 5));

        System.out.println();
        System.out.println("оператор обрабатывает очередь");
        service.processRequests();
        service.processRequests();

        System.out.println();
        service.updateBattery(2, 100);
        service.removeDrone(3);
        System.out.println("после удаления дрона 3");
        printList(service.getSorted());

        System.out.println();
        service.printReport();
    }

    private static void printList(List<Drone> list) {
        for (Drone drone : list) {
            System.out.println("  " + drone);
        }
    }
}
