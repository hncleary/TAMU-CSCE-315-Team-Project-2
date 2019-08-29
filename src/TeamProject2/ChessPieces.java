package TeamProject2;

import java.util.*;
//import javafx.util.*;

abstract class ChessPieces {
	//Check if a piece has moved yet
	protected boolean moved = false;
	//Priority of piece
	protected int priority;
	//Current state of chess piece; 0 = alive, 1 = captured
	protected int state = 0;
	//Kind of chess piece
	protected char type;
	//0 for Player 1 (located on bottom), 1 for Player 2 (located on top)
	protected int player;
	//Previous location of chess piece in (y,x) coordinates
	protected int[] previous_location = new int[2];
	//Current location of chess piece in (y,x) coordinates
	protected int[] current_location = new int[2];
	//List of possible moves (y,x) that the chess piece can make at current location
	protected ArrayList<int[]> list_of_moves = new ArrayList<int[]>();
	
	//Shared constructor - location in (y,x)
	public ChessPieces(int[] location, int player_num, String chessBoard[][]) throws Exception {
		//Check if player color correct
		if(player_num != 0 && player_num != 1) {
			throw new Exception("ChessPieces(): Error: Player number/index must be 0 or 1\n");
		}
		player = player_num;
		
		//Check if location a coordinate
		if(location.length != 2) {
			throw new Exception("ChessPieces(): Error: Location not size 2 (x,y)\n");
		}
		
		//Check if location is on the board
		if(!is_on_board(location)) {
			throw new Exception("Negative coord\n");
		}
		
		//Check if location is currently occupied
//		if(chessBoard[location[0]][location[1]] != " ") {
//			throw new Exception("ChessPieces(): Error: Location already occupied by another piece\n");
//		}
		
		current_location = Arrays.copyOf(location, location.length);
		previous_location = Arrays.copyOf(location, location.length);
		update_list_of_moves(chessBoard);
	}
	
	public ChessPieces() {}
	
	public abstract ChessPieces copy(ChessPieces cp, String chessBoard[][]) throws Exception;
	
	//Movement and Attack methods - returns true if valid, false otherwise
	public void setLocation(int row, int col) {
		current_location[0] = row;
		current_location[1] = col;
	}
	
	public boolean move(int[] move_location, String chessBoard[][]) throws Exception {
		//Check against list of moves list
		update_list_of_moves(chessBoard);
		if(move_location.length != 2) {
			throw new Exception("move(): Error: Location not size 2 (x,y)\n");
		}
		for(int i = 0; i < list_of_moves.size(); i++) {
			//Check for valid move and if the space is empty
			if(move_location[0] == list_of_moves.get(i)[0] && move_location[1] == list_of_moves.get(i)[1] 
					&& chessBoard[move_location[0]][move_location[1]] == " ") {
				if(!moved) moved = true;
				previous_location = Arrays.copyOf(current_location, current_location.length);
				current_location = Arrays.copyOf(move_location, move_location.length);
				update_list_of_moves(chessBoard);
				return true;
			}
		}
		return false;
	}
	
	public boolean attack(int[] attack_location, String chessBoard[][]) throws Exception {
		//Check against list of attacks list
		update_list_of_moves(chessBoard);
		if(attack_location.length != 2) {
			throw new Exception("move(): Error: Location not size 2 (x,y)\n");
		}
		for(int i = 0; i < list_of_moves.size(); i++) {
			if(attack_location[0] == list_of_moves.get(i)[0] && attack_location[1] == list_of_moves.get(i)[1]
					&& chessBoard[attack_location[0]][attack_location[1]] != " ") { 
				if(!moved) moved = true;
				previous_location = Arrays.copyOf(current_location, current_location.length);
				current_location = Arrays.copyOf(attack_location, attack_location.length);
				update_list_of_moves(chessBoard);
				return true;
			}
		}
		return false;
	}
	public void eraseMoves() {
		list_of_moves.clear();
	}
	public void kill() {
		list_of_moves.clear();
		state = 1;
	}
	
	public void revive() {
		state = 0;
	}
	
	//Get move and attack lists - returns an array list of valid coordinates
	public boolean has_moved() {
		return moved;
	}
	
	public int get_priority() {
		return priority;
	}
	
	public int get_state() {
		return state;
	}
	
	public char get_type() {
		return type;
	}
	
	public int get_player() {
		return player;
	}
	
	public int[] get_current_location() {
		return current_location;
	}
	
	public ArrayList<int[]> get_list_of_moves() {
		if (state == 1) {
			return new ArrayList<int[]>();
		}
		return list_of_moves;
	}
	
	public int get_num_moves() {
		return list_of_moves.size();
	}
	
	//Printing methods
	public void listPossibleMoves() {
		System.out.println("Possible moves: ");
		for(int i = 0; i < list_of_moves.size(); i++) {
			System.out.println(list_of_moves.get(i)[0] + ", "+ list_of_moves.get(i)[1]);	 
		}
		System.out.println("list of moves count: " + list_of_moves.size());
	}
	
	//Updating methods - depends on piece
	protected void list_moves_of_direction(String chessBoard[][], int[] direction) {
		if (state == 1) {
			list_of_moves.clear();
			return;
		}
		int y_direction = direction[0];
		int x_direction = direction[1];
		
		//Basic movement and attack
		int[] new_location = new int[] {current_location[0] + y_direction, current_location[1] + x_direction};
		
		while(is_on_board(new_location)) {
			String occupying_piece = chessBoard[new_location[0]][new_location[1]];
			
			if(occupying_piece == " ") {
				//Basic movement
				list_of_moves.add(new_location);
				new_location = new int[] {new_location[0] + y_direction, new_location[1] + x_direction};
				
			} else if((player == 0 && Character.isLowerCase(occupying_piece.charAt(0)))
					|| (player == 1 && Character.isUpperCase(occupying_piece.charAt(0)))) {
				//Basic attack
				list_of_moves.add(new_location);
				break;
			
			} else { //hit pieces of own side
				break;
			}
		}
	}
	
	public abstract void update_list_of_moves(String chessBoard[][]);
	
	public void set_has_moved(boolean new_has_moved) {
		moved = new_has_moved;
	}
	
	public void changeState(int newState) {
		state = newState;
	}
	
	//Checking methods
	public boolean is_on_board(int[] location) {
		//check vertical index
		if(location[0] < 0 || location[0] > 7) {
			return false;
		}
		//check horizontal index
		if(location[1] < 0 || location[1] > 7) {
			return false;
		}
		return true;
	}
	
	public boolean locationMatches(int row, int col) {
		if (row == current_location[0]) {
			if (col == current_location[1]) {
				return true;
			}
		}
		/*
		System.out.print("Given Location :");
		System.out.print(row);
		System.out.print(",");
		System.out.println(col);
		System.out.print("Does not match with: ");
		System.out.print(current_location[0]);
		System.out.print(",");
		System.out.println(current_location[1]);
		*/
		return false;
	}
}
