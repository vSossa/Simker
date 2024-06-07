package main;

import main.app.Simker;

public class Main {
	public static void main(String[] args) {
		Simker app = new Simker();

		if (args.length == 1) {
			app.loadTasks(args[0]);
			app.menu();
		} else if (args.length == 0) {
			app.menu();
		} else {
			System.out.println("Usage: ./build <tasks.csv>");
			System.out.println();
			System.out.println("ERROR: too many arguments");
		}
	}
}
