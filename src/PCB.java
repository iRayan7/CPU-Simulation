import java.util.Comparator;

public class PCB implements Comparable<PCB>{
	
	private int id; 
	private int expectedExecutionTime;
	private int memorySize;
	private String state; //possible states: new, running, ready, interrupt, io, terminate
	private int IOtime; //IOtime
	private String terminationType; //termination type ( "none" initial type , "normally" , "abnormally" , "IO" )
	private int actualExcutionTime; //actual execution time

	public PCB ( int id , int expectedExecutionTime , int memorySize , String state, int IOtime) {
		this.id = id ; 
		this.expectedExecutionTime = expectedExecutionTime ; 
		this.memorySize = memorySize ; 
		this.state = state ; 
		
		this.IOtime = IOtime;
		
		this.terminationType = "none";
		this.setActualExcutionTime(0);
	}
	
	
	// https://www.programcreek.com/2013/01/sort-linkedlist-of-user-defined-objects-in-java/
	
	@Override
	public int compareTo(PCB o) {
		
		int comparedMemorySize = o.memorySize;
		if (this.memorySize > comparedMemorySize)
			return 1;
		else if (this.memorySize == comparedMemorySize) 
			return 0;
		else 
			return -1;
	}
	

	

		
	
	


	public int getExpectedExecutionTime() {
		return expectedExecutionTime;
	}
	
	public void setExpectedExecutionTime( int newTime ) {
		this.expectedExecutionTime = newTime ; 
	}

	public int getId() {
		return id;
	}


	public int getMemorySize() {
		return memorySize;
	}


	public String getState() {
		return state;
	}

	// the only needed set
	public void setState(String state) {
		this.state = state;
	}


	public int getIOtime() {
		return IOtime;
	}


	public void subtractFromIOtime(int amount) {
		this.IOtime -= amount;
	}


	public String getTerminationType() {
		return terminationType;
	}


	public void setTerminationType(String terminationType) {
		this.terminationType = terminationType;
	}


	public void addToActualExcutionTime(int amount) {
		this.setActualExcutionTime(this.getActualExcutionTime() + amount);
	}


	public int getActualExcutionTime() {
		return actualExcutionTime;
	}


	public void setActualExcutionTime(int actualExcutionTime) {
		this.actualExcutionTime = actualExcutionTime;
	}

	
}

// https://www.tutorialspoint.com/java/java_using_comparator.htm
class SortByIOtime implements Comparator<PCB>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(PCB a, PCB b)
    {
        return a.getIOtime() - b.getIOtime();
    }
}

class SortByMemorySize implements Comparator<PCB>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(PCB a, PCB b)
    {
        return a.getMemorySize() - b.getMemorySize();
    }
}

class SortByExpectedExecutionTime implements Comparator<PCB>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(PCB a, PCB b)
    {
        return a.getExpectedExecutionTime() - b.getExpectedExecutionTime();
    }
}