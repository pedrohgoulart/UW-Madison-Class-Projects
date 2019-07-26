#
# Sample makefile updated to include the commands to run the Clang Static Analyzer
#
#    scan-build will "make" your program and run CSA at the same time.
#
#    scan-view will do a scan-build (if necessary) and then open a web browser
#      window to review the results.
#

prodcomm:	queue.o threads.o main.o
	gcc -pthread -lpthread -Wall -Wextra -o prodcomm queue.o threads.o main.o -pthread

main.o:	main.c threads.h queue.h
	gcc -pthread -lpthread -Wall -Wextra -c main.c

threads.o:	threads.c threads.h
	gcc -pthread -lpthread -Wall -Wextra -c threads.c

queue.o:	queue.c queue.h
	gcc -pthread -lpthread -Wall -Wextra -c queue.c

clean:
	rm -f prodcomm *.o
	rm -rf scan-build-out

#
# Run the Clang Static Analyzer
#
scan-build: clean
	scan-build -o scan-build-out make

#
# View the one scan available using firefox
#
scan-view: scan-build
	firefox -new-window scan-build-out/*/index.html
