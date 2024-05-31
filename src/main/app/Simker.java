package main.app; 

import main.app.task.*;
import main.app.lexer.*;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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

		if (tokens.isEmpty()) return 1;
		
		Token op = tokens.get(0);
		switch (op.getType()) {
			case COMMAND: {
				switch (op.getValue()) { // see if it's a valid operation
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
										  op.getLocation(),
										  op.getValue());
						System.out.println("TIP: type 'h' or 'help' for help");
					}
				}
				break;
			}

			default: {
				System.out.printf("%d: ERROR: unkown command '%s'%n", 
								  op.getLocation(),
								  op.getValue());
				System.out.println("TIP: type 'h' or 'help' for help");
			}
		}

		return 1;
	}

	private void usage(ArrayList<Token> args) {
		if (args.size() > 1) {
			System.out.printf("%d: ERROR: '%s' don't accept any arguments%n",
							  args.get(1).getLocation(),
							  args.get(0).getValue());
		} else {
			System.out.println("COMMANDS:");
				System.out.println("a, add [status] <name> [description]        add a new task to Simker");
				System.out.println("m, mark <index> <status>                    mark the index-task with the given status");
				System.out.println("s, show                                     show tasks");
				System.out.println("q, quit                                     quit Simker");
				System.out.println("h, help                                     show this message");
		}
	}

	private void addTask(ArrayList<Token> args) { 
		Token operationName = args.remove(0);

		switch (args.size()) {
			case 0: {
				System.out.printf("%d: ERROR: missing arguments for '%s'%n",
								  operationName.getLocation(),
								  operationName.getValue());
				break;
			} 

			case 1: {
				Token arg = args.get(0);				
				if (arg.getType() != TokenType.STRING) {
					System.out.printf("%d: ERROR: expected STRING, but got %s%n",
									  arg.getLocation(),
									  arg.getType());	
				} else {
					this.tasks.add( new Task(arg.getValue()) );	
				}
				break;
			}
		
			case 2: {
				Token arg1 = args.get(0);	
				Token arg2 = args.get(1);
				if (arg1.getType() == TokenType.INT) { // status and name
					if (arg2.getType() != TokenType.STRING) {
						System.out.printf("%d: ERROR: expected STRING, but got %s%n",
										  arg2.getLocation(),
										  arg2.getType());
					} else {			
						int statusValue;
						try {
							statusValue = Integer.parseInt(arg1.getValue());	
						} catch (NumberFormatException e) {
							System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%s'%n",
											  arg1.getLocation(),
											  arg1.getValue());
							break;
						}				
						Status s = statusValueToStatus(statusValue);	
						if (s == null) {
							System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%d'%n",
											  arg1.getLocation(),
											  statusValue);
						} else {
							this.tasks.add( new Task(s, arg1.getValue()) );
						}
					}
				} else if (arg1.getType() == TokenType.STRING) { // name and description
					if (arg2.getType() != TokenType.STRING) {
						System.out.printf("%d: ERROR: expected STRING, but got %s%n",
									      arg2.getLocation(),
										  arg2.getValue());	
					} else {
						this.tasks.add( new Task(arg1.getValue(), arg2.getValue()) );	
					}
				} else { // unknow
					System.out.printf("%d: ERROR: expected INT or STRING, but got %s%n",
								      arg1.getLocation(),
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
									  arg1.getLocation(),
									  arg1.getType());
					break;
				}

				if (arg2.getType() != TokenType.STRING) {
					System.out.printf("%d: ERROR: expected STRING, but got %s%n",
									  arg2.getLocation(),
									  arg2.getType());
					break;
				}

				if (arg3.getType() != TokenType.STRING) {
					System.out.printf("%d: ERROR: expected STRING, but got %s%n",
									  arg3.getLocation(),
									  arg3.getType());
					break;
				}

				int statusValue;
				try {
					statusValue = Integer.parseInt(arg1.getValue());	
				} catch (NumberFormatException e) {
					System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%s'%n",
									  arg1.getLocation(),
									  arg1.getValue());
					break;
				}				
				Status s = statusValueToStatus(statusValue);	
				if (s == null) {
					System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%d'%n",
									  arg1.getLocation(),
									  statusValue);
				} else {
					this.tasks.add( new Task(s, arg2.getValue(), arg3.getValue()) );
					System.out.printf("Task added with sucess!");
				}
				break;
			}

			default: {
				System.out.printf("%d: ERROR: too many arguments for '%s'%n",
								 args.get(3).getLocation(),
								 operationName.getValue());
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
							  args.get(1).getLocation(),
							  operationName.getValue());
		} else if (len > 2) {
			System.out.printf("%d: ERROR: too many arguments for '%s'%n",
							  args.get(2).getLocation(),
							  operationName.getValue());
		} else {
			Token arg1 = args.get(0);
			Token arg2 = args.get(1);

			if (arg1.getType() != TokenType.INT) {
				System.out.printf("%d: ERROR: expected INT, but got %s%n",
								  arg1.getLocation(),
								  arg1.getType());
				return ;
			}

			if (arg2.getType() != TokenType.INT) {
				System.out.printf("%d: ERROR: expected INT, but got %s%n",
								  arg2.getLocation(),
								  arg2.getType());
				return ;
			}
			int index;
			try {
				index = Integer.parseUnsignedInt(arg1.getValue());
			} catch (NumberFormatException e) {
				System.out.printf("%d: ERROR: expected a non-negative value, but got %s%n", 
								  arg1.getLocation(),
								  arg1.getValue());
				return ;
			}
			if (!isValidIndex(index)) {
				System.out.printf("%d: ERROR: '%d' is out of range%n",
								  arg1.getLocation(),
								  index);	
			} else { 
				int statusValue;
				try {
					statusValue = Integer.parseInt(arg2.getValue());	
				} catch (NumberFormatException e) {
					System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%s'%n",
									  arg2.getLocation(),
									  arg2.getValue());
					return ;
				}				
				Status s = statusValueToStatus(statusValue);	
				if (s == null) {
					System.out.printf("%d: ERROR: expected 0, 1 or 2, but got '%d'%n",
									  arg2.getLocation(),
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
							  args.get(1).getLocation(),
							  args.get(0).getValue());
		} else {
			System.out.println("Goodbye!");
		}
	}
}
