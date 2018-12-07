package broker.threetier.exception;

public class InvalidTransactionException extends Exception {
	public InvalidTransactionException(){
		
		this("this is NotFoundIsbnException");
	}
	
	public InvalidTransactionException(String message){
		super(message);
	}
}

