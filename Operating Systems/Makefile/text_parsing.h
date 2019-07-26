#ifndef __text_parsing_h__

#define __text_parsing_h__

typedef struct lines_group {
  char** lines; // Array of line strings
  int size; // Number of lines (size of lines_group)
  int targets; // Number of targets
} Lines;

Lines* get_lines();
void free_lines(Lines* line_group);

#endif
