package Logic;
import util.resource.ImageResource;

import java.util.ArrayList;
import java.util.Arrays;

public class Pawn extends Piece{

    public Pawn(ArrayList<Integer> pos, Player owner, String type, String label) {
        super(pos, owner, type, label);
    }
    public ArrayList<ArrayList<Integer>> getMobilities(){
        int k;
        if(owner==Game.White){k=1;}
        else{k=-1;}
        ArrayList<Integer> m1 = new ArrayList<>(Arrays.asList(0,k));
        ArrayList<ArrayList<Integer>> mobilities1 = new ArrayList<>(Arrays.asList(m1));

        ArrayList<Integer> m2 = new ArrayList<>(Arrays.asList(k,0));
        ArrayList<Integer> m3 = new ArrayList<>(Arrays.asList(k,k));
        ArrayList<Integer> m4 = new ArrayList<>(Arrays.asList(-k,k));
        ArrayList<Integer> m5 = new ArrayList<>(Arrays.asList(-k,0));
        ArrayList<Integer> m6 = new ArrayList<>(Arrays.asList(0,-k));
        ArrayList<ArrayList<Integer>> mobilities2 = new ArrayList<>(Arrays.asList(m1,m2,m3,m4,m5,m6));
        if(this.upgraded){return mobilities2;}
        else{return  mobilities1;}
    }
    public ImageResource getIconName(){
        if(owner==Game.White){return isUpgraded()?ImageResource.UP_PAWN_W:ImageResource.PAWN_W;}
        else{return isUpgraded()?ImageResource.UP_PAWN_B:ImageResource.PAWN_B;}
    }

}
