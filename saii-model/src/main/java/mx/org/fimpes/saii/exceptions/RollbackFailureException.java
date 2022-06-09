package mx.org.fimpes.saii.exceptions;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public class RollbackFailureException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public RollbackFailureException(String message, Throwable cause) {
        super(message, cause);
    }
	
    public RollbackFailureException(String message) {
        super(message);
    }
}
