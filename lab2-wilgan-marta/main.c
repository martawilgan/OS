//
//  main.c
//  lab2-wilgan-marta
//
//  Created by marta wilgan on 2/27/13.
//  Copyright (c) 2013 nyu. All rights reserved.
//

#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

/* =================== BASIC CONSTANTS ==================== */

#define ISIZE   4        /* size of arrays for process info */

/* =================== HELPFUL STRUCTS ==================== */

/*
 * Process stores process information
 */
typedef struct {
    
    int a;
	int b;
	int c;
	int cpu_time;
	int done_time;
    int i_index;
	int io;
	int io_time;
	int left_over;
    int p_index;
	int priority;
	int q;
	int remaining_c;
	int state_time;
	int wait_time;
	int state;      /* 
                     * 0 - empty
                     * 1 - unstarted
                     * 2 - ready 
                     * 3 - running 
                     * 4 - blocked 
                     * 5 - done/terminated 
                     */
} Process;


/* ===================== DECLARATIONS ===================== */
/* GLOBALS */

static int r_index = 0;         /* index for random number file */
static int verbose = 0;         /* 1 if --verbose flag, 0 otherwise */
static int num_of_procs = 0;    /* num of processes */
FILE *random_file;              /* random numbers file */

/* HELPER ROUTINES */

void empty_char_array(char array [ISIZE][ISIZE]);
void empty_int_array(int array [ISIZE]);
void print_process(Process p);
void print_process_details(Process p);
void print_array_details(Process p[]);
void init_process_array(Process array[]);
void empty_process_array(Process array[]);
void p_add(Process p, Process array[]);
void p_empty(Process *p);
void print_process_array(Process array[]);
void decr_priorities(Process array[]);
void set_description(Process *p, int a, int b, int c, int io);
void set_i_index (Process array[]);
void set_p_index (Process array[]);
void update_all(Process p[], Process run, Process r[], Process b[]);
void update_time(Process *p);
void update_times(Process *run, Process ready[], Process blocked[]);
int p_last_index(Process array[]);
Process new_process(void);
Process new_process_with_description(int a, int b, int c, int io);
Process p_remove_at_index(int index, Process array[]);

/* FILE ROUTINES */

void get_input_and_run(int index, const char * argv[]);
int check_verbose(const char * argv[]);
int randomOS(const char * argv[], int U);
int get_random(const char * argv[]);

/* SCHEDULING ROUTINES */

void break_ties(Process ready[]);
void sort_by_arrival(Process array[]);
void sort_by_remaining(Process array[]);
void run_the_type(const char * argv[], int the_type, Process array[]);
void print_details(Process process_array[], int cycle);
void print_run_info(Process process_array[], int type, int io);


/* =================== END DECLARATIONS =================== */

int main (int argc, const char * argv[])
{
    /* 
     * check for --verbose flag 
     * if present 
     * random file is arg[3] 
     * input file is arg[2]
     * otherwise
     * random file is arg[2] 
     * input file is arg[1]
     */
     
    if (check_verbose(argv) == 1)
    {
        get_input_and_run(2, argv);
    }
    else
    {
        get_input_and_run(1, argv);
    }
    
    return 0;
    
} /* end main */

/*
 * check_verbose - check for --verbose argument
 * return 1 if found, 0 otherwise 
 */
int check_verbose(const char * argv[])
{
    if(strcmp(argv[1], "--verbose") == 0)
    {
        verbose = 1;
        return 1;
    }
    
    return 0;
    
} /* end check_verbose */

/*
 * new_process creates new process with default values
 */
Process new_process(void)
{
    Process new = 
    {    0,	/* a */
         0,	/* b */
         0,	/* c */
         0,	/* cpu_time */
         0, /* done_time */
         0, /* i_index */
         0,	/* io */
         0,	/* io_time */
         0,	/* left_over */
        -1, /* p_index */
         0,	/* priority */
        -1,	/* q */
         0,	/* remaining_c */
         0,	/* state_time */
         0,	/* wait_time */
         0	/* state */
    };

    return new;
    
} /* end new_process */

/*
 * new_process_with_description creates new process with the variable values
 */
Process new_process_with_description(int a, int b, int c, int io)
{
    Process new_process = 
    {	a,	/* a */
        b,	/* b */
        c,	/* c */
        0,	/* cpu_time */
        0, 	/* done_time */
        0,  /* i_index */
        io,	/* io */
        0,	/* io_time */
        0,	/* left_over */
       -1,  /* p_index */
        0,	/* priority */
       -1,	/* q */
        c,	/* remaining_c */
        0,	/* state_time */
        0,	/* wait_time */
        1	/* state */
    };
    
    
    return new_process;
    
} /* end new_process_with_description */

/*
 * decr_priorities - decrements the priorities
 * for all processes in ready array
 */
void decr_priorities(Process array[])
{
    for(int i = 0; i < num_of_procs; i++)
    {
        if(array[i].state != 0)
        {
            array[i].priority--;
        }
    }
    
} /* end decr_priorities */

/*
 * set_description - sets the process p with a,b,c, and io
 */
void set_description(Process *p, int a, int b, int c, int io)
{
    p->a = a;
    p->b = b;
    p->c = c;
    p->io = io;
    p->p_index = -1;
    p->remaining_c = c;
    p->state = 1; /* set to unstarted */
    
} /* end set_description */

/*
 * print_process - prints out a,b,c,io of process
 */
void print_process(Process p)
{
    printf(" (");
    printf(" %i", p.a);
    printf(" %i", p.b);
    printf(" %i", p.c);
    printf(" %i", p.io);
    printf(" ) ");
    
} /* end print_process */

/*
 * print_process_details - prints all 
 * process's variables and values
 */
void print_process_details(Process p)
{
    printf("\n a: \t\t\t %i", p.a);
    printf("\n b: \t\t\t %i", p.b);
    printf("\n c: \t\t\t %i", p.c);
    printf("\n cpu_time: \t\t %i", p.cpu_time);
    printf("\n done_time: \t %i", p.done_time);
    printf("\n i_index: \t\t %i", p.i_index);
    printf("\n io: \t\t\t %i", p.io);
    printf("\n io_time: \t\t %i", p.io_time);
    printf("\n left_over: \t %i", p.left_over);
    printf("\n p_index: \t\t %i", p.p_index);
    printf("\n priority: \t\t %i", p.priority);
    printf("\n q: \t\t\t %i", p.q);
    printf("\n remaining_c: \t %i", p.remaining_c);
    printf("\n state_time: \t %i", p.state_time);
    printf("\n wait_time: \t %i", p.wait_time);
    printf("\n state: \t\t %i", p.state);
  
} /* end print_process_details */

/*
 * print_array_details prints all variables 
 * of each process in array
 */
void print_array_details(Process p[])
{
    for (int z = 0; z < num_of_procs; z++)
    {
        printf("a: %i \t", p[z].a);
        printf("b: %i \t", p[z].b);
        printf("c: %i \t", p[z].c);
        printf("cpu_time: %i \t", p[z].cpu_time);
        printf("i_index: %i \t", p[z].i_index);
        printf("done_time: %i \t", p[z].done_time);
        printf("io: %i \t", p[z].io);
        printf("io_time: %i \t", p[z].io_time);
        printf("left_over: %i \t", p[z].left_over);
        printf("p_index: %i \t", p[z].p_index);
        printf("priority: %i \t", p[z].priority);
        printf("q: %i \t", p[z].q);
        printf("remaining_c: %i \t", p[z].remaining_c);
        printf("state_time: %i \t", p[z].state_time);
        printf("wait_time: %i \t", p[z].wait_time);
        printf("state: %i \t", p[z].state);
        printf("\n");
    }
    
} /* end print_array_details */

/* 
 * p_add - traverses through array
 * and adds process to first empty 
 * spot to simulate an arraylist
 */
void p_add(Process p, Process array[])
{
    for(int i = 0; i< num_of_procs; i++)
    {
        if(array[i].a == 0 && array[i].b == 0 &&
           array[i].c == 0 && array[i].io == 0)
        {
            array[i] = p;
            i = num_of_procs; /* exit loop */
        }
    }
    
} /* end p_add */

/*
 * p_empty - empty the process variables
 */
void p_empty(Process *p)
{
    p->a = 0;
    p->b = 0;
    p->c = 0;
    p->cpu_time = 0;
    p->done_time = 0;
    p->i_index = 0;
    p->io = 0;
    p->io_time = 0;
    p->left_over = 0;
    p->p_index = -1;
    p->priority = 0;
    p->q = -1;
    p->remaining_c = 0;
    p->state_time = 0;
    p->wait_time = 0;
    p->state = 0;
	
} /* end p_empty */

/*
 * print_process_array - prints out the processes in array
 */
void print_process_array(Process array[])
{
    for (int i = 0; i < num_of_procs; i++)
    {
        print_process(array[i]);
    }
    
} /* end print_process_array*/

/*
 * init_process_array - inits the processes in array
 */
void init_process_array(Process array[])
{
    for (int i = 0; i < num_of_procs; i++)
    {
        array[i] = new_process();
    }
    
} /* end init_process_array */

/*
 * empty_process - empties all variables
 * for each process in array
 */
void empty_process_array(Process array[])
{
    for (int i = 0; i < num_of_procs; i++)
    {
        p_empty(&array[i]);
    }
    
} /* end empty_process_array */

/*
 * p_remove_at_index - removes the process at index
 * then traverses through array and
 * decrements other processes' indexes
 * to simulate an arraylist
 */
Process p_remove_at_index(int index, Process array[])
{
    Process p = array[index]; /* removed process */
    
    /* if only one process in array */
    if (num_of_procs == 1)
    {
        p_empty(&array[index]);
    }
    
    /* for more than one process in array */
    for (int i = index; i < num_of_procs-1; i++)
    {
        if ((array[i+1].state) != 0)
        {
            /* slide down one index */
            array[i] = array[i+1];
            
            /* clear i+1 */
            p_empty(&array[i+1]);
        }
        else
        {
            /* empty last process left in array */
            p_empty(&array[i]);
        }
    }  
    
    /* if last process is being removed */
    if(index == num_of_procs-1)
    {
        /* clear last process in array */
        p_empty(&array[index]);
    }
    
    return p;
    
} /* end p_remove_at_index */

/*
 * p_last_index - find int index of the last
 * process in array that has values other than
 * default values
 */
int p_last_index (Process array[])
{
    int index = -1;
    
    for (int i = 0; i < num_of_procs; i++)
    {
        if( (array[i].state) != 0)
        {
            index++;
        }
    }
    
    return index;
    
} /* end p_last_index */

/* 
 * set_i_index - sets all i_index in array
 * based on order listed in input
 */
void set_i_index (Process array[])
{
    for (int i = 0; i < num_of_procs; i++)
    {
        array[i].i_index = i;
    }
    
} /* end set_i_index */

/* 
 * set_p_index - sets all p_index in array
 * based on current location
 */
void set_p_index (Process array[])
{
    for (int i = 0; i < num_of_procs; i++)
    {
        array[i].p_index = i;
    }
    
} /* end set_p_index */

/*
 * update_all - updates process array 
 * 
 * updates time variables for processes
 * updates all changes made during cycle
 * by processes in run, r[] and b[]
 */
void update_all(Process p[], Process run, Process r[], Process b[])
{
    /*
     * for each non-empty process in running process
     * and in ready and blocked arrays, 
     * update information in process array
     */
    for(int i = 0; i < num_of_procs; i++)
    {  
        /* if p_index matches and in running state */
        if (run.p_index == i && run.state == 3)
        {
            p[i] = run;
        }
        
        for (int j = 0; j < num_of_procs; j++)
        {    
            /* if p_index matches and in ready state */
            if(r[j].p_index == i && r[j].state == 2) 
            {
                p[i] = r[j];
            }
        
            /* if p_index matches and in blocked state */
            if(b[j].p_index == i && b[j].state == 4)
            {
                p[i] = b[j];
            }
        }    
    }
    
} /* end update_all */

/*
 * update_time updates process's time variables
 */
void update_time(Process *p)
{
    if (p->state_time != 0 && 
        p->state != 1 /* unstarted */ &&
        p->state != 5 /* terminated */)
    {
        p->state_time--;
    }
    
    
    if(p->state == 2 /* ready */)
    {
        p->wait_time++; /* 
                         * count cycles process ready
                         * and waiting */
    }
    
    if(p->state == 3 /* running */)
    {
        p->remaining_c--; /* decrement time remaining */
        p->cpu_time++;    /* count cycles process is running */
        p->q--;           /* decrement quantum */
        p->left_over--;   /* 
                           * decrement time left over 
                           * from last burst for rr
                           */
    }
    
    if(p->state == 4 /* blocked */)
    {
        p->io_time++; /* count cycles process is blocked */
    }
    
} /* end update_time */

/*
 * update_times - updates time variables for 
 * running, ready, and blocked processes
 */
void update_times(Process *run, Process ready[], Process blocked[])
{
    int r = p_last_index(ready);
    int b = p_last_index(blocked);
    
    update_time(run);
    
    for(int i =0; i <= r; i++)
    {
        update_time(&ready[i]);
    }
    
    for(int i =0; i <= b; i++)
    {
        update_time(&blocked[i]);
    }
    
} /* end update time */

/*
 * empty_char_array - loop through char array of ISIZE 
 * and empty each char
 */

void empty_char_array(char array [ISIZE][ISIZE])
{
    int x,y;
    for(x = 0; x <ISIZE; x++)
    {
        for(y=0; y<ISIZE;y++)
        {
            array[x][y] = '\0';
        }
    }
    return;
    
} /* end empty_char_array */

/*
 * empty_int_array - loop through int array of ISIZE 
 * and clear each int
 */

void empty_int_array(int array [ISIZE])
{
    for(int x = 0; x <ISIZE; x++)
    {
            array[x] = 0;

    }
    return;
    
} /* end empty_int_array */


/*
 * get_input_and_run takes info from input text file 
 * and runs the scheduling algorithm using
 * run_the_type
 */

void get_input_and_run(int index, const char * argv[])
{
    char info[ISIZE][ISIZE]; /* a,b,c,io as char for each process */
    int int_info[ISIZE];     /* a,b,c,io as int for each process */
    int stat[ISIZE]; /* 
                      * keeps track of status for a,b,c,io 
                      * of each process
                      * 0 - clear
                      * 1 - set
                      */
    
    /* assume argv[index] is the filename to open */
    FILE *file = fopen( argv[index], "r" );
    
    /* check for failure i.e. fopen returns 0 */
    if(file == 0)
    {
        printf("Could not open input file\n");
    }
    else 
    {
        int x; /* index used for current character being read */
        empty_char_array(info); /* initally empty array info */
        empty_int_array(stat);  /* initially empty array is_used */
        
        /* read characters one at a time 
         * stop at end of file
         */
        while((x = fgetc(file)) != EOF && num_of_procs == 0)
        {
           
            /* first number indicates number of processes */ 
            if (isdigit(x) != 0)
            {
                num_of_procs = x - '0'; /* convert x to int */
            }
        } 
        
        /* 
         * since # of processes' now known
         * declare and init process array
         */
        Process process_array[num_of_procs];
        init_process_array(process_array);
        
        int n = 0;
        
        while((x = fgetc(file)) != EOF)
        {    
            /*
             * find (a,b,c,io) for each process 
             */
   
            for(int i = 0; i <ISIZE; i++)
            {
                if (isdigit(x) != 0 && info[i][0] == '\0')
                {
                    
                    /* first digit */
                    info[i][0] = (char)x;

                    /* account for possibility of more than one digit */
                    int y = 1;
                    while ((x = fgetc(file)) != EOF && isdigit(x)!= 0)
                    {
                        info[i][y] = (char)x;
                        y++;
                            
                    }
                    
                    /* convert to int */
                    int_info[i] =  atoi(info[i]);
                    stat[i] = 1; /* set */
                    x = fgetc(file); /* update x */
                    
                } /* end if */                
            } /* end for */
                
            /*
             * if all stats are set
             * give the info to the process struct
             */
        
            if (stat[0] && stat[1] && stat[2] && stat[3])
            {
                set_description(&process_array[n], 
                                int_info[0], 
                                int_info[1], 
                                int_info[2], 
                                int_info[3]);
                    
                /* set all stats to clear */
                stat[0] = stat[1] = stat[2] = stat[3] = 0;
                    
                empty_char_array(info); /* empty info for next process */
                n++; /* update process number */

            }
        } /* end while */
        
        fclose( file );
        
        /* print the organized input */
        printf("\nThe original input was : \t %i", num_of_procs );
        print_process_array(process_array);
        printf("\n");
        
        /* set indicies and sort */
        set_i_index(process_array);
        sort_by_arrival(process_array);
        set_p_index(process_array);
        
        /* print the sorted input */
        printf("\nThe sorted input is : \t \t %i", num_of_procs );
        print_process_array(process_array);
        printf("\n\n");
        
        /* 
         * open random file and
         * run the scheduling algorithm of type
         * 0 - FCFS
         * 1 - RR with quantum of 2
         * 2 - uniprogrammed
         * 3 - SJF
         */
        
        for(int t = 0; t <= 3; t++)
        {
            /* 
             * if --verbose flag is present 
             * file is argv[3] else argv[2] 
             */
            if (verbose)
            {
                random_file = fopen(argv[3], "r");
            }
            else
            {
                random_file = fopen(argv[2], "r");
            }
            
            run_the_type(argv, t,process_array);
            fclose( random_file );
        }
        
    } /* end if/else */
    
} /* end get_input_and_run */ 

/*
 * randomOS - reads a random non-negative integer number 
 * from file using get_random 
 * and returns 1 + (X mod U)
 */
int randomOS(const char * argv[], int U)
{
    int X = get_random(argv);
    return 1 + (X % U);
    
} /* end randomOS */

/*
 * get_random - reads from the random numbers file
 * and returns the next random number as an int
 */
int get_random(const char * argv[])
{
    /* 
     * random_str stores number as char array 
     * of size 10, since no random number is 
     * longer than 10 digits in file
     */
    char random_str[] = {'\0','\0','\0','\0','\0',
                         '\0','\0','\0','\0','\0'};
    int s_index = 0;    /* index within random_str */
    int random_int = 0; /* random_str as int */
    int found = 0;      /* 1 if random_int is found, 0 otherwise */
    
    /* check for failure i.e. fopen returns 0 */
    if(random_file == 0)
    {
        printf("Could not open random file\n");
    }
    else 
    {
        int x = r_index; /* index where to start reading */
        
        while(found == 0)
        {
            /*
             * if x is digit and found == 0 
             * get the random number
             */
            
            if ((x = fgetc(random_file)) != EOF && isdigit(x) != 0)
            {
                
                /* first digit */
                random_str[s_index] = (char)x;
                s_index++; /* increment s_index */
                r_index++; /* increment r_index */
                
                /* account for possibility of more than one digit */
                while ((x = fgetc(random_file)) != EOF && isdigit(x)!= 0)
                {
                    random_str[s_index] = (char)x;
                    s_index++; /* increment s_index */
                    r_index++; /* increment r_index */
                    
                }
                
                /* convert to int */
                random_int =  atoi(random_str);
                
                /* clear random_str */
                for(int i = 0; i < 10; i++)
                {
                    random_str[i] = '\0';
                }
                
                r_index--; /* decr r_index for next call */
                found = 1; /* set found to true */
            }
            else
            {
               r_index++; /* increment r_index */
            } /* end if/else */
        } /* end while */
    } /* end if/else */   
       
    return random_int;
    
} /* end get_random */

/*
 * break_ties - makes sure processes in ready
 * are in correct order
 *
 * first checks priority
 * then arrival time
 * then order listed in input
 */
void break_ties(Process ready[])
{
    int first = p_last_index(ready) - 1;
    int second = p_last_index(ready);
    
    if (ready[first].priority == ready[second].priority &&
        ready[first].a >= ready[second].a)
    {
        Process removed_1 = p_remove_at_index(first, ready);
        Process removed_2 = p_remove_at_index(first, ready);
        
        if(removed_1.a > removed_2.a)
        {
            p_add(removed_2, ready);
            p_add(removed_1, ready);
        }
        if(removed_1.a == removed_2.a)
        {
            if(removed_1.i_index < removed_2.i_index)
            {
                p_add(removed_1, ready);
                p_add(removed_2, ready);
            }
            else if (removed_1.i_index > removed_2.i_index)
            {
                p_add(removed_2, ready);
                p_add(removed_1, ready);
            }
        }
        
    } /* otherwise input should be correct do nothing */ 
    
} /* end break_ties */

/*
 * sort_by_arrival - sort the process array 
 * in order of a (time created)
 */  
void sort_by_arrival(Process array[])
{
    for(int i = num_of_procs - 1; i > 0; i--)
    {
        if (array[i-1].a > array[i].a)
        {
            Process temp = array[i];
            array[i] = array[i-1];
            array[i-1] = temp;
        }
    }
    
} /* end sort_by_arrival*/


/*
 * sort by remaining - sort the process array
 * in order of time remaining (remaining_c)
 */
void sort_by_remaining(Process array[])
{
    int last = p_last_index(array);
    
    for(int i = last; i > 0; i--)
    {
        if (array[i-1].remaining_c > array[i].remaining_c)
        {
            Process temp = array[i];
            array[i] = array[i-1];
            array[i-1] = temp;
        }
    }

} /* end sort_by_remaining */

/*
 * run_the_type - run scheduling algorithm of type
 * type = 0 - FCFS
 * type = 1 - RR q = 2
 * type = 2 - uniprogrammed
 * type = 3 - SJF
 */
void run_the_type(const char * argv[], int the_type, Process array[])
{
    int type = the_type;    /* type of algorithm */
    int cycle = 0;          /* current cycle */
    int num_of_done = 0;    /* num of processes terminated */
    int io = 0;             /* number of cycles a process is blocked */
    int uni_priority = 0;   /* priority of next uni process created */
    int last_b = 0;     /* index of last non-default 
                         * process in blocked array 
                         */
    int last_r = -1;    /* index of last non-default 
                         * process in ready array 
                         */
    r_index = 0;        /* set to zero */
    
    /* process array */
    Process process_array[num_of_procs];
    
    /* copy array processes */
    for(int i = 0; i < num_of_procs; i++)
    {
        process_array[i] = array[i];
    }
    
    Process running;                /* currently running process */
    Process blocked[num_of_procs];  /* currently blocked processes */
    Process ready[num_of_procs];    /* currently ready processes */
    
    /* initialize processes with default values */
    running = new_process();
    init_process_array(blocked);
    init_process_array(ready);
    
    /* print out the type */
    switch(type)
    {
        case 0:
            printf("\n =========> The algorithm used is First Come First Serve <==========");
            break;
        case 1:
            printf("\n =========> The algorithm used is RR with quantum 2 <===============");
            break;
        case 2:
            printf("\n =========> The algorithm used is Uniprogrammed <===================");
            break;
        case 3:
            printf("\n =========> The algorithm used is Shortest Job First <==============");
            break;            
    }
    
    /* if --verbose flag present print */    
    if (verbose)
    {    
        printf("\n\nThis detailed printout gives the state "); 
        printf("and remaining burst for each process\n");
    }    
    
    /*
     *    Run the algorithm in order:
     *
	 *    1. Do Blocked Processes
	 *    2. Do Running Processes
	 *    3. Do Created Processes
	 *    4. Do Ready Processes
     *
     *    finished when all processes terminate
	 */
    
    while (num_of_done < num_of_procs)
    {    
        
        /* 
         * if --verbose flag present 
         * print details for each cycle 
         */
        if (verbose)
        {    
            printf("\n");
            print_details(process_array, cycle);
        }    
        
        if(cycle > 0)
        {
            update_times(&running, ready, blocked);
        }
        
        /* DO BLOCKED */
        
        /* last_b = 0 when no processes are blocked */
        last_b = p_last_index(blocked);
        
        int num_unblocked = 0; /* 
                                * number of processes
                                * unblocked this cycle 
                                */
        
        /*
         * check for processes to unblock this cycle
         *
         * if more than one make sure they are inserted
         * correctly into the ready array
         * 
         */
        
        for(int i = 0; i <= p_last_index(blocked); i++)
        {
            if(blocked[i].state_time == 0)
            {
                blocked[i].state = 2; /* set to ready */
               
                /*
                 * make any changes to priority
                 * 
                 * fcfs & rr set to cycle unblocked
                 * uni keep the same
                 * sjf set to total time remaining i.e. c - cycles left
                 */
                
                if (type == 0 || type == 1 /* fcfs rr */)
                {    
                    blocked[i].priority = cycle;
                } 
                if (type == 3 /* sjf */)
                {
                    blocked[i].priority = blocked[i].remaining_c;
                }
                
                /* find index of last inserted into ready */
                last_r = p_last_index(ready);
                
                /* remove from blocked, add to ready */
                Process removed = p_remove_at_index(i, blocked);
                p_add(removed, ready);
                
                if (type == 3 /* sjf */)
                {
                    sort_by_remaining(ready);
                }
                
                if(num_unblocked > 0)
                {
                    break_ties(ready);
                }
                
                /* update to account for unblocked process */
                num_unblocked++;
                i--;
                
            } /* end if blocked[i].state_time == 0 */
            
        } /* end for */
        
                
        /* DO RUNNING */
        
        /* remove process that has run for alloted time */
        if(running.state == 3 && 
           (running.state_time == 0 || running.remaining_c == 0))
        {
  
            /*
             * if no more CPU time needed to complete terminate
             * otherwise block the process and
             * set time to be blocked by random number
             * except if round robbin quantum = 0 then prempt process
			 */
            if(running.remaining_c <= 0)
            {
                running.state = 5; /* done/terminated */
                running.state_time = 0;
                num_of_done++;
                running.done_time = cycle;
                
                /* 
                 * if uni decrement priorities 
                 * for all ready processes 
                 */
                if (type == 2 /* uni */)
                {    
                    decr_priorities(ready);
                }    
                
                /* update and save info for terminated process */
                process_array[running.p_index] = running;
                
            }
            else
            {
                if (type == 1 /* rr */ && running.q == 0)
                {
                    running.state = 2; /* ready */
                    running.state_time = 0;
                    running.priority = cycle;
                    p_add(running, ready);
                    break_ties(ready);
                }
                else
                {   
                    running.state = 4; /* blocked */
                    running.state_time = randomOS(argv, running.io);
                    p_add(running, blocked);
                    last_b = p_last_index(blocked);
                }    
            }
            
            p_empty(&running); /* set to defaults */
            
        }
        
        /* DO CREATED */
        
        /* find processes to be created this cycle */
        for(int i = 0; i < num_of_procs; i++)
        {
            if(cycle == process_array[i].a)
            {
                process_array[i].state = 2; /* ready */
                //process_array[i].priority = priority;
            
                /*
                 * make sure processes have correct priority
                 * fcfs & rr set to cycle
                 * uni - make sure only one can run to completion at a time
                 * sjf - set to total time remaining
                 */
                
                if(type == 0 || type == 1 /* fcfs || rr */)
                {
                    process_array[i].priority = cycle;
                }
                if(type == 2 /* uni */)
                {
                    process_array[i].priority = uni_priority;
                    uni_priority++;
                }
                if(type == 3 /* sjf */)
                {
                    process_array[i].priority = process_array[i].remaining_c;
                }
                
                p_add(process_array[i], ready); /* add to ready */
                
                if(type == 3 /* sjf */)
                {
                    sort_by_remaining(ready);
                }
            } /* end if */
        } /* end for */
  
        
        /* DO READY */
        
        last_r = p_last_index(ready);
        
        /* check if no process is currently running */
        if ( (running.state != 3) && (last_r >= 0) )
        {
            /* 
             * find which process is to run next from ready array
             *
             * fcfs - make sure process added to ready first runs first
             * rr - same as fcfs
             * uni - one process must terminate before another can run
             * sjf - run process with shortest execution time (c) first
             */
            
            if ( (type == 0 || type == 3 /* fcfs or sjf */) 
                && ready[0].state == 2)
            {
                running = p_remove_at_index(0,ready);
                running.state = 3; /* running */
                running.state_time = randomOS(argv, running.b);
            }
            
            if(type == 1 /* rr */ && ready[0].state == 2)
            {
                running = p_remove_at_index(0,ready);
                running.state = 3; /* running */
                
                /* 
                 * if time left over from last burst 
                 * set state_time to left_over
                 * else get new burst
                 */
                if( running.left_over > 0 )
                {
                    running.state_time = running.left_over;
                }
                else
                {
                    running.state_time = randomOS(argv, running.b);
                }
                
                /* set quantum for rr for state_time > 2 */
                if(running.state_time > 2)
                {
                    running.left_over = running.state_time;
                    running.state_time = 2;
                    running.q = 2;
                }
            }
            
            if (type == 2 /* uni */)
            {
                /* find process with priority 0 */
                for(int i = 0; i <=last_r; i++)
                {
                    
                    if(ready[i].state == 2 && ready[i].priority == 0)
                    {
                        running = p_remove_at_index(i, ready);
                        running.state = 3; /* running */
                        running.state_time = randomOS(argv, running.b);
                        i = last_r+1;
                    }
                }
            }
        } /* end if (running.state != 3) && (last_r >= 0) */
        
        /* update process_array */
        update_all(process_array, running, ready, blocked);
        
        /* count cycles at least one process is blocked */
        if (p_last_index(blocked) >= 0)
        {
            io++;
        }
        
        cycle++; /* update cycle */
        
    } /* end while */    
    
    print_run_info(process_array, type, io);
    
} /* end run_the_type */

/*
 * print_run_info prints the information about
 * the scheduling algorithm's run
 */
void print_run_info(Process process_array[], int type, int io)
{
    printf("\n");
    
    /* print run info for each process */
    for(int i = 0; i < num_of_procs; i++)
    {
        printf("\n\nProcess: %i", i);
        printf("\n\t(A,B,C,IO) = ");
        printf("(%i,", process_array[i].a);
        printf("%i,", process_array[i].b);
        printf("%i,", process_array[i].c);
        printf("%i)", process_array[i].io);
        printf("\n\tFinishing time: %i ", process_array[i].done_time);
        printf("\n\tTurnaround time: %i ", 
               process_array[i].done_time - process_array[i].a);
        printf("\n\tI/O time: %i ", process_array[i].io_time);
        printf("\n\tWaiting time: %i ", process_array[i].wait_time);

    }
    
    /* get the summary data */
    int finishing = 0;
    int util_CPU = 0;
    int turn_around = 0;
    int wait_time = 0;
    
    for (int i = 0; i< num_of_procs; i++){
        
        /* get process's finishing time */
        int temp = process_array[i].done_time;
        
        /* save the largest finishing time */
        if (temp > finishing) 
        {
            finishing = temp;
        }
        
        /* find amount of time some process is running */
        util_CPU += process_array[i].cpu_time;
        
        /* calculate process turn around time */
        turn_around += process_array[i].done_time - process_array[i].a;
        
        /* find amount of time process is ready and waiting */
        wait_time += process_array[i].wait_time;
    }
    
    /* perform appropriate calculations */
    double d_CPU = 0;
    double d_IO = 0;
    double d_throughput = 0;
    double d_avg_turn_around = 0;
    double d_avg_wait = 0;
    
    
    d_CPU = ((double)(util_CPU)/finishing);
    d_IO = ((double)io/finishing);
    d_avg_turn_around = ((double)turn_around/num_of_procs);
    d_avg_wait = ((double)wait_time/num_of_procs);
    
    if (type == 2 /* uni */) {
        d_throughput = (100/ ((double)(turn_around - wait_time)/num_of_procs)) ;
    }
    else {
        d_throughput = (100/ ((double)(finishing)/num_of_procs)) ;			
    }
    
    /* print the summary data */
    printf("\n\nSummary Data:");
    printf("\n\tFinishing time:\t\t\t %i", finishing);
    printf("\n\tCPU Utilization:\t\t %f", d_CPU);
    printf("\n\tI/O Utilization:\t\t %f", d_IO);
    printf("\n\tThroughput:\t\t\t %f", d_throughput); 
        printf(" processes per hundred cycles");
    printf("\n\tAverage turnaround time:\t %f", d_avg_turn_around);
    printf("\n\tAverage waiting time:\t\t %f", d_avg_wait);
    printf("\n\n");
    
} /* end print_run_info */

/* 
 * print_details prints the details for each cycle
 */

void print_details(Process process_array[], int cycle)
{
    
    printf("\nBefore cycle \t %i", cycle);
    
    for(int i=0; i < num_of_procs; i++)
    {
        /* print the state */
        switch (process_array[i].state)
        {
            case 0:
                printf("\t no process here");
                break;
            case 1:
                printf("\t unstarted");
                break; 
            case 2:
                printf("\t ready");
                break; 
            case 3:
                printf("\t running");
                break; 
            case 4:
                printf("\t blocked");
                break;
            case 5:
                printf("\t terminated");
                break;    
        } /* end switch*/ 
        
        /* print time left in state */
        printf("  %i", process_array[i].state_time);
        
    } /* end for */
    
} /* end print_details */




