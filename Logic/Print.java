package Logic;
import java.util.ArrayList;

public class Print {
    public static ArrayList<String> prtStr=new ArrayList<>();
    public static void print(){
        String strBoard= Game.board.boardToString();
        prtStr.add(strBoard);
        System.out.println(strBoard);

        //part3
        String prtb="";
        if(Game.Black.hand!=null){
        for(Piece st:Game.Black.hand){prtb=prtb.concat(st.label);}}
        System.out.println(prtb);
        prtStr.add(prtb);

        //part2
        String prtw="";
        if(Game.White.hand!=null){
            for(Piece st:Game.White.hand){prtw=prtw.concat(st.label);}}
        System.out.println(prtw);
        prtStr.add(prtw);



    }
}
