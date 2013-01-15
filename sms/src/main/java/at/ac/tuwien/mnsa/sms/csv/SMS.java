package at.ac.tuwien.mnsa.sms.csv;

public class SMS {
	
	private String number;
	private String text;
	
	public SMS(String number, String text) {
		super();
		this.number = number;
		this.text = text;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	

}
