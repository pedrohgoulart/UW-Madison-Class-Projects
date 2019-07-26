//
// Title:            Sticks
// Files:            Config.java
// Semester:         CS302 Fall 2016
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// CS Login:         koeler-goulart
// Lecturer's Name:  Gary Dahl
// Lab Section:      331
//

import java.util.Arrays;
import java.util.Scanner;

/**
 * This class contains the game Sticks.
 *
 * Bugs: No bugs encountered so far.
 *
 * @author Pedro Henrique Koeler Goulart
 */

public class Sticks2 {
	
	/**
	 * This is the main method for the game of Sticks. 
	 * In milestone 1 this contains the whole program for playing
	 * against a friend.
	 * In milestone 2 this contains the welcome, name prompt, 
	 * how many sticks question, menu, calls appropriate methods
	 * and the thank you message at the end.
	 * One method called in multiple places is promptUserForNumber.
	 * When the menu choice to play against a friend is chosen,
	 * then playAgainstFriend method is called.
	 * When the menu choice to play against a computer is chosen,
	 * then playAgainstComputer method is called.  If the
	 * computer with AI option is chosen then trainAI is called
	 * before calling playAgainstComputer.  Finally, 
	 * call strategyTableToString to prepare a strategy table
	 * for printing. 
	 * 
	 * @param args (unused)
	 */
	
	public static void main(String[] args) {
		//Scanner utility
		Scanner input = new Scanner(System.in);
		
	    //Variables
		String player1Name = "";
		String player2Name = "";
		int startSticks = 0;
		int mainMenuSelection = 0;
		
	    //Header
		System.out.println("Welcome to the Game of Sticks!");
		System.out.println("==============================");
		skipLine(1); //skips 1 line
		
	    //Player 1 name
		System.out.print("What is your name? ");
		player1Name = input.next(); 
		System.out.println("Hello " + player1Name.trim() + ".");
			
	    //Number of sticks
		startSticks = promptUserForNumber(input ,"How many sticks are there on the table initially (" + Config.MIN_STICKS + "-" + Config.MAX_STICKS + ")? ", Config.MIN_STICKS, Config.MAX_STICKS);

	    //Main Menu
		skipLine(1); //skips 1 line
		System.out.println("Would you like to:");
		System.out.println(" 1) Play against a friend");
		System.out.println(" 2) Play against computer (basic)");
		System.out.println(" 3) Play against computer with AI");
		mainMenuSelection = promptUserForNumber(input, "Which do you choose (1,2,3)? ", 1, 3);
		skipLine(1); //skips 1 line
		
	    //Play against a friend
		if (mainMenuSelection == 1){
			playAgainstFriend(input, startSticks, player1Name, player2Name);
		}
		
	    //Play against a computer (Basic)
		else if (mainMenuSelection == 2){
			playAgainstComputer(input, startSticks, player1Name, null);
		} 
	
	    //Play against a computer with Ai
		else if (mainMenuSelection == 3){
			
		}
			
	    //Footer
		System.out.println("=========================================");
		System.out.println("Thank you for playing the Game of Sticks!");
			
		input.close();
	}
	
	
	/**
	 * This method skips a certain number of lines
	 *
	 * @param a 
	 *			Number of lines to be skipped
	 */
	public static void skipLine(int a){
		for (int b=0; b < a; b++){
			System.out.println("");
		}
	}
	
	/**
	 * This method encapsulates the code for prompting the user for a number and
	 * verifying the number is within the expected bounds.
	 * 
	 * @param input
	 *            The instance of the Scanner reading System.in.
	 * @param prompt
	 *            The prompt to the user requesting a number within a specific
	 *            range.
	 * @param min
	 *            The minimum acceptable number.
	 * @param max
	 *            The maximum acceptable number.
	 * @return The number entered by the user between and including min and max.
	 */
	static int promptUserForNumber(Scanner input, String prompt, int min, int max){
		int promptUserForNumber = 0;
		boolean validNumber = false;
		do {
			System.out.print(prompt);
			    //If user types an int value
				if (input.hasNextInt()) {
					promptUserForNumber = input.nextInt();
					if (promptUserForNumber < min || promptUserForNumber > max) {
						System.out.println("Please enter a number between " + min + " and " + max + ".");
					}
					else{
						validNumber = true;
					}
				} 
			    //If user types anything other than an int value
				else {
					input.nextLine();
					String invalid = input.nextLine();
					System.out.println("Error: expected a number between " + min + " and " + max + " but found: " + invalid);
				}  
		} while (!validNumber); //When boolean is true, leave loop	
		return promptUserForNumber;
	}
	
	/**
	 * This method has one person play the Game of Sticks against another
	 * person.
	 * 
	 * @param input
	 *            An instance of Scanner to read user answers.
	 * @param startSticks
	 *            The number of sticks to start the game with.
	 * @param player1Name
	 *            The name of one player.
	 * @param player2Name
	 *            The name of the other player.
	 * 
	 *            As a courtesy, player2 is considered the friend and gets to
	 *            pick up sticks first.
	 * 
	 */
	static void playAgainstFriend(Scanner input, int startSticks, String player1Name, String player2Name) {
		//Variables
		int i = 0;
		String player = "";
		int sticksOut = 0;
		
		//Player 2 name
		System.out.print("What is your friend's name? ");
		player2Name = input.next(); 
		System.out.println("Hello " + player2Name.trim() + ".");
		skipLine(1); //skips 1 line
	
	    //Sticks main game
		for (i=0; startSticks > 0; ++i){
			//Display number of sticks on the board
			if (startSticks == 1) System.out.println("There is 1 stick on the board.");
			else System.out.println("There are " + startSticks + " sticks on the board.");
			
			//Defining player 1 and player 2
			if (i % 2 == 0) player = player2Name; //i is even
			else player = player1Name; //i is odd
				
			//Check number of sticks that can be taken
			if (startSticks == Config.MIN_ACTION){
				sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + Config.MIN_ACTION + ")? ", Config.MIN_ACTION, Config.MIN_ACTION);
			}
			else if (startSticks == (Config.MIN_ACTION + 1)){
				sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + (Config.MIN_ACTION + 1) + ")? ", Config.MIN_ACTION, (Config.MIN_ACTION + 1));
			}
			else if (startSticks == (Config.MIN_ACTION + 2)){
				sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + (Config.MIN_ACTION + 2) + ")? ", Config.MIN_ACTION, (Config.MIN_ACTION + 2));
			}
			else {
				sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + Config.MAX_ACTION + ")? ", Config.MIN_ACTION, Config.MAX_ACTION);
			}
				
			//Takes the sticks out
			startSticks = startSticks - sticksOut;
		}
			
		//Determining the winner
		if (i % 2 == 0){
			System.out.println(player2Name + " wins. " + player1Name + " loses.");
		}
		else{
			System.out.println(player1Name + " wins. " + player2Name + " loses.");
		}
		skipLine(1); //skips 1 line	   
	}	
	
	/**
	 * Make a choice about the number of sticks to pick up when given the number
	 * of sticks remaining.
	 * 
	 * Algorithm: If there are less than Config.MAX_ACTION sticks remaining, 
	 * then pick up the minimum number of sticks (Config.MIN_ACTION). 
	 * If Config.MAX_ACTION sticks remain, randomly choose a number between 
	 * Config.MIN_ACTION and Config.MAX_ACTION. Use Config.RNG.nextInt(?) 
	 * method to generate an appropriate random number.
	 * 
	 * @param sticksRemaining
	 *            The number of sticks remaining in the game.
	 * @return The number of sticks to pick up, or 0 if sticksRemaining is <= 0.
	 */
	static int basicChooseAction(int sticksRemaining) {
		int basicChooseAction = 0;
		//If sticks remaining are MAX_ACTION or more
		if (sticksRemaining >= Config.MAX_ACTION){
			basicChooseAction = Config.RNG.nextInt(Config.MAX_ACTION) + Config.MIN_ACTION;
		}
		//If sticks remaining are more than (or equal to) MIN_ACTION or less than MAX_ACTION
		else if (sticksRemaining >= Config.MIN_ACTION && sticksRemaining < Config.MAX_ACTION){
			basicChooseAction = Config.MIN_ACTION;
		}
		//If sticks are less than MIN_ACTION
		else{
			basicChooseAction = 0;
		}
		
		return basicChooseAction;
	}
	
	/**
	 * This method has a person play against a computer.
	 * Call the promptUserForNumber method to obtain user input.  
	 * Call the aiChooseAction method with the actionRanking row 
	 * for the number of sticks remaining. 
	 * 
	 * If the strategyTable is null, then this method calls the 
	 * basicChooseAction method to make the decision about how 
	 * many sticks to pick up. If the strategyTable parameter
	 * is not null, this method makes the decision about how many sticks to 
	 * pick up by calling the aiChooseAction method. 
	 * 
	 * @param input
	 *            An instance of Scanner to read user answers.
	 * @param startSticks
	 *            The number of sticks to start the game with.
	 * @param playerName
	 *            The name of one player.
	 * @param strategyTable
	 *            An array of action rankings. One action ranking for each stick
	 *            that the game begins with.
	 * 
	 */
	static void playAgainstComputer(Scanner input, int startSticks, String playerName, int[][] strategyTable) {
		//Variables
		int i = 0;
		int sticksOut = 0;
		String player = "";
		String playerComputer = "";
		
		//Sticks main game
		for (i=0; startSticks > 0; ++i){
			//Display number of sticks on the board
			if (startSticks == 1) System.out.println("There is 1 stick on the board.");
			else System.out.println("There are " + startSticks + " sticks on the board.");
			
			//Defining player 1 and player 2
			if (i % 2 == 0) player = playerName; //i is even
			else player = playerComputer; //i is odd
			
			//Player 1 turn
			if (player == playerName){
				//Check number of sticks that can be taken
				if (startSticks == Config.MIN_ACTION){
					sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + Config.MIN_ACTION + ")? ", Config.MIN_ACTION, Config.MIN_ACTION);
					}
				else if (startSticks == (Config.MIN_ACTION + 1)){
					sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + (Config.MIN_ACTION + 1) + ")? ", Config.MIN_ACTION, (Config.MIN_ACTION + 1));
				}
				else if (startSticks == (Config.MIN_ACTION + 2)){
					sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + (Config.MIN_ACTION + 2) + ")? ", Config.MIN_ACTION, (Config.MIN_ACTION + 2));
					}
				else {
					sticksOut = promptUserForNumber(input, player + ": How many sticks do you take (" + Config.MIN_ACTION + "-" + Config.MAX_ACTION + ")? ", Config.MIN_ACTION, Config.MAX_ACTION);;
				}
			}
			//Computer turn
			else {
				if (startSticks < Config.MAX_ACTION){
					sticksOut = basicChooseAction(startSticks);
					System.out.println("Computer selects " + sticksOut + " stick.");
				}
				else{
					sticksOut = basicChooseAction(startSticks);
					System.out.println("Computer selects " + sticksOut + " sticks.");
				}
			}
				
			//Takes the sticks out
			startSticks = startSticks - sticksOut;
		}
			
		//Determining the winner
		if (i % 2 == 0){
			System.out.println(playerName + " wins. Computer loses.");
		}
		else{
			System.out.println("Computer wins. " + playerName + " loses.");
		}
		skipLine(1); //skips 1 line
	}
	
	/**
	 * This method chooses the number of sticks to pick up based on the
	 * sticksRemaining and actionRanking parameters.
	 * 
	 * Algorithm: If there are less than Config.MAX_ACTION sticks remaining 
	 * then the chooser must pick the minimum number of sticks (Config.MIN_ACTION). 
	 * For Config.MAX_ACTION or more sticks remaining then pick based on the 
	 * actionRanking parameter.
	 * 
	 * The actionRanking array has one element for each possible action. The 0
	 * index corresponds to Config.MIN_ACTION and the highest index corresponds
	 * to Config.MAX_ACTION. For example, if Config.MIN_ACTION is 1 and 
	 * Config.MAX_ACTION is 3, an action can be to pick up 1, 2 or 3 sticks. 
	 * actionRanking[0] corresponds to 1, actionRanking[1] corresponds to 2, etc. 
	 * The higher the element for an action in comparison to other elements, 
	 * the more likely the action should be chosen.
	 * 
	 * First calculate the total number of possibilities by summing all the
	 * element values. Then choose a particular action based on the relative
	 * frequency of the various rankings. 
	 * For example, if Config.MIN_ACTION is  1 and Config.MAX_ACTION is 3: 
	 * If the action rankings are {9,90,1}, the total is 100. Since 
	 * actionRanking[0] is 9, then an action of picking up 1 should be chosen 
	 * about 9/100 times. 2 should be chosen about 90/100 times and 1 should 
	 * be chosen about 1/100 times. Use Config.RNG.nextInt(?) method to 
	 * generate appropriate random numbers.
	 * 
	 * @param sticksRemaining
	 *            The number of sticks remaining to be picked up.
	 * @param actionRanking
	 *            The counts of each action to take. The 0 index corresponds to
	 *            Config.MIN_ACTION and the highest index corresponds to
	 *            Config.MAX_ACTION.
	 * @return The number of sticks to pick up. 0 is returned for the following
	 *         conditions: actionRanking is null, actionRanking has a length of
	 *         0, or sticksRemaining is <= 0.
	 * 
	 */
	static int aiChooseAction(int sticksRemaining, int[] actionRanking) {
		//Variables
		int aiChooseAction = 0;
		int[] rankingsCumulativeSum = new int[Config.MAX_ACTION]; //Array to hold cumulative sum of actionRanking, for example, 1, 7, 10 (original: 1, 6, 3).
		int cumulative = 0;
		int probability = 0;
		
		//If sticks remaining are MAX_ACTION or more
		if (sticksRemaining >= Config.MAX_ACTION){
			//Sums the variable in actionRanking array with the previous one and assigns it to rankingsCumulativeSum
			for (int i = 0; i < actionRanking.length; i++){
				cumulative += actionRanking[i];
				rankingsCumulativeSum[i] = cumulative;
			}
			//Generates a random number between 0 and total sum of arrays
			probability = Config.RNG.nextInt(rankingsCumulativeSum[rankingsCumulativeSum.length - 1]);
			//Chooses the number of sticks to remove based on probability
			for (int j = 0; j < actionRanking.length; j++) {
				if (rankingsCumulativeSum[j] > probability){
					aiChooseAction = Config.MIN_ACTION + j;
					break;
				}
			}
		}
		//If sticks remaining are between MIN_ACTION and MAX_ACTION
		else if (sticksRemaining >= Config.MIN_ACTION && sticksRemaining < Config.MAX_ACTION){
			aiChooseAction = Config.MIN_ACTION;
		}
		//If actionRanking or sticksRemaining are 0 
		else if (sticksRemaining <= 0 || actionRanking == null || actionRanking.length == 0){
			aiChooseAction = 0;
		}
		
		return aiChooseAction;
	}
	

	/**
	 * This method initializes each element of the array to 1. If actionRanking
	 * is null then method simply returns.
	 * 
	 * @param actionRanking
	 *            The counts of each action to take. Use the length of the
	 *            actionRanking array rather than rely on constants for the
	 *            function of this method.
	 */
	static void initializeActionRanking(int[] actionRanking) {
		if (actionRanking != null){
			for (int i = 0; i < actionRanking.length; i++){
				actionRanking[i] = 1;
			}
		}
	}
	
	/**
	 * This method returns a string with the number of sticks left and the
	 * ranking for each action as follows.
	 * 
	 * An example: 10     3,4,11
	 * 
	 * The string begins with a number (number of sticks left), then is followed
	 * by 1 tab character, then a comma separated list of rankings, one for each
	 * action choice in the array. The string is terminated with a newline (\n) 
	 * character.
	 * 
	 * @param sticksLeft
	 *            The number of sticks left.
	 * @param actionRanking
	 *            The counts of each action to take. Use the length of the
	 *            actionRanking array rather than rely on constants for the
	 *            function of this method.
	 * @return A string formatted as described.
	 */
	static String actionRankingToString(int sticksLeft, int[] actionRanking) {
		//Converts array to string and remove square brackets and spaces from it
		String displayArrays = Arrays.toString(actionRanking).replaceAll("\\[","").replaceAll("[ ]","").replaceAll("\\]","");
		
		return sticksLeft + "	" + displayArrays + "\n";
	}


	/**
	 * This method updates the actionRanking based on the action. Since the game
	 * was lost, the actionRanking for the action is decremented by 1, but not
	 * allowing the value to go below 1.
	 * 
	 * @param actionRanking
	 *            The counts of each action to take. The 0 index corresponds to
	 *            Config.MIN_ACTION and the highest index corresponds to
	 *            Config.MAX_ACTION.
	 * @param action
	 *            A specific action between and including Config.MIN_ACTION and
	 *            Config.MAX_ACTION.
	 */
	static void updateActionRankingOnLoss(int []actionRanking, int action) {
		    //TODO
	}
	
	/**
	 * This method updates the actionRanking based on the action. Since the game
	 * was won, the actionRanking for the action is incremented by 1.
	 * 
	 * @param actionRanking
	 *            The counts of each action to take. The 0 index corresponds to
	 *            Config.MIN_ACTION and the highest index corresponds to
	 *            Config.MAX_ACTION.
	 * @param action
	 *            A specific action between and including Config.MIN_ACTION and
	 *            Config.MAX_ACTION.
	 */
	static void updateActionRankingOnWin(int []actionRanking, int action) {
		    //TODO
	}
	
	/**
	 * Allocates and initializes a 2 dimensional array. The number of rows
	 * corresponds to the number of startSticks. Each row is an actionRanking
	 * with an element for each possible action. The possible actions range from
	 * Config.MIN_ACTION to Config.MAX_ACTION. Each actionRanking is initialized
	 * with the initializeActionRanking method.
	 * 
	 * @param startSticks
	 *            The number of sticks the game is starting with.
	 * @return The two dimensional strategyTable, properly initialized.
	 */
	static int[][] createAndInitializeStrategyTable(int startSticks) {
		return null;     //TODO change to return the array
	}	
		
	/**
	 * This formats the whole strategyTable as a string utilizing the
	 * actionRankingToString method. For example:
	 * 
	 * Strategy Table 
	 * Sticks Rankings 
	 * 10	  3,4,11 
	 * 9      6,2,5 
	 * 8      7,3,1 etc.
	 * 
	 * The title "Strategy Table" should be proceeded by a \n.
	 * 
	 * @param strategyTable
	 *            An array of actionRankings.
	 * @return A string containing the properly formatted strategy table.
	 */
	static String strategyTableToString(int[][] strategyTable) {
		return "";     //TODO change to return the formatted String
	}	
	
	
	/**
	 * This updates the strategy table since a game was won.
	 * 
	 * The strategyTable has the set of actionRankings for each number of sticks
	 * left. The actionHistory array records the number of sticks the user took 
	 * when a given number of sticks remained on the table. Remember that 
	 * indexing starts at 0. For example, if actionHistory at index 6 is 2, 
	 * then the user took 2 sticks when there were 7 sticks remaining on the 
	 * table.  
	 * For each action noted in the history, this calls the 
	 * updateActionRankingOnWin method passing the corresponding action 
	 * and actionRanking. After calling this method, the actionHistory is
	 * cleared (all values set to 0).
	 * 
	 * @param strategyTable
	 *            An array of actionRankings.
	 * 
	 * @param actionHistory
	 *            An array where the index indicates the sticks left and the
	 *            element is the action that was made.
	 */
	static void updateStrategyTableOnWin(int[][] strategyTable, int[] actionHistory) {
		    //TODO 
	}
	
	/**
	 * This updates the strategy table for a loss.
	 * 
	 * The strategyTable has the set of actionRankings for each number of sticks
	 * left. The actionHistory array records the number of sticks the user took 
	 * when a given number of sticks remained on the table. Remember that 
	 * indexing starts at 0. For example, if actionHistory at index 6 is 2, 
	 * then the user took 2 sticks when there were 7 sticks remaining on the 
	 * table. 
	 * For each action noted in the history, this calls the 
	 * updateActionRankingOnLoss method passing the corresponding action 
	 * and actionRanking. After calling this method, the actionHistory is 
	 * cleared (all values set to 0).
	 * 
	 * @param strategyTable
	 *            An array of actionRankings.
	 * @param actionHistory
	 *            An array where the index indicates the sticks left and the
	 *            element is the action that was made.
	 */
	static void updateStrategyTableOnLoss(int[][] strategyTable, int[] actionHistory) {
		    //TODO
	}	

	/**
	 * This method simulates a game between two players using their
	 * corresponding strategyTables. Use the aiChooseAction method
	 * to choose an action for each player. Record each player's 
	 * actions in their corresponding history array. 
	 * This method doesn't print out any of the actions being taken. 
	 * Player 1 should make the first move in the game.
	 * 
	 * @param startSticks
	 *            The number of sticks to start the game with.
	 * @param player1StrategyTable
	 *            An array of actionRankings.
	 * @param player1ActionHistory
	 *            An array for recording the actions that occur.
	 * @param player2StrategyTable
	 *            An array of actionRankings.
	 * @param player2ActionHistory
	 *            An array for recording the actions that occur.
	 * @return 1 or 2 indicating which player won the game.
	 */
	static int playAiVsAi(int startSticks, int[][] player1StrategyTable, 
			int[] player1ActionHistory, int[][] player2StrategyTable, 
			int[] player2ActionHistory) {
		return -1;     //TODO change to return the winning player.
	}

	/**
	 * This method has the computer play against itself many times. Each time 
	 * it plays it records the history of its actions and uses those actions 
	 * to improve its strategy.
	 * 
	 * Algorithm: 
	 * 1) Create a strategy table for each of 2 players with 
	 *    createAndInitializeStrategyTable. 
	 * 2) Create an action history for each player.  An action history is a 
	 *    single dimension array of int. Each index in action history 
	 *    corresponds to the number of sticks remaining where the 0 index is
	 *    1 stick remaining.
	 * 3) For each game, 
	 * 		4) Call playAiVsAi with the return value indicating the winner. 
	 * 		5) Call updateStrategyTableOnWin for the winner and 
	 * 		6) Call updateStrategyTableOnLoss for the loser. 
	 * 7) After the games are played then the strategyTable for whichever 
	 * 	  strategy won the most games is returned. When both players win the 
	 *    same number of games, return the first player's strategy table.
	 * 
	 * @param startSticks
	 *            The number of sticks to start with.
	 * @param numberOfGamesToPlay
	 *            The number of games to play and learn from.
	 * @return A strategyTable that can be used to make action choices when
	 *         playing a person. Returns null if startSticks is less than
	 *         Config.MIN_STICKS or greater than Config.MAX_STICKS. Also returns
	 *         null if numberOfGamesToPlay is less than 1.
	 */
	static int[][] trainAi(int startSticks, int numberOfGamesToPlay) {
		return null;     //TODO return the strategy table of the winning player
	}

}