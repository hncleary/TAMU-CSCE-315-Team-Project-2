package TeamProject2;
import java.net.*;
import java.io.*;

public class Client {
	private Socket socket = null;
	private DataInputStream client_in = null;
	private DataOutputStream client_out = null;
	private String message = "";
	
	private int time;
	private String client_side;
	private String server_side;
	
	//Needs IP address of server (loop-back IP for same machine is 127.0.0.1)
	//Needs port number (any number between 0-65535)
	public Client(String address, int port) {
		try {
			//Attempt to create connection with server
			socket = new Socket(address, port);
			System.out.println("Connected to server with port " + port);
			
			//Create structures to get input from and output to server socket
			client_in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			client_out = new DataOutputStream(socket.getOutputStream());
			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	//Use concurrently with server using "get_client_chess_settings"
	public boolean send_server_chess_settings(int time, String side) {
		if(time < 0) {
			//Time cannot be negative
			return false;
		}
		if(!side.equals("White") && !side.equals("Black")) {
			return false;
		}
		try {
			client_out.writeInt(time);
			client_out.writeUTF(side);
			
			this.time = time;
			this.client_side = side;
			server_side = client_side.equals("White") ? "Black" : "White";
			
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	//Use concurrently with server using "wait_for_client_ready"
	public boolean send_server_ready() {
		//Wait for client to ready-up
		try {
			client_out.writeUTF("READY");
			return true;
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	//Two functions for sending a client move and receiving appropriate confirmation
	public boolean send_server_move_list(String move) {
		//Send client move
		try {
			//Attempt to send a move from client to the server
			client_out.writeUTF(move);
			return true;
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	public String get_server_confirmation() {
		//Get server's information on move - OK/ILLEGAL/TIME
		try {
			message = client_in.readUTF();
			return message;
		} catch(Exception e) {
			System.out.println(e);
			return "fail";
		}
	}
	
	//Two functions for receiving a server's move and sending appropriate confirmation
	public String get_server_move_list() {
		//Get the server's move
		try {
			message = client_in.readUTF();
			return message;
		} catch(Exception e) {
			System.out.println(e);
			return "fail";
		}
	}
	
	public boolean send_server_confirmation() {
		//Since client side-no need to check move - already checked on server side
		message = "OK";
		try {
			client_out.writeUTF(message);
		} catch(Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	public boolean close() {
		try {
			//Close all connections
			System.out.println("Closing client connection");
			client_in.close(); 
			client_out.close();
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
