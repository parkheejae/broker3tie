package broker.threetier.exception;

public class DuplicateSSNException extends Exception {
	
	
	public DuplicateSSNException(){
		
		this("this is duplicateIDException");
	}
	
	public DuplicateSSNException(String message){
		super(message);
	}
}
