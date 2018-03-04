import java.io.*;
import java.util.*;

public class Project1 {
	
	final int T_CS = 8;
	final int T_SLICE = 80;
	
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

	private static void fcfs(ArrayList<Process> processes) {
		int n = processes.size();
		int time = 0;
		ArrayList<Process> queue = new ArrayList<>();
		ArrayList<Process> ioBlock = new ArrayList<>();
		Process currentProcess = null;
		boolean done = false;
		
		while (!done) {
			for(int i = 0; i < processes.size(); i++) {
				if (processes.get(i).getArrivalTime() == time)
					queue.add(processes.get(i));
			}
			if (time == 0)
				continue;
			
			if (currentProcess == null && queue.size() > 0)
				currentProcess = queue.remove(0);
			
			if (currentProcess == null)
				currentProcess.decrementBurst();
			
			if (currentProcess.getRemainingBurstTime() == 0) {
				currentProcess.decrementNumBursts();
				currentProcess.resetBurstTime();
				Process temp = new Process(currentProcess);
				queue.add(temp);
				ioBlock.add(temp);
				currentProcess = null;
			}
			
			for(int i = 0; i < ioBlock.size(); i++) {
				ioBlock.get(i).decrementIO();
				if (ioBlock.get(i).getRemainingIOTime() == 0) {
					Process temp = ioBlock.remove(i);
					temp.resetIOTime();
					queue.add(temp);
				}
			}
			
			
			
			if (currentProcess != null && currentProcess.getRemainingBursts() == 0)
				n--;
			
			if (n == 0) {
				done = true;
			}
				
			time++;
		}
		
		
	}
	
}

