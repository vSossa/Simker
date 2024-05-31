package main.app.task;

public enum Status { 
	OPEN(0),
	IN_PROGRESS(1),
	CLOSED(2);

	private final int statusValue;

	private Status(int statusValue) {
		this.statusValue = statusValue;			
	}

	public int getStatusValue() { return this.statusValue; }

	@Override
	public String toString() {
		String s;
		if (this.statusValue == 0) {
			s = "OPEN";	
		} else if (this.statusValue == 1) {
			s = "IN_PROGRESS";
		} else {
			s = "CLOSED";
		}

		return s;
	}
}
