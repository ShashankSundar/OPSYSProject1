
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
	
	void reset() {
		remainingBurstTime = originalBurstTime;
		remainingNumBursts= originalNumBursts;
		remainingIOTime = originalIOTime;
	}
	
	
}

