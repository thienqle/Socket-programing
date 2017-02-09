#ifdef WIN32
#include <winsock2.h>
#include <windows.h>
#include <unistd.h>
#include <stdlib.h>
#include <ws2tcpip.h>
/*This does not work on windows cygwin*/
/*Run program on Linux*/
#else
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#endif
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>
#include <pthread.h>

#define MINIMUM  1025
#define MAXIMUM  65355

/* Reference to http://www.linuxquestions.org */
#ifdef _WIN32
   const char* OSname = "Windows\n";
#elif _WIN64
   const char* OSname = "Windows\n";
#elif __unix || __unix__
   const char* OSname = "Unix\n";
#elif __APPLE__ || __MACH__
   const char* OSname = "Mac OSX\n";
#elif __linux__
   const char* OSname = "Linux\n";
#else
   const char* OSname = "Other\n";
#endif

/*This is reference to the instruction to this homework*/
typedef struct BEACON
{
  int ID;         
  int StartUpTime;
  int IP[4];	
  int CmdPort;  
} beacon;

int getCurrentTime();
void *CmdAgent(void *arg);

void GenerateBEACON(beacon *A,int ID,char *IPAddress){
  
  int index1 = 0;
  int index2 = 0;
  int index3 = 0;
  int length = strlen(IPAddress);
  int i=0;
  
  for(i=0;i<length;i++){
    if(index1==0 && IPAddress[i]=='.'){
      index1=i;
    } else if (index2 < index1 && IPAddress[i] == '.'){
      index2=i;
    } else if (index3 < index2 && IPAddress[i] == '.'){
      index3=i;
    }
  }

  char substring1[index1];
  char substring2[index2-index1+1];
  char substring3[index3-index2+1];
  char substring4[length-index3+1];

  memset(substring1,'\0',index1);  
  memset(substring2,'\0',index2-index1);  
  memset(substring3,'\0',index3-index2);  
  memset(substring4,'\0',length-index3);  
  
  memcpy(substring1,&IPAddress[0],index1);
  memcpy(substring2,&IPAddress[index1+1],index2-index1-1);
  memcpy(substring3,&IPAddress[index2+1],index3-index2-1);
  memcpy(substring4,&IPAddress[index3+1],length-index3-1);
  
  time_t t = time(NULL);
  A->ID = ID;
  A->StartUpTime = getCurrentTime(); /*Get time in integer format*/
  A->IP[0]=atoi(substring1);
  A->IP[1]=atoi(substring2);
  A->IP[2]=atoi(substring3);
  A->IP[3]=atoi(substring4);
}

char * toString(beacon *A){
  char *result;
  result = (char *)malloc(1024 * sizeof(result));
  snprintf(result,1024,"%d#%d#%d.%d.%d.%d#%d\n",A->ID,A->StartUpTime,
	   A->IP[0],
	   A->IP[1],
	   A->IP[2],
	   A->IP[3],
	   A->CmdPort);
  return result;
}

typedef struct beacon_listener
{
  char *ServerIP;
  int port;
  beacon A;

}beacon_listener_t;

char * getIPAddress(beacon *A){
  char *result; /* pointer to array of 40 char */
  result = (char *)malloc(1024 * sizeof(result));
  snprintf(result,1024,"%d.%d.%d.%d",
	   A->IP[0],
	   A->IP[1],
	   A->IP[2],
	   A->IP[3]);
  return result;
  
}

void updateCmdPort(beacon *A,int port){
  A->CmdPort = port;
}

void GetLocalOS(char OS[16], int *valid){
  int i=0;
  for(i=0;i<strlen(OSname);i++){
    OS[i]=OSname[i];
  }
  *valid = 1;
}

/* Reference to http://stackoverflow.com/ on get system time*/
int getCurrentTime(){
  time_t rtime;
  struct tm * aTm;

  time ( &rtime );
  aTm = localtime ( &rtime );

  /*Convert to int on second unit*/
  int result = aTm->tm_hour*3600 + aTm->tm_min*60 + aTm->tm_sec;
  
  return result;
}

/*Idea is from cplusplus.com */
void GetLocalTime(int *time, int *valid){
  *time = getCurrentTime();
  *valid = 1;
}


/*UDP connection to server */
void *BeaconSender(void *arg){
  
  beacon_listener_t *input = (beacon_listener_t *)arg;
  char *ServerIP;
  int port;
  beacon A;
  int clientSocket;
  int messageLength;

  ServerIP = input->ServerIP;
  port = input->port;
  A = input->A;

  /*This code is reference to instruction in-class */
  struct sockaddr_in sin;
  memset (&sin, 0, sizeof(sin));
  socklen_t addr_size;


  /*Create UDP socket*/
  clientSocket = socket(PF_INET, SOCK_DGRAM, 0);

  sin.sin_family = AF_INET;
  sin.sin_port = htons(port);
  sin.sin_addr.s_addr = inet_addr(ServerIP);
  memset(sin.sin_zero, '\0', sizeof sin.sin_zero);  

  /* memset (&sin, 0, sizeof(sin));*/
  
  addr_size = sizeof sin;

  printf("\nManager Info: %s ",ServerIP);
  printf("Port: %d",port);

  while(1){
    
    char *message = toString(&A);
    messageLength = strlen(message);
    
    printf("\nSend to Manager this Beacon: %s\n",message);
    
    /*Send message to server*/
    if(sendto(clientSocket,message,messageLength,0,(struct sockaddr *)&sin,addr_size)==-1){
      printf("Cannot connect to Manager");
    }

    free(message);
   
    sleep(10);

  }
}

typedef struct CmdAgent_argument
{
  beacon *A;
  int svrSock;
  char *cli_addr;
  int clilen;
}CmdAgent_argument_t;


void BindSocket(beacon *A,CmdAgent_argument_t *input){
    
  char *cli_addr;
  int send_size;
  char manager_message[1024];
  int read_size;

  unsigned int svrAddr = INADDR_ANY;
  unsigned short svrPort = MINIMUM;
   /* unsigned short svrPort = atoi("8888");*/

  struct sockaddr_in sin;
  memset (&sin, 0, sizeof (sin));
  sin.sin_family = AF_INET;
  sin.sin_addr.s_addr = svrAddr; 
  sin.sin_port = htons (svrPort);

  int svrSock = socket( AF_INET, SOCK_STREAM, 0 );
  
  A->CmdPort = svrPort;

  /* Bind */
  while(svrPort<MAXIMUM && bind(svrSock, (struct sockaddr *) &sin, sizeof(sin)) < 0){
    	svrPort++;
	printf("change to another port %d\n",svrPort);
	sin.sin_port = htons (svrPort);
	memset(sin.sin_zero, '\0', sizeof sin.sin_zero); 
	A->CmdPort = svrPort;
	printf("\nCmdPort =  %d",A->CmdPort);
  }

  input->A = A;
  input->svrSock = svrSock;
  input->cli_addr = cli_addr;

  printf("\nBind successfully");
}


/*This code is refer to instruction in class */
void *CmdAgent(void *arg){

  CmdAgent_argument_t *input = (CmdAgent_argument_t *)arg;

  beacon *A = input->A;
  int svrSock = input->svrSock;
  char *cli_addr = input->cli_addr;
  int clilen = input->clilen;
  

  char *manager_message;
  int send_size;
  int read_size;
  listen(svrSock, 5); /* maximum 5 connections will be queued */

  while (1)
    {
      /* Accept */
      int cltSock = accept(svrSock, (struct sockaddr *)&cli_addr,&clilen);
      /* launch a new thread to take care of this client connection */
      /* cli_addr contains the address of the connecting client */
      /* clilent is the buffer length that is valid in cli_addr */
      /* both cli_addr and clileng are optional */
      
      while(recv(cltSock,manager_message,1024,0) > 0)
	{
	   printf("\nClient:\%s\n",manager_message);
	   /* Send the message back to magager(client) */
	   	   int valid=0;
		   /*char *local_time_message = "10:00\n";*/
	   char local_time_message[16];
	   memset (&local_time_message, 0, sizeof(local_time_message));
	   int localtime;
	   GetLocalTime(&localtime,&valid);
	   snprintf(local_time_message,16,"%d\n",localtime);
	   
	   send_size = send(cltSock, local_time_message , strlen(local_time_message),0);
	   
 	  char OS_message[16];
 	   GetLocalOS(OS_message,&valid);
	  send_size = send(cltSock,OS_message , strlen(OS_message),0);
	}
     if(read_size == 0)
       {
	 printf("\nTCP disconnected\n");
	 fflush(stdout);
       }
      
    }

}




int main(int argc, char *argv[]){

  if(argc <= 1){
    printf("\nPlease input argument following order\n");
    printf("./Agent AgentIP ManagerIP ManagerPort\n");
    return 1;
  } else if (argc > 5){
    printf("Too many argument");
    printf("\nPlease input argument following order");
    printf("./Agent AgentIP ManagerIP ManagerPort\n");
    return 1;
  }

  printf("Current time = %d",getCurrentTime());
  srand(time(NULL));
  pthread_t TCP_thread,UDP_thread;
  
  char *AgentIP = argv[2];
  char *ManagerIP = argv[3];
  int portUDP = atoi(argv[4]);
  int ID = atoi(argv[1]);
  beacon A;

  /* CmdAgent(&A);*/
  GenerateBEACON(&A,ID,AgentIP);

  CmdAgent_argument_t aCmdAgent_argument_t;
  BindSocket(&A,&aCmdAgent_argument_t);

  beacon_listener_t aBeaconListener;
  aBeaconListener.ServerIP = ManagerIP;
  aBeaconListener.A = A;
  aBeaconListener.port = portUDP;

  
  /*Thread call*/
  pthread_create(&UDP_thread, NULL, &BeaconSender, (void *)&aBeaconListener);
  pthread_create(&TCP_thread, NULL, &CmdAgent, (void *)&aCmdAgent_argument_t);
  pthread_join(TCP_thread, NULL);
  return 0;
}
