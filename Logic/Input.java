package Logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Input{
    protected String[]  listcmd;
    public static String interedlabel;

    public Movementcmd read(Scanner sc){

        String strcmd= sc.nextLine();
        listcmd = strcmd.split(" ");


        if(Objects.equals(listcmd[0], "0")){Game.go =false;Game.go2=false;return null;}
        else{
        interedlabel = listcmd[0];
        return new Movementcmd(interedlabel,posdecoder(listcmd[1]),posdecoder(listcmd[2]));}

    }
    private ArrayList posdecoder(String postr){
        ArrayList decoded = new ArrayList<Integer>();
        decoded.add(Character.getNumericValue(postr.charAt(0)));
        decoded.add(Character.getNumericValue(postr.charAt(1)));
        return decoded;}


}