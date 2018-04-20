import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Collections;


public class main {
	  
	public static void main(String[] args) throws IOException {
		PCBs pcbsObj = new PCBs();
		pcbsObj.loadPCBs();
		System.out.println("1- Number of Processes in the Job Queue is: " + pcbsObj.getNumberOfProcessesInJobQueue());
		System.out.println("2- The average program size of all programms is: " + pcbsObj.getAverageProcessSize());
	}
	
	public static void testSortingMethods() {
LinkedList<PCB> l1 = new LinkedList<PCB>();
		
		PCB p1 = new PCB ( 1555 , 1 , 3 , "new", 1 ) ;
		PCB p2 = new PCB ( 1555 , 2 , 1 , "new", 2 ) ;
		PCB p3 = new PCB ( 1555 , 4 , 2 , "new", 3 ) ;
		PCB p4 = new PCB ( 1555 , 5 , 4 , "new", 4 ) ;
		PCB p5 = new PCB ( 1555 , 3 , 5 , "new", 5 ) ;
		
		l1.add(p1);
		l1.add(p2);
		l1.add(p5);
		l1.add(p4);
		l1.add(p3);
		
		System.out.println("Memory Sizes before sorting");
		for (PCB process : l1) {
			System.out.println(process.getMemorySize());
		}
		
		Collections.sort(l1);
		
		System.out.println("Memory Sizes after sorting");
		for (PCB process : l1) {
			System.out.println(process.getMemorySize());
		}
		
		
		
		System.out.println("IOtime before sorting");

		for (PCB process : l1) {
			System.out.println(process.getIOtime());
		}
		

		
		Collections.sort(l1, new SortByIOtime());
		System.out.println("IOtime after sorting");

		for (PCB process : l1) {
			System.out.println(process.getIOtime());
		}
		
		
		System.out.println("expectedExecutionTime before sorting");
		
		for (PCB process : l1) {
			System.out.println(process.getExpectedExecutionTime());
		}
		
		
		
		Collections.sort(l1, new SortByExpectedExecutionTime());
		System.out.println("expectedExecutionTime after sorting");
		
		for (PCB process : l1) {
			System.out.println(process.getExpectedExecutionTime());
		}
	}

}