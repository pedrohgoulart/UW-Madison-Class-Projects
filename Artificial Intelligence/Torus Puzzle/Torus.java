/////////////////////////////////////////////////////////////////////////////
// Title:            Torus
// Files:            Torus.java (current file)
// Project:			 hw3 - Question 1
// Semester:         CS540 Fall 2017
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// NetID:         	 koelergoular
// Section:      	 002
////////////////////////////80 columns wide //////////////////////////////////

import java.util.*;

/**
 * Program that creates a puzzle of 9 pieces with 8 numbers and an empty space.
 * The empty space can be moved leftwards, upwards, rightwards, and downwards, 
 * with the 0 being moved to the other side of the puzzle in case it is moved 
 * towards the bounds of the puzzle (position 3 goes to position 1 if moved 
 * left). 
 * The puzzle is represented on a line and there are 5 functions to it,
 * 100 shows the successors, 2XX uses a depth-limited depth-first search
 * with a cutoff of XX, 3XX does the same as previous function but displays the
 * backpointers, 4XX does the same thing as 2XX but prints out the prefix path,
 * and 500 shows the path to the goal.
 * 
 * @author CS540, Pedro Henrique Koeler Goulart
 *
 */

/**
 * The State class holds information on the board, parent of successor, and depth.
 */
class State {
	int[] board;
	State parentPt;
	int depth;

	public State(int[] arr) {
		this.board = Arrays.copyOf(arr, arr.length);
		this.parentPt = null;
		this.depth = 0;
	}

	public State[] getSuccessors() {
		State[] successors = new State[4];
		
		//For loop to search 0's position in the board
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0) {
				//Variables to know how much should 0 be moved
				int moveLeft = -1; //assumes 0 is not in Left-most part
				int moveUp = -3; //assumes 0 is not in Upper-most part
				int moveRight = 1; //assumes 0 is not in Right-most part
				int moveDown = 3; //assumes 0 is not in Down-most part
				
				//Checks if 0 is in Left-most part and updates moves
				if (i % 3 == 0){
					moveLeft = 2;
				}
				
				//Checks if 0 is in Upper-most part and updates moves
				if (i <= 2) {
					moveUp = 6;
				}
				
				//Checks if 0 is in Right-most part and updates moves
				if ((i - 2) % 3 == 0) {
					moveRight = -2;
				}
				
				//Checks if 0 is in Down-most part and updates moves
				if (i >= 6) {
					moveDown = -6;
				}
				
				//Generates 4 boards and copies original board to them
				int[][] sucessorsBoard = new int [4][9];
				
				for (int a = 0; a < 4; a++) {
					for (int b = 0; b < board.length; b++) {
						sucessorsBoard[a][b] = board[b];
					}
				}
				
				//Swaps 0 position for LEFT MOVE (first board)
				sucessorsBoard[0][i] = sucessorsBoard[0][i + moveLeft];
				sucessorsBoard[0][i + moveLeft] = 0;
				
				//Swaps 0 position for UP MOVE (second board)
				sucessorsBoard[1][i] = sucessorsBoard[1][i + moveUp];
				sucessorsBoard[1][i + moveUp] = 0;
				
				//Swaps 0 position for RIGHT MOVE (third board)
				sucessorsBoard[2][i] = sucessorsBoard[2][i + moveRight];
				sucessorsBoard[2][i + moveRight] = 0;
				
				//Swaps 0 position for DOWN MOVE (forth board)
				sucessorsBoard[3][i] = sucessorsBoard[3][i + moveDown];
				sucessorsBoard[3][i + moveDown] = 0;
				
				//Creates 2 String lists and copies the successorsBoard to it
				List<String> unsortedList = new ArrayList<String>();
				List<String> sortedList = new ArrayList<String>();
				
				for (int a = 0; a < 4; a++) {
					String temporaryBoard = Arrays.toString(sucessorsBoard[a]);
					unsortedList.add(temporaryBoard);
					sortedList.add(temporaryBoard);
				}
				
				//Sorts one of the lists
				Collections.sort(sortedList);
				
				//Uses the sorted list to find the first board, uses the unsorted 
				//list to get the position of that board in sucessorsBoard list
				for (int a = 0; a < 4; a++) {
					for (int b = 0; b < 4; b++) {
						if (sortedList.get(a).equals(unsortedList.get(b))) {
							State tempState = new State(sucessorsBoard[b]);
							successors[a] = tempState;
						}
					}
				}
			}
		}
		
		return successors;
	}
	
	public void printState(int option) {
		//Options 1, 2, 4, and 5
		if (option != 3) {
			System.out.println(getBoard());
		}
		
		//Option 3
		else if (option == 3) {
			if (depth > 0) {
				System.out.println(getBoard() + " parent " + parentPt.getBoard());
			}
			else {
				String initialStateParents = "";
				
				for (int i = 0; i < board.length; i++) {
					initialStateParents += "0 ";
				}
				
				System.out.println(getBoard() + " parent " + initialStateParents);
			}
		}
	}

	public String getBoard() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			builder.append(this.board[i]).append(" ");
		}
		return builder.toString().trim();
	}

	public boolean isGoalState() {
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != (i + 1) % 9)
				return false;
		}
		return true;
	}

	public boolean equals(State src) {
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != src.board[i])
				return false;
		}
		return true;
	}
}


/**
 * This is the main class and is responsible for analyzing user input and calling
 * the State class to generate board and successors.
 */
public class Torus {

	public static void main(String args[]) {
		//Checks for valid input
		if (args.length < 10) {
			System.out.println("Invalid Input");
			return;
		}
		
		//Analyzes input and gets flag and board initial state
		int flag = Integer.valueOf(args[0]);
		int[] board = new int[9];
		
		//Populates board
		for (int i = 0; i < 9; i++) {
			board[i] = Integer.valueOf(args[i + 1]);
		}
		
		//Gets flag's 3rd digit for option, and remaining digits for cutoff
		int option = flag / 100;
		int cutoff = flag % 100;
		
		if (option == 1) {
			State init = new State(board);
			
			State[] successors = init.getSuccessors();
			
			for (State successor : successors) {
				successor.printState(option);
			}
		}
		
		else {
			State init = new State(board);
			
			Stack<State> stack = new Stack<>();
			List<State> prefix = new ArrayList<>();
			int goalChecked = 0;
			int maxStackSize = Integer.MIN_VALUE;
			
			boolean checkGoalLoop = false;

			while (true) {
				stack.push(init);
				
				while (!stack.isEmpty()) {
					//Checks size of stack and updates maxStackSize
					if (stack.size() > maxStackSize) {
						maxStackSize = stack.size();
					}
					
					//Pops current state from the stack
					State currentState = stack.pop();
					
					//Variable to keep track of the State's Parent position
					int stateParentPos = 0;
					
					//Finds current State's parent in the prefix list
					for (int i = 0; i < prefix.size(); i++) {
						if (prefix.get(i).equals(currentState.parentPt)) {
							//Removes everything after the position of the 
							//parent of current State
							while (prefix.size() > (stateParentPos = i + 1)) {
								prefix.remove(stateParentPos);
							}
						}
					}
					
					//Adds current state to the prefix list and prints it
					prefix.add(currentState);
					if (option < 4) {
						currentState.printState(option); //Only if option 2 or 3
					}
					
					//OPTION 4
					//Prints only the prefix path for first goalChecked state
					if (option == 4) {
						if (goalChecked == cutoff + 1) {
							for (int b = 0; b < prefix.size(); b++) {
								prefix.get(b).printState(option);
							}
						}
					}
					
					//Checks if current state is goal state
					goalChecked++;
					if (currentState.isGoalState()) {
						if (option == 5) {
							checkGoalLoop = true;
							//Prints solution path to goal
							for (int a = 0; a < prefix.size(); a++) {
								prefix.get(a).printState(option);
							}
						}
						break; //Breaks from current loop
					}
					
					//Gets successors for the current state
					State[] successors = currentState.getSuccessors();
					
					//Adds each successor to the stack if they aren't in prefix
					for (int j = 0; j < successors.length; j++) {
						boolean listCheck = false;
						
						//Checks if current Successor is in prefix list
						for (int k = 0; k < prefix.size(); k++) {
							if (successors[j].equals(prefix.get(k))) {
								listCheck = true;
								k = prefix.size(); //ends loop
							}
						}
						
						if (!listCheck) {
							//Updates the current Successor's parent and depth
							successors[j].parentPt = currentState;
							successors[j].depth = currentState.depth + 1;
							
							//Adds successor to the stack
							if (successors[j].depth <= cutoff) {
								stack.push(successors[j]);
							}
						}
					}
				}
				
				if (option != 5) {
					break;
				}
				
				//Increases cutoff to continue with execution
				cutoff++;
				
				//Resets prefix list
				prefix = new ArrayList<>();
				
				//Checks if goal has been reached
				if (checkGoalLoop) {
					System.out.println("Goal-check " + goalChecked);
					System.out.println("Max-stack-size " + maxStackSize);
					break;
				}
			}
		}
	}
}