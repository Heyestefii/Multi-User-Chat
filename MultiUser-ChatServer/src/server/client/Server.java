/* 
 *  Multi-threading chat server 
    Copyright (C) 2021 Estefania Ortiz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/


package server.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
	
	private final int port;
	
	private ArrayList<Client> clientList = new ArrayList<>();
	
	public Server(int port) {
		this.port = port;
	}
	
	public List<Client> getClientList() {
		return clientList;
	}
	
	@Override
	public void run() {
		accept_connection();
	}
	
	public void accept_connection() {
		try {
			
			// Create a Server Socket 
			// The parameter to a server socket is a port
			// You need to past the port to the instance of the Server Socket
			ServerSocket socket = new ServerSocket(port);
			
			// Continuously accept the connection from the client
			while(true) {
				
				// The accept() method is the one that create the connection between the server and the client
				// This method accept the incoming request to the socket
				Socket client = socket.accept(); 
				
				// Show in the server that the connection with the client is working
				System.out.println("Connection accepted :" + client);
				
				// Create a new instance of the class Client
				// Parameters : socket
				Client thread_n = new Client(this, client);
				clientList.add(thread_n);
				thread_n.start();
				
				OutputStream output = client.getOutputStream();
				output.write("Hello! Please login to your account\n".getBytes());
				
				// OutputStream output1 = client.getOutputStream();
				// output1.write("Login username password\n".getBytes());
				
				// Create a new thread every time it gets a new connection from the client
				//  Thread thread1 = new Thread() {
				//	 @Override 
				//	 public void run() {
				//	 	 try {
				//			 multi_client(client);
				//		 } catch(IOException e) {
				//			 e.printStackTrace();
				//		 } catch(InterruptedException e) {
				//			 e.printStackTrace();
				//		 }
				//	 }
				// };
				// thread1.start();
				
				// Check it out working with only one connection
				// OutputStream output = client.getOutputStream();
				// output.write("working\n".getBytes());
				// client.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void removeClient(Client clientThread) {
		// TODO Auto-generated method stub
		clientList.remove(clientThread);
	}
}
