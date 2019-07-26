//////////////////////////////////////////////////////////////////////////////
// Main File:         main
// This File:         threads
// Other Files:       main and queue
// Semester:          CS 537 Fall 2018
//
// Authors:           Pedro Henrique Koeler Goulart, Varun Sreenivasan
// Emails:            koelergoular@wisc.edu, vsreenivasan@wisc.edu
// CS Logins:         koeler-goulart, sreenivasan
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
#include <unistd.h>
#include <string.h>
#include <ctype.h>
#include <pthread.h>
#include "queue.h"
#include "threads.h"

// Variable for max size of line allowed on queue
static const int buf_char_size = 1024;

/*
 * Reader thread. Reads assigned input file and performs checks on size of
 * line, dumping the rest of the line. When end of file is reached, enqueues
 * a NULL line to make sure all processes terminate.
 *
 * Parameters: arg - queue argument
 * Return: NULL
 */
void *Reader(void * arg) {
  // Variables
  char c;
  char * buf = malloc(sizeof(char)*buf_char_size);

  if(buf == NULL) {
    fprintf(stderr, "malloc error: reader buf allocation\n");
    exit(1);
  }

  int buff_size = 0;
  int line_number = 1;
  Queue* q1 = (Queue*)arg;

  c = getchar();

  // Retrieves information from line and stores it in buf_input
  while (c != EOF) {
    // Reads line
    while (c != '\n') {
      // Checks size of queue
      if (buff_size < (buf_char_size - 1)) {
        buf[buff_size] = c;
      }

      // Gets next character
      buff_size++;
      c = getchar();

      // Checks for inline EOF
      if (c == EOF) {
        break;
      }
    }

    // Checks size of buf_input
    if (buff_size >= buf_char_size) {
      fprintf(stderr, "String on line %d exceeds max number of chars (%d)\n",
        line_number, buf_char_size);
        free(buf);
    } else {
      // Adds null character to end of string
      buf[buff_size] = '\0';

      // Enqueues string
      EnqueueString(q1, buf);
    }

    line_number++;
    buff_size = 0;
    buf = malloc(sizeof(char)*buf_char_size);

    if (buf == NULL) {
      fprintf(stderr, "malloc error: reader buf allocation\n");
      exit(1);
    }

    // Gets following character
    if (c != EOF) {
      c = getchar();
    } else {
      printf("\n"); // Inline EOF jump line for displaying output
    }
  }

  // Enqueues a NULL string to let other threads know there is no more input
  EnqueueString(q1, NULL);

  // Quits thread
  pthread_exit(NULL);

  return NULL;
}

/*
 * Munch1 thread. Gets line from queue 1 and substitutes all space characters
 * to * and checks for NULL line to terminate thread. Passes line to queue 2.
 *
 * Parameters: args - two queue arguments, queue 1 and queue 2
 * Return: NULL
 */
void *Munch1(void * args) {
  // Variables
  char* string = NULL;
  int temp_length = 0;
  queues_pack* m1 = (queues_pack*)args;
  Queue *q1 = m1->q1;
  Queue *q2 = m1->q2;

  // Dequeues, performs munch1 changes, and enqueue
  while (1) {
    string = DequeueString(q1);

    // Checks if string is NULL and ends thread
    if (string == NULL) {
      EnqueueString(q2, string);
      break;
    }

    temp_length = strlen(string);

    for (int i=0; i < temp_length; i++) {
      if (string[i] == ' ') {
        string[i] = '*';
      }
    }

    EnqueueString(q2, string);
  }

  // Quits thread
  pthread_exit(NULL);

  return NULL;
}

/*
 * Munch2 thread. Gets lines from queue 2 and turns characters to uppercase,
 * it also checks for NULL line to terminate thread. Passes line to queue 3.
 *
 * Parameters: args - two queue arguments, queue 2 and queue 3
 * Return: NULL
 */
void *Munch2(void * args) {
  // Variables
  char* string = NULL;
  int temp_length = 0;
  queues_pack* m2 = (queues_pack*)args;
  Queue *q2 = m2->q1;
  Queue *q3 = m2->q2;

  // Dequeues, performs munch2 changes, and enqueue
  while (1) {
    string = DequeueString(q2);

    // Checks if string is NULL and ends thread
    if (string == NULL) {
      EnqueueString(q3, string);
      break;
    }

    temp_length = strlen(string);

    for (int i=0; i < temp_length; i++) {
      // Checks if char is lowercase and changes it
      if (islower(string[i])) {
        string[i] = toupper(string[i]);
      }
    }

    EnqueueString(q3, string);
  }

  // Quits thread
  pthread_exit(NULL);

  return NULL;
}

/*
 * Writer thread. Gets lines from queue 3 and writes them to file. Checks for
 * NULL line to terminate thread.
 *
 * Parameters: arg - queue argument
 * Return: NULL
 */
void *Writer(void * arg) {
  // Variables
  char* string = NULL;
  Queue* q3 = (Queue*)arg;

  // Dequeues and writes string
  while (1) {
    string = DequeueString(q3);

    // Checks if string is NULL and ends thread
    if (string == NULL) {
      break;
    }

    printf("%s\n", string);
    free(string); // Frees pointer created in reader thread
  }

  // Quits thread
  pthread_exit(NULL);

  return NULL;
}
