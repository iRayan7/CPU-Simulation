
public class PCB {
	
	private int id ; 
	private int expectedExecutionTime ; 
	private int memorySize ; 
	private String state  ; 

	public PCB ( int id , int expectedExecutionTime , int memorySize , String state ) {
		this.id = id ; 
		this.expectedExecutionTime = expectedExecutionTime ; 
		this.memorySize = memorySize ; 
		this.state = state ; 
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
	
}