import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static Stack<Integer> W = new Stack<>();
    public static Stack<Integer> S = new Stack<>();
    public static Stack<Integer> E = new Stack<>();
    public static Stack<Integer> step = new Stack<>();
    public static Stack<Integer> P = new Stack<>();

    public static void main(String[] args){run();
    }

    public static void F1(){
        if(W.size()!=0){
            S.add(W.pop());
            step.add(1);
        }
    }

    public static void F2(){
        if(W.size()!=0){
            E.add(W.pop());
            step.add(2);
        }
    }

    public static void F3(){
        if(S.size()!=0){
            E.add(S.pop());
            step.add(3);
        }
    }

    public static void run(){
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());
        String[] strings = sc.nextLine().split(" ");
        for(String str :strings){
            P.add(Integer.valueOf(str));
        }
        for(int i=1;i<n+1;i++){
            W.add(i);
        }

        while (P.size()>0){
            if((S.size()>0)&&(Objects.equals(P.get(P.size()-1), S.get(S.size()-1)))){
                F3();
                P.pop();
            }
            else{
                //if(!W.contains(P.get(P.size()-1))){
                if(W.size() < P.get(P.size()-1)){
                    System.out.println(-1);
                    return;}

                else{
                    int k = W.size()-P.get(P.size()-1);
                    for(int i=0;i<(k);i++){
                        F1();
                    }
                    F2();
                    P.pop();
                }}
        }

        System.out.println(step.size());
        for (Integer integer : step) {
            System.out.print(integer + " ");
        }
    }
}