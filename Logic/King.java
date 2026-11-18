package  Logic;
import util.resource.ImageResource;

import java.util.ArrayList;
import java.util.Arrays;

public class King extends Piece{


    public King(ArrayList<Integer> pos, Player owner, String type, String label) {
        super(pos, owner, type, label);
    }
    public ArrayList<ArrayList<Integer>> getMobilities(){
        int k;
        if(owner==Game.White){k=1;}
        else{k=-1;}
        ArrayList<Integer> m1 = new ArrayList<>(Arrays.asList(1*k,0));
        ArrayList<Integer> m2 = new ArrayList<>(Arrays.asList(1*k,1*k));
        ArrayList<Integer> m3 = new ArrayList<>(Arrays.asList(0,1*k));
        ArrayList<Integer> m4 = new ArrayList<>(Arrays.asList(-1*k,1*k));
        ArrayList<Integer> m5 = new ArrayList<>(Arrays.asList(-1*k,0));
        ArrayList<Integer> m6 = new ArrayList<>(Arrays.asList(-1*k,-1*k));
        ArrayList<Integer> m7 = new ArrayList<>(Arrays.asList(0,-1*k));
        ArrayList<Integer> m8 = new ArrayList<>(Arrays.asList(1*k,-1*k));
        ArrayList<ArrayList<Integer>> mobilities = new ArrayList<>(Arrays.asList(m1,m2,m3,m4,m5,m6,m7,m8));
        return mobilities;}
    public ImageResource getIconName(){
        if(owner==Game.White){return ImageResource.KING_W;}
        else{return ImageResource.KING_B;}
    }
}
