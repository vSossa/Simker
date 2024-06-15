package main.app; 

import main.app.task.*;
import main.app.lexer.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
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
			System.out.printf("ERROR: could not read tasks from `%s`%n",
							  tasksFilePath);
			System.exit(1);
		} 

		for (String line : lines) {
			Status s = null;
			String name = "";
			String description = "";

			ArrayList<String> taskComponents = Tokenizer.splitString(line, ',');
			int len = taskComponents.size();
			String statusValue = taskComponents.get(0);
			s = statusValueToStatus(Integer.parseInt(statusValue));

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
			while (Tokenizer.count(description, "\"", 0) < 2) { 
				// string
				if (Tokenizer.count(description, "\"", 0) > 1) {
					j++;	
				// ...string...
				} else {
					do {
						++j;
						description = (j == len) ?
								description :
								description.concat(" ").concat(taskComponents.get(j));
					} while (j < len && 
							 Tokenizer.count(description, "\"", 0) != 2);
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
		System.out.println("Bye!");
		input.close();
	}

	public int parseCommandToOperation(String command) { 
		int returnStatus = 1;
		ArrayList<Token> tokens = Tokenizer.getTokens(command);

		if (tokens == null) return returnStatus;
		
		Token op = tokens.get(0);
		switch (op.value()) {
		case "add": {
			addTask(tokens);	
			break;
		}	

		case "mark": {
			markTask(tokens);
			break;
		}

		case "progress": {
			progressTask(tokens);
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

		case "exit": case "quit": {
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

		case "save": {
			saveTasks(tokens);
			break;
		}

		case "select": {
			selectTasksAndDoCommand(tokens);
			break;
		}

		case "reset": {
			resetTasks(tokens);
			break;
		}

		default: {
			System.out.printf("%d: ERROR: unknow command: %s%n", 
							  op.index(),
							  op.value());
			System.out.println("TIP: type `help` for help");
		}
		}	

		return returnStatus;
	}

	public void progressTask(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			System.out.printf("ERROR: missing task index%n");
			break;
		}

		case 2: {
			Token indexToken = args.get(1);
			if (indexToken.type() != TokenType.INT) {
				System.out.printf("%d: ERROR: expected an index: `%s`%n",
								  indexToken.index(),
								  indexToken.value());
				break;
			}				

			int index = Integer.parseInt(indexToken.value());
			if (isOutOfBounds(index)) {
				System.out.printf("%d: ERROR: out of bounds: `%d`%n",
								  indexToken.index(),
								  index);
			} else {
				Task t = this.tasks.get(index);
				Status s = t.status();
				
				if (s == Status.OPEN) {
					t.setStatus(Status.IN_PROGRESS);	
					this.tasks.set(index, t);
				} else if (s == Status.IN_PROGRESS) {
					t.setStatus(Status.CLOSED);	
					this.tasks.set(index, t);
				} else {
					this.tasks.remove(t);
				}
			}
			break;
		} 

		default: {
			System.out.printf("ERROR: too many arguments%n");
		}
		}
	}

	public void resetTasks(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			System.out.println("Reseting...");
			this.tasks.clear();
			break;
		}

		default: {
			System.out.printf("%d: ERROR: too many arguments for `%s`%n",
							  args.get(0).index(),
							  args.get(0).value());
		}
		}
	} 

	public void loadTasks(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			loadTasks("tasks.csv");
			System.out.println("Loading tasks...");
			break;
		}

		case 2: {
		    Token filePath = args.get(1);
			if (filePath.type() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got `%s`%n",
								  filePath.index(),
								  filePath.value());
				break;
			}
			if (!filePath.value().endsWith(".csv")) {
				System.out.printf("%d: ERROR: expected a csv file: `%s`%n",
								  filePath.index(),
								  filePath.value());
				break;
			}

			loadTasks(filePath.value());
			System.out.println("Loading tasks...");
			break;	
		}

		default: {
			System.out.printf("ERROR: too many arguments for `%s`%n",
							  args.get(0).value());
		}
		}
	}

	public void selectTasksAndDoCommand(ArrayList<Token> args) {
		ArrayList<Token> tokensToCommand = new ArrayList<>();

		int len = args.size();
		if (len <= 2) { 
			System.out.printf("ERROR: not enough arguments%n");
			return ;
		} 
	
		int i = 1;
		while (i < len && args.get(i).type() != TokenType.COMMAND) {
			Token arg = args.get(i);
			if (arg.type() != TokenType.INT) {
				System.out.printf("%d: ERROR: not an integer: `%s`%n",
                                  i, arg.value());
				return ;
			}

			if (isOutOfBounds(Integer.parseInt(arg.value()))) { 
				System.out.printf("%d: ERROR: out of bounds: `%s`%n",
                                  i, arg.value());
				return ;
			}
			tokensToCommand.add(arg);
			++i;
		}

		if (i == len) {
			System.out.printf("ERROR: missing command%n");
			return ;
		} 

		tokensToCommand = sortTokenIndexes(tokensToCommand);	

		Token command = args.get(i);
		switch (command.value()) {
		case "progress": {
			if (i != len - 1) {
				System.out.printf("%d: ERROR: too many arguments for `%s`%n",
                                  command.index(), 
                                  command.value());
				break;
			}

			ArrayList<Token> progressAndIndex = new ArrayList<>();
				progressAndIndex.add(command);
				progressAndIndex.add(null);
			for (int j = 0; j < tokensToCommand.size(); ++j) {
				progressAndIndex.set(1, tokensToCommand.get(j));
				progressTask(progressAndIndex);	
			}
			break;
		}
        
		case "rm": {
			if (i != len - 1) {
				System.out.printf("%d: ERROR: too many arguments for `%s`%n",
								  command.index(),
                                  command.value());
				break;
			}

			ArrayList<Token> removeAndIndex = new ArrayList<>();
				removeAndIndex.add(command);
				removeAndIndex.add(null);
			for (int j = 0; j < tokensToCommand.size(); ++j) {
				removeAndIndex.set(1, tokensToCommand.get(j));
				removeTasks(removeAndIndex);	
			}
			break;
		}

		case "mark": {
			if (i == len - 1) { 
				System.out.printf("%d: ERROR: not enough arguments for `%s`%n",
                                  command.index(), 
                                  command.value());	
				break;
			} 
			if (i > len - 2) {
				System.out.printf("%d: ERROR: too many arguments for `%s`%n",
                                  command.index(), 
                                  command.value());
				break;
			}
			
			ArrayList<Token> markAndIndex = new ArrayList<>();
				markAndIndex.add(command);
				markAndIndex.add(null);
				markAndIndex.add(null);
				markAndIndex.set(2, args.get(++i));
			for (int j = 0; j < tokensToCommand.size(); ++j) {
				markAndIndex.set(1, tokensToCommand.get(j));
				markTask(markAndIndex);
			}
			break;	
		}
	
		default: {
			System.out.printf("ERROR: missing command for the selected tasks%n");
		}
		}
	}

	public void clear(ArrayList<Token> args) {
		if (args == null || args.size() == 1) {
			System.out.printf("\033[H\033[J");
		} else {
			System.out.printf("ERROR: too many arguments for `%s`%n",
							  args.get(0).value());
		}
	}

	public void usage(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			System.out.println("COMMANDS:");
			System.out.println("    add <name> [description]                       add a new task.");

			System.out.println("    mark <index> <status>                          mark the index-task with the given status, which is given by 0, 1 or 2, or -o (--open),");
			System.out.println("                                                   -i (--in-progress) or -c (--closed), respectively.");

			System.out.println("    rm <-a | --all | index |                       remove index-task or all or all of the tasks in a range.");
			System.out.println("        --range <indexBegin> <indexEnd | ..>>      If, as the last argument of the `--range` subcommand, you pass `..`, then all of the tasks"); 
			System.out.println("                                                   between the beginning and the end are going to be removed.");

			System.out.println("    save [-o <file.csv> | --output <file.csv>]     save tasks into a csv file.");
			System.out.println("    quit [-o <file.csv> | --output <file.csv> |    quit Simker and, optinally, saves the tasks in a csv file.");
			System.out.println("          -s | --save]                             Note that `quit -s` and `quit --save` are aliases for `save` and then `quit`.");

			System.out.println("    load [file.csv]                                load tasks from file. If no file path is provided, load from `tasks.csv`.");

			System.out.println("    select <index ...> <rm | progress |            apply command to the select tasks");
            System.out.println("                        mark <status>>"); 
			
			System.out.println("    progress <index>                               up the status. If the status is CLOSED, delete it");
	
			System.out.println("    exit                                           alias for `quit` command.");

			System.out.println("    clear                                          clear screen.");

			System.out.println("    ls                                             list tasks.");

			System.out.println("    reset                                          alias for `rm --all` and `rm -a`.");

			System.out.println("    help                                           show this message.");
			break;
		}

		default: {
			System.out.printf("ERROR: too many arguments for `%s`%n",
							  args.get(0).value());
		}	
		}
	}

	public void removeTasks(ArrayList<Token> args) {
		switch (args.size()) { 
		case 1: {
			System.out.printf("ERROR: not enough arguments for `%s`%n",
							  args.get(0).value());
			break;
		}
	
		case 2: {
			if (args.get(1).value().equals("--range")) {
				System.out.printf("%d: ERROR: missing indexes for `range` subcommand%n",
	                              1);
				break;
			}

			if (args.get(1).type() != TokenType.INT) {
				System.out.printf("%d: ERROR: not an integer: `%s`%n",
								   1,
								   args.get(1).value());
				break;
			} 

			int index = Integer.parseInt(args.get(1).value());
			if (isOutOfBounds(index)) {
				System.out.printf("%d: ERROR: out of bounds: `%d`%n",
								   1, index);
				break;
			} 

			this.tasks.remove(index);
			break;	
		}

		case 3: {
			Token subcommand = args.get(1);

			if (subcommand.type() == TokenType.INT) {
				System.out.printf("%d: ERROR: too many arguments",
                                  subcommand.index());
				break;
			}
			if (subcommand.type() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got: `%s`%n",
                                  1, 
                                  subcommand.value());
				break;
			}
			if (!subcommand.value().equals("--range")) {
				System.out.printf("%d: ERROR: unknow sucommand: `%s`%n",
                                  1,
                                  subcommand.value());
				break;
			} 

			System.out.printf("%d: ERROR: missing end index for `range` subcommand%n",
                              1);
			break;
		}
	
		case 4: {
			Token subcommand = args.get(1);
			if (subcommand.type() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got: `%s`%n",
                                  1, 
                                  subcommand.value());
				break;
			}

			if (!subcommand.value().equals("--range")) {
				System.out.printf("%d: ERROR: unknow sucommand: `%s`%n",
                                  1,
                                  subcommand.value());
				break;
			} 

			removeTaskRange(args.get(2),
							args.get(3));
			break;			
		}

		default: {
			System.out.printf("ERROR: too many arguments%n");
		}
		}
	}

	public void addTask(ArrayList<Token> args) { 
		switch (args.size()) {
		case 1: {
			System.out.printf("ERROR: not enough arguments for `%s`%n",
							  args.get(0).value());
			break;
		}

		case 2: {
			Token t = args.get(1);
			if (t.type() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got: `%s`%n",
								  t.index(),
								  t.type());
				break;
			} 

			String name = t.value();
			if (Tokenizer.stripLeft(name, " ", 0) == -1) {
				System.out.println("ERROR: the name of a task must have at least one character");
				break;
			}

			this.tasks.add( new Task(name) );	
			break;
		}

		case 3: {
			Token t1 = args.get(1);
			Token t2 = args.get(2);
			if (t1.type() != TokenType.STRING ) {
				System.out.printf("%d: ERROR: expected STRING but got: `%s`%n",
								  t1.index(),
								  t1.type());
				break;
			} 
			if (t2.type() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got: `%s`%n",
								  t2.index(),
								  t2.type());
				break;
			} 

			String name = t1.value();
			if (Tokenizer.stripLeft(name, " ", 0) == -1) {
				System.out.println("ERROR: the name of a task must have at least one character");
				break;
			}

			String description = t2.value();
			if (Tokenizer.stripLeft(name, " ", 0) == -1) { 
				this.tasks.add( new Task(name) );	
			} else {
				this.tasks.add( new Task(name, description) );	
			}
			break;
		}

		default: {
			System.out.printf("ERROR: too many arguments for `%s`%n",
							   args.get(0).value());
		}
		}
	}

	public void markTask(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: case 2: {
			System.out.printf("ERROR: missing arguments for `%s`%n",
							  args.get(0).value());
			break;
		}

		case 3: {
			Token index = args.get(1);
			Token status = args.get(2);
			if (index.type() != TokenType.INT) {
				System.out.printf("%d: ERROR: expected INT but got: %s%n",
								  index.index(),
								  index.type());
				return ;
			} 

			int i = Integer.parseInt(index.value());
			if (isOutOfBounds(i)) {
				System.out.printf("%d: ERROR: `%d` is out of bounds%n",
								  index.index(),
								  i);
				return ;
			} 	

			switch (status.type()) {
			case INT: {
				int statusValue = Integer.parseInt(status.value());
				Status s = statusValueToStatus(statusValue);
				if (s == null) {
					System.out.printf("%d: ERROR: invalid status code: `%s`%n",
									  status.index(),
									  statusValue);
				} else {
					Task t = this.tasks.get(i);
					t.setStatus(s);	
					this.tasks.set(i, t);
				}
				break;
			}

			case COMMAND: {
				String statusString = status.value();
				Status s = statusStringToStatus(statusString);
				if (s == null) {
					System.out.printf("%d: ERROR: invalid subcommand: `%s`%n",
									  status.index(),
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
								  status.index(),
								  status.type());
			}
			}
			break;
		}

		default: {
			System.out.printf("ERROR: too many arguments for `%s`%n",
							  args.get(0).value());
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
					System.out.printf("%d. %s%n",
									  i,
									  this.tasks.get(i));
				}
			}
			break;
		}
		
		default: {
			System.out.printf("ERROR: too many arguments for `%s`%n",
							  args.get(0).value());
		}
		}
	}

	public int quit(ArrayList<Token> args) {
		int returnStatus = 1;
		switch (args.size()) {
		case 1: {
			returnStatus = 0;
			break;
		}

		case 2: {
			Token arg = args.get(1);
			if (arg.type() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got `%s`%n",
								  arg.index(),
								  arg.type());
				break;
			} 

			if (arg.value().equals("-o") || 
				arg.value().equals("--output")) {
				System.out.printf("%d: ERROR: missing file for `%s`%n",
								  arg.index(),
								  arg.value());
			} else if (arg.value().equals("-s") ||
					   arg.value().equals("--save")) {
				if (saveTasks("tasks.csv")) { 
					System.out.printf("Saving tasks...%n");
					returnStatus = 0;
				} else {
					System.out.printf("ERROR: could not save tasks%n");
				}
			} else {
				System.out.printf("%d: ERROR: unknow subcommand `%s`%n",
								  arg.index(),
								  arg.value());
			}
			break;
		}

		case 3: {
			Token subcommand = args.get(1);
			if (subcommand.type() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got `%s`%n",
								  subcommand.index(),
								  subcommand.type());
				break;
			}
			if (!(subcommand.value().equals("-o") || 
				subcommand.value().equals("--output"))) {
				System.out.printf("%d: ERROR: unknow subcommand `%s`%n",
								  subcommand.index(),
								  subcommand.value());
				break;
			} 				

			Token file = args.get(2);	
			if (file.type() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got `%s`%n",
								  file.index(),
								  file.type());
				break;
			}
			if (!file.value().endsWith(".csv")) {
				System.out.printf("%d: ERROR: expected a csv file but got `%s`%n",
								  file.index(),
								  file.value());
				break;
			}

			String filePath = file.value();
			if (saveTasks(filePath)) { 
				System.out.printf("Saving tasks into %s...%n",
								  filePath);
				returnStatus = 0;
			} else {
				System.out.printf("ERROR: could not write into the file: `%s`%n",
								   filePath);
			}
			break;
		}

		default: {
			System.out.printf("%d: ERROR: too many arguments for `%s` command%n",
							  args.get(1).index(),
						      args.get(1).value());
		}
		}

		return returnStatus;
	}

	public void saveTasks(ArrayList<Token> args) {
		switch (args.size()) {
		case 1: {
			if (saveTasks("tasks.csv")) { 
				System.out.printf("Saving tasks...%n");
			} else {
				System.out.printf("ERROR: could not save tasks%n");
			}
			break;
		}

		case 2: {
			Token t = args.get(1);
			if (t.type() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got `%s`%n",
								  t.index(),
								  t.type());
				break;
			}

			if (t.value().equals("-o") ||
				t.value().equals("--output")) {
				System.out.printf("%d: ERROR: missing file path%n");
			} else { 
				System.out.printf("%d: ERROR: unknow subcommand `%s`%n",
								  t.index(),
								  t.value());
			}
			break;
		}

		case 3: {
			Token subcommand = args.get(1);
			if (subcommand.type() != TokenType.COMMAND) {
				System.out.printf("%d: ERROR: expected COMMAND but got `%s`%n",
								  subcommand.index(),
								  subcommand.value());
				break;
			}

			if (!subcommand.equals("-o") &&
				!subcommand.equals("--output")) {
				System.out.printf("%d: ERROR: unknow subcommand `%s`%n",
								  subcommand.index(),
								  subcommand.value());
				break;
			} 			

			Token file = args.get(2);	
			if (file.type() != TokenType.STRING) {
				System.out.printf("%d: ERROR: expected STRING but got `%s`%n",
								  file.index(),
								  file.type());
				break;
			}
			if (!file.value().endsWith(".csv")) {
				System.out.printf("%d: ERROR: expected a csv file but got `%s`%n",
								  file.index(),
								  file.value());
				break;
			}

			if (saveTasks(file.value())) { 
				System.out.printf("Saving tasks into %s...%n",
								  file.value());
			} else {
				System.out.printf("%d: ERROR: could not write into file: `%s`%n",
								   file.index(),
								   file.value());
			}
			break;
		}

		default: {
			Token t = args.get(0);
			System.out.printf("%d: ERROR: too many arguments for `%s` command%n",
							  t.index(),
							  t.value());
		}
		}
	}

	private ArrayList<Token> sortTokenIndexes(ArrayList<Token> indexes) {
		ArrayList<Token> sortIndexes = new ArrayList<>();
			sortIndexes.addAll(indexes);
		int len = indexes.size();
		
		for (int i = 0; i < len - 1; ++i) {
			boolean swap = false; 
			for (int j = 0; j < len - i - 1; ++j) {
				if (Integer.parseInt(indexes.get(j).value()) < 
                    Integer.parseInt(indexes.get(j + 1).value())) {
					swap = true;
					Token aux = sortIndexes.get(j);
					sortIndexes.set(j, sortIndexes.get(j+1));
					sortIndexes.set(j+1, aux);
				}
			}

			if (!swap) { 
				break;
			}
		}

		return sortIndexes;
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
				sucess = false;
			}
		}

		return sucess;
 	}

	private void writeTasksIntoFile(Path file) throws IOException {
		for (Task task : this.tasks) {
			Files.writeString(file, 
							  task.toCSVFormat(),
							  StandardOpenOption.APPEND);	
		}
	}

	private void removeTaskRange(Token indexStart, Token indexEnd) {
		if (indexStart.type() != TokenType.INT) {
			System.out.printf("%d: ERROR: expected INT but got: `%s`%n", 
							  indexStart.index(),
							  indexStart.value()); 
			return ;
		}	

		if (indexEnd.type() == TokenType.STRING) {
			System.out.printf("%d: ERROR: expected INT or COMMAND, but got: `%s`%n", 
							  indexEnd.index(),
							  indexEnd.value()); 
			return ;
		}

		int i, j;	
		i = Integer.parseInt(indexStart.value());
		if (isOutOfBounds(i)) {
			System.out.printf("%d: ERROR: %d is out of bounds%n", 
							  indexStart.index(),
							  i); 
			return ;
		}

		if (indexEnd.type() == TokenType.COMMAND) {
			if (!indexEnd.value().equals("..")) {
				System.out.printf("%d: ERROR: unknow subcommand: `%s`%n",
								   indexEnd.index(),
								   indexEnd.value());	
				return ;
			}

			System.out.printf("Removing tasks between %d and the end...%n",
							  i);
			int start = i;
			while (i <= this.tasks.size()) {
				this.tasks.remove(start);
				++i;	
			}
		} else {
			j = Integer.parseInt(indexEnd.value());
			if (isOutOfBounds(j)) {
				System.out.printf("%d: ERROR: %d is out of bounds%n", 
								  indexEnd.index(),
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
