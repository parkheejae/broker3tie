package broker.threetier.exception;

public class RecordNotFoundException extends Exception {
	
	
	public RecordNotFoundException(){
		
		this("this is ReconrdNotFoundException");
	}
	
	public RecordNotFoundException(String message){
		super(message);
	}
}
