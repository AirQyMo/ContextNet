package main.java.EnQyMo;

import utils.java.Sensor.Sensor;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.List;

import java.io.File;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import ckafka.data.Swap;
import ckafka.data.SwapData;
import main.java.application.ModelApplication;

public class ProcessingNode extends ModelApplication{
    private Swap swap;
    private ObjectMapper objectMapper;
    private Sensor[] sensors;
    //private static final Logger logger = LoggerFactory.getLogger(ProcessingNode.class);

    private boolean fim = false;

    // Map<String, Consumer<String[]>> commandMap = new HashMap<>();

    /**
     * Constructor
     */
    public ProcessingNode() {
        this.objectMapper = new ObjectMapper();
        this.swap = new Swap(objectMapper);
    }

    /**
     * Main
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        ProcessingNode pn = new ProcessingNode();
        pn.runPN(keyboard);
    }

    public void runPN(Scanner keyboard) {
        System.out.println("Processing Node started. Type 'CTRL + C' to stop.");

        ObjectMapper mapper = new ObjectMapper();

        // Getting JSON object with sensor data
        try {
            this.sensors = mapper.readValue(new File("data/sensors.json"), Sensor[].class);
        } catch (com.fasterxml.jackson.core.JsonParseException | com.fasterxml.jackson.databind.JsonMappingException e) {
            System.err.println("Error parsing sensors.json: " + e.getMessage());
            e.printStackTrace();
        } catch (java.io.IOException e) {
            System.err.println("IO error reading sensors.json: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Loaded " + (sensors != null ? sensors.length : 0) + " sensors.");

        while(!fim) {
            // System.out.println("### n = " + n++ + " ###");

        }
        keyboard.close();
        System.out.println("END!");
        System.exit(0);
    }

    /**
     * Execute a command
     * @param fullCommand : Full command to execute
     */
    private void executeCommand(String fullCommand) {
        String[] parts = fullCommand.split(" ", 2);
        if (parts.length > 1 && parts[0].equals("sensor")) {
            System.out.println("Sensor command received:\n" + parts[1]);
            // Process regular sensor commands here
        } else {
            System.out.println("Invalid command");
        }
    }


    /**
     * Process sensor alert from JSON
     * @param analysis : AlertAnalysis object parsed from JSON
     */
    private void processAnalysis(AlertAnalysis analysis) {
        try {

            System.out.println("=== Alert Analysis ===");
            System.out.println("Alert ID: " + analysis.getAlertId());
            System.out.println("Timestamp: " + analysis.getTimestamp());
            System.out.println("Number of sensors: " + analysis.getSensores().size());

            for (SensorAlert sensorAlert : analysis.getSensores()) {
                System.out.println("\n--- Sensor: " + sensorAlert.getSensorId() + " ---");

                for (Pollutant pollutant : sensorAlert.getPollutants()) {
                    System.out.println("  Pollutant: " + pollutant.getName());
                    System.out.println("  Risk Level: " + pollutant.getRiskLevel());
                    System.out.println("  Affected Diseases: " +
                        String.join(", ", pollutant.getAffectedDiseases().getDisease()));
                }
            }


        } catch (Exception e) {
            System.err.println("Error processing sensor alert: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Record the received Kafka message
     * @param record : ConsumerRecord containing the message
     */
    @Override
    public void recordReceived(ConsumerRecord record) {
        System.out.println(String.format("Command received from %s", record.key()));

        try {
            // First, try to parse as raw JSON to check the message type
            String rawMessage = new String((byte[]) record.value(), StandardCharsets.UTF_8);
            System.out.println("Raw message received: " + rawMessage);

            // Check if JSON contains "analisys" field
            if (rawMessage.trim().contains("\"analisys\"")) {
                System.out.println("Analysis command detected. Processing alert...");
                Alert alert = objectMapper.readValue(rawMessage, Alert.class);
                processAnalysis(alert.getAnalisys());
            } else {
                // Try to deserialize as SwapData for regular messages
                try {
                    SwapData data = swap.SwapDataDeserialization((byte[]) record.value());
                    String text = new String(data.getMessage(), StandardCharsets.UTF_8);
                    System.out.println("Message received: " + text);
                    System.out.println("Regular sensor command detected. Executing command...");
                    executeCommand("sensor " + text);
                } catch (Exception e) {
                    System.out.println("Could not parse as SwapData, treating as plain text...");
                    executeCommand("sensor " + rawMessage);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param keyboard
     */
    private void sendUnicastMessage(Scanner keyboard) {
        System.out.println("UUID:\nHHHHHHHH-HHHH-HHHH-HHHH-HHHHHHHHHHHH");
        String uuid = keyboard.nextLine();
        System.out.print("Message: ");
        String messageText = keyboard.nextLine();
        System.out.println(String.format("Sending |%s| to %s.", messageText, uuid));

        try {
            sendRecord(createRecord("PrivateMessageTopic", uuid, swap.SwapDataSerialization(createSwapData(messageText))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send groupcast message
     * @param keyboard
     */
    private void sendGroupcastMessage(String messageText, Integer groupNumber, String topic) {
        String group = String.format("%d", groupNumber);

        System.out.println(String.format("Sending message %s to group %s.", messageText, group));
        try {
            // sendRecord(createRecord("GroupMessageTopic", group, swap.SwapDataSerialization(createSwapData(messageText))));

            SwapData data = createSwapData(messageText);
            data.setMessage(messageText.getBytes());
            data.setTopic(topic);

            sendRecord(createRecord("GroupMessageTopic", group, swap.SwapDataSerialization(data)));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error on SendGroupCastMessage", e);
        }
    }
}

// ==================== Private Classes for Alert Analysis ====================

/**
 * Wrapper class for the JSON structure with "analisys" field
 */
class Alert {
    @JsonProperty("analisys")
    private AlertAnalysis analisys;

    public Alert() {}

    public AlertAnalysis getAnalisys() {
        return analisys;
    }

    public void setAnalisys(AlertAnalysis analisys) {
        this.analisys = analisys;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "analisys=" + analisys +
                '}';
    }
}

/**
 * Main class representing the alert analysis structure
 */
class AlertAnalysis {
    @JsonProperty("alert_id")
    private String alertId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("sensores")
    private List<SensorAlert> sensores;

    public AlertAnalysis() {}

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<SensorAlert> getSensores() {
        return sensores;
    }

    public void setSensores(List<SensorAlert> sensores) {
        this.sensores = sensores;
    }

    @Override
    public String toString() {
        return "AlertAnalysis{" +
                "alertId='" + alertId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sensores=" + sensores +
                '}';
    }
}

/**
 * Class representing a sensor alert with pollutants information
 */
class SensorAlert {
    @JsonProperty("sensor_id")
    private String sensorId;

    @JsonProperty("poluentes")
    private List<Pollutant> pollutants;

    public SensorAlert() {}

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public List<Pollutant> getPollutants() {
        return pollutants;
    }

    public void setPollutants(List<Pollutant> pollutants) {
        this.pollutants = pollutants;
    }

    @Override
    public String toString() {
        return "SensorAlert{" +
                "sensorId='" + sensorId + '\'' +
                ", pollutants=" + pollutants +
                '}';
    }
}

/**
 * Class representing a pollutant with risk level and affected diseases
 */
class Pollutant {
    @JsonProperty("poluente")
    private String name;

    @JsonProperty("risk_level")
    private String riskLevel;

    @JsonProperty("affected_diseases")
    private AffectedDiseases affectedDiseases;

    public Pollutant() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public AffectedDiseases getAffectedDiseases() {
        return affectedDiseases;
    }

    public void setAffectedDiseases(AffectedDiseases affectedDiseases) {
        this.affectedDiseases = affectedDiseases;
    }

    @Override
    public String toString() {
        return "Pollutant{" +
                "name='" + name + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", affectedDiseases=" + affectedDiseases +
                '}';
    }
}

/**
 * Class representing the list of affected diseases
 */
class AffectedDiseases {
    @JsonProperty("disease")
    private List<String> disease;

    public AffectedDiseases() {}

    public List<String> getDisease() {
        return disease;
    }

    public void setDisease(List<String> disease) {
        this.disease = disease;
    }

    @Override
    public String toString() {
        return "AffectedDiseases{" +
                "disease=" + disease +
                '}';
    }
}
