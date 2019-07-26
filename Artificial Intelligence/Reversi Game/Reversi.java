/////////////////////////////////////////////////////////////////////////////
// Title:            Reversi
// Files:            Reversi.java (current file)
// Project:			 hw6 - Question 1
// Semester:         CS540 Fall 2017
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// NetID:         	 koelergoular
// Section:      	 002
////////////////////////////80 columns wide //////////////////////////////////

import java.util.*;

class State {
    char[] board;

    public State(char[] arr) {
        this.board = Arrays.copyOf(arr, arr.length);
    }

    public int getScore() {
    		//Variables for number of pieces
    		int darkPieces = 0;
		int lightPieces = 0;
        	
		//Updates number of dark/light pieces on the board
		for (int i = 0; i < board.length; i++) {
			if (board[i] == '1') darkPieces++;
			else if (board[i] == '2') lightPieces++;
		}
		
		//Returns score according to difference of number of pieces
		if (darkPieces < lightPieces) {
			return -1;
		}
		else if (darkPieces == lightPieces) {
			return 0;
		}
		else {
			return 1;
		}
    }
    
    public boolean isTerminal() {
    		//Number of successors for each player
    		int darkPiecesSuccessors = getSuccessors('1').length;
        	int lightPiecesSuccessors = getSuccessors('2').length;
        	
        	//Returns true if terminal and false if not
        	if (darkPiecesSuccessors == 0 && lightPiecesSuccessors == 0) {
        		return true;
        	}
        	else {
        		return false;
        	}
    }

    public State[] getSuccessors(char player) {
    		//List of successors as States
    		ArrayList<State> successorsList = new ArrayList<State>();
    		
    		//State to add in successorsList
    	 	State currentState;
    	 	
    		//Board to add in currentState
    		char [] currentBoard = new char[16];
    		
    		//2D Board to better handle moves
    		char [][] charactersBoard = new char[4][4];
    		
    		//Sets opponent player value
    		char opponentPlayer = '1';
    		if (player == '1') opponentPlayer = '2';
    		
    		//Copies board to currentBoard and charactersBoard (2D board)
    		int count = 0;
    		
    		for (int i = 0; i < 4; i++) {
    			for (int j = 0; j < 4; j++) {
    				currentBoard[count] = board[count];
    				charactersBoard[i][j] = board[count];
    				count++;
    			}
    		}
    		
    		//Main loop to decide and perform moves
    	 	for(int a = 0; a < 4; a++) {
    	 		for (int b = 0; b < 4; b++) {
    	 			if (charactersBoard[a][b] == '0') { 
    	 				//Variables to keep track of possible moves
    	 				boolean hasSuccessor, hasLeftRightMoveA, hasLeftRightMoveB, 
    	 						hasUpDownMoveA, hasUpDownMoveB, hasDiagonalMoveA, 
    	 						hasDiagonalMoveB;
    	 				hasSuccessor = hasLeftRightMoveA = hasLeftRightMoveB = 
    	 						hasUpDownMoveA = hasUpDownMoveB = hasDiagonalMoveA = 
    	 						hasDiagonalMoveB = false;
    	 				
    	 				//Divides the 4x4 square into smaller 2x2 squares and 
    	 				//analyzes each case separately
    	 				//DOWN RIGHT SQUARE
    	 				if (a > 1 && b > 1) {
    	 					//Checks for LEFT/RIGHT successors
        					if (charactersBoard[a][b - 2] == player && 
        							charactersBoard[a][b - 1] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[a*4 + b - 1] = player;
    							hasSuccessor = hasLeftRightMoveA = true;
    						}
        					else if (b == 3 && 
        							charactersBoard[a][b - 3] == player && 
        							charactersBoard[a][b - 2] == opponentPlayer && 
        							charactersBoard[a][b - 1] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
        						currentBoard[a*4 + b - 1] = player;
        						currentBoard[a*4 + b - 2] = player;
        						hasSuccessor = hasLeftRightMoveB = true;
        					}
        					
        					//Checks for UP/DOWN successors
        					if (charactersBoard[a - 2][b] == player && 
        							charactersBoard[a - 1][b] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b] = player;
    							hasSuccessor = hasUpDownMoveA = true;
    						}
        					else if (a == 3 && 
        							charactersBoard[a - 3][b] == player && 
        							charactersBoard[a - 2][b] == opponentPlayer && 
        							charactersBoard[a - 1][b] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b] = player;
    							currentBoard[(a - 2)*4 + b] = player;
    							hasSuccessor = hasUpDownMoveB = true;
        					}
        					
        					//Checks for DIAGONAL successors
        					if (charactersBoard[a - 2][b - 2] == player && 
        							charactersBoard[a - 1][b - 1] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b - 1] = player;
    							hasSuccessor = hasDiagonalMoveA = true;
    						}
        					else if (a == 3 && b == 3 && 
        							charactersBoard[a - 3][b - 3] == player && 
        							charactersBoard[a - 2][b - 2] == opponentPlayer && 
        							charactersBoard[a - 1][b - 1] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b - 1] = player;
    							currentBoard[(a - 2)*4 + b - 2] = player;
    							hasSuccessor = hasDiagonalMoveB = true;
        					}
        					
        					//Adds updated board to successorsList and changes 
        					//it back to original
        					if (hasSuccessor) {
        						currentState = new State(currentBoard);
    	 						successorsList.add(currentState);
    	 						
    	 						currentBoard[a*4 + b] = '0';
    	 						
    	 						if (hasLeftRightMoveA) {
    	 							currentBoard[a*4 + b - 1] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveA = false;
    	 						}
    	 						if (hasLeftRightMoveB) {
    	 							currentBoard[a*4 + b - 1] = opponentPlayer;
    	 							currentBoard[a*4 + b - 2] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveB = false;
    	 						}
    	 						if (hasUpDownMoveA) {
    	 							currentBoard[(a - 1)*4 + b] = opponentPlayer;
    	 							hasSuccessor = hasUpDownMoveA = false;
    	 						}
    	 						if (hasUpDownMoveB) {
    	 							currentBoard[(a - 1)*4 + b] = opponentPlayer;	
        							currentBoard[(a - 2)*4 + b] = opponentPlayer;
        							hasSuccessor = hasUpDownMoveB = false;
    	 						}
    	 						if (hasDiagonalMoveA) {
    	 							currentBoard[(a - 1)*4 + b - 1] = opponentPlayer;
    	 							hasSuccessor = hasDiagonalMoveA = false;
    	 						}
    	 						if (hasDiagonalMoveB) {
    	 							currentBoard[(a - 1)*4 + b - 1] = opponentPlayer;
        							currentBoard[(a - 2)*4 + b - 2] = opponentPlayer;
        							hasSuccessor = hasDiagonalMoveB = false;
    	 						}
        					}
    	 				}

        				//DOWN LEFT SQUARE
        				else if (a > 1 && b < 2) {
        					//Checks for LEFT/RIGHT successors
        					if (charactersBoard[a][b + 2] == player && 
        							charactersBoard[a][b + 1] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[a*4 + b + 1] = player;
    							hasSuccessor = hasLeftRightMoveA = true;
    						}
        					else if (b == 0 && 
        							charactersBoard[a][b + 3] == player && 
        							charactersBoard[a][b + 2] == opponentPlayer && 
        							charactersBoard[a][b + 1] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[a*4 + b + 1] = player;
    							currentBoard[a*4 + b + 2] = player;
    							hasSuccessor = hasLeftRightMoveB = true;
        					}
        					
        					//Checks for UP/DOWN successors
        					if (charactersBoard[a - 2][b] == player && 
        							charactersBoard[a - 1][b] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b] = player;
    							hasSuccessor = hasUpDownMoveA = true;
    						}
        					else if (a == 3 && 
        							charactersBoard[a - 3][b] == player && 
        							charactersBoard[a - 2][b] == opponentPlayer && 
        							charactersBoard[a - 1][b] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b] = player;	
    							currentBoard[(a - 2)*4 + b] = player;
    							hasSuccessor = hasUpDownMoveB = true;
        					}
        					
        					//Checks for DIAGONAL successors
        					if (charactersBoard[a - 2][b + 2] == player && 
        							charactersBoard[a - 1][b + 1] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b + 1] = player;
    							hasSuccessor = hasDiagonalMoveA = true;
    						}
        					else if (a == 3 && b == 0 && 
        							charactersBoard[a - 3][b + 3] == player &&
        							charactersBoard[a - 2][b + 2] == opponentPlayer && 
        							charactersBoard[a - 1][b + 1] == opponentPlayer) {
        						//Changes current board 
        						currentBoard[a*4 + b] = player;
    							currentBoard[(a - 1)*4 + b + 1] = player;
    							currentBoard[(a - 2)*4 + b + 2] = player;
    							hasSuccessor = hasDiagonalMoveB = true;
        					}
        					
        					//Adds updated board to successorsList and changes 
        					//it back to original
        					if (hasSuccessor) {
        						currentState = new State(currentBoard);
    	 						successorsList.add(currentState);
    	 						
    	 						currentBoard[a*4 + b] = '0';
    	 						
    	 						if (hasLeftRightMoveA) {
    	 							currentBoard[a*4 + b + 1] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveA = false;
    	 						}
    	 						if (hasLeftRightMoveB) {
    	 							currentBoard[(a)*4 + b + 1] = opponentPlayer;
    	 							currentBoard[(a)*4 + b + 2] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveB = false;
    	 						}
    	 						if (hasUpDownMoveA) {
    	 							currentBoard[(a - 1)*4 + b] = opponentPlayer;
    	 							hasSuccessor = hasUpDownMoveA = false;
    	 						}
    	 						if (hasUpDownMoveB) {
    	 							currentBoard[(a - 1)*4 + b] = opponentPlayer;
    	 							currentBoard[(a - 2)*4 + b] = opponentPlayer;
        							hasSuccessor = hasUpDownMoveB = false;
    	 						}
    	 						if (hasDiagonalMoveA) {
    	 							currentBoard[(a - 1)*4 + b + 1] = opponentPlayer;
    	 							hasSuccessor = hasDiagonalMoveA = false;
    	 						}
    	 						if (hasDiagonalMoveB) {
    	 							currentBoard[(a - 1)*4 + b + 1] = opponentPlayer;
    	 							currentBoard[(a - 2)*4 + b + 2] = opponentPlayer;
        							hasSuccessor = hasDiagonalMoveB = false;
    	 						}
        					}
        				}
    	 				
    	 				//UP RIGHT SQUARE
    	 				else if (a < 2 && b > 1) {
        					//Checks for LEFT/RIGHT successors
        					if (charactersBoard[a][b - 2] == player && 
        							charactersBoard[a][b - 1] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[a*4 + b - 1] = player;
    							hasSuccessor = hasLeftRightMoveA = true;
    						}
        					else if (b == 3 && 
        							charactersBoard[a][b - 3] == player &&
        							charactersBoard[a][b - 2] == opponentPlayer &&
        							charactersBoard[a][b - 1] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[a*4 + (b - 1)] = player;
    							currentBoard[a*4 + (b - 2)] = player;
    							hasSuccessor = hasLeftRightMoveB = true;
        					}
        					
        					//Checks for UP/DOWN successors
        					if (charactersBoard[a + 2][b] == player && 
        							charactersBoard[a + 1][b] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
        						currentBoard[(a + 1)*4 + b] = player;
        						hasSuccessor = hasUpDownMoveA = true;
    						} 
        					else if (a == 0 && 
        							charactersBoard[a + 3][b] == player &&
        							charactersBoard[a + 2][b] == opponentPlayer && 
        							charactersBoard[a + 1][b] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[(a + 1)*4 + b] = player;
    							currentBoard[(a + 2)*4 + b] = player;
    							hasSuccessor = hasUpDownMoveB = true;
        					}
        					
        					//Checks for DIAGONAL successors
        					if (charactersBoard[a + 2][b - 2] == player && 
        							charactersBoard[a + 1][b - 1] == opponentPlayer) {
    							//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[(a + 1)*4 + b - 1] = player;
    							hasSuccessor = hasDiagonalMoveA = true;
    						}
        					else if (a == 0 && b == 3 && 
        							charactersBoard[a + 1][b - 1] == opponentPlayer && 
        							charactersBoard[a + 2][b - 2] == opponentPlayer && 
        							charactersBoard[a + 3][b - 3] == player) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
    							currentBoard[(a + 1)*4 + b - 1] = player;
    							currentBoard[(a + 2)*4 + b - 2] = player;
    							hasSuccessor = hasDiagonalMoveB = true;
        					}
        					
        					//Adds updated board to successorsList and changes 
        					//it back to original
        					if (hasSuccessor) {
        						currentState = new State(currentBoard);
    	 						successorsList.add(currentState);
    	 						
    	 						currentBoard[a*4 + b] = '0';
    	 						
    	 						if (hasLeftRightMoveA) {
    	 							currentBoard[a*4 + b - 1] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveA = false;
    	 						}
    	 						if (hasLeftRightMoveB) {
    	 							currentBoard[a*4 + b - 1] = opponentPlayer;
    								currentBoard[a*4 + b - 2] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveB = false;
    	 						}
    	 						if (hasUpDownMoveA) {
    	 							currentBoard[(a + 1)*4 + b] = opponentPlayer;
    	 							hasSuccessor = hasUpDownMoveA = false;
    	 						}
    	 						if (hasUpDownMoveB) {
    	 							currentBoard[(a + 1)*4 + b] = opponentPlayer;
    								currentBoard[(a + 2)*4 + b] = opponentPlayer;
        							hasSuccessor = hasUpDownMoveB = false;
    	 						}
    	 						if (hasDiagonalMoveA) {
    	 							currentBoard[(a + 1)*4 + b - 1] = opponentPlayer;
    	 							hasSuccessor = hasDiagonalMoveA = false;
    	 						}
    	 						if (hasDiagonalMoveB) {
    	 							currentBoard[(a + 1)*4 + b - 1] = opponentPlayer;
    								currentBoard[(a + 2)*4 + b - 2] = opponentPlayer;
        							hasSuccessor = hasDiagonalMoveB = false;
    	 						}
        					}
        				}
    	 				
    	 				//UP LEFT SQUARE
    	 				else if (a < 2 && b < 2) {
    	 					//Checks for LEFT/RIGHT successors
    	 					if (charactersBoard[a][b + 2] == player && 
    	 							charactersBoard[a][b + 1] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[a*4 + b + 1] = player;
    							hasSuccessor = hasLeftRightMoveA = true;
    						}
        					else if (b == 0 && 
        							charactersBoard[a][b + 3] == player && 
        							charactersBoard[a][b + 2] == opponentPlayer && 
        							charactersBoard[a][b + 1] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
        						currentBoard[a*4 + b + 1] = player;
        						currentBoard[a*4 + b + 2] = player;
        						hasSuccessor = hasLeftRightMoveB = true;
        					}
    	 					
    	 					//Checks for UP/DOWN successors
    	 					if (charactersBoard[a + 2][b] == player && 
    	 							charactersBoard[a + 1][b] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[(a + 1)*4 + b] = player;
    							hasSuccessor = hasUpDownMoveA = true;
    						}
        					else if (a == 0 && 
        							charactersBoard[a + 3][b] == player && 
    								charactersBoard[a + 2][b] == opponentPlayer && 
    								charactersBoard[a + 1][b] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
        						currentBoard[(a + 1)*4 + b] = player;
        						currentBoard[(a + 2)*4 + b] = player;
        						hasSuccessor = hasUpDownMoveB = true;
    						}
        					
    	 					//Checks for DIAGONAL successors
        					if (charactersBoard[a + 2][b + 2] == player && 
        							charactersBoard[a + 1][b + 1] == opponentPlayer) {
        						//Changes current board
    							currentBoard[a*4 + b] = player;
    							currentBoard[(a + 1)*4 + b + 1] = player;
    							hasSuccessor = hasDiagonalMoveA = true;
    						}
        					else if (a == 0 && b == 0 && 
        							charactersBoard[a + 3][b + 3] == player && 
        							charactersBoard[a + 2][b + 2] == opponentPlayer &&
        							charactersBoard[a + 1][b + 1] == opponentPlayer) {
        						//Changes current board
        						currentBoard[a*4 + b] = player;
        						currentBoard[(a + 1)*4 + b + 1] = player;
        						currentBoard[(a + 2)*4 + b + 2] = player;
        						hasSuccessor = hasDiagonalMoveB = true;
        					}
        					
        					//Adds updated board to successorsList and changes 
        					//it back to original
        					if (hasSuccessor) {
        						currentState = new State(currentBoard);
    	 						successorsList.add(currentState);
    	 						
    	 						currentBoard[a*4 + b] = '0';
    	 						
    	 						if (hasLeftRightMoveA) {
    	 							currentBoard[a*4 + b + 1] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveA = false;
    	 						}
    	 						if (hasLeftRightMoveB) {
    	 							currentBoard[a*4 + b + 1] = opponentPlayer;
    								currentBoard[a*4 + b + 2] = opponentPlayer;
    	 							hasSuccessor = hasLeftRightMoveB = false;
    	 						}
    	 						if (hasUpDownMoveA) {
    	 							currentBoard[(a + 1)*4 + b] = opponentPlayer;
    	 							hasSuccessor = hasUpDownMoveA = false;
    	 						}
    	 						if (hasUpDownMoveB) {
    	 							currentBoard[(a + 1)*4 + b] = opponentPlayer;
    								currentBoard[(a + 2)*4 + b] = opponentPlayer;
        							hasSuccessor = hasUpDownMoveB = false;
    	 						}
    	 						if (hasDiagonalMoveA) {
    	 							currentBoard[(a + 1)*4 + b + 1] = opponentPlayer;
    	 							hasSuccessor = hasDiagonalMoveA = false;
    	 						}
    	 						if (hasDiagonalMoveB) {
    	 							currentBoard[(a + 1)*4 + b + 1] = opponentPlayer;
    								currentBoard[(a + 2)*4 + b + 2] = opponentPlayer;
        							hasSuccessor = hasDiagonalMoveB = false;
    	 						}
        					}
        				}
        				
        			}
        		}
        	}

    	 	State [] successors = new State[successorsList.size()];
        	
        	for(int c = 0; c < successorsList.size(); c++)	{
        		successors[c] = successorsList.get(c);
        	}
				
		return successors;
    }
 
    public void printState(int option, char player) {
    		//Option 1
    		if (option == 1 && !isTerminal()) {
    			State [] tempState = getSuccessors(player);
    			
    			for(int i = 0; i < tempState.length; i++) {
    				System.out.println(tempState[i].board);
    			}
    		}
    		
    		//Option 2
    		else if (option == 2) {
    			//Prints non-Terminal message if node is not terminal
    			if (!isTerminal()) {
        			System.out.println("non-terminal");
        		}
    			//Prints message according to difference of dark and light pieces
    			else {
    				int darkPieces = 0;
    				int lightPieces = 0;
    				
    				for (int i = 0; i < board.length; i++) {
    					if (board[i] == '1') darkPieces++;
    					else if (board[i] == '2') lightPieces++;
    				}
    				
    				if (darkPieces < lightPieces) {
    					System.out.println("-1");
    				}
    				else if (darkPieces == lightPieces) {
    					System.out.println("0");
    				}
    				else {
    					System.out.println("1");
    				}
    			}
    		}
    		
    		//Option 3
    		else if (option == 3) {
    			System.out.println(Minimax.run(this, player));
        		System.out.println(Minimax.visitedStates);
    		}
    		
    		//Option 5
    		else if (option == 5) {
    			System.out.println(Minimax.run_with_pruning(this, player));
    			System.out.println(Minimax.visitedStates);
    		}
    		
    		//Option 4 and Option 6
    		else if (option == 4 || option == 6) {
    			//Gets successors for current State
    			State [] successors = getSuccessors(player);
    			
    			//List with optimal successors
    			ArrayList<Integer> optimalSucessors = new ArrayList<Integer>();
    			
    			//String for optimal move
    			String optimalMove = "";
    			
    			//Booleans to keep track if optimal result is found
    			boolean maxResultFound = false;
    			boolean tieResultFound = false;
    			boolean minResultFound = false;
        		
        		//Loop to fill optimalSucessors array
        		for(int i = 0; i < successors.length; i++) {
        			if (option == 4) {
        				Integer optimalMinimax = Minimax.run(successors[i], player);
        				optimalSucessors.add(optimalMinimax);
        			}
        			else {
        				Integer optimalWPruning = Minimax.run_with_pruning(successors[i], player);
        				optimalSucessors.add(optimalWPruning);
        			}
        		}
        		
        		//Gets optimal move based on number of successors and player number
        		if (optimalSucessors.size() == 0 && !isTerminal()) {
        			optimalMove = getBoard();
        		}
        		else if(player == '1') {
        			for(int a = 0; a < optimalSucessors.size(); a++) {
        				if(optimalSucessors.get(a) == 1) {
        					optimalMove = successors[a].getBoard();
        					break;
        				}
        				else if(optimalSucessors.get(a) == 0 && !tieResultFound) {
        					optimalMove = successors[a].getBoard();
        					tieResultFound = true;
        					minResultFound = true;
        				}
        				else if(optimalSucessors.get(a) == -1 && !minResultFound) {
        					optimalMove = successors[a].getBoard();
        					minResultFound = true;
        				}
        			}
        		}
        		else if(player == '2') {
        			for(int b = 0; b < optimalSucessors.size(); b++) {
        				if(optimalSucessors.get(b) == -1) {
        					optimalMove = successors[b].getBoard();
        					break;
        				}
        				else if(!tieResultFound && optimalSucessors.get(b) == 0) {
        					optimalMove = successors[b].getBoard();
        					tieResultFound = true;
        					maxResultFound = true;
        				}
        				else if(!maxResultFound && optimalSucessors.get(b) == 1) {
        					optimalMove = successors[b].getBoard();
        					maxResultFound = true;
        				}
        			}
        		}
        		
        		if(optimalMove.length() != 0) {
        			System.out.println(optimalMove);
        		}
    		}
    		
    }

    public String getBoard() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            builder.append(this.board[i]);
        }
        return builder.toString().trim();
    }

    public boolean equals(State src) {
        for (int i = 0; i < 16; i++) {
            if (this.board[i] != src.board[i])
                return false;
        }
        return true;
    }
}

class Minimax {
	public static int visitedStates = 0;
	
	private static int max_value(State curr_state) {
		visitedStates++;
		
		if (curr_state.isTerminal()) {
			return curr_state.getScore();
		}
		else if (curr_state.getSuccessors('1').length == 0) {
			return min_value(curr_state);
		}
		else {
			int alpha = Integer.MIN_VALUE;
			
			for (int a = 0; a < curr_state.getSuccessors('1').length; a++) {
				alpha = Math.max(alpha, min_value(curr_state.getSuccessors('1')[a]));
			}
			
			return alpha;
		}
	}
	
	private static int min_value(State curr_state) {
		visitedStates++;
		
		if (curr_state.isTerminal()) {
			return curr_state.getScore();
		}
		else if (curr_state.getSuccessors('2').length == 0) {
			return max_value(curr_state);
		}
		else {
			int beta = Integer.MAX_VALUE;
			
			for (int b = 0; b < curr_state.getSuccessors('2').length; b++) {
				beta = Math.min(beta, max_value(curr_state.getSuccessors('2')[b]));
			}
			
			return beta;
		}
	}
	
	private static int max_value_with_pruning(State curr_state, int alpha, int beta) {
		visitedStates++;
		
		if (curr_state.isTerminal()) {
			return curr_state.getScore();
		}
		else if (curr_state.getSuccessors('1').length == 0) {
			return min_value_with_pruning(curr_state, alpha, beta);
		}
		else {
			for(int a = 0; a < curr_state.getSuccessors('1').length; a++) {
				alpha = Math.max(alpha, 
						min_value_with_pruning(curr_state.getSuccessors('1')[a], alpha, beta));
				//Returns beta if alpha is bigger than or equal to
				if(alpha >= beta) return beta;
			}
			
			return alpha;
		}
	}
	
	private static int min_value_with_pruning(State curr_state, int alpha, int beta) {
		visitedStates++;
		
		if (curr_state.isTerminal()) {
			return curr_state.getScore();
		}
		else if (curr_state.getSuccessors('2').length == 0) {
			return max_value_with_pruning(curr_state, alpha, beta);
		}
		else {
			for(int b = 0; b < curr_state.getSuccessors('2').length; b++) {
				beta = Math.min(beta, 
						max_value_with_pruning(curr_state.getSuccessors('2')[b], alpha, beta));
				//Returns alpha if beta is bigger than or equal to
				if(alpha >= beta) return alpha;
			}
			
			return beta;
		}
	}
	
	public static int run(State curr_state, char player) {
		int runValue = 0;
		
		if(player == '1') runValue = max_value(curr_state);
		else runValue = min_value(curr_state);
		
		return runValue;
	}
	
	public static int run_with_pruning(State curr_state, char player) {
		int runValue = 0;
		
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		if(player == '1') runValue = max_value_with_pruning(curr_state, alpha, beta);
		else runValue = min_value_with_pruning(curr_state, alpha, beta);
		
		return runValue;
	}
}

public class Reversi {
    public static void main(String args[]) {
        if (args.length != 3) {
            System.out.println("Invalid Number of Input Arguments");
            return;
        }
        int flag = Integer.valueOf(args[0]);
        char[] board = new char[16];
        for (int i = 0; i < 16; i++) {
            board[i] = args[2].charAt(i);
        }
        int option = flag / 100;
        char player = args[1].charAt(0);
        if ((player != '1' && player != '2') || args[1].length() != 1) {
            System.out.println("Invalid Player Input");
            return;
        }
        State init = new State(board);
        init.printState(option, player);
    }
}