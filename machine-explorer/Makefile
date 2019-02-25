CC = gcc
CFLAGS =  -Wall
PROGRAM = machine_explorer

default: $(PROGRAM)

$(PROGRAM): $(PROGRAM).o 
	$(CC) $^ -o $@

$(PROGRAM).o: $(PROGRAM).c solution.c
	$(CC) -c $(CFLAGS) -o $@ $(PROGRAM).c

clean:
	/bin/rm -f $(PROGRAM).o $(PROGRAM)
