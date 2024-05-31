package main.app.task;

public class Task {
	private Status status;
	private String name;
	private String description;

	public Task(Status status,
				String name,
				String description) {
		this.status = status;
		this.name = name;
		this.description = description;	
	}

	public Task(Status status,
				String name) {
		this.status = status;
		this.name = name;
		this.description = null;
	}

	public Task(String name,
				String description) {
		this.status = Status.OPEN;
		this.name = name;
		this.description = description;
	}

	public Task(String name) {
		this.status = Status.OPEN;
		this.name = name;
		this.description = null;
	}

	public Status getStatus()      { return this.status; }
	public String getName()        { return this.name; }
	public String getDescription() { return this.description; }

	public void setStatus(Status s) { this.status = s; }

	@Override 
	public String toString() {
		String s;
		if (this.status == Status.OPEN) { 
			s = String.format("[ ] %s%n", this.name);
		} else if (this.status == Status.IN_PROGRESS) {
			s = String.format("[.] %s%n", this.name);
		} else {
			s = String.format("[x] %s%n", this.name);
		}

		return s;
	}
} 

