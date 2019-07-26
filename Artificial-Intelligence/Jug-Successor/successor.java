/////////////////////////////////////////////////////////////////////////////
// Title:            Sucessor
// Files:            successor.java (current file)
// Project:			 hw2 - Question 1
// Semester:         CS540 Fall 2017
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// NetID:         	 koelergoular
// Section:      	 002
////////////////////////////80 columns wide //////////////////////////////////

import java.util.*;

/**
 * Program that shows 3 jugs. Each jug has a capacity and a current content.
 * The jugs can be emptied, filled, or used to fill another jug. The program 
 * starts by emptying each jug separately, then it fills each jug separately, 
 * and finally uses one of the jugs to fill another jug (until former is empty
 * or latter is full).
 * 
 * @author CS540, Pedro Henrique Koeler Goulart
 *
 */
public class successor {
    public static class JugState {
        int[] Capacity = new int[]{0,0,0};
        int[] Content = new int[]{0,0,0};
        
        public JugState(JugState copyFrom) {
            this.Capacity[0] = copyFrom.Capacity[0];
            this.Capacity[1] = copyFrom.Capacity[1];
            this.Capacity[2] = copyFrom.Capacity[2];
            this.Content[0] = copyFrom.Content[0];
            this.Content[1] = copyFrom.Content[1];
            this.Content[2] = copyFrom.Content[2];
        }
        
        public JugState() {
        }
        
        public JugState(int A,int B, int C) {
            this.Capacity[0] = A;
            this.Capacity[1] = B;
            this.Capacity[2] = C;
        }
        
        public JugState(int A,int B, int C, int a, int b, int c) {
            this.Capacity[0] = A;
            this.Capacity[1] = B;
            this.Capacity[2] = C;
            this.Content[0] = a;
            this.Content[1] = b;
            this.Content[2] = c;
        }

        public void printContent() {
            System.out.println(this.Content[0] + " " + this.Content[1] + " " 
            					+ this.Content[2]);
        }

        public ArrayList<JugState> getNextStates() {
            ArrayList<JugState> successors = new ArrayList<>();
            int sizeOfContent = this.Content.length;
            
            //Empty jug
            //For loop according to the size of the content list
            for (int i = 0; i < sizeOfContent; i++) {
            	//Creates a copy of JugState to empty
            	JugState jugCopy = new JugState(this);
            	
            	//Checks if current jar is empty, and if it is not, empties it
            	if (jugCopy.Content[i] != 0) {
            		jugCopy.Content[i] = 0;
            		
            		successors.add(jugCopy);
            	}
            }
            
            //Fill jug
            //For loop according to the size of the content list
            for (int i = 0; i < sizeOfContent; i++) {
            	//Creates a copy of JugState to fill
            	JugState jugCopy = new JugState(this);
            	
            	//Checks if current jar is not full, and if it is not, fills it
            	if (jugCopy.Content[i] < jugCopy.Capacity[i]) {
            		jugCopy.Content[i] = jugCopy.Capacity[i];

            		successors.add(jugCopy);
            	}
            }
            
            //Pour water from one jug to another
            //For loop used for new jug
            for (int i = 0; i < sizeOfContent; i++) {
            	//For loop used for current jug
            	for (int j = 0; j < sizeOfContent; j++) {
            		//Checks if jugs are equal and if current jug has liquid
            		if (i != j && this.Content[j] > 0) {
            			//Creates a copy of JugState to pour water from/to
                		JugState jugCopy = new JugState(this);
                		
                		//Calculates space available in new jug
                		int firstJugSpace = jugCopy.Capacity[i] 
                							- jugCopy.Content[i];
                		
                		//Checks if there is space in new jug
                		if (firstJugSpace > 0) {
                			//Checks if space is less than available content
                			if (jugCopy.Content[j] < firstJugSpace) {
                				jugCopy.Content[i] += jugCopy.Content[j];
                				jugCopy.Content[j] = 0;
                			}
                			
                			//Checks if space is >= than/to available content
                			else {
                				jugCopy.Content[i] = jugCopy.Capacity[i];
                				jugCopy.Content[j] -= firstJugSpace;
                			}
                			
                			successors.add(jugCopy);
                		}
            		}
            	}
            }

            return successors;
        }
    }

    public static void main(String[] args) {
        if( args.length != 6 ) {
            System.out.println("Usage: java successor [A] [B] [C] [a] [b] [c]");
            return;
        }

        // parse command line arguments
        JugState a = new JugState();
        a.Capacity[0] = Integer.parseInt(args[0]);
        a.Capacity[1] = Integer.parseInt(args[1]);
        a.Capacity[2] = Integer.parseInt(args[2]);
        a.Content[0] = Integer.parseInt(args[3]);
        a.Content[1] = Integer.parseInt(args[4]);
        a.Content[2] = Integer.parseInt(args[5]);

        // Implement this function
        ArrayList<JugState> asist = a.getNextStates();

        // Print out generated successors
        for(int i=0;i< asist.size(); i++) {
            asist.get(i).printContent();
        }

        return;
    }
}

