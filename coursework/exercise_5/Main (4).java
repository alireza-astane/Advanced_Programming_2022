import java.util.*;

public class Main {
    public static String collectionName;
    public static int inputSize;
    public static String command;
    public static ArrayList<Integer> arrayList;
    public static LinkedHashMap<Integer, String> map;
    public static Scanner sc;

    public static void main(String[] args) {
        sc = new Scanner(System.in);
        String input1 = sc.nextLine();
        ArrayList<String> list1 = new ArrayList<>(Arrays.asList(input1.split(" ")));
        collectionName = list1.get(0);
        inputSize = Integer.parseInt(list1.get(1));
        command = list1.get(2);

        if (Objects.equals(collectionName, "ArrayList")) {
            String[] input2 = sc.nextLine().split(" ");
            ArrayList<String> list2 = new ArrayList<>(Arrays.asList(input2));
            arrayList = new ArrayList<>();
            for (String str : list2) {
                arrayList.add(Integer.valueOf(str));
            }
        } else {
            map = new LinkedHashMap<>();

            for (int i = 0; i < inputSize; i++) {
                String[] strList = sc.nextLine().split(" ");
                int key = Integer.parseInt(strList[0]);
                String value = strList[1];
                map.put(key, value);
            }

        }
        switch (command) {
            case "insert" : Insert.perform();break;
            case "contains" : Contains.perform();break;
            case "containsKey" : ContainsKey.perform();break;
            case "extractReverseRejoin" : ExtractReverseRejoin.perform();break;
            case "getIndex" : GetIndex.perform();break;
            case "getSize" : GetSize.perform();break;
            case "getValue" : GetValue.perform();break;
            case "modify" : Modify.perform();break;
            case "remove" : Remove.perform();break;
            case "retrieve" : Retrieve.perform();break;
            case "sort" : Sort.perform();break;
            case "sortByKeys" : SortByKeys.perform();break;
            case "sortByValues" : SortByٰValues.perform();break;
            case "swap" : Swap.perform();break;
            case "reverse" : Reverse.perform();break;
            case "print" : Print.perform();break;
        }


    }

    public static class Insert {
        public static int index;
        public static int num;

        public static void perform() {
            String[] strList = sc.nextLine().split(" ");
            index = Integer.parseInt(strList[0]);
            num = Integer.parseInt(strList[1]);

            ArrayList<Integer> arrayList1 = arrayList;
            arrayList1.add(index, num);
            for(Integer integer:arrayList1){
                System.out.print(integer+" ");}
        }
    }

    public static class Contains {
        public static int num;

        public static void perform() {
            num = Integer.parseInt(sc.nextLine());

            ArrayList<Integer> arrayList1 = arrayList;
            if (arrayList1.contains(num)) {
                System.out.println("y");
            } else {
                System.out.println("n");
            }
        }
    }

    public static class ExtractReverseRejoin {
        public static int index;

        public static void perform() {
            index = Integer.parseInt(sc.nextLine());
            ArrayList<Integer> arrayList1 = new ArrayList<>();
            for(int i=0;i<index;i++){
                arrayList1.add(arrayList.get(i));}


            ArrayList<Integer> arrayList2 = new ArrayList<>();
            for(int j=index;j< arrayList.size();j++){
                arrayList2.add(arrayList.get(j));}

            Collections.reverse(arrayList1);
            arrayList1.addAll(arrayList2);

            for(Integer integer:arrayList1){
                System.out.print(integer+" ");}
        }
    }

    public static class GetValue{
        public static int key;

        public static void perform() {
            key = Integer.parseInt(sc.nextLine());

            System.out.println(map.get(key));
        }
    }

    public static class GetSize{
        public static void perform() {
            System.out.println(arrayList.size());
        }
    }

    public static class GetIndex {
        public static int num;

        public static void perform() {
            num = Integer.parseInt(sc.nextLine());

            System.out.println(arrayList.indexOf(num));
        }

    }

    public static class Remove{
        public static int index;
        public static int key;


        public static void perform() {
            if(Objects.equals(collectionName, "ArrayList")){
                index = Integer.parseInt(sc.nextLine());}
            else {key = Integer.parseInt(sc.nextLine());}


            if(Objects.equals(collectionName, "ArrayList")){
                ArrayList<Integer> arrayList1 = arrayList;
                arrayList1.remove(index);
                for(Integer integer:arrayList1){
                    System.out.print(integer+" ");}
            }
            else {
                map.remove(key);
                TreeMap<Integer,String> treeMap = new TreeMap<>();
                for(Integer i:map.keySet()){
                    treeMap.put(i,map.get(i));
                }

                StringBuilder str = new StringBuilder();
                for(Integer key: treeMap.keySet()){
                    str.append("(").append(key).append(",").append(treeMap.get(key)).append(") ");
                }
                System.out.println(str);
            }

        }
    }

    public static class Modify{
        public static int index;
        public static int num;


        public static void perform() {
            String[] strList = sc.nextLine().split(" ");
            index = Integer.parseInt(strList[0]);
            num = Integer.parseInt(strList[1]);


            ArrayList<Integer> arrayList1 = arrayList;
            arrayList1.remove(index);
            arrayList1.add(index,num);
            for(Integer integer:arrayList1){
                System.out.print(integer+" ");}
        }
    }

    public static class Retrieve{
        public static int index;

        public static void perform() {
            index = Integer.parseInt(sc.nextLine());

            System.out.println(arrayList.get(index));
        }
    }

    public static class Sort{

        public static void perform() {
            ArrayList<Integer> arrayList1 = arrayList;
            Collections.sort(arrayList1);
            for(Integer integer:arrayList1){
                System.out.print(integer+" ");}
        }
    }

    public static class SortByKeys{

        public static void perform() {
            LinkedHashMap<Integer,String> treeMap = new LinkedHashMap<>();
            List<Integer> sortedKeys=new ArrayList<Integer>(map.keySet());
            Collections.sort(sortedKeys);
            for(Integer i:sortedKeys){
                treeMap.put(i,map.get(i));
            }

            StringBuilder str = new StringBuilder();
            for(Integer key: treeMap.keySet()){
                str.append("(").append(key).append(",").append(treeMap.get(key)).append(") ");
            }
            System.out.println(str);
        }
    }

    public static class SortByٰValues{

        public static void perform() {
            LinkedHashMap<Integer,String> treeMap = new LinkedHashMap<>();

            List<Integer> mapKeys = new ArrayList<>(map.keySet());
            List<String> mapValues = new ArrayList<>(map.values());
            Collections.sort(mapValues);
            Collections.sort(mapKeys);


            for (String val : mapValues) {
                Iterator<Integer> keyIt = mapKeys.iterator();

                while (keyIt.hasNext()) {
                    Integer key = keyIt.next();
                    String comp1 = map.get(key);

                    if (comp1.equals(val)) {
                        keyIt.remove();
                        treeMap.put(key, val);
                        break;
                    }
                }
            }

            StringBuilder str = new StringBuilder();
            for(Integer key: treeMap.keySet()){
                str.append("(").append(key).append(",").append(treeMap.get(key)).append(") ");
            }
            System.out.println(str);



        }
    }

    public static class Swap{
        public static int index1;
        public static int index2;


        public static void perform() {
            String[] strList = sc.nextLine().split(" ");
            index1 = Integer.parseInt(strList[0]);
            index2 = Integer.parseInt(strList[1]);



            ArrayList<Integer> arrayList1 = arrayList;
            Collections.swap(arrayList1,index1,index2);
            for(Integer integer:arrayList1){
                System.out.print(integer+" ");}
        }
    }

    public static class Reverse{

        public static void perform() {
            ArrayList<Integer> arrayList1 =arrayList;
            Collections.reverse(arrayList1);
            for(Integer integer:arrayList1){
                System.out.print(integer+" ");}
        }
    }

    public static class ContainsKey{
        public static int num;

        public static void perform() {
            num = Integer.parseInt(sc.nextLine());


            if(map.containsKey(num)){System.out.println("y");}
            else{System.out.println("n");}
        }
    }

    public static class Print{
        public static void perform(){
            StringBuilder str = new StringBuilder();
            for(Integer key: map.keySet()){
                str.append("(").append(key).append(",").append(map.get(key)).append(") ");
            }
            System.out.println(str);
        }

    }

}







