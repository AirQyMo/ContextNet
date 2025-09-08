package utils.java.Sensor;

public class Sensor {
    public String id;
    public SensorProperties properties;

    // Construtor padrão para Jackson
    public Sensor() {}

    public Sensor(String id, String name, int group) {
        this.id = id;
        this.properties = new SensorProperties(name, group);
    }
}

class SensorProperties {
    public String name;
    public int group;

    // Construtor padrão para Jackson
    public SensorProperties() {}

    public SensorProperties(String name, int group) {
        this.name = name;
        this.group = group;
    }
}
