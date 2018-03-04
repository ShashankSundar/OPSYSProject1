import java.io.*;
import java.util.*;

public class Project1 {
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
	}
}

