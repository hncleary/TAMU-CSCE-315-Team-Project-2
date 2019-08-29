package TeamProject2;
import java.net.*;
import java.io.*;

public class Server {
	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream server_in = null;
	private DataOutputStream server_out = null;
	private String message = "";
	
	private int time;
	private String server_side;
	private String client_side;
	
	//Needs port number (any number between 0-65535)
	public Server(int port) {
		try {
			server = new ServerSocket(port);
			System.out.println("Server created with port " + port);
			
			//Wait for connection with client
			System.out.println("Waiting for client");
			socket = server.accept();
			System.out.println("Client accepted");
			
			//Create structures to get input from and output to client socket
			server_in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			server_out = new DataOutputStream(socket.getOutputStream());  
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	//Use concurrently with client using "send_server_chess_settings"
	public boolean get_client_chess_settings() {
		try {
			time = server_in.readInt();
			client_side = server_in.readUTF();
			server_side = client_side.equals("White") ? "Black" : "White";
			System.out.println("INFO " + time + " " + client_side); //Send to client - currently for debug
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	//Use concurrently with client using "send_server_ready"
	public boolean wait_for_client_ready() {
		//Wait for client to ready-up
		try {
			server_in.readUTF();
			if(message.equals("READY")) {
				return true;
			}
			return false;
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	//Two functions for receiving a client's move and sending appropriate confirmation
	public String get_client_move_list() {
		//Get the client's move
		try {
			message = server_in.readUTF();
			return message;
		} catch(Exception e) {
			System.out.println(e);
			return "fail";
		}
	}
	
	public boolean send_client_confirmation() {
		//Send client information on move - OK/ILLEGAL/TIME
		//Do stuff here then send if OK/ILLEGAL/TIME
		message = "OK";
		try {
			server_out.writeUTF(message);
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
		//Could potentially do stuff here to to stop game
		return true;
	}
	
	//Two functions for sending a server's move and receiving appropriate confirmation
	public boolean send_client_move_list(String move) {
		//Send server move
		try {
			//Attempt to send a move from server to the client
			server_out.writeUTF(move);
			return true;
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	public String get_client_confirmation() {
		//Get client's information on move - OK
		try {
			message = server_in.readUTF();
			return message;
		} catch(Exception e) {
			System.out.println(e);
			return "fail";
		}
	}
	
	public boolean close() {
		try {
			//Close all connections
			System.out.println("Closing server connection");
			server_in.close();
			server_out.close();
			socket.close();
			return true;
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	public int get_time() {
		return time;
	}
	
	public String get_client_side() {
		return client_side;
	}
	
	public String get_server_side() {
		return server_side;
	}
}
