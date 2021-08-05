import java.util.ArrayList;
import java.util.Scanner;


public class Lexer {
    private String[] keywords;
    private StringBuffer buffer;
    private int forwardIndex;
    private int lexStartIndex;
    private ArrayList<String> tokenList;
    private int lineNum;

    public Lexer(){
        buffer = null;
        keywords = new String[]{"Integer", "char", "if", "else", "elif", "in", "while", "print", "println", "func", "ret"};
        forwardIndex = 0;
        lexStartIndex = 0;
        tokenList = null;
    }

    public boolean isDiv(){
        if (buffer.charAt(lexStartIndex) == '/'){
            tokenList.add("('/', ^)");
            displayToken("('/', ^)");
            lexStartIndex = lexStartIndex + 1;
            return true;
        }
        return false;
    }

    public boolean isPlus(){
        if (buffer.charAt(lexStartIndex) == '+'){
            tokenList.add("('+', ^)");
            displayToken("('+', ^)");
            lexStartIndex = lexStartIndex + 1;
            return true;
        }
        return false;
    }

    public boolean isSub(){
        if (buffer.charAt(lexStartIndex) == '-'){
            tokenList.add("('-', ^)");
            displayToken("('-', ^)");
            lexStartIndex = lexStartIndex + 1;
            return true;
        }
        return false;
    }

    public boolean isMul(){
        if (buffer.charAt(lexStartIndex) == '*'){
            tokenList.add("('*', ^)");
            displayToken("('*', ^)");
            lexStartIndex = lexStartIndex + 1;
            return true;
        }
        return false;
    }

    public boolean isRelOp(){
        forwardIndex = lexStartIndex;
        int state = 0;
        while (state != 10) {
            switch (state) {
                case 0:
                    if (buffer.charAt(lexStartIndex) == '>') {
                        state = 1;
                        forwardIndex = forwardIndex + 1;
                    } else if (buffer.charAt(lexStartIndex) == '<') {
                        state = 2;
                        forwardIndex = forwardIndex + 1;
                    } else if (buffer.charAt(lexStartIndex) == '/') {
                        state = 3;
                        forwardIndex = forwardIndex + 1;
                    } else if (buffer.charAt(lexStartIndex) == '=') {
                        state = 4;
                        forwardIndex = forwardIndex + 1;
                    } else {
                        state = 10;
                    }
                    break;
                case 1:
                    if (buffer.charAt(lexStartIndex+1) == '=') {
                        state = 5;
                        forwardIndex = forwardIndex + 1;
                    } else {
                        state = 6;
                    }
                    break;
                case 2:
                    System.out.println(buffer.charAt(lexStartIndex));
                    if (buffer.charAt(lexStartIndex+1) == '=') {
                        state = 7;
                        forwardIndex = forwardIndex + 1;
                    } else {
                        state = 8;
                    }
                    break;
                case 3:
                    if (buffer.charAt(lexStartIndex+1) == '=') {
                        state = 9;
                    }
                    break;
                case 4:
                    tokenList.add("(RELOP, EQ)");
                    displayToken("(RELOP, EQ)");
                    lexStartIndex = lexStartIndex + 1;
                    return true;

                case 5:
                    tokenList.add("(RELOP, GE)");
                    displayToken("(RELOP, GE)");
                    lexStartIndex = forwardIndex + 1;
                    return true;
                case 6:
                    tokenList.add("(RELOP, GT)");
                    displayToken("(RELOP, GT)");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 7:
                    tokenList.add("(RELOP, LE)");
                    displayToken("(RELOP, LE)");
                    lexStartIndex = forwardIndex + 1;
                    return true;
                case 8:
                    tokenList.add("(RELOP, LT)");
                    displayToken("(RELOP, LT)");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 9:
                    tokenList.add("(RELOP, NE)");
                    displayToken("(RELOP, NE)");
                    lexStartIndex = forwardIndex + 1;
                    return true;
            }
        }
        return false;
    }

    public boolean isComment(){
        forwardIndex = lexStartIndex;
        int state = 0;
        while (state != 5) {
            switch (state) {
                case 0:
                    if (buffer.charAt(lexStartIndex) == '/') {
                        state = 1;
                        forwardIndex = forwardIndex + 1;
                    } else
                        state = 5;
                    break;
                case 1:
                    if (buffer.charAt(forwardIndex) == '*') {
                        state = 2;
                        forwardIndex = forwardIndex + 1;
                    } else
                        state = 5;
                    break;
                case 2:
                    while (!(buffer.charAt(forwardIndex) == '*')) {
                        forwardIndex = forwardIndex + 1;
                    }
                    state = 3;
                    break;
                case 3:
                    forwardIndex = forwardIndex + 1;
                    if (buffer.charAt(forwardIndex) == '/') {
                        state = 4;
                    } else
                        state = 5;
                    break;
                case 4:
                    tokenList.add("(COMMENT, " + buffer.subSequence(lexStartIndex, forwardIndex + 1) + ")");
                    displayToken("(COMMENT, " + buffer.subSequence(lexStartIndex, forwardIndex + 1) + ")");
                    forwardIndex = forwardIndex + 1;
                    lexStartIndex = forwardIndex;
                    return true;
            }
        }
        return false;
    }

    public boolean isKeyword(){
        for(int i = 0; i<keywords.length; i++){
            if (buffer.subSequence(lexStartIndex, forwardIndex).equals(keywords[i])){
                tokenList.add("("+keywords[i] + ", ^)");
                displayToken("("+keywords[i] + ", ^)");
                lexStartIndex = forwardIndex;
                return true;
            }
        }
        return false;
    }

    public boolean isID(){
        forwardIndex = lexStartIndex;
        int state = 0;
        while (state != 3) {
            char c = buffer.charAt(lexStartIndex);
            switch (state) {
                case 0:
                    if (Character.isLetter(c)) {
                        state = 1;
                        forwardIndex = forwardIndex + 1;
                    } else
                        state = 3;
                    break;
                case 1:
                    while (Character.isLetterOrDigit(buffer.charAt(forwardIndex))) {
                        forwardIndex = forwardIndex + 1;
                    }
                    state = 2;
                    break;
                case 2:
                    if (!isKeyword()) {
                        tokenList.add("(ID, " + '"' + buffer.subSequence(lexStartIndex, forwardIndex) + '"' + ")");
                        displayToken("(ID, " + '"' + buffer.subSequence(lexStartIndex, forwardIndex) + '"' + ")");
                        lexStartIndex = forwardIndex;
                    }
                    return true;
            }
        }

        return false;

    }

    public boolean isNum(){
        forwardIndex = lexStartIndex;
        int state = 0;
        while(state != 3) {
            switch (state) {
                case 0:
                    if (Character.isDigit(buffer.charAt(lexStartIndex))) {
                        state = 1;
                        forwardIndex = forwardIndex + 1;
                    } else
                        state = 3;
                    break;
                case 1:
                    while (Character.isDigit(buffer.charAt(forwardIndex))) {
                        forwardIndex = forwardIndex + 1;
                    }
                    state = 2;
                    break;
                case 2:
                    tokenList.add("(NUM, " + buffer.subSequence(lexStartIndex, forwardIndex) + ")");
                    displayToken("(NUM, " + buffer.subSequence(lexStartIndex, forwardIndex) + ")");
                    lexStartIndex = forwardIndex;
                    return true;
            }
        }

        return false;
    }

    public boolean isLiteral(){
        forwardIndex = lexStartIndex;
        int state = 0;
        while (state != 4) {
            switch (state) {
                case 0:
                    if (buffer.charAt(lexStartIndex) == '‘') {
                        state = 1;
                        forwardIndex = forwardIndex + 1;
                    } else
                        state = 4;
                    break;
                case 1:
                    if (Character.isLetter(buffer.charAt(forwardIndex))) {
                        forwardIndex = forwardIndex + 1;
                        state = 2;
                    } else
                        state = 4;
                    break;
                case 2:
                    if (buffer.charAt(lexStartIndex) == '‘') {
                        state = 3;
                    } else {
                        System.out.println("Error: Literal does not have an ending '");
                        state = 4;
                    }
                    break;
                case 3:
                    tokenList.add("(LITERAL, " + buffer.subSequence(lexStartIndex, forwardIndex + 1) + ")");
                    displayToken("(LITERAL, " + buffer.subSequence(lexStartIndex, forwardIndex + 1) + ")");
                    forwardIndex = forwardIndex + 1;
                    lexStartIndex = forwardIndex;
                    return true;
            }
        }
        return false;
    }

    public boolean isString(){
        forwardIndex = lexStartIndex;
        int state = 0;
        while(state != 3) {
            switch (state) {
                case 0:
                    if (buffer.charAt(lexStartIndex) == '"') {
                        state = 1;
                        forwardIndex = forwardIndex + 1;
                    } else
                        state = 3;
                    break;
                case 1:
                    while (!(buffer.charAt(forwardIndex) == '"')) {
                        forwardIndex = forwardIndex + 1;
                    }
                    state = 2;
                    break;
                case 2:
                    tokenList.add("(STRING, " + buffer.subSequence(lexStartIndex, forwardIndex + 1) + ")");
                    displayToken("(STRING, " + buffer.subSequence(lexStartIndex, forwardIndex + 1) + ")");
                    forwardIndex = forwardIndex + 1;
                    lexStartIndex = forwardIndex;
                    return true;
            }
        }
        return false;
    }

    public boolean isBracket(){
        int state = 0;
        while (state != 7) {
            switch (state) {
                case 0:
                    if (buffer.charAt(lexStartIndex) == '{') {
                        state = 1;
                    } else if (buffer.charAt(lexStartIndex) == '}') {
                        state = 2;
                    } else if (buffer.charAt(lexStartIndex) == '(') {
                        state = 3;
                    } else if (buffer.charAt(lexStartIndex) == ')') {
                        state = 4;
                    } else if (buffer.charAt(lexStartIndex) == '[') {
                        state = 5;
                    } else if (buffer.charAt(lexStartIndex) == ']') {
                        state = 6;
                    } else {
                        state = 7;
                    }
                    break;
                case 1:
                    tokenList.add("(BRACKET, '{')");
                    displayToken("(BRACKET, '{')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 2:
                    tokenList.add("(BRACKET, '}')");
                    displayToken("(BRACKET, '}')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 3:
                    tokenList.add("(BRACKET, '(')");
                    displayToken("(BRACKET, '(')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 4:
                    tokenList.add("(BRACKET, ')')");
                    displayToken("(BRACKET, ')')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 5:
                    tokenList.add("(BRACKET, '[')");
                    displayToken("(BRACKET, '[')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 6:
                    tokenList.add("(BRACKET, ']')");
                    displayToken("(BRACKET, ']')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
            }
        }
        return false;
    }

    public boolean isAssignOp(){
        if (buffer.subSequence(lexStartIndex, lexStartIndex + 2).equals(":=")){
            tokenList.add("(ASSIGNOP, ^)");
            displayToken("(ASSIGNOP, ^)");
            lexStartIndex = lexStartIndex + 2;
            return true;
        }

        return false;

    }

    public boolean isInOp(){
        if (buffer.subSequence(lexStartIndex, lexStartIndex + 2).equals(">>")){
            tokenList.add("(INOP, ^)");
            displayToken("(INOP, ^)");
            lexStartIndex = lexStartIndex + 2;
            return true;
        }
        return false;
    }

    public boolean isSynOp(){
        int state = 0;
        while (state != 4) {
            switch (state) {
                case 0:
                    if (buffer.charAt(lexStartIndex) == ':') {
                        state = 1;
                    } else if (buffer.charAt(lexStartIndex) == ';') {
                        state = 2;
                    } else if (buffer.charAt(lexStartIndex) == ',') {
                        state = 3;
                    } else
                        state = 4;
                    break;
                case 1:
                    tokenList.add("(SYNOP, ':')");
                    displayToken("(SYNOP, ':')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 2:
                    tokenList.add("(SYNOP, ';')");
                    displayToken("(SYNOP, ';')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
                case 3:
                    tokenList.add("(SYNOP, ',')");
                    displayToken("(SYNOP, ',')");
                    lexStartIndex = lexStartIndex + 1;
                    return true;
            }
        }
        return false;
    }

    public boolean isWhiteSpace(){
        if (buffer.charAt(lexStartIndex) == '\r' || buffer.charAt(lexStartIndex) == '\t'|| buffer.charAt(lexStartIndex) == ' '){
            lexStartIndex = lexStartIndex + 1;
            return true;
        }

        return false;
    }

    public boolean isNewLine(){
        if (buffer.charAt(lexStartIndex) == '\n'){
            lineNum = lineNum + 1;
            lexStartIndex = lexStartIndex + 1;
            return true;
        }
        return false;
    }

    public void displayToken(String token){
        System.out.println(token + "\n");
    }

    public String getFilePath(){
        Scanner userInput = new Scanner(System.in);
        System.out.print("Please input the file path: ");
        String path = userInput.nextLine();
        if (!path.endsWith(".go")){
            System.out.println("Invalid file: The given file is not a .go file");
            return null;
        }
        return path;
    }

    public ArrayList<String> getTokens(String buffer){
        this.buffer = new StringBuffer(buffer);
        tokenList = new ArrayList<String>();

        while (lexStartIndex < this.buffer.length()){
            if (isWhiteSpace()){
                //do nothing
            }
            else if(isBracket()){
            }
            else if(isComment()){
            }
            else if(isDiv()){
            }
            else if(isMul()){
            }
            else if(isPlus()){
            }
            else if(isSub()){
            }
            else if(isNewLine()){
            }
            else if(isAssignOp()){
            }
            else if(isSynOp()){
            }
            else if(isID()){
            }
            else if(isLiteral()){
            }
            else if(isInOp()){
            }
            else if(isRelOp()){
            }
            else if(isString()){
            }
            else if(isNum()){
            }
            else{
                System.out.println("Error: cannot recognize token on line " + Integer.toString(lineNum));
                lexStartIndex = lexStartIndex + 1;
            }
        }


        return tokenList;
    }
}

