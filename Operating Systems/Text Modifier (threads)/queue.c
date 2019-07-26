//////////////////////////////////////////////////////////////////////////////
// Main File:         main
// This File:         main
// Other Files:       queue and threads
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
#include <pthread.h>
#include <sys/types.h>
#include "queue.h"

/*
 * Function to create Queues
 * args - size of the queue
 */
Queue *CreateStringQueue(int size) {
  // Initialize fields
  Queue* q = malloc(sizeof(Queue));

  //check for successful allocation
  if(q == NULL) {
    fprintf(stderr, "malloc error: queue allocation\n");
    exit(1);
  }

  q->array = malloc(size * sizeof(char*));

  if(q->array == NULL) {
    fprintf(stderr, "malloc error: queue array allocation\n");
    exit(1);
  }
  q->size = size;
  q->first = 0;
  q->last = 0;
  q->ec = 0; // Enqueue Count
  q->dc = 0; // Dequeue Count
  q->ebc = 0; // Enqueue Block Count
  q->dbc = 0; // Dequeue Block Count

  // Initialize pthreads and exit if initialization not successful
  if(pthread_cond_init(&(q->full), NULL)) {
    fprintf(stderr, "pthread_cond_init full error: CreateStringQueue\n");
    exit(1);
  }

  if(pthread_cond_init(&(q->empty), NULL)) {
    fprintf(stderr, "pthread_cond_init empty error: CreateStringQueue\n");
    exit(1);
  }

  if(pthread_mutex_init(&(q->lock), NULL)) {
    fprintf(stderr, "pthread_mutex_init error: CreateStringQueue\n");
    exit(1);
  }

  return q;
}

/*
 * function to enqueue string
 * args
 *   1:  Queue in which element is enqueued
 *   2:  String to enqueue
 *
 */
void EnqueueString(Queue *q, char *string) {
  // Lock
  if(pthread_mutex_lock(&(q->lock))) {
     fprintf(stderr,"pthread_mutex_lock error: EnqueueString\n");
     exit(1);
  }

  // Check if queue is full, if so add thread to waiting queue
  while (((q->last + 1)%(q->size)) == q->first) {
    q->ebc++;
    if (pthread_cond_wait(&(q->full), &(q->lock))) {
      fprintf(stderr,"pthread_cond_wait error: EnqueueString\n");
      exit(1);
    }
  }

  // Enqueue (increments counter if array not NULL)
  if (string != NULL) {
    q->ec++;
  }
  q->array[q->last] = string;
  q->last = (q->last+1) % (q->size);

  // Notifies waiting threads
  if(pthread_cond_signal(&(q->empty))) {
    fprintf(stderr,"pthread_cond_signal error: EnqueueString\n");
    exit(1);
  }

  // Unlock
  if(pthread_mutex_unlock(&(q->lock))) {
    fprintf(stderr,"pthread_mutex_unlock error: EnqueueString\n");
    exit(1);
  }
}

/*
 * function to dequeue string
 * args: queue from which element is removed
 */
char * DequeueString(Queue *q) {
  // Lock
  if(pthread_mutex_lock(&(q->lock))) {
     fprintf(stderr,"pthread_mutex_lock error: DequeueString\n");
     exit(1);
  }


  // Check if queue is empty and add to waiting queue if so
  while (q->last == q->first) {
    q->dbc++;
    if (pthread_cond_wait(&(q->empty), &(q->lock))) {
      fprintf(stderr,"pthread_cond_wait error: DequeueString\n");
      exit(1);
    }
  }

  // Dequeue (increments counter if array not NULL)
  if (q->array[q->first] != NULL) {
    q->dc++;
  }
  char* ret = q->array[q->first];
  q->first = (q->first+1)% q->size;

  // Notifies waiting threads
  if(pthread_cond_signal(&(q->full))) {
    fprintf(stderr,"pthread_cond_signal error: DequeueString\n");
    exit(1);
  }


  // Unlock
  if(pthread_mutex_unlock(&(q->lock))) {
    fprintf(stderr,"pthread_mutex_unlock error: DequeueString\n");
    exit(1);
  }

  return ret;
}

/*
 * function to print queue statistics
 * args: queue for which stats are to be displayed
 */
void PrintQueueStats(Queue *q) {
  int ec = q->ec;
  int dc = q->dc;
  int ebc = q-> ebc;
  int dbc = q->dbc;

  fprintf(stderr,"Enqueue: %d | Dequeue: %d | Enqueue Block: %d | Dequeue Block: %d \n",
    ec, dc, ebc, dbc);
}
