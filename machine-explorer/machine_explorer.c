#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>

#define MAX_STRING_SIZE 256

/** Prototypes **/
void print_kernel_release();                                    
void print_num_cores();                                         
void print_ram_capacity();                                

/* Simple function to get a string input */
void get_string_input(char *msg, char **string) {
  char input[MAX_STRING_SIZE];

  while(1) {
    fprintf(stdout, "%s ", msg); 
    char *return_value = fgets(input, MAX_STRING_SIZE, stdin);

    // On EOF (i.e., ^D) exit
    if (return_value == NULL) {
      printf("\n");
      exit(0);
    }

    // Remove trailing newline
    char *newline;
    if ((newline = strchr(input, '\n')) != NULL) {
      *newline = '\0';
    }

    // Break out if input is non-empty
    if (strlen(input) > 0) {
      break;
    }
  }
  *string = strdup(input);
  return;
}

/* Simple function to get a Y/N input */
unsigned char get_onedigit_input(char *msg, int min, int max) {


  if ((min < 0) || (max > 9)) {
    fprintf(stderr,"Fatal error: cannot ask for multi-digit numerical input\n");
    exit(1);
  }

  while(1) {
    fprintf(stdout, "%s ", msg);  
    // This is a non-portable way of avoiding the need for typing ENTER
    system ("/bin/stty raw");
    int return_value = fgetc(stdin);
    // Erase the input entered on screen
    fprintf(stdout,"\r  ");


    // This is non-portable and ugly (i.e., might change the terminal's behavior
    // after this program completes..)
    system ("/bin/stty cooked");

    printf("\n");

    unsigned char entered_char = (unsigned char) return_value;

    if (entered_char == 13)  // CARRIAGE RETURN
      continue;

    if (entered_char == 4)  // EOT
      exit(0);

    entered_char -= '0'; 


    // Out of range input
    if ((entered_char < min) || (entered_char > max))
      return 0;

    return entered_char;
  }
}

/* Simple function to print the choices */
void print_list_of_options() {
  fprintf(stdout, "\n");
  fprintf(stdout, "  1. See the kernel release\n");   
  fprintf(stdout, "  2. See the number of cores\n"); 
  fprintf(stdout, "  3. See the RAM capacity\n");  
  fprintf(stdout, "  4. Exit\n");
  fprintf(stdout, "\n");
}

/* Main function */
int main(int argc, char **argv) {

  // Main loop
  while (1) {   
 
    print_list_of_options();

    int input = get_onedigit_input("", 1, 5);

    switch(input) {
      case 1: {
        print_kernel_release();
	break;
      }
      case 2: {
	print_num_cores();
        break;
      }
      case 3: {
	print_ram_capacity(); 
        break;
      }
      case 4: {
        exit(0);
        break;
      }
    }
  }
}

#include "solution.c"
