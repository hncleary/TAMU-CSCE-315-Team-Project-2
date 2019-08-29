package TeamProject2;

import java.awt.*;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import java.awt.event.*;
//import java.util.ArrayList;
//import java.util.Vector;
//import java.io.File;

import javax.swing.*;

//import java.util.concurrent.TimeUnit;

public class userInterface extends JPanel implements MouseListener, MouseMotionListener{
	//mouse location
	static int x = 0,y = 0;
	static int mouseX, mouseY, newMouseX, newMouseY;

	static int squareSize = 100;
	boolean pieceDragging;
	static String draggedPiece;
	static int dragPieceX;
	static int dragPieceY;
	boolean turnOver = false;
	
	boolean aiState; 
	//developer toggles
	//decide whether or not to show the debug details printed as strings in the GUI
	boolean debugMode = false;
	//decide whether or not to have the AI toggle switch spawn on the game board
	boolean activateAISwitch = false;
	//debug numbers
	int totalMovesMade = 0; //total number of moves made in the User Inteface
	int currentPlayerUI = 0; //current player according to the User Interface, should correspond with 
	
	//GAME MODE switches / MENU switches 
	static boolean gameStarted = false;
	static boolean playerDebug = false;
	static String gameMode; //can equal "pvp", "pve", "client", "server" //added "eve"
	
	static boolean dualCPUButton = true;
	static boolean dualCPUModeOn = true;
	static boolean dualCPU0Working = false;
	static boolean dualCPU1Working = false;
	//modeAiOn || dualCPUModeOn
	static boolean modeAiOn = false;
	static boolean AiWorking = false;
	static boolean modeClientOn = false;
	static boolean modeServerOn = false;
	
	static boolean pawnPromotionShow = false;
	static String pawnPromotionPiece = "";
	static int pawnPromotionRow;
	static int pawnPromotionCol;
	
	//client mode variables and menu booleans
	static boolean clientMenuShow =  false;
	static String clientServerIpNumber = "127.0.0.1"; //defaults to loopback ip
	static String clientServerPortNumber = "5000";
	static int maxMoveTimeClient = 5000;
	
	//server mode variables and menu booleans
	static boolean serverMenuShow = false;
	static boolean serverModeAi = false;
	static boolean serverModePlayer = false;
	static String serverPortNumber = "5000";	
	
	//Game Ending conditions
	static boolean gameOver = false;
	//default winner of the game, should change based on board conditions
	static String winner = "white";
	
	//player timers
	static long  whiteTurnTime = 0;
	static long  blackTurnTime = 0;
	
	static long blackStartTime;
	static long blackEndTime;
	static long whiteStartTime;
	static long whiteEndTime;
	
	int boxLift = -55;
	
	static boolean whiteTimerStarted = false;
	static boolean blackTimerStarted = false;
	
	static boolean firstMove = true;

	
//	static int testVariable = 0;

	public void paintComponent(Graphics g) {
		//draws background
		super.paintComponent(g);
		//creates a new color using RGB
		this.setBackground(new Color(100,175,50) );
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		Image Background;
		Background = new ImageIcon("BackgroundTexture2.png").getImage();
		g.drawImage(Background, 0, 0, 1920, 1080, this);
		
		//draw menu items if the game has not been started yet
		if(!gameStarted && !clientMenuShow && !serverMenuShow) {
			Image MenuButtons = new ImageIcon("MenuButtons.png").getImage();
			g.drawImage(MenuButtons, 0,0, 1670, 1000, this);
			//gifs work with swing
//			Image chessGif = new ImageIcon("piecegif.gif").getImage();
//			g.drawImage(chessGif,0 , 0 , 400 , 400, this); 
			if(dualCPUButton) {
				Image CPUvCPUButton = new ImageIcon("dualCpuButton.png").getImage();
				g.drawImage(CPUvCPUButton, 0,0, 1670, 1000, this);
			}
		}
		if(clientMenuShow) {
			Image clientMenu = new ImageIcon("clientMenu.png").getImage();
			g.drawImage(clientMenu, 0,0, 1670, 1000, this);
		}
		if (serverMenuShow) {
			Image serverMenu = new ImageIcon("serverMenu.png").getImage();
			g.drawImage(serverMenu, 0,0, 1670, 1000, this);
		}
		
		//don't draw game items until the game mode is selected / game is started
		if(gameStarted) {
				Image ChessboardTexture;
				ChessboardTexture = new ImageIcon("ChessBoardTexture.png").getImage();
				g.drawImage(ChessboardTexture, 0, 0, this);
		
				Image currentTurnImage;
				if( Chess.currentPlayer == 0) {
					currentTurnImage = new ImageIcon("WhiteTurn.png").getImage();
				}
				else {
					currentTurnImage = new ImageIcon("BlackTurn.png").getImage();
				}
				g.drawImage(currentTurnImage, 1100, 0 + boxLift, 450, 450,  this);
		
				Image blackTimer;
				Image whiteTimer;
				blackTimer = new ImageIcon("BlackTimer.png").getImage();
				whiteTimer = new ImageIcon("WhiteTimer.png").getImage();
				g.drawImage(blackTimer, 940, 220 + boxLift, 400, 400,  this);
				g.drawImage(whiteTimer, 1320, 220 + boxLift, 400, 400,  this);
				
				//print time taken from previous turns onto timer blocks
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier", Font.PLAIN, 64));
				
				//starting the black timer if its blacks turn and it hasn't been started
				if(!blackTimerStarted) {
					if(currentPlayerUI == 1) {
						blackStartTime =  System.nanoTime();
						blackTimerStarted = true;
					}
				}
				//starting the white timer it its whites turn and it hasn't been started
				if(blackTimerStarted) {
					if(currentPlayerUI  == 0) {
						blackEndTime = System.nanoTime();
						blackTurnTime = (blackEndTime - blackStartTime) / 1000000000;
						repaint();
						blackTimerStarted = false;
					}
				}
				//starting the white timer if its whites turn and it hasn't been started
				if(!whiteTimerStarted) {
					if(currentPlayerUI == 0) {
						whiteStartTime = System.nanoTime();
						whiteTimerStarted = true;
					}
				}
				//stopping the white timer when it switched to blacks turn
				if(whiteTimerStarted) {
					if(currentPlayerUI == 1) {
						whiteEndTime = System.nanoTime();
						whiteTurnTime = (whiteEndTime - whiteStartTime) / 1000000000;
						repaint();
						whiteTimerStarted = false;
					}
				}
				if(blackTurnTime == 0 && firstMove == true) {
					g.setFont(new Font("Courier", Font.PLAIN, 24));
					g.drawString("No Move Yet", 1030,  335);
					firstMove = false;
				} /*else if (!firstMove && (modeAiOn || dualCPUModeOn) ) {
					g.drawString("<1 sec", 1030,  350);
				} */else {
					g.setFont(new Font("Courier", Font.PLAIN, 64));
					g.drawString(blackTurnTime + "secs", 1030,  350);
				}
				if(whiteTurnTime == 0 && firstMove == true) {
					g.setFont(new Font("Courier", Font.PLAIN, 24));
					g.drawString("No Move Yet", 1405,  335);
				}/*else if (!firstMove && (modeAiOn || dualCPUModeOn)) {
					g.setFont(new Font("Courier", Font.PLAIN, 64));
					g.drawString("<1 sec", 1405,  350);
					
				}*/else {
					g.setFont(new Font("Courier", Font.PLAIN, 64));
					g.drawString(whiteTurnTime + "secs", 1405,  350);
					firstMove = false;
				}
				
				//gives info on king in check status
				Image infoBox;
				try {
					if ( Chess.is_in_check(0) ) {
						infoBox = new ImageIcon("whiteKingInCheck.png").getImage();
					} else if (Chess.is_in_check(1)) {
						infoBox = new ImageIcon("blackKingInCheck.png").getImage();
					}
					else {
						infoBox = new ImageIcon("info.png").getImage();
					}
					g.drawImage(infoBox, 1130, 420 + boxLift, 400, 400,  this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//				if(activateAISwitch) {
//					Image AIState;
//					if (aiState == true) {
//						AIState = new ImageIcon("AIOn2.png").getImage();
//					} else {
//						AIState = new ImageIcon("AIOff2.png").getImage();
//					}
//					g.drawImage(AIState, 960, 225, 450, 450, this);
//				}
				
				if(gameMode == "pve") {
					modeAiOn = true;
				}
				if(modeAiOn) {
					Image AIStatus;
					if (AiWorking ){
						AIStatus = new ImageIcon("aiThoughtPanelWorking.png").getImage();
					} else {
						AIStatus = new ImageIcon("aiThoughtPanelWaiting.png").getImage();
					}
					g.drawImage(AIStatus, 1050, 600, 550, 550, this);
				}
				if(gameMode == "eve") {
					dualCPUModeOn = true;
				}
				if(dualCPUModeOn && !modeAiOn && gameMode != "pvp") {
					Image AIStatus0;
					if (dualCPU1Working ){
						AIStatus0 = new ImageIcon("aiThoughtPanelBlackOn.png").getImage();
					} else {
						AIStatus0 = new ImageIcon("aiThoughtPanelBlackOff.png").getImage();
					}
					g.drawImage(AIStatus0, 1045, 600, 550, 550, this);
					Image AIStatus1;
					if (dualCPU0Working ){
						AIStatus1 = new ImageIcon("aiThoughtPanelWhiteOn.png").getImage();
					} else {
						AIStatus1 = new ImageIcon("aiThoughtPanelWhiteOff.png").getImage();
					}
					g.drawImage(AIStatus1, 1345, 600, 550, 550, this);
				}
		
				for (int i = 0 ; i < 64; i += 2) {
					//draw chess board checkers
					g.setColor(new Color(255,200,100));
					g.fillRect( (i%8+(i/8)%2)*squareSize , (i/8)*squareSize , squareSize, squareSize);
					g.setColor(new Color(130,50,30));
					g.fillRect(((i+1)%8-((i+1)/8)%2)*squareSize , ((i+1)/8)*squareSize , squareSize, squareSize);
				}
		}

//		g.setColor(Color.YELLOW);
//		g.fillRect(240, 240, 200, 200);
		g.setColor(Color.BLUE);
//		g.fillRect(x-20, y-20, 40,40);
		
		if (debugMode) {
			int textOffset = -400;
			int textOffsetX = 75;
			g.setColor(Color.RED);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
			g.drawString("<---Debug Stats--->", 1080  + textOffsetX,  squareSize * 8 + 21 + textOffset);
			g.drawString("Current Player According to Chess.java Variable: " + currentPlayerUI, 1080  +textOffsetX,  squareSize * 8 + 48 + textOffset);
			g.drawString("Total Dragged Moves Made: " + totalMovesMade, 1080  +textOffsetX,  squareSize * 8 + 75 + textOffset);
			g.drawString("Current Mouse X Position: " + x, 1080 +textOffsetX, squareSize * 8 + 102 + textOffset);
			g.drawString("Current Mouse Y Position: " + y, 1080 + textOffsetX, squareSize * 8 + 129 + textOffset);
			String AiStateString;
			if(aiState) {
				AiStateString = "True";
			} else {
				AiStateString = "False";
			}
			g.drawString("Current AI Switch State: " + AiStateString, 1080 + textOffsetX, squareSize * 8 + 156 + textOffset);
			g.drawString("Current Game Mode Selected: " + gameMode , 1080 + textOffsetX, squareSize * 8 + 183+ textOffset);
			try {
				g.drawString("White King in check: " + Chess.is_in_check(0), 1080 + textOffsetX, squareSize * 8 + 210 + textOffset);
				g.drawString("Black King in check: " + Chess.is_in_check(1), 1080 + textOffsetX, squareSize * 8 + 237 + textOffset);
				if (Chess.is_in_checkmate(0)) {
					//g.drawString("Player 1 wins! " + Chess.is_in_check(1), 1080 + textOffsetX, squareSize * 8 + 237 + textOffset);
				}
				if (Chess.is_in_checkmate(1)) {
					//g.drawString("Player 0 wins! " + Chess.is_in_check(1), 1080 + textOffsetX, squareSize * 8 + 237 + textOffset);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//g.drawString("System Nano Time (seconds) : " + System.nanoTime() / 1000000000 , 1080 + textOffsetX, squareSize * 8 + 264 + textOffset);
			
		}
		if(playerDebug && gameStarted) {
			//g.drawString("<---Debug Stats--->", 1080  + textOffsetX,  squareSize * 8 + 21 + textOffset);
			g.setColor(Color.WHITE);
			g.fillRect(945, 10, 100, 930);
			g.setColor(Color.RED);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
			for( int i = 0 ; i < Chess.pieces.length ; i++) {
				g.drawString(Chess.pieces[i].get_type() + "" + Chess.pieces[i].get_player() + " = " + Chess.pieces[i].get_state()  , 955 ,  35 + 27*i);
			}
		}
			
		
		Image chessPieceImage;
		chessPieceImage = new ImageIcon("ChessPiecesNew.png").getImage();

		Image currentDraggedPiece;
		currentDraggedPiece = new ImageIcon("ChessPiecesNewHalfTransparent.png").getImage();
		int m = dragPieceX;
		int l = dragPieceY;
		if (pieceDragging) {
			g.drawImage(currentDraggedPiece,x-50, y-50,x+squareSize-50, y+squareSize-50, m*327, l*327, (m+1)*327, (l+1)*327,this);
		}
		
		if (gameStarted) {
			//placing chess pieces
			for( int i = 0 ; i < 64 ; i++) {
				int j = -1;
				int k = -1;
				switch (Chess.chessBoard[i/8][i%8]) {
					case "P":
						j = 5;
						k = 0;
						break;
					case "p":
						j = 5;
						k = 1;
						break;
					case "R":
						j = 2;
						k = 0;
						break;
					case "r":
						j = 2;
						k = 1;
						break;
					case "N":
						j = 4;
						k = 0;
						break;
					case "n":
						j = 4;
						k = 1;
						break;
					case "B":
						j = 3;
						k = 0;
						break;
					case "b":
						j = 3;
						k = 1;
						break;
					case "Q":
						j = 1;
						k = 0;
						break;
					case "q":
						j = 1;
						k = 1;
						break;
					case "K":
						j = 0;
						k = 0;
						break;
					case "k":
						j = 0;
						k = 1;
						break;
				}
				if(j != -1 || k != -1) {
					g.drawImage(chessPieceImage,(i% 8)*squareSize, (i/8)*squareSize, (i%8+1)*squareSize, (i/8+1)*squareSize, j*327, k*327, (j+1)*327, (k+1)*327,this);
				}
			}
		}
		//pawn promotion selection menu
		if(gameStarted) {
			if(pawnPromotionShow) {
				Image PawnPromotion;
				//pawn promotion skins pieces differently depending on the current player taking their turn
				if(currentPlayerUI == 0) {
					PawnPromotion = new ImageIcon("PawnPromotionBlack.png").getImage();

				}
				else {
					PawnPromotion = new ImageIcon("PawnPromotion.png").getImage();
				}
				g.drawImage(PawnPromotion, 270, 0, 1080, 1080, this);
				//if shown allow click selection from the user
			}
		}
		//checks to see if one of the players is in checkmate
		try {
			if(gameStarted) {
				if ( Chess.is_in_checkmate(1) ) {
					winner = "white";
					gameOver = true;
				}
				if (Chess.is_in_checkmate(0)) {
					winner = "black";
					gameOver = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//winner screen display
		if(gameStarted) {
			if(gameOver) {
				if(winner == "white") {
					//white has won the game
					Image winnerDisplay = new ImageIcon("whiteWinsCrowned.png").getImage();
					g.drawImage(winnerDisplay,0, 0, 1720, 1080, this);
				} else {
					//black has won the game
					Image winnerDisplay = new ImageIcon("blackWinsCrowned.png").getImage();
					g.drawImage(winnerDisplay,0, 0, 1720, 1080, this);
					
				}
			}
		}

	}
	public void endGame(int winningPlayer) {
		gameOver = true;
		if (winningPlayer == 0) {
			winner = "white";
		}else {
			winner = "black";
		}
	}
	//Mouse Event Handlers
	public void mouseMoved(MouseEvent e) {
		//turnOver = false;
		x = e.getX();
		y = e.getY();
		//you need this for debugging
		//but comment it out when finished to increase CPU
		//performance by 2x
		if(debugMode) {
			repaint();
		}
	};
	public void mousePressed(MouseEvent e) {
		//turnOver = false;
		//make sure that mouse is inside board
		//System.out.println("Mouse Pressed");
		repaint();
		//menu button selection
		if(!gameStarted && !clientMenuShow && !serverMenuShow) {
			//player vs player button
			if (e.getX() > 530 && e.getX() < 1145 
					&& e.getY() > 275 && e.getY() < 435) {
				gameStarted = true;
				gameMode = "pvp";
			}
			//player vs cpu button
			if (e.getX() > 530 && e.getX() < 1145 
					&& e.getY() > 475 && e.getY() < 645) {
				gameStarted = true;
				gameMode = "pve";
				//turns the AI on for the black pieces
				Chess.turnBlackAiOn();
				repaint();
			}
			//client button
			if (e.getX() > 600 && e.getX() < 1065 
					&& e.getY() > 670 && e.getY() < 790) {
				//gameStarted = true;
				gameMode = "client";
				clientMenuShow = true;
				repaint();
			}
			//server button
			if (e.getX() > 600 && e.getX() < 1065 
					&& e.getY() > 815 && e.getY() < 955) {
				//gameStarted = true;
				gameMode = "server";
				serverMenuShow = true;
				repaint();
			}
			if(dualCPUButton) {
				if (e.getX() > 1265 && e.getX() < 1600 
						&& e.getY() > 715 && e.getY() < 960) {
					gameStarted = true;
					gameMode = "eve";
					Chess.turnBlackAiOn();
					Chess.turnWhiteAiOn();
					repaint();
					try {
						Chess.getMove(4, 3, 6, 3, 0);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//Chess.firstPawnMove();
					Chess.endTurn();
				}
			}
		}
		if (clientMenuShow) {
			if (e.getX() > 600 && e.getX() < 1060 
					&& e.getY() > 770 && e.getY() < 910) {
				//gameStarted = true;
				gameMode = "client";
				gameStarted = true;
				clientMenuShow = false;
				repaint();
			}
		}
		if ( serverMenuShow ) {
			if (e.getX() > 600 && e.getX() < 1060 
					&& e.getY() > 770 && e.getY() < 910) {
				//gameStarted = true;
				gameMode = "server";
				gameStarted = true;
				serverMenuShow = false;
				repaint();
			}
		}
		//if one of the menu buttons has already been clicked
		if(gameStarted) {
			//during pawn promotion selection screen
			if(pawnPromotionShow) {
				//keep menu open while pawnPromotionPiece == ""
				//select knight
				if (e.getX() > 515 && e.getX() < 770 
						&& e.getY() > 295 && e.getY() < 540) {
					
					repaint();
					System.out.println("Knight was selected from promotion menu.");
					//promotion selection depends on current player being promoted
					if ( currentPlayerUI == 0) {
						pawnPromotionPiece = "n";
					} else {
						pawnPromotionPiece = "N";
					}
					System.out.println(pawnPromotionRow + " " + pawnPromotionCol);
					Chess.upgradePawn(pawnPromotionPiece, pawnPromotionRow, pawnPromotionCol);
					pawnPromotionPiece = "";
					pawnPromotionShow = false;
				}
				//select queen
				if (e.getX() > 835 && e.getX() < 1090 
						&& e.getY() > 295 && e.getY() < 540) {
					
					repaint();
					System.out.println("Queen was selected from promotion menu.");
					//promotion selection depends on current player being promoted
					if ( currentPlayerUI == 0) {
						pawnPromotionPiece = "q";
					} else {
						pawnPromotionPiece = "Q";
					}
					Chess.upgradePawn(pawnPromotionPiece, pawnPromotionRow, pawnPromotionCol);
					pawnPromotionPiece = "";
					pawnPromotionShow = false;
				}
				
				//select bishop
				if (e.getX() > 515 && e.getX() < 770 
						&& e.getY() > 585 && e.getY() < 835) {
					
					repaint();
					System.out.println("Bishop was selected from promotion menu.");
					//promotion selection depends on current player being promoted
					if ( currentPlayerUI == 0) {
						pawnPromotionPiece = "b";
					} else {
						pawnPromotionPiece = "B";
					}
					Chess.upgradePawn(pawnPromotionPiece, pawnPromotionRow, pawnPromotionCol);
					pawnPromotionPiece = "";
					pawnPromotionShow = false;
				}
				
				//select rook
				if (e.getX() > 835 && e.getX() < 1090 
						&& e.getY() > 585 && e.getY() < 835) {
					
					repaint();
					System.out.println("Rook was selected from promotion menu.");
					//promotion selection depends on current player being promoted
					if ( currentPlayerUI == 0) {
						pawnPromotionPiece = "r";
					} else {
						pawnPromotionPiece = "R";
					}
					Chess.upgradePawn(pawnPromotionPiece, pawnPromotionRow, pawnPromotionCol);
					pawnPromotionPiece = "";
					pawnPromotionShow = false;
				}
			}
			else {
			//not during pawn promotion selection screen
				if(activateAISwitch) {
					//location of AI switch
					if (e.getX() > 1135 && e.getX() < 1235 
							&& e.getY() > 475 && e.getY() < 590) {
						//switch the AI switch 
						if(aiState == false) {
							aiState = true;
						} else {
							aiState = false;
						}
						
					}
				}
		
				if (e.getX() < 8*squareSize && e.getY() < 8*squareSize ) {
					mouseX = e.getX();
					mouseY = e.getY();
					repaint();
					if (e.getX() < 8*squareSize && e.getY() < 8*squareSize ) {
						//getting the type of piece that is currently being clicked on
						draggedPiece = Chess.chessBoard[mouseY/squareSize][mouseX/squareSize] ;
						//System.out.println(draggedPiece);
					}
					else {
						draggedPiece = "";
					}
				}
			}
		}
	};

	public boolean isTurnOver() {
		repaint();
		return turnOver;
	}

	public void mouseReleased(MouseEvent e){
		
		//turnOver = false;
		//make sure that mouse is inside board
	if(pieceDragging){
		try {
		int from_row= 0, from_col = 0, to_row = 0, to_col = 0, player = 0;
		String pieceChar = "";
		//System.out.println("Moused Released");
		if (e.getX() < 8*squareSize && e.getY() < 8*squareSize ) {
			newMouseX = e.getX();
			newMouseY = e.getY();

			//if it is a left click
			if(e.getButton() == MouseEvent.BUTTON1) {
				String dragMove;
				if (newMouseY/squareSize == 0 && mouseY/squareSize==1 && "P".equals(Chess.chessBoard[mouseY/squareSize][mouseX/squareSize])
						|| newMouseY/squareSize == 7 && mouseY/squareSize==6 && "p".equals(Chess.chessBoard[mouseY/squareSize][mouseX/squareSize]) ) {
					
					//regular move data
					from_row = mouseY/squareSize;
					from_col = mouseX/squareSize;
					to_row = newMouseY/squareSize;
					to_col = newMouseX/squareSize;
					pieceChar = Chess.board_at(mouseY/squareSize, mouseX/squareSize);
					player = Chess.get_player();
					dragMove = "" + mouseY/squareSize + mouseX/squareSize + newMouseY/squareSize + newMouseX/squareSize + Chess.chessBoard[newMouseX/squareSize][newMouseX/squareSize];
					
					//pawn promotion
					//check to make sure that move is valid first
					if(!modeAiOn) {
						if(Chess.getMove(to_row, to_col, from_row, from_col, player)) {
							pawnPromotionPiece = "";
							System.out.println("User is now selecting their pawn promotion piece");
							if (pawnPromotionPiece == "") {
								pawnPromotionShow = true;
								repaint();
							}
							totalMovesMade++;
							System.out.println("Player "+ player + " turn is valid!");
							pawnPromotionRow = to_row;
							pawnPromotionCol = to_col;
							Chess.endTurn();
							//pawnUpgrade function is performed after menu click in the mouse pressed parameters						
						} else {
							//invalid move -- do nothing.
						}
					} else {
						//pawn promotion if the AI is playing 
						if(Chess.getMove(to_row, to_col, from_row, from_col, player)) {
							if(currentPlayerUI == 0) {
								pawnPromotionPiece = "Q";
							} else {
								pawnPromotionPiece = "q";
							}
							
							System.out.println("User is now selecting their pawn promotion piece");
							if (pawnPromotionPiece == "") {
								//pawnPromotionShow = true;
								repaint();
							}
							totalMovesMade++;
							System.out.println("Player "+ player + " turn is valid!");
							pawnPromotionRow = to_row;
							pawnPromotionCol = to_col;
							
							Chess.upgradePawn(pawnPromotionPiece, pawnPromotionRow, pawnPromotionCol);
							
							Chess.endTurn();
					}
					}
					//System.out.println("User has selected " + pawnPromotionPiece + " to promote their pawn with.");

					
					
				} else {
					//regular move total combination
					from_row = mouseY/squareSize;
					from_col = mouseX/squareSize;
					to_row = newMouseY/squareSize;
					to_col = newMouseX/squareSize;
					pieceChar = Chess.board_at(mouseY/squareSize, mouseX/squareSize);
					player = Chess.get_player();
					dragMove = "" + mouseY/squareSize + mouseX/squareSize + newMouseY/squareSize + newMouseX/squareSize + Chess.chessBoard[newMouseX/squareSize][newMouseX/squareSize];
					
					if(!Chess.getMove(to_row, to_col, from_row, from_col, player)) {
						//do not take input
					}else {
						totalMovesMade++;
						System.out.println("Player "+ player + " turn is valid!");
						Chess.endTurn();
						
					}
				}

					
			};
		}
		pieceDragging = false;
		repaint();
		} catch (Exception e1) {
					e1.printStackTrace();
				}



		//wait(100);
		
		
	}
	};
	public void mouseClicked(MouseEvent e) {

	};
	public void mouseDragged(MouseEvent e) {
		//turnOver = false;
		x = e.getX();
		y = e.getY();
		//pieceDragging = true;
		if(gameStarted) {
			repaint();
			if(draggedPiece == "") {
				//nothing
			}
			else {
				pieceDragging = true;
				int j = -1;
				int k = -1;
				switch(draggedPiece) {
					case "P":
						j = 5;
						k = 0;
						break;
					case "p":
						j = 5;
						k = 1;
						break;
					case "R":
						j = 2;
						k = 0;
						break;
					case "r":
						j = 2;
						k = 1;
						break;
					case "N":
						j = 4;
						k = 0;
						break;
					case "n":
						j = 4;
						k = 1;
						break;
					case "B":
						j = 3;
						k = 0;
						break;
					case "b":
						j = 3;
						k = 1;
						break;
					case "Q":
						j = 1;
						k = 0;
						break;
					case "q":
						j = 1;
						k = 1;
						break;
					case "K":
						j = 0;
						k = 0;
						break;
					case "k":
						j = 0;
						k = 1;
						break;
				}
				if(j != -1 || k != -1) {
	//				g.drawImage(chessPieceImage,x, y, 20, 20, j*327, k*327, (j+1)*327, (k+1)*327,this);
					dragPieceX = j;
					dragPieceY = k;
				}
			}
		}
		repaint();
	}


	public void mouseEntered(MouseEvent e) {

	};
	public void mouseExited(MouseEvent e) {

	};
	
	public void changeCurrentPlayer(){
		if(currentPlayerUI == 1) {
			currentPlayerUI = 0;
		}
		else {
			currentPlayerUI = 1;
		}
	};
	
	public void makeComputerMove() {
		//at the end of player 0's move call AI function
		//turn aistatus on
		//get current chess board
		//get move from ai
		//make move directed by ai
		//turn ai status off 
		//switch current turn
	};
	
	static public void workingAiOn() {
		AiWorking = true;
	};
	static public void workingAiOff() {
		AiWorking = false;
	};
	static public void dualAiWorking0On() {
		dualCPU0Working = true;
		if (dualCPU1Working) {
			dualAiWorking1Off();
		}
		
	};
	static public void dualAiWorking0Off() {
		dualCPU0Working = false;
	};
	static public void dualAiWorking1On() {
		dualCPU1Working = true;
		if (dualCPU0Working) {
			dualAiWorking0Off();
		}
	};
	static public void dualAiWorking1Off() {
		dualCPU1Working = false;
	};
	
	
	//drag conditions
	//if ai on and currentplayer != 0
	//conditions for ai vs ai
	
	
//	public void restartApplication()	
//	{
//	  final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
//	  final File currentJar = new File(userInt.class.getProtectionDomain().getCodeSource().getLocation().toURI());
//
//	  /* is it a jar file? */
//	  if(!currentJar.getName().endsWith(".jar"))
//	    return;
//
//	  /* Build command: java -jar application.jar */
//	  final ArrayList<String> command = new ArrayList<String>();
//	  command.add(javaBin);
//	  command.add("-jar");
//	  command.add(currentJar.getPath());
//
//	  final ProcessBuilder builder = new ProcessBuilder(command);
//	  builder.start();
//	  System.exit(0);
//	};
}
