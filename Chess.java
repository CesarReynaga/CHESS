import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.stream.IntStream;
import java.awt.event.*;
public class Chess extends JPanel{
    boolean keyPressed = false;
    BufferedImage wp;
    BufferedImage wr;
    BufferedImage wn;
    BufferedImage wb;
    BufferedImage wq;
    BufferedImage wk;
    BufferedImage bp;
    BufferedImage br;
    BufferedImage bn;
    BufferedImage bb;
    BufferedImage bq;
    BufferedImage bk;
    boolean qPressed = false;
    boolean isFlipped = false;
    int first = -1;
    int second = -1;
    boolean captured = false;
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean inPast = false;
    int pastIndex = 0;
    int turn = 0;
    List<Square> board = new ArrayList<Square>();
    List<Move> history = new ArrayList<Move>();
    public Chess() {
        try{
            wp = ImageIO.read(new File("wPawn.png"));
            wr = ImageIO.read(new File("wRook.png"));
            wn = ImageIO.read(new File("wKnight.png"));
            wb = ImageIO.read(new File("wBishop.png"));
            wq = ImageIO.read(new File("wQueen.png"));
            wk = ImageIO.read(new File("wKing.png"));
            bp = ImageIO.read(new File("bPawn.png"));
            br = ImageIO.read(new File("bRook.png"));
            bn = ImageIO.read(new File("bKnight.png"));
            bb = ImageIO.read(new File("bBishop.png"));
            bq = ImageIO.read(new File("bQueen.png"));
            bk = ImageIO.read(new File("bKing.png"));
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        setFocusable(true);
        addKeyListener(new KeyAdapter() 
        {

            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
                else if(e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
                else{
                    rightPressed = false;
                    leftPressed = false;
                }
                keyPressed = true;
            }
            
            public void keyReleased(KeyEvent e) {
                leftPressed = false;
                rightPressed = false;
                keyPressed = false;
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = 0;
                int clickCount = 0;
                for(Square square : board){
                    if(square.click(e.getX(), e.getY())){ 
                        System.out.println("Clicked on square index: " + i);
                        clickCount++;
                        System.out.println("Clicked on square: " + square.getSquare());
                        if(qPressed) square.setPiece(new Piece("queen", "white", wq));
                        if(second == -1 && first == -1 && square.getPiece() != null) first = i;
                        else if(first!= -1 && first != i) second = i;
                        else if(first == i) first = -1;
                        if(second != -1 && isLegal(first, second, pastIndex, board)){
                            Piece capturedPiece = null;
                            if(board.get(second).getPiece() != null){ captured = true; capturedPiece = board.get(second).getPiece();}
                            else captured = false;
                            board.get(second).setPiece(board.get(first).getPiece());
                            board.get(first).removePiece();
                            String piece = board.get(second).getPiece().getType();
                            String p;
                            if(!piece.equals("knight") && !piece.equals("pawn")) p = piece.charAt(0) + "";
                            else if(piece.equals("knight")) p = "n";
                            else if(captured) p = board.get(first).getSquare().charAt(0) + "";
                            else p = "";
                            if(captured) p += "x";
                            if(!piece.equals("pawn")) p = p.toUpperCase();
                            System.out.println(p + board.get(second).getSquare());
                            if(inPast){
                                while(history.size() > pastIndex){
                                    history.remove(history.size() - 1);
                                }
                            }
                            if(!captured && !isFlipped)history.add(new Move(first, second, board.get(second).getPiece()));
                            else if(captured && !isFlipped)history.add(new Move(first, second, board.get(second).getPiece(), capturedPiece));
                            else if(!captured && isFlipped)history.add(new Move(63 - first, 63 - second, board.get(second).getPiece()));
                            else if(captured && isFlipped)history.add(new Move(63 - first, 63 - second, board.get(second).getPiece(), capturedPiece));
                            second = -1;
                            first = -1;
                            pastIndex++;
                            flipBoard();
                        }else if(second != -1){ second = -1; first = -1;}
                    }
                    i++;
                }
            }
        });
        
        makeBoard(board);
        labelBoard();
        initPieces();
        Timer timer = new Timer(16, e -> {
            if(keyPressed) first = -1;
            if(leftPressed && history.size() > 0){
                leftPressed = false;
                if(pastIndex == 0) return;
                flipBoard();
                int index = pastIndex - 1;
                Move move = history.get(index);
                int second = -1;
                int first = -1;
                if(isFlipped){
                    first = 63 - move.getStart();
                    second = 63 - move.getEnd();
                }else{
                    first = move.getStart();
                    second = move.getEnd();
                }
                board.get(first).setPiece(board.get(second).getPiece());
                if(move.getCaptured() == null) board.get(second).removePiece();
                else board.get(second).setPiece(move.getCaptured());
                inPast = pastIndex > 0;
                pastIndex--;
            }
            if(rightPressed && inPast){
                rightPressed = false;
                if(pastIndex >= history.size()) return;
                flipBoard();
                //pastIndex++;
                Move move = history.get(pastIndex);
                int second = -1;
                int first = -1;
                if(isFlipped){
                    second = 63 - move.getStart();
                    first = 63 - move.getEnd();
                }else{
                    second = move.getStart();
                    first = move.getEnd();
                }
                board.get(first).setPiece(board.get(second).getPiece());
                if(move.getCaptured() == null) board.get(second).removePiece();
                else board.get(second).setPiece(move.getCaptured());
                pastIndex++;

            }
            repaint();
        });
        
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        drawBoard(g);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MOVING SHIT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(new Chess());
 
        frame.setVisible(true);
    }

    public static boolean isLegal(int first, int second, int pastIndex, List<Square> board){
        String piece = board.get(first).getPiece().getType();
        String color = board.get(first).getPiece().getColor();
        if(color.equals("white") && pastIndex % 2 == 0) return true;
        else if(color.equals("black") && pastIndex % 2 == 1) return true;
        else return false;
    }

    public void flipBoard(){
        List<Square> flipped = new ArrayList<Square>();
        makeBoard(flipped);
        for(int i = 0; i < board.size(); i++){
            flipped.get(63-i).setPiece(board.get(i).getPiece());
            flipped.get(63-i).setSquare(board.get(i).getSquare());
        }
        board = flipped;
        isFlipped = !isFlipped;
    }

    public void labelBoard(){
        int num = 0;
        int[] idk = {8, 7, 6, 5, 4, 3, 2, 1};
        for(Square square : board){
            int row = 0;
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if(num ==(i * 8) + j){
                        row = idk[j]; 
                        square.setSquare("" + row);
                        break;
                    }
                }
            }
            num++;
        }
        int count = 0;
        String[] flank = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for(Square square : board){
            String letter = "";
            if(count >= 0 && count < 8){ 
                letter = flank[0];
            }
            if(count >= 8 && count < 16){
                letter = flank[1];
                
            }
            if(count >= 16 && count < 24){
                letter = flank[2];
                
            }
            if(count >= 24 && count < 32){
                letter = flank[3];
                
            }
            if(count >= 32 && count < 40){
                letter = flank[4];
                
            }
            if(count >= 40 && count < 48){
                letter = flank[5];
                
            }
            if(count >= 48 && count < 56){
                letter = flank[6];
                
            }
            if(count >= 56 && count < 64){
                letter = flank[7];
                
            }
            square.setSquare(letter +  square.getSquare());
            count++;
        }
    }

    public void initPieces(){
        for(Square square : board){
            String code = square.getSquare();
            if(code.contains("7")) square.setPiece(new Piece("pawn", "black", bp));
            else if(code.contains("2")) square.setPiece(new Piece("pawn", "white", wp));
            else if(code.equals("a8") || code.equals("h8")) square.setPiece(new Piece("rook", "black", br));
            else if(code.equals("a1") || code.equals("h1")) square.setPiece(new Piece("rook", "white", wr));
            else if(code.equals("b8") || code.equals("g8")) square.setPiece(new Piece("knight", "black", bn));
            else if(code.equals("b1") || code.equals("g1")) square.setPiece(new Piece("knight", "white", wn));
            else if(code.equals("c8") || code.equals("f8")) square.setPiece(new Piece("bishop", "black", bb));
            else if(code.equals("c1") || code.equals("f1")) square.setPiece(new Piece("bishop", "white", wb));
            else if(code.equals("e8")) square.setPiece(new Piece("king", "black", bk));
            else if(code.equals("e1")) square.setPiece(new Piece("king", "white", wk));
            else if(code.equals("d8")) square.setPiece(new Piece("queen", "black", bq));
            else if(code.equals("d1")) square.setPiece(new Piece("queen", "white", wq));
        }
    }
    public void drawBoard(Graphics g){
        boolean toggle = false;
        int count = 0;
        for(Square square : board){
            if(!toggle && board.indexOf(square) != first) g.setColor(new Color(240,217,181));
            else if(toggle && board.indexOf(square) != first) g.setColor(new Color(181,136,99));
            else if(board.indexOf(square) == first || board.indexOf(square) == second)g.setColor(new Color(255, 255, 0));
            count++;
            if(count != 8)toggle = !toggle;
            else count = 0;
            g.fillRect(square.getX(), square.getY(), 80, 80);
            g.setColor(Color.BLACK);
            g.drawRect(square.getX(), square.getY(), 80, 80);
            if(square.getPiece() != null) g.drawImage(square.getPiece().getImage(), square.getX(), square.getY(), 80, 80, null);
        }
    }
    
    public void makeBoard(List<Square> board){
        int blockWidth = 80;
        int blockHeight = 80;
        int gridWidth = 8 * blockWidth;
        int gridHeight = 8 * blockHeight;
        int startX = ((800 - gridWidth) / 2);
        int startY = ((800 - gridHeight) / 2);
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board.add(new Square((i * 80) + startX, (j * 80) + startY));
            }
        }

    }
    
    public static void writeHighScore(int high) {
        int old = 0;
        try
        {
            File file = new File("output.txt");
            Scanner scanner = new Scanner(file);
            scanner.next();
            scanner.next();
            if(scanner.hasNextInt()) old = scanner.nextInt();
        }
        catch(IOException e)
        {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        if(high > old)
        {
            String fileName = "output.txt";
            String content = ("HIGHEST WPM: " + high + " wpm");
    
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write(content);
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }
    }
    
    public static int getHighScore()
    {
        try
        {
            File file = new File("output.txt");
            Scanner scanner = new Scanner(file);
            scanner.next();
            scanner.next();
            if(scanner.hasNextInt()) return scanner.nextInt();
        }
        catch(IOException e)
        {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        return -1;
    }
}
