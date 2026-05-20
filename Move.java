public class Move {
    private int start;
    private int end;
    private Piece piece;
    private Piece capturedPiece;
    public Move(int start, int end, Piece piece){
        this.start = start;
        this.end = end;
        this.piece = piece;
        capturedPiece = null;
    }

    public Move(int start, int end, Piece piece, Piece capturedPiece){
        this.start = start;
        this.end = end;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
    }

    public int getStart(){return start;}
    public int getEnd(){return end;}
    public Piece getPiece(){return piece;}
    public Piece getCaptured(){return capturedPiece;}
}
