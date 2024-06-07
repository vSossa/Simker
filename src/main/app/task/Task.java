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
		this.description = "";
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
		this.description = "";
	}

	public Status status()      { return this.status; }
	public String name()        { return this.name; }
	public String description() { return this.description; }

	public void setStatus(Status s) { this.status = s; }

	public String saveAsCSV() {
		String s;
		if (this.status == Status.OPEN) { 
			if (this.description.isEmpty()) {
				s = String.format("0,\"%s\",\"\"\n", 
								  this.name);
			} else {
				s = String.format("0,\"%s\",\"%s\"\n", 	
								  this.name, 
								  this.description);
			}
		} else if (this.status == Status.IN_PROGRESS) {
			if (this.description.isEmpty()) {
				s = String.format("1,\"%s\",\"\"\n", 
								  this.name);
			} else {
				s = String.format("1,\"%s\",\"%s\"\n", 	
								  this.name, 
								  this.description);
			}
		} else {
			if (this.description.isEmpty()) {
				s = String.format("2,\"%s\",\"\"\n", 
								  this.name);
			} else {
				s = String.format("2,\"%s\",\"%s\"\n", 	
								  this.name, 
								  this.description);
			}
		}

		return s;
	}

	@Override 
	public String toString() {
		String s;
		if (this.status == Status.OPEN) { 
			if (this.description.isEmpty()) {
				s = String.format("[ ] %s", 
								  this.name);
			} else {
				s = String.format("[ ] %s : %s", 	
								  this.name, 
								  this.description);
			}
		} else if (this.status == Status.IN_PROGRESS) {
			if (this.description.isEmpty()) {
				s = String.format("[.] %s", 
								  this.name);
			} else {
				s = String.format("[.] %s : %s", 	
								  this.name, 
								  this.description);
			}
		} else {
			if (this.description.isEmpty()) {
				s = String.format("[x] %s", 
								  this.name);
			} else {
				s = String.format("[x] %s : %s", 	
								  this.name, 
								  this.description);
			}
		}

		return s;
	}
} 
