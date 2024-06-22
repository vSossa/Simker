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

	public String toCSVFormat() {
		return (this.description.isEmpty()) ?
			String.format("%d,\"%s\",\"\"\n",
                          this.status.value(),
                          this.name) :
            String.format("%d,\"%s\",\"%s\"\n",
                          this.status.value(),
                          this.name,
                          this.description);
	}

	@Override 
	public String toString() {
		String taskString = (this.description.isEmpty()) ?
			this.name : 
            String.format("%s : %s", 
                          this.name, 
                          this.description);
				
		if (this.status == Status.OPEN) { 
			taskString = "[ ] ".concat(taskString);
		} else if (this.status == Status.IN_PROGRESS) {
			taskString = "[.] ".concat(taskString);
		} else {
			taskString = "[x] ".concat(taskString);
		}

		return taskString;
	}
} 
