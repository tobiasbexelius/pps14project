#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

#include "hardwareAPI.h"

#define MAX_NUMBER_OF_ELEVATORS (5)
#define MAX_TOP_FLOOR (6)

void * ElevatorController(void *);
void * Master(void *);

int main(int argc, char **argv)
{
    /* Declare variables. */
    char * hostname;
    int port;

    /* Parse command line parameters. */
    if (argc != 3)
    {
        fprintf(stderr, "Usage: %s host-name port\n", argv[0]);
        fflush(stderr);
        exit(-1);
    }
    hostname = argv[1];
    if ((port = atoi(argv[2])) <= 0)
    {
        fprintf(stderr, "Bad port number: %s\n", argv[2]);
        fflush(stderr);
        exit(-1);
    }

    /* Connect to "hardware". */
    initHW(hostname, port);

    /* Create threads. */
    pthread_t elevator_controller_thread, master_thread;
    if (pthread_create(&elevator_controller_thread, NULL, ElevatorController, (void *) 0) != 0)
    {
        perror("pthread_create");
        exit(-1);
    }
    if (pthread_create(&master_thread, NULL, Master, (void *) 0) != 0)
    {
        perror("pthread_create");
        exit(-1);
    }
    (void) pthread_join(elevator_controller_thread, NULL);
    (void) pthread_join(master_thread, NULL);

    return 0;
}

void * ElevatorController(void *p)
{
    while(1)
    {
        sleep(10);
    }

    terminate();
}

void * Master(void *p)
{
    EventType e;
    EventDesc ed;

    while(1)
    {
        e = waitForEvent(&ed);

        switch (e)
        {
        case FloorButton:
            fprintf(stdout, "floor button: floor %d, type %d\n",
                ed.fbp.floor, (int) ed.fbp.type);
            fflush(stdout);
            break;

        case CabinButton:
            fprintf(stdout, "cabin button: cabin %d, floor %d\n",
                ed.cbp.cabin, ed.cbp.floor);
            fflush(stdout);
            break;

        case Position:
            fprintf(stdout, "cabin position: cabin %d, position %f\n",
                ed.cp.cabin, ed.cp.position);
            fflush(stdout);
            break;

        case Speed:
            fprintf(stdout, "speed: %f\n", ed.s.speed);
            fflush(stdout);
            break;

        case Error:
            fprintf(stdout, "error: \"%s\"\n", ed.e.str);
            fflush(stdout);
            break;
        }
    }
}
