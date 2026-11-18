package Logic;
import java.util.ArrayList;

public class Player {
    public Player(String name){
        this.hand=new ArrayList<Piece>();
        this.name=name;
    }

    public ArrayList<Piece> hand;
    public String name;
}
