public class Move {
    private int start;
    private int end;
    private Piece piece;
    private Piece capturedPiece;
    private boolean isSpecial;
    private String special;
    public Move(int start, int end, Piece piece){
        this.start = start;
        this.end = end;
        this.piece = piece;
        capturedPiece = null;
        special = "";
        isSpecial = false;
    }

    public Move(int start, int end, Piece piece, String special){
        this.start = start;
        this.end = end;
        this.piece = piece;
        isSpecial = true;
        this.special = special;
    }

    public Move(int start, int end, Piece piece, Piece capturedPiece){
        this.start = start;
        this.end = end;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        isSpecial = false;
        special = "";
    }

    public Move(int start, int end, Piece piece, Piece capturedPiece, String special){
        this.start = start;
        this.end = end;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.special = special;
        isSpecial = true;
    }

    public String getType(){return special;}
    public boolean isSpecial(){return isSpecial;}
    public int getStart(){return start;}
    public int getEnd(){return end;}
    public Piece getPiece(){return piece;}
    public Piece getCaptured(){return capturedPiece;}
}
