////////////////////////////////////////////////////////////////////////////////
// Main File:        intdate
// This File:        sendsig.c
// Other Files:      intdate.c, division.c
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

int main(int argc, char *argv[]) {
  if (argc != 3) {
    fprintf(stderr, "Usage: <signal type> <pid>\n");
    exit(1);
  }

  // Gets arguments and splits them into signal type and pid
  char* signal = *(argv + 1);
  int pid = atoi(*(argv + 2));

  // Send SIGUSR1 or SIGINT according to user input
  if (strcmp("-u", signal) == 0) {
    kill(pid, SIGUSR1);
  } else if (strcmp("-i", signal) == 0) {
    kill(pid, SIGINT);
  } else {
    fprintf(stderr, "The signal type was not recognized\n");
    exit(1);
  }
}

