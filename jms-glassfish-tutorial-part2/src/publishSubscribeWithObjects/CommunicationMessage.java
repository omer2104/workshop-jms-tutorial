package publishSubscribeWithObjects;

import java.io.Serializable;

public class CommunicationMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 945329021690903243L;
	private String name;
	private String message;
	
	public CommunicationMessage(String name, String message) {
		this.name = name;
		this.message = message;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
