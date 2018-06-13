/*
	Windows C++ client for playing a Kalaha game using the Kalaha game server.
	The client uses the Wsock32.lib library for connecting to the server.

	Author: Johan Hagelbäck (jhg@bth.se)
*/

#include <winsock.h>
#include <stdio.h>
#include <iostream>
#include <string>
#include <vector>

using namespace std;

//Method definitions
void gameLoop(int mySocket, int player);
void makeMove(int mySocket, int player);
void sendMoveCmd(int mySocket, int player, int myMove);
string makeBoardStr(char input[]);
string makeSpaces(string str);
void tokenizeBoard(char input[], vector<string> &tokens);

int main(int a, char *args[]) {

	//Connection details
	int PORT = 8888;
	char* IP = "127.0.0.1";

	//Initialize that a winsock shall be used
	WSADATA ws;
	WSAStartup(0x0101, &ws);

	//Create and configure the socket
	int mySocket = socket(AF_INET, SOCK_STREAM, 0);
	struct sockaddr_in peer; 
	peer.sin_family = AF_INET;
	peer.sin_port = htons(PORT);
	peer.sin_addr.s_addr = inet_addr(IP);

	//Connect to server...
	connect(mySocket, (struct sockaddr*)&peer, sizeof(peer));
	//... and the game
	send(mySocket, "HELLO\n", 6, 0);
	
	//Check which player you are
	char input[43]; 
	recv(mySocket, input, sizeof(input), 0);
	if (input[6] != '1' && input[6] != '2') {
		cout << "Error connecting to Kalaha server" << endl;
		system("pause");
		return 0;
	}
	int player = input[6] - '0';

	cout << "Connected to Kalaha server as player " << player << endl;

	//Start the game loop
	gameLoop(mySocket, player);

	system("pause");
}

void gameLoop(int mySocket, int player) {
	bool gameRunning = true;
	char input[43];

	while (gameRunning) {
		//Check who's turn it is
		send(mySocket, "PLAYER\n", 7, 0);
		recv(mySocket, input, sizeof(input), 0);

		int nextToMove = input[0] - '0';
		
		//Your turn, make a move
		if (nextToMove == player) {
			makeMove(mySocket, player);
		}
		
		//Wait a bit
		Sleep(1000);
	}
}

void makeMove(int mySocket, int player) {
	char input[43];

	//Ask for current board
	send(mySocket, "BOARD\n", 6, 0);
	recv(mySocket, input, sizeof(input), 0);

	//Convert the recived board datastructure to a printable string
	string out = makeBoardStr(input);

	//Print the board
	cout << out << endl;

	//Ask the player for his move
	cout << "\nYou are next! make a move." << endl;
	cout << "[1,2,3,4,5,6] > ";
	int myMove = 0;
	cin >> myMove;

	//Send a move command to the Kalaha server.
	sendMoveCmd(mySocket, player, myMove);
}

void sendMoveCmd(int mySocket, int player, int myMove) {
	char input[43];
	char output[9];

	//Generate the command string
	output[0] = 'M';
	output[1] = 'O';
	output[2] = 'V';
	output[3] = 'E';
	output[4] = ' ';
	output[5] = myMove + '0';
	output[6] = ' ';
	output[7] = player + '0';
	output[8] = '\n';
	
	//Send the command
	send(mySocket, output, sizeof(output), 0);
	recv(mySocket, input, sizeof(input), 0);
}

string makeBoardStr(char input[]) {
	//Convert the board datastructure to a vector of ; separated tokens.
	vector<string> myBoard;
	tokenizeBoard(input, myBoard);

	//Generate a nice output of the board.
	string out = "\n[2]";

	for(int i=13; i>7; i--) {
		out += makeSpaces(myBoard.at(i));
	}

	out += "\n" + makeSpaces(myBoard.at(0)) + "                  " + makeSpaces(myBoard.at(7)) + "\n" + "[1]";

	for(int i=1; i<7; i++) {
		out += makeSpaces(myBoard.at(i));
	}

	return out;
}

string makeSpaces(string str) {
	string res = "";

	//Formats a number (0-99) to a nice string.
	if(str.length() == 2) {
		res = " " + str;
	}
	else if(str.length() == 1) {
		res = "  " + str;
	}
	else {
		res = str;
	}

	return res;
}

void tokenizeBoard(char input[], vector<string> &tokens) {
	//Converts a board datastructure to a vector of ; separated tokens.
	char sep[] = ";";
	char *token;
	token = strtok(input, sep);

	while (token != NULL) {
		tokens.push_back(token);
		token = strtok(NULL, sep);
	}
}
