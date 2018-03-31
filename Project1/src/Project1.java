// *********************************************************************************
//	Shashank Sundar: sundas6
//	Jonathan Cheng: chengj6
//	Kris Whelan: whelak2
// *********************************************************************************

import java.io.*;
import java.util.*;
import java.lang.Math;
import java.text.DecimalFormat;

public class Project1 {
	
	final static int T_CS = 8;
	final static int T_SLICE = 80;
	public static PriorityQueue<Process> queue = new PriorityQueue<Process>();
	public static Process currentProcess = null;
	public static int numPreemptions = 0;
	public static int cswitches = 0;
	final static DecimalFormat df = new DecimalFormat("#0.00");
	static int time = 0;
	
	public static void main(String[] args) throws Exception{
		// Error handling
		if(args.length > 3 || args.length <= 1) {
			System.err.println("ERROR: Invalid arguments\nUSAGE: ./a.out <input-file> <stats-output-file> [<rr-add>]");
		}

		// Read File
		File file = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(file));

		// Write File
		File outFile = new File(args[1]);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		
		if (!file.exists()) {
			System.err.println("ERROR: Invalid input file format");
		}
		
		boolean beg = false;
		if (args.length == 3) {
			String whatEnd = args[2];
			if (whatEnd.equals("BEGINNING")){
				beg = true;
			}
		}
				

		ArrayList<Process> processes = new ArrayList<>();
		
		String st;
		while ((st = br.readLine()) != null) {
			if (st.indexOf('#') != -1 || st.equals(""))
				continue;
			int newMark = 0;
			int oldMark = 0;
			
			newMark = st.indexOf('|');
			String id = st.substring(oldMark, newMark);
			//System.out.println(id);
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
		
		fcfs(processes, writer);
		for(int i = 0; i < processes.size(); i++) {
			processes.get(i).reset();
		}
		srt(processes,writer);
		
		for(int i = 0; i < processes.size(); i++) {
			processes.get(i).reset();
		}
		rr(processes, writer, beg);
	
		writer.close();
		
	}

	
// FCFS STUFF

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
				System.out.print("time "+time+"ms: Process "+temp.getID()+" completed I/O; added to ready queue");
				printQueue(queue);
				i--;
			}
		}
	}
	
	private static void arrival(ArrayList<Process> processes, ArrayList<Process> queue, int time) {
		for(int i = 0; i < processes.size(); i++) {
			if (processes.get(i).getArrivalTime() == time) {
				queue.add(processes.get(i));
				System.out.print("time "+time+"ms: Process "+processes.get(i).getID()+" arrived and added to ready queue");
				printQueue(queue);
			}
		}
	}
	
	private static void fcfs(ArrayList<Process> processes, BufferedWriter writer) {
		double avgWaitTime = 0.0;
		double avgBurstTime = 0.0;
		double avgTurnaroundTime = 0.0;
		int numContextSwitches = 0;
		int numPreemptions = 0; //constant: FCFS has no preemptions
		int totalBursts = 0;
		int n = processes.size();
		time = 0;
		ArrayList<Process> queue = new ArrayList<>();
		ArrayList<Process> ioBlock = new ArrayList<>();
		currentProcess = null;
		
		System.out.println("time "+time+"ms: Simulator started for FCFS [Q <empty>]");
		
		while (true) {
			// processes arrive
			arrival(processes, queue, time);
			
			// context switch in to start process
			if (currentProcess == null && queue.size() > 0) {
				currentProcess = queue.remove(0);
				numContextSwitches++;
				for (int i = 0; i < T_CS/2; i++) {
					time++;
					waitingProc(queue);
					ioHandle(time, queue, ioBlock);
					arrival(processes, queue, time);
				}
				System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" started using the CPU");
				printQueue(queue);	
				continue;
			}
		
			// process burst finishes
			if (currentProcess != null && currentProcess.getRemainingBurstTime() -1 == 0) {
				time++;
				waitingProc(queue);
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
					arrival(processes, queue, time);
					//context switch
					for (int i = 0; i < T_CS/2; i++) {
						time++;
						waitingProc(queue);
						ioHandle(time, queue, ioBlock);
						arrival(processes, queue, time);
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
				arrival(processes, queue, time);
				currentProcess = null;
				for (int i = 0; i < T_CS/2; i++) {
					time++;
					waitingProc(queue);
					ioHandle(time, queue, ioBlock);
					arrival(processes, queue, time);
				}
				continue;
			}
			
			// all processes done
			if (n == 0) 
				break;
						
			time++;
			
			// Waiting
			waitingProc(queue);
			// I/0
			ioHandle(time, queue, ioBlock);
			
			
			// decrement running process
			if (currentProcess != null) {
				currentProcess.decrementBurst();
			}
				
		}
		
		
		for (int i = 0; i < processes.size(); i++) {
			avgBurstTime += processes.get(i).getOriginalBurstTime() * processes.get(i).getOriginalBursts();
			totalBursts += processes.get(i).getOriginalBursts();
		}
		System.out.println("time "+time+"ms: Simulator ended for FCFS\n");
		avgBurstTime = avgBurstTime/totalBursts;
		avgWaitTime = avgWaitTime/totalBursts;
		avgTurnaroundTime = (avgTurnaroundTime+T_CS*numContextSwitches)/totalBursts;
		try {
			writer.write("Algorithm FCFS\n");
			writer.flush();
			writer.write("-- average CPU burst time: "+df.format(avgBurstTime)+" ms\n");
			writer.flush();
			writer.write("-- average wait time: "+df.format(avgWaitTime)+" ms\n");
			writer.flush();
			writer.write("-- average turnaround time: "+df.format(avgTurnaroundTime)+" ms\n");
			writer.flush();
			writer.write("-- total number of context switches: "+numContextSwitches+"\n");
			writer.flush();
			writer.write("-- total number of preemptions: "+numPreemptions+"\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	// SRT STUFF

	
		// For priority queue
		private static void printPQueue() {
			System.out.print(" [Q");
			if (queue.size() == 0) {
				System.out.print(" <empty>");
			}
			else
				for (Process p : queue) {
					System.out.print(" " + p.getID());
				}
			System.out.println("]");
		}
		// For priority queue
		private static void waitingPProc() {
			for(Process p: queue) {
				p.incrementWait();
			} 
		}

		// For priority queue
		private static void ioPHandle(ArrayList<Process> processes,ArrayList<Process> ioBlock, boolean terminated) {
			for(int i = 0; i < ioBlock.size(); i++) {
				ioBlock.get(i).decrementIO();
				if (ioBlock.get(i).getRemainingIOTime() == 0) {
					Process temp = ioBlock.remove(i);
					temp.resetIOTime();
					// Check if it will preempt current process
					if(currentProcess != null) {
						// Before we add it to the Queue we must check for a preemption
						if(temp.getRemainingBurstTime() < currentProcess.getRemainingBurstTime()) {
							// Increment number of preemptions
							numPreemptions++;
							cswitches++;
							// 8 MS TOO LATE
							System.out.print("time "+time+"ms: Process "+temp.getID()+" completed I/O and will preempt "+currentProcess.getID());
							currentProcess.decrementBurst();
							printPQueue();
							i--;
							for(int count = 0; count < T_CS/2; count++) { // Use all of the cs time: half to take it out and half to put the new one in
								time++;
								waitingPProc();
								ioPHandle(processes,ioBlock, terminated);
								srtArrival(processes,ioBlock,terminated);
							}
							queue.add(currentProcess);
							currentProcess = temp;
							for(int count = 0; count < T_CS/2; count++) { // Use all of the cs time: half to take it out and half to put the new one in
								time++;
								waitingPProc();
								ioPHandle(processes,ioBlock, terminated);
								srtArrival(processes,ioBlock,terminated);
							}
							// Account for context switch
//							for(int count = 0; count < T_CS; count++) { // Use all of the cs time: half to take it out and half to put the new one in
//								time++;
//								if (count<T_CS/2)
//									waitingPProc();
//								ioPHandle(processes,ioBlock, terminated);
//								srtArrival(processes,ioBlock,terminated);
//							}
							System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" started using the CPU");
							printPQueue();
							time++;
							waitingPProc();
							ioPHandle(processes,ioBlock, terminated);
							srtArrival(processes,ioBlock,terminated);
						}
						else {
							queue.add(temp);
							System.out.print("time "+time+"ms: Process "+temp.getID()+" completed I/O; added to ready queue");
							printPQueue();
							i--;
						}
					}
					else {
						queue.add(temp);
						System.out.print("time "+time+"ms: Process "+temp.getID()+" completed I/O; added to ready queue");
						printPQueue();
						i--;
					}
				}
			}
		}

		private static void srtArrival(ArrayList<Process> processes, ArrayList<Process> ioBlock,boolean terminated) {
			for(int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getArrivalTime() == time) {
					System.out.print("time "+time+"ms: Process "+processes.get(i).getID()+" arrived and ");

					// Check if it will preempt current process
					if(currentProcess != null) {
						// Before we add it to the Queue we must check for a preemption
						if(processes.get(i).getRemainingBurstTime() < currentProcess.getRemainingBurstTime() && terminated == false) {
							// Increment number of preemptions
							numPreemptions++;
							System.out.print("will preempt " + currentProcess.getID());
							printPQueue();
//							queue.add(new Process(currentProcess));
//							currentProcess = processes.get(i);
							cswitches++;
							for(int count = 0; count < T_CS/2; count++) { // Use all of the cs time: half to take it out and half to put the new one in
								time++;
								waitingPProc();
								ioPHandle(processes,ioBlock, terminated);
								srtArrival(processes,ioBlock,terminated);
							}
							queue.add(new Process(currentProcess));
							currentProcess = processes.get(i);
							for(int count = 0; count < T_CS/2; count++) { // Use all of the cs time: half to take it out and half to put the new one in
								time++;
								waitingPProc();
								ioPHandle(processes,ioBlock, terminated);
								srtArrival(processes,ioBlock,terminated);
							}
							// Account for context switch
//							for(int count = 0; count < T_CS; count++) { // Use all of the cs time: half to take it out and half to pu the new one in
//								time++;
//								waitingPProc();
//								ioPHandle(processes,ioBlock,terminated);
//								srtArrival(processes,ioBlock,terminated);
//							}
							System.out.print("time "+time+"ms: Process "+currentProcess.getID()+
									" started using the CPU");
							//System.out.println("AND WILL USE IT TILL TIME: "+(time+currentProcess.getRemainingBurstTime())+" ms");
							printPQueue();	
							continue;
						}
						else{
							System.out.print("added to ready queue");
							queue.add(processes.get(i));
						}
					}
					else{
						System.out.print("added to ready queue");
						queue.add(processes.get(i));
					}
					printPQueue();
				}
			}
		}
		// Shortest remaining time
		private static void srt(ArrayList<Process> processes, BufferedWriter writer) {
			double avgWaitTime = 0.0;
			double avgBurstTime = 0.0;
			double avgTurnaroundTime = 0.0;
			int totalBursts = 0;
			int n = processes.size();
			time = 0;
			currentProcess = null;
			boolean terminated = false;
			ArrayList<Process> ioBlock = new ArrayList<>();
			System.out.println("time "+time+"ms: Simulator started for SRT [Q <empty>]");

			while (true) {
				terminated = false;
				// A process has gone too far
				if(currentProcess != null)
					if(currentProcess.getRemainingBursts() < 0) {
						break;
					}
				// processes arrive
				srtArrival(processes, ioBlock,terminated);

				// Context switch for the first process
				if(currentProcess == null && queue.size() > 0) {
					currentProcess = queue.poll();
					cswitches++;
					//context switch
					//System.out.println("Context switch for when current process is null(first process) +4");
					for (int i = 0; i < T_CS/2; i++) {
						time++;
						waitingPProc();
						ioPHandle(processes,ioBlock,terminated);
						srtArrival(processes,ioBlock,terminated);
					}
					System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" started using the CPU");
					if(currentProcess.getRemainingBurstTime() != currentProcess.getOriginalBurstTime()) {
						System.out.print(" with " + currentProcess.getRemainingBurstTime() + "ms remaining");
					}
					printPQueue();	
				}

				// process burst finishes
				if (currentProcess != null && currentProcess.getRemainingBurstTime() -1 == 0) {
					// Increment time and check for arrival
					time++;		
					waitingPProc();
					currentProcess.decrementBurst();
					currentProcess.decrementNumBursts();
					currentProcess.resetBurstTime();
					if (currentProcess.getRemainingBursts() != 0) {
						if (currentProcess.getRemainingBursts() == 1) {
							System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" completed a CPU burst; "+currentProcess.getRemainingBursts()
							+ " burst to go");
						}
						else {
							System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" completed a CPU burst; "+currentProcess.getRemainingBursts()
							+ " bursts to go");
						}
						printPQueue();
						System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" switching out of CPU; will block on I/O until time "
								+ (time+currentProcess.getRemainingIOTime() + T_CS/2)+"ms");
						printPQueue();

						Process temp = new Process(currentProcess);
						currentProcess = null;

						ioPHandle(processes,ioBlock,terminated);
						srtArrival(processes, ioBlock,terminated);

						//context switch
						for (int i = 0; i < T_CS/2; i++) {
							// Increment time and check for arrival
							time++;
							waitingPProc();
							ioPHandle(processes,ioBlock,terminated);
							srtArrival(processes, ioBlock,terminated);
						}
						ioBlock.add(temp);
						continue;
					}

					System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" terminated");
					terminated = true;
					printPQueue();
					avgWaitTime += currentProcess.getWaitTime();
					avgTurnaroundTime += currentProcess.getWaitTime() + (currentProcess.getOriginalBurstTime() * currentProcess.getOriginalBursts());
					n--; // Decrement processes
					ioPHandle(processes,ioBlock,terminated);
					srtArrival(processes, ioBlock,terminated);
					currentProcess = null;
					// Remove process 
					for (int i = 0; i < T_CS/2; i++) {
						// Increment time and check for arrival
						time++;
						waitingPProc();
						ioPHandle(processes,ioBlock,terminated);
						srtArrival(processes,ioBlock,terminated);
					}
					continue;
				}
				// all processes done
				if (n == 0) 
					break;

				time++;
				
				// Waiting
				waitingPProc();

				// I/0
				ioPHandle(processes,ioBlock,terminated);


				// decrement running process
				if (currentProcess != null) {
					currentProcess.decrementBurst();
				}
			}

			
			for (int i = 0; i < processes.size(); i++) {
				avgBurstTime += processes.get(i).getOriginalBurstTime() * processes.get(i).getOriginalBursts();
				totalBursts += processes.get(i).getOriginalBursts();
			}
			// Final calculations
			System.out.println("time "+time+"ms: Simulator ended for SRT\n");
			avgBurstTime = avgBurstTime/totalBursts;
			avgWaitTime = avgWaitTime/totalBursts;
			avgTurnaroundTime = (avgTurnaroundTime+T_CS*cswitches)/totalBursts;

			try {
				writer.write("Algorithm SRT\n");
				writer.flush();
				writer.write("-- average CPU burst time: "+df.format(avgBurstTime)+" ms\n");
				writer.flush();
				writer.write("-- average wait time: "+df.format(avgWaitTime)+" ms\n");
				writer.flush();
				writer.write("-- average turnaround time: "+df.format(avgTurnaroundTime)+" ms\n");
				writer.flush();
				writer.write("-- total number of context switches: "+cswitches+"\n");
				writer.flush();
				writer.write("-- total number of preemptions: "+numPreemptions+"\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	

	
//  RR STUFF
	
	private static void ioHandleRR(int time, ArrayList<Process> queue, ArrayList<Process> ioBlock, boolean beg) {
		for(int i = 0; i < ioBlock.size(); i++) {
			ioBlock.get(i).decrementIO();
			if (ioBlock.get(i).getRemainingIOTime() == 0) {
				Process temp = ioBlock.remove(i);
				temp.resetIOTime();
				rr_add(queue, temp, beg);
				System.out.print("time "+time+"ms: Process "+temp.getID()+" completed I/O; added to ready queue");
				printQueue(queue);
				i--;
			}
		}
	}
	
	private static void arrivalRR(ArrayList<Process> processes, ArrayList<Process> queue, int time, boolean beg) {
		for(int i = 0; i < processes.size(); i++) {
			if (processes.get(i).getArrivalTime() == time) {
				rr_add(queue, processes.get(i), beg);
				System.out.print("time "+time+"ms: Process "+processes.get(i).getID()+" arrived and added to ready queue");
				printQueue(queue);
			}
		}
	}
	
	public static void rr_add(ArrayList<Process> queue, Process process, boolean beg) {
		if(beg) {
			queue.add(0,process);		
		}else {
			queue.add(process);
		}
	}
	
	
	private static void rr(ArrayList<Process> processes, BufferedWriter writer, boolean beg) {
		int timeSlice = 80;
		boolean preempt = false;
		double avgWaitTime = 0.0;
		double avgBurstTime = 0.0;
		double avgTurnaroundTime = 0.0;
		int numContextSwitches = 0;
		numPreemptions = 0; 
		int totalBursts = 0;
		int n = processes.size();
		time = 0;
		ArrayList<Process> queue = new ArrayList<>();
		ArrayList<Process> ioBlock = new ArrayList<>();
		currentProcess = null;
		
		System.out.println("time "+time+"ms: Simulator started for RR [Q <empty>]");
		
		while (true) {
			// processes arrive
			arrivalRR(processes, queue, time,beg);
			//time slice expired
			if( timeSlice == 0) {
				//not preempted
				if(queue.size()==0 && currentProcess!=null){
					preempt = false;
				//preempted
				}else if(queue.size()!= 0 && currentProcess!=null) {
					preempt = true;
					numPreemptions++;
					//context switch out
					for (int i = 0; i < T_CS/2; i++) {
						time++;
						waitingProc(queue);
						ioHandleRR(time, queue, ioBlock,beg);
						arrivalRR(processes, queue, time,beg);
					}
					
					//rr_add(queue, currentProcess, beg);	//issue: beg does not matter for timeslice
					queue.add(currentProcess);
					currentProcess = null;				
				}
				timeSlice = 80;
			}
			// context switch in
			if (currentProcess == null && queue.size() > 0) {
				currentProcess = queue.remove(0);
				numContextSwitches++;
				//context switch
				for (int i = 0; i < T_CS/2; i++) {
					time++;
					waitingProc(queue);
					ioHandleRR(time, queue, ioBlock,beg);
					arrivalRR(processes, queue, time,beg);
					//reset Time Slice
					timeSlice = 80;	
				}
				System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" started using the CPU");
				if(preempt && currentProcess.getOriginalBurstTime()!=currentProcess.getRemainingBurstTime()) {
					System.out.print(" with "+currentProcess.getRemainingBurstTime()+"ms remaining");
				}
				printQueue(queue);	
				continue;
			}
			// process burst finishes
			if (currentProcess != null && currentProcess.getRemainingBurstTime() -1 == 0) {
				time++;
				timeSlice --;
				waitingProc(queue);
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
					ioHandleRR(time, queue, ioBlock,beg);
					arrivalRR(processes, queue, time,beg);
					//context switch
					for (int i = 0; i < T_CS/2; i++) {
						time++;
						waitingProc(queue);
						ioHandleRR(time, queue, ioBlock,beg);
						arrivalRR(processes, queue, time,beg);
					}
					
					Process temp = new Process(currentProcess);
					ioBlock.add(temp);
					currentProcess = null;
					continue;
				}
				else {
					System.out.print("time "+time+"ms: Process "+currentProcess.getID()+" terminated");
					printQueue(queue);
					avgWaitTime += currentProcess.getWaitTime();
					avgTurnaroundTime += currentProcess.getWaitTime() + (currentProcess.getOriginalBurstTime() * currentProcess.getOriginalBursts());
					n--;
					ioHandleRR(time, queue, ioBlock,beg);
					arrivalRR(processes, queue, time,beg);
					currentProcess = null;
					for (int i = 0; i < T_CS/2; i++) {
						time++;
						waitingProc(queue);
						ioHandleRR(time, queue, ioBlock,beg);
						arrivalRR(processes, queue, time,beg);
					}
					continue;
				}
			}
			
			// all processes done
			if (n == 0)
				break;
						
			time++;
			timeSlice--;
			
			if( timeSlice == 0) {
				if(queue.size()==0 && currentProcess!=null){
					System.out.print("time "+time+"ms: Time slice expired; no preemption because ready queue is empty");
					printQueue(queue);
				}else if(queue.size()!= 0 && currentProcess!=null) {
					int remainingbursttime = currentProcess.getRemainingBurstTime() - 1;
					System.out.print("time "+time+"ms: Time slice expired; process "+currentProcess.getID()+" preempted with "+remainingbursttime+"ms to go");
					printQueue(queue);
				}
			}
			// Waiting
			waitingProc(queue);
			
			// I/0
			ioHandleRR(time, queue, ioBlock,beg);
			
			// decrement running process
			if (currentProcess != null) {
				currentProcess.decrementBurst();
			}
				
		}
		
		for (int i = 0; i < processes.size(); i++) {
			avgBurstTime += processes.get(i).getOriginalBurstTime() * processes.get(i).getOriginalBursts();
			totalBursts += processes.get(i).getOriginalBursts();
		}
		System.out.print("time "+time+"ms: Simulator ended for RR\n");
		avgBurstTime = avgBurstTime/totalBursts;
		avgWaitTime = avgWaitTime/totalBursts;
		avgTurnaroundTime = (avgTurnaroundTime+T_CS*numContextSwitches)/totalBursts;
		
		try {
			writer.write("Algorithm RR\n");
			writer.flush();
			writer.write("-- average CPU burst time: "+df.format(avgBurstTime)+" ms\n");
			writer.flush();
			writer.write("-- average wait time: "+df.format(avgWaitTime)+" ms\n");
			writer.flush();
			writer.write("-- average turnaround time: "+df.format(avgTurnaroundTime)+" ms\n");
			writer.flush();
			writer.write("-- total number of context switches: "+numContextSwitches+"\n");
			writer.flush();
			writer.write("-- total number of preemptions: "+numPreemptions+"\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}