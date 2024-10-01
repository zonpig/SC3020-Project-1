import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;

public class Main {
    private static Disk disk = new Disk();
    private static int numRecords = 0;
    private static BPlusTree bplustree = new BPlusTree();
    public static void main(String[] arg) {

        // Option to 
        int choice;
        Scanner inputScanner = new Scanner(System.in);
        do {
            System.out.println("Choose Method for Building B+ Tree:");
            System.out.println("1. Iterative Insertion");
            System.out.println("2. Bulk Loading");
            choice = inputScanner.nextInt();
        } while (!(choice == 1 || choice == 2));

        inputScanner.close();

        // Define the file path
        String filePath = "games.txt";
         
        // Create List of Addresses
        ArrayList<Address> listOfAddresses = new ArrayList<Address>();

        try {
            Scanner scanner = new Scanner(new File(filePath));
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                // Split the line into individual values using tab as the delimiter
                String[] values = line.split("\t");

                if (!values[0].equals("") && !values[1].equals("") && !values[2].equals("")
                        && !values[3].equals("") && !values[4].equals("") && !values[5].equals("")
                        && !values[6].equals("") && !values[7].equals("") && !values[8].equals("")) { // ignore rows with empty values

                    // Extract and store individual values in variables
                    String dateStr = values[0].replace("/", ""); // Remove slashes
                    int date = Integer.parseInt(dateStr);
                    int team_id_home = Integer.parseInt(values[1]);
                    short pts_home = (short)Integer.parseInt(values[2]);
                    float fg_pct_home = Float.parseFloat(values[3]);
                    float ft_pct_home = Float.parseFloat(values[4]);
                    float fg3_pct_home = Float.parseFloat(values[5]);
                    byte ast_home = (byte)Integer.parseInt(values[6]);
                    byte reb_home = (byte)Integer.parseInt(values[7]);
                    byte home_team_wins = (byte)Integer.parseInt(values[8]);

                    Record newRecord = new Record(date, team_id_home, pts_home, fg_pct_home, ft_pct_home, fg3_pct_home, ast_home, reb_home, home_team_wins);
                    Address address = disk.insertRecord(newRecord);
                    if (choice == 1) {
                        bplustree.insertRecord(address);
                    }
                    else if (choice == 2) {
                        listOfAddresses.add(address);
                    }
                    numRecords++;
                }
            }

            // Close the scanner
            scanner.close();
            System.out.println("Number of records: " + numRecords);
            if (choice ==2){
                bplustree.bulkLoad(listOfAddresses,numRecords);
            }

            lines();
            task1();
            lines();
            task2();
            lines();
            task3();
            lines();

        } catch (FileNotFoundException | NumberFormatException e) {
            System.err.println("File not found: " + filePath);

            System.out.println(e.getMessage());
            System.out.println("record number: " + numRecords);
        }
        

    }

    public static void lines() {
        System.out.println("========================================================================");
    }

    public static void task1() {
        System.out.println("TASK 1");
        System.out.println("Storing data on disk...");
        System.out.println("Number of records: " + numRecords);
        System.out.println("Size of a record: " + Block.RECORD_SIZE);
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

    public static void task3() {
        // bplustree.enumerateNodes();
        System.out.println("Task 3");
        System.out.println("Retrieving records with 'FG_PCT_home' between 0.5 and 0.8 inclusively...");
        float lowerKey = 0.5f;
        float upperKey = 0.8f;

        long start = System.nanoTime();
        System.out.printf("Average 'FG3_PCT_home' of the retrieved records: %.4f\n", bplustree.rangeQuery(lowerKey, upperKey));
        long end = System.nanoTime();
        System.out.println("Running time of retrieval process in nanoseconds: "+ (end-start));

        System.out.println();
        System.out.println("BRUTE FORCE SCANNING");
        Set<Block> blocks = disk.getBlockSet();
        int blocksAccessed = 0;
        int numRecords = 0;
        float resultSum = 0;

        start = System.nanoTime();
        for(Block block : blocks) {
            for(Record record : block.getRecords()) {
                if(record != null && record.getFg_pct_home() >= lowerKey && record.getFg_pct_home() <= upperKey) {
                    numRecords++; // numRecords++ to simulate reading the record
                    resultSum += record.getFg3_pct_home();
                }
            }
            blocksAccessed++;
        }
        end = System.nanoTime();

        System.out.println("Num records found: " + numRecords);
        System.out.println("Number of blocks accessed by brute-force linear scan method: "+ blocksAccessed);
        System.out.printf("Average 'FG3_PCT_home' of the retrieved records: %.4f\n", resultSum/numRecords);
        System.out.println("Running time of brute force scan in nanoseconds: "+ (end-start));

    }
    
}
