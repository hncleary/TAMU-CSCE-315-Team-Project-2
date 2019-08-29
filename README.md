# CSCE-315-Team-Project-2

### How to play

###### Currently, our implementation takes move inputs by the user entering the coordinates of the piece they want to move and then entering the coordinates of where they want to move that piece into the terminal.
###### For Example:
```
|r||n||b||q||k||b||n||r|
|p||p||p||p||p||p||p||p|
| || || || || || || || |
| || || || || || || || |
| || || || || || || || |
| || || || || || || || |
|P||P||P||P||P||P||P||P|
|R||N||B||Q||K||B||N||R|
  Enter row & col of the piece you would like to move (sperated by a space):
> 6 4
  Enter row & col of the location you would like to move to (sperated by a space):
> 4 4
```
##### Would Output:
```
|r||n||b||q||k||b||n||r|
|p||p||p||p||p||p||p||p|
| || || || || || || || |
| || || || || || || || |
| || || || ||P|| || || |
| || || || || || || || |
|P||P||P||P|| ||P||P||P|
|R||N||B||Q||K||B||N||R|
```
##### In the terminal.
| SCRUM # | Date | Notes + Time|
| ------- | ---- | -------------------------------------------------------------------------------- |
| SCRUM 1 | 3-25-2019 | Backlog meeting - 15 mins |
| SCRUM 2 | 3-27-2019 | Chess manager meeting - 10 mins |
| SCRUM 3 | 3-28-2019 | Overall state of simple chess program - 20 mins |
| SCRUM 4 | 4-01-2019 | Sprint 2 Backlog meeting - 15 mins |
| SCRUM 5 | 4-03-2019 | Chess manager + GUI integration meeting - 20 mins |
| SCRUM 6 | 4-04-2019 | GUI integration meeting - 10 mins |
| SCRUM 7 | 4-08-2019 | Sprint 3 Backlog meeting - 10 mins |
| SCRUM 8 | 4-10-2019 | State of AI and integration - 20 mins |
| SCRUM 9 | 4-11-2019 | Client-server merging thoughts - 15 mins |

|Name	      | Date of Changes | Changes  |
| ------------- |:-------------:| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| Hung Bui      | 3-24-2019 	| Added document file detailing current backlog; Project backlog, initial burndown chart, sprint 1 backlog;|
| Hunter Cleary | 3-25-2019     |  Completed basic chess board representation, .png pieces will now update on the board based on the conditions of the chessboard[][] array|
| Hung Bui | 3-25-2019 |  Added initial chess piece classes; ChessPieces abstract class - other classes derive from it;|
| Hung Bui | 3-26-2019 |  Added definitions of chess piece movements, Castling. en passant, and king checks are currently not implemented in the moveset|
| Matthew Mora 	| 3-27-2019	| Added a board reset, a print virtual board and fixed some images |
| Hung Bui | 3-28-2019 | Fixed Pawn class moveset and added a few efficiencies |
| Hung Bui | 3-29-2019 | Added functions for checking when king in check and for castling |
| Hunter Cleary | 3-29-2019     | Updated graphics and drag move functionality. Added global variable for currentPlayer|
| Matthew Mora | 3-29-2019 | Added implementation for moving and attacking and added a new temporary way to get input |
| Hunter Cleary | 3-29-2019     | merged graphics textures and drag function for use in the master branch. will soon use "movePiece" function implementation|
| Hung Bui | 4-01-2019 | Modified ChessPieces and derivatives for AI - Created initial functions for AI + pseudocode for Minimax algorithm |
| Hunter Cleary | 4-09-19 | Added a large amount of Graphics and Functions for the graphics to display based on certain criteria|
| Hung Bui | 4-10-19 | Basic AI function mostly complete with alpha-beta pruning and AI checks |
| Hunter Cleary | 4-10-19 | Created Stalemate Graphic, updated timer graphics, added working timer for moves of both player, added pawn promotion menu and system, improved performace of the graphics engine
| Hung Bui | 4-10-19 | Started client-server operations - currently simple interaction|
| Hung Bui | 4-11-19 | Created foundational client-server functions - needs testing, very simple, server to client only, security risks possible |
| Hunter Cleary | 4-12-19 | Pawn Promotion Menu Wokring, added function that allows for upgrading pawn based on position on selected promotion piece. fixed other key issues
| Hunter Cleary | 4-13-19 | Added Function and User Interface graphics for playing against the AI. User interface displays whether or not the AI is currently calculating its move