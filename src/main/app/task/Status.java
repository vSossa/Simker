package main.app.task;

public enum Status { 
	OPEN(0),
	IN_PROGRESS(1),
	CLOSED(2);

	final int VALUE;

	private Status(int value) {
		this.VALUE = value;
	}

	public int value() {
		return VALUE;
	}
}
