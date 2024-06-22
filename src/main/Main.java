package main;

import main.app.Simker;

public class Main {
	public static void main(String[] args) {
		Simker app = new Simker();

		switch (args.length) { 
		case 0: {
			app.menu();
			break;
		}

		case 1: {
			if (args[0].equals("-h") ||
				args[0].equals("--help")) {	
				System.out.println("Usage: ./build [filePath | -h | --help]");
			} else {
				if (app.loadTasksFromFile(args[0])) {
					System.out.println("Loading tasks...");
					app.menu();
				} else {
					System.out.printf("ERROR: could not read tasks from `%s`%n",
                                      args[0]);
				}
			}
			break;
		}

		default: {
			System.out.println("Usage: ./build [tasks.csv | -h | --help]");
			System.out.println();
			System.out.println("ERROR: too many arguments");
		}
		}
	}
}
