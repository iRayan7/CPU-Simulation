import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Collections;

public class OperatingSystem extends TimerTask {
	
	private PCBs Processes = new PCBs () ; 	// the object that holds all the processes ( JobQueue ) 
	private boolean OSReady = false ; 		// boolean indicates that the CPU can start processing ( it will be true after finishing preparation )
	private LinkedList<PCB> jobQueue = Processes.getJobQueue();
	private LinkedList<PCB> readyQueue = new LinkedList<PCB> () ; 		// all processes in the ready queue ( should not exceed 132 MB )
	private LinkedList<PCB> deviceQueue = new LinkedList<PCB> () ; 		// all processes which asked an I/O operation
	private LinkedList<PCB> deadQueue = new LinkedList<PCB> () ; 		// all processes which completed their processing
	private PCB p = null;
	
	
	//starting the operations of the operating system ( most important method )
	public void start () throws IOException {

		//////////////////////////////////////// preparing processes to be executed
		
		// loading processes into JobQueue
		Processes.loadPCBs () ; // will load all processes from text file to the PCBs list
		System.out.println(Processes.getJobQueue().size() ); // check
		
		// preparing the timer for the method to run every 1 millisecond 
		Timer timer = new Timer();
		timer.schedule(new OperatingSystem() , 0, 1 ); // the method run ( will run every 1 millisecond ) 
		
		// fill ready queue
		fillReadyQueue();
		
		//////////////////////////////////////// preparation finished		
		
		// make OSready = true
		OSReady = true;
			
		// execute the simulation run()
		run();
	}
	
	
	//  get PCBs to the readyQueue, till addToReadyQueue method return false
	public boolean fillReadyQueue() {
		
		// 1- flag to exit the loop if the readyQueue is full
		boolean flag = true;
		while( flag ) {
			
			// 2- remove and return first PCB in jobQueue
			PCB current = jobQueue.removeFirst();
			// 3- check if the PCB has "new" state
			if( current.getState().equals("new")) {
				// 4- addToReady function return true if the process has been added successfully, and false if 
				flag = addToReady(current);
			}
			jobQueue.add(current);
		}
		
		// to check the size
		System.out.print(readyQueue.size());
		return true;
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
				
				// 1- get the process from readyQueue to CPU
				p = getFromReady();
				
				// 2- set the state to running
				p.setState("running");
				
				// 3- generate a random number for handling interrupt odds
				Random randGen = new Random();
				int randNum = randGen.nextInt(101); //generate random number between 0 (inclusive) and 101 (exclusive).
				
				//// interrupt chance
				if(randNum > 0 && randNum <= 10) {
					addToReady(p);
				}
				//// IO request chance
				if(randNum >= 11 && randNum <= 30) {
					addToIOQueue(p);
				}
				//// normal termination chance
				if(randNum >= 31 && randNum <= 35) {
					
					addToDeadQueue(p, "normally");
				}
				//// abnormal termination chance
				if(randNum == 36) {
					addToDeadQueue(p, "abnormally");
				}
			}
		}
	}
	
	public PCB getFromReady() { // get PCB from readyQueue after sorting it, it will return PCB with the least memorySize
		// 1- sort the readyQueue
		Collections.sort(readyQueue);
		 // 2- return PCB with the least memorySize, which will be first after sorting readyQueue
		return readyQueue.getFirst();
	}
	
	public boolean addToReady(PCB process) {
		// 1- calculate total of memory sizes in readyQueue
		int readyQueueMemorySizes = 0;
		for (PCB temp : readyQueue) {
            readyQueueMemorySizes += temp.getMemorySize();
        }
		
		// 2- if readyQueueMemorySizes + process.memorySize is over the selected amount
		if( (readyQueueMemorySizes + process.getMemorySize())  > 16384 )
			return false;
		
		// 3- change state of process to ready
		process.setState("ready");
		
		// 4- add the process to readyQueue
		readyQueue.add(process);
		
		return true;
		
	}
	
	public boolean addToIOQueue(PCB process) {
		// 1- change state to waiting
		process.setState("waiting");
		
		// 2- add to ioQueue
		deviceQueue.add(process);
		
		// 3- sort ioQueue
		Collections.sort(deviceQueue, new SortByIOtime());
		
		return true;
	}
	
	public boolean addToDeadQueue(PCB process,String terminationType) {
		// 1- delete process from readyQueue, if the process does not exist in readyQueue return false
		if ( !readyQueue.remove(process) )
			return false;
		
		// 2- change state
		process.setState("terminate");
		
		// 3- change termination type
		process.setTerminationType(terminationType);
		
		// 4- add to deadQueue
		deadQueue.add(process);
		
		return true;
	}
	
	public boolean IOOperation() {
		// 1- generate a random number
		Random randGen = new Random();
		int randomNumber = randGen.nextInt(101);
		
		// 2- iterate over deviceQueue and apply the odds
		for (PCB process : deviceQueue) {
			if (randomNumber <= 20) {
				// 1- remove from deviceQueue
				deviceQueue.remove(process);
				// 2- add to deadQueue, which will remove it from readyQueue eventually 
				addToDeadQueue(process, "IO");
			}
        }
		
		// 3- sort the deviceQueue
		Collections.sort(deviceQueue, new SortByIOtime());
		
		// 4- subtract 1ut from the device been served (first node after sorting)
		if ( !deviceQueue.isEmpty() ) {
			PCB current = deviceQueue.getFirst();
			current.subtractFromIOtime(1);
			// remove if the process completed IOtime
			if ( current.getIOtime() <= 0 )
				deviceQueue.remove(current);
		}
		
		return true;
	}
	
	
	
	
	
	
}
