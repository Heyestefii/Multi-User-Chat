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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Client extends Thread {
	
	// Private final variables are not accessible outside of the class
	// and they can't change the value once they are initialized. 
	private final Socket client;
	
	private String login = null;
	private Server server;
	private OutputStream output;
	private HashSet<String> groupchat = new HashSet<>();
	
	public Client(Server server, Socket client) {
		this.client = client;
		this.server = server;
	}
	
	@Override
	// Every thread has a run method
	// Create a new thread every time it gets a new connection from the client
	public void run() {
		try {
			multi_client();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void multi_client() throws IOException, InterruptedException{
		// Bidirectional communication 
		
		// InputStream for reading the data from the client
		InputStream input = client.getInputStream();
		// OutputStream get data from the client
		this.output = client.getOutputStream();
		
		// Buffer to help us read line by line 
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		// Variable to help us read the inputs 
		String line;
		
		// Method readLine() reads all typed characters until it finds the <Enter>
		while ((line = reader.readLine()) != null) {
			
			// Method StringUtils.split() provided text into different string tokens
			// based on the whitespace characters
			String[] tokens = StringUtils.split(line);
			
			// Check that tokens doesn't cause null point exceptions 
			if (tokens != null && tokens.length > 0) {
				
				// The first typed word will be allocated in the position 0 
				// and it is going to work like a command 
				String command = tokens[0];
				
				// If the command received is quit the client will be 
				// disconnected from the server
				if ("logout".equalsIgnoreCase(command)) {
					disconnected();
					break;
				} 
				
				// If the command received is login the client will star session
				else if ("login".equalsIgnoreCase(command)){
					handleLogin(output, tokens);
				} 
				
				// If the command received is message the client will send 
				// the text to another client or a group chat
				else if ("message".equalsIgnoreCase(command)){
					String[] tokensText = StringUtils.split(line, null, 3);
					sendMessage(tokensText);
				} 
				
				// If the command received is group chat the client will be 
				// added to the group chat specified
				else if ("groupchat".equalsIgnoreCase(command)){
					groupChatMessage(tokens);
				} 
				
				// If the command received is leave the client will be 
				// remove from the group chat 
				else if ("leave".equalsIgnoreCase(command)) {
					leaveGroupChat(tokens);
				} 
				
				// In case the client use another command that the server 
				// don't recognize it will show a message of unknown command
				else {
					String message = "unknown command " + command + "\n";
					output.write(message.getBytes());
				}
				
			}

		}
		
		client.close();
	}

	private void leaveGroupChat(String[] tokens) {
		// TODO Auto-generated method stub
		if (tokens.length > 1) {
			String group = tokens[1];
			groupchat.remove(group);
		}
	}

	public boolean groupMember(String group) {
		return groupchat.contains(group);
	}
	
	private void groupChatMessage(String[] tokens) {
		// TODO Auto-generated method stub
		if (tokens.length > 1) {
			String group = tokens[1];
			groupchat.add(group);
		}
	}

	private void sendMessage(String[] tokens) throws IOException {
		// TODO Auto-generated method stub
		// Server side 
		String sendTo = tokens[1];
		String text = tokens[2];
		
		boolean isGroupchat = sendTo.charAt(0) == '#';
		
		List<Client> clientList = server.getClientList();
		for(Client thread_n : clientList) {
			if (isGroupchat) {
				if (thread_n.groupMember(sendTo)) {
					String textSend = "message from groupchat " + sendTo + ":" + login + " " + text + "\n";
					thread_n.send(textSend);
				}
			} else {
				if (sendTo.equalsIgnoreCase(thread_n.getLogin())) {
					String textSend = "message from " + login + " " + text + "\n";
					thread_n.send(textSend);
				}
			}
		}
	}

	private void disconnected() throws IOException {
		// TODO Auto-generated method stub
		server.removeClient(this);
		
		List<Client> clientList = server.getClientList();
		
		// Send the users online that a user is now disconnected
		String new_active = "the user " + login + " is offline \n"; 
		for(Client thread_n : clientList) {
			if (!login.equals(thread_n.getLogin())) {
				thread_n.send(new_active);
			}
		}
		
		client.close();
	}

	public String getLogin() {
		return login;
	}
	
	
	private void handleLogin(OutputStream output, String[] tokens) throws IOException {
		if (tokens.length == 3) {
			String login = tokens[1];
			String password = tokens[2];
			
			// Hard code users 
			if ((login.equals("Estefi") && password.equals("*****")) || (login.equals("Gerardo") && password.equals("*****"))
			   || (login.equals("Clau") && password.equals("*****")) || (login.equals("Andres") && password.equals("*****"))
			   || (login.equals("Lalo") && password.equals("*****")) || (login.equals("Andrea") && password.equals("*****"))
			   || (login.equals("Fer") && password.equals("*****")) || (login.equals("Xime") && password.equals("*****"))
			   || (login.equals("Azul") && password.equals("*****")) || (login.equals("Paola") && password.equals("*****"))
			   || (login.equals("Jazmin") && password.equals("*****")) || (login.equals("Jaime") && password.equals("*****"))
			   || (login.equals("Marce") && password.equals("*****")) || (login.equals("Vicky") && password.equals("*****"))
			   || (login.equals("Belen") && password.equals("*****")) || (login.equals("Isidro") && password.equals("*****"))
			   || (login.equals("Beto") && password.equals("*****")) || (login.equals("Luis") && password.equals("*****"))
			   || (login.equals("Carlos") && password.equals("*****")) || (login.equals("Efren") && password.equals("*****"))) {
				
				String message = "Welcome back " + login + "\n";
				output.write(message.getBytes());
				
				this.login = login;
				System.out.println("The user " + login + " was logged in \n");
			
				List<Client> clientList = server.getClientList();
				
				// Send the user all the other users that are already online
				for(Client thread_n : clientList) {
					if (thread_n.getLogin() != null) {
						if (!login.equals(thread_n.getLogin())) {
							String online = thread_n.getLogin() + " is online!\n";
							send(online);
						}
					}
				}
				
				
				// Send the users online that a new user is active
				String new_active = "The user " + login + " join the chat server!\n"; 
				for(Client thread_n : clientList) {
					if (!login.equals(thread_n.getLogin())) {
						thread_n.send(new_active);
					}
				}
			} else {
				String message = "wrong username or password\n";
				output.write(message.getBytes());
			}
		}

	}

	private void send(String message) throws IOException {
		// TODO Auto-generated method stub
		if (login != null) {
			output.write(message.getBytes());	
		}
	}
}
