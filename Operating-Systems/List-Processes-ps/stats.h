#ifndef __stats_h__

#define __stats_h__

int fileSize(char *dir);
int parseStatm(char* dir);
char* parseStat(char option, char* dir);
char* parseCMDLine(char * dir);

#endif


