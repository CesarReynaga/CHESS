//import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Piece {
    private String type;
    private String color;
    private BufferedImage image;
    private boolean hasMoved;
    private boolean isInCheck;
    public Piece(String piece, String color,  BufferedImage image){
        type = piece;
        this.image = image;
        this.color = color;
        hasMoved = false;
        isInCheck = false;
    }

    public String getType(){
        return type;
    }

    public boolean isInCheck(){return isInCheck;}
    public void setChecked(boolean check){isInCheck = check;}

    public boolean hasMoved(){return hasMoved;}
    public void setMoved(boolean moved){hasMoved = moved;}
    public BufferedImage getImage(){
        return image;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getColor(){
        return color;
    }

    public void setColor(String color){
        this.color = color;
    }
}

