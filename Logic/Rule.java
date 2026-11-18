package Logic;
import java.util.ArrayList;
import static Logic.Piece.la;
public class Rule {
    Movementcmd cmd;
    public Rule(Movementcmd cmd){
        this.cmd=cmd;
    }
    public boolean legal(){
        boolean ans = false;
        if(checkRanges()){if(checkpiece()){if(checkturn()){if(checkDestination()){if(checkMove()){ans=true;}}}}}
        return ans;}

    public boolean checkRanges(){return ((cmd.StartingPoint.get(0)<6)&&(cmd.StartingPoint.get(0)>=0)&&(cmd.StartingPoint.get(1)>=0)&&(cmd.StartingPoint.get(1)<6)&&(cmd.Destination.get(0)<6)&&(cmd.Destination.get(0)>=0)&&(cmd.Destination.get(1)>=0)&&(cmd.Destination.get(1)<6));}

    public boolean checkLabel(){
        return(cmd.piecedecoder(cmd.StartingPoint).label.equals(cmd.interedLabel));}


    public boolean checkpiece(){boolean ans=false;
        if(!(cmd.piecedecoder(cmd.StartingPoint)==(null))){if(checkLabel()){ans=true;}}
        return ans;}

    public boolean checkturn(){
         return (Game.turn==cmd.piece.owner);
    }

    public boolean checkNoSkip(){return !(cmd.Destination==cmd.StartingPoint);}
    public boolean isOccupiedByAllie(){
        boolean ans=false;
        if(DestinationisOccuoied()){ans=(cmd.piece.owner==Game.board.getPieceAt(cmd.Destination).owner);}
        return ans;
    }
    public boolean checkDestination(){
        boolean ans=false;
        if(checkNoSkip()&&!isOccupiedByAllie()&&checkHandCase()&&checkNotHand()){ans=true;}
        return  ans;
    }
    public boolean DestinationisOccuoied(){
        return (Game.board.getPieceAt(cmd.Destination)!=null);
    }
    public boolean checkHandCase(){boolean ans =true;
        if(DestinationisOccuoied()&&cmd.piece.fromHand()){ans=false;}
        return ans;
    }

    public boolean checkMove(){
        boolean ans;
        if(!cmd.piece.fromHand()){

        ans=((cmd.piece.getMobilities().contains(cmd.Displacement))&&!isblocked());}
        else{ans=true;}
        return  ans;
    }
    public boolean checkNotHand(){
        return ((cmd.Destination.get(0)!=0)&&(cmd.Destination.get(1)!=0));
        }



    public boolean isblocked(){
        boolean ans =false;
        if(!cmd.StartingPoint.equals(la)){

            int n=pathLength();
            for(int i=1;i<n;i++){

                if(Game.board.getPieceAt(Movementcmd.sub(cmd.StartingPoint, mul(cmd.Displacement, (-(double)i / n)))) != null)
                {ans=true;break;}}
        }
        return ans;}
    public int pathLength(){
        int ans = Math.max(Math.abs(cmd.Displacement.get(0)),Math.abs(cmd.Displacement.get(1)));
        return ans;
    }
    public ArrayList<Integer> mul(ArrayList<Integer> a,double num){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        for(double i:a){ans.add((int)(i*num));}

        return ans;
    }
}




