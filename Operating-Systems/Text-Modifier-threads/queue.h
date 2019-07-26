#ifndef __queue_h__

#define __queue_h__

typedef struct _queue {
   char** array;
   int size;
   int first;
   int last;
   int ec;
   int dc;
   int ebc;
   int dbc;
   pthread_cond_t full;
   pthread_cond_t empty;
   pthread_mutex_t lock;
}Queue;

Queue *CreateStringQueue(int size);
void EnqueueString(Queue *q, char *string);
char * DequeueString(Queue *q);
void PrintQueueStats(Queue *q);

#endif


