import javax.swing.*;

public class Tile extends JButton {
    public int r, c;
    public boolean isFixed;

    public Tile(int r, int c) {
        this.r = r;
        this.c = c;
    }
}
