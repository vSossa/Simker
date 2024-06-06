package main.app; 

import main.app.task.*;
import main.app.lexer.*;

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
		ArrayList<Token> tokens = Tokenizer.getTokens(command);

		if (tokens == null) return 1;
		
		Token op = tokens.get(0);
		switch (op.getStringValue()) {
		case "a": case "add": {
			addTask(tokens);	
			break;
		}	

		case "m": case "mark": {
			markTask(tokens);
			break;
		}

		case "clear": {
			clear(tokens);
			break;
		}

		case "h": case "help": {
			usage(tokens);
			break;
		}

		case "q": case "quit": {
			quit(tokens);
			return 0;
		}

		case "ls": {
			listTasks(tokens);	
			break;
		}

		case "rm": {
			removeTasks(tokens);
			break;
		}

		default: {
			System.out.printf("%d: ERROR: unknow command: %s%n", 
							  op.getIndex(),
							  op.getStringValue());
			System.out.println("TIP: type 'h' or 'help' for help");
		}
		}	

		return 1;
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
			System.out.println("    a, add <name> [description]                      add a new task");
			System.out.println("    m, mark <index> <status>                         mark the index-task with the given status");
			System.out.println("    clear                                            clear screen");
			System.out.println("    ls                                               list tasks");
			System.out.println("    rm <-a | --all | index | indexBegin indexEnd>    remove index-task or all or all of the tasks between indexBegin and indexEnd, inclusive");
			System.out.println("    q, quit                                          quit Simker");
			System.out.println("    h, help [-v | --verbose]                         show this message");
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
			Token name = args.get(1);
			if (name.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got: '%s'%n",
								  name.getIndex(),
								  name.getType());
			} else {
				this.tasks.add( new Task(name.getStringValue()) );	
			}
			break;
		}

		case 3: {
			Token name = args.get(1);
			Token description = args.get(2);
			if (name.getType() != TokenType.STRING ) {
				System.out.printf("%d: ERROR: expected STRING but got: '%s'%n",
								  name.getIndex(),
								  name.getType());
			} else if (description.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got: '%s'%n",
								  description.getIndex(),
								  description.getType());
			} else {
				this.tasks.add( new Task(name.getStringValue(), description.getStringValue()) );	
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
		// TODO: allow the user to specify the type of task
		// that they wanna see, the number, if it's from the
		// beginning or the end. 
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

	public void quit(ArrayList<Token> args) {
		// TODO: allow the user to save the tasks to a file
		if (args.size() > 1) {
			System.out.printf("%d: ERROR: '%s' dont't accept any arguments%n",
							  args.get(1).getIndex(),
							  args.get(0).getStringValue());
		} else {
			System.out.println("Goodbye!");
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
		while (i <= j) {
			this.tasks.remove(i);
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
