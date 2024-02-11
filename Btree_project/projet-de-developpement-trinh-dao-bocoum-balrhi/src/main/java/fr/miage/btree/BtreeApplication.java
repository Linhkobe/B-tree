package fr.miage.btree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import org.springframework.util.StopWatch;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@SpringBootApplication
public class BtreeApplication {

    int index = 0;

    // The tree key and value are both String, but you can change it to whatever you want, Generic types are used
    Btree<String, String> bplustree;
    private List<String> benchmarkKeys = new ArrayList<>();



    public static void main(String[] args) {
        SpringApplication.run(BtreeApplication.class, args);
    }
    @Bean
    public Btree<String, String> bplustree() {
        bplustree = new Btree<>();
        return bplustree;
    }
    /**
     * This method is called on the application startup thanks to the @PostConstruct annotation
     * It is used to initialize the tree with some random data
     */
    @PostConstruct
    public void init() {
        if (bplustree == null) {
            bplustree = bplustree();
        }
        importDataFromCSV("src/main/resources/DATA_100000.csv");
        // Generate and store 1000 random keys into benchmarkKeys
        generateRandomKeysForBenchmark(1000);

    }
    private void importDataFromCSV(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            List<KeyValueData> keyValueBatch = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                // Split the CSV line into fields (assuming comma-separated values)
                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    String key = fields[0].trim();
                    String value = fields[1].trim();
                    // Insert key and value into your Btree
                    bplustree.insert(key, value);
                }
            }

            System.out.println("Data imported successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error importing data from CSV.");
        }
    }
    /**
     * Method to generate random keys for benchmarking
     */
    private void generateRandomKeysForBenchmark(int numKeys) {
        List<String> allKeys = bplustree.getAllKeys(); 
        Collections.shuffle(allKeys);
        benchmarkKeys = allKeys.subList(0, Math.min(numKeys, allKeys.size()));
    }
    /**
     * Method to benchmark search operations in B-tree and sequential search
     */
    private void benchmarkSearchAndSequential() throws IOException {
        List<Long> btreeSearchTimes = new ArrayList<>();
        List<Long> sequentialSearchTimes = new ArrayList<>();

        // Benchmark B-tree search
        for (String key : benchmarkKeys) {
            long startTime = System.nanoTime();
            bplustree.search(key);
            long endTime = System.nanoTime();
            btreeSearchTimes.add(endTime - startTime);
        }

        // Benchmark sequential search
        String filePath = "src/main/resources/DATA_100000.csv";
        for (String key : benchmarkKeys) {
            long startTime = System.nanoTime();
            sequentialSearch(key, filePath);
            long endTime = System.nanoTime();
            sequentialSearchTimes.add(endTime - startTime);
        }

        // Save results to CSV
        saveResultsToCSV(benchmarkKeys, btreeSearchTimes, sequentialSearchTimes);
    }
    /**
     * Method for sequential search
     */
    private void sequentialSearch(String key, String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equals(key)) {
                    return;
                }
            }
        }
    }
    /**
     * Method to save results to CSV
     */

    private void saveResultsToCSV(List<String> keys, List<Long> btreeTimes, List<Long> sequentialTimes) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("benchmark_results.csv"))) {
            writer.println("Key;B-Tree Search Time (ns);B-Tree Search Time (s);Sequential Search Time (ns);Sequential Search Time (s)");
            for (int i = 0; i < keys.size(); i++) {
                double btreeTimeInSeconds = btreeTimes.get(i) / 1_000_000_000.0; // Convert nanoseconds to seconds
                double sequentialTimeInSeconds = sequentialTimes.get(i) / 1_000_000_000.0; // Similarly for sequential search
                String formattedBtreeTime = String.format("%.9f", btreeTimeInSeconds); // Format as floating-point number
                String formattedSequentialTime = String.format("%.9f", sequentialTimeInSeconds); // Format as floating-point number
                writer.printf("%s;%d;%s;%d;%s\n", keys.get(i), btreeTimes.get(i), formattedBtreeTime, sequentialTimes.get(i), formattedSequentialTime);
            }
        }
    }




    /**
     * This endpoint is used to render the tree in the browser, with a json format
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/")
    public String index() throws JsonProcessingException {
        return renderView(bplustree);
    }

    /**
     * This method is used to illustrate the "add" process
     * /!\ take care to change the data type if you change the tree key and value type
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/add")
    public String add() throws JsonProcessingException {
        importDataFromCSV("src/main/resources/DATA_100000.csv");

        return renderView(bplustree);
    }

    /**
     * This method is used to illustrate the "delete"
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/delete")
    public String delete() throws JsonProcessingException {
        bplustree = new Btree<>();
        index = 0;
        return renderView(bplustree);
    }
    @GetMapping("/benchmark")
    public String benchmark() {
        try {
            benchmarkSearchAndSequential();
            return "Benchmark completed. Check the CSV file for results.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error during benchmark.";
        }
    }

    /**
     * This method is used to render the tree in the browser, with a json format
     * @param btree
     * @return
     * @throws JsonProcessingException
     */
    public String renderView(Btree btree) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        String result = mapper
                .writerWithView(Views.Public.class)
                .writeValueAsString(btree);

        return result;
    }

}
