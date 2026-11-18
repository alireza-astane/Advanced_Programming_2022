import Logic.*;

import java.util.Scanner;

public class Main {
    static Game game = new Game();
    static Scanner sc = new Scanner(System.in);


    public static void main(String[] args) throws Exception {

        do{
            Game.cmd =game.in.read(sc);
            if(Game.go){Game.cmd.perform();}
            Game.board.boardUpdate();
            if(Game.go){Print.print();}
        } while (Game.go);
        if(Game.go2)Print.print();
        if(Game.winner!=null){System.out.print(Game.winner.name + " wins!");}
    }
}
