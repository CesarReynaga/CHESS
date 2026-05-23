
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed,aPressed, dPressed, shiftPressed, ePressed, qPressed, rPressed, pPressed;
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_UP){
            upPressed = true;
        }

        if(code == KeyEvent.VK_DOWN){
            downPressed = true;

        }
        if(code == KeyEvent.VK_LEFT){
            leftPressed = true;

        }
        if(code == KeyEvent.VK_RIGHT){
            rightPressed = true;
        }
        if(code == KeyEvent.VK_A){
            aPressed = true;
        }
        if(code == KeyEvent.VK_D){
            dPressed = true;
        }
        if(code == KeyEvent.VK_Q){
            qPressed = true;
        }
        if(code == KeyEvent.VK_E){
            ePressed = true;
        }
        if(code == KeyEvent.VK_R){
            rPressed = true;
        }
        if(code == KeyEvent.VK_P){
            pPressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_UP){
            upPressed = false;
        }
        if(code == KeyEvent.VK_DOWN){
            downPressed = false;
        }
        if(code == KeyEvent.VK_LEFT){
            leftPressed = false;
        }
        if(code == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }
        if(code == KeyEvent.VK_A) {
            aPressed = false;
        }
        if(code == KeyEvent.VK_D) {
            dPressed = false;
        }
        if(code == KeyEvent.VK_Q) {
            qPressed = false;
        }
        if(code == KeyEvent.VK_E) {
            ePressed = false;
        }
        if(code == KeyEvent.VK_R){
            rPressed = false;
        }
        if(code == KeyEvent.VK_P){
            pPressed = false;
        }



    }
}