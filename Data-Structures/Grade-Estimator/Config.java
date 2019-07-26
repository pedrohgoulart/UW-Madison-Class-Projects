public class Config {

	/** Default grade letters used if no grade info file provided. */
	public static final String[] GRADE_LETTER = {"A","B","C","D","F","N"};

	/** Default grade thresholds used if no grade info file provided. */
	public static double[] GRADE_THRESHOLD = {90,80,70,60,5,0};

	/** Default score categories used if no grade info file provided. */
	public static String[] CATEGORY_KEY = {"homeworks","programs","midterms","final"};

	/** Default score category weights used if no grade info file provided. */
	public static double[] CATEGORY_WEIGHT = {20,25,34,21};


	// This constant must be before USAGE_MESSAGE which depends on this constant
	/**
	 * Shows the required format if grade information is to be provided
	 * via a text file.
	 */
	public static final String GRADE_INFO_FILE_FORMAT_EXAMPLE = 
			
			"A B C D F N                    # letter grades\n"+
			"90 80 70 60 5 0                # minimum thresholds\n"+
			"homework program midterm final # category names\n"+
			"20 25 34 21                    # category weights of 100\n"+
			"h1 34 50                       # homework 1 34/50 points\n"+
			"h2 29 30                       # homework 2\n"+
			"p1 195 200                     # program 1\n"+
			"p2 230 240                     # program 2\n"+
			"p3 50 80                       # program 3\n"+
			"m1 57 66                       # exam 1 56/66\n"+
			"m2 61 66                       # exam 2 61/66\n"+
			"f 78 81                        # final exam 78/81\n"
			;
	
	/** Displayed to user if they do not provide a grade info file */
	public static final String USAGE_MESSAGE = 
		"No \'grade_info\' filename was specified.\n" +
		"Default grade letters, minimum letter grade thresholds,\n"+
		"score categories, and category weight percentages will be used\n"+
		"to estimate your letter grade.\n"+
		"\nNote: Scores within a given category have equal weight.\n"+
		"\nAlternate program usage:\n"+
		"\n    java GradeEstimator grade_info\n" +
		"\nwhere \"grade_info\" is the name of a file that contains\n"+
		"grade information in the following format:\n\n"+
		GRADE_INFO_FILE_FORMAT_EXAMPLE;

}
