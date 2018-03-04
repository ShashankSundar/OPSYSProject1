import java.util.ArrayList;

public class Process {
	
	private String id;
	private int arrivalTime;
	private int originalBurstTime;
	private int originalIOTime;
	private int originalNumBursts;
	private int remainingBurstTime;
	private int remainingIOTime;
	private int remainingNumBursts;
	
	public Process(String procId, int arrival, int burstTime, int numBursts, int io) {
		id = procId;
		arrivalTime = arrival;
		originalBurstTime = burstTime;
		remainingBurstTime = burstTime;
		originalNumBursts = numBursts;
		remainingNumBursts = numBursts;
		originalIOTime = io;
		remainingIOTime = io;
	}
	
	public Process(Process copy) {
		id = copy.getID();
		arrivalTime = copy.getArrivalTime();
		originalBurstTime = copy.getOriginalBurstTime();
		remainingBurstTime = copy.getRemainingBurstTime();
		originalNumBursts = copy.getOriginalBursts();
		remainingNumBursts = copy.getRemainingBursts();
		originalIOTime = copy.getOriginalIOTime();
		remainingIOTime = copy.getRemainingIOTime();
	}
	
	public void reset() {
		remainingBurstTime = originalBurstTime;
		remainingNumBursts= originalNumBursts;
		remainingIOTime = originalIOTime;
	}
	
	public void resetBurstTime() {
		remainingBurstTime = originalBurstTime;
	}
	public void resetIOTime() {
		remainingIOTime = originalIOTime;
	}
	
	public int getArrivalTime() { 
		return arrivalTime;
	}
	public String getID() { 
		return id;
	}
	public int getRemainingIOTime() {
		return remainingIOTime;
	}
	public void decrementIO() {
		remainingIOTime--;
	}
	public int getRemainingBurstTime() {
		return remainingBurstTime;
	}
	public void decrementBurst() {
		remainingBurstTime--;
	}
	public int getRemainingBursts() {
		return remainingNumBursts;
	}
	public void decrementNumBursts() {
		remainingNumBursts--;
	}
	
	
	public int getOriginalIOTime() {
		return originalIOTime;
	}
	public int getOriginalBurstTime() {
		return originalBurstTime;
	}
	public int getOriginalBursts() {
		return originalNumBursts;
	}
	
}

