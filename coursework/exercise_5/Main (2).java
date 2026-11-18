import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Input.input();
        Matrix.run();
    }

    public static class Matrix {
        private static final DecimalFormat df = new DecimalFormat("0.00");
        public static void run() {
            if(Input.commandChar.equals("m")){multiplication(Input.matrixA,Input.matrixB);}
            if(Input.commandChar.equals("t")){System.out.println(transpose(Input.matrixA));}
            if(Input.commandChar.equals("r")){trace(Input.matrixA);}
            if(Input.commandChar.equals("n")){print(norm(Input.matrixA));}
        }

        public static double Double_trace(ArrayList<ArrayList<Integer>> matrix){
            if(matrix.size()!=matrix.get(0).size()){
                System.out.println("Dimension Error!");
                return 0;
            }
            else{
                int trace = 0 ;
                for(int i=0;i<matrix.size();i++){
                    trace +=matrix.get(i).get(i);
                }
                return trace;
            }
        }

        public static void trace(ArrayList<ArrayList<Integer>> matrix){
            if(matrix.size()!=matrix.get(0).size()){
                System.out.println("Dimension Error!");
            }
            else{
                int trace = 0 ;
                for(int i=0;i<matrix.size();i++){
                    trace +=matrix.get(i).get(i);
                }
                print(trace);
            }
        }


        public static void multiplication(ArrayList<ArrayList<Integer>> a,ArrayList<ArrayList<Integer>> b){
            if(a.get(0).size()!=b.size()){
                System.out.println("Dimension Error!");
            }

            else{
                ArrayList<ArrayList<Integer>> result = new ArrayList<>();
                for(int i=0;i<a.size();i++){
                    ArrayList<Integer> arrayList = new ArrayList<>();
                    for(int j=0;j<b.get(0).size();j++){
                        int m = 0;
                        for(int k=0;k<b.size();k++){
                            m += a.get(i).get(k) * b.get(k).get(j);
                        }
                        arrayList.add(m);
                    }
                    result.add(arrayList);
                }
                System.out.println(result);
            }
        }

        public static ArrayList<ArrayList<Integer>> transpose(ArrayList<ArrayList<Integer>> matrix){
            ArrayList<ArrayList<Integer>> transpose = new ArrayList<>();
            for(int i=0;i<matrix.get(0).size();i++){
                ArrayList<Integer> arrayList = new ArrayList<>();
                for(int j=0;j<matrix.size();j++){
                    arrayList.add(matrix.get(j).get(i));
                }
                transpose.add(arrayList);
            }
            return transpose;
        }

        public static double norm(ArrayList<ArrayList<Integer>> matrix){
            int k = 0;
            for(int i=0;i<matrix.size();i++){
                for(int j=0;j<matrix.get(0).size();j++){
                    k += Math.pow(matrix.get(i).get(j),2);
                }
            }
            return Math.sqrt(k);
            //return Math.sqrt(Double_trace(Objects.requireNonNull(multiplication(transpose(matrix), matrix))));
        }

        public static void print(double m){
            if((int)m == m){System.out.println((int)m);}
            else{System.out.println(df.format(m));}
        }

    }

    public static class Input {
        public static ArrayList<ArrayList<Integer>> matrixA;
        public static ArrayList<ArrayList<Integer>> matrixB;
        public static String commandChar;
        public static Scanner sc = new Scanner(System.in);


        public static void input(){
            commandChar = sc.nextLine();
            matrixA = inputMatrix();
            if (Objects.equals(commandChar, "m")){
                matrixB = inputMatrix();
            }
        }


        public static ArrayList<ArrayList<Integer>> inputMatrix(){
            ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();

            String[] list = sc.nextLine().split(" ");
            int a = Integer.parseInt(list[0]);
            int b = Integer.parseInt(list[1]);
            list = sc.nextLine().split(" ");

            for(int i=0;i<a;i++){
                ArrayList<Integer> arrayList = new ArrayList<>();
                for(int j=0;j<b;j++){
                    arrayList.add(Integer.valueOf(list[i*b+j]));
                }
                matrix.add(arrayList);

            }
            return matrix;







        }

    }




}


