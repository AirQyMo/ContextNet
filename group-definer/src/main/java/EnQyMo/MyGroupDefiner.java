package main.java.EnQyMo;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ckafka.data.Swap;
import main.java.ckafka.GroupDefiner;
import main.java.ckafka.GroupSelection;


public class MyGroupDefiner implements GroupSelection {
    /** Logger */
    final Logger logger = LoggerFactory.getLogger(MyGroupDefiner.class);

    public static void main(String[] args) {
        MyGroupDefiner MyGD = new MyGroupDefiner();
    }

    /**
     * Constructor
     */
    public MyGroupDefiner() {
        ObjectMapper objectMapper = new ObjectMapper();
        Swap swap = new Swap(objectMapper);
        new GroupDefiner(this, swap);
    }

    /**
     * Set with all the groups that this GroupDefiner controls.
     * @return set with all the groups that this GroupDefiner manages
     */
    public Set<Integer> groupsIdentification() {
        /**
         * 1001 -> Beacon 1, Beacon 2
         * 1002 -> Beacon 3
         * 1003 -> Beacon 4
         */
        Set<Integer> setOfGroups = new HashSet<Integer>();

        setOfGroups.add(1001);
        setOfGroups.add(1002);
        setOfGroups.add(1003);

        return setOfGroups;
    }

    /**
     * Function to get user group ID from location
     * @param location
     * @return group ID
     */
    private int getGroupIDFromBeacons(String[] beacons)
    {
        for (String beacon : beacons) {
            // System.out.println("Beacon: " + beacon);
            beacon = beacon.replace("\"", "");
            switch (beacon) {
                case "Beacon 1":
                    return 1001;
                case "Beacon 2":
                    return 1001;
                case "Beacon 3":
                    return 1002;
                case "Beacon 4":
                    return 1003;
            }
        }

        return -1;
    }

    /**
     * Set with all the groups related to this contextInfo.
     * Only groups controlled by this GroupDefiner.
     *
     * @param contextInfo context info
     * @return set with all the groups related to this contextInfo
     */
    public Set<Integer> getNodesGroupByContext(ObjectNode contextInfo) {
        // Initialize a set to store group IDs
        Set<Integer> setOfGroups = new HashSet<Integer>();
        System.out.println("#--------------# Receiving context #--------------#");

        // Extract the location from contextInfo
        String string_beacons = String.valueOf(contextInfo.get("beacons"));
        System.out.println("Beacons from context: " + string_beacons);

        // Remove brackets and spaces, then split into array
        string_beacons = string_beacons.replace("[", "").replace("]", "");
        String[] beacons = string_beacons.split(", ");


        if (beacons != null) {
            int group = this.getGroupIDFromBeacons(beacons);

            if (group != -1) {
                setOfGroups.add(group);
                System.out.println("Group ID " + group + " added based on beacons: " + Arrays.toString(beacons));
            } else {
                System.out.println("No valid group found for beacons: " + Arrays.toString(beacons));
            }

        } else {
            System.out.println("No beacons detected at location.");
        }

        // Log the final set of groups for debugging purposes
        System.out.println(setOfGroups);

        return setOfGroups; // Return the set of group IDs
}

    public String kafkaConsumerPrefix() {
        return "gd.one.consumer";
    }

    public String kafkaProducerPrefix() {
        return "gd.one.producer";
    }
}
