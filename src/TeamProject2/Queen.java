package TeamProject2;

import java.util.*;
//import javafx.util.*;

public class Queen extends ChessPieces {
	public Queen(int[] location, int player, String chessBoard[][]) throws Exception {
		super(location, player, chessBoard);
		priority = 9;
		type = 'Q';
		update_list_of_moves(chessBoard);
	}
	
	public Queen() {}

	public ChessPieces copy(ChessPieces cp, String[][] chessBoard) throws Exception {
		ChessPieces chesspiece = new Queen();
		chesspiece.moved = cp.moved;
		chesspiece.priority = cp.priority;
		chesspiece.state = cp.state;
		chesspiece.type = cp.type;
		chesspiece.player = cp.player;
		chesspiece.current_location = Arrays.copyOf(cp.current_location, cp.current_location.length);
		chesspiece.update_list_of_moves(chessBoard);
		return chesspiece;
	}
	
	public void update_list_of_moves(String chessBoard[][]) {
		list_of_moves = new ArrayList<int[]>();
		if (state == 1) {
			list_of_moves.clear();
			return;
		}
		
		//ROOK MOVES
		//Basic movement and attack - NORTH (on chess board)
		list_moves_of_direction(chessBoard, new int[] {-1, 0});
		
		//Basic movement and attack - SOUTH (on chess board)
		list_moves_of_direction(chessBoard, new int[] {1, 0});
		
		//Basic movement and attack - EAST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {0, 1});
		
		//Basic movement and attack - WEST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {0, -1});
		
		//BISHOP MOVES
		//Basic movement and attack - NORTH-EAST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {-1, 1});
		
		//Basic movement and attack - NORTH-WEST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {-1, -1});
		
		//Basic movement and attack - SOUTH-WEST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {1, -1});
		
		//Basic movement and attack - SOUTH-EAST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {1, 1});
	}
}
