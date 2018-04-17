import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class PCBs {
	
	// list of all the processes regarding their state > ( Job Queue ) 
	private LinkedList<PCB> JobQueue = new LinkedList<PCB>();	
	
	// returns the linked list of the processes 
	public LinkedList<PCB> getJobQueue () {
		return JobQueue ; 
	}
	
	// loads all the processes from the text file into the PCBs list
	public void loadPCBs() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("//Users//alobaili//Projects//OS//processes.txt"));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    addPCB ( dividingText ( line ) ) ;  // diving the line and adding the process to the linked list
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		        if ( line != null ) {
				    addPCB ( dividingText ( line ) ) ; // diving the line and adding the process to the linked list
		        }
		    }
		    String everything = sb.toString();
		} finally {
		    br.close();
		}
		
	}
	
	// dividing the line that comes from the text file and adding it as a process
	private PCB dividingText ( String line ) {
		int id ;
		int CPU ; 
		int RAM ; 
		
		String line2 [] = line.split(";") ; 
		id = Integer.parseInt( line2[0].split(":")[1] ) ; 
		CPU = Integer.parseInt( line2[1].split(":")[1] ) ; 
		RAM = Integer.parseInt( line2[2].split(":")[1] ) ; 
		
		
		Random randGen = new Random();
		int IOtime = randGen.nextInt(100) + 100; //random number between 100 and 200

		PCB newProcess = new PCB ( id , CPU , RAM , "new", IOtime ) ; // any process will be added as new process 
		
		return newProcess ; 
	}
	
	// adding a single process to the list
	private void addPCB ( PCB newProcess ) {
		JobQueue.add(newProcess) ; 
	}
	
	// used to add a process after changing its state to ready
	public boolean addReadyProcess ( PCB newProcess ) {
		
		if( newProcess.getState().equals("new") )
			return false;
		
		JobQueue.add(newProcess) ; 
		
		return true;
	}
	

	
}
