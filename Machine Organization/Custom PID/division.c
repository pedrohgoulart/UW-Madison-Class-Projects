////////////////////////////////////////////////////////////////////////////////
// Main File:        intdate
// This File:        division.c
// Other Files:      intdate.c, sendsig.c
// Semester:         CS 354 Spring 2018
//
// Author:           Pedro Henrique Koeler Goulart
// Email:            koelergoular@wisc.edu
// CS Login:         koeler-goulart
//
/////////////////////////// OTHER SOURCES OF HELP //////////////////////////////
//                   fully acknowledge and credit all sources of help,
//                   other than Instructors and TAs.
//
// Persons:          No persons.
//
// Online sources:   No online sources.
////////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>

// Variable for successful operations counter
static int operations = 0;

// Handler for kill signals received
void handlerKill(int signal) {
  // Checks if SIGINT was received and exists the program
  // and exits the program
  if (signal == SIGINT) {
      printf("\nTotal number of operations successfully completed: %d\n",
          operations);
      printf("The program will be terminated.\n");
      exit(0);
  }
}

// Handler for division by zero
void handlerZero(int signal) {
  // Checks if SIGFPE was received, displays error message, and exits program
  if (signal == SIGFPE) {
      printf("Error: a division by 0 operation was attempted.\n");
      printf("Total number of operations completed successfully: %d\n",
          operations);
      printf("The program will be terminated.\n");
      exit(0);
  }
}

int main() {
  // Variable for max string size
  int max_string_size = 100;

  // Temporary String for fgets
  char tempString[100];

  // Variables for integer division
  int firstInt = 0;
  int secondInt = 0;

  // Sets up kill input (crtl-c) signal
  struct sigaction killSig;
  memset (&killSig, 0, sizeof(killSig));
  killSig.sa_handler = handlerKill;

  // Tries to register kill signaler
  if (sigaction(SIGINT, &killSig, NULL) != 0) {
    printf("Error: SIGINT can't be set");
  }

  // Sets up kill SIGUSR1 signal
  struct sigaction divZero;
  memset (&divZero, 0, sizeof(divZero));
  divZero.sa_handler = handlerZero;

  // Tries to register SIGUSR1 signaler
  if (sigaction(SIGFPE, &divZero, NULL) != 0) {
     printf("Error: SIGUSR1 can't be set");
  }

  while (1) {
    // Gets first integer and assigns it to firstInt
    printf("Enter first integer: ");
    fgets(tempString, max_string_size, stdin);
    firstInt = atoi(tempString);

    // Gets second integer and assigns it to secondInt
    printf("Enter second integer: ");
    fgets(tempString, max_string_size, stdin);
    secondInt = atoi(tempString);

    // Prints result
    printf("%d / %d is %d with a remainder of %d\n", firstInt, secondInt,
         firstInt/secondInt, firstInt%secondInt);

    // Increments operations counter (if no errors are caught first)
    operations++;
  }
}
