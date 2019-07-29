import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Comparator;

public class SchedulingAlgorithm{
    private ArrayList<ArrayList<Process>> processList;
    private int contextSwitchCost = 3;
    private FileWriter writeToOutput;

    public SchedulingAlgorithm(ArrayList<ArrayList<Process>> processList, int algorithmNumber){
        this.processList = processList;
        switch(algorithmNumber){
            case 1:
                FirstComeFirstServe();
                break;
            case 2:
                ShortestJobFirst();
                break;
            case 3:
                RoundRobin(20);
                break;
            case 4:
                RoundRobin(40);
                break;
            case 5:
                Lottery(40);
                break;
        }
    }

    public void FirstComeFirstServe(){
        for(int i = 0; i < processList.size(); i++){
            initiateOutput("FCFS", "batch", 0, Integer.toString(i + 1), i);
        }
    }

    public void ShortestJobFirst() {
        for (int i = 0; i < processList.size(); i++){
            processList.get(i).sort(Comparator.comparing(Process::getStartBurstTime));
            initiateOutput("SJF", "batch", 0, Integer.toString(i + 1), i);
        }
    }

    public void RoundRobin(int timeQuantum){
        for (int i = 0; i < processList.size(); i++) {
            initiateOutput("RR" + timeQuantum, "roundRobin", timeQuantum, Integer.toString(i + 1), i);
        }
    }

    public void Lottery(int timeQuantum){
        for(int i = 0; i < processList.size(); i++) {
            processList.get(i).sort(Comparator.comparing(Process::getPriority));
            initiateOutput("Lottery" + timeQuantum, "lottery", timeQuantum, Integer.toString(i + 1), i);
        }
    }

    public void initiateOutput(String algorithmName, String flag, int timeQuantum, String fileNumber, int testData){
        String folderPath = "src/output_data";
        File outputFolder = new File(folderPath);
        if(!outputFolder.exists()){
            outputFolder.mkdir();
        }
        try {
            writeToOutput = new FileWriter(new File( "src/output_data", createOutputPath(algorithmName,fileNumber)));
            consumeTime(writeToOutput, flag, timeQuantum, testData);
        }catch(Exception e){
            System.out.println("Exception Error: " + e);
        }
    }
    public void consumeTime(FileWriter writeToOutput, String flag, int timeQuantum, int testData) throws Exception{
        int ticket, endBurstTime = 0, position = 0, prioritySum = 0, winner = 0, completeTime = 0,
                time = 0, turnAroundTime = 0, cpuTime = 0;
        float numOfProcesses = processList.get(testData).size();
        Boolean loopCondition;
        StringBuilder output = new StringBuilder();

        CircularLinkedList<Process> processCLL = null;
        Iterator<Process> it = null;
        Process currentProcess;
        Random select = new Random();

        output.append("CPUTIME,PID,STARTBURST,ENDBURST,COMPLETED\n");

        if(flag.equals("roundRobin")){
            processCLL = new CircularLinkedList<>();
            for(int i = 0; i < processList.get(testData).size(); i++){
                processCLL.add(processList.get(testData).get(i));
            }
            it = processCLL.iterator();
            loopCondition = it.hasNext();
        }
        else {
            loopCondition = !processList.get(testData).isEmpty();
        }

        while(loopCondition){
            if(flag.equals("roundRobin")) {
                currentProcess = it.next();

                output.append(cpuTime + "," + currentProcess.getPID() + "," + currentProcess.getStartBurstTime() + ",");

                if (currentProcess.getStartBurstTime() > timeQuantum) {
                    endBurstTime = currentProcess.getStartBurstTime() - timeQuantum;
                    output.append(endBurstTime);
                    time = timeQuantum;
                    output.append(",0");
                } else {
                    time = currentProcess.getStartBurstTime();
                    endBurstTime = 0;
                    completeTime = cpuTime + time;
                    turnAroundTime += completeTime;
                    output.append(endBurstTime + "," +  completeTime);
                }

                currentProcess.changeStartBurstTime(endBurstTime);
                if (currentProcess.getStartBurstTime() == 0) {
                    processCLL.remove(position % processCLL.size());
                }

                output.append("\n");
                cpuTime += time + contextSwitchCost;
                position++;
                loopCondition = it.hasNext();
            }
            else {
                if(flag.equals("lottery")){
                    for(int i = 0; i < processList.get(testData).size(); i++){
                        prioritySum += processList.get(testData).get(i).getPriority();
                    }
                    ticket = select.nextInt(prioritySum) + 1;
                    for (int i = 0; i < processList.get(testData).size() && ticket > 0; i++) {
                        ticket -= processList.get(testData).get(i).getPriority();
                        winner = i;
                    }
                    position = winner;
                }
                output.append(cpuTime + "," + processList.get(testData).get(position).getPID() + "," +
                        processList.get(testData).get(position).getStartBurstTime() + ",");

                if(flag.equals("batch")) {
                    output.append(endBurstTime);
                    time = processList.get(testData).get(position).getStartBurstTime() - endBurstTime;
                    turnAroundTime += time + cpuTime;
                    completeTime = time + cpuTime;
                    output.append("," + completeTime);
                    processList.get(testData).remove(position);
                }
                else{
                    if(processList.get(testData).get(winner).getStartBurstTime() > timeQuantum) {
                        endBurstTime = processList.get(testData).get(winner).getStartBurstTime() - timeQuantum;
                        output.append(endBurstTime + ",0");
                        time = timeQuantum;
                    }
                    else {
                        time =  processList.get(testData).get(winner).getStartBurstTime();
                        endBurstTime = 0;
                        completeTime = cpuTime + time;
                        turnAroundTime += completeTime;
                        output.append(endBurstTime + "," + completeTime);
                    }

                    processList.get(testData).get(winner).changeStartBurstTime(endBurstTime);
                    if(processList.get(testData).get(winner).getStartBurstTime() == 0){
                        processList.get(testData).remove(winner);
                    }
                }
                output.append("\n");
                cpuTime += time + contextSwitchCost;
                loopCondition = !processList.get(testData).isEmpty();
            }
        }
        output.append("TURNAROUND, AVG \n" +
                turnAroundTime + "," + String.format("%.2f",turnAroundTime/numOfProcesses));
        writeToOutput.write(output.toString());
        writeToOutput.close();
    }

    public String createOutputPath(String algorithmName, String testNumber){
        return algorithmName + "_testdata" + testNumber + "_OutputData" + ".csv";
    }
}
