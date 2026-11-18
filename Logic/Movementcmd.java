package Logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static Logic.Piece.la;

public class Movementcmd{
    public Piece piece;
    protected String interedLabel;
    protected ArrayList<Integer> StartingPoint;
    protected ArrayList<Integer> Destination;
    protected ArrayList<Integer> Displacement;
    protected boolean isLegal;
    Rule rule = new Rule(this);

    public Movementcmd(String interedLabel,ArrayList<Integer> StartingPoint,ArrayList<Integer> Destination){
        this.interedLabel=interedLabel;
        this.StartingPoint = StartingPoint;
        this.Destination =Destination;
        this.Displacement =sub(this.Destination,this.StartingPoint);
        Rule rule = new Rule(this);
        if(rule.checkpiece()){this.piece=piecedecoder(this.StartingPoint);}

    }
    public Piece piecedecoder(ArrayList StartingPoint){
        //todo
        Piece pi=null;
        if(StartingPoint.equals(la)){

            ArrayList<Piece> TH = new ArrayList<>();
            TH.addAll(Game.White.hand);TH.addAll(Game.Black.hand);
            for(Piece p:TH){if(Objects.equals(p.label, interedLabel)){this.piece=p; return p;}}

            }

        else{pi=Game.board.getPieceAt(StartingPoint);}
        return pi;}

    public void perform(){

        if (rule.legal()) {
            this.piece = piecedecoder(StartingPoint);
            piece.move();
            Game.nextTurn();
        }

    }

    public static ArrayList<Integer> sub(ArrayList<Integer> a, ArrayList<Integer> b){
           int ans1=(int)a.get(0)-(int)b.get(0);
           int ans2=(int)a.get(1)-(int)b.get(1);
           ArrayList<Integer> answer=new ArrayList<Integer>(Arrays.asList(ans1,ans2));
           return answer;}
}