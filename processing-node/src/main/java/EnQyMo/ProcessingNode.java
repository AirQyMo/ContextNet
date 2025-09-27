package main.java.EnQyMo;

import utils.java.Sensor.Sensor;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import java.io.File;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;

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
        } else {
            System.out.println("Invalid command");
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
            SwapData data = swap.SwapDataDeserialization((byte[]) record.value());
            String text = new String(data.getMessage(), StandardCharsets.UTF_8);
            System.out.println("Message received: " + text);
            executeCommand("sensor " + text);
        } catch (Exception e) {
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
