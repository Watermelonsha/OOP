public class Request {

    private int id;
    private String clientName;
    private boolean delivery;
    private double value;

    public Request(int id, String clientName, boolean delivery, double value) {
        this.id = id;
        this.clientName = clientName;
        this.delivery = delivery;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request other = (Request) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        if (delivery) {
            return "заявка #" + id + " от " + clientName + ", доставка " + value + " кг";
        }
        return "заявка #" + id + " от " + clientName + ", съёмка " + (int) value + " мин";
    }
}
