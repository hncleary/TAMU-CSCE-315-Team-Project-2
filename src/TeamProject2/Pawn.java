package TeamProject2;

import java.util.*;
//import javafx.util.*;

public class Pawn extends ChessPieces {
	public Pawn(int[] location, int player, String chessBoard[][]) throws Exception {
		super(location, player, chessBoard);
		priority = 1;
		type = 'P';
		update_list_of_moves(chessBoard);
	}

	public Pawn() {}

	public ChessPieces copy(ChessPieces cp, String[][] chessBoard) throws Exception {
		ChessPieces chesspiece = new Pawn();
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
		int forward_one = -1; //Player 0 moves up
		if(player == 1) {
			forward_one = 1;  //Player 1 moves down
		}
		
		//Basic Movement
		int[] new_move_location = new int[] {current_location[0] + forward_one, current_location[1]};
		boolean can_move_one_ahead = false;
		if(is_on_board(new_move_location) 
				&& chessBoard[new_move_location[0]][new_move_location[1]] == " ") {
			new_list_of_moves.add(new_move_location);
			can_move_one_ahead = true;
		}
		
		//First turn (for pawn) - Move 2
		
		new_move_location = new int[] {current_location[0] + 2*forward_one, current_location[1]};
		
		if(is_on_board(new_move_location) 
				&& !moved
				&& can_move_one_ahead
				&& chessBoard[new_move_location[0]][new_move_location[1]] == " ") {
			new_list_of_moves.add(new_move_location);
		}
		
		//Attack left
		int[] new_attack_location = new int[]{current_location[0] + forward_one, current_location[1] - 1};
		if(is_on_board(new_attack_location)) {
			String occupying_piece = chessBoard[new_attack_location[0]][new_attack_location[1]];
			
			if((player == 0 && Character.isLowerCase(occupying_piece.charAt(0)))
					|| (player == 1 && Character.isUpperCase(occupying_piece.charAt(0)))) {
				new_list_of_moves.add(new_attack_location);
			}
		}
		
		
		//Attack right
		new_attack_location = new int[]{current_location[0] + forward_one, current_location[1] + 1};
		
		if(is_on_board(new_attack_location)) {
			String occupying_piece = chessBoard[new_attack_location[0]][new_attack_location[1]];
			
			if((player == 0 && Character.isLowerCase(occupying_piece.charAt(0)))
					|| (player == 1 && Character.isUpperCase(occupying_piece.charAt(0)))) {
				new_list_of_moves.add(new_attack_location);
			}
		}
		
		/*
		System.out.println("size of old list: "+ list_of_moves.size());
		System.out.println("size of new list: "+ new_list_of_moves.size());
		
		listPossibleMoves();
		*/
		list_of_moves = new_list_of_moves;
	}
}