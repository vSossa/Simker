package main.app; 

import main.app.task.*;
import main.app.lexer.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Simker {
	private List<Task> tasks;	

	private void todo(String message) {
		System.out.printf("TODO: %s%n", message);
	}

	public Simker() {
		this.tasks = new ArrayList<>();	
	}

	public void loadTasks(String tasksFilePath) {
		List<String> lines = null;
		try { 
			lines = Files.readAllLines(Paths.get(tasksFilePath));
		} catch (IOException e) {
			System.out.printf("ERROR: could not read tasks from '%s'%n",
							  tasksFilePath);
			System.exit(1);
		} 

		for (int i = 0; i < lines.size(); ++i) {
			Status s = null;
			String name = "";
			String description = "";

			ArrayList<String> taskComponents = Tokenizer.splitString(lines.get(i), ',');
			int len = taskComponents.size();
			String statusCode = taskComponents.get(0);
			if (statusCode.equals("0")) {
				s = Status.OPEN;
			} else if (statusCode.equals("1")) {
				s = Status.IN_PROGRESS;
			} else if (statusCode.equals("2")) {
				s = Status.CLOSED;
			} else {
				System.out.printf("%d: ERROR: invalid status code %s%n",
								  i, statusCode);
				System.exit(1);
			}
			
			int j = 1;
			name = taskComponents.get(j);
			while (Tokenizer.count(name, "\"", 0) < 2) { 
				// string
				if (Tokenizer.count(name, "\"", 0) > 1) {
					j++;	
				// ...string...
				} else {
					do {
						++j;
						name = (j == len) ?
								name :
								name.concat(" ").concat(taskComponents.get(j));
					} while (j < len && 
							 Tokenizer.count(name, "\"", 0) != 2);
				}
			}
	
			if (j == 1) ++j;	

			description = taskComponents.get(j);
			while (Tokenizer.count(name, "\"", 0) < 2) { 
				// string
				if (Tokenizer.count(name, "\"", 0) > 1) {
					j++;	
				// ...string...
				} else {
					do {
						++j;
						name = (j == len) ?
								name :
								name.concat(" ").concat(taskComponents.get(j));
					} while (j < len && 
							 Tokenizer.count(name, "\"", 0) != 2);
				}
			}

			if (Tokenizer.stripLeft(description, " ", 0) == -1) {
				this.tasks.add( new Task(s, name.replace("\"", "")) );
			} else {
				this.tasks.add( new Task(s, name.replace("\"", ""), description.replace("\"", "")) );
			}
		}
	}

	public void menu() {
		Scanner input = new Scanner(System.in);

		clear(null);
		System.out.println("==== Simker ====");
		System.out.print("> ");
		String command = input.nextLine();	
		System.out.println();
		while (parseCommandToOperation(command) != 0) { 
			System.out.println("================");
			System.out.print("> ");
			command = input.nextLine();	
			System.out.println();
		}
		input.close();
	}

	public int parseCommandToOperation(String command) { 
		int returnStatus = 1;
		ArrayList<Token> tokens = Tokenizer.getTokens(command);

		if (tokens == null) return returnStatus;
		
		Token op = tokens.get(0);
		switch (op.getStringValue()) {
		case "add": {
			addTask(tokens);	
			break;
		}	

		case "mark": {
			markTask(tokens);
			break;
		}

		case "clear": {
			clear(tokens);
			break;
		}

		case "help": {
			usage(tokens);
			break;
		}

		case "quit": {
			returnStatus = quit(tokens);
			break;
		}

		case "ls": {
			listTasks(tokens);	
			break;
		}

		case "load": {
			loadTasks(tokens);
			break;
		}

		case "rm": {
			removeTasks(tokens);
			break;
		}

		case "reset": {
			resetTasks(tokens);
			break;
		}

		default: {
			System.out.printf("%d: ERROR: unknow command: %s%n", 
							  op.getIndex(),
							  op.getStringValue());
			System.out.println("TIP: type 'help' for help");
		}
		}	

		return returnStatus;
	}

	public void resetTasks(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			System.out.println("Reseting...");
			this.tasks.clear();
			break;
		}

		default: {
			System.out.printf("%d: ERROR: too many arguments for '%s'%n",
							  args.get(0).getIndex(),
							  args.get(0).getStringValue());
		}
		}
	} 

	public void loadTasks(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			System.out.printf("%d: ERROR: missing file path for '%s' command%n",
							  args.get(0).getIndex(),
						      args.get(0).getStringValue());
			break;
		}

		case 2: {
		    Token filePath = args.get(1);
			if (filePath.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got '%s'%n",
								  filePath.getIndex(),
								  filePath.getStringValue());
				return ;
			}
			if (!filePath.getStringValue().endsWith(".csv")) {
				System.out.printf("%d: ERROR: expected a csv file: '%s'%n",
								  filePath.getIndex(),
								  filePath.getStringValue());
				return ;
			}

			loadTasks(filePath.getStringValue());
			System.out.println("Loading tasks...");
			break;	
		}

		default: {
			System.out.printf("ERROR: too many arguments for '%s'%n",
							  args.get(0).getStringValue());
		}
		}
	}

	public void clear(ArrayList<Token> args) {
		if (args == null || args.size() == 1) {
			System.out.printf("\033[H\033[J");
		} else {
			System.out.printf("ERROR: too many arguments for '%s'%n",
							  args.get(0).getStringValue());
		}
	}

	public void usage(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			System.out.println("COMMANDS:");
			System.out.println("    add <name> [description]                         add a new task");
			System.out.println("    mark <index> <status>                            mark the index-task with the given status");
			System.out.println("    clear                                            clear screen");
			System.out.println("    ls                                               list tasks");
			System.out.println("    reset                                            equivalent to 'rm --all'");
			System.out.println("    rm <-a | --all | index | indexBegin indexEnd>    remove index-task or all or all of the tasks between indexBegin and indexEnd, inclusive");
			System.out.println("    quit [-o <fileName.csv>]                         quit Simker and, optinally saves the tasks in a csv file ");
			System.out.println("    help [-v | --verbose]                            show this message");
			break;
		}

		case 2: {
			Token subcommand = args.get(1);
			if (subcommand.getStringValue().equals("-v") || 
				subcommand.getStringValue().equals("--verbose")) {
				todo("verbose usage message");
			} else {
				System.out.printf("%d: ERROR: unknow subcommand '%s'%n",
								  subcommand.getIndex(),
								  subcommand.getStringValue());
			}
			break;
		}

		default: {
			System.out.printf("ERROR: too many arguments for '%s'%n",
							  args.get(0).getStringValue());
		}	
		}
	}

	public void removeTasks(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			System.out.printf("ERROR: not enough arguments for '%s'%n",
							  args.get(0).getStringValue());
			break;
		}

		case 2: {
			removeTask(args.get(1));
			break;
		}

		case 3: {
			removeTaskRange(args.get(1),
							args.get(2));
			break;
		}

		default: {
			System.out.printf("ERROR: too many arguments for '%s'%n",
							  args.get(0).getStringValue());
		}
		}
	}

	public void addTask(ArrayList<Token> args) { 
		switch (args.size()) {
		case 1: {
			System.out.printf("ERROR: not enough arguments for '%s'%n",
							  args.get(0).getStringValue());
			break;
		}

		case 2: {
			Token t = args.get(1);
			if (t.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got: '%s'%n",
								  t.getIndex(),
								  t.getType());
				return ;
			} 

			String name = t.getStringValue();
			if (Tokenizer.stripLeft(name, " ", 0) == -1) {
				System.out.println("ERROR: a task must have at least one character");
				return ;
			}

			this.tasks.add( new Task(name) );	
			break;
		}

		case 3: {
			Token t1 = args.get(1);
			Token t2 = args.get(2);
			if (t1.getType() != TokenType.STRING ) {
				System.out.printf("%d: ERROR: expected STRING but got: '%s'%n",
								  t1.getIndex(),
								  t1.getType());
				return ;
			} 
			if (t2.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got: '%s'%n",
								  t2.getIndex(),
								  t2.getType());
				return ;
			} 

			String name = t1.getStringValue();
			if (Tokenizer.stripLeft(name, " ", 0) == -1) {
				System.out.println("ERROR: a task must have at least one character");
				return ;
			}

			String description = t2.getStringValue();
			if (Tokenizer.stripLeft(name, " ", 0) == -1) { 
				this.tasks.add( new Task(name) );	
			} else {
				this.tasks.add( new Task(name, description) );	
			}
			break;
		}

		default: {
			System.out.printf("ERROR: too many arguments for '%s'%n",
							   args.get(0).getStringValue());
		}
		}
	}

	public void markTask(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: case 2: {
			System.out.printf("ERROR: missing arguments for '%s'%n",
							  args.get(0).getStringValue());
			break;
		}

		case 3: {
			markTask(args.get(1), 
					 args.get(2));
			break;
		}

		default: {
			System.out.printf("ERROR: too many arguments for '%s'%n",
							  args.get(0).getStringValue());
		}
		}
	}

	public void listTasks(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			if (this.tasks.isEmpty()) {
				System.out.println("**empty**");
			} else {
				for (int i = 0; i < this.tasks.size(); ++i) {
					System.out.println(i + ". " + this.tasks.get(i));
				}
			}
			break;
		}
		
		default: {
			System.out.printf("ERROR: too many arguments for '%s'%n",
							  args.get(0).getStringValue());
		}
		}
	}

	public int quit(ArrayList<Token> args) {
		int returnStatus = 1;
		switch (args.size()) {
		case 1: {
			System.out.println("Goodbye!");
			returnStatus = 0;
			break;
		}

		case 2: {
			Token arg = args.get(1);
			if (arg.getType() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got '%s'%n",
								  arg.getIndex(),
								  arg.getType());
				break;
			} 

			if (arg.getStringValue().equals("-o") || 
				arg.getStringValue().equals("--output")) {
				System.out.printf("%d: ERROR: missing file for '%s'%n",
								  arg.getIndex(),
								  arg.getStringValue());
			} else {
				System.out.printf("%d: ERROR: unknow subcommand '%s'%n",
								  arg.getIndex(),
								  arg.getStringValue());
			}
			break;
		}

		case 3: {
			Token subcommand = args.get(1);
			if (subcommand.getType() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got '%s'%n",
								  subcommand.getIndex(),
								  subcommand.getType());
				break;
			}
			if (!(subcommand.getStringValue().equals("-o") || 
				subcommand.getStringValue().equals("--output"))) {
				System.out.printf("%d: ERROR: unknow subcommand '%s'%n",
								  subcommand.getIndex(),
								  subcommand.getStringValue());
				break;
			} 				

			Token file = args.get(2);	
			if (file.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got '%s'%n",
								  file.getIndex(),
								  file.getType());
				break;
			}
			if (!file.getStringValue().endsWith(".csv")) {
				System.out.printf("%d: ERROR: expected a csv file but got '%s'%n",
								  file.getIndex(),
								  file.getStringValue());
				break;
			}

			String filePath = file.getStringValue();
			if (saveTasks(filePath)) { 
				System.out.printf("Saving tasks into %s...%n",
								  filePath);
				System.out.println("Goodbye!");
				returnStatus = 0;
			} else {
				System.out.printf("ERROR: could not write into the file: '%s'%n",
								   filePath);
			}
			break;
		}

		default: {
			System.out.printf("%d: ERROR: too many arguments for '%s' command%n",
							  args.get(1).getIndex(),
						      args.get(1).getStringValue());
		}
		}

		return returnStatus;
	}

	private boolean saveTasks(String filePath) {
		boolean sucess = true;
		Path file = Paths.get("./", filePath);	

		if (Files.exists(file)) {
			try {
				Files.delete(file);
				Files.createFile(file);
				writeTasksIntoFile(file);
			} catch (IOException e) {
				sucess = false;
			}
		} else {
			try {
				Files.createFile(file);
				writeTasksIntoFile(file);
			} catch (IOException e) {
				System.out.printf("ERROR: could not write into the file: '%s'%n",
								   filePath);
				sucess = false;
			}
		}

		return sucess;
 	}

	private void writeTasksIntoFile(Path file) throws IOException {
		for (Task task : this.tasks) {
			if (task.getStatus() == Status.OPEN) {
				if (task.getDescription().isEmpty()) {
					Files.writeString(file, 
									  String.format("0,\"%s\",\"\"\n", task.getName()),
									  StandardOpenOption.APPEND);			
				} else {
					Files.writeString(file, 
									  String.format("0,\"%s\",\"%s\"\n", task.getName(), task.getDescription()),
									  StandardOpenOption.APPEND);			
				}
			} else if (task.getStatus() == Status.IN_PROGRESS) {
				if (task.getDescription().isEmpty()) {
					Files.writeString(file, 
									  String.format("1,\"%s\",\"\"\n", task.getName()),
									  StandardOpenOption.APPEND);			
				} else {
					Files.writeString(file, 
									  String.format("1,\"%s\",\"%s\"\n", task.getName(), task.getDescription()),
									  StandardOpenOption.APPEND);			
				}
			} else {
				if (task.getDescription().isEmpty()) {
					Files.writeString(file, 
									  String.format("2,\"%s\",\"\"\n", task.getName()),
									  StandardOpenOption.APPEND);			
				} else {
					Files.writeString(file, 
									  String.format("2,\"%s\",\"%s\"\n", task.getName(), task.getDescription()),
									  StandardOpenOption.APPEND);			
				}
			}
		}
	}

	private void removeTask(Token arg) {
		switch (arg.getType()) {
		case INT: {
			int index = Integer.parseInt(arg.getStringValue());
			if (isOutOfBounds(index)) {
				System.out.printf("%d: ERROR: %d is out of bounds%n",
								  arg.getIndex(),
								  index);
				return ;
			}

			System.out.printf("Removing %d-task...%n",
							  index);
			this.tasks.remove(index);
			break;
		}

		case COMMAND: {
			if (arg.getStringValue().equals("--all") ||
				arg.getStringValue().equals("-a")) {
				System.out.printf("Removing all tasks...%n");
				this.tasks.clear();
			} else {
				System.out.printf("%d: ERROR: unknow subcommand: '%s'%n",
								  arg.getIndex(), 
								  arg.getStringValue());
			}
			break;
		} 

		default: {
			System.out.printf("%d: ERROR: unknow subcommand: '%s'%n",
						      arg.getIndex(),
							  arg.getStringValue());
		}
		}
	} 

	private void removeTaskRange(Token indexStart, Token indexEnd) {
		if (indexStart.getType() != TokenType.INT) {
			System.out.printf("%d: ERROR: expected INT but got: %s%n", 
							  indexStart.getIndex(),
							  indexStart.getStringValue()); 
			return ;
		}	

		if (indexEnd.getType() != TokenType.INT) {
			System.out.printf("%d: ERROR: expected INT but got: %s%n", 
							  indexEnd.getIndex(),
							  indexEnd.getStringValue()); 
			return ;
		}

		int i, j;	
		i = Integer.parseInt(indexStart.getStringValue());
		if (isOutOfBounds(i)) {
			System.out.printf("%d: ERROR: %d is out of bounds%n", 
							  indexStart.getIndex(),
							  i); 
			return ;
		}

		j = Integer.parseInt(indexEnd.getStringValue());
		if (isOutOfBounds(j)) {
			System.out.printf("%d: ERROR: %d is out of bounds%n", 
							  indexEnd.getIndex(),
							  j); 
			return ;
		}

		if (j < i) {
			System.out.printf("ERROR: invalid range: %d < %d%n", 
							  j, i);
			return ;
		}	

		System.out.printf("Removing all tasks between %d and %d%n",
						  i, j);

		int start = i;
		while (i <= j) {
			this.tasks.remove(start);
			++i;	
		}
	}

	private void markTask(Token index, Token status) {
		if (index.getType() != TokenType.INT) {
			System.out.printf("%d: ERROR: expected INT but got: %s%n",
							  index.getIndex(),
							  index.getType());
			return ;
		} 

		int i = Integer.parseInt(index.getStringValue());
		if (isOutOfBounds(i)) {
			System.out.printf("%d: ERROR: '%d' is out of bounds%n",
							  index.getIndex(),
							  i);
			return ;
		} 	

		switch (status.getType()) {
		case INT: {
			int statusValue = Integer.parseInt(status.getStringValue());
			Status s = statusValueToStatus(statusValue);
			if (s == null) {
				System.out.printf("%d: ERROR: invalid status code: '%s'%n",
								  status.getIndex(),
								  statusValue);
			} else {
				Task t = this.tasks.get(i);
				t.setStatus(s);	
				this.tasks.set(i, t);
			}
			break;
		}

		case COMMAND: {
			String statusString = status.getStringValue();
			Status s = statusStringToStatus(statusString);
			if (s == null) {
				System.out.printf("%d: ERROR: invalid subcommand: '%s'%n",
								  status.getIndex(),
								  statusString);
			} else {
				Task t = this.tasks.get(i);
				t.setStatus(s);	
				this.tasks.set(i, t);
			}
			break;
		}

		default: {
			System.out.printf("%d: ERROR: expected COMMAND or INT: %s%n",
							  status.getIndex(),
							  status.getType());
		}
		}
	}

	private boolean isOutOfBounds(int index) {
		return 0 > index || index >= this.tasks.size(); 
	}

	private Status statusValueToStatus(int statusValue) {
		Status s;
		switch (statusValue) {
		case 0: {
			s = Status.OPEN;	
			break;
		}

		case 1: {
			s = Status.IN_PROGRESS;
			break;
		}

		case 2: {
			s = Status.CLOSED;
			break;
		}
		
		default: {
			s = null;
		}
		}

		return s;
	}

	private Status statusStringToStatus(String statusString) {
		Status s;
		switch (statusString) {
		case "--open": case "-o": {
			s = Status.OPEN;	
			break;
		} 

		case "--in-progress": case "-i": {
			s = Status.IN_PROGRESS;
			break;
		}
	
		case "--closed": case "-c": {
			s = Status.CLOSED;
			break;
		}
		
		default: {
			s = null;
		}
		}

		return s;
	}
}
