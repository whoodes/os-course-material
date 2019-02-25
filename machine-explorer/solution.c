#include <sys/wait.h>
#include <stdlib.h>

#define BUFFER_SIZE 1024

/*
 * print_kernel_release()
 */
void print_kernel_release() {
    pid_t pid = fork();
    if (pid < 0) {
        printf("ERROR: The process could not be forked!\n");
        exit(1);
    }

    if (!pid) {
        char *args[3] = {"/bin/uname", "-r", NULL};
        execvp(args[0], args);
        exit(0);
    } else {
        pid = wait(NULL);
    }
}

/*
 * print_num_cores()
 */
void print_num_cores() {
    FILE *fp;
    char buffer[BUFFER_SIZE];
    fp = popen("cat /proc/cpuinfo | grep 'processor' | wc -l", "r");
    if (fp) {
        while (fgets(buffer, BUFFER_SIZE, fp)) {
            printf("%s", buffer);
        }
    } else {
        printf("ERROR: Piping failed!\n");
        exit(1);
    }
    pclose(fp);
}

/*
 * print_ram_capacity()
 */
void print_ram_capacity() {
    FILE *fp;
    char buffer[BUFFER_SIZE];
    fp = popen("cat /proc/meminfo | grep 'MemTotal' | sed 's/[a-z|A-Z]*://' | sed 's/^ *//'", "r");
    if (fp) {
        while (fgets(buffer, BUFFER_SIZE, fp)) {
            printf("%s", buffer);
        }
    } else {
        printf("ERROR: Piping failed!");
        exit(1);
    }
    pclose(fp);
}
