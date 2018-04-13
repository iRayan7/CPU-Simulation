import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class OperatingSystem extends TimerTask {
	
	private PCBs Processes = new PCBs () ; 	// the object that holds all the processes ( JobQueue ) 
	private boolean OSready = false ; 		// boolean indicates that the CPU can start processing ( it will be true after finishing prepration ) 
	private LinkedList<PCB> readyQueue = new LinkedList<PCB> () ; 		// all processes in the ready queue ( should not exceed 132 MB )
	private LinkedList<PCB> deviceQueue = new LinkedList<PCB> () ; 		// all processes which asked an I/O operation 
	private LinkedList<PCB> deadQueue = new LinkedList<PCB> () ; 		// all processes which completed their processing
	
	
	//starting the operations of the operating system ( most important method )
	public void start () throws IOException {


		//////////////////////////////////////// preparing processes to be executed /////////////////////////////////////////
		
		// loading processes into JobQueue
		Processes.loadPCBs () ; // will load all processes from text file to the PCBs list
		System.out.println(Processes.getJobQueue().size() ); // check
		
		// preparing the timer for the method to run every 1 millisecond 
		Timer timer = new Timer();
		timer.schedule(new OperatingSystem() , 0, 1 ); // the method run ( will run every 1 second ) 
		
		// fill ready queue 
//		fillReadyQueue() ; 
			
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
	}


	// This method is the actual CPU , it will run once every 1 millisecond
	public void run() {
		
	}
	
}
