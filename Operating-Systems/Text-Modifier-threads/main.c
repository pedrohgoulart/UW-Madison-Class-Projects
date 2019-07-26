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
#include "queue.h"
#include "threads.h"

// Variable to control max queue size
static const int queue_size = 11;

/*
 * Main caller method for program.
 *
 * Return: 0 upon successful completion
 */
int main() {
  // Creates queues
  Queue* q1 = CreateStringQueue(queue_size);
  Queue* q2 = CreateStringQueue(queue_size);
  Queue* q3 = CreateStringQueue(queue_size);

  // Creates pthreads
  pthread_t reader_thr, munch1_thr, munch2_thr, writer_thr;

  // Packs queues together for use in Munch threads
  queues_pack m1 = {q1, q2};
  queues_pack m2 = {q2, q3};

  // Creates the threads
  if (pthread_create(&reader_thr, NULL, Reader,q1)) {
    fprintf(stderr,"pthread_create error: reader thread failed\n");
    exit(1);
  }
  if (pthread_create(&munch1_thr, NULL, Munch1,&m1)) {
    fprintf(stderr,"pthread_create error: munch1 thread failed\n");
    exit(1);
  }
  if (pthread_create(&munch2_thr, NULL, Munch2,&m2)) {
    fprintf(stderr,"pthread_create error: munch2 thread failed\n");
    exit(1);
  }
  if (pthread_create(&writer_thr, NULL, Writer,q3)) {
    fprintf(stderr,"pthread_create error: writer thread failed\n");
    exit(1);
  }

  // Waits for threads to complete
  if (pthread_join(reader_thr, NULL)) {
    fprintf(stderr,"pthread_join error: reader thread failed\n");
    exit(1);
  }
  if (pthread_join(munch1_thr, NULL)) {
    fprintf(stderr,"pthread_join error: munch1 thread failed\n");
    exit(1);
  }
  if (pthread_join(munch2_thr, NULL)) {
    fprintf(stderr,"pthread_join error: munch2 thread failed\n");
    exit(1);
  }
  if (pthread_join(writer_thr, NULL)) {
    fprintf(stderr,"pthread_join error: writer thread failed\n");
    exit(1);
  }

  // Prints queue statuse
  fprintf(stderr,"----- Queue Status -----\n");
  fprintf(stderr,"Queue 1: ");
  PrintQueueStats(q1);

  fprintf(stderr,"Queue 2: ");
  PrintQueueStats(q2);

  fprintf(stderr,"Queue 3: ");
  PrintQueueStats(q3);

  // Frees arrays and queues
  free(q1->array);
  free(q1);

  free(q2->array);
  free(q2);

  free(q3->array);
  free(q3);

  return 0;
}
