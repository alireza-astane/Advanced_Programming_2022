package Logic;
import util.resource.ImageResource;

import java.util.ArrayList;
import java.util.Arrays;
public class GoldenGeneral extends Piece{


    public GoldenGeneral(ArrayList<Integer> pos, Player owner, String type, String label) {
        super(pos, owner, type, label);
    }
    public ArrayList<ArrayList<Integer>> getMobilities(){
        int k;
        if(owner==Game.White){k=1;}
        else{k=-1;}
        ArrayList<Integer> m1 = new ArrayList<>(Arrays.asList(k,0));
        ArrayList<Integer> m2 = new ArrayList<>(Arrays.asList(k,k));
        ArrayList<Integer> m3 = new ArrayList<>(Arrays.asList(0,k));
        ArrayList<Integer> m4 = new ArrayList<>(Arrays.asList(-k,k));
        ArrayList<Integer> m5 = new ArrayList<>(Arrays.asList(-k,0));
        ArrayList<Integer> m6 = new ArrayList<>(Arrays.asList(0,-k));
        ArrayList<ArrayList<Integer>> mobilities = new ArrayList<>(Arrays.asList(m1,m2,m3,m4,m5,m6));
        return mobilities;}

    public ImageResource getIconName(){
        if(owner==Game.White){return ImageResource.GOLDENGENERAL_W;}
        else{return ImageResource.GOLDENGENERAL_B;}
    }
}
