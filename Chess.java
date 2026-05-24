import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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

    int mouseX;
    int mouseY;
    int tileSize = 100;

    int col = mouseX / tileSize;
    int row = mouseY / tileSize;




    int selectedRow = -1;
    int selectedCol = -1;
    boolean hasSelection = false;
    boolean rightPressed = false;
    boolean leftPressed = false;

    boolean validMove = false;
    boolean whiteTurn = true;
    boolean flipped = false;



    Piece[][] Piece = new Piece[8][8];
    String[][] squares = new String[8][8];
    boolean[][] firstMove = new boolean[8][8];


    Thread gameThread;

    MouseHandler mouse;
    KeyHandler keyH = new KeyHandler();

    public Chess() {


        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                char file = (char)('a' + col);
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
        try{
            bRook = ImageIO.read(getClass().getResource("/blackRook.png"));
            bKnight = ImageIO.read(getClass().getResource("/blackKnight.png"));
            bBishop = ImageIO.read(getClass().getResource("/blackBishop.png"));
            bQueen = ImageIO.read(getClass().getResource("/blackQueen.png"));
            bKing = ImageIO.read(getClass().getResource("/blackKing.png"));
            bPawn = ImageIO.read(getClass().getResource("/blackPawn.png"));
            // WHITE PIECES
            wRook = ImageIO.read(getClass().getResource("/whiteRook.png"));
            wKnight = ImageIO.read(getClass().getResource("/whiteKnight.png"));
            wBishop = ImageIO.read(getClass().getResource("/whiteBishop.png"));
            wQueen = ImageIO.read(getClass().getResource("/whiteQueen.png"));
            wKing = ImageIO.read(getClass().getResource("/whiteKing.png"));
            wPawn = ImageIO.read(getClass().getResource("/whitePawn.png"));


        } catch (Exception e){
            e.printStackTrace();
        }

        mouse = new MouseHandler();

        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addKeyListener(keyH);


        this.setPreferredSize(new Dimension(800, 800));
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
        if(keyH.rPressed){
            flipped = !flipped;
            keyH.rPressed = false;
            System.out.println("Flipped");
        }
        if (!mouse.mousePressed) return;

        col = mouse.mouseX / tileSize;
        row = mouse.mouseY / tileSize;

        if (row < 0 || row > 7 || col < 0 || col > 7) {
            mouse.mousePressed = false;
            return;
        }

        if (!hasSelection) {

            if (Piece[row][col] != null &&
                    Piece[row][col].isWhite == whiteTurn) {

                selectedRow = row;
                selectedCol = col;
                hasSelection = true;

                System.out.println("Selected: " + Piece[row][col].type);
            }

        } else {

            Piece piece = Piece[selectedRow][selectedCol];

            if (piece == null) {
                hasSelection = false;
                mouse.mousePressed = false;
                return;
            }

            validMove = false;

            if (Piece[row][col] != null && Piece[row][col].isWhite == piece.isWhite) {
                System.out.println("Invalid: own piece");
                hasSelection = false;
                mouse.mousePressed = false;
                return;
            }

            // BLACK PAWN
            if (piece.type.equals("Pawn") && !piece.isWhite) {

                if (col == selectedCol && row == selectedRow + 1 && Piece[row][col] == null) {
                    validMove = true;
                }

                else if (firstMove[selectedRow][selectedCol]) {
                    if (col == selectedCol && row == selectedRow + 2 && Piece[selectedRow + 1][col] == null && Piece[row][col] == null) {
                        validMove = true;
                    }
                }

                else if ((col == selectedCol - 1 || col == selectedCol + 1) &&
                        row == selectedRow + 1 && Piece[row][col] != null && Piece[row][col].isWhite) {
                    validMove = true;
                }
            }

            // WHITE PAWN
            if (piece.type.equals("Pawn") && piece.isWhite) {

                if (col == selectedCol && row == selectedRow - 1 && Piece[row][col] == null) {
                    validMove = true;
                }

                else if (firstMove[selectedRow][selectedCol]) {
                    if (col == selectedCol && row == selectedRow - 2 && Piece[selectedRow - 1][col] == null && Piece[row][col] == null) {
                        validMove = true;
                    }
                }

                else if ((col == selectedCol - 1 || col == selectedCol + 1) &&
                        row == selectedRow - 1 && Piece[row][col] != null && !Piece[row][col].isWhite) {
                    validMove = true;
                }
            }

            if (row == selectedRow && col == selectedCol) {
                hasSelection = false;
                mouse.mousePressed = false;
                return;
            }

            if (validMove || !piece.type.equals("Pawn")) {

                Piece[row][col] = piece;
                Piece[selectedRow][selectedCol] = null;

                System.out.println("Moved Piece");

                whiteTurn = !whiteTurn;
            }

            else {
                System.out.println("Illegal Move");
            }

            if (piece.type.equals("Pawn")) {
                firstMove[selectedRow][selectedCol] = false;
            }

            hasSelection = false;
        }

        mouse.mousePressed = false;
    }




    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        if(flipped == true){
            g2.rotate(Math.toRadians(180), getWidth() / 2.0, getHeight() / 2.0);
        }
        drawPiece(g2);



        g2.dispose();
    }

    public void drawPiece(Graphics2D g2) {
        int tileSize = 100;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                // board colors
                if ((row + col) % 2 == 0) {
                    g2.setColor(new Color(118, 150, 86));
                } else {
                    g2.setColor(new Color(238, 238, 210));
                }

                if (hasSelection && row == selectedRow && col == selectedCol) {
                    g2.setColor(Color.yellow);
                }

                if (rightPressed) {
                    g2.setColor(Color.yellow);
                }

                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);

                Piece p = Piece[row][col];
                if (p == null) continue;

                if (p.type.equals("Rook") && !p.isWhite)
                    g2.drawImage(bRook, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Knight") && !p.isWhite)
                    g2.drawImage(bKnight, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Bishop") && !p.isWhite)
                    g2.drawImage(bBishop, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Queen") && !p.isWhite)
                    g2.drawImage(bQueen, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("King") && !p.isWhite)
                    g2.drawImage(bKing, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Pawn") && !p.isWhite)
                    g2.drawImage(bPawn, col * tileSize, row * tileSize, tileSize, tileSize, null);

                if (p.type.equals("Rook") && p.isWhite)
                    g2.drawImage(wRook, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Knight") && p.isWhite)
                    g2.drawImage(wKnight, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Bishop") && p.isWhite)
                    g2.drawImage(wBishop, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Queen") && p.isWhite)
                    g2.drawImage(wQueen, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("King") && p.isWhite)
                    g2.drawImage(wKing, col * tileSize, row * tileSize, tileSize, tileSize, null);
                if (p.type.equals("Pawn") && p.isWhite)
                    g2.drawImage(wPawn, col * tileSize, row * tileSize, tileSize, tileSize, null);
            }
        }
    }

}
