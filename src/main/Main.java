package main;

import main.app.Simker;

public class Main {
	public static void main(String[] args) {
		Simker app = new Simker();

		if (args.length == 1) {
			if (args[0].equals("-h") ||
				args[0].equals("--help")) {	
				System.out.println("Usage: ./build [filePath | -h | --help]");
			} else {
				app.loadTasks(args[0]);
				app.menu();
			}
		} else if (args.length == 0) {
			app.menu();
		} else {
			System.out.println("Usage: ./build [tasks.csv | -h | --help]");
			System.out.println();
			System.out.println("ERROR: too many arguments");
		}
	}
}
