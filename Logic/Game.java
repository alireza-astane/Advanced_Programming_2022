//import Pieces.Player;
package  Logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ArrayList;

public class Game {
    public static boolean go=true;
    public static Movementcmd cmd;
    public static boolean go2=true;
    public Input in = new Input();
    public static ArrayList<Piece> listOfPieces = new ArrayList<Piece>();
    public static Player turn;
    public static Player winner;
    public Game(){

        this.White = new Player("white");
        this.Black = new Player("black");
        this.turn = Black;
        ArrayList<Integer> pK = new ArrayList<Integer>(Arrays.asList(1,1));
        Piece K = new King(pK,White,"King","K");
        ArrayList<Integer> pk = new ArrayList<Integer>(Arrays.asList(5,5));
        Piece k = new King(pk,Black,"King","k");
        ArrayList<Integer> pG = new ArrayList<Integer>(Arrays.asList(2,1));
        Piece G = new GoldenGeneral(pG,White,"GoldenGeneral","G");
        ArrayList<Integer> pg = new ArrayList<Integer>(Arrays.asList(4,5));
        Piece g = new GoldenGeneral(pg,Black,"GoldenGeneral","g");
        ArrayList<Integer> pS = new ArrayList<Integer>(Arrays.asList(3,1));
        Piece S = new SilverGeneral(pS,White,"SilverGeneral","S");
        ArrayList<Integer> ps = new ArrayList<Integer>(Arrays.asList(3,5));
        Piece s = new SilverGeneral(ps,Black,"SilverGeneral","s");
        ArrayList<Integer> pB = new ArrayList<Integer>(Arrays.asList(4,1));
        Piece B = new Bishop(pB,White,"Bishop","B");
        ArrayList<Integer> pb = new ArrayList<Integer>(Arrays.asList(2,5));
        Piece b = new Bishop(pb,Black,"Bishop","b");
        ArrayList<Integer> pL = new ArrayList<Integer>(Arrays.asList(5,1));
        Piece L = new Lance(pL,White,"Lance","L");
        ArrayList<Integer> pl = new ArrayList<Integer>(Arrays.asList(1,5));
        Piece l = new Lance(pl,Black,"Lance","l");
        ArrayList<Integer> pP = new ArrayList<Integer>(Arrays.asList(1,2));
        Piece P = new Pawn(pP,White,"Pawn","P");
        ArrayList<Integer> pp= new ArrayList<Integer>(Arrays.asList(5,4));
        Piece p = new Pawn(pp,Black,"Pawn","p");
        this.board = new Board();

    }
    public static Board board;
    public static Player White;
    public static Player Black;
    public static void endGame(){go=false;}

    public static void nextTurn(){
        if(turn==White){turn=Black;}
        else{turn=White;}

    }





}
