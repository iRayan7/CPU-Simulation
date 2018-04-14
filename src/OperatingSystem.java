import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class OperatingSystem extends TimerTask {
	
	private PCBs Processes = new PCBs () ; 	// the object that holds all the processes ( JobQueue ) 
	private boolean OSReady = false ; 		// boolean indicates that the CPU can start processing ( it will be true after finishing prepration ) 
	private LinkedList<PCB> readyQueue = new LinkedList<PCB> () ; 		// all processes in the ready queue ( should not exceed 132 MB )
	private LinkedList<PCB> deviceQueue = new LinkedList<PCB> () ; 		// all processes which asked an I/O operation 
	private LinkedList<PCB> deadQueue = new LinkedList<PCB> () ; 		// all processes which completed their processing
	private PCB p = null;
	
	
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
		/*
		 * if (OSReady == true){
		 * 		check if a process is stored in p (the global variable)
		 * 		generate a random number between 1 and 100
		 * 		implement the odds (terminate normally, terminate abnormally, terminate in io queue, io request happen's, interrupt happen's)
		 * 		If one of the odds occur, make p = null. If not, leave the process in p (so that when the next tick the process stays in CPU).
		 */
		if(OSReady == true) {
			if(p != null) {
				p = getFromReady();
				Random randGen = new Random();
				int randNum = randGen.nextInt(101); //generate random number between 0 (inclusive) and 101 (exclusive).
				
				//interrupt chance
				if(randNum > 0 && randNum <= 10) {
					p.setState("ready");
					addToReady(p);
				}
				//IO request chance
				if(randNum > 11 && randNum <= 30) {
					p.setState("waiting");
					addToIOQueue(p);
				}
				//normal termination chance
				if(randNum > 31 && randNum <= 35) {
					p.setState("terminated normally");
					addToDeadQueue(p);
				}
				//abnormal termination chance
				if(randNum >36 && randNum <=37) {
					p.setState("terminated abnormally");
					addToDeadQueue(p);
				}
			}
		}
	}
	
	//To-Do
	//getFromReady() every time a process terminates
	//addToReady() from CPU or device queue
	//addToIOQueue()
	//addToDeadQueue(int termination type)
	//IOOperation() checks the IO queue and terminates the waiting process according to the odds.
	
	
}
