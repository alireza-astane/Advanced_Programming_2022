package Logic;
import util.resource.ImageResource;
import java.util.ArrayList;
import java.util.Arrays;


public class Piece {

    public String  label;

    public Piece(ArrayList<Integer> pos, Player owner, String type, String label){
          this.pos=pos;
          this.owner=owner;
          this.label=label;
          this.type=type;
          Game.listOfPieces.add(this);
    }

    public static ArrayList<Integer> la = new ArrayList<Integer>(Arrays.asList(0, 0));
    public Player owner;
    public String type;
    public ArrayList<Integer> pos;
    public boolean upgraded = false;

    public void move() {
        pick();
        put();
    }

    public void setUpgrade(boolean order) {
        this.upgraded = order;

    }

    public void setPos(ArrayList<Integer> posi) {
        this.pos = posi;
    }

    public boolean isUpgraded() {
        return upgraded;
    }

    public boolean fromHand() {
        return pos.equals(la);
    }

    public void pick() {
        if (fromHand()) {
            owner.hand.remove(this);
        }
    }

    public void put() {
        if (Game.board.isOccupied(Game.cmd.Destination)) {
            hitted(Game.board.getPieceAt(Game.cmd.Destination));
        }
        if ((!isUpgraded()) && !fromHand() &&(this.owner==Game.White)&& ((int)Game.cmd.Destination.get(1) >3)) {
            setUpgrade(true);}
        if ((!isUpgraded()) && !fromHand() &&(this.owner==Game.Black)&& ((int)Game.cmd.Destination.get(1) < 3)) {
            setUpgrade(true);}

        setPos(Game.cmd.Destination);
    }


    public void hitted(Piece obj) {
        captured(obj);
        if (obj.type == "King") {
            Game.winner=Game.cmd.piece.owner;
            Game.endGame();}

    }
    public void captured(Piece obj){
        obj.setUpgrade(false);
        switchOwner(obj);
        obj.pos=la;
        Game.cmd.piece.owner.hand.add(obj);
    }
    public void switchOwner(Piece obj){

        if(Character.isUpperCase(obj.label.charAt(0))){obj.label=obj.label.toLowerCase();}
        else{obj.label=obj.label.toUpperCase();}
        if (obj.owner.equals(Game.White)){obj.owner=Game.Black;}
        else{obj.owner=Game.White;}
    }

    public ArrayList<ArrayList<Integer>> getMobilities() {return null;}
    public ImageResource getIconName() {
        return null;
    }
}




