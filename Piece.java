//import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Piece {
    private String type;
    private String color;
    private BufferedImage image;
    public Piece(String piece, String color,  BufferedImage image){
        type = piece;
        this.image = image;
        this.color = color;
    }

    public String getType(){
        return type;
    }

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

