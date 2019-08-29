package TeamProject2;

import java.util.*;
//import javafx.util.*;

public class Knight extends ChessPieces {
	public Knight(int[] location, int player, String chessBoard[][]) throws Exception {
		super(location, player, chessBoard);
		priority = 3;
		type = 'N';
		update_list_of_moves(chessBoard);
	}
	
	public Knight() {}

	public ChessPieces copy(ChessPieces cp, String[][] chessBoard) throws Exception {
		ChessPieces chesspiece = new Knight();
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
		ArrayList<int[]> new_list_of_moves = new ArrayList<int[]>();
		if (state == 1) {
			return;
		}
		//Basic movement and attack
		int[][] moves = new int[][] {{2,-1}, {2,1}, {1, 2}, {-1, 2}, 
        		{-2,1}, {-2,-1}, {-1,-2}, {1,-2}};
        
        int[] new_move_location;
		String occupying_piece;
		
		for(int i = 0; i < moves.length; i++) {
		    new_move_location = new int[] {current_location[0] + moves[i][0], current_location[1] + moves[i][1]};
			if(is_on_board(new_move_location)) {
				occupying_piece = chessBoard[new_move_location[0]][new_move_location[1]];
				if(occupying_piece == " "
						|| (player == 0 && Character.isLowerCase(occupying_piece.charAt(0)))
						|| (player == 1 && Character.isUpperCase(occupying_piece.charAt(0)))) {
					new_list_of_moves.add(new_move_location);
				}
			}
		}
		
		list_of_moves = new_list_of_moves;
	}
}
