import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

public class OperatingSystem{
	
	private PCBs Processes = new PCBs () ; 	// the object that holds all the processes ( JobQueue ) 
	private boolean OSReady = false ; 		// boolean indicates that the CPU can start processing ( it will be true after finishing preparation )
	private LinkedList<PCB> jobQueue = Processes.getJobQueue();
	private LinkedList<PCB> readyQueue = new LinkedList<PCB> () ; 		// all processes in the ready queue ( should not exceed 132 MB )
	private LinkedList<PCB> deviceQueue = new LinkedList<PCB> () ; 		// all processes which asked an I/O operation
	private LinkedList<PCB> deadQueue = new LinkedList<PCB> () ; 		// all processes which completed their processing
	private LinkedList<PCB> jobQueueCopy = new LinkedList<PCB>();
	private PCB processInCPU = null;
	
	
	//starting the operations of the operating system ( most important method )
	public void start () throws IOException {
		
		// loading processes into JobQueue
		Processes.loadPCBs (); // will load all processes from text file to the PCBs list
		
		copyList(jobQueue, jobQueueCopy);
		
		// sort the jobQueueCopy according to the memory size
		Collections.sort(jobQueueCopy, new SortByMemorySize());
	
		
		// fill ready queue
		fillReadyQueue();
		

		////////////// preparation finished  //////////////
		
		//////////////////////////// Starting The Operating System Simulation ////////////////////////////////
		int counter = 1;
		while( deadQueue.size() != 3000 ) {
//		for ( int i = 0 ; i < 50 ; i++ ) {
			
			////////////////////////// CPU start ////////////////////////

			counter ++ ; 
			// the method that will start the CPU operation / counter > to count the number of iterations
			CPUOperation(counter);
			
			/////////////////////////// OI start ////////////////////////
				
			// the method that will start the I/O operation 
			IOOperation(); 
			
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		
		System.out.println("<<<<< Program Ended Execution >>>>>");
		System.out.println("* 1 - Number of Processes in the Job Queue is : " + jobQueue.size());
		System.out.println("* 2 - The average program size of all program is : " + getAverageProcessSize());
		System.out.println("* 3 - The number of process that terminated abnormally : " + getNumberOfDead("abnormally") ) ;
		System.out.println("* 4 - The number of process that terminated normally : " + getNumberOfDead("normally") );
		System.out.println("* 5 - The number of CPU bound processes : " + getNumberOfCPU() );
		System.out.println("* ");
		System.out.println("* More Information :: ");
		System.out.println("* Number of iterations it took to finish the program " + --counter );
		System.out.println("* The number of process in ready state : " + getNumOfState("ready") );
		System.out.println("* The number of process in waiting state : " + getNumOfState("waiting") );
		System.out.println("* The number of process in terminated state : " + getNumOfState("terminate") );
		System.out.println("* The number of process left to be executed : " + jobQueueCopy.size());
		System.out.println("* The number of peocess that terminated while it was in IO operation : " + getNumberOfDead("IO") );
		System.out.println("* The number of I/O bound processes : " + ( 3000 - getNumberOfCPU() ) );
		System.out.println("* The average number of jobs that have completed their execution normally: " + (getNumberOfDead("normally") / 3000.0)*100 + "%");
		System.out.println("* The average number of jobs that have completed their execution abnormally: " + (getNumberOfDead("abnormally") / 3000.0)*100 + "%");

		System.out.println();
		
	}

	private void CPUOperation( int counter ) {
		
		// generate a random number for handling interrupt odds
		Random randGen = new Random();
		int randNum = randGen.nextInt(100) + 1; //generate random number between 0 (inclusive) and 101 (exclusive).
		
			// get the process from readyQueue to CPU
			if ( processInCPU == null ) {
				processInCPU = getFromReady();
			}
			
			if(processInCPU != null) {
				processInCPU.setActualExcutionTime(processInCPU.getActualExcutionTime()+1); //increment actualExcutionTime
				
				// set the state to running
				processInCPU.setState("running");
				
				System.out.println( "/////////////////// Step " + counter + "  /////////////////// " );
				System.out.println("* The generated random number generated random number : " + randNum);
				System.out.println("* The number of process left to be executed : " + jobQueueCopy.size());
				System.out.println("* The number of process in ready state : " + getNumOfState("ready") );
				System.out.println("* The number of process in waiting state : " + getNumOfState("waiting") );
				System.out.println("* The number of process in terminated state : " + getNumOfState("terminate") );

				System.out.println((processInCPU != null ? "* There is a process in the CPU" : "* There is no process in the CPU"));
				int memorySize = 0 ; 
				for ( PCB process : readyQueue ) {
					memorySize += process.getMemorySize();
				}
				for ( PCB process : deviceQueue ) {
					memorySize += process.getMemorySize();
				}
				
				memorySize += processInCPU.getMemorySize(); 
					
				System.out.println("* The actual size in the memory is :" + memorySize);
				System.out.println("////////////////////////////////////////////////////");
				System.out.println();

				
				//// interrupt chance
				if(randNum > 0 && randNum <= 10) {
					addToReady(processInCPU);
					processInCPU = null ;
				}
				//// IO request chance **not completed**
				if(randNum >= 11 && randNum <= 30) {
					addToIOQueue(processInCPU);
					processInCPU = null ;
				}
				
				//// normal termination chance
				if(randNum >= 31 && randNum <= 35) {
					
					addToDeadQueue(processInCPU, "normally");
					processInCPU = null;
				}
				//// abnormal termination chance
				if(randNum == 36) {
					addToDeadQueue(processInCPU, "abnormally");
					processInCPU = null;
				}
			}
	}
	
	private int getNumOfState(String string) {
		int sum = 0 ; 
		for ( PCB process : jobQueue ) {
			if ( process.getState().equals(string) ) {
				sum ++ ; 
			}
			
		}
		return sum;
	}

	private int getNumberOfCPU() {
		int sum = 0 ; 
		for ( PCB process : jobQueue ) {
			if ( process.getActualExcutionTime() > process.getActualIOTime() ) {
				sum ++ ; 
			}
		}
		return sum ;
	}

	private int getNumberOfDead(String string) {
		int sum = 0 ; 
		for ( PCB process : jobQueue ) {
			if ( process.getTerminationType().equals(string) ) {
				sum ++ ; 
			}
			
		}
		return sum;
	}

	//  get PCBs to the readyQueue, till addToReadyQueue method return false
	public boolean fillReadyQueue() {
		
		// 1- flag to exit the loop if the readyQueue is full
		boolean flag = true;
		while( flag ) {
			
			if(jobQueueCopy.size() > 0) {
				// 2- remove and return first PCB in jobQueue
				PCB current = jobQueueCopy.removeFirst();
				
				// 4- addToReady function return true if the process has been added successfully, and false if 
				flag = addToReady(current);
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	public PCB getFromReady() { // get PCB from readyQueue after sorting it, it will return PCB with the least memorySize
		
		if(readyQueue.isEmpty()) {
			return null;
		}
		// 1- return PCB with the least memorySize, which will be first after sorting readyQueue
		return readyQueue.removeFirst();
	}
	
	public boolean addToReady(PCB process) {
		
		// ******** check if there is enough space in RAM (remember, RAM size is 160MB).
		// 1- calculate total of memory sizes in readyQueue
		int readyQueueMemorySizes = 0;
		for (PCB temp : readyQueue) {
            readyQueueMemorySizes += temp.getMemorySize();
        }
		// 2- calculate total of memory sizes in deviceQueue
		int deviceQueueMemorySizes = 0;
		for (PCB temp : deviceQueue) {
            deviceQueueMemorySizes += temp.getMemorySize();
        }
				
		// 3- check if readyQueueMemorySizes + deviceQueueMemorySizes + process + processInCPU size is over 160MB
		//    if it is, add process to jobQueue and sort it again by memory size.
		if( (readyQueueMemorySizes + deviceQueueMemorySizes + process.getMemorySize() + ( (processInCPU != null) ? processInCPU.getMemorySize():( 0 ) ) > 160000 ) ) {
			jobQueueCopy.add(process);
			Collections.sort(jobQueueCopy, new SortByMemorySize());
			return false;
		}
		// ******** at this point, there is actually enough space in RAM, so add process safely to readyQueue
		
		// 4- change state of process to ready
		process.setState("ready");
		
		// 5- add the process to readyQueue
		readyQueue.add(process);
		
		// 6- sort the readyQueue again
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
		
		fillReadyQueue();
		
		return true;
	}
	
	public boolean addToDeadQueue(PCB process,String terminationType) {
		
		// 1- delete process from readyQueue, if the process does not exist in readyQueue return false
		if ( readyQueue.remove(process) ) {
			return false;
		}
		
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
		if(!deviceQueue.isEmpty()) { // if deviceQueue is not empty
			
			// 1- generate a random number between 0 and 100
			Random randGen = new Random();
			int randomNumber = randGen.nextInt(101);
			
			// 2- pull the fist element in deviceQueue and increment its actualIOTime by one ut.
			PCB process = deviceQueue.removeFirst();
			process.setActualIOTime(process.getActualIOTime()+1);
			
			// 3- there's a 20% chance that it will terminate, so setup this chance to occur,
			//    and when it does, return the process to the IO queue.
			if (process.getActualIOTime() == process.getIOtime()) {
				// 2- return the process to readyQueue 
				addToReady(process);
			}
			
			else if(randomNumber <= 20) {
//				addToDeadQueue(process, "IO");
				addToReady(process);
			}
			
			else
				deviceQueue.addFirst(process);
		}
		
		return true;
	}
	
	private void copyList(LinkedList<PCB> l1, LinkedList<PCB> l2) {
		for(PCB process : l1) {
			l2.add(process);
		}
	}
	
	// returns the average process size of all processes in the job queue
		public int getAverageProcessSize() {
			int averageProcessSize;
			int totalProcessesSize = 0;
			
			for(PCB process : jobQueue) {
				totalProcessesSize += process.getMemorySize();
			}
			
			averageProcessSize = totalProcessesSize / jobQueue.size();
			
			return averageProcessSize;
		}
	
}
