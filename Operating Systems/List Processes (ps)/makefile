537ps: stats.o processList.o options.o
	gcc -Wall -Wextra -o 537ps stats.o processList.o options.o

options.o: options.c processList.h stats.h
	gcc -Wall -Wextra -c options.c

processList.o: processList.c processList.h
	gcc -Wall -Wextra -c processList.c

stats.o: stats.c stats.h
	gcc -Wall -Wextra -c stats.c

clean:
	rm -f 537ps *.o
