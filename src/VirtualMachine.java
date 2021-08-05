import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class VirtualMachine {
    private static ArrayList<Object> ds = new ArrayList<>();
    private static ArrayList<String[]> quad = new ArrayList<>();

    public static void initializeDS(Scanner symbols){
        while(symbols.hasNextLine()){
            String[] line = symbols.nextLine().split(" ");
            if(line[0].equals("Integer")){
                if(line.length == 4){
                    int temp = Integer.parseInt(line[3].substring(0,line[3].length()));
                    ds.add(temp);
                }
                else{
                    int temp = 0;
                    ds.add(temp);
                }
            }
            else if(line[0].equals("char")){
                if(line.length == 4){
                    char temp = line[3].charAt(1);
                    ds.add(temp);
                }
                else{
                    char temp = 0;
                    ds.add(temp);
                }
            }

        }
    }
    public static void fillQuad(Scanner machineCode){
        while(machineCode.hasNextLine()){
            String[] line = machineCode.nextLine().split(" ");
            quad.add(line);
        }
    }
    public static void main(String[] args) throws IOException {
        Translator.start();
        File fp = new File("translator-symboltable.txt");
        File fp2 = new File("machine-code.txt");
        try{
            Scanner symbols = new Scanner(fp);
            Scanner machineCode = new Scanner(fp2);
            initializeDS(symbols);
            fillQuad(machineCode);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        int j, i, k, t1, t2;
        Scanner userInput = new Scanner(System.in);
        for(int pc = 0; pc<quad.size();pc++){
            String[] inst = quad.get(pc);
            switch(inst[0]){
                case "0":
                    // =
                    j = Integer.parseInt(inst[1]);
                    i = Integer.parseInt(inst[2]);
                    ds.set(i, ds.get(j));
                    break;
                case "1":
                    // +
                    j = Integer.parseInt(inst[1]);
                    i = Integer.parseInt(inst[2]);
                    k = Integer.parseInt(inst[3]);
                    t1 = (int)ds.get(j);
                    t2 = (int)ds.get(i);
                    ds.set(k, t1 + t2);
                    break;
                case "2":
                    // -
                    j = Integer.parseInt(inst[1]);
                    i = Integer.parseInt(inst[2]);
                    k = Integer.parseInt(inst[3]);
                    t1 = (int)ds.get(j);
                    t2 = (int)ds.get(i);
                    ds.set(k, t1 - t2);
                    break;
                case "3":
                    //*
                    j = Integer.parseInt(inst[1]);
                    i = Integer.parseInt(inst[2]);
                    k = Integer.parseInt(inst[3]);
                    t1 = (int)ds.get(j);
                    t2 = (int)ds.get(i);
                    ds.set(k, t1 * t2);
                    break;
                case "4":
                    // /
                    j = Integer.parseInt(inst[1]);
                    i = Integer.parseInt(inst[2]);
                    k = Integer.parseInt(inst[3]);
                    t1 = (int)ds.get(j);
                    t2 = (int)ds.get(i);
                    ds.set(k, t1 / t2);
                    break;
                case "5":
                    // output
                    j = Integer.parseInt(inst[1]);
                    System.out.print(ds.get(j));
                    break;
                case "6":
                    //output line
                    j = Integer.parseInt(inst[1]);
                    System.out.println(ds.get(j));
                    break;
                case "7":
                    //input
                    i = Integer.parseInt(inst[1]);
                    System.out.print("Input: ");
                    ds.set(i, userInput.nextInt());
                    userInput.nextLine();
                    break;
                case "8":
                    //goto
                    pc = Integer.parseInt(inst[1]) - 1;
                    break;
                case "9":
                    // <
                    i = Integer.parseInt(inst[1]);
                    j = Integer.parseInt(inst[2]);
                    t1 = (int)ds.get(i);
                    t2 = (int)ds.get(j);
                    if (t1 < t2){
                        pc = Integer.parseInt(inst[3]) - 1;
                    }
                    break;
                case "10":
                    // <=
                    i = Integer.parseInt(inst[1]);
                    j = Integer.parseInt(inst[2]);
                    t1 = (int)ds.get(i);
                    t2 = (int)ds.get(j);
                    if (t1 <= t2){
                        pc = Integer.parseInt(inst[3]) - 1;
                    }
                    break;
                case "11":
                    // >
                    i = Integer.parseInt(inst[1]);
                    j = Integer.parseInt(inst[2]);
                    t1 = (int)ds.get(i);
                    t2 = (int)ds.get(j);
                    if (t1 > t2){
                        pc = Integer.parseInt(inst[3]) - 1;
                    }
                    break;
                case "12":
                    // >=
                    i = Integer.parseInt(inst[1]);
                    j = Integer.parseInt(inst[2]);
                    t1 = (int)ds.get(i);
                    t2 = (int)ds.get(j);
                    if (t1 >= t2){
                        pc = Integer.parseInt(inst[3]) - 1;
                    }
                    break;
                case "13":
                    // ==
                    i = Integer.parseInt(inst[1]);
                    j = Integer.parseInt(inst[2]);
                    t1 = (int)ds.get(i);
                    t2 = (int)ds.get(j);
                    if (t1 == t2){
                        pc = Integer.parseInt(inst[3]) - 1;
                    }
                    break;
                case "14":
                    // !=
                    i = Integer.parseInt(inst[1]);
                    j = Integer.parseInt(inst[2]);
                    t1 = (int)ds.get(i);
                    t2 = (int)ds.get(j);
                    if (t1 != t2){
                        pc = Integer.parseInt(inst[3]) - 1;
                    }
                    break;
            }
        }
    }
}
