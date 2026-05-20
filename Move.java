public class Move {
    private Square start;
    private Square end;
    private Piece piece;
    private Piece capturedPiece;
    public Move(Square start, Square end, Piece piece){
        this.start = start;
        this.end = end;
        this.piece = piece;
        capturedPiece = null;
    }

    public Move(Square start, Square end, Piece piece, Piece capturedPiece){
        this.start = start;
        this.end = end;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
    }

    public Square getStart(){return start;}
    public Square getEnd(){return end;}
    public Piece getPiece(){return piece;}
    public Piece getCaptured(){return capturedPiece;}
}
