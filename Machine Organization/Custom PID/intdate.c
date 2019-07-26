////////////////////////////////////////////////////////////////////////////////
// Main File:        intdate
// This File:        intdate.c
// Other Files:      sendsig.c, division.c
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
#include <time.h>

// Time of seconds between alarm calls
static int timeSeconds = 3;

// Counter for number of times keyboard key was pressed
static int keySigCounter = 0;

// Handler function for alarm
void handlerAlarm(int signal) {
  // Variable to keep track of time
  time_t currentTime;
  time(&currentTime);

  // Prints time and date
  printf("PID: %d | Current Time: %s", getpid(), ctime(&currentTime));

  alarm(timeSeconds);
}

// Handler for kill signals received
void handlerKill(int signal) {
  // Checks if SIGINT was received, prints number of times SIGUSR1 was pressed
  // and exits the program
  if (signal == SIGINT) {
    printf("\nSIGINT received.\n");
    printf("SIGUSR1 was received %d times. Exiting now.\n", keySigCounter);
    exit(0);
  }
}

// Handler for SIGUSR1 signals received
void handlerKey(int signal) {
  // Checks if SIGUSR1 was received and increments keySigCounter
  if (signal == SIGUSR1) {
    keySigCounter++;
    printf("SIGUSR1 caught!\n");
  }
}

// Main method of function
int main() {
  // Sets up alarm
  struct sigaction alarmSig;
  memset (&alarmSig, 0, sizeof(alarmSig));
  alarmSig.sa_handler = handlerAlarm;

  // Tries to register alarm
  if (sigaction(SIGALRM, &alarmSig, NULL) != 0) {
    printf("Error: SIGALRM can't be set");
  }

  // Program starts
  printf("Pid and time will be printed every 3 seconds.\n");
  printf("Enter ^C to end the program.\n");

  alarm(timeSeconds);

  // Sets up kill input (crtl-c) signal
  struct sigaction killSig;
  memset (&killSig, 0, sizeof(killSig));
  killSig.sa_handler = handlerKill;

  // Tries to register kill signaler
  if (sigaction(SIGINT, &killSig, NULL) != 0) {
    printf("Error: SIGINT can't be set");
  }

  // Sets up kill SIGUSR1 signal
  struct sigaction keySig;
  memset (&keySig, 0, sizeof(keySig));
  keySig.sa_handler = handlerKey;

  // Tries to register SIGUSR1 signaler
  if (sigaction(SIGUSR1, &keySig, NULL) != 0) {
     printf("Error: SIGUSR1 can't be set");
  }

  // Loop to keep program running until user presses crtl-c (kill)
  while (1) {
  }
}

