// TcpEchoServer.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <winsock2.h>
#include <stdio.h>
#include <windows.h>
// Need to link with Ws2_32.lib
#pragma comment (lib, "Ws2_32.lib")

int _tmain(int argc, _TCHAR* argv[])
{
	//----------------------
	// Initialize Winsock.
	WSADATA wsaData;
	int result = WSAStartup(MAKEWORD(2, 2), &wsaData);
	if (result != NO_ERROR) {
		wprintf(L"WSAStartup failed with error: %ld\n", result);
		return 1;
	}

	//----------------------
	// Create a SOCKET for listening for
	// incoming connection requests.
	SOCKET listenSocket;
	listenSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (listenSocket == INVALID_SOCKET) {
		wprintf(L"socket failed with error: %ld\n", WSAGetLastError());
		WSACleanup();
		return 1;
	}
	//----------------------
	// The sockaddr_in structure specifies the address family,
	// IP address, and port for the socket that is being bound.
	sockaddr_in addrServer;
	addrServer.sin_family = AF_INET;
	addrServer.sin_addr.s_addr = htonl(INADDR_ANY);
	addrServer.sin_port = htons(20122);

	if (bind(listenSocket, (SOCKADDR *)& addrServer, sizeof (addrServer)) == SOCKET_ERROR) {
		wprintf(L"bind failed with error: %ld\n", WSAGetLastError());
		closesocket(listenSocket);
		WSACleanup();
		return 1;
	}

	// Listen for incoming connection requests.
	// on the created socket
	if (listen(listenSocket, 1) == SOCKET_ERROR) {
		wprintf(L"listen failed with error: %ld\n", WSAGetLastError());
		closesocket(listenSocket);
		WSACleanup();
		return 1;
	}

	SOCKADDR_IN addrClient;
	int len = sizeof(SOCKADDR);

	while (true) {
		SOCKET AcceptSocket = accept(listenSocket, (SOCKADDR*)&addrClient, &len);
		if (AcceptSocket == INVALID_SOCKET) {
			break;
		}

		char recvBuf[51] = { 0 };
		while (true){
			int count = recv(AcceptSocket, recvBuf, 50, 0);
			if (count == 0) {
				break;
			}

			if (count == SOCKET_ERROR) {
				break;
			}

			int sendCount, currentPosition = 0;
			while (count>0 && (sendCount = send(AcceptSocket, recvBuf + currentPosition, count, 0)) != SOCKET_ERROR) {
				count -= sendCount;
				currentPosition += sendCount;
			}
			if (sendCount == SOCKET_ERROR) {
				break;
			}

			printf("Got messages from client[%s] message[%d]\n", inet_ntoa(addrClient.sin_addr), recvBuf);
		}

		closesocket(AcceptSocket);
	}

	closesocket(listenSocket);
	WSACleanup();
	return 0;
}

