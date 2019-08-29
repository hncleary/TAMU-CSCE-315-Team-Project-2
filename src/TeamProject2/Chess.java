package TeamProject2;

import java.util.*;

import javax.swing.*;

import java.util.Random;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Chess {
	//static int currentPlayerGlobal = 0;
	//chess board is an 8x8 array
	//whites are capital on bottom, blacks are lowercase on top
	static String chessBoard[][]= {
		     	{" "," "," "," "," "," "," "," "}, //index [0][n] (y, x)
				{" "," "," "," "," "," "," "," "}, //index [1][n]
				{" "," "," "," "," "," "," "," "},
				{" "," "," "," "," "," "," "," "},
				{" "," "," "," "," "," "," "," "},
				{" "," "," "," "," "," "," "," "},
				{" "," "," "," "," "," "," "," "},
				{" "," "," "," "," "," "," "," "}
	};
	static String prevBoard[][]= {
	     	{" "," "," "," "," "," "," "," "}, //index [0][n] (y, x)
			{" "," "," "," "," "," "," "," "}, //index [1][n]
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "}
};
	static ChessPieces[] pieces = new ChessPieces[32];
	static ArrayList<ChessPieces> capturedPieces = new ArrayList<ChessPieces>();
	static AI ai;
	static AI ai2;
	static int currentPlayer;
	static boolean turnOver = false;
	//"lowercase" and "uppercase" king position
	//king is the most important piece, keep variable updated
	//rather than looking for king in after each turn
	static int kingPositionC, kingPositionL;
	
	static boolean player0AiOn = false;
	static boolean player1AiOn = false;

	public static void main(String[] args) throws Exception {
		//System.out.println("howdy world");

		//JFrame f is a new window
		JFrame f = new JFrame("Chess AI - Team Project 2");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		userInterface ui = new userInterface();
		f.add(ui);
		f.setSize(1720,1080);
		f.setResizable(false);
		f.setVisible(true);

		//ui.changeCurrentPlayer();

		try {

			resetBoard();
			printVirtualBoard();
			update_all_pieces();
			
			//testing upgrade pawn function
//			upgradePawn("N",6, 2);
//			upgradePawn("B",6, 3);
//			upgradePawn("Q",6, 4);
//			upgradePawn("R",6, 5);
			
			//NEW AI
			int CPULevel = 4;
			ai = new AI(chessBoard, pieces, 1, CPULevel); //it doesn't like higher than 6 (6 takes 7-10 seconds);
			ai2 = new AI(chessBoard, pieces, 0, CPULevel);
			int[][] ai_move = ai.get_move(chessBoard, pieces);
			System.out.println("Current location: " + ai_move[0][0] + ", " + ai_move[0][1]);
			System.out.println("New location    : " + ai_move[1][0] + ", " + ai_move[1][1]);
			
			
			boolean gameOver = false;
			currentPlayer = 0; // player 0 is white, 1 black
			//update_player(currentPlayerLocal);
			while (!gameOver) {
				//refresh board

				turnOver = false;
				update_all_pieces();
				update_board();
				
				//check for checkmate or check
				if (is_in_check(currentPlayer)){
					System.out.println("Player "+ currentPlayer + " is in check!");
				}
				//get player 1 or 2 move
				//if(!turnInProgress()) {//!getMove(currentPlayer)) { // old
					//boolean playerMoveisLeagal = false;
				if(userInterface.gameStarted) {
					if(player0AiOn && currentPlayer == 0) {
						userInterface.whiteStartTime = System.nanoTime();
						currentPlayerAiMove(0);
//						userInterface.dualAiWorking0On();
					}
					if(player1AiOn && currentPlayer == 1) {
						userInterface.blackStartTime = System.nanoTime();
						currentPlayerAiMove(1);
//						userInterface.dualAiWorking1On();
					}
				}
					
					while (turnInProgress() == false) {//!playerMoveisLeagal) {
						//get move
						//playerMoveisLeagal = getMove(currentPlayer);// old
						// re validate
						//playerMoveisLeagal = ui.isTurnOver();
						doNothing();
					}
					if(turnInProgress()) {
						System.out.println("Player turn over: "+ currentPlayer);
					}
				//}

				// update state
				update_all_pieces();
				update_board();
				printVirtualBoard();
				ui.repaint();
				if (currentPlayer == 0) {					
					//currentPlayerGlobal = 1;
					currentPlayer = 1;
				}
				else {
					currentPlayer = 0;
				}
				//update_player(currentPlayer);
				ui.changeCurrentPlayer();
				// check for checkmate
				if (is_in_check(currentPlayer)){
					if (is_in_checkmate(currentPlayer)){
						gameOver = true;
						System.out.println("Game over!" );
						if (currentPlayer == 0) {					
							//currentPlayerGlobal = 1;
							currentPlayer = 1;
						}
						else {
							currentPlayer = 0;
						}
						ui.endGame(currentPlayer);
					}
				}

			}
			ui.repaint();
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	public static boolean turnInProgress() {
		//System.out.println("Player "+ currentPlayer + " turn is over!");
		return turnOver;
	 }

	 public static void endTurn() {
		 System.out.println("Ending turn...");
		turnOver = true;
	 }
	public static boolean getMove( int to_row, int to_col, int from_row,int from_col, int player) throws Exception{

		String piece = chessBoard[from_row][from_col];
		ChessPieces movingPiece = piece_at(from_row, from_col);
		// player can only move their pieces
		if (Character.isLowerCase(piece.charAt(0)) && player == 0) {
			//System.out.println("Invalid move, cannot move other player's pieces");
			return false;
		}
		if (Character.isUpperCase(piece.charAt(0)) && player == 1) {
			//System.out.println("Invalid move, cannot move other player's pieces");
			return false;
		}

		if ((player == 0 && Character.isLowerCase(chessBoard[to_row][to_col].charAt(0))) || player == 1 && Character.isUpperCase(chessBoard[to_row][to_col].charAt(0))){
			ChessPieces deadPiece = piece_at(to_row, to_col);
			if (attack(from_row, from_col, to_row, to_col)) {
				// update all moves 
				update_all_pieces();
				if (is_in_check(player)) {
					// undo
					undo_board();
					movingPiece.setLocation(from_row, from_col);
					deadPiece.setLocation(to_row, to_col);
					deadPiece.revive();
					update_all_pieces();
			
					return false;
				}
				return true;
			} else {
				System.out.println("Invalid attack");
				return false;
				}
		}
		if (move(from_row, from_col, to_row, to_col, chessBoard[from_row][from_col])) {
			update_all_pieces();
			if (is_in_check(player)) {
				// undo
				undo_board();
				movingPiece.setLocation(from_row, from_col);
				update_all_pieces();
				//System.out.println("Invalid move: King in check!");
				return false;
			}
			return true;
		}

		return false;
	}

	public static boolean getMove(int player) throws Exception{
		System.out.println("Enter row & col of the piece you would like to move (sperated by a space):");
		Scanner sc = new Scanner(System.in);
		int input, to_row, to_col, from_row, from_col;
		from_row = sc.nextInt();
		//System.out.println("Enter col # of the piece you would like to move:");
		from_col = sc.nextInt();
		// check if thats the player's piece
		String piece = chessBoard[from_row][from_col];
		ChessPieces movingPiece = piece_at(from_row, from_col);
		// player can only move their pieces
		if (Character.isLowerCase(piece.charAt(0)) && player == 0) {
			System.out.println("Invalid move");
			return false;
		}
		if (Character.isUpperCase(piece.charAt(0)) && player == 1) {
			System.out.println("Invalid move");
			return false;
		}
		// get move
		System.out.println("Enter row & col of the location you would like to move to (sperated by a space):");
		to_row = sc.nextInt();
		//System.out.println("Enter col # of the location you would like to move to:");
		to_col = sc.nextInt();
		if ((player == 0 && Character.isLowerCase(chessBoard[to_row][to_col].charAt(0))) || player == 1 && Character.isUpperCase(chessBoard[to_row][to_col].charAt(0))){
			ChessPieces deadPiece = piece_at(to_row, to_col);
			if (attack(from_row, from_col, to_row, to_col)) {
				if (is_in_check(player)) {
					// undo
					undo_board();
					movingPiece.setLocation(from_row, from_col);
					deadPiece.setLocation(to_row, to_col);
					deadPiece.revive();
					return false;
				}
				return true;
			} else {
				System.out.println("Invalid attack");
				return false;
				}
		}
		if (move(from_row, from_col, to_row, to_col, chessBoard[from_row][from_col])) {
			if (is_in_check(player)) {
				// undo
				undo_board();
				movingPiece.setLocation(from_row, from_col);
				return false;
			}
			return true;
		}

		return false;
	}

	public static void undo_board() {
		for (int i=0; i<8;i++) {
			for (int j=0; j<8;j++) {
				chessBoard[i][j] = prevBoard[i][j];
			}
		}
	}

	public static void update_board() {
		for (int i=0; i<8;i++) {
			for (int j=0; j<8;j++) {
				prevBoard[i][j] = chessBoard[i][j];
			}
		}
	}

		public static boolean move(int from_row, int from_col, int to_row, int to_col, String pieceChar) throws Exception{
			// find which piece belongs in that spot
			int[] to_location = {to_row, to_col};
			for (int i = 0; i < 32; i++) {
				if(pieces[i].locationMatches(from_row, from_col) && pieces[i].get_state() == 0) {
					// execute move if legal
					//System.out.println("Testing move");
					if (pieces[i].move(to_location, chessBoard)) {
						// update virtual board
						chessBoard[to_row][to_col] = pieceChar;
						chessBoard[from_row][from_col] = " ";
						return true;
					}else {
						System.out.println("move not legal!");
						pieces[i].listPossibleMoves();
					}
				}
			}
			return false;
		}

		public static boolean attack(int from_row, int from_col, int to_row, int to_col) throws Exception{
			// find which piece belongs in that spot
			int[] to_location = {to_row, to_col};
			ChessPieces victim = piece_at(to_row, to_col);
			ChessPieces attacker = piece_at(from_row, from_col);
			if (attacker != null && victim != null) {
				// execute attack
				for (int i = 0; i < 32; i++) {
					if(pieces[i].locationMatches(from_row, from_col) && pieces[i].get_state() == 0) {
						if (pieces[i].attack(to_location, chessBoard)) {
							capturedPieces.add(victim);
							victim.kill();
							chessBoard[to_row][to_col] = chessBoard[from_row][from_col];
							chessBoard[from_row][from_col]=" ";
							update_all_pieces();
							return true;
						}else {
							pieces[i].listPossibleMoves();
						}
					}
				}
			} else {System.out.println("Trouble finding pieces");}
			return false;
		}

		public static ChessPieces piece_at(int row, int col) {
			for (int i = 0; i < 32; i++) {
				if(pieces[i].locationMatches(row, col)) {
					return pieces[i];
				}
			}
			System.out.println("WARNING: Piece not found");
			return null;
		}

		public static void resetBoard() throws Exception {
		int[] coord = {0,0};
		ChessPieces r1 = new Rook(coord, 1, chessBoard);
		chessBoard[0][0] = "r";
		coord[1]++;
		ChessPieces n1 = new Knight(coord, 1, chessBoard);
		chessBoard[0][1] = "n";
		coord[1]++;
		ChessPieces b1 = new Bishop(coord, 1, chessBoard);
		chessBoard[0][2] = "b";
		coord[1]++;
		ChessPieces q = new Queen(coord, 1, chessBoard);
		chessBoard[0][3] = "q";
		coord[1]++;
		ChessPieces k = new King(coord, 1, chessBoard);
		chessBoard[0][4] = "k";
		coord[1]++;
		ChessPieces b2 = new Bishop(coord, 1, chessBoard);
		chessBoard[0][5] = "b";
		coord[1]++;
		ChessPieces n2 = new Knight(coord, 1, chessBoard);
		chessBoard[0][6] = "n";
		coord[1]++;
		ChessPieces r2 = new Rook(coord, 1, chessBoard);
		chessBoard[0][7] = "r";


		coord[0] = 1;
		coord[1] = 0;

		ChessPieces p1 = new Pawn(coord, 1, chessBoard);

		coord[1]++;
		//System.out.println("here");
		ChessPieces p2 = new Pawn(coord, 1, chessBoard);

		coord[1]++;
		ChessPieces p3 = new Pawn(coord, 1, chessBoard);
		coord[1]++;
		ChessPieces p4 = new Pawn(coord, 1, chessBoard);
		coord[1]++;
		ChessPieces p5 = new Pawn(coord, 1, chessBoard);
		coord[1]++;
		ChessPieces p6 = new Pawn(coord, 1, chessBoard);
		coord[1]++;
		ChessPieces p7 = new Pawn(coord, 1, chessBoard);
		coord[1]++;
		ChessPieces p8 = new Pawn(coord, 1, chessBoard);
		for(int i=0; i<=7;i++) {
			chessBoard[1][i] = "p";
		}

		coord[0] = 6;
		coord[1] = 0;

		ChessPieces P1 = new Pawn(coord, 0, chessBoard);
		coord[1]++;
		ChessPieces P2 = new Pawn(coord, 0, chessBoard);
		coord[1]++;
		ChessPieces P3 = new Pawn(coord, 0, chessBoard);
		coord[1]++;
		ChessPieces P4 = new Pawn(coord, 0, chessBoard);
		coord[1]++;
		ChessPieces P5 = new Pawn(coord, 0, chessBoard);
		coord[1]++;
		ChessPieces P6 = new Pawn(coord, 0, chessBoard);
		coord[1]++;
		ChessPieces P7 = new Pawn(coord, 0, chessBoard);
		coord[1]++;
		ChessPieces P8 = new Pawn(coord, 0, chessBoard);
		for(int i=0; i<=7;i++) {
			chessBoard[6][i] = "P";
		}
		coord[0] = 7;
		coord[1] = 0;
		ChessPieces R1 = new Rook(coord, 0, chessBoard);
		chessBoard[7][0] = "R";
		coord[1]++;
		ChessPieces N1 = new Knight(coord, 0, chessBoard);
		chessBoard[7][1] = "N";
		coord[1]++;
		ChessPieces B1 = new Bishop(coord, 0, chessBoard);
		chessBoard[7][2] = "B";
		coord[1]++;
		ChessPieces Q = new Queen(coord, 0, chessBoard);
		chessBoard[7][3] = "Q";
		coord[1]++;
		ChessPieces K = new King(coord, 0, chessBoard);
		chessBoard[7][4] = "K";
		coord[1]++;
		ChessPieces B2 = new Bishop(coord, 0, chessBoard);
		chessBoard[7][5] = "B";
		coord[1]++;
		ChessPieces N2 = new Knight(coord, 0, chessBoard);
		chessBoard[7][6] = "N";
		coord[1]++;
		ChessPieces R2 = new Rook(coord, 0, chessBoard);
		chessBoard[7][7] = "R";
		pieces[0] = r1;
		pieces[1] = r2;
		pieces[2] = R1;
		pieces[3] = R2;
		pieces[4] = n1;
		pieces[5] = n2;
		pieces[6] = N1;
		pieces[7] = N2;
		pieces[8] = b1;
		pieces[9] = b2;
		pieces[10] = B1;
		pieces[11] = B2;
		pieces[12] = q;
		pieces[13] = Q;
		pieces[14] = k;
		pieces[15] = K;
		pieces[16] = p1;
		pieces[17] = p2;
		pieces[18] = p3;
		pieces[19] = p4;
		pieces[20] = p5;
		pieces[21] = p6;
		pieces[22] = p7;
		pieces[23] = p8;
		pieces[24] = P1;
		pieces[25] = P2;
		pieces[26] = P3;
		pieces[27] = P4;
		pieces[28] = P5;
		pieces[29] = P6;
		pieces[30] = P7;
		pieces[31] = P8;
		// opposing_piece_indices = new int[] {0, 1, 4, 5, 8, 9, 12, 14, 16, 17, 18, 19, 20, 21, 22, 23};
		// opposing_piece_indices = new int[] {2, 3, 6, 7, 10, 11, 13, 15, 24, 25, 26, 27, 28, 29, 30, 31};
		update_all_pieces();
	}
	public static void printVirtualBoard() {
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				System.out.print("|");
				System.out.print(chessBoard[i][j]);
				System.out.print("|");
			}
			System.out.println("");
		}
	}

	public static void update_all_pieces() {
		for(int i = 0; i < pieces.length; i++) {
			if(pieces[i].get_state() == 1) {
				pieces[i].eraseMoves();
			}else {
				pieces[i].update_list_of_moves(chessBoard);
			}
		}
	}

	public static void update_player(int player) {
		currentPlayer = player;
	}

	public static void change_player() {
		//a binary switch for whose turn it currently is
		if (currentPlayer == 0) {
			currentPlayer = 1;
		}
		else if (currentPlayer == 1) {
			currentPlayer = 1;
		}
		else {
			System.out.println("Something went fucky wucky in the currentPlayer variable." );
		}

	}

	public static int get_player() {
		return currentPlayer;
	}

	public static boolean is_location_attacked(int player, int[] coordinates) throws Exception {
		int[] opposing_piece_indices;

		if(player == 0) {
			opposing_piece_indices = new int[] {0, 1, 4, 5, 8, 9, 12, 14, 16, 17, 18, 19, 20, 21, 22, 23};
		} else if(player == 1) {
			opposing_piece_indices = new int[] {2, 3, 6, 7, 10, 11, 13, 15, 24, 25, 26, 27, 28, 29, 30, 31};
		} else {
			throw new Exception("is_location_attacked(): Error: player not 0 or 1\n");
		}
		update_all_pieces();
		for(int i = 0; i < opposing_piece_indices.length; i++) {
			ArrayList<int[]> opposing_piece_movelist = pieces[opposing_piece_indices[i]].get_list_of_moves();
			for(int j = 0; j < opposing_piece_movelist.size(); j++) {
				if(Arrays.equals(coordinates, opposing_piece_movelist.get(j))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean is_in_checkmate(int player) throws Exception {
		update_all_pieces();
		update_board();
		for (int i = 0; i< 32; i++) {
			if (player == pieces[i].get_player()) {
				// get all moves for each players piece
				ArrayList<int[]> possibleMoves= pieces[i].get_list_of_moves();
				for (int j=0; j < possibleMoves.size(); j++) {
					int to_row = possibleMoves.get(j)[0];
					int to_col = possibleMoves.get(j)[1];
					int from_row = pieces[i].get_current_location()[0];
					int from_col = pieces[i].get_current_location()[1];
					ChessPieces movingPiece = piece_at(from_row, from_col);
					// try all moves, check for check
					// if a move removes a check, return false
					if (getMove( to_row, to_col, from_row, from_col, player)) {
						// undo move and return
						undo_board();
						movingPiece.setLocation(from_row, from_col);
						if ((player == 0 && Character.isLowerCase(chessBoard[to_row][to_col].charAt(0))) || player == 1 && Character.isUpperCase(chessBoard[to_row][to_col].charAt(0))){
							ChessPieces deadPiece = piece_at(to_row, to_col);
							deadPiece.setLocation(to_row, to_col);
							deadPiece.revive();
						}
						update_all_pieces();
						return false;
					}
				}
			}
		}
		if (player == 0) {
			System.out.println("Player 1 wins!" );
		}
		if (player == 1) {
			System.out.println("Player 0 wins!" );
		}
		undo_board();
		update_all_pieces();
		return true;
	}

	public static boolean is_in_check(int player) throws Exception {
		update_all_pieces();
		int[] king_position;

		if(player == 0) {
			king_position = pieces[15].get_current_location();
		} else if(player == 1) {
			king_position = pieces[14].get_current_location();
		} else {
			throw new Exception("is_in_check(): Error: player not 0 or 1\n");
		}

		if(is_location_attacked(player, king_position)) {
			return true;
		}
		return false;
	}

	public static boolean can_castle(int player, String side) throws Exception {

		if(!Objects.equals(side, "left") && !Objects.equals(side, "right")) {
			throw new Exception("can_castle(): Error: side not \"left\" or \"right\"\n");
		} else if(player != 0 && player != 1) {
			throw new Exception("can_castle():  Error: player not 0 or 1\n");
		}

		ChessPieces king;
		ChessPieces rook;
		ArrayList<int[]> squares_to_check = new ArrayList<int[]>();

		if(player == 0) {
			king = pieces[15];
			if(Objects.equals(side, "left")) {
				rook = pieces[2];
				squares_to_check.add(new int[] {7,1});
				squares_to_check.add(new int[] {7,2});
				squares_to_check.add(new int[] {7,3});
			} else { //Objects.equals(side, "right")
				rook = pieces[3];
				squares_to_check.add(new int[] {7,5});
				squares_to_check.add(new int[] {7,6});
			}
		} else { //player == 1
			king = pieces[14];
			if(Objects.equals(side, "left")) {
				rook = pieces[0];
				squares_to_check.add(new int[] {0,1});
				squares_to_check.add(new int[] {0,2});
				squares_to_check.add(new int[] {0,3});
			} else { //Objects.equals(side, "right")
				rook = pieces[1];
				squares_to_check.add(new int[] {0,5});
				squares_to_check.add(new int[] {0,6});
			}
		}
		//Cannot castle if either King or Rook has moved
		if(king.has_moved() || rook.has_moved()) {
			System.out.println();
			return false;
		}
		//Cannot castle if in check
		if(is_in_check(player)) {
			return false;
		}

		//Cannot castle if there are pieces between King and Rook
		for(int i = 0; i < squares_to_check.size(); i++) {
			if(chessBoard[squares_to_check.get(i)[0]][squares_to_check.get(i)[1]] != " ") {
				return false;
			}
		}
		//Cannot castle if there are attacks on spaces between King and Rook
		for(int i = 0; i < squares_to_check.size(); i++) {
			if(is_location_attacked(player, squares_to_check.get(i))) {
				return false;
			}
		}
		return true;
	}

	public static String board_at(int row, int col) {
		return chessBoard[row][col];
	}

	public static void doNothing() {
		//do nothing
		System.out.print("");
	};
	
	public static void upgradePawn(String promotionPiece, int row, int col) {
		int pieceArrayLocation = 0;
		int[] coordinates = {row,col};
		//find piece based on "from" coordinates
		for(int i = 0 ;  i < pieces.length ; i++ ) {
			//System.out.println( pieces[i].get_current_location()[0] + " " + pieces[i].get_current_location()[1] );
			if(pieces[i].get_current_location()[1] ==  col) {
				if(pieces[i].get_current_location()[0] ==  row) {
					//System.out.println ( pieces[i].type );
					//System.out.println(pieces[i].get_current_location()[0] + " " + pieces[i].get_current_location()[1]);
					pieceArrayLocation = i;
				}
			}
		}
		switch (promotionPiece) {
		case "N":
			try {
				pieces[pieceArrayLocation] = new Knight(coordinates, 0, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "n":
			try {
				pieces[pieceArrayLocation] = new Knight(coordinates, 1, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "Q":
			try {
				pieces[pieceArrayLocation] = new Queen(coordinates, 0, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "q":
			try {
				pieces[pieceArrayLocation] = new Queen(coordinates, 1, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "B":
			try {
				pieces[pieceArrayLocation] = new Bishop(coordinates, 0, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "b":
			try {
				pieces[pieceArrayLocation] = new Bishop(coordinates, 1, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		case "R":
			try {
				pieces[pieceArrayLocation] = new Rook(coordinates, 0, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "r":
			try {
				pieces[pieceArrayLocation] = new Rook(coordinates, 1, chessBoard);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		chessBoard[row][col] = promotionPiece;
		update_all_pieces();
		//replace piece with new piece class
		
		
	};
	
	public static void currentPlayerAiMove(int currentPlayerInput) {
		try {
			if(userInterface.gameMode == "pve") {
				userInterface.workingAiOn();
			}
			if(userInterface.gameMode == "eve") {
				if(currentPlayerInput == 0) {
					userInterface.dualAiWorking0On();
				} else { 
					userInterface.dualAiWorking1On();
				}
			}
		//int CPUlevel = 4;  //it doesn't like higher than 6 (6 takes 7-10 seconds);
		int[][] ai_move;
		if(currentPlayerInput == 1) {
			ai_move = ai.get_move(chessBoard, pieces);
			System.out.println("NEW AI MOVE");
			System.out.println("Current location: " + ai_move[0][0] + ", " + ai_move[0][1]);
			System.out.println("New location    : " + ai_move[1][0] + ", " + ai_move[1][1]);
		} else /*(currentPlayerInput == 0) */ {
//			int[][] ai_move;
			ai_move = ai2.get_move(chessBoard, pieces);
			System.out.println("NEW AI MOVE");
			System.out.println("Current location: " + ai_move[0][0] + ", " + ai_move[0][1]);
			System.out.println("New location    : " + ai_move[1][0] + ", " + ai_move[1][1]);
		} 
		
		int from_row = ai_move[0][0];
		int from_col = ai_move[0][1];
		int to_row = ai_move[1][0];
		int to_col = ai_move[1][1];
		
		if(!getMove(to_row, to_col, from_row, from_col, currentPlayer)) {
//			throw new Exception();
		}
		
		//updateBoard();
		endTurn();
		if(userInterface.gameMode == "pve") {
			userInterface.workingAiOff();
		}
		if(userInterface.gameMode == "eve") {
			if(currentPlayerInput == 0) {
				userInterface.dualAiWorking0Off();
				userInterface.whiteTurnTime = (System.nanoTime() - userInterface.whiteStartTime) / 1000000000;
			} else { 
				userInterface.dualAiWorking1Off();
			}
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
	
	static public void turnWhiteAiOn() {
		player0AiOn = true;
	};
	static public void turnBlackAiOn() {
		player1AiOn = true;
	};
	
	static public void firstPawnMove() {
		Random rand = new Random();
		int n = rand.nextInt(12);
		switch (n) {
		case 1:
			try {
				getMove(5, 0, 6, 0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				getMove(4, 0, 6, 0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				getMove(5, 1, 6, 1, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 4:
			try {
				getMove(4, 1, 6, 1, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 5:
			try {
				getMove(5, 2, 6, 2, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 6:
			try {
				getMove(4, 2, 6, 2, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 7:
			try {
				getMove(5, 3, 6, 3, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 8:
			try {
				getMove(4, 3, 6, 3, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 9:
			try {
				getMove(5, 4, 6, 4, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 10:
			try {
				getMove(4, 4, 6, 4, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 11:
			try {
				getMove(5, 5, 6, 5, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 12:
			try {
				getMove(4, 5, 6, 5, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		
	};
}
