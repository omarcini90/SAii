package mx.org.fimpes.saii.exceptions;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public class ValidationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ValidationException(String message) {
		super(message);
	}
	
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}