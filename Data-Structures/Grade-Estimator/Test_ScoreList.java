public class Test_ScoreList {

	private static int passed = 0;  // keep count of each passed test
	private static int failed = 0;  // keep count of each failed test

	public static void main(String[] args) {

		// some values to use in testing
		String[] names    = {"h0","h1","p0","p1"};
		double[] points   = { 41 , 28 , 95 , 85 };
		double[] possible = { 46 , 30 ,100 ,105 };

		Score[] scores = test01_constructScores(names,points,possible); 
		test02_getCategoryOfScore(names);   // names and categories
		test03_getPercent(points,possible); // points, possible, and percent
		test04_score_exceptions();

		test05_scoreList_add_a_score();       
		test06_scoreList_add_scores(scores); 

		test07_scoreList_add_and_remove_a_score();       
		test08_scoreList_add_and_remove_scores(scores); 


		System.out.println("Passed " + passed + " tests.");
		System.out.println("Failed " + failed + " tests.");
	}

	private static void test08_scoreList_add_and_remove_scores(Score[] scores) {
		try {
			ScoreListADT list = new ScoreList();
			for ( int i=0; i < scores.length ; i++ ) 
				list.add(scores[i]);

			for ( int i=0; i < scores.length ; i++ ) {
				Score removedScore = list.remove(0);
				if ( ! removedScore.equals(scores[i])) {
					System.out.println("test08 remove(0)=" + removedScore + 
									" but should be "+scores[i]);
					failed++;
				} else { passed++; }
			}
		} catch (Exception e) {
			System.out.println("test08 list.add or list.remove caused " + 
							e.getClass().getName());
			failed += scores.length;
		}

	}

	private static void test07_scoreList_add_and_remove_a_score() {
		ScoreListADT list = new ScoreList();
		Score score = new Score("test07score",37,40);
		list.add(score);

		try {
			Score removedScore = list.remove(0);
			if ( ! removedScore.equals(score) ) {
				System.out.println("test07 list.remove(o)=" + removedScore +
								" but should equal "+score);
				failed++;
			} else { passed++; }

			if (list.size()!=0) {
				System.out.println("test07 list.size();="+list.size() + 
								" but should equal 0 after remove(0)");
				failed++;
			} else { passed++; }
		} catch (Exception e) { 
			System.out.println("test07 list.remove(0) caused " + 
							e.getClass().getName()); 
			failed += 2;
		}
	}

	private static void test06_scoreList_add_scores(Score[] scores) {
		try {
			ScoreListADT list = new ScoreList();
			for ( int i=0; i < scores.length ; i++ ) 
				list.add(scores[i]);
			passed++;
			for ( int i=0; i < scores.length ; i++ ) 
				if ( ! list.get(i).equals(scores[i])) 
					System.out.println("test06 get after adding scores to ScoreList failed");
			passed++;
		} catch (Exception e) {
			System.out.println("test06 list.add or list.get caused " + 
		e.getClass().getName());
			failed += 2;
		}

	}

	/**
	 * Check that ScoreList class exist and size is 0 at start
	 * and 1 after one score has been added.
	 */
	private static void test05_scoreList_add_a_score() {
		ScoreListADT list = new ScoreList();
		Score score = new Score("test05score",43,88);
		if ( list.size() != 0 ) {
			System.out.println("test05 list.size()=" + list.size() + 
							" but should equal 0 at start");
			failed++;
		} else { passed++; }

		list.add(score);
		passed++;

		try {
			if ( list.size() != 1 ) {
				System.out.println("test05 list.size()=" + list.size() +
								" but should equal 1");
				failed++;
			} else { passed++; }
		} catch (Exception e) { 
			System.out.println("test05 list.size() caused " + 
							e.getClass().getName()); 
			failed++;
		}

		try {
			if ( ! list.get(0).equals(score) ) {
				System.out.println("test05 list.get(0)="+list.get(0) + 
								" but should equal " + score );
				failed++;
			} else { passed++; }
		} catch (Exception e) { 
			System.out.println("test05 list.get(0).equals(score) caused " + 
							e.getClass().getName());
			failed++;
		}
	}

	/**
	 * Check that invalid name, points, or possible values are detected by  
	 * constructor and will cause IllegalArgumentException to be thrown. 
	 */
	private static void test04_score_exceptions() {
		String msg = " should cause IllegalArgumentException but did not!";

		try {
			try {
				new Score(null,10,10); 
				System.out.println("test04a new Score(null,10,10)" + msg);
				failed++;
			} catch (IllegalArgumentException e) { passed++;
			/* expected exceptions */ }

			try {
				new Score("a1",-1,0);
				System.out.println("test04b new Score(\"a1\",-1,10)" + msg);
				failed++;
			} catch (IllegalArgumentException e) { passed++;
			/* expected exceptions */ }

			try {
				new Score("a1",1,-10);
				System.out.println("test04c new Score(\"a1\",1,-10)" + msg);
				failed++;
			} catch (IllegalArgumentException e) { passed++;
			/* expected exceptions */ }

			try {
				new Score("a1",11,10);
				System.out.println("test04d new Score(\"a1\",11,10)" + msg);
				failed++;
			} catch (IllegalArgumentException e) { passed++;
			/* expected exceptions */ }

		} catch (Exception e) {
			System.out.println("test04 Unable to complete testing due to:");
			e.printStackTrace();
			failed += 4;  // four tests unable to run
		}
	}

	/**
	 * Test that the percentage computed by the Score class is a correct
	 * match for the percentage of each score.  Since values being compared 
	 * are type double, consider results to be correct if the result is 
	 * within 1/1000th of the expected value.
	 * 
	 * Example:
	 * If the score is 9/10 and the percent result is within 0.899 and 0.901,
	 *                accept as correct.
	 * 
	 * PRE-CONDITION: each array has the same number of values. 
	 *                each array represents a valid score where points
	 *                is greater than or equal to zero and possible is
	 *                greater than or equal to zero.
	 *                
	 * POST-CONDITION: only errors are reported.
	 * 
	 * @param points  an array of points earned for each assignment
	 * @param possible an array of maximum points possible for each assignment
	 */
	private static void test03_getPercent(double[] points, double[] possible) {
		try {
			for (int i=0; i < points.length; i++) {
				double err = 1.0 / 1000; // threshold for reporting errors

				// not concerned with points, possible, or percent for this test
				Score score = new Score("a"+i,points[i],possible[i]);

				// just a sanity check on the names
				if ( ! score.getName().equals("a"+i) )
					System.out.println("test03a ["+i+"] score.getName()=" +
					score.getName() + " but should be " + "a"+i );

				// check the points for each score
				if ( Math.abs(score.getPoints() - points[i]) > err ) 
					System.out.println("test03b ["+i+"] score.getName()=" +
					score.getName() + " but should be " + points[i] );

				// check the max possible for each score
				if ( Math.abs(score.getMaxPossible() - possible[i]) > err ) 
					System.out.println("test03c ["+i+"] score.getMaxPossible()=" +
					score.getMaxPossible() + " but should be " + possible[i] );

				// check the percent for each score
				// multiply points by 100 first and then divide by possible
				// this order is more likely to reduce rounding error
				double perc = points[i]*100/possible[i];
				if ( Math.abs(score.getPercent() - perc) > err ) {
					System.out.println("test03d ["+i+"] score.getPercent()=" + 
					score.getPercent() + " but should be " + perc );
				}

			}
			passed += 5;  // all tests passed for all items in the arrays
		} catch (Exception e) {
			System.out.println("test03 Unable to complete testing due to " + 
							e.getClass().getName());
			failed += 5;
		}

	}

	/**
	 * Test the ability of the Score class to return the correct
	 * assignment name and category.
	 * 
	 * The category of an assignment is the first letter of the assignment
	 * name.
	 * 
	 * @param names array of assignment names
	 * @param points array of points earned for each assignment
	 * @param possible array of maximum points possible for each assignment
	 */
	private static void test02_getCategoryOfScore(String[] names) {
		try {
			Score[] scoreArray = new Score[names.length];
			for (int i=0; i < scoreArray.length; i++) {
				// not concerned with points, possible, or percent for this test
				Score score = new Score(names[i],10,10);

				if ( ! score.getName().equals(names[i]) ) {
					System.out.println("test02a ["+i+"] score.getName()=" +
									score.getName() + " but should be "+names[i]);
					failed++;
				} else { passed++; }

				String category = "" + names[i].charAt(0);
				if ( ! score.getCategory().equals(category) ) {
					System.out.println("test02b ["+i+"] score.getCategory()=" + 
									score.getCategory() + 
									" but should be "+category);
					failed++;
				} else { passed++; }
			}
		} catch (Exception e) {
			System.out.println("test02 Unable to complete testing due to " + 
							e.getClass().getName());
			failed += names.length;  
		}
	}

	/**
	 * Construct each score as described in the given arrays and return
	 * an array of the Score instances that correspond to the assignment scores.
	 * 
	 * PRE-CONDITION: all three parameter arrays are same length
	 *                and contain valid values.
	 *                
	 * POST-CONDITION: if no error, no output is produced, but an
	 * array of scores is returned.
	 * 
	 * @param names  An array of assignment names.
	 * @param points An array of points earned for each assignment.
	 * @param max    An array of the maximum points possible for each assignment.
	 */
	private static Score[] test01_constructScores( 
					String[]names, double[] points, double[] max ) {
		try {
			Score[] scoreArray = new Score[names.length]; 
			passed++;

			for (int i=0; i < scoreArray.length; i++) 
				scoreArray[i] = new Score(names[i], points[i], max[i]);
			passed++;

			return scoreArray;
		} catch (Exception e) {
			System.out.println("test01 Unable to complete testing due to " + 
							e.getClass().getName());
			failed++;
		}
		return null;
	}
}
