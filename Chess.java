import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chess extends JPanel implements Runnable {

    private BufferedImage img;
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

    int col = mouseX/ 100;
    int row = mouseY/100;

    int selectedRow = -1;
    int selectedCol = -1;
    boolean hasSelection = false;


    String[][] board = new String[8][8];


    Thread gameThread;

    MouseHandler mouse;

    public Chess() {



        board[0][0] = "/bRook.png";
        board[0][1] = "/bKnight.png";
        board[0][2] = "/bBishop.png";
        board[0][3] = "/bQueen.png";
        board[0][4] = "/bKing.png";
        board[0][5] = "/bBishop.png";
        board[0][6] = "/bKnight.png";
        board[0][7] = "/bRook.png";
        for (int i = 0; i < 8; i++) {
            board[1][i] = "bp";
        }
        board[7][0] = "/wRook.png";
        board[7][1] = "/wKnight.png";
        board[7][2] = "/wBishop.png";
        board[7][3] = "/wQueen.png";
        board[7][4] = "/wKing.png";
        board[7][5] = "/wBishop.png";
        board[7][6] = "/wKnight.png";
        board[7][7] = "/wRook.png";
        for (int i = 0; i < 8; i++) {
            board[6][i] = "wp";
        }
        try{
            bRook = ImageIO.read(getClass().getResource("/bRook.png"));
            bKnight = ImageIO.read(getClass().getResource("/bKnight.png"));
            bBishop = ImageIO.read(getClass().getResource("/bBishop.png"));
            bQueen = ImageIO.read(getClass().getResource("/bQueen.png"));
            bKing = ImageIO.read(getClass().getResource("/bKing.png"));
            bPawn = ImageIO.read(getClass().getResource("/bPawn.png"));
            // WHITE PIECES
            wRook = ImageIO.read(getClass().getResource("/wRook.png"));
            wKnight = ImageIO.read(getClass().getResource("/wKnight.png"));
            wBishop = ImageIO.read(getClass().getResource("/wBishop.png"));
            wQueen = ImageIO.read(getClass().getResource("/wQueen.png"));
            wKing = ImageIO.read(getClass().getResource("/wKing.png"));
            wPawn = ImageIO.read(getClass().getResource("/wPawn.png"));


        } catch (Exception e){
            e.printStackTrace();
        }

        mouse = new MouseHandler();

        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

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

        if (mouse.mousePressed) {
            int tileSize = 100;

            col = mouse.mouseX/tileSize;
            row = mouse.mouseY/tileSize;

            System.out.println("Mouse X: " + col + "\nMouse Y: " +row);





            mouse.mousePressed = false;
        }


    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        drawBoard(g2);


        g2.dispose();
    }

    public void drawBoard(Graphics2D g2) {
        int tileSize = 100;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    g2.setColor(new Color(118, 150, 86));  // dark
                } else {
                    g2.setColor(new Color(238, 238, 210)); // light
                }
                if(hasSelection){
                    g2.setColor(Color.yellow);
                }
                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);


                if (board[row][col] != null && board[row][col].equals("/bRook.png")) {
                    g2.drawImage(bRook, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/bKnight.png")) {
                    g2.drawImage(bKnight, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/bBishop.png")) {
                    g2.drawImage(bBishop, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/bQueen.png")) {
                    g2.drawImage(bQueen, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/bKing.png")) {
                    g2.drawImage(bKing, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/bPawn.png")) {
                    g2.drawImage(bPawn, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }
                // WHITE
                if (board[row][col] != null && board[row][col].equals("/wRook.png")) {
                    g2.drawImage(wRook, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/wKnight.png")) {
                    g2.drawImage(wKnight, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/wBishop.png")) {
                    g2.drawImage(wBishop, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/wQueen.png")) {
                    g2.drawImage(wQueen, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/wKing.png")) {
                    g2.drawImage(wKing, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }

                if (board[row][col] != null && board[row][col].equals("/wPawn.png")) {
                    g2.drawImage(wPawn, col * tileSize, row * tileSize, tileSize, tileSize, null);
                }


            }

        }



    }
    public void drawPieces(Graphics2D g2){

    }
}

