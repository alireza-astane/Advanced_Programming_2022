
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args){
        Tank.input();
        Tank.print(Tank.add(Tank.data1,Tank.data2));
    }

    public static class Tuple {
        public int a,b;
        public Tuple(int a,int b){
            this.a=a;
            this.b=b;
        }
        @Override
        public String toString(){
            return "("+this.a+","+this.b+")";

        }
    }

    public static class Tank {
        public static LinkedList<LinkedList<Tuple>> data1 = new LinkedList<>();
        public static LinkedList<LinkedList<Tuple>> data2 = new LinkedList<>();
        public static int n;
        public static int m;

        public static void input() {
            Scanner sc = new Scanner(System.in);
            String[] strings = sc.nextLine().split(" ");
            m = Integer.parseInt(strings[0]);
            n = Integer.parseInt(strings[1]);
            for(int i=0;i<m;i++){
                data1.add(new LinkedList<>());
                data2.add(new LinkedList<>());
            }
            int t1 = Integer.parseInt(sc.nextLine());

            for(int i=0;i<t1;i++){
                strings = sc.nextLine().split(" ");
                Tuple tuple = new Tuple(Integer.parseInt(strings[1]),Integer.parseInt(strings[2]));
                data1.get(Integer.parseInt(strings[0])).add(tuple);
            }

            int t2 = Integer.parseInt(sc.nextLine());

            for(int i=0;i<t2;i++){
                strings = sc.nextLine().split(" ");
                Tuple tuple = new Tuple(Integer.parseInt(strings[1]),Integer.parseInt(strings[2]));
                data2.get(Integer.parseInt(strings[0])).add(tuple);
            }
        }

        public static void print(LinkedList<LinkedList<Tuple>> data){
            System.out.println(data.toString());
        }


        public static LinkedList<LinkedList<Tuple>> add(LinkedList<LinkedList<Tuple>> data1, LinkedList<LinkedList<Tuple>> data2) {
            LinkedList<LinkedList<Tuple>> data = new LinkedList<>();
            for(int i=0;i<data1.size();i++){
                LinkedList<Tuple> linkedList1 = new LinkedList<>();
                LinkedList<Tuple> linkedList2 = new LinkedList<>();
                linkedList1.addAll(data1.get(i));
                linkedList1.addAll(data2.get(i));
                for(int m=0;m<linkedList1.size();m++){
                    int a = linkedList1.get(m).a;
                    int b = linkedList1.get(m).b;
                    for(int n=m+1;n<linkedList1.size();n++){
                        if(linkedList1.get(n).a == linkedList1.get(m).a){b += linkedList1.get(n).b;linkedList1.remove(linkedList1.get(n));}
                    }
                    Tuple tuple = new Tuple(a,b);
                    linkedList2.add(tuple);
                }
                linkedList2.sort(comparator);
                data.add(linkedList2);
            }
            return data;
        }

        public static Comparator<Tuple> comparator = new Comparator<Tuple>() {
            @Override
            public int compare(Tuple o1, Tuple o2) {
                return (o1.a - o2.a);
            }};
    }




}
