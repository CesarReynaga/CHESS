import java.awt.Point;
/**
 * Write a description of class Block here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Square
{
    private int x;
    private int y;
    public Square(int x , int y){
        this.x = x;
        this.y = y;
    }
    
    public boolean click(int clickX, int clickY){
        return (clickX >= x && clickX < x + 30) &&
           (clickY >= y && clickY < y + 30);
    }
    
    public Point getPoint(){
        Point point = new Point(x, y);
        return point;
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
}