package main.app; 

import main.app.task.*;
import main.app.lexer.*;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Simker {
	private List<Task> tasks;	

	public Simker() {
		this.tasks = new ArrayList<>();	
	}

	private void todo(String message) {
		System.out.printf("TODO: %s%n", message);
	}

	public void menu() {
		Scanner input = new Scanner(System.in);

		for (;;) {
			System.out.println("==== Simker ====");
			System.out.print("> ");
			String command = input.nextLine();	
			System.out.println();
			if (parseCommandToOperation(command) == 0) break;	
			System.out.println();
		}
		input.close();
	}

	private int parseCommandToOperation(String command) { 
		ArrayList<Token> tokens = Tokenizer.getTokens(command);

		if (tokens == null) return 1;
		
		Token op = tokens.get(0);
		switch (op.getType()) {
		case COMMAND: {
			switch (op.getStringValue()) { // see if it's a valid operation
			case "a": case "add": {
				addTask(tokens);	
				break;
			}	

			case "m": case "mark": {
				markTask(tokens);
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

			case "s": case "show": {
				showTasks(tokens);	
				break;
			}

			default: {
				System.out.printf("%d: ERROR: unkown command '%s'%n", 
								  op.getIndex(),
								  op.getStringValue());
				System.out.println("TIP: type 'h' or 'help' for help");
			}
			}
			break;
		}

		default: {
			System.out.printf("%d: ERROR: unkown command '%s'%n", 
							  op.getIndex(),
							  op.getStringValue());
			System.out.println("TIP: type 'h' or 'help' for help");
		}
		}

		return 1;
	}

	private void usage(ArrayList<Token> args) {
		if (args.size() > 1) {
			System.out.printf("%d: ERROR: '%s' don't accept any arguments%n",
							  args.get(1).getIndex(),
							  args.get(0).getStringValue());
		} else {
			System.out.println("COMMANDS:");
				System.out.println("    a, add [status] <name> [description]        add a new task to Simker");
				System.out.println("    m, mark <index> <status>                    mark the index-task with the given status");
				System.out.println("    s, show                                     show tasks");
				System.out.println("    q, quit                                     quit Simker");
				System.out.println("    h, help                                     show this message");
		}
	}

	private void addTask(ArrayList<Token> args) { 
		Token operationName = args.remove(0);

		switch (args.size()) {
		case 0: {
			System.out.printf("%d: ERROR: missing arguments for '%s'%n",
							  operationName.getIndex(),
							  operationName.getStringValue());
			break;
		} 

		case 1: {
			Token arg = args.get(0);				
			if (arg.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING, but got %s%n",
								  arg.getIndex(),
								  arg.getType());	
			} else {
				this.tasks.add( new Task(arg.getStringValue()) );	
			}
			break;
		}
	
		case 2: {
			Token arg1 = args.get(0);	
			Token arg2 = args.get(1);
			if (arg1.getType() == TokenType.INT) { // status and name
				if (arg2.getType() != TokenType.STRING) {
					System.out.printf("%d: ERROR: expected STRING, but got %s%n",
									  arg2.getIndex(),
									  arg2.getType());
				} else {			
					int statusValue;
					try {
						statusValue = Integer.parseInt(arg1.getStringValue());	
					} catch (NumberFormatException e) {
						System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%s'%n",
										  arg1.getIndex(),
										  arg1.getStringValue());
						break;
					}				
					Status s = statusValueToStatus(statusValue);	
					if (s == null) {
						System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%d'%n",
										  arg1.getIndex(),
										  statusValue);
					} else {
						this.tasks.add( new Task(s, arg1.getStringValue()) );
					}
				}
			} else if (arg1.getType() == TokenType.STRING) { // name and description
				if (arg2.getType() != TokenType.STRING) {
					System.out.printf("%d: ERROR: expected STRING, but got %s%n",
									  arg2.getIndex(),
									  arg2.getStringValue());	
				} else {
					this.tasks.add( new Task(arg1.getStringValue(), arg2.getStringValue()) );	
				}
			} else { // unknow
				System.out.printf("%d: ERROR: expected INT or STRING, but got %s%n",
								  arg1.getIndex(),
								  arg1.getType());
			}
			break;
		}

		case 3: { 
			Token arg1 = args.get(0);
			Token arg2 = args.get(1);
			Token arg3 = args.get(2);
			if (arg1.getType() != TokenType.INT) {
				System.out.printf("%d: ERROR: expected INT, but got %s%n",
								  arg1.getIndex(),
								  arg1.getType());
				break;
			}

			if (arg2.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING, but got %s%n",
								  arg2.getIndex(),
								  arg2.getType());
				break;
			}

			if (arg3.getType() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING, but got %s%n",
								  arg3.getIndex(),
								  arg3.getType());
				break;
			}

			int statusValue;
			try {
				statusValue = Integer.parseInt(arg1.getStringValue());	
			} catch (NumberFormatException e) {
				System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%s'%n",
								  arg1.getIndex(),
								  arg1.getStringValue());
				break;
			}				
			Status s = statusValueToStatus(statusValue);	
			if (s == null) {
				System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%d'%n",
								  arg1.getIndex(),
								  statusValue);
			} else {
				this.tasks.add( new Task(s, arg2.getStringValue(), arg3.getStringValue()) );
			}
			break;
		}

		default: {
			System.out.printf("%d: ERROR: too many arguments for '%s'%n",
							 args.get(3).getIndex(),
							 operationName.getStringValue());
		}
		}
	}

	private Status statusValueToStatus(int statusValue) {
		Status s;
		if (statusValue == 0) {
			s = Status.OPEN;	
		} else if (statusValue == 1) {
			s = Status.IN_PROGRESS;
		} else if (statusValue == 2) {
			s = Status.CLOSED;
		} else {
			s = null;
		}

		return s;
	}

	private void markTask(ArrayList<Token> args) {
		Token operationName = args.remove(0);
		
		int len = args.size();
		if (len < 2) {
			System.out.printf("%d: ERROR: missing arguments for '%s'%n",
							  args.get(1).getIndex(),
							  operationName.getStringValue());
		} else if (len > 2) {
			System.out.printf("%d: ERROR: too many arguments for '%s'%n",
							  args.get(2).getIndex(),
							  operationName.getStringValue());
		} else {
			Token arg1 = args.get(0);
			Token arg2 = args.get(1);

			if (arg1.getType() != TokenType.INT) {
				System.out.printf("%d: ERROR: expected INT, but got %s%n",
								  arg1.getIndex(),
								  arg1.getType());
				return ;
			}

			if (arg2.getType() != TokenType.INT) {
				System.out.printf("%d: ERROR: expected INT, but got %s%n",
								  arg2.getIndex(),
								  arg2.getType());
				return ;
			}

			int index;
			try {
				index = Integer.parseUnsignedInt(arg1.getStringValue());
			} catch (NumberFormatException e) {
				System.out.printf("%d: ERROR: expected a non-negative value, but got %s%n", 
								  arg1.getIndex(),
								  arg1.getStringValue());
				return ;
			}
			if (!isValidIndex(index)) {
				System.out.printf("%d: ERROR: '%d' is out of range%n",
								  arg1.getIndex(),
								  index);	
			} else { 
				int statusValue;
				try {
					statusValue = Integer.parseInt(arg2.getStringValue());	
				} catch (NumberFormatException e) {
					System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%s'%n",
									  arg2.getIndex(),
									  arg2.getStringValue());
					return ;
				}				
				Status s = statusValueToStatus(statusValue);	
				if (s == null) {
					System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%d'%n",
									  arg2.getIndex(),
									  statusValue);
				} else {
					Task t = this.tasks.get(index);
					t.setStatus(s);	
					this.tasks.set(index, t);
				}
			}	
		}
	}

	private boolean isValidIndex(int index) {
		return (0 <= index && index < this.tasks.size());
	}

	private void showTasks(ArrayList<Token> args) {
		// TODO: allow the user to specify the type of task
		// that they wanna see, the number, if it's from the
		// beginning or the end. 
		Token operationName = args.remove(0);
		
		switch (args.size()) {
		case 0: {
			for (Task task : this.tasks) {
				System.out.println(task);
			}
			break;
		}
		
		default: {
			break;	
		}
		}
	}

	private void quit(ArrayList<Token> args) {
		// TODO: allow the user to save the tasks to a file
		if (args.size() > 1) {
			System.out.printf("%d: ERROR: '%s' dont't accept any arguments%n",
							  args.get(1).getIndex(),
							  args.get(0).getStringValue());
		} else {
			System.out.println("Goodbye!");
		}
	}
}
