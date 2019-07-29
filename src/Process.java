public class Process{
    private int pid, startBurstTime, priority;

    public Process(int pid, int burst_time, int priority){
        this.pid = pid;
        startBurstTime = burst_time;
        this.priority = priority;
    }

    public void changeStartBurstTime(int delta){
        startBurstTime = delta;
    }

    public int getPriority(){
        return priority;
    }

    public int getPID(){
        return pid;
    }

    public int getStartBurstTime(){
        return startBurstTime;
    }

}
