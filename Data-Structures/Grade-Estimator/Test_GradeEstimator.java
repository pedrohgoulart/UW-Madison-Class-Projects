/**
 * Run several tests to see if the GradeEstimator program is working
 * as we expect.  
 * 
 * Run without input file, with input file that doesn't exist, 
 * with example grade_info.txt input file, and any other
 * files and samples created by students.
 * 
 * @author deppeler (for p1 Spring 2017)
 */
public class Test_GradeEstimator {

	/**
	 * Uncomment the call to each test that you want to try.
	 * @param args  UNUSED
	 */
	public static void main( String[] args ) {

		/////////////////////////////////////////////////////////////////////
		// Sample test runs of your program.  Comment and uncomment as you like
		// to focus on each specified type of test for your program
		/////////////////////////////////////////////////////////////////////

		// Run your program without a file specified
		test("test00 No Grade File specified", null, "Usage message and defaults");

		// Run your program with a file specified that does not exist
		test("test01 Grade File Does NOT Exist", "no_file_exists.txt", "File Not Found ");

		// Run your program with a the sample grade_info.txt 
		test("test02 Grade File Exists", "grade_info.txt", "Letter Grade Estimate: B");

		// Run your program with a the sample grade_info_bad.txt 
		test("test03 Bad File Format", "grade_info_bad.txt", "GradeFileFormatException");

		/////////////////////////////////////////////////////////////////////
		// Add your own additional test runs.
		// 1. Create a grade info file
		// 2. Manually compute the expected letter grade
		// 3. Add a line of this form to call your program here:
		//    test("--test name here--","filename.txt", "--expected results here--");
		/////////////////////////////////////////////////////////////////////

	}

	/**
	 * Runs the GradeEstimator program with the given command-line argument.
	 * 
	 * Any exceptions that are thrown by GradeEstimator are caught,
	 * their call stack trace is printed, and the method ends.
	 * 
	 * Doing this allows other tests to be run without the program itself
	 * ending due to an unexpected exception.
	 * 
	 * @param name  The test's name for display purposes 
	 * @param cla The command line argument to include for this run
	 * @param expectedResult Letter grade or other expected result of this run.
	 * It is best if expected result is not more than one line in length.
	 */
	private static void test(String name, String cla, String expectedResult ) {

		// Create the command-line argument array for this test run
		String [] args = { cla };
		
		// Replace args with empty array if there was no valid cla for this run
		if ( cla == null || cla.equals("") ) { 
			args = new String[0];  // need array with 0 elements
			cla = "";              // replaced, so 'null' will not display
		}
		
		testHeader(name + " --> java GradeEstimator "+ cla);	

		// CATCH and REPORT EXCEPTION, THEN CONTINUE (SO OTHER TESTS CAN RUN)
		try { GradeEstimator.main(args); } 
		catch (Exception e) { e.printStackTrace(); }
		
		testFooter(expectedResult);		
	}

	/**
	 * Displays a border and label -- BEFORE THE TEST RESULTS
	 * 
	 * For example:
	 * =================================================================
	 * RUN: test01 Grade File Does NOT Exist --> java GradeEstimator no_file_exists.txt
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	 * 
	 * @param testname The label to display in the header.
	 */
	private static void testHeader( String testname ) {
		System.out.println(
		"=================================================================" +
		"\nRUN: "+testname +
		"\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
		);
	}

	/**
	 * Displays the bottom border and expected results -- AFTER THE TEST
	 * 
	 * For example:
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	 * EXPECTED: File Not Found 
	 * =================================================================
	 * 
	 * @param expected The expected results to compare against actual.
	 */
	private static void testFooter( String expected ) {
		System.out.println(
		"\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" +
		"\nEXPECTED: " + expected +
		"\n=================================================================");		
	}

}
