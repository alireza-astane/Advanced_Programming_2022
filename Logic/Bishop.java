//import Pieces.Player;
package Logic;
import util.resource.ImageResource;

import java.util.ArrayList;
import java.util.Arrays;

public class Bishop extends Piece{


    public Bishop(ArrayList<Integer> pos, Player owner, String type, String label){super(pos, owner, type, label);
    }

    public ImageResource getIconName(){
        if(owner==Game.White){return isUpgraded()?ImageResource.UP_BISHOP_W:ImageResource.BISHOP_W;}
        else{return isUpgraded()?ImageResource.UP_BISHOP_B:ImageResource.BISHOP_B;}
    }

    public ArrayList<ArrayList<Integer>> getMobilities(){
        int k;
        if(owner==Game.White){k=1;}
        else{k=-1;}
        ArrayList<Integer> m1 = new ArrayList<>(Arrays.asList(k,k));
        ArrayList<Integer> m2 = new ArrayList<>(Arrays.asList(2*k,2*k));
        ArrayList<Integer> m3 = new ArrayList<>(Arrays.asList(3*k,3*k));
        ArrayList<Integer> m4 = new ArrayList<>(Arrays.asList(4*k,4*k));
        ArrayList<Integer> m5 = new ArrayList<>(Arrays.asList(-1*k,1*k));
        ArrayList<Integer> m6 = new ArrayList<>(Arrays.asList(-2*k,2*k));
        ArrayList<Integer> m7 = new ArrayList<>(Arrays.asList(-3*k,3*k));
        ArrayList<Integer> m8 = new ArrayList<>(Arrays.asList(-4*k,4*k));

        ArrayList<ArrayList<Integer>> mobilities1 = new ArrayList<>(Arrays.asList(m1,m2,m3,m4,m5,m6,m7,m8));

        ArrayList<Integer> m9= new ArrayList<>(Arrays.asList(-1*k,-1*k));
        ArrayList<Integer> m10 = new ArrayList<>(Arrays.asList(-2*k,-2*k));
        ArrayList<Integer> m11= new ArrayList<>(Arrays.asList(-3*k,-3*k));
        ArrayList<Integer> m12= new ArrayList<>(Arrays.asList(-4*k,-4*k));
        ArrayList<Integer> m13= new ArrayList<>(Arrays.asList(1*k,-1*k));
        ArrayList<Integer> m14= new ArrayList<>(Arrays.asList(2*k,-2*k));
        ArrayList<Integer> m15 = new ArrayList<>(Arrays.asList(3*k,-3*k));
        ArrayList<Integer> m16= new ArrayList<>(Arrays.asList(4*k,-4*k));
        ArrayList<Integer> m17 = new ArrayList<>(Arrays.asList(k,0));
        ArrayList<Integer> m18= new ArrayList<>(Arrays.asList(0,k));
        ArrayList<Integer> m19 = new ArrayList<>(Arrays.asList(-k,0));
        ArrayList<Integer> m20= new ArrayList<>(Arrays.asList(0,-k));

        ArrayList<ArrayList<Integer>> mobilities2 = new ArrayList<>(Arrays.asList(m1,m2,m3,m4,m5,m6,m7,m8,m9,m10,m11,m12,m13,m14,m15,m16,m17,m18,m19,m20));
        if(this.upgraded){return mobilities2;}
        else{return  mobilities1;}
    }





}

