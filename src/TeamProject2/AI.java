package TeamProject2;

import java.util.*;

public class AI {
	private int player;
	private int depth;
	
	private String[][] chessboard;
	private ChessPieces[] pieces;
	
	AI(String[][] new_chessboard, ChessPieces[] new_pieces, int new_player, int new_depth) throws Exception {
		update_AI(new_chessboard, new_pieces);
		player = new_player;
		if(new_depth < 1) {
			throw new Exception("AI(): Error: depth must be 1 or greater\n");
		}
		depth = new_depth;
	}
	
	public int[][] get_move(String[][] new_chessboard, ChessPieces[] new_pieces) throws Exception {
		update_AI(new_chessboard, new_pieces);
		
		//enter Minimax algorithm through each possible initial move from pieces
		//i.e. modified get_max function
		double best_move = -Double.MAX_VALUE;
		int[] location_of_piece = new int[2];
		int[] location_of_movement = new int[2];
		
		for(int i = 0; i < pieces.length; i++) {
			if(player == pieces[i].get_player() && pieces[i].get_state() == 0) {
				//record current state for future undo
				int[] saved_location = Arrays.copyOf(pieces[i].get_current_location(), pieces[i].get_current_location().length);
				boolean saved_first_turn = pieces[i].has_moved();
				
				ArrayList<int[]> move_list = pieces[i].get_list_of_moves();
				for(int j = 0; j < move_list.size(); j++) {
					int[] new_location = Arrays.copyOf(move_list.get(j), move_list.get(j).length);
					
					int attacked_piece = attempt_move(i, new_location);
					//print_board();
					double move_score = -Double.MAX_VALUE;
					if(!in_check(player)) {
						move_score = get_min(depth-1, -Double.MAX_VALUE, Double.MAX_VALUE);
					}
					if(move_score > best_move) {
						best_move = move_score;
						location_of_piece = saved_location;
						location_of_movement = new_location;
					}
					undo_move(i, saved_location, saved_first_turn, attacked_piece);
				    //print_board();
				}
			}
		}
		
		return new int[][] {location_of_piece, location_of_movement};
	}
	
	//Minimax algorithm
	private double get_max(int new_depth, double alpha, double beta) throws Exception {
		if(new_depth == 0) {
			return utility(player);
		}
		double best_move = -Double.MAX_VALUE; //most negative number
		
		for(int i = 0; i < pieces.length; i++) {
			if(player == pieces[i].get_player() && pieces[i].get_state() == 0) {
				//record current state for future undo
				int[] saved_location = Arrays.copyOf(pieces[i].get_current_location(), pieces[i].get_current_location().length);
				boolean saved_first_turn = pieces[i].has_moved();
				
				ArrayList<int[]> move_list = pieces[i].get_list_of_moves();
				for(int j = 0; j < move_list.size(); j++) {
					int[] new_location = Arrays.copyOf(move_list.get(j), move_list.get(j).length);
					
					//System.out.println("Chessboard move");
					int attacked_piece = attempt_move(i, new_location);
					//System.out.println("Attacked Piece = " + attacked_piece);
					
					//print_board();
					best_move = Math.max(best_move, get_min(new_depth-1, alpha, beta));
					undo_move(i, saved_location, saved_first_turn, attacked_piece);
					//System.out.println("Chessboard undo");
					//print_board();
					
					alpha = Math.max(best_move, alpha);
					if(beta <= alpha) {
						return best_move;
					}
				}
			}
		}
		return best_move;
	}
	
	private double get_min(int new_depth, double alpha, double beta) throws Exception {
		if(new_depth == 0) {
			return utility(player == 0 ? 1 : 0);
		}
		double worst_move = Double.MAX_VALUE; //most positive number
		
		for(int i = 0; i < pieces.length; i++) {
			if(player != pieces[i].get_player() && pieces[i].get_state() == 0) {
				//record current state for future undo
				int[] saved_location = Arrays.copyOf(pieces[i].get_current_location(), pieces[i].get_current_location().length);
				boolean saved_first_turn = pieces[i].has_moved();
				
				ArrayList<int[]> move_list = pieces[i].get_list_of_moves();
				for(int j = 0; j < move_list.size(); j++) {
					int[] new_location = Arrays.copyOf(move_list.get(j), move_list.get(j).length);
					
					//System.out.println("Chessboard move");
					int attacked_piece = attempt_move(i, new_location);
					//System.out.println("Attacked Piece = " + attacked_piece);
					
					//print_board();
					worst_move = Math.min(worst_move, get_max(new_depth-1, alpha, beta));
					undo_move(i, saved_location, saved_first_turn, attacked_piece);
					//System.out.println("Chessboard undo");
					//print_board();
					
					beta = Math.min(worst_move, beta);
					if(beta <= alpha) {
						return worst_move;
					}
				}
			}
		}
		return worst_move;
	}
	
	//Auxiliary functions
	public void update_AI(String[][] new_chessboard, ChessPieces[] new_pieces) throws Exception {
		
		//Copy Chess board
		chessboard = new String[new_chessboard.length][];
		for(int i = 0; i < new_chessboard.length; i++) {
			chessboard[i] = new String[new_chessboard[i].length];
			System.arraycopy(new_chessboard[i], 0, chessboard[i], 0, new_chessboard[i].length);
		}
		
		//Copy Chess pieces
		pieces = new ChessPieces[new_pieces.length];
		for(int i = 0; i < pieces.length; i++) {
			pieces[i] = new_pieces[i].copy(new_pieces[i], new_chessboard);
		}
	}
	
	private void update_all_pieces() {
		for(int i = 0; i < pieces.length; i++) {
			pieces[i].update_list_of_moves(chessboard);
		}
	}
	
	private int attempt_move(int piece_index, int[] new_location) throws Exception{
		int attacked_piece_index = -1;
		int[] current_location = Arrays.copyOf(pieces[piece_index].get_current_location(),
				pieces[piece_index].get_current_location().length);
		
		if(!pieces[piece_index].move(new_location, chessboard)) {
			//Attack
			for(int i = 0; i < pieces.length; i++) {
				if(Arrays.equals(pieces[i].get_current_location(), new_location)) {
					attacked_piece_index = i;
					pieces[piece_index].attack(new_location, chessboard);
					pieces[attacked_piece_index].changeState(1);
					break;
				}
			}
			
		}
		chessboard[current_location[0]][current_location[1]] = " ";
		
		String piece_type = Character.toString(pieces[piece_index].get_type());
		chessboard[new_location[0]][new_location[1]]
			= (pieces[piece_index].get_player() == 0) ? piece_type : piece_type.toLowerCase();
		
		update_all_pieces();
		return attacked_piece_index;
	}
	
	private void undo_move(int piece_index, int[] prev_location, boolean prev_moved, int attacked_piece_index) {
		
		pieces[piece_index].set_has_moved(prev_moved);
				
		int[] current_location = Arrays.copyOf(pieces[piece_index].get_current_location(),
				pieces[piece_index].get_current_location().length);
		
		pieces[piece_index].setLocation(prev_location[0], prev_location[1]);
		
		if(attacked_piece_index != -1) {
			pieces[attacked_piece_index].changeState(0);
			
			String attacked_piece_type = Character.toString(pieces[attacked_piece_index].get_type());
			chessboard[current_location[0]][current_location[1]]
					= (pieces[attacked_piece_index].get_player() == 0) 
					? attacked_piece_type : attacked_piece_type.toLowerCase();
		} else {
			chessboard[current_location[0]][current_location[1]] = " ";
		}
		
		String piece_type = Character.toString(pieces[piece_index].get_type());
		chessboard[prev_location[0]][prev_location[1]]
				= (pieces[piece_index].get_player() == 0) ? piece_type : piece_type.toLowerCase();
		
		update_all_pieces();
	}
	
	private boolean in_check(int given_player) {
		//Check for check
		ChessPieces King = null;
		//get King piece
		for(int i = 0; i < pieces.length; i++) {
			if(given_player == pieces[i].get_player() && pieces[i].get_type() == 'K') {
				King = pieces[i];
			}
		}
		//check all moves to see if attacking King
		for(int i = 0; i < pieces.length; i++) {
			if(given_player != pieces[i].get_player() && pieces[i].get_state() == 0) {
				ArrayList<int[]> move_list = pieces[i].get_list_of_moves();
				for(int j = 0; j < move_list.size(); j++) {
					if(Arrays.equals(King.get_current_location(), move_list.get(j))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//sign = 1 if AI, -1 if User
	private double utility(int given_player) {
		double evaluation = 0;
		double w1 = 1;   //Weight for factor 1
		double w2 = 0.1; //Weight for factor 2
		
		//Factor 1: Material
		int material_difference;
		int sum_AI_material = 0;
		int sum_user_material = 0;
		
		//Factor 2: Mobility
		int move_difference;
		int num_AI_moves = 0;
		int num_user_moves = 0;
		
		//Find factors looping though all pieces
		for(int i = 0; i < pieces.length; i++) {
			if(pieces[i].get_state() == 0) { //alive
				if(player == pieces[i].get_player()) {
					//AI Pieces
					sum_AI_material += pieces[i].get_priority();
					num_AI_moves += pieces[i].get_num_moves();
				} else {
					//User Pieces
					sum_user_material += pieces[i].get_priority();
				    num_user_moves += pieces[i].get_num_moves();
				}
			}
		}
		
		if(in_check(player)) {
			return -Double.MAX_VALUE;
		}
		
		material_difference = (sum_AI_material - sum_user_material);
		move_difference = (num_AI_moves - num_user_moves);
		
		//Create evaluation based on sum of weighted factors
		evaluation += w1*(material_difference) + w2*(move_difference);
		
		return evaluation;
	}
	
	//Debugging functions
	public void print_board() {
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				System.out.print("|");
				System.out.print(chessboard[i][j]);
				System.out.print("|");
			}
			System.out.println("");
		}
		System.out.println("");
	}
}