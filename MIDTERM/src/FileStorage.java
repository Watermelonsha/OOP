import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private String fileName;

    public FileStorage(String fileName) {
        this.fileName = fileName;
    }

    public boolean fileExists() {
        return Files.exists(Paths.get(fileName));
    }

    // каждая строка файла это одна запись, поля разделены точкой с запятой
    public void save(DroneService service, int nextRequestId) {
        List<String> lines = new ArrayList<>();

        for (Drone drone : service.getAllDrones()) {
            if (drone instanceof CameraDrone) {
                CameraDrone camera = (CameraDrone) drone;
                lines.add("CAMERA;" + camera.getId() + ";" + clean(camera.getModel()) + ";"
                        + camera.getBatteryLevel() + ";" + camera.getMaxAltitude() + ";"
                        + camera.getCameraResolution());
            } else if (drone instanceof DeliveryDrone) {
                DeliveryDrone delivery = (DeliveryDrone) drone;
                lines.add("DELIVERY;" + delivery.getId() + ";" + clean(delivery.getModel()) + ";"
                        + delivery.getBatteryLevel() + ";" + delivery.getMaxAltitude() + ";"
                        + delivery.getMaxPayload());
            }
        }

        for (Pilot pilot : service.getPilots()) {
            lines.add("PILOT;" + pilot.getId() + ";" + clean(pilot.getName()));
            for (Drone drone : pilot.getDrones()) {
                lines.add("ASSIGN;" + pilot.getId() + ";" + drone.getId());
            }
        }

        for (Request request : service.getRequests()) {
            lines.add("REQUEST;" + request.getId() + ";" + clean(request.getClientName()) + ";"
                    + request.isDelivery() + ";" + request.getValue());
        }

        lines.add("NEXTID;" + nextRequestId);
        lines.add("STATS;" + service.getDoneCount() + ";" + service.getFailedCount());

        try {
            Files.write(Paths.get(fileName), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("не удалось сохранить файл: " + e.getMessage());
        }
    }

    // возвращает номер для следующей заявки
    public int load(DroneService service) {
        int nextRequestId = 1;
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("не удалось прочитать файл: " + e.getMessage());
            return nextRequestId;
        }

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split(";");
            try {
                if (p[0].equals("CAMERA")) {
                    service.addDrone(new CameraDrone(Integer.parseInt(p[1]), p[2],
                            Double.parseDouble(p[3]), Double.parseDouble(p[4]), Integer.parseInt(p[5])));
                } else if (p[0].equals("DELIVERY")) {
                    service.addDrone(new DeliveryDrone(Integer.parseInt(p[1]), p[2],
                            Double.parseDouble(p[3]), Double.parseDouble(p[4]), Double.parseDouble(p[5])));
                } else if (p[0].equals("PILOT")) {
                    service.addPilot(new Pilot(Integer.parseInt(p[1]), p[2]));
                } else if (p[0].equals("ASSIGN")) {
                    service.assignDrone(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                } else if (p[0].equals("REQUEST")) {
                    service.addRequest(new Request(Integer.parseInt(p[1]), p[2],
                            Boolean.parseBoolean(p[3]), Double.parseDouble(p[4])));
                } else if (p[0].equals("NEXTID")) {
                    nextRequestId = Integer.parseInt(p[1]);
                } else if (p[0].equals("STATS")) {
                    service.setCounters(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                }
            } catch (Exception e) {
                System.out.println("строка пропущена: " + line);
            }
        }
        return nextRequestId;
    }

    // точка с запятой в тексте сломала бы разбор строки
    private String clean(String text) {
        return text.replace(';', ',');
    }
}
