import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main {
    private static int numRecords = 0;
    private static BPlusTree bplustree = new BPlusTree();

    public static void main(String[] arg) {
        int choice;
        Scanner inputScanner = new Scanner(System.in);

        // User input for method selection
        do {
            System.out.println("Choose Method for Building B+ Tree:");
            System.out.println("1. Iterative Insertion");
            System.out.println("2. Bulk Loading");
            choice = inputScanner.nextInt();
        } while (!(choice == 1 || choice == 2));

        // Define the file path
        String filePath = "games.txt";
        ArrayList<Map.Entry<Float, Address>> listOfAddressPairs = new ArrayList<>();

        // Use try-with-resources for Disk and Scanner
        try (Disk disk = new Disk("database.bin");
             Scanner scanner = new Scanner(new File(filePath))) {
            
            scanner.nextLine(); // Skip header line

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split("\t");

                if (values.length == 9 && allValuesPresent(values)) {
                    try {
                        // Parse and create a new record
                        int date = Integer.parseInt(values[0].replace("/", ""));
                        int team_id_home = Integer.parseInt(values[1]);
                        short pts_home = Short.parseShort(values[2]);
                        float fg_pct_home = Float.parseFloat(values[3]);
                        float ft_pct_home = Float.parseFloat(values[4]);
                        float fg3_pct_home = Float.parseFloat(values[5]);
                        byte ast_home = Byte.parseByte(values[6]);
                        byte reb_home = Byte.parseByte(values[7]);
                        byte home_team_wins = Byte.parseByte(values[8]);

                        Record newRecord = new Record(date, team_id_home, pts_home, fg_pct_home, ft_pct_home, fg3_pct_home,
                                ast_home, reb_home, home_team_wins);
                        Address address = disk.insertRecord(newRecord);

                        // Insert or bulk load based on user choice
                        if (choice == 1) {
                            bplustree.insertRecord(address);
                        } else {
                            listOfAddressPairs.add(new AbstractMap.SimpleEntry<>(fg_pct_home, address));
                        }
                        numRecords++;
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    }
                }
            }

            // Process after file reading
            if (choice == 2) {
                // Sort and bulk load
                listOfAddressPairs.sort(Comparator.comparing(Map.Entry::getKey));
                ArrayList<Address> sortedAddresses = (ArrayList<Address>) listOfAddressPairs.stream()
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
                bplustree.bulkLoad(sortedAddresses, numRecords);
            }

            // Execute tasks
            lines();
            task1(disk);
            lines();
            task2();
            lines();
            task3(disk);
            lines();

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            inputScanner.close(); // Close the input scanner here
        }
    }

    private static boolean allValuesPresent(String[] values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void lines() {
        System.out.println("========================================================================");
    }

    public static void task1(Disk disk) {
        System.out.println("TASK 1");
        System.out.println("Storing data on disk...");
        System.out.println("Size of a record: " + Block.RECORD_SIZE);
        System.out.println("Number of records: " + numRecords);
        System.out.println("Number of records stored in a block: " + Block.MAX_NUM_RECORDS);
        System.out.println("Number of blocks for storing data: " + disk.getNumBlocks());
    }

    public static void task2() {
        System.out.println("TASK 2");
        System.out.println("Building B+ tree on attribute 'FG_PCT_home'...");
        System.out.println("The parameter n of the B+ tree is: " + Node.n);
        System.out.println("The number of nodes of the B+ tree is: " + bplustree.countNodes());
        System.out.println("The number of levels of the B+ tree is: " + bplustree.countLevels());
        System.out.println("The content of the root node (only the keys) is:");
        bplustree.rootNodeContent();
    }

    public static void task3(Disk disk) {
        System.out.println("Task 3");
        System.out.println("Retrieving records with 'FG_PCT_home' between 0.5 and 0.8 inclusively...");
        float lowerKey = 0.5f;
        float upperKey = 0.8f;

        long start = System.nanoTime();
        System.out.printf("Average 'FG3_PCT_home' of the retrieved records: %.4f\n",
                bplustree.rangeQuery(lowerKey, upperKey));
        long end = System.nanoTime();
        System.out.println("Running time of retrieval process in nanoseconds: " + (end - start));

        System.out.println();
        System.out.println("BRUTE FORCE SCANNING");
        Set<Block> blocks = disk.getBlockSet();
        int blocksAccessed = 0;
        int numRecords = 0;
        float resultSum = 0;

        start = System.nanoTime();
        for (Block block : blocks) {
            for (Record record : block.getRecords()) {
                if (record != null && record.getFg_pct_home() >= lowerKey && record.getFg_pct_home() <= upperKey) {
                    numRecords++; // Increment to count valid records
                    resultSum += record.getFg3_pct_home();
                }
            }
            blocksAccessed++;
        }
        end = System.nanoTime();

        System.out.println("Num records found: " + numRecords);
        System.out.println("Number of blocks accessed by brute-force linear scan method: " + blocksAccessed);
        if (numRecords > 0) {
            System.out.printf("Average 'FG3_PCT_home' of the retrieved records: %.4f\n", resultSum / numRecords);
        } else {
            System.out.println("No records found in the specified range.");
        }
        System.out.println("Running time of brute force scan in nanoseconds: " + (end - start));
    }
}