package TeamProject2;

import java.util.*;
//import javafx.util.*;

public class Bishop extends ChessPieces {
	public Bishop(int[] location, int player, String chessBoard[][]) throws Exception {
		super(location, player, chessBoard);
		priority = 3;
		type = 'B';
		update_list_of_moves(chessBoard);
	}
	
	public Bishop() {}

	public ChessPieces copy(ChessPieces cp, String[][] chessBoard) throws Exception {
		ChessPieces chesspiece = new Bishop();
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
			return;
		}
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
