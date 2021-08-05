import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Parser {
    private static int count = 0;
    private static int tabCount = 0;
    private static String look = null;
    private static ArrayList<String> tokens;
    private static ArrayList<String> parsertokens =new ArrayList<>();
    private static StringBuffer parsetree = new StringBuffer();

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
                FUNCALL();
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
            match(look);
            if (look.equals("(ASSIGNOP, ^)")){
                print(tabCount, "(ASSIGNOP, ^)");
                match("(ASSIGNOP, ^)");
            }
            print(tabCount, "VAR");
            VAR();
        }
        tabCount--;
    }
    public void VAR(){
        tabCount++;
        if (look.substring(1,8).equals("LITERAL")){
            print(tabCount, look);
            match(look);
        }
        else{
            print(tabCount, "E");
            E();
        }
        tabCount--;
    }

    //arithematic statements
    public void E(){
        tabCount++;
        print(tabCount, "T");
        T();
        print(tabCount, "E'");
        E_not();
        tabCount--;
    }
    public void E_not(){
        tabCount++;
        if(look.equals("('+', ^)")){
            print(tabCount,"('+', ^)");
            match("('+', ^)");
            print(tabCount, "T");
            T();
            print(tabCount, "E'");
            E_not();
        }
        else if(look.equals("('-', ^)")){
            print(tabCount,"('-', ^)");
            match("('-', ^)");
            print(tabCount, "T");
            T();
            print(tabCount, "E'");
            E_not();
        }
        tabCount--;
    }
    public void T(){
        tabCount++;
        print(tabCount, "F");
        F();
        print(tabCount, "T'");
        T_not();
        tabCount--;
    }
    public void T_not(){
        tabCount++;
        if(look.equals("('*', ^)")){
            print(tabCount, "('*', ^)");
            match("('*', ^)");
            print(tabCount, "F");
            F();
            print(tabCount, "T'");
            T_not();
        }
        else if(look.equals("('/', ^)")){
            print(tabCount, "('/', ^)");
            match("('/', ^)");
            print(tabCount, "F)");
            F();
            print(tabCount, "T'");
            T_not();
        }
        tabCount--;
    }
    public void F(){
        tabCount++;
        if (look.substring(1,3).equals("ID")) {
            print(tabCount, look);
            match(look);
        }
        else if(look.substring(1,4).equals("NUM")){
            print(tabCount, look);
            match(look);
        }
        else if(look.equals("(BRACKET, '(')")){
            print(tabCount, "(BRACKET, '(')");
            match("(BRACKET, '(')");
            print(tabCount, "E");
            E();
            print(tabCount, "(BRACKET, ')')");
            match("(BRACKET, ')')");
        }
        tabCount--;
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
            M();
            print(tabCount, "U");
            U();
            print(tabCount, "OC");
            OC();
        }
        tabCount--;
    }
    public void OC(){
        tabCount++;
        if (look.equals("(else, ^)")){
            print(tabCount, "(else, ^)");
            match("(else, ^)");
            print(tabCount, "U");
            U();
        }
        else if(look.equals("(elif, ^)")){
            print(tabCount, "(elif, ^)");
            match("(elif, ^)");
            print(tabCount, "M");
            M();
            print(tabCount, "U");
            U();
            print(tabCount, "OC");
            OC();
        }
        tabCount--;
    }
    public void M(){
        tabCount++;
        print(tabCount, "VAR");
        VAR();
        if (look.substring(1,6).equals("RELOP")){
            print(tabCount, look);
            match(look);
            print(tabCount, "VAR");
            VAR();
            if(look.equals("(SYNOP, ':')")){
                print(tabCount, "(SYNOP, ':')");
                match("(SYNOP, ':')");
            }
        }
        tabCount--;
    }
    public void U(){
        tabCount++;
        if(look.equals("(BRACKET, '{')")){
            print(tabCount, "(BRACKET, '{')");
            match("(BRACKET, '{')");
            print(tabCount, "S");
            S();
            print(tabCount,"(BRACKET, '}')");
            match("(BRACKET, '}')");
        }
        tabCount--;
    }

    //loop statement
    public void L(){
        tabCount++;
        if(look.equals("(while, ^)")){
            print(tabCount, "(while, ^)");
            match("(while, ^)");
            print(tabCount, "M");
            M();
            print(tabCount, "U");
            U();
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
            match("(print, ^)");
        }
        else if(look.equals(("(println, ^)"))) {
            print(tabCount, "(println, ^)");
            match("(println, ^)");
        }
        tabCount--;
    }
    public void OUT(){
        tabCount++;
        if(look.substring(1, 7).equals("STRING")){
            print(tabCount, look);
            match(look);
        }
        else if(look.substring(1,3).equals("ID")){
            print(tabCount, look);
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
                match(look);
            }
        }
        tabCount--;
    }

    //function call statement
    public void FUNCALL(){
        tabCount++;
        if (look.substring(1,3).equals("ID")){
            print(tabCount, look);
            match(look);
            if(look.equals("(BRACKET, '(')")){
                print(tabCount, "(BRACKET, '(')");
                match("(BRACKET, '(')");
                print(tabCount, "PARA");
                PARAMS();
                print(tabCount, "(BRACKET, ')')");
                match("(BRACKET, ')')");
            }
        }
        tabCount--;
    }
    public void PARAMS(){
        tabCount++;
        if (look.substring(1,3).equals("ID")){
            print(tabCount, look);
            match(look);
            if (look.equals("(SYNOP, ',')")){
                print(tabCount,"(SYNOP, ',')");
                match("(SYNOP, ',')");
                print(tabCount, "PARAMS");
                PARAMS();
            }
        }
        tabCount--;
    }


}
