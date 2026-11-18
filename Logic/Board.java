package Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Board {

    static HashMap<ArrayList,Piece> boardmap;


    public Board(){boardmap =new HashMap<ArrayList,Piece>();boardUpdate();}



    public String[][] table = new String[5][5];
    public boolean isOccupied(ArrayList<Integer> pos){return !(boardmap.get(pos)==(null));}
    public Piece getPieceAt(ArrayList<Integer> pos){return boardmap.get(pos);}
    public void boardUpdate(){
        boardmap.clear();
        for(Piece pie:Game.listOfPieces){
            boardmap.put(pie.pos, pie);
        }




    }
    public String boardToString(){
        String str="";
        for(int i=1;i<6;i++){
            for(int j=1;j<6;j++){
                ArrayList<Integer> key=new ArrayList<Integer>(Arrays.asList(j,i));
                if(boardmap.get(key)==(null)){str=str.concat("-");}
                    else{str=str.concat(boardmap.get(key).label);}

            }
        }
        return str;
    }



}
