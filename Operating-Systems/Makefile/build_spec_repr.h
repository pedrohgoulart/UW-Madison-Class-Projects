#ifndef __build_spec_repr_h__

#define __build_spec_repr_h__

#include "text_parsing.h"

typedef struct _specification_rep {
  char* target;
  char** files;
  int num_Files;
  struct stat* finfo;
  int nodeNumber;
  char** commands;
  int num_Commands;
  int needsBuild;
} S_repr;

S_repr** createRep(Lines* line_group);
void freeNode(S_repr* rep);

#endif
