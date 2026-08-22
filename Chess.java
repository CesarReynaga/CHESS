import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
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
    static int wPoints;
    static int bPoints;
    boolean qPressed = false;
    boolean isFlipped = false;
    static final String[] alph = {"a", "b", "c", "d", "e" ,"f", "g", "h"};
    int first = -1;
    List<Piece> bCaptures = new ArrayList<Piece>();
    List<Piece> wCaptures = new ArrayList<Piece>();
    int second = -1;
    boolean captured = false;
    static boolean canCapture = false;
    boolean leftPressed = false;
    boolean rightPressed = false;
    String[] options = {"Queen", "Bishop", "Rook", "Knight"};
    boolean inPast = false;
    int pastIndex = 0;
    int turn = 0;
    static int lastEnd = 0;
    boolean upPressed = false;
    List<Square> board = new ArrayList<Square>();
    List<Move> history = new ArrayList<Move>();
    static boolean enPassant = false;
    static boolean castlingShort = false;
    static boolean castlingLong = false;
    static boolean checking = false;
    static String playerColor = "";
    int logCount = 0;
    boolean cPressed = false;
    JTextArea moveLog = new JTextArea();
    boolean clong = false;
    boolean cshort= false;
    JScrollPane scrollPane;
    int moveCount = 1;
    boolean check = false;
    static boolean mated = false;
    static String matedKing = null;
    public Chess(){
        try{
            wp = ImageIO.read(getClass().getResource("wPawn.png"));
            wr = ImageIO.read(getClass().getResource("wRook.png"));
            wn = ImageIO.read(getClass().getResource("wKnight.png"));
            wb = ImageIO.read(getClass().getResource("wBishop.png"));
            wq = ImageIO.read(getClass().getResource("wQueen.png"));
            wk = ImageIO.read(getClass().getResource("wKing.png"));
            bp = ImageIO.read(getClass().getResource("bPawn.png"));
            br = ImageIO.read(getClass().getResource("bRook.png"));
            bn = ImageIO.read(getClass().getResource("bKnight.png"));
            bb = ImageIO.read(getClass().getResource("bBishop.png"));
            bq = ImageIO.read(getClass().getResource("bQueen.png"));
            bk = ImageIO.read(getClass().getResource("bKing.png"));
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        setFocusable(true);
        addKeyListener(new KeyAdapter() 
        {

            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
                else if(e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
                else if(e.getKeyCode() == KeyEvent.VK_C) cPressed = true;
                else if(e.getKeyCode() == KeyEvent.VK_UP) upPressed = true;
                else{
                    rightPressed = false;
                    leftPressed = false;
                    cPressed = false;
                    upPressed = false;
                }
                keyPressed = true;
            }
            
            public void keyReleased(KeyEvent e) {
                leftPressed = false;
                cPressed = false;
                upPressed = false;
                rightPressed = false;
                keyPressed = false;
            }
        });
        
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int i = 0;
                for(Square square : board){
                    if(square.click(e.getX(), e.getY())){ 
                        System.out.println("Clicked on square index: " + i);
                        System.out.println("Clicked on square: " + square.getSquare());
                        if(qPressed) square.setPiece(new Piece("queen", "white", wq));
                        if(second == -1 && first == -1 && square.getPiece() != null) first = i;
                        else if(first!= -1 && first != i) second = i;
                        else if(first == i) first = -1;
                        if(second != -1 && !inPast && (isLegal(first, second, pastIndex, board, history, isFlipped) || cPressed)){
                            Piece capturedPiece = null;
                            if(board.get(second).getPiece() != null || enPassant){captured = true; if(enPassant)capturedPiece = pastIndex % 2 == 0 ? new Piece("pawn", "black", bp) : new Piece("pawn", "white", wp); else capturedPiece = board.get(second).getPiece();}
                            else captured = false;
                            board.get(second).setPiece(board.get(first).getPiece());
                            board.get(first).removePiece();
                            if(enPassant) board.get(lastEnd).removePiece();
                            String color = board.get(second).getPiece().getColor();
                            if(castlingShort && color.equals("white")){
                                Piece rook = board.get(second + 8).getPiece();
                                board.get(second - 8).setPiece(rook);
                                board.get(second + 8).removePiece();
                                castlingShort = false;
                                 cshort = true;
                            }else if(castlingShort && color.equals("black")){
                                Piece rook = board.get(second - 8).getPiece();
                                board.get(second + 8).setPiece(rook);
                                board.get(second - 8).removePiece();
                                castlingShort = false;
                                cshort = true;
                            }
                            if(castlingLong && color.equals("white")){
                                Piece rook = board.get(second - 16).getPiece();
                                board.get(second + 8).setPiece(rook);
                                board.get(second - 16).removePiece();
                                castlingLong = false;
                                clong = true;
                            }
                            else if(castlingLong && color.equals("black")){
                                Piece rook = board.get(second + 16).getPiece();
                                board.get(second - 8).setPiece(rook);
                                board.get(second + 16).removePiece();
                                castlingLong = false;
                                clong = true;
                            }
                            
                            String piece = board.get(second).getPiece().getType();
                            String p;
                            if(!piece.equals("knight") && !piece.equals("pawn")) p = piece.charAt(0) + "";
                            else if(piece.equals("knight")) p = "n";
                            else if(captured) p = board.get(first).getSquare().charAt(0) + "";
                            else p = "";
                            if(!piece.equals("pawn")){
                                 p = p.toUpperCase();
                                 if(otherPieceCouldToo(board.get(second).getPiece(), second)) p += board.get(first).getSquare().charAt(0);
                            }
                            if(captured) p += "x";
                            String notation = p + board.get(second).getSquare();
                            if(cshort){ notation = "O-O"; cshort = false;}
                            else if(clong){ notation = "O-O-O"; clong = false;}
                            int row = Integer.parseInt(board.get(second).getSquare().charAt(1) + "");
                            if(board.get(second).getPiece().getType().equals("pawn") && (row == 8 || row == 1)){
                                int option = makeOptions();
                                String promotion = options[option].toLowerCase();
                                int points = 0;
                                switch (promotion){
                                    case "pawn" : points += 1; break;
                                    case "bishop" : points += 3; break;
                                    case "knight" : points += 3; break;
                                    case "rook" : points += 5; break;
                                    case "queen" : points += 9; break;
                                }
                                if(color.equals("white")){
                                    bCaptures.add(board.get(second).getPiece());
                                    wPoints += points; 
                                }else{
                                    wCaptures.add(board.get(second).getPiece());
                                    bPoints =+ points;
                                }

                                String image = "" + color.charAt(0) + options[option] + ".png";
                                notation += ("=" + options[option].charAt(0)); 
                                try{
                                    board.get(second).setPiece(new Piece(promotion, color, ImageIO.read(getClass().getResource(image))));
                                }catch (IOException dih){
                                    System.out.println(dih.getMessage());
                                }
                            }
                            
                            if(capturedPiece != null){
                                if(color.equals("white")) wCaptures.add(capturedPiece);
                                else bCaptures.add(capturedPiece);
                            }
                            if(!isFlipped && !cPressed && !enPassant && !castlingLong && !castlingShort)history.add(new Move(first, second, board.get(second).getPiece()));
                            else if(!isFlipped && !cPressed && !enPassant && !castlingLong && !castlingShort)history.add(new Move(first, second, board.get(second).getPiece(), capturedPiece));
                            else if(!isFlipped && !cPressed && enPassant && !castlingLong && !castlingShort)history.add(new Move(first, second, board.get(second).getPiece(), capturedPiece, "enPassant"));
                            else if(isFlipped && !cPressed && !enPassant && !castlingLong && !castlingShort)history.add(new Move(63 - first, 63 - second, board.get(second).getPiece()));
                            else if(isFlipped && !cPressed && !enPassant && !castlingLong && !castlingShort)history.add(new Move(63 - first, 63 - second, board.get(second).getPiece(), capturedPiece));
                            else if(isFlipped && !cPressed && enPassant && !castlingLong && !castlingShort)history.add(new Move(first, second, board.get(second).getPiece(), capturedPiece, "enPassant"));
                            else if(castlingShort && !isFlipped){history.add(new Move(first, second, board.get(second).getPiece(), "castlingShort"));}
                            else if(castlingShort && isFlipped)history.add(new Move(63 - first,63 - second, board.get(second).getPiece(), "castlingShort"));
                            else if(castlingLong && !isFlipped)history.add(new Move(first, second, board.get(second).getPiece(), "castlingLong"));
                            else if(castlingLong && isFlipped)history.add(new Move(63 - first,63 - second, board.get(second).getPiece(), "castlingLong"));
                            if(kingIsMated(color)){ 
                                notation += "#"; 
                                mated = true; 
                                for(Square check : board){
                                    String kingColor = color.equals("white") ? "black" : "white";
                                    if(check.getPiece() != null && check.getPiece().getType().equals("king") && check.getPiece().getColor().equals(kingColor)){matedKing = check.getSquare(); break;}
                                } 
                            }
                            else if(kingIsChecked(color)) notation += "+";
                            System.out.println(notation);
                            if(logCount == 0){
                                moveLog.append("\n" + moveCount + ". " + notation);
                                logCount++;

                            }
                            else if(logCount == 1){
                                moveLog.append(" " + notation);
                                logCount = 0;
                                moveCount++;
                            }
                            
                            moveLog.setCaretPosition(moveLog.getDocument().getLength());

                            try{
                                AudioInputStream audioStream = null;
                                if(!captured){
                                    String player = board.get(second).getPiece().getColor();
                                    if(player.equals("white")){
                                        audioStream =
                                            AudioSystem.getAudioInputStream(getClass().getResource("/white.wav"));
                                    }
                                    else{
                                        audioStream =
                                            AudioSystem.getAudioInputStream(getClass().getResource("/black.wav"));
                                    }
                                }else{
                                    audioStream =
                                        AudioSystem.getAudioInputStream(getClass().getResource("/capture.wav"));
                                }
                                if(notation.contains("+")) audioStream =
                                    AudioSystem.getAudioInputStream(getClass().getResource("/check.wav"));
                                Clip clip = AudioSystem.getClip();
                                clip.open(audioStream);
                                clip.start();
                            }catch(IOException d){
                                System.out.println(d.getMessage());
                            }catch(UnsupportedAudioFileException a){
                                System.out.println(a.getMessage());
                            }catch(LineUnavailableException l){
                                System.out.println(l.getMessage());
                            }
                            board.get(second).getPiece().setMoved(true);

                            
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
        moveLog.setEditable(false);
        moveLog.setLineWrap(true);
        moveLog.setFont(new Font("Monospaced", Font.BOLD, 18)); 

        scrollPane = new JScrollPane(moveLog);
        scrollPane.setPreferredSize(new Dimension(200, 800));
        
        Timer timer = new Timer(16, e -> {
            inPast = pastIndex != history.size();
            playerColor = isFlipped ? "black" : "white";
            if(keyPressed && !cPressed) first = -1;
            for(Square square : board){
                square.setAttacked(false);
            }

            // now let every piece contribute attacks
            if(!checking){
                int piece = 0;

                List<Square> flippedBoard = getFlippedBoard(board);

                for(Square attacker : flippedBoard){
                    if(attacker.getPiece() != null){

                        int target = 0;

                        for(Square check : flippedBoard){

                            if(isLegal(piece, target, pastIndex, flippedBoard, history, isFlipped, piece)){
                                if((true)){
                                    if(!attacker.getPiece().getColor().equals(playerColor))board.get(63 - flippedBoard.indexOf(check))
                                    .setAttacked(true);
                                }
                            }
                            target++;
                        }
                    }

                    piece++;
                }
            }

            if(upPressed && history.size() > 0){
                upPressed = false;
                for(int i = pastIndex; i < history.size(); i++){
                    redo();
                    try{
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/white.wav"));
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.start();
                    }
                    catch(IOException d){
                        System.out.println(d.getMessage());
                    }catch(UnsupportedAudioFileException a){
                        System.out.println(a.getMessage());
                    }catch(LineUnavailableException l){
                        System.out.println(l.getMessage());
                    }
                }
                
            }
            if(leftPressed && history.size() > 0){
                leftPressed = false;
                undo();
                try{
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/white.wav"));
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.start();
                    }
                    catch(IOException d){
                        System.out.println(d.getMessage());
                    }catch(UnsupportedAudioFileException a){
                        System.out.println(a.getMessage());
                    }catch(LineUnavailableException l){
                        System.out.println(l.getMessage());
                    }
            }
            if(rightPressed && inPast){
                redo();
                try{
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/black.wav"));
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.start();
                    }
                    catch(IOException d){
                        System.out.println(d.getMessage());
                    }catch(UnsupportedAudioFileException a){
                        System.out.println(a.getMessage());
                    }catch(LineUnavailableException l){
                        System.out.println(l.getMessage());
                    }

            }
            repaint();
        });
        
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        drawBoard(g);
        showCaptures(g);
    }

    public void showCaptures(Graphics g){
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString("WHITE MATERIAL: ", 25, 25);
        wPoints = 0;
        bPoints = 0;
        int wCount = 0;
        int wX = 0;
        int bX = 0;
        for(Piece piece : wCaptures){
            g.drawImage(piece.getImage(), (wCount * 15) + 25, 35, 20, 20, null);
            wCount++;
            wX = (wCount * 15) + 25;
        }
        g.drawString("BLACK MATERIAL: ", 600, 25);
        int bCount = 0;
        for(Piece piece : bCaptures){
            g.drawImage(piece.getImage(), (bCount * 15) + 575, 35, 20, 20, null);
            bCount++;
            bX = (bCount * 15) + 575;
        }
        
        for(Piece piece : wCaptures){
            String type = piece.getType();
            switch (type){
                case "pawn" : wPoints += 1; break;
                case "bishop" : wPoints += 3; break;
                case "knight" : wPoints += 3; break;
                case "rook" : wPoints += 5; break;
                case "queen" : wPoints += 9; break;
            }
        }

        for(Piece piece : bCaptures){
            String type = piece.getType();
            switch (type){
                case "pawn" : bPoints += 1; break;
                case "bishop" : bPoints += 3; break;
                case "knight" : bPoints += 3; break;
                case "rook" : bPoints += 5; break;
                case "queen" : bPoints += 9; break;
            }
        }

        if(wPoints > bPoints){
            int diff = wPoints - bPoints;
            g.drawString("+" + diff, wX + 15, 50);
        }
        else if(bPoints > wPoints){
            int diff = bPoints - wPoints;
            g.drawString("+" + diff, bX + 15, 50);
        }

    }

    public boolean otherPieceCouldToo(Piece moved, int second){
        String movedType = moved.getType();
        String movedColor = moved.getColor();
        Square otherPiece = null;
        int i = 0;
        for(Square square : board){
            if(square.getPiece() != null && !square.getPiece().equals(moved) && square.getPiece().getType().equals(movedType) && square.getPiece().getColor().equals(movedColor)){ otherPiece = square; break;}
            i++;
        }
        if(otherPiece == null){
            return false;
        }else{
            board.get(second).removePiece();
            if(isLegal(i, second, pastIndex, board, history, isFlipped)){
                board.get(second).setPiece(moved);
                return true;
            }else{board.get(second).setPiece(moved); return false;}
        }
    }

    public boolean kingIsChecked(String color){
            int piece = 0;
            String kingColor = color.equals("white") ? "black" : "white";
            for(Square square : board){
                if(square.getPiece() != null) square.getPiece().setChecked(false);
            }
            for(Square attacker : board){
                if(attacker.getPiece() != null){

                    int target = 0;

                    for(Square check : board){

                        if(isLegal(piece, target, pastIndex, board, history, isFlipped, piece)){
                            if((true)){
                                if(attacker.getPiece().getColor().equals(color) && check.getPiece() != null && check.getPiece().getColor().equals(kingColor) && check.getPiece().getType().equals("king"))board.get(board.indexOf(check))
                                .getPiece().setChecked(true);
                                
                            }
                        }
                        target++;
                    }
                }

                piece++;
            }

        Square kingCheck = null;
        for(Square thingy : board){
            Piece pieceCheck = thingy.getPiece();
            if(pieceCheck != null && pieceCheck.getType().equals("king") && pieceCheck.getColor().equals(kingColor)) kingCheck = thingy;
        }
        return kingCheck.getPiece().isInCheck();

    }

    public boolean kingIsMated(String color){
        checking = true;
        int count = 0;
        
        List<Square> flippedBoard = getFlippedBoard(board);

        if(kingIsChecked(color)){
            for(int i = 0; i < flippedBoard.size(); i++){
                if(flippedBoard.get(i).getPiece() != null){
                    for(int j = 0; j < flippedBoard.size(); j++){
                        pastIndex++;
                        if(isLegal(i, j, pastIndex, flippedBoard, history, true)){
    
                            System.out.println(flippedBoard.get(i).getSquare() + "" + flippedBoard.get(j).getSquare());
                            count++;
                        }else{
                            if(flippedBoard.get(i).getPiece().getType().equals("king")){
                                System.out.println( flippedBoard.get(i).getPiece().getColor() + " king wanted to move to " + flippedBoard.get(j).getSquare() );
                            if(flippedBoard.get(j).isAttacked()) System.out.println("because the square was attacked");
                            }
                        }
                        pastIndex--;
                    }
                }
            }
            if(count == 0) System.out.println("MATED");
            System.out.println(count);
            checking = false;
            return count == 0;
        }
        else return false;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CHESS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        Chess chess = new Chess();
        JPanel container = new JPanel(new BorderLayout());
        container.add(chess, BorderLayout.CENTER);
        container.add(chess.scrollPane, BorderLayout.EAST);
        frame.add(container);

        frame.setVisible(true);
    }

    public void redo(){
        rightPressed = false;
                if(pastIndex >= history.size()) return;
                flipBoard();
                //pastIndex++;
                Move move = history.get(pastIndex);
                int second = -1;
                int first = -1;
                if(isFlipped){
                    first = 63 - move.getStart();
                    second = 63 - move.getEnd();
                }else{
                    first = move.getStart();
                    second = move.getEnd();
                }
                if(move.getType().equals("enPassant")) board.get(second - 1).removePiece();
                board.get(second).setPiece(board.get(first).getPiece());
                board.get(first).removePiece();
                String player = board.get(second).getPiece().getColor();
                if(move.getType().equals("castlingShort") && player.equals("white")){
                    Piece rook = board.get(second - 8).getPiece(); 
                    board.get(second + 8).setPiece(rook);
                    board.get(second - 8).removePiece();
                }else if(move.getType().equals("castlingShort") && player.equals("black")){
                    Piece rook = board.get(second + 8).getPiece();
                    board.get(second - 8).setPiece(rook);
                    board.get(second + 8).removePiece();
                }
                if(move.getType().equals("castlingLong") && player.equals("white")){
                    Piece rook = board.get(second + 16).getPiece();
                    board.get(second - 8).setPiece(rook);
                    board.get(second + 16).removePiece();
                }
                else if(move.getType().equals("castlingLong") && player.equals("black")){
                    Piece rook = board.get(second - 16).getPiece();
                    board.get(second + 8).setPiece(rook);
                    board.get(second - 16).removePiece();
                }
                pastIndex++;
                flipBoard();
    }

    public void undo(){
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
                if(move.getType().equals("enPassant")){
                    board.get(second).removePiece();
                    board.get(second + 1).setPiece(move.getCaptured());
                }
                else if(move.getType().equals("castlingShort")){
                    System.out.println("ansfdkansdflkajn");
                    if(board.get(second).getPiece().getColor().equals("white")){
                        Piece rook = board.get(second - 8).getPiece();
                        rook.setMoved(false);
                        board.get(second).getPiece().setMoved(false);
                        board.get(second + 8).setPiece(rook);
                        board.get(second - 8).removePiece();
                    }
                    else if(board.get(second).getPiece().getColor().equals("black")){
                        Piece rook = board.get(second + 8).getPiece();
                        rook.setMoved(false);
                        board.get(second).getPiece().setMoved(false);
                        board.get(second - 8).setPiece(rook);
                        board.get(second + 8).removePiece();
                    }
                }
                else if(move.getType().equals("castlingLong")){
                    if(board.get(second).getPiece().getColor().equals("white")){
                        Piece rook = board.get(second + 8).getPiece();
                        rook.setMoved(false);
                        board.get(second).getPiece().setMoved(false);
                        board.get(second - 16).setPiece(rook);
                        board.get(second + 8).removePiece();
                    }
                    else if(board.get(second).getPiece().getColor().equals("black")){
                        Piece rook = board.get(second - 8).getPiece();
                        rook.setMoved(false);
                        board.get(second).getPiece().setMoved(false);
                        board.get(second + 16).setPiece(rook);
                        board.get(second - 8).removePiece();
                    }
                }
                else if(move.getCaptured() == null) board.get(second).removePiece();
                else board.get(second).setPiece(move.getCaptured());
                if(move.getType().equals("castlingShort") || move.getType().equals("castlingLong")) board.get(second).removePiece();
                //System.out.println(move.getType() + move.getType().equals("enPassant"));
                pastIndex--;
                flipBoard();
    }

    public static boolean isLegal(int first, int second, int pastIndex, List<Square> board, List<Move> history, boolean isFlipped){
        String piece = board.get(first).getPiece().getType();
        String color = board.get(first).getPiece().getColor();
        boolean returny = false;
        if(board.get(second).getPiece() != null){
            if(board.get(second).getPiece().getColor().equals(color)) return false;
        }
        
        if((color.equals("white") && pastIndex % 2 == 0) || (color.equals("black") && pastIndex % 2 == 1)){
            if(piece.equals("pawn")){
                //pawn cant move backwards
                //pawn only moves diagonally if its capturing / en passant
                enPassant = false;
                if(color.equals("black") && pastIndex > 0){
                    Move lastMove = history.get(pastIndex - 1);
                    int lastStart = 63 - lastMove.getStart();
                    lastEnd = 63 - lastMove.getEnd();
                    String lastPiece = lastMove.getPiece().getType();
                    int lastDiff = Math.abs(lastStart - lastEnd);
                    if(lastPiece.equals("pawn") &&
                    lastDiff == 2 &&
                    second == lastEnd - 1)
                    {
                        enPassant = true;
                    }else enPassant = false;
                }else if (pastIndex > 0){
                    Move lastMove = history.get(pastIndex - 1);
                    int lastStart = lastMove.getStart();
                    lastEnd = lastMove.getEnd();
                    String lastPiece = lastMove.getPiece().getType();
                    int lastDiff = Math.abs(lastStart - lastEnd);
                    if(lastPiece.equals("pawn") &&
                    lastDiff == 2 &&
                    second == lastEnd - 1)
                    {
                        enPassant = true;
                    }else enPassant = false;
                }
                if(second == first + 7 && (board.get(second).getPiece() != null || enPassant)){canCapture = true; returny = true;}
                else if(second == first - 9 && (board.get(second).getPiece() != null || enPassant)){canCapture = true; returny = true;}
                //only move forward if piece is not in the way / first pawn move
                else if(second == first - 1 && board.get(second).getPiece() == null){canCapture = false; returny = true;}
                else if(second == first - 2 && board.get(second).getPiece() == null && board.get(first - 1).getPiece() == null && (Integer.parseInt(board.get(first).getSquare().charAt(1) + "") == 2 || (Integer.parseInt(board.get(first).getSquare().charAt(1) + "") == 7))){canCapture = false; returny = true;}
                canCapture = false;
            }else if(piece.equals("knight")){
                //knight moves in 'L's
                //high right +6
                //low right +15
                //high left -10
                //low left -17
                int firstCol = indexOf(alph, board.get(first).getSquare().charAt(0) + "");
                int firstRow = Integer.parseInt(board.get(first).getSquare().charAt(1)+"");
                int secCol = indexOf(alph, board.get(second).getSquare().charAt(0) + "");
                int secRow = Integer.parseInt(board.get(second).getSquare().charAt(1)+"");
                boolean onTop = firstRow == 8;
                boolean onBottom = firstRow == 1;
                boolean onRight = firstCol == 7;
                boolean onLeft = firstCol == 0;
                //high up right
                if(firstRow < 7 && secRow == firstRow + 2 && !onRight && secCol == firstCol + 1){canCapture = true; returny = true;}
                //low up right
                else if(!onTop && secRow == firstRow + 1 && firstCol < 6 && secCol == firstCol + 2){canCapture = true; returny = true;}
                //high up left
                else if(firstRow < 7 && secRow == firstRow + 2 && !onLeft && secCol == firstCol - 1) {canCapture = true; returny = true;}
                //low up left
                else if(!onTop && secRow == firstRow + 1 && firstCol > 1 && secCol == firstCol - 2) {canCapture = true; returny = true;}
                //far down right
                else if(firstRow > 2 && secRow == firstRow - 2 && !onRight && secCol == firstCol + 1) {canCapture = true; returny = true;}
                //close down right
                else if(!onBottom && secRow == firstRow - 1 && firstCol < 6 && secCol == firstCol + 2) {canCapture = true; returny = true;}
                //far down left
                else if(firstRow > 2 && secRow == firstRow - 2 && !onLeft && secCol == firstCol - 1) {canCapture = true; returny = true;}
                //close down left
                else if(!onBottom && secRow == firstRow - 1 && firstCol > 1 && secCol == firstCol - 2) {canCapture = true; returny = true;}
                else {canCapture = false; return false;}
            }else if(piece.equals("bishop")){
                //bishop only moves diagonally
                boolean diag = false;
                String secondSquare = board.get(second).getSquare();
                int secondRow = indexOf(alph, secondSquare.charAt(0) + "");
                int secondCol = Integer.parseInt(secondSquare.charAt(1) + "");
                String firstSquare = board.get(first).getSquare();
                int firstRow = indexOf(alph, firstSquare.charAt(0) + "");
                int firstCol = Integer.parseInt(firstSquare.charAt(1) + "");
                int colDiff = Math.abs(secondCol - firstCol);
                int rowDiff = Math.abs(secondRow - firstRow);
                if(colDiff == rowDiff) diag = true;
                if(diag){
                    //bishop cannot move through pieces
                    int col = firstCol;
                    int row = firstRow;
                    String notation1 = "";
                    int cStep = 0;
                    int rStep = 0;
                    if(firstCol < secondCol)cStep = 1;
                        else cStep = -1;
                        if(firstRow < secondRow) rStep=1;
                        else rStep = -1;
                    while(col != secondCol || row != secondRow){
                        row+=rStep;
                        col+=cStep;
                        notation1 = alph[row] + col;
                        Square check1 = null;
                        for(Square square : board){
                            if(square.getSquare().equals(notation1))check1 = square;
                        }
                        //making sure you can capture
                        if(check1.equals(board.get(second))){
                            if(check1.getPiece() != null){canCapture = true; returny = true;}
                            break;
                        }
                        //cant go though pieces
                        if(check1.getPiece() != null){canCapture = false; return false;}
                    }
                    canCapture = true;
                    returny = true;
                }
                else{canCapture = false; return false;}
                
            }else if(piece.equals("rook")){
                boolean straight = false;
                String secRow = board.get(second).getSquare().charAt(0) + "";
                int secCol = Integer.parseInt(board.get(second).getSquare().charAt(1) + "");
                String firstRow = board.get(first).getSquare().charAt(0) + "";
                int firstCol = Integer.parseInt(board.get(first).getSquare().charAt(1) + "");
                if(secRow.equals(firstRow) || secCol == firstCol) straight = true;
                if(straight){
                    if(firstCol != secCol){
                        int col = firstCol;
                        int row = indexOf(alph, firstRow);
                        int colDiff = 0;
                        String notation = "";
                        if(col < secCol) colDiff = 1;
                        else colDiff = -1;
                        while(col != secCol){
                            col += colDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check = square;
                            }
                            //can capture pieces
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; returny = true;}
                                break;
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }
                        canCapture = true;
                        returny = true;
                    }else if(firstCol == secCol){
                        int row = indexOf(alph,firstRow);
                        int sec = indexOf(alph, secRow);
                        int col = firstCol;
                        int rowDiff = 0;
                        String notation = "";
                        if(row < sec)rowDiff = 1;
                        else rowDiff = -1;
                        while(row != sec){
                            row += rowDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check= square;
                            }
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; returny = true;}
                                break;
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }
                        canCapture = true;
                        returny = true;
                    }
                }else{canCapture = false; return false;}
            }else if(piece.equals("queen")){
                boolean straight = false;
                String secRow = board.get(second).getSquare().charAt(0) + "";
                int secCol = Integer.parseInt(board.get(second).getSquare().charAt(1) + "");
                String firstRow = board.get(first).getSquare().charAt(0) + "";
                int firstCol = Integer.parseInt(board.get(first).getSquare().charAt(1) + "");
                if(secRow.equals(firstRow) || secCol == firstCol) straight = true;
                boolean diag = false;
                String secondSquare = board.get(second).getSquare();
                int secondRow = indexOf(alph, secondSquare.charAt(0) + "");
                int secondCol = Integer.parseInt(secondSquare.charAt(1) + "");
                String firstSquare = board.get(first).getSquare();
                int fr = indexOf(alph, firstSquare.charAt(0) + "");
                int colDiff = Math.abs(secondCol - firstCol);
                int rowDiff = Math.abs(secondRow - fr);
                if(colDiff == rowDiff) diag = true;
                if(straight){
                    if(firstCol != secCol){
                        int col = firstCol;
                        int row = indexOf(alph, firstRow);
                        int cDiff = 0;
                        String notation = "";
                        if(col < secCol) cDiff = 1;
                        else cDiff = -1;
                        while(col != secCol){
                            col += cDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check = square;
                            }
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; returny = true;}
                                break;
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }
                        canCapture = true;
                        returny = true;
                    }else if(firstCol == secCol){
                        int row = indexOf(alph,firstRow);
                        int sec = indexOf(alph, secRow);
                        int col = firstCol;
                        int rDiff = 0;
                        String notation = "";
                        if(row < sec)rDiff = 1;
                        else rDiff = -1;
                        while(row != sec){
                            row += rDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check= square;
                            }
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; returny = true;}
                                break;
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }
                        canCapture = true;
                        returny = true;
                    }
                }else if(diag){
                    //bishop cannot move through pieces
                    int col = firstCol;
                    int row = fr;
                    String notation1 = "";
                    int cStep = 0;
                    int rStep = 0;
                    if(firstCol < secondCol)cStep = 1;
                        else cStep = -1;
                        if(fr < secondRow) rStep=1;
                        else rStep = -1;
                    while(col != secondCol || row != secondRow){
                        row+=rStep;
                        col+=cStep;
                        notation1 = alph[row] + col;
                        Square check1 = null;
                        for(Square square : board){
                            if(square.getSquare().equals(notation1))check1 = square;
                        }
                        //making sure you can capture
                        if(check1.equals(board.get(second))){
                            if(check1.getPiece() != null){canCapture = true; returny = true;}
                            break;
                        }
                        //cant go though pieces
                        if(check1.getPiece() != null){canCapture = false; return false;}
                    }
                    canCapture = true;
                    returny = true;
                }else{canCapture = false; return false;}
            }else if(piece.equals("king")){
                int firstRow = indexOf(alph, board.get(first).getSquare().charAt(0) + "");
                int firstCol = Integer.parseInt(board.get(first).getSquare().charAt(1) + "");
                int secondRow = indexOf(alph, board.get(second).getSquare().charAt(0) + "");
                int secondCol = Integer.parseInt(board.get(second).getSquare().charAt(1) + "");
                boolean onBottom = firstRow == 0;
                boolean onTop = firstRow == 7;
                boolean onLeft = firstCol == 1;
                boolean onRight = firstCol == 8;
                //cannot move into an attack
                if(board.get(second).isAttacked()) return false;
                //castle
                if(color.equals("white")){
                    if(!board.get(first).getPiece().hasMoved() && secondRow == firstRow + 2 && secondCol == firstCol){
                        if(board.get(second).getPiece() != null || board.get(second).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                        if(board.get(second - 8).isAttacked() || board.get(second - 8).getPiece() != null){castlingShort  = false; castlingLong = false; return false;}
                        Square rookSquare = board.get(second + 8);
                        if(rookSquare.getPiece() != null && rookSquare.getPiece().getType().equals("rook")){
                            if(!rookSquare.getPiece().hasMoved()){
                                if(board.get(first).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                                castlingShort = true;
                                castlingLong = false;
                                return true;
                            }
                            else{castlingShort = false; castlingLong = false; return false;}
                        }
                        else{castlingShort = false; castlingLong = false; return false;}
                    }
                    else if(!board.get(first).getPiece().hasMoved() && secondRow == firstRow - 2 && secondCol == firstCol){
                        if(board.get(second).getPiece() != null || board.get(second).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                        if(board.get(second + 8).isAttacked() || board.get(second + 8).getPiece() != null){castlingShort = false; castlingLong = false; return false;}
                        if(board.get(second - 8).isAttacked() || board.get(second - 8).getPiece() != null){castlingShort = false; castlingLong = false; return false;}
                        Square rookSquare = board.get(second - 16);
                        if(rookSquare.getPiece() != null && rookSquare.getPiece().getType().equals("rook")){
                            if(!rookSquare.getPiece().hasMoved()){
                                if(board.get(first).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                                castlingLong = true;
                                castlingShort = false;
                                return true;
                            }
                            else {castlingShort = false; castlingLong = false; return false;}
                        }
                        else {castlingShort = false; castlingLong = false; return false;}
                    }else{castlingShort = false; castlingLong = false;}
                }
                if(color.equals("black")){
                    if(!board.get(first).getPiece().hasMoved() && secondRow == firstRow + 2 && secondCol == firstCol){
                        if(board.get(second).getPiece() != null || board.get(second).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                        if(board.get(second + 8).isAttacked() || board.get(second + 8).getPiece() != null){castlingShort = false; castlingLong = false; return false;}
                        Square rookSquare = board.get(second - 8);
                        if(rookSquare.getPiece() != null && rookSquare.getPiece().getType().equals("rook")){
                            if(!rookSquare.getPiece().hasMoved()){
                                if(board.get(first).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                                castlingShort = true;
                                castlingLong = false;
                                return true;
                            }
                            else{castlingShort = false; castlingLong = false; return false;}
                        }
                        else{castlingShort = false; castlingLong = false; return false;}
                    }else if(!board.get(first).getPiece().hasMoved() && secondRow == firstRow - 2 && secondCol == firstCol){
                        if(board.get(second).getPiece() != null || board.get(second).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                        if(board.get(second -8).isAttacked() || board.get(second - 8).getPiece() != null){castlingShort = false; castlingLong = false; return false;}
                        if(board.get(second + 8).isAttacked() || board.get(second + 8).getPiece() != null){castlingShort = false; castlingLong = false; return false;}
                        Square rookSquare = board.get(second + 16);
                        if(rookSquare.getPiece() != null && rookSquare.getPiece().getType().equals("rook")){
                            if(!rookSquare.getPiece().hasMoved()){
                                if(board.get(first).isAttacked()){castlingShort = false; castlingLong = false; return false;}
                                castlingLong = true;
                                castlingShort = false;
                                return true;
                            }
                            else{castlingShort = false; castlingLong = false; return false;}
                        }
                        else{castlingShort = false; castlingLong = false; return false;}
                    }else{castlingShort = false; castlingLong = false;}
                }
                //move up/down
                if(secondCol == firstCol){
                    if(!onTop && secondRow == firstRow + 1){canCapture = true; returny = true;}
                    else if(!onBottom && secondRow == firstRow -1){canCapture = true; returny = true;}
                    else {canCapture = false; return false;}
                }
                //move left/right
                else if(secondRow == firstRow){
                    if(!onRight && secondCol == firstCol + 1){canCapture = true; returny = true;}
                    else if (!onLeft && secondCol == firstCol - 1){canCapture = true; returny = true;}
                    else{canCapture = false; return false;}
                }
                //move diag right up/down
                else if(!onRight && secondCol == firstCol + 1){
                    if(!onTop && secondRow == firstRow + 1){canCapture = true; returny = true;}
                    else if(!onBottom && secondRow == firstRow - 1){canCapture = true; returny = true;}
                    else {canCapture = false; return false;}
                }
                //move diag left up/down
                else if(!onLeft && secondCol == firstCol - 1){
                    if(!onTop && secondRow == firstRow + 1){canCapture = true; returny = true;}
                    else if(!onBottom && secondRow == firstRow - 1) {canCapture = true; returny = true;}
                    else {canCapture = false; return false;}
                }
                
                else{canCapture = false; return false;}
            }
            if(returny){
                List<Square> testerBoard = new ArrayList<Square>();
                for(Square square : board){
                    Square copy = new Square(square.getX(), square.getY());
                    Piece copyPiece = square.getPiece() == null ? null : new Piece(square.getPiece().getType(), square.getPiece().getColor(), square.getPiece().getImage());
                    if(square.getPiece() != null){ copyPiece.setMoved(square.getPiece().hasMoved()); copyPiece.setChecked(square.getPiece().isInCheck());}
                    copy.setAttacked(square.isAttacked());
                    copy.setSquare(square.getSquare());
                    copy.setChecked(square.isChecked());
                    copy.setPiece(copyPiece);
                    testerBoard.add(copy);
                }
                testerBoard.get(second).setPiece(testerBoard.get(first).getPiece());
                testerBoard.get(first).removePiece();
                Square king = null;
                for(Square square : testerBoard){
                    if(square.getPiece() != null && square.getPiece().getType().equals("king") && square.getPiece().getColor().equals(color)){ king = square; break;}
                }
                for(Square square : testerBoard){
                    square.setAttacked(false);
                }

                int idk = 0;

                List<Square> flippedBoard = getFlippedBoard(testerBoard);

                for(Square attacker : flippedBoard){
                    if(attacker.getPiece() != null){

                        int target = 0;

                        for(Square check : flippedBoard){

                            if(isLegal(idk, target, pastIndex, flippedBoard, history, isFlipped, idk)){
                                if(true){
                                    if(!attacker.getPiece().getColor().equals(color)){testerBoard.get(63 - flippedBoard.indexOf(check)).setAttacked(true);}
                                }
                                
                            }

                            target++;
                        }
                    }

                    idk++;
                }
                if(!king.isAttacked())return true; else return false;
            }
            else return false;
        }
        {canCapture = false; return false;}
        
    }
    public static boolean isLegal(int first, int second, int pastIndex, List<Square> board, List<Move> history, boolean isFlipped, int sum){
        String piece = board.get(first).getPiece().getType();
        String color = board.get(first).getPiece().getColor();
        if(first == second) return false;
        if((/*color.equals("white") && pastIndex % 2 == 0) || (color.equals("black") && pastIndex % 2 == 1*/ true)){
            if(piece.equals("pawn")){
                //pawn cant move backwards
                //top right  +7
                //top left  -9
                //bottom right  +9
                //bottom left -7
                //pawn only moves diagonally if its capturing / en passant
                
                if(second == first + 7 && (board.get(second).getPiece() != null || enPassant)){return true;}
                else if(second == first - 9 && (board.get(second).getPiece() != null || enPassant)){return true;}
                if(second == first + 7){ canCapture = true; return true;}
                else if(second == first - 9) {canCapture = true; return true;}
                //only move forward if piece is not in the way / first pawn move
                canCapture = false;
                return false;
            }else if(piece.equals("knight")){
                //knight moves in 'L's
                //high right +6
                //low right +15
                //high left -10
                //low left -17
                int firstCol = indexOf(alph, board.get(first).getSquare().charAt(0) + "");
                int firstRow = Integer.parseInt(board.get(first).getSquare().charAt(1)+"");
                int secCol = indexOf(alph, board.get(second).getSquare().charAt(0) + "");
                int secRow = Integer.parseInt(board.get(second).getSquare().charAt(1)+"");
                boolean onTop = firstRow == 8;
                boolean onBottom = firstRow == 1;
                boolean onRight = firstCol == 7;
                boolean onLeft = firstCol == 0;
                //high up right
                if(firstRow < 7 && secRow == firstRow + 2 && !onRight && secCol == firstCol + 1){canCapture = true; return true;}
                //low up right
                else if(!onTop && secRow == firstRow + 1 && firstCol < 6 && secCol == firstCol + 2){canCapture = true; return true;}
                //high up left
                else if(firstRow < 7 && secRow == firstRow + 2 && !onLeft && secCol == firstCol - 1) {canCapture = true; return true;}
                //low up left
                else if(!onTop && secRow == firstRow + 1 && firstCol > 1 && secCol == firstCol - 2) {canCapture = true; return true;}
                //far down right
                else if(firstRow > 2 && secRow == firstRow - 2 && !onRight && secCol == firstCol + 1) {canCapture = true; return true;}
                //close down right
                else if(!onBottom && secRow == firstRow - 1 && firstCol < 6 && secCol == firstCol + 2) {canCapture = true; return true;}
                //far down left
                else if(firstRow > 2 && secRow == firstRow - 2 && !onLeft && secCol == firstCol - 1) {canCapture = true; return true;}
                //close down left
                else if(!onBottom && secRow == firstRow - 1 && firstCol > 1 && secCol == firstCol - 2) {canCapture = true; return true;}
                else {canCapture = false; return false;}
            }else if(piece.equals("bishop")){
                //bishop only moves diagonally
                boolean diag = false;
                String secondSquare = board.get(second).getSquare();
                int secondRow = indexOf(alph, secondSquare.charAt(0) + "");
                int secondCol = Integer.parseInt(secondSquare.charAt(1) + "");
                String firstSquare = board.get(first).getSquare();
                int firstRow = indexOf(alph, firstSquare.charAt(0) + "");
                int firstCol = Integer.parseInt(firstSquare.charAt(1) + "");
                int colDiff = Math.abs(secondCol - firstCol);
                int rowDiff = Math.abs(secondRow - firstRow);
                if(colDiff == rowDiff) diag = true;
                if(diag){
                    //bishop cannot move through pieces
                    int col = firstCol;
                    int row = firstRow;
                    String notation1 = "";
                    int cStep = 0;
                    int rStep = 0;
                    if(firstCol < secondCol)cStep = 1;
                        else cStep = -1;
                        if(firstRow < secondRow) rStep=1;
                        else rStep = -1;
                    while(col != secondCol || row != secondRow){
                        row+=rStep;
                        col+=cStep;
                        notation1 = alph[row] + col;
                        Square check1 = null;
                        for(Square square : board){
                            if(square.getSquare().equals(notation1))check1 = square;
                        }
                        //making sure you can capture
                        if(check1.equals(board.get(second))){
                            if(check1.getPiece() != null){canCapture = true; return true;}
                        }
                        //cant go though pieces
                        if(check1.getPiece() != null){canCapture = false; return false;}
                    }
                    canCapture = true;
                    return true;
                }
                else{canCapture = false; return false;}
                
            }else if(piece.equals("rook")){
                boolean straight = false;
                String secRow = board.get(second).getSquare().charAt(0) + "";
                int secCol = Integer.parseInt(board.get(second).getSquare().charAt(1) + "");
                String firstRow = board.get(first).getSquare().charAt(0) + "";
                int firstCol = Integer.parseInt(board.get(first).getSquare().charAt(1) + "");
                if(secRow.equals(firstRow) || secCol == firstCol) straight = true;
                if(straight){
                    if(firstCol != secCol){
                        int col = firstCol;
                        int row = indexOf(alph, firstRow);
                        int colDiff = 0;
                        String notation = "";
                        if(col < secCol) colDiff = 1;
                        else colDiff = -1;
                        while(col != secCol){
                            col += colDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check = square;
                            }
                            //can capture pieces
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; return true;}
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }
                    }else if(firstCol == secCol){
                        int row = indexOf(alph,firstRow);
                        int sec = indexOf(alph, secRow);
                        int col = firstCol;
                        int rowDiff = 0;
                        String notation = "";
                        if(row < sec)rowDiff = 1;
                        else rowDiff = -1;
                        while(row != sec){
                            row += rowDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check= square;
                            }
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; return true;}
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }

                    }
                }

                else{canCapture = false; return false;}
            }else if(piece.equals("queen")){
                boolean straight = false;
                String secRow = board.get(second).getSquare().charAt(0) + "";
                int secCol = Integer.parseInt(board.get(second).getSquare().charAt(1) + "");
                String firstRow = board.get(first).getSquare().charAt(0) + "";
                int firstCol = Integer.parseInt(board.get(first).getSquare().charAt(1) + "");
                if(secRow.equals(firstRow) || secCol == firstCol) straight = true;
                boolean diag = false;
                String secondSquare = board.get(second).getSquare();
                int secondRow = indexOf(alph, secondSquare.charAt(0) + "");
                int secondCol = Integer.parseInt(secondSquare.charAt(1) + "");
                String firstSquare = board.get(first).getSquare();
                int fr = indexOf(alph, firstSquare.charAt(0) + "");
                int colDiff = Math.abs(secondCol - firstCol);
                int rowDiff = Math.abs(secondRow - fr);
                if(colDiff == rowDiff) diag = true;
                if(straight){
                    if(firstCol != secCol){
                        int col = firstCol;
                        int row = indexOf(alph, firstRow);
                        int cDiff = 0;
                        String notation = "";
                        if(col < secCol) cDiff = 1;
                        else cDiff = -1;
                        while(col != secCol){
                            col += cDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check = square;
                            }
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; return true;}
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }
                    }else if(firstCol == secCol){
                        int row = indexOf(alph,firstRow);
                        int sec = indexOf(alph, secRow);
                        int col = firstCol;
                        int rDiff = 0;
                        String notation = "";
                        if(row < sec)rDiff = 1;
                        else rDiff = -1;
                        while(row != sec){
                            row += rDiff;
                            notation = alph[row] + col;
                            Square check = null;
                            for(Square square : board){
                                if(square.getSquare().equals(notation)) check= square;
                            }
                            if(check.equals(board.get(second))){
                                if(check.getPiece() != null){canCapture = true; return true;}
                            }
                            //cant go though pieces
                            if(check.getPiece() != null){canCapture = false; return false;}
                        }
                    }
                }else if(diag){
                    //bishop cannot move through pieces
                    int col = firstCol;
                    int row = fr;
                    String notation1 = "";
                    int cStep = 0;
                    int rStep = 0;
                    if(firstCol < secondCol)cStep = 1;
                        else cStep = -1;
                        if(fr < secondRow) rStep=1;
                        else rStep = -1;
                    while(col != secondCol || row != secondRow){
                        row+=rStep;
                        col+=cStep;
                        notation1 = alph[row] + col;
                        Square check1 = null;
                        for(Square square : board){
                            if(square.getSquare().equals(notation1))check1 = square;
                        }
                        //making sure you can capture
                        if(check1.equals(board.get(second))){
                            if(check1.getPiece() != null){canCapture = true; return true;}
                        }
                        //cant go though pieces
                        if(check1.getPiece() != null){canCapture = false; return false;}
                    }
                }else{canCapture = false; return false;}
            }else if(piece.equals("king")){
                int firstRow = indexOf(alph, board.get(first).getSquare().charAt(0) + "");
                int firstCol = Integer.parseInt(board.get(first).getSquare().charAt(1) + "");
                int secondRow = indexOf(alph, board.get(second).getSquare().charAt(0) + "");
                int secondCol = Integer.parseInt(board.get(second).getSquare().charAt(1) + "");
                boolean onBottom = firstRow == 0;
                boolean onTop = firstRow == 7;
                boolean onLeft = firstCol == 1;
                boolean onRight = firstCol == 8;
                //move up/down
                if(secondCol == firstCol){
                    if(!onTop && secondRow == firstRow + 1){canCapture = true; return true;}
                    else if(!onBottom && secondRow == firstRow -1){canCapture = true; return true;}
                    else {canCapture = false; return false;}
                }
                //move left/right
                else if(secondRow == firstRow){
                    if(!onRight && secondCol == firstCol + 1){canCapture = true; return true;}
                    else if (!onLeft && secondCol == firstCol - 1){canCapture = true; return true;}
                    else{canCapture = false; return false;}
                }
                //move diag right up/down
                else if(!onRight && secondCol == firstCol + 1){
                    if(!onTop && secondRow == firstRow + 1){canCapture = true; return true;}
                    else if(!onBottom && secondRow == firstRow - 1){canCapture = true; return true;}
                    {canCapture = false; return false;}
                }
                //move diag left up/down
                else if(!onLeft && secondCol == firstCol - 1){
                    if(!onTop && secondRow == firstRow + 1){canCapture = true; return true;}
                    else if(!onBottom && secondRow == firstRow - 1) {canCapture = true; return true;}
                    {canCapture = false; return false;}
                }
                else{canCapture = false; return false;}
            }
            return true;
        }
        return false;
    }
    public static int indexOf(String[] arr, String target){
        int count = 0;
        for(String value : arr){
            if(value.equals(target)) return count;
            count++;
        }
        return -1;
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

    public static List<Square> getFlippedBoard(List<Square> board){
        List<Square> flipped = new ArrayList<Square>();
        makeBoard(flipped);
        for(int i = 0; i < board.size(); i++){
            flipped.get(63-i).setPiece(board.get(i).getPiece());
            flipped.get(63-i).setSquare(board.get(i).getSquare());
            flipped.get(63 - i).setAttacked(board.get(i).isAttacked());
        }
        return flipped;
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
            if(!toggle && board.indexOf(square) != first) g.setColor(new Color(212,212,216));
            else if(toggle && board.indexOf(square) != first) g.setColor(new Color(120,120,126));
            else if(board.indexOf(square) == first || board.indexOf(square) == second)g.setColor(new Color(255, 255, 0));
            //if(square.isAttacked()) g.setColor(new Color(255, 0, 0));
            //if(square.getSquare().equals("d2")) System.out.println("d2 being attacked is " + square.isAttacked());
            if(mated && square.getSquare().equals(matedKing)){
                g.setColor(Color.RED);
            }
            count++;
            if(count != 8)toggle = !toggle;
            else count = 0;
            g.fillRect(square.getX(), square.getY(), 80, 80);
            g.setColor(Color.BLACK);
            g.drawRect(square.getX(), square.getY(), 80, 80);
            if(square.getPiece() != null) g.drawImage(square.getPiece().getImage(), square.getX(), square.getY(), 80, 80, null);
            if(first != -1 && board.get(first).getPiece() != null){
                if(board.indexOf(square) != first){
                    if(isLegal(first, board.indexOf(square), pastIndex, board, history, isFlipped)){
                        g.setColor(Color.DARK_GRAY);
                        int size = 20;
                        g.fillOval(square.getX() + (80/2 - size/2), square.getY() + (80/2 - size/2), size, size);
                    }
                }
            }
            
        }
    }
    
    public static void makeBoard(List<Square> board){
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

    public int makeOptions(){
        int choice = JOptionPane.showOptionDialog(null, 
        "Choose your promotion:", 
        "PORMOTION",
        JOptionPane.DEFAULT_OPTION, 
        JOptionPane.INFORMATION_MESSAGE, 
        null, options, options[0]);

        System.out.println("You chose: " + options[choice]);
        return choice;
    }

    
    
}
