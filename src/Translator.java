import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.*;

public class Translator {
    private static int count = 0;
    private static int address = 0;
    private static int n = 1;
    private static int tempvariablecount = 1;
    private static int tabCount = 0;
    private static String look = null;
    private static ArrayList<String> tokens;
    private static ArrayList<String> parsertokens =new ArrayList<>();
    private static StringBuffer parsetree = new StringBuffer();
    private static StringBuffer threeaddresscode = new StringBuffer();
    private static ArrayList<String> translatorsymbols = new ArrayList<>();
    private static StringBuffer machinecode = new StringBuffer();

    public static String findAddress(String name, ArrayList<String> symboltable){
        String address = null;
        for (String entry: symboltable) {
            String[] splitEntry = entry.split(" ");
            if(splitEntry[1].equals(name)){
                address = splitEntry[2];
                break;
            }
        }
        return address;
    }

    public static String matchRel(String rel){
        String code = null;
        switch (rel){
            case ("LT"):
                code = "9";
                break;
            case("LE"):
                code = "10";
                break;
            case("GT"):
                code ="11";
                break;
            case("GE"):
                code ="12";
                break;
            case("EQ"):
                code ="13";
                break;
            case("NE"):
                code ="14";
                break;
        }
        return code;
    }

    public static void print(int tab, String input){
        for(int i=0;i<tabCount;i++){
            parsetree.append("-");
        }
        parsetree.append("|_");
        parsetree.append(input);
        parsetree.append("\n");
    }

    public static String nextTok(){
        if (count == tokens.size())
            return null;
        else
            return tokens.get(count++);
    }

    public static void match(String tok){
        if (look.equals(tok)){
            look = nextTok();
        }
        else{
            System.out.println("Invalid token");
        }
    }
    public static void machineBackpatch(int lineNo, int value){
        int i = 0, j = 0;
        while (i != lineNo){
            if (machinecode.charAt(j) == '\n'){
                i++;
            }
            j++;
        }
        j--;
        StringBuffer tmp = new StringBuffer(machinecode.substring(0, j) + " "+Integer.toString(value)+machinecode.substring(j));
        machinecode = tmp;

    }
    public static void backpatch(int lineNo, int value){
        int i = 0, j = 0;
        while (i != lineNo){
            if (threeaddresscode.charAt(j) == '\n'){
                i++;
            }
            j++;
        }
        j--;
        StringBuffer tmp = new StringBuffer(threeaddresscode.substring(0, j) + " "+Integer.toString(value)+threeaddresscode.substring(j));
        threeaddresscode = tmp;
    }

    public static void writeToFile(ArrayList<String> tokens, String filename){
        try {
            File fp = new File(filename);
            fp.createNewFile();
            FileWriter fw = new FileWriter(fp);
            for(int i = 0;i<tokens.size();i++){
                fw.write(tokens.get(i) + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void start(){
        Lexer lex = new Lexer();
        String filePath = lex.getFilePath();
        try {
            File fp = new File(filePath);
            String buffer = Files.readString(Path.of(filePath));
            Translator.tokens = lex.getTokens(buffer);
            writeToFile(Translator.tokens, "words.txt");

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Translator translator = new Translator();

        //parser-translator code
        Translator.look = nextTok();
        translator.S();
        writeToFile(Translator.parsertokens, "parser-symboltable.txt");
        writeToFile(Translator.translatorsymbols, "translator-symboltable.txt");
        File fp = new File("parsetree.txt");

        try{
            fp.createNewFile();
            FileWriter fw = new FileWriter("parsetree.txt");
            fw.write(Translator.parsetree.toString());
            fw.close();
            fw = new FileWriter("tac.txt");
            fw.write(Translator.threeaddresscode.toString());
            fw.close();
            String mc = machinecode.toString();
            //String finalMachineCode = mc.replaceAll("#\n", "");
            fw = new FileWriter("machine-code.txt");
            fw.write(mc);
            fw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String newtemp(){
        String tempvar = "t" + Integer.toString(tempvariablecount++);
        return tempvar;
    }
    public void S(){
        if (look == null) {
            ;
        }
        else{
            if (!look.equals("(BRACKET, '}')")) {
                tabCount++;
                print(tabCount, "STATS");
                STATS();
                print(tabCount, "AFTER");
                AFTER();
                print(tabCount, "S");
                S();
                tabCount--;
            }
        }
    }
    public void STATS(){
        tabCount++;
        if(look.substring(1,3).equals("ID")){
            if(tokens.get(count).equals("(ASSIGNOP, ^)")){
                print(tabCount,"AS");
                AS();
            }
            else if(tokens.get(count).equals("(BRACKET, '(')")){
                print(tabCount,"FUNCALL");
                FUNCALL(null);
            }
            else{
                print(tabCount,"E");
                E();
            }
        }
        else if(look.equals("(Integer, ^)") || look.equals("(char, ^)")){
            print(tabCount,"D");
            D();
        }
        else if(look.equals("(if, ^)")){
            print(tabCount,"C");
            C();
        }
        else if(look.equals("(while, ^)")){
            print(tabCount,"L");
            L();
        }
        else if(look.equals("(in, ^)")){
            print(tabCount,"INP");
            INP();
        }
        else if(look.equals("(print, ^)") || look.equals("(println, ^)")){
            print(tabCount,"P");
            P();
        }
        else if(look.equals("(func, ^)")){
            print(tabCount,"FUN");
            FUN();
        }
        else if(look.equals("(ret, ^)")){
            print(tabCount,"RET");
            RET();
        }
        else if(look.substring(1,8).equals("COMMENT")){
            print(tabCount, look);
            match(look);
        }
        else if(look.substring(1,8).equals("LITERAL")){
            print(tabCount, look);
            match(look);
        }
        else if(look.substring(1,6).equals("RELOP")){
            print(tabCount, look);
            match(look);
        }
        else if(look.substring(1,4).equals("NUM")){
            print(tabCount, look);
            match(look);
        }
        else if(look.equals("('-', ^)")){
            print(tabCount, look);
            match(look);
        }
        else if(look.equals("('+', ^)")){
            print(tabCount, look);
            match(look);
        }
        else if(look.equals("('*', ^)")){
            print(tabCount, look);
            match(look);
        }
        else if(look.equals("('/', ^)")){
            print(tabCount, look);
            match(look);
        }
        else{
            System.out.println("Invalid token");
        }

        tabCount--;
    }
    public void AFTER(){
        if (look != null) {
            tabCount++;
            if (look.equals("(SYNOP, ';')")) {
                print(tabCount, "(SYNOP, ';')");
                match("(SYNOP, ';')");
            }
            tabCount--;
        }
    }

    //assignment statement
    public void AS(){
        tabCount++;
        if (look.substring(1,3).equals("ID")){
            print(tabCount, look);
            String lexeme = look.substring(6, look.length()-2);
            match(look);
            if (look.equals("(ASSIGNOP, ^)")){
                print(tabCount, "(ASSIGNOP, ^)");
                match("(ASSIGNOP, ^)");
            }

            if (tokens.get(count).equals("(BRACKET, '(')")){
                FUNCALL(lexeme);
            }
            else{
                print(tabCount, "VAR");
                String result = VAR(lexeme);
                if (result != null){
                    threeaddresscode.append(lexeme + " = " + result + "\n");
                    if(!Pattern.matches("[_a-zA-Z]\\w*",result)){
                        String temp = newtemp();
                        translatorsymbols.add("Integer "+temp + " " + Integer.toString(address)+ " " + result);
                        result = Integer.toString(address);
                        address++;
                        machinecode.append("0 " + result + " " + findAddress(lexeme,translatorsymbols)+"\n");
                    }
                    else
                        machinecode.append("0 " + findAddress(result,translatorsymbols) + " " + findAddress(lexeme,translatorsymbols)+"\n");
                    n++;

                }
            }
        }
        tabCount--;
    }
    public String VAR(String lex){
        tabCount++;
        String val = null;
        if (look.substring(1,8).equals("LITERAL")){
            print(tabCount, look);
            String tmp = newtemp();
            translatorsymbols.add("char " + tmp + " " + Integer.toString(address) + " " + look.substring(10, look.length()-1));
            address++;
            threeaddresscode.append(lex + '=' + look.substring(10, look.length()-1) + "\n");
            machinecode.append("0 " + findAddress(tmp,translatorsymbols) + " " + findAddress(lex,translatorsymbols) + "\n");
            n++;
            match(look);
        }
        else{
            print(tabCount, "E");
            val = E();
        }
        tabCount--;
        return val;
    }

    //arithematic statements
    public String E(){
        tabCount++;
        print(tabCount, "T");
        print(tabCount, "E'");
        String v = T();
        String s = E_not(v);
        tabCount--;
        return s;
    }
    public String E_not(String i){
        tabCount++;
        String s;
        if(look.equals("('+', ^)")){
            print(tabCount,"('+', ^)");
            match("('+', ^)");
            print(tabCount, "T");
            print(tabCount, "E'");
            String tmp = newtemp();
            String v = T();
            threeaddresscode.append(tmp + " = " + i + " + " + v+"\n");
            translatorsymbols.add("Integer "+tmp + " " + Integer.toString(address));
            address++;
            n++;
            machinecode.append("1 ");
            if(Pattern.matches("[_a-zA-Z]\\w*", i)){
                machinecode.append(findAddress(i,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + i);
                machinecode.append(address+" ");
                address++;
            }
            if(Pattern.matches("[_a-zA-Z]\\w*", v)){
                machinecode.append(findAddress(v,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + v);
                machinecode.append(address+" ");
                address++;
            }

            machinecode.append(findAddress(tmp,translatorsymbols)+"\n");


            s = E_not(tmp);
        }
        else if(look.equals("('-', ^)")){
            print(tabCount,"('-', ^)");
            match("('-', ^)");
            print(tabCount, "T");
            print(tabCount, "E'");
            String tmp = newtemp();
            String v = T();
            threeaddresscode.append(tmp + " = " + i + " - " + v+"\n");
            translatorsymbols.add("Integer "+tmp + " " + Integer.toString(address));
            address++;
            n++;
            machinecode.append("2 ");
            if(Pattern.matches("[_a-zA-Z]\\w*", i)){
                machinecode.append(findAddress(i,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + i);
                machinecode.append(address+" ");
                address++;
            }
            if(Pattern.matches("[_a-zA-Z]\\w*", v)){
                machinecode.append(findAddress(v,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + v);
                machinecode.append(address+" ");
                address++;
            }

            machinecode.append(findAddress(tmp,translatorsymbols)+"\n");

            s = E_not(tmp);
        }
        else{
           s = i;
        }
        tabCount--;
        return s;
    }
    public String T(){
        tabCount++;
        print(tabCount, "F");
        print(tabCount, "T'");
        String v = F();
        String s = T_not(v);
        tabCount--;
        return s;
    }
    public String T_not(String i){
        tabCount++;
        String s;
        if(look.equals("('*', ^)")){
            print(tabCount, "('*', ^)");
            match("('*', ^)");
            print(tabCount, "F");
            print(tabCount, "T'");
            String tmp = newtemp();
            String v = F();
            threeaddresscode.append(tmp + " = " + i + " * " + v + "\n");
            translatorsymbols.add("Integer "+tmp + " " + Integer.toString(address));
            address++;
            n++;
            machinecode.append("3 ");
            if(Pattern.matches("[_a-zA-Z]\\w*", i)){
                machinecode.append(findAddress(i,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + i);
                machinecode.append(address+" ");
                address++;
            }
            if(Pattern.matches("[_a-zA-Z]\\w*", v)){
                machinecode.append(findAddress(v,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + v);
                machinecode.append(address+" ");
                address++;
            }

            machinecode.append(findAddress(tmp,translatorsymbols)+"\n");

            s = T_not(tmp);

        }
        else if(look.equals("('/', ^)")){
            print(tabCount, "('/', ^)");
            match("('/', ^)");
            print(tabCount, "F)");
            print(tabCount, "T'");
            String tmp = newtemp();
            String v = F();
            threeaddresscode.append(tmp + " = " + i + " / " + v + "\n");
            translatorsymbols.add("Integer "+tmp + " " + Integer.toString(address));
            address++;
            n++;
            machinecode.append("4 ");
            if(Pattern.matches("[_a-zA-Z]\\w*", i)){
                machinecode.append(findAddress(i,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + i);
                machinecode.append(address+" ");
                address++;
            }
            if(Pattern.matches("[_a-zA-Z]\\w*", v)){
                machinecode.append(findAddress(v,translatorsymbols) + " ");
            }
            else{
                String tmp2 = newtemp();
                translatorsymbols.add("Integer "+tmp2 + " " + Integer.toString(address)+ " " + v);
                machinecode.append(address+" ");
                address++;
            }

            machinecode.append(findAddress(tmp,translatorsymbols)+"\n");

            s = T_not(tmp);

        }
        else{
            s = i;
        }
        tabCount--;
        return s;
    }
    public String F(){
        tabCount++;
        String v = null;
        if (look.substring(1,3).equals("ID")) {
            v = look.substring(6, look.length()-2);
            print(tabCount, look);
            match(look);
        }
        else if(look.substring(1,4).equals("NUM")){
            print(tabCount, look);
            v = look.substring(6,look.length()-1);
            match(look);
        }
        else if(look.equals("(BRACKET, '(')")){
            print(tabCount, "(BRACKET, '(')");
            match("(BRACKET, '(')");
            print(tabCount, "E");
            v = E();
            print(tabCount, "(BRACKET, ')')");
            match("(BRACKET, ')')");
        }
        tabCount--;
        return v;
    }

    //declaration statements
    public void D(){
        tabCount++;
        StringBuffer datatype = new StringBuffer();
        print(tabCount, "DT");
        DT(datatype);
        print(tabCount, "(SYNOP, ':'");
        match("(SYNOP, ':')");
        print(tabCount, "IDS");
        IDS(true, datatype);
        tabCount--;
    }
    public void DT(StringBuffer dt){
        tabCount++;
        if (look.equals("(Integer, ^)")){
            print(tabCount, "(Integer, ^)");
            match("(Integer, ^)");
            dt.append("Integer");
        }
        else if (look.equals("(char, ^)")){
            print(tabCount, "(char, ^)");
            match("(char, ^)");
            dt.append("char");
        }
        tabCount--;
    }
    public void IDS(Boolean dec, StringBuffer var){
        tabCount++;
        if (look.substring(1,3).equals("ID")){
            if (dec == true){
                StringBuffer parserSymbol = new StringBuffer();
                parserSymbol.append(var + ", " + look.substring(6, look.length()-2));
                parsertokens.add(parserSymbol.toString());
                translatorsymbols.add(var + " " +look.substring(6, look.length()-2) + " " + Integer.toString(address));
                address++;
            }
            else{
                threeaddresscode.append("in " + look.substring(6, look.length()-2) + "\n");
                machinecode.append("7 " + findAddress(look.substring(6, look.length()-2), translatorsymbols)+"\n");
                n++;

            }
            print(tabCount, look);
            match(look);
            if (look.equals("(SYNOP, ',')")){
                print(tabCount, "(SYNOP, ',')");
                match("(SYNOP, ',')");
                print(tabCount, "IDS");
                IDS(dec, var);
            }
        }
        tabCount--;
    }

    //Conditional Statements
    public void C(){
        tabCount++;
        if(look.equals("(if, ^)")){
            print(tabCount, "(if, ^)");
            match("(if, ^)");
            print(tabCount, "M");
            int t = n;
            int f = M();
            backpatch(t, n);
            machineBackpatch(t, n);
            print(tabCount, "U");
            int next = U();
            threeaddresscode.append("goto \n");
            machinecode.append("8\n");
            n++;
            print(tabCount, "OC");
            OC(next, f);
        }
        tabCount--;
    }
    public int M(){
        tabCount++;
        int f = 0;
        print(tabCount, "VAR");
        String lex = null;
        String id1 = VAR(lex);
        if (look.substring(1,6).equals("RELOP")){
            print(tabCount, look);
            String reloplex = look.substring(8, look.length()-1);
            match(look);
            print(tabCount, "VAR");
            String id2 = VAR(lex);
            threeaddresscode.append("if " + id1 + " " + reloplex + " " + id2 + " goto \n");
            if(!Pattern.matches("[_a-zA-Z]\\w*",id1)){
                String temp = newtemp();
                translatorsymbols.add("Integer "+temp + " " + Integer.toString(address)+ " " + id1);
                id1 = Integer.toString(address);
                address++;
            }
            else{
                String temp = findAddress(id1,translatorsymbols);
                id1 = temp;
            }
            if(!Pattern.matches("[_a-zA-Z]\\w*",id2)){
                String temp = newtemp();
                translatorsymbols.add("Integer "+temp + " " + Integer.toString(address)+ " " + id2);
                id2 = Integer.toString(address);
                address++;
            }
            else{
                String temp = findAddress(id2,translatorsymbols);
                id2=temp;
            }
            String code = matchRel(reloplex);
            machinecode.append(code + " " + id1 + " " + id2 + "\n");
            n++;
            f = n;
            threeaddresscode.append("goto \n");
            machinecode.append("8\n");
            n++;
            if(look.equals("(SYNOP, ':')")){
                print(tabCount, "(SYNOP, ':')");
                match("(SYNOP, ':')");
            }
        }
        tabCount--;
        return f;
    }
    public int U(){
        tabCount++;
        int next = 0;
        if(look.equals("(BRACKET, '{')")){
            print(tabCount, "(BRACKET, '{')");
            match("(BRACKET, '{')");
            print(tabCount, "S");
            S();
            next = n;
            print(tabCount,"(BRACKET, '}')");
            match("(BRACKET, '}')");
        }
        tabCount--;
        return next;
    }
    public void OC(int next, int f){
        tabCount++;
        if (look.equals("(else, ^)")){
            print(tabCount, "(else, ^)");
            match("(else, ^)");
            backpatch(f, n);
            machineBackpatch(f,n);
            print(tabCount, "U");
            U();
            backpatch(next, n);
            machineBackpatch(next,n);
        }
        else if(look.equals("(elif, ^)")){
            print(tabCount, "(elif, ^)");
            match("(elif, ^)");
            print(tabCount, "M");
            int t = n;
            backpatch(f, t);
            machineBackpatch(f,t);

            int f2 = M();
            backpatch(t, n);
            machineBackpatch(t,n);
            print(tabCount, "U");
            int next2 = U();
            threeaddresscode.append("goto \n");
            machinecode.append("8\n");
            n++;
            print(tabCount, "OC");
            OC(next2, f2);
            backpatch(next, n);
            machineBackpatch(next,n);
        }
        else if(look.equals("(BRACKET, '}')")){
           // print(tabCount, "(BRACKET, '}')");
           // match("(BRACKET, '}')");
            backpatch(next,n);
            machineBackpatch(next,n);
            backpatch(f, n);
            machineBackpatch(f, n);
        }
        tabCount--;
    }

    //loop statement
    public void L(){
        tabCount++;
        if(look.equals("(while, ^)")){
            print(tabCount, "(while, ^)");
            match("(while, ^)");
            int start = n;
            int t = n;
            print(tabCount, "M");
            int f = M();
            backpatch(t, n);
            machineBackpatch(t,n);
            print(tabCount, "U");
            U();
            threeaddresscode.append("goto " + Integer.toString(start)+"\n");
            machinecode.append("8 " + Integer.toString(start)+"\n");
            n++;
            backpatch(f, n);
            machineBackpatch(f,n);
        }
        tabCount--;
    }

    //input statement
    public void INP(){
        tabCount++;
        StringBuffer temp = null;
        if (look.equals("(in, ^)")){
            print(tabCount, "(in, ^)");
            match("(in, ^)");
            if (look.equals("(INOP, ^)")){
                print(tabCount, "(INOP, ^)");
                match("(INOP, ^)");
                print(tabCount, "IDS");
                IDS(false, temp);
            }
        }
        tabCount--;
    }

    //print statement
    public void P(){
        print(tabCount, "PR");
        PR();
        if(look.equals("(BRACKET, '(')")){
            print(tabCount, "(BRACKET, '(')");
            match("(BRACKET, '(')");
            print(tabCount, "OUT");
            OUT();
            print(tabCount, "(BRACKET, ')')");
            match("(BRACKET, ')')");
        }
    }
    public void PR(){
        tabCount++;
        if(look.equals(("(print, ^)"))){
            print(tabCount, "(print, ^)");
            threeaddresscode.append("out" + " ");
            machinecode.append("5 ");
            match("(print, ^)");
        }
        else if(look.equals(("(println, ^)"))) {
            print(tabCount, "(println, ^)");
            threeaddresscode.append("outl" + " ");
            machinecode.append("6 ");
            match("(println, ^)");
        }
        tabCount--;
    }
    public void OUT(){
        tabCount++;
        if(look.substring(1, 7).equals("STRING")){
            print(tabCount, look);
            threeaddresscode.append(look.substring(9,look.length()-1) + "\n");
            machinecode.append(look.substring(9,look.length()-1)+"\n");
            n++;
            match(look);
        }
        else if(look.substring(1,3).equals("ID")){
            print(tabCount, look);
            threeaddresscode.append(look.substring(6,look.length()-2) + "\n");
            machinecode.append(findAddress(look.substring(6,look.length()-2), translatorsymbols)+"\n");
            n++;
            match(look);
        }
        tabCount--;
    }

    //function declaration statement
    public void FUN(){
        tabCount++;
        StringBuffer temp = new StringBuffer();
        if(look.equals("(func, ^)")){
            print(tabCount, "(func, ^)");
            match("(func, ^)");
            print(tabCount, "DT");
            DT(temp);
            temp=null;
            if(look.equals("(SYNOP, ':')")){
                print(tabCount,"(SYNOP, ':')");
                match("(SYNOP, ':')");
                if(look.substring(1,3).equals("ID")){
                    print(tabCount, look);
                    match(look);
                    if(look.equals("(BRACKET, '(')")){
                        print(tabCount,"(BRACKET, '(')");
                        match("(BRACKET, '(')");
                        print(tabCount, "PARA");
                        PARA();
                        print(tabCount, "(BRACKET, ')')");
                        match("(BRACKET, ')')");
                        print(tabCount, "U");
                        U();
                    }
                }
            }
        }
        tabCount--;
    }
    public void PARA(){
        tabCount++;
        StringBuffer datatype = new StringBuffer();
        print(tabCount, "DT");
        DT(datatype);
        if(look.equals("(SYNOP, ':')")){
            print(tabCount, "(SYNOP, ':')");
            match("(SYNOP, ':')");
            print(tabCount, "MP");
            MP(datatype);
        }
        tabCount--;
    }
    public void MP(StringBuffer var){
        tabCount++;
        if (look.substring(1,3).equals("ID")){
            StringBuffer parserSymbol = new StringBuffer();
            parserSymbol.append(var + ", " + look.substring(6, look.length()-2));
            parsertokens.add(parserSymbol.toString());
            print(tabCount, look);
            match(look);
            if (look.equals("(SYNOP, ',')")){
                print(tabCount, "(SYNOP, ',')");
                match("(SYNOP, ',')");
                print(tabCount, "PARA");
                PARA();
            }
        }
        tabCount--;
    }

    //Return statement
    public void RET(){
        tabCount++;
        if(look.equals("(ret, ^)")){
            print(tabCount, "(ret, ^)");
            match("(ret, ^)");
            if (look.substring(1,3).equals("ID")){
                print(tabCount, look);
                threeaddresscode.append("ret " + look.substring(6,look.length()-2) + "\n");
                machinecode.append("#\n");
                n++;
                match(look);
            }
        }
        tabCount--;
    }

    //function call statement
    public void FUNCALL(String variabletoreturn){
        int paramcount = 0;
        tabCount++;
        if (look.substring(1,3).equals("ID")){
            print(tabCount, look);
            String lex = look.substring(6,look.length()-2);
            match(look);
            if(look.equals("(BRACKET, '(')")){
                print(tabCount, "(BRACKET, '(')");
                match("(BRACKET, '(')");
                print(tabCount, "PARA");
                paramcount = PARAMS();
                print(tabCount, "(BRACKET, ')')");
                match("(BRACKET, ')')");
            }
            threeaddresscode.append("call " + lex + " " + Integer.toString(paramcount) + " " + variabletoreturn + "\n");
            machinecode.append("#\n");
            n++;
        }
        tabCount--;
    }
    public int PARAMS(){
        int paramcount = 0;
        tabCount++;
        if (look.substring(1,3).equals("ID")){
            paramcount++;
            print(tabCount, look);
            threeaddresscode.append("param " + look.substring(6,look.length()-2) + "\n");
            machinecode.append("#\n");
            n++;
            match(look);
            if (look.equals("(SYNOP, ',')")){
                print(tabCount,"(SYNOP, ',')");
                match("(SYNOP, ',')");
                print(tabCount, "PARAMS");
                paramcount = paramcount + PARAMS();
            }
        }
        tabCount--;
        return paramcount;
    }

}

