package test;

import main.app.task.*;
import main.app.Simker;
import main.app.lexer.*;

import java.util.ArrayList;

public class Tests {
	public static void main(String[] args) {
		ArrayList<Test> tests = new ArrayList<>();
			tests.add(new TestSimker());
			tests.add(new TestTask());
			tests.add(new TestLexer());

		System.out.println("====== TESTS ======");
		for (Test test : tests) {
			test.doTests();
			System.out.println();
		}
	}	
}

abstract class Test {
	// TODO: this doesn't work, but I think it's a cool ideia, so I intend 
	// to try to make it work in the futute
	protected int nTests = 0;

	abstract void doTests();

	// TODO: the ideia is that, as you create new tests, this method would help
	// you to not forget to add them into the doTests() method, but it isn't 
	// working as intended, like I said in the previous TODO
	void assertNumberOfTestsOf(String testClass, int currentNumberOfTests) { 
		assert this.nTests == currentNumberOfTests :
			String.format("exaustive handling of tests in %s%n", testClass);
	}

	void pass(String testName) {
		System.out.printf("(%02d) PASS: %s%n", this.nTests, testName);
	}

	void assertCondition(boolean condition, String description) {
		assert condition : 
			String.format("(%02d) FAILED: %s%n", this.nTests, description); 
	}
}

class TestSimker extends Test { 
	@Override
	void doTests() {
		System.out.println("++++ Simker tests:");
			testAddTask();
			testMarkTask();

		assertNumberOfTestsOf("TestSimker", 2);
	}

	void testAddTask() {
		super.nTests++;;
		pass("testAddTask");
	}

	void testMarkTask() {
		super.nTests++;
		pass("testMarkTask");
	}
}

class TestTask extends Test {
	@Override
	void doTests() {
		System.out.println("++++ Class Task tests:");
			testTaskConstructorStatusNameAndDescription();	
			testTaskConstructorStatusNameAndNoDescription();
			testTaskConstructorNoStatusNameAndDescription();
			testTaskConstructorNoStatusNameAndNoDescription();

		assertNumberOfTestsOf("TestTask", 4);
	}	

	void testTaskConstructorStatusNameAndDescription() {
		super.nTests++;
		Task task = new Task(
			Status.IN_PROGRESS, 
			"test",
			"testing constructor"
        );	

		Status s = task.getStatus();
		assertCondition(s != null, "expected a status but got NULL");
		assertCondition(s == Status.IN_PROGRESS, 
			            String.format("expected 'IN_PROGRESS' but got '%s'", s));

		String name = task.getName();
		assertCondition(name != null, "expected a name but got NULL");
		assertCondition(name.equals("test"), 
			            String.format("expected 'test' but got '%s'", name));

		String description = task.getDescription();	
		assertCondition(description != null, "expected a description but got NULL");
		assertCondition(description.equals("testing constructor"), 
			            String.format("expected 'testing constructor' but got '%s'", description));

		pass("testTaskConstructorStatusNameAndDescription");
	}

	void testTaskConstructorStatusNameAndNoDescription() {
		super.nTests++;
		Task task = new Task(
			Status.IN_PROGRESS, 
			"test"
        );	

		Status s = task.getStatus();
		assertCondition(s != null, "expected a status but got NULL");
		assertCondition(s == Status.IN_PROGRESS, 
			            String.format("expected 'IN_PROGRESS' but got '%s'", s));

		String name = task.getName();
		assertCondition(name != null, "expected a name but got NULL");
		assertCondition(name.equals("test"),
			            String.format("expected 'test' but got '%s'", name));

		String description = task.getDescription();	
		assertCondition(description == null, 
			            String.format("expected NULL but got '%s'", description));

		pass("testTaskConstructorStatusNameAndNoDescription");
	}

	void testTaskConstructorNoStatusNameAndDescription() {
		super.nTests++;
		Task task = new Task(
			"test",
			"testing constructor"
        );	

		Status s = task.getStatus();
		assertCondition(s != null, "expected a status but got NULL");
		assertCondition(s == Status.OPEN, 
			            String.format("expected 'OPEN' but got '%s'", s));

		String name = task.getName();
		assertCondition(name != null, "expected a name but got NULL");
		assertCondition(name.equals("test"), 
			            String.format("expected 'test' but got '%s'", name));

		String description = task.getDescription();	
		assertCondition(description != null, "expected a description but got null");
		assertCondition(description.equals("testing constructor"), 
			            String.format("expected 'testing constructor' but got '%s'", description));

		pass("testTaskConstructorNoStatusNameAndDescription");
	}

	void testTaskConstructorNoStatusNameAndNoDescription() {
		super.nTests++;
		Task task = new Task(
			"test"
        );	

		Status s = task.getStatus();
		assertCondition(s != null, "expected a status but got NULL");
		assertCondition(s == Status.OPEN, 
			            String.format("expected 'OPEN' but got '%s'", s));

		String name = task.getName();
		assertCondition(name != null, "expected a name but got NULL");
		assertCondition(name.equals("test"),  
			            String.format("expected 'test' but got '%s'", name));

		String description = task.getDescription();	
		assertCondition(description == null, 
			            String.format("expected null but got '%s'%n", description));

		pass("testTaskConstructorNoStatusNameAndNoDescription");
	}
}

class TestLexer extends Test {
	@Override 
	void doTests() {
		System.out.println("++++ Lexer tests:");
			testStripLeft();
			testGetTokens();

		assertNumberOfTestsOf("TestLexer", 2);
	}

	void testStripLeft() { 
		super.nTests++;
		String testString1 = "       testing";

		int result = Tokenizer.stripLeft(testString1, 0);		
		int expectedResult = testString1.indexOf('t');
		assertCondition(result == expectedResult, 
			            String.format("expected '%d' but got '%d'", 
									  expectedResult, 
									  result));

		String testString2 = "testing     2 foo";
		result = Tokenizer.stripLeft(testString2, testString2.indexOf('g') + 1);		
		expectedResult = testString2.indexOf('2');
		assertCondition(result == expectedResult, 
			            String.format("expected '%d' but got '%d'", 
									  expectedResult, 
									  result));
			
		pass("testStripLeft");
	}

	void testGetTokens() {
		super.nTests++;
		String testString1 = "add 1 \"task name\" \"task description\"";
		
		int result = Tokenizer.getTokens(testString1).size();
		int expectedAmountOfTokens = 4;
		assertCondition(result == expectedAmountOfTokens,
						String.format("expected '%d' tokens but got '%d'",
									  result,
									  expectedAmountOfTokens));

		String testString2 = "            add 102         \"    task name \" \"task    description\"";
		result = Tokenizer.getTokens(testString2).size();
		assertCondition(result == expectedAmountOfTokens,
						String.format("expected '%d' tokens but got '%d'",
									  result,
									  expectedAmountOfTokens));

		String testString3 = "";
		result = Tokenizer.getTokens(testString3).size();
		expectedAmountOfTokens = 0;
		assertCondition(result == expectedAmountOfTokens,
						String.format("expected '%d' tokens but got '%d'",
									  result,
									  expectedAmountOfTokens));
		
		pass("testGetTokens");
	}
}
