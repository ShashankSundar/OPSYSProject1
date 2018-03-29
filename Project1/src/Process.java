import java.util.ArrayList;

public class Process implements Comparable<Process>{
	
	// PRIVATE INSTANCE DATA
	private String id;
	private int arrivalTime;
	private int originalBurstTime;
	private int originalIOTime;
	private int originalNumBursts;
	private int remainingBurstTime;
	private int remainingIOTime;
	private int remainingNumBursts;
	private int waitTime;
	
	// CONSTRUCTOR
	public Process(String procId, int arrival, int burstTime, int numBursts, int io) {
		id = procId;
		arrivalTime = arrival;
		originalBurstTime = burstTime;
		remainingBurstTime = burstTime;
		originalNumBursts = numBursts;
		remainingNumBursts = numBursts;
		originalIOTime = io;
		remainingIOTime = io;
		waitTime = 0;
	}
	
	// COPY CONSTRUCTOR
	public Process(Process copy) {
		id = copy.getID();
		arrivalTime = copy.getArrivalTime();
		originalBurstTime = copy.getOriginalBurstTime();
		remainingBurstTime = copy.getRemainingBurstTime();
		originalNumBursts = copy.getOriginalBursts();
		remainingNumBursts = copy.getRemainingBursts();
		originalIOTime = copy.getOriginalIOTime();
		remainingIOTime = copy.getRemainingIOTime();
		waitTime = copy.getWaitTime();
	}
	
	// RESETS
	public void reset() {
		remainingBurstTime = originalBurstTime;
		remainingNumBursts= originalNumBursts;
		remainingIOTime = originalIOTime;
		waitTime = 0;
	}
	public void resetBurstTime() {
		remainingBurstTime = originalBurstTime;
	}
	public void resetIOTime() {
		remainingIOTime = originalIOTime;
	}
	public void resetWaitTime() {
		waitTime = 0;
	}
	
	// DECREMENTS
	public void decrementIO() {
		remainingIOTime--;
	}
	public void decrementBurst() {
		remainingBurstTime--;
	}
	public void decrementNumBursts() {
		remainingNumBursts--;
	}
	
	// INCREMENT
	public void incrementWait() {
		waitTime++;
	}
	
	// GETTERS
	public int getArrivalTime() { 
		return arrivalTime;
	}
	public String getID() { 
		return id;
	}
	public int getRemainingIOTime() {
		return remainingIOTime;
	}
	public int getOriginalIOTime() {
		return originalIOTime;
	}
	public int getRemainingBurstTime() {
		return remainingBurstTime;
	}
	public int getOriginalBurstTime() {
		return originalBurstTime;
	}
	public int getOriginalBursts() {
		return originalNumBursts;
	}
	public int getRemainingBursts() {
		return remainingNumBursts;
	}
	public int getWaitTime() {
		return waitTime;
	}

	@Override
	public int compareTo(Process p) {
        // Compare the two processes based on remaining burst time
        if (this.getRemainingBurstTime() < p.getRemainingBurstTime()){
            return -1;
        }
        if (this.getRemainingBurstTime() > p.getRemainingBurstTime()){
            return 1;
        }
        return 0;
	}
	
}