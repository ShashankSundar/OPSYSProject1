import java.io.*;
import java.util.*;
import java.lang.Math;

public class Project1 {
	
	final static int T_CS = 8;
	final static int T_SLICE = 80;
	
	public static void main(String[] args) throws Exception{
		File file = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(file));

		ArrayList<Process> processes = new ArrayList<>();
		
		String st;
		while ((st = br.readLine()) != null) {
			if (st.indexOf('#') != -1)
				continue;
			int newMark = 0;
			int oldMark = 0;
			
			newMark = st.indexOf('|', oldMark);
			String id = st.substring(oldMark, newMark);
			
			oldMark = newMark+1;
			newMark = st.indexOf('|', oldMark);	
			int arrival = Integer.parseInt(st.substring(oldMark, newMark));
			
			oldMark = newMark+1;
			newMark = st.indexOf('|', oldMark);
			int burstTime = Integer.parseInt(st.substring(oldMark, newMark));
			
			oldMark = newMark+1;
			newMark = st.indexOf('|', oldMark);
			int numBursts = Integer.parseInt(st.substring(oldMark, newMark));
			
			oldMark = newMark+1;
			int ioTime = Integer.parseInt(st.substring(oldMark));
			
			processes.add(new Process(id, arrival, burstTime, numBursts, ioTime));
		}
		
		fcfs(processes);
	}

	private static void printQueue(ArrayList<Process> queue) {
		System.out.print(" [Q");
		if (queue.size() == 0) {
			System.out.print(" <empty>");
		}
		else
			for (int j = 0; j < queue.size(); j++) {
				System.out.print(" "+queue.get(j).getID());
			}
		System.out.println("]");
	}
	
	private static void waitingProc(ArrayList<Process> queue) {
		for (int j = 0; j < queue.size(); j++) {
			queue.get(j).incrementWait();
		} 
	}
	
	private static void ioHandle(int time, ArrayList<Process> queue, ArrayList<Process> ioBlock) {
		for(int i = 0; i < ioBlock.size(); i++) {
			ioBlock.get(i).decrementIO();
			if (ioBlock.get(i).getRemainingIOTime() == 0) {
				Process temp = ioBlock.remove(i);
				temp.resetIOTime();
				queue.add(temp);
				System.out.print("time "+time+"ms: Process "+temp.getID()+" completed I/0; added to ready queue");
				printQueue(queue);
				i--;
			}
		}
	}
	
	private static void fcfs(ArrayList<Process> processes) {
		double avgWaitTime = 0.0;
		double avgBurstTime = 0.0;
		double avgTurnaroundTime = 0.0;
		int numContextSwitches = 0;
		int numPreemptions = 0; //constant: FCFS has no preemptions
		int totalBursts = 0;
		int n = processes.size();
		int time = 0;
		ArrayList<Process> queue = new ArrayList<>();
		ArrayList<Process> ioBlock = new ArrayList<>();
		Process currentProcess = null;
		boolean done = false;
		
		System.out.println("time "+time+"ms: Simulator started for FCFS [Q <empty>]");
		
		while (!done) {
			// processes arrive
			for(int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getArrivalTime() == time) {
					avgBurstTime += processes.get(i).getOriginalBurstTime() * processes.get(i).getOriginalBursts();
					totalBursts += processes.get(i).getOriginalBursts();
					queue.add(processes.get(i));
					System.out.print("time "+time+"ms: Process "+processes.get(i).getID()+" arrived and added to ready queue");
					printQueue(queue);
				}
			}
			
			// context switch in to start process
			if (currentProcess == null && queue.size() > 0) {
				currentProcess = queue.remove(0);
				//context switch
				for (int i = 0; i < T_CS/2; i++) {
					time++;
					ioHandle(time, queue, ioBlock);
					waitingProc(queue);
				}
				numContextSwitches++;
				System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" started using the CPU");
				printQueue(queue);	
				continue;
			}
			
			// process burst finishes
			if (currentProcess != null && currentProcess.getRemainingBurstTime() -1 == 0) {
				time++;
				currentProcess.decrementBurst();
				currentProcess.decrementNumBursts();
				currentProcess.resetBurstTime();
				if (currentProcess.getRemainingBursts() != 0) {
					if (currentProcess.getRemainingBursts() == 1) 
						System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" completed a CPU burst; "+currentProcess.getRemainingBursts()
								+ " burst to go");
					else
						System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" completed a CPU burst; "+currentProcess.getRemainingBursts()
						+ " bursts to go");
					printQueue(queue);
					System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" switching out of CPU; will block on I/O until time "
							+ (time+currentProcess.getRemainingIOTime() + T_CS/2)+"ms");
					printQueue(queue);
					ioHandle(time, queue, ioBlock);
					//context switch
					for (int i = 0; i < T_CS/2; i++) {
						time++;
						ioHandle(time, queue, ioBlock);
						waitingProc(queue);
					}
					
					Process temp = new Process(currentProcess);
					ioBlock.add(temp);
					currentProcess = null;
					continue;
				}
				
				System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" terminated");
				printQueue(queue);
				avgWaitTime += currentProcess.getWaitTime();
				avgTurnaroundTime += currentProcess.getWaitTime() + (currentProcess.getOriginalBurstTime() * currentProcess.getOriginalBursts());
				n--;
				ioHandle(time, queue, ioBlock);
				currentProcess = null;
				for (int i = 0; i < T_CS/2; i++) {
					time++;
					ioHandle(time, queue, ioBlock);
					waitingProc(queue);
				}
				continue;
			}
			
			// all processes done
			if (n == 0) 
				break;
						
			time++;
			
			// I/0
			ioHandle(time, queue, ioBlock);
			
			// Waiting
			waitingProc(queue);
			
			
			// decrement running process
			if (currentProcess != null) {
				currentProcess.decrementBurst();
			}
				
		}
		
		
		avgBurstTime = avgBurstTime/totalBursts;
		avgBurstTime = (double)Math.round(avgBurstTime * 100d) / 100d;
		avgWaitTime = avgWaitTime/totalBursts;
		avgWaitTime = (double)Math.round(avgWaitTime * 100d) / 100d;
		avgTurnaroundTime = (avgTurnaroundTime+T_CS*numContextSwitches)/totalBursts;
		avgTurnaroundTime = (double)Math.round(avgTurnaroundTime * 100d) / 100d;
		System.out.println("time "+time+"ms: Simulator ended for FCFS");
		System.out.println("-- average CPU burst time: "+avgBurstTime+" ms");
		System.out.println("-- average wait time: "+avgWaitTime+" ms");
		System.out.println("-- average turnaround time: "+avgTurnaroundTime+" ms");
		System.out.println("-- total number of context switches:: "+numContextSwitches);
		System.out.println("-- total number of preemptions: "+numPreemptions);
	}
	
}

