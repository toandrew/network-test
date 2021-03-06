// UdpEchoServer.cpp : UDP each server。
//

#include "stdafx.h"

#include <winsock2.h>
#include <stdio.h>
#include <windows.h>
// Need to link with Ws2_32.lib
#pragma comment (lib, "Ws2_32.lib")

int _tmain(int argc, _TCHAR* argv[])
{
	// Initialize Winsock.
	WSADATA wsaData;
	int result = WSAStartup(MAKEWORD(2, 2), &wsaData);
	if (result != NO_ERROR) {
		wprintf(L"WSAStartup failed with error: %ld\n", result);
		return 1;
	}

	// Create a SOCKET for listening for
	// incoming connection requests.
	SOCKET srvSocket;
	srvSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (srvSocket == INVALID_SOCKET) {
		wprintf(L"socket failed with error: %ld\n", WSAGetLastError());
		WSACleanup();
		return 1;
	}

	// The sockaddr_in structure specifies the address family,
	// IP address, and port for the socket that is being bound.
	sockaddr_in addrServer;
	addrServer.sin_family = AF_INET;
	addrServer.sin_addr.s_addr = htonl(INADDR_ANY);
	addrServer.sin_port = htons(20123);

	if (bind(srvSocket, (SOCKADDR *)& addrServer, sizeof (addrServer)) == SOCKET_ERROR) {
		wprintf(L"bind failed with error: %ld\n", WSAGetLastError());
		closesocket(srvSocket);
		WSACleanup();
		return 1;
	}

	char recvBuf[2048 + 1] = { 0 };
	while (1) {
		struct sockaddr_in cliAddr;
		int cliAddrLen = sizeof(cliAddr);
		int count = recvfrom(srvSocket, recvBuf, 2048, 0, (struct sockaddr *)&cliAddr, &cliAddrLen);
		if (count == 0) {
			break;
		}
		if (count == SOCKET_ERROR) {
			break;
		}

		printf("client IP = %s\n", inet_ntoa(cliAddr.sin_addr));

		if (sendto(srvSocket, recvBuf, count, 0, (struct sockaddr *)&cliAddr, cliAddrLen) < count) {
			break;
		}
	}

	closesocket(srvSocket);
	WSACleanup();

	return 0;
}

