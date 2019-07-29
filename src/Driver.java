import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Driver {
    public static void main(String[] args){
        SchedulingAlgorithm scheduler = new SchedulingAlgorithm(populateProcessList(), menu());
    }

    public static int menu(){
        int algorithmNumber = 0;
        Boolean invalidChoice = true;
        System.out.println("OS Scheduler Simulator");

        do {
            try {
                System.out.println("Select the corresponding number for the selected algorithm:\n" +
                        "1. First-Come-First-Server\n" +
                        "2. Shortest-Job-First\n" +
                        "3. Round-Robin, Time Quantum: 20\n" +
                        "4. Round-Robin, Time Quantum: 40\n" +
                        "5. Lottery, Time Quantum: 40");
                System.out.println("Selection: ");
                Scanner reader = new Scanner(System.in);
                algorithmNumber = reader.nextInt();
                if(algorithmNumber > 0 && algorithmNumber < 6){
                    invalidChoice = false;
                }
                else {
                    System.out.println("Invalid Choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }while(invalidChoice);

        return algorithmNumber;
    }

    public static ArrayList populateProcessList(){
        String folder = "src/test_data/testdata";
        String fileType = ".txt";
        String path;
        File openFile;
        Scanner read;
        ArrayList<ArrayList<Process>> processList = new ArrayList<>(4);
        ArrayList<Process> testData1 = new ArrayList<>();
        ArrayList<Process> testData2 = new ArrayList<>();
        ArrayList<Process> testData3 = new ArrayList<>();
        ArrayList<Process> testData4 = new ArrayList<>();


        try {
            for (int i = 1; i < 5; i++) {
                String fileNumber = Integer.toString(i);
                path = folder + fileNumber + fileType;
                openFile = new File(path);
                read = new Scanner(openFile);

                processList.add(new ArrayList<>());

                while (read.hasNextInt()) {
                    processList.get(i - 1).add(new Process(read.nextInt(), read.nextInt(), read.nextInt()));
                }
                read.close();
            }
        }
        catch(IOException error){
            System.err.println("Caught IOException: " + error.getMessage());
        }

        return processList;
    }
}
