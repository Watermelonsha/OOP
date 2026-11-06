import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static DroneService service = new DroneService();
    private static FileStorage storage = new FileStorage("data.txt");
    private static int nextRequestId = 1;

    public static void main(String[] args) {
        if (storage.fileExists()) {
            nextRequestId = storage.load(service);
            System.out.println("данные загружены из файла data.txt");
        } else {
            fillStartData();
            storage.save(service, nextRequestId);
            System.out.println("создан файл data.txt с начальными данными");
        }
        printMenu();

        boolean running = true;
        while (running) {
            String line = readLine("\n> ").toLowerCase();
            int command = getCommand(line);

            if (command == 0) {
                running = false;
            } else if (command == 1) {
                showDrones();
            } else if (command == 2) {
                makeOrder();
            } else if (command == 3) {
                service.processRequests();
            } else if (command == 4) {
                addDrone();
            } else if (command == 5) {
                removeDrone();
            } else if (command == 6) {
                findDrone();
            } else if (command == 7) {
                chargeDrone();
            } else if (command == 8) {
                showSorted();
            } else if (command == 9) {
                service.printReport();
            } else if (command == 10) {
                printMenu();
            } else {
                System.out.println("не понял команду, напиши 10 чтобы увидеть меню");
            }

            boolean changed = command == 2 || command == 3 || command == 4
                    || command == 5 || command == 7;
            if (changed) {
                storage.save(service, nextRequestId);
            }
        }
        System.out.println("пока!");
    }

    private static void fillStartData() {
        service.addDrone(new CameraDrone(1, "Mavic", 80, 500, 20));
        service.addDrone(new CameraDrone(2, "Air", 15, 400, 12));
        service.addDrone(new DeliveryDrone(3, "Cargo", 90, 300, 5));
        service.addDrone(new DeliveryDrone(4, "Heavy", 50, 200, 10));

        service.addPilot(new Pilot(1, "Айгуль"));
        service.addPilot(new Pilot(2, "Бексултан"));
        service.assignDrone(1, 1);
        service.assignDrone(1, 3);
        service.assignDrone(2, 4);
    }

    private static void printMenu() {
        System.out.println("меню (пиши цифру или обычную фразу):");
        System.out.println("1  список дронов и заряд (дай количество дронов и их заряд)");
        System.out.println("2  сделать заказ");
        System.out.println("3  обработать очередь заказов");
        System.out.println("4  добавить дрона");
        System.out.println("5  удалить дрона");
        System.out.println("6  найти дрона по id");
        System.out.println("7  зарядить дрона");
        System.out.println("8  сортировка и фильтр");
        System.out.println("9  отчёт");
        System.out.println("10 меню");
        System.out.println("0  выход");
    }

    private static int getCommand(String line) {
        if (isNumber(line)) {
            return Integer.parseInt(line);
        }
        if (line.contains("выход") || line.contains("exit")) {
            return 0;
        }
        if (line.contains("зарядить")) {
            return 7;
        }
        if (line.contains("добав")) {
            return 4;
        }
        if (line.contains("удал")) {
            return 5;
        }
        if (line.contains("найти") || line.contains("найд") || line.contains("поиск")) {
            return 6;
        }
        if (line.contains("заказ") || line.contains("заявк")) {
            return 2;
        }
        if (line.contains("очеред") || line.contains("обработ")) {
            return 3;
        }
        if (line.contains("сортир") || line.contains("фильтр")) {
            return 8;
        }
        if (line.contains("отчет") || line.contains("отчёт")) {
            return 9;
        }
        if (line.contains("помощь") || line.contains("меню")) {
            return 10;
        }
        if (line.contains("количество") || line.contains("сколько") || line.contains("заряд")
                || line.contains("список") || line.contains("дрон")) {
            return 1;
        }
        return -1;
    }

    private static void showDrones() {
        List<Drone> drones = service.getSorted();
        System.out.println("всего дронов: " + drones.size());
        System.out.println("камера-дронов: " + service.getByType(Drone.CAMERA_TYPE).size());
        System.out.println("дронов-доставщиков: " + service.getByType(Drone.DELIVERY_TYPE).size());
        for (Drone drone : drones) {
            System.out.println("  " + drone);
        }
    }

    private static void makeOrder() {
        String name = readLine("имя клиента: ");
        if (name.isEmpty()) {
            System.out.println("имя не может быть пустым");
            return;
        }
        int type = readInt("что нужно? 1 - съёмка видео, 2 - доставка груза: ");
        if (type == 1) {
            int minutes = readInt("сколько минут снимать: ");
            if (minutes <= 0) {
                System.out.println("минуты должны быть больше 0");
                return;
            }
            service.submitRequest(new Request(nextRequestId, name, false, minutes));
            nextRequestId++;
        } else if (type == 2) {
            double weight = readDouble("вес груза в кг: ");
            if (weight <= 0) {
                System.out.println("вес должен быть больше 0");
                return;
            }
            service.submitRequest(new Request(nextRequestId, name, true, weight));
            nextRequestId++;
        } else {
            System.out.println("нужно ввести 1 или 2");
        }
    }

    private static void addDrone() {
        int type = readInt("тип? 1 - камера-дрон, 2 - дрон-доставщик: ");
        if (type != 1 && type != 2) {
            System.out.println("нужно ввести 1 или 2");
            return;
        }
        int id = readInt("id: ");
        String model = readLine("модель: ");
        if (model.isEmpty()) {
            System.out.println("модель не может быть пустой");
            return;
        }
        double battery = readDouble("заряд (0-100): ");
        double altitude = readDouble("максимальная высота, м: ");

        try {
            if (type == 1) {
                int resolution = readInt("разрешение камеры, МП: ");
                service.addDrone(new CameraDrone(id, model, battery, altitude, resolution));
            } else {
                double payload = readDouble("максимальный груз, кг: ");
                service.addDrone(new DeliveryDrone(id, model, battery, altitude, payload));
            }
            System.out.println("дрон добавлен");
        } catch (IllegalArgumentException e) {
            System.out.println("ошибка: " + e.getMessage());
        }
    }

    private static void removeDrone() {
        int id = readInt("id дрона: ");
        try {
            service.removeDrone(id);
            System.out.println("дрон удалён");
        } catch (Exception e) {
            System.out.println("ошибка: " + e.getMessage());
        }
    }

    private static void findDrone() {
        int id = readInt("id дрона: ");
        try {
            Drone drone = service.findById(id);
            drone.printInfo();
        } catch (Exception e) {
            System.out.println("ошибка: " + e.getMessage());
        }
    }

    private static void chargeDrone() {
        int id = readInt("id дрона: ");
        double percent = readDouble("на сколько процентов зарядить: ");
        try {
            Drone drone = service.findById(id);
            if (drone instanceof Chargeable) {
                ((Chargeable) drone).chargeBattery(percent);
            }
        } catch (Exception e) {
            System.out.println("ошибка: " + e.getMessage());
        }
    }

    private static void showSorted() {
        System.out.println("1 - по модели, 2 - по заряду, 3 - по времени полёта,");
        System.out.println("4 - по высоте, 5 - только с зарядом от N");
        int choice = readInt("выбор: ");

        List<Drone> result;
        if (choice == 1) {
            result = service.getSorted();
        } else if (choice == 2) {
            result = service.getSorted(new BatteryComparator());
        } else if (choice == 3) {
            result = service.getSorted(new FlightTimeComparator());
        } else if (choice == 4) {
            result = service.getSorted(new AltitudeComparator());
        } else if (choice == 5) {
            double min = readDouble("минимальный заряд: ");
            result = service.filterByBattery(min);
        } else {
            System.out.println("нет такого пункта");
            return;
        }

        if (result.isEmpty()) {
            System.out.println("ничего не найдено");
        }
        for (Drone drone : result) {
            System.out.println("  " + drone);
        }
    }

    private static String readLine(String text) {
        System.out.print(text);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("ввод закончился, выход");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private static int readInt(String text) {
        while (true) {
            String line = readLine(text);
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("нужно целое число, попробуй ещё раз");
            }
        }
    }

    private static double readDouble(String text) {
        while (true) {
            String line = readLine(text).replace(',', '.');
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("нужно число, попробуй ещё раз");
            }
        }
    }

    private static boolean isNumber(String line) {
        try {
            Integer.parseInt(line);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
