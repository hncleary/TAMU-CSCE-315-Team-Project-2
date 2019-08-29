package TeamProject2;

import java.util.*;
//import javafx.util.*;

public class Rook extends ChessPieces {
	public Rook(int[] location, int player, String chessBoard[][]) throws Exception {
		super(location, player, chessBoard);
		priority = 5;
		type = 'R';
		update_list_of_moves(chessBoard);
	}

	public Rook() {}

	public ChessPieces copy(ChessPieces cp, String[][] chessBoard) throws Exception {
		ChessPieces chesspiece = new Rook();
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
		//Basic movement and attack - NORTH (on chess board)
		list_moves_of_direction(chessBoard, new int[] {-1, 0});
		
		//Basic movement and attack - SOUTH (on chess board)
		list_moves_of_direction(chessBoard, new int[] {1, 0});
		
		//Basic movement and attack - EAST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {0, 1});
		
		//Basic movement and attack - WEST (on chess board)
		list_moves_of_direction(chessBoard, new int[] {0, -1});
	}
}
