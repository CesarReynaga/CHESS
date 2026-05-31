import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;


public class Chess extends JPanel implements Runnable {


    BufferedImage bRook;
    BufferedImage bKnight;
    BufferedImage bBishop;
    BufferedImage bQueen;
    BufferedImage bKing;
    BufferedImage bPawn;


    BufferedImage wRook;
    BufferedImage wKnight;
    BufferedImage wBishop;
    BufferedImage wQueen;
    BufferedImage wKing;
    BufferedImage wPawn;

    int screenWidth = 950;
    int screenHeight = 800;
    int mouseX;
    int mouseY;
    int tileSize = 100;


    int col = mouseX / tileSize;
    int row = mouseY / tileSize;


    int selectedRow = -1;
    int selectedCol = -1;
    boolean hasSelection = false;
    boolean clicked = false;
    boolean gameOver;
    boolean restart;


    boolean whiteTurn = true;
    boolean flipped = false;
    boolean visibility = false;
    String lastMove = "";
    int logY = 50;


    Piece[][] Piece = new Piece[8][8];
    String[][] squares = new String[8][8];
    boolean[][] firstMove = new boolean[8][8];


    boolean[][] isValidMove = new boolean[8][8];


    Thread gameThread;


    MouseHandler mouse;
    KeyHandler keyH = new KeyHandler();


    public Chess() {


        for (int row = 0; row < 8; row++) {


            for (int col = 0; col < 8; col++) {


                char file = (char) ('a' + col);
                int rank = 8 - row;


                squares[row][col] = "" + file + rank;
            }
        }
        for (int i = 0; i < 8; i++) {
            firstMove[1][i] = true; // black pawns
            firstMove[6][i] = true; // white pawns
        }




        Piece[0][0] = new Piece("Rook", false);
        Piece[0][1] = new Piece("Knight", false);
        Piece[0][2] = new Piece("Bishop", false);
        Piece[0][3] = new Piece("Queen", false);
        Piece[0][4] = new Piece("King", false);
        Piece[0][5] = new Piece("Bishop", false);
        Piece[0][6] = new Piece("Knight", false);
        Piece[0][7] = new Piece("Rook", false);


        for (int i = 0; i < 8; i++) {
            Piece[1][i] = new Piece("Pawn", false);
        }
        Piece[7][0] = new Piece("Rook", true);
        Piece[7][1] = new Piece("Knight", true);
        Piece[7][2] = new Piece("Bishop", true);
        Piece[7][3] = new Piece("Queen", true);
        Piece[7][4] = new Piece("King", true);
        Piece[7][5] = new Piece("Bishop", true);
        Piece[7][6] = new Piece("Knight", true);
        Piece[7][7] = new Piece("Rook", true);


        for (int i = 0; i < 8; i++) {
            Piece[6][i] = new Piece("Pawn", true);
        }
        try {
            bRook = ImageIO.read(Objects.requireNonNull(getClass().getResource("/blackRook.png")));
            bKnight = ImageIO.read(Objects.requireNonNull(getClass().getResource("/blackKnight.png")));
            bBishop = ImageIO.read(Objects.requireNonNull(getClass().getResource("/blackBishop.png")));
            bQueen = ImageIO.read(Objects.requireNonNull(getClass().getResource("/blackQueen.png")));
            bKing = ImageIO.read(Objects.requireNonNull(getClass().getResource("/blackKing.png")));
            bPawn = ImageIO.read(Objects.requireNonNull(getClass().getResource("/blackPawn.png")));
            // WHITE PIECES
            wRook = ImageIO.read(Objects.requireNonNull(getClass().getResource("/whiteRook.png")));
            wKnight = ImageIO.read(Objects.requireNonNull(getClass().getResource("/whiteKnight.png")));
            wBishop = ImageIO.read(Objects.requireNonNull(getClass().getResource("/whiteBishop.png")));
            wQueen = ImageIO.read(Objects.requireNonNull(getClass().getResource("/whiteQueen.png")));
            wKing = ImageIO.read(Objects.requireNonNull(getClass().getResource("/whiteKing.png")));
            wPawn = ImageIO.read(Objects.requireNonNull(getClass().getResource("/whitePawn.png")));


        } catch (Exception e) {
            e.printStackTrace();
        }


        mouse = new MouseHandler();


        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addKeyListener(keyH);


        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);


    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }


    @Override
    public void run() {


        while (gameThread != null) {


            update();
            repaint();


            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void update() {
        boolean captured;

        if (keyH.fPressed) {
            flipped = !flipped;
            keyH.fPressed = false;
            System.out.println("Flipped");
        }

        if (keyH.rPressed) {
            restart = !restart;
            keyH.rPressed = false;
        }

        if (restart) {
            restart(); // recommended (or replace with your reset code)
            restart = false;
        }

        if (!mouse.mousePressed) return;

        col = mouse.mouseX / tileSize;
        row = mouse.mouseY / tileSize;

        if (flipped) {
            col = 7 - col;
            row = 7 - row;
        }

        if (row < 0 || row > 7 || col < 0 || col > 7) {
            mouse.mousePressed = false;
            return;
        }

        if (!hasSelection) {
            if (Piece[row][col] != null && Piece[row][col].isWhite == whiteTurn) {

                selectedRow = row;
                selectedCol = col;
                hasSelection = true;
                visibility = true;

                initLegalMoves(Piece[row][col], row, col);

                System.out.println("Selected: " + Piece[row][col].type);
            }
        } else {

            Piece piece = Piece[selectedRow][selectedCol];

            if (piece == null) {
                hasSelection = false;
                mouse.mousePressed = false;
                return;
            }

            if (row == selectedRow && col == selectedCol) {
                hasSelection = false;
                visibility = false;
                mouse.mousePressed = false;
                return;
            }

            if (isValidMove[row][col]) {

                Piece movedPiece = Piece[selectedRow][selectedCol];
                Piece temp = Piece[row][col];

                Piece[row][col] = movedPiece;
                Piece[selectedRow][selectedCol] = null;

                boolean inCheck = isKingChecked(movedPiece.isWhite);

                if (inCheck) {
                    Piece[selectedRow][selectedCol] = movedPiece;
                    Piece[row][col] = temp;

                    System.out.println("Illegal move (king in check)");
                } else {

                    if (movedPiece.type.equals("Pawn")) {
                        firstMove[selectedRow][selectedCol] = false;
                    }

                    boolean oppColor = !movedPiece.isWhite;

                    if (isCheckmate(oppColor)) {
                        System.out.println("CHECKMATE : " + movedPiece.isWhite);
                        gameOver = true;
                    }

                    whiteTurn = !whiteTurn;

                   lastMove = logMoves(movedPiece, selectedRow, selectedCol, row, col);

                }

            } else {
                System.out.println("Illegal Move");
            }

            hasSelection = false;
            selectedRow = -1;
            selectedCol = -1;
            visibility = false;
            logY = logY +5;
        }

        mouse.mousePressed = false;
    }





    public void initLegalMoves(Piece piece, int row, int col) {


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                isValidMove[i][j] = false;
            }
        }


        ///
        // BLACKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
        ///
        if (piece.type.equals("Pawn") && !piece.isWhite) {


            // down 1
            if (row + 1 < 8 && Piece[row + 1][col] == null) {
                isValidMove[row + 1][col] = true;
            }


            // UP 2 if FIRST MOVE
            if (firstMove[row][col] && row + 2 < 8 &&
                    Piece[row + 1][col] == null &&
                    Piece[row + 2][col] == null) {


                isValidMove[row + 2][col] = true;
            }
            // TAKE LEFT


            if (row + 1 < 8 && col - 1 >= 0 &&
                    Piece[row + 1][col - 1] != null &&
                    Piece[row + 1][col - 1].isWhite) {


                isValidMove[row + 1][col - 1] = true;
            }
            // TAKE RIGHT


            if (row + 1 < 8 && col + 1 < 8 &&
                    Piece[row + 1][col + 1] != null &&
                    Piece[row + 1][col + 1].isWhite) {


                isValidMove[row + 1][col + 1] = true;
            }
        }
        ///
        // ROOKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
        ///

        // DOWN
        if (piece.type.equals("Rook")) {
            for (int r = row + 1; r < 8; r++) {
                if (Piece[r][col] == null) {
                    isValidMove[r][col] = true;
                } else {
                    if (Piece[r][col].isWhite != piece.isWhite) {
                        isValidMove[r][col] = true;
                    }
                    break;
                }
            }
            // UPPP
            for (int r = row - 1; r >= 0; r--) {
                if (Piece[r][col] == null) {
                    isValidMove[r][col] = true;
                } else {
                    if (Piece[r][col].isWhite != piece.isWhite) {
                        isValidMove[r][col] = true;
                    }
                    break;
                }

            }
            for (int c = col - 1; c >= 0; c--) {
                if (Piece[row][c] == null) {
                    isValidMove[row][c] = true;
                } else {
                    if (Piece[row][c].isWhite != piece.isWhite) {
                        isValidMove[row][c] = true;
                    }
                    break;
                }
            }
            // RIGHT
            for (int c = col + 1; c < 8; c++) {
                if (Piece[row][c] == null) {
                    isValidMove[row][c] = true;
                } else {
                    if (Piece[row][col].isWhite != piece.isWhite) {
                        isValidMove[row][c] = true;
                    }
                    break;
                }
            }


        }
        if (piece.type.equals("Bishop")) {
            for (int i = 1; row + i < 8 && col + i < 8; i++) {
                if (Piece[row + i][col + i] == null) {
                    isValidMove[row + i][col + i] = true;
                } else {
                    if (Piece[row + i][col + i].isWhite != piece.isWhite) {
                        isValidMove[row + i][col + i] = true;
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 0 && col + i < 8; i++) {
                if (Piece[row - i][col + i] == null) {
                    isValidMove[row - i][col + i] = true;
                } else {
                    if (Piece[row - i][col + i].isWhite != piece.isWhite) {
                        isValidMove[row - i][col + i] = true;
                    }
                    break;
                }

            }
            for (int i = 1; row - i >= 0 && col - i >= 0; i++) {
                if (Piece[row - i][col - i] == null) {
                    isValidMove[row - i][col - i] = true;
                } else {
                    if (Piece[row - i][col - i].isWhite != piece.isWhite) {
                        isValidMove[row - i][col - i] = true;
                    }
                    break;
                }

            }
            for (int i = 1; row + i < 8 && col - i >= 0; i++) {
                if (Piece[row + i][col - i] == null) {
                    isValidMove[row + i][col - i] = true;
                } else {
                    if (Piece[row + i][col - i].isWhite != piece.isWhite) {
                        isValidMove[row + i][col - i] = true;
                    }
                    break;
                }

            }

        }
        if (piece.type.equals("Queen")) {
            for (int r = row + 1; r < 8; r++) {
                if (Piece[r][col] == null) {
                    isValidMove[r][col] = true;
                } else {
                    if (Piece[r][col].isWhite != piece.isWhite) {
                        isValidMove[r][col] = true;
                    }
                    break;
                }
            }
            // UPPP
            for (int r = row - 1; r >= 0; r--) {
                if (Piece[r][col] == null) {
                    isValidMove[r][col] = true;
                } else {
                    if (Piece[r][col].isWhite != piece.isWhite) {
                        isValidMove[r][col] = true;
                    }
                    break;
                }

            }
            for (int c = col - 1; c >= 0; c--) {
                if (Piece[row][c] == null) {
                    isValidMove[row][c] = true;
                } else {
                    if (Piece[row][c].isWhite != piece.isWhite) {
                        isValidMove[row][c] = true;
                    }
                    break;
                }
            }
            // RIGHT
            for (int c = col + 1; c < 8; c++) {
                if (Piece[row][c] == null) {
                    isValidMove[row][c] = true;
                } else {
                    if (Piece[row][col].isWhite != piece.isWhite) {
                        isValidMove[row][c] = true;
                    }
                    break;
                }
            }
            for (int i = 1; row + i < 8 && col + i < 8; i++) {
                if (Piece[row + i][col + i] == null) {
                    isValidMove[row + i][col + i] = true;
                } else {
                    if (Piece[row + i][col + i].isWhite != piece.isWhite) {
                        isValidMove[row + i][col + i] = true;
                    }
                    break;
                }
            }
            for (int i = 1; row - i >= 0 && col + i < 8; i++) {
                if (Piece[row - i][col + i] == null) {
                    isValidMove[row - i][col + i] = true;
                } else {
                    if (Piece[row - i][col + i].isWhite != piece.isWhite) {
                        isValidMove[row - i][col + i] = true;
                    }
                    break;
                }

            }
            for (int i = 1; row - i >= 0 && col - i >= 0; i++) {
                if (Piece[row - i][col - i] == null) {
                    isValidMove[row - i][col - i] = true;
                } else {
                    if (Piece[row - i][col - i].isWhite != piece.isWhite) {
                        isValidMove[row - i][col - i] = true;
                    }
                    break;
                }

            }
            for (int i = 1; row + i < 8 && col - i >= 0; i++) {
                if (Piece[row + i][col - i] == null) {
                    isValidMove[row + i][col - i] = true;
                } else {
                    if (Piece[row + i][col - i].isWhite != piece.isWhite) {
                        isValidMove[row + i][col - i] = true;
                    }
                    break;
                }

            }


        }
        if (piece.type.equals("Knight")) {
            if (row + 1 < 8 && col + 2 < 8) {
                if (Piece[row + 1][col + 2] == null || Piece[row + 1][col + 2].isWhite != piece.isWhite) {
                    isValidMove[row + 1][col + 2] = true;
                }
            }
            if (row + 2 < 8 && col + 1 < 8) {
                if (Piece[row + 2][col + 1] == null || Piece[row + 2][col + 1].isWhite != piece.isWhite) {
                    isValidMove[row + 2][col + 1] = true;
                }
            }
            if (row - 2 >= 0 && col - 1 >= 0) {
                if (Piece[row - 2][col - 1] == null || Piece[row - 2][col - 1].isWhite != piece.isWhite) {
                    isValidMove[row - 2][col - 1] = true;
                }
            }
            if (row - 1 >= 0 && col - 2 >= 0) {
                if (Piece[row - 1][col - 2] == null || Piece[row - 1][col - 2].isWhite != piece.isWhite) {
                    isValidMove[row - 1][col - 2] = true;
                }
            }
            if (row + 1 < 8 && col - 2 >= 0) {
                if (Piece[row + 1][col - 2] == null || Piece[row + 1][col - 2].isWhite != piece.isWhite) {
                    isValidMove[row + 1][col - 2] = true;
                }
            }
            if (row - 1 >= 0 && col + 2 < 8) {
                if (Piece[row - 1][col + 2] == null || Piece[row - 1][col + 2].isWhite != piece.isWhite) {
                    isValidMove[row - 1][col + 2] = true;
                }
            }
            if (row - 2 >= 0 && col + 1 < 8) {
                if (Piece[row - 2][col + 1] == null || Piece[row - 2][col + 1].isWhite != piece.isWhite) {
                    isValidMove[row - 2][col + 1] = true;
                }
            }
            if (row + 2 < 8 && col - 1 >= 0) {
                if (Piece[row + 2][col - 1] == null || Piece[row + 2][col - 1].isWhite != piece.isWhite) {
                    isValidMove[row + 2][col - 1] = true;
                }
            }

        }
        if (piece.type.equals("King")) {
            if (row + 1 < 8 && (Piece[row + 1][col] == null || Piece[row + 1][col].isWhite != piece.isWhite)) {
                isValidMove[row + 1][col] = true;
            }
            if (row - 1 >= 0 && (Piece[row - 1][col] == null || Piece[row - 1][col].isWhite != piece.isWhite)) {
                isValidMove[row - 1][col] = true;
            }
            if (col + 1 < 8 && (Piece[row][col + 1] == null || Piece[row][col + 1].isWhite != piece.isWhite)) {
                isValidMove[row][col + 1] = true;
            }
            if (col - 1 >= 0 && (Piece[row][col - 1] == null || Piece[row][col - 1].isWhite != piece.isWhite)) {
                isValidMove[row][col - 1] = true;
            }
            if (row + 1 < 8 && (col + 1 < 8 && Piece[row + 1][col + 1] == null || Piece[row + 1][col + 1].isWhite != piece.isWhite)) {
                isValidMove[row + 1][col + 1] = true;
            }
            if (row - 1 >= 0 && (col + 1 < 8 && Piece[row - 1][col + 1] == null || Piece[row - 1][col + 1].isWhite != piece.isWhite)) {
                isValidMove[row - 1][col + 1] = true;
            }
            if (row + 1 < 8 && (col - 1 >= 0 && Piece[row + 1][col - 1] == null || Piece[row + 1][col - 1].isWhite != piece.isWhite)) {
                isValidMove[row + 1][col - 1] = true;
            }
            if (row - 1 >= 0 && (col - 1 >= 0 && Piece[row - 1][col - 1] == null || Piece[row - 1][col - 1].isWhite != piece.isWhite)) {
                isValidMove[row - 1][col - 1] = true;
            }

        }


        ///
        // WHITEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
        ///
        if (piece.type.equals("Pawn") && piece.isWhite) {


            // up 1
            if (row - 1 >= 0 && Piece[row - 1][col] == null) {
                isValidMove[row - 1][col] = true;
            }


            // CAPTURED LEFT


            if (row - 1 >= 0 && col - 1 >= 0 && Piece[row - 1][col - 1] != null && !Piece[row - 1][col - 1].isWhite) {


                isValidMove[row - 1][col - 1] = true;
            }
            // CAPTURED RIGHT


            if (row - 1 >= 0 && col + 1 < 8 && Piece[row - 1][col + 1] != null && !Piece[row - 1][col + 1].isWhite) {


                isValidMove[row - 1][col + 1] = true;
            }
            // UP 2 if FIRST MOVE


            if (firstMove[row][col] && row - 2 >= 0 && Piece[row - 1][col] == null && Piece[row - 2][col] == null) {


                isValidMove[row - 2][col] = true;
            }
        }


    }

    public boolean isKingChecked(boolean isWhite) {

        int kingRow = -1;
        int kingCol = -1;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (Piece[r][c] != null &&
                        Piece[r][c].type.equals("King") &&
                        Piece[r][c].isWhite == isWhite) {

                    kingRow = r;
                    kingCol = c;
                }
            }
        }

        if (kingRow == -1) return false;

        boolean inCheck = false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                Piece p = Piece[r][c];
                if (p == null) continue;

                initLegalMoves(p, r, c);

                if (isValidMove[kingRow][kingCol]) {
                    inCheck = true;
                }
            }
        }

        return inCheck;
    }
    public boolean isCheckmate(boolean isWhite){
        if(!isKingChecked(isWhite)) return false;
        for(int r1 = 0; r1 <8; r1++){
            for(int c1 = 0; c1 < 8; c1++){
                Piece p = Piece[r1][c1];
                if(p == null || p.isWhite != isWhite) continue;
                initLegalMoves(p,r1,c1);
                for(int r2 = 0; r2 <8; r2++){
                    for(int c2 = 0; c2 <8; c2++){
                        if(!isValidMove[r2][c2]) continue;
                        Piece temp = Piece[r2][c2];
                        Piece[r2][c2] = p;
                        Piece[r1][c1] = null;
                        boolean stillChecked = isKingChecked(isWhite);
                        Piece[r1][c1] = p;
                        Piece[r2][c2] = temp;
                        if(!stillChecked){
                            return false;
                        }
                    }

                }
            }

        }
        System.out.println("CHECKMATE");
            return true;
    }
    public void restart(){


            // clear board
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece[r][c] = null;
                    firstMove[r][c] = false;
                }
            }

            // reset state
            whiteTurn = true;
            hasSelection = false;
            selectedRow = -1;
            selectedCol = -1;
            visibility = false;
            gameOver = false;

            // reset pawns first move
            for (int i = 0; i < 8; i++) {
                firstMove[1][i] = true;
                firstMove[6][i] = true;
            }

            // rebuild pieces (same as constructor)
            Piece[0][0] = new Piece("Rook", false);
            Piece[0][1] = new Piece("Knight", false);
            Piece[0][2] = new Piece("Bishop", false);
            Piece[0][3] = new Piece("Queen", false);
            Piece[0][4] = new Piece("King", false);
            Piece[0][5] = new Piece("Bishop", false);
            Piece[0][6] = new Piece("Knight", false);
            Piece[0][7] = new Piece("Rook", false);

            for (int i = 0; i < 8; i++) {
                Piece[1][i] = new Piece("Pawn", false);
                Piece[6][i] = new Piece("Pawn", true);
            }

            Piece[7][0] = new Piece("Rook", true);
            Piece[7][1] = new Piece("Knight", true);
            Piece[7][2] = new Piece("Bishop", true);
            Piece[7][3] = new Piece("Queen", true);
            Piece[7][4] = new Piece("King", true);
            Piece[7][5] = new Piece("Bishop", true);
            Piece[7][6] = new Piece("Knight", true);
            Piece[7][7] = new Piece("Rook", true);

    }
    public String logMoves(Piece piece, int fromRow, int fromCol, int toRow, int toCol){


        return piece.type + " " + squares[fromRow][fromCol] + " -> " + squares[toRow][toCol];
    }






    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D g2 = (Graphics2D) g;
        if (flipped) {
            for (int i = 1; col + i < 8 && row + i < 8; i++) {


            }
        }
        drawPiece(g2);
        drawValidMoves(g2);
        drawAccessibility(g2);
        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, screenWidth, screenHeight);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("CHECKMATE", 350, 350 );
            g2.drawString("PRESS R TO RESTART", 325, 450 );
        }


        g2.dispose();
    }

    public void drawValidMoves(Graphics2D g2) {
        g2.setColor(Color.white);
        if (visibility ) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (isValidMove[row][col]) {
                        int drawCol = col;
                        int drawRow = row;

                        if (flipped) {
                            drawCol = 7 - col;
                            drawRow = 7 - row;
                        }

                        int x = drawCol * tileSize + (tileSize - 67);
                        int y = drawRow * tileSize + (tileSize - 67);

                        g2.fillOval(x, y, 35, 35);

                    }
                }
            }
        }


    }
    public void drawAccessibility(Graphics2D g2){

              g2.setColor(Color.black);
             g2.setFont(new Font("Arial", Font.BOLD, 20));
            if(whiteTurn){
                g2.drawString("Move : White", 815,25);
            }
            else{
                g2.drawString("Move : Black", 815,25);

            }
            g2.drawString(lastMove, 815,50);


    }



    public void drawPiece(Graphics2D g2) {
        int tileSize = 100;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                int drawCol = col;
                int drawRow = row;

                if (flipped) {
                    drawCol = 7 - col;
                    drawRow = 7 - row;
                }

                // board colors (use original row/col so pattern stays correct)
                if ((row + col) % 2 == 0) {
                    g2.setColor(new Color(118, 150, 86));
                } else {
                    g2.setColor(new Color(238, 238, 210));
                }

                if (hasSelection && row == selectedRow && col == selectedCol) {
                    g2.setColor(Color.yellow);
                }

                if (clicked) {
                    g2.setColor(Color.yellow);
                }

                g2.fillRect(drawCol * tileSize, drawRow * tileSize, tileSize, tileSize);

                Piece p = Piece[row][col];
                if (p == null) continue;

                // black pieces
                if (p.type.equals("Rook") && !p.isWhite)
                    g2.drawImage(bRook, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Knight") && !p.isWhite)
                    g2.drawImage(bKnight, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Bishop") && !p.isWhite)
                    g2.drawImage(bBishop, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Queen") && !p.isWhite)
                    g2.drawImage(bQueen, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("King") && !p.isWhite)
                    g2.drawImage(bKing, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Pawn") && !p.isWhite)
                    g2.drawImage(bPawn, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                // white pieces
                if (p.type.equals("Rook") && p.isWhite)
                    g2.drawImage(wRook, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Knight") && p.isWhite)
                    g2.drawImage(wKnight, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Bishop") && p.isWhite)
                    g2.drawImage(wBishop, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Queen") && p.isWhite)
                    g2.drawImage(wQueen, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("King") && p.isWhite)
                    g2.drawImage(wKing, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Pawn") && p.isWhite)
                    g2.drawImage(wPawn, drawCol * tileSize, drawRow * tileSize, tileSize, tileSize, null);
            }
        }
    }
}