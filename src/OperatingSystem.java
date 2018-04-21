import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

public class OperatingSystem extends TimerTask{
	
	private PCBs Processes = new PCBs () ; 	// the object that holds all the processes ( JobQueue ) 
	private boolean OSReady = false ; 		// boolean indicates that the CPU can start processing ( it will be true after finishing preparation )
	private LinkedList<PCB> jobQueue = Processes.getJobQueue();
	private LinkedList<PCB> readyQueue = new LinkedList<PCB> () ; 		// all processes in the ready queue ( should not exceed 132 MB )
	private LinkedList<PCB> deviceQueue = new LinkedList<PCB> () ; 		// all processes which asked an I/O operation
	private LinkedList<PCB> deadQueue = new LinkedList<PCB> () ; 		// all processes which completed their processing
	private LinkedList<PCB> jobQueueCopy = new LinkedList<PCB>();
	private PCB p = null;
	
	
	//starting the operations of the operating system ( most important method )
	public void start () throws IOException {
		
		// preparing the timer for the method to run every 1 millisecond 
				Timer timer = new Timer();
				timer.schedule(new OperatingSystem() , 0, 1 ); // the method run ( will run every 1 millisecond )
		
		
		//////////////////////////////////////// preparing processes to be executed
		
		// loading processes into JobQueue
		Processes.loadPCBs (); // will load all processes from text file to the PCBs list
		
		copyList(jobQueue, jobQueueCopy);
		
		// sort the jobQueueCopy according to the memory size
		Collections.sort(jobQueueCopy, new SortByMemorySize());
		
//		// preparing the timer for the method to run every 1 millisecond 
//		Timer timer = new Timer();
//		timer.schedule(new OperatingSystem() , 0, 1 ); // the method run ( will run every 1 millisecond ) 
		
		// fill ready queue
		fillReadyQueue();
		
//		for(PCB process : readyQueue) {
//			System.out.println(process.getExpectedExecutionTime());
//		}
		//////////////////////////////////////// preparation finished		
		
		// make OSready = true
		OSReady = true;
		
		
	}
	
	
	//  get PCBs to the readyQueue, till addToReadyQueue method return false
	public boolean fillReadyQueue() {
		
		// 1- flag to exit the loop if the readyQueue is full
		boolean flag = true;
		while( flag ) {
			// 2- remove and return first PCB in jobQueue
			PCB current = jobQueueCopy.removeFirst();
			// 3- check if the PCB has "new" state
			if( current.getState().equals("new")) {
				// 4- addToReady function return true if the process has been added successfully, and false if 
				flag = addToReady(current);
			}
		}
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
		//if(OSReady == true) {
			System.out.println("hello");
			
//			if (p == null) {
//				
//				
//				// 1- get the process from readyQueue to CPU
//				p = getFromReady();
//				
//				// 2- set the state to running
//				p.setState("running");
//				
//				// 3- generate a random number for handling interrupt odds
//				Random randGen = new Random();
//				int randNum = randGen.nextInt(100) + 1; //generate random number between 0 (inclusive) and 101 (exclusive).
//				
//				System.out.println(randNum);
//				
//				//// interrupt chance
//				if(randNum > 0 && randNum <= 10) {
//					addToReady(p);
//					p = null ; 
//				}
//				//// IO request chance
//				if(randNum >= 11 && randNum <= 30) {
//					addToIOQueue(p);
//					p = null ; 
//				}
//				
//				//// normal termination chance
//				if(randNum >= 31 && randNum <= 35) {
//					
//					addToDeadQueue(p, "normally");
//					p = null;
//				}
//				//// abnormal termination chance
//				if(randNum == 36) {
//					addToDeadQueue(p, "abnormally");
//					p = null;
//				}
//				
//				p.setActualExcutionTime(p.getActualExcutionTime()+1); //increment actualExcutionTime
//				
//			} 
//			
//			
//			
//			
//			
//			
		}
	//}
	
	public PCB getFromReady() { // get PCB from readyQueue after sorting it, it will return PCB with the least memorySize
		 // 1- return PCB with the least memorySize, which will be first after sorting readyQueue
		return readyQueue.removeFirst();
	}
	
	public boolean addToReady(PCB process) {
		// 1- calculate total of memory sizes in readyQueue
		int readyQueueMemorySizes = 0;
		for (PCB temp : readyQueue) {
            readyQueueMemorySizes += temp.getMemorySize();
        }
		
		// 2- if readyQueueMemorySizes + process.memorySize is over the selected amount
		if( (readyQueueMemorySizes + process.getMemorySize())  > 160000 )
			return false;

		// 3- change state of process to ready
		process.setState("ready");
		
		// 4- add the process to readyQueue
		readyQueue.add(process);
		
		// 5- sort the readyQueue
		Collections.sort(readyQueue, new SortByExpectedExecutionTime());
		
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
		
		// 5- add another process to readyQueue if there's space
		fillReadyQueue();
		
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
	
	private void copyList(LinkedList<PCB> l1, LinkedList<PCB> l2) {
		for(int i = 0; i<l1.size(); i++) {
			l2.add(l1.get(i));
		}
	}

	
	
	
	
	
	
}
