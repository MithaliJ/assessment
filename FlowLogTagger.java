import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FlowLogTagger {

    // Map to store lookup data (port + protocol -> tag)
    private static final Map<String, String> tagLookupMap = new HashMap<>();
    private static final Map<String, Integer> tagCountMap = new HashMap<>();
    private static final Map<String, Integer> portProtocolCountMap = new HashMap<>();
    private static final String UNTAGGED = "Untagged";

    public static void main(String[] args) throws IOException {
        String lookupFile = "lookup_table.csv";
        String flowLogFile = "flow_log.txt";
        String outputFile = "output_results.txt";

        // Load lookup data
        loadLookupData(lookupFile);

        // Process flow log file
        processFlowLog(flowLogFile);

        // Write results to output file
        writeOutput(outputFile);
    }

    private static void loadLookupData(String lookupFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(lookupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("dstport")) continue;
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String portProtocolKey = parts[0].trim() + "," + parts[1].trim().toLowerCase();
                    String tag = parts[2].trim();
                    tagLookupMap.put(portProtocolKey, tag);
                }
            }
        }
    }

    private static void processFlowLog(String flowLogFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(flowLogFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length < 14) continue;  // skip lines that don't have the expected number of columns

                String dstPort = parts[7];
                String protocol = parts[8].equals("6") ? "tcp" : parts[6].equals("17") ? "udp" : parts[6].equals("1") ? "icmp" : "other";
                String portProtocolKey = dstPort + "," + protocol;

                // Get tag from the lookup map
                String tag = tagLookupMap.getOrDefault(portProtocolKey, UNTAGGED);

                // Update tag count
                tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);

                // Update port/protocol count
                portProtocolCountMap.put(portProtocolKey, portProtocolCountMap.getOrDefault(portProtocolKey, 0) + 1);
            }
        }
    }

    private static void writeOutput(String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Tag Counts:\n");
            writer.write("Tag,Count\n");
            for (Map.Entry<String, Integer> entry : tagCountMap.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }

            writer.write("\nPort/Protocol Combination Counts:\n");
            writer.write("Port,Protocol,Count\n");
            for (Map.Entry<String, Integer> entry : portProtocolCountMap.entrySet()) {
                String[] portProtocol = entry.getKey().split(",");
                writer.write(portProtocol[0] + "," + portProtocol[1] + "," + entry.getValue() + "\n");
            }
        }
    }
}
