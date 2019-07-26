#ifndef __threads_h__

#define __threads_h__

typedef struct _arguments {
  Queue* q1;
  Queue* q2;
}queues_pack;

void * Reader(void* arg);
void * Munch1(void* args);
void * Munch2(void* args);
void * Writer(void* arg);

#endif


