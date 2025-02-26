package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;

public class Lexer {
    private final Reader input;
    private int line = 1;

    public Lexer(Reader input) {
        this.input = input;
    }
    
    public Symbol getNextSymbol() throws IOException {
        int current;
        while((current = input.read()) != -1){
            char currentChar = (char)current;
            System.out.println("Reading character: " + currentChar);

            if(Character.isWhitespace(currentChar)){
                if(currentChar == '\n'){
                    line ++;
                    System.out.println("New line detected, line number: " + line);
                }
                continue;
            }
            if (currentChar == '$'){
                System.out.println("Comment detected, line number: " + line);
                while ((currentChar = (char) input.read()) != -1 && currentChar != '\n') {
                    // Skip characters until end of line
                }
                line++;
                continue;
            }
            switch (currentChar) {
                case '(': System.out.println("Left parenthesis detected"); return new Symbol(TokenType.LEFT_PAREN, "(", line);
                case ')': System.out.println("Right parenthesis detected"); return new Symbol(TokenType.RIGHT_PAREN, ")", line);
                case '{': System.out.println("Left brace detected"); return new Symbol(TokenType.LEFT_BRACE, "{", line);
                case '}': System.out.println("Right brace detected"); return new Symbol(TokenType.RIGHT_BRACE, "}", line);
                case '+': System.out.println("Plus sign detected"); return new Symbol(TokenType.PLUS, "+", line);
                case '-': System.out.println("Minus sign detected"); return new Symbol(TokenType.MINUS, "-", line);
                case '*': System.out.println("Star detected"); return new Symbol(TokenType.STAR, "*", line);
                case '/': System.out.println("Slash detected"); return new Symbol(TokenType.SLASH, "/", line);
                case '=': System.out.println("equal detected"); return new Symbol(TokenType.EQUAL, "=", line);
                case ';': System.out.println("semicolon detected"); return new Symbol(TokenType.SEMICOLON, ";", line);

            }

            if (Character.isDigit(currentChar)) {
                System.out.println("Starting number token");
                return number(currentChar);
            } else if (Character.isLetter(currentChar)) {
                System.out.println("Starting identifier/keyword token");
                return identifier(currentChar);
            }

            throw new RuntimeException("Unexpected character: " + currentChar);

        }
        System.out.println("End of input reached");
        return new Symbol(TokenType.EOF, "", line);
    }

    private Symbol number(char first) throws IOException {
        StringBuilder value = new StringBuilder();
        value.append(first);

        input.mark(1);
        int next;
        while ((next = input.read()) != -1 && Character.isDigit((char) next)) {
            value.append((char) next);
            input.mark(1);
        }
        input.reset();

        System.out.println("Number token complete: " + value);
        return new Symbol(TokenType.NUMBER, value.toString(), line);
    }

    private Symbol identifier(char first) throws IOException {
        StringBuilder value = new StringBuilder();
        value.append(first);


        input.mark(1);
        int next;
        while ((next = input.read()) != -1 && (Character.isLetter((char) next) || Character.isDigit((char) next)||((char) next) == '_')) {
            value.append((char) next);
            input.mark(1);
        }
        input.reset();

        String word = value.toString();
        System.out.println("Identifier/keyword token complete: " + word);
        switch (word) {
            //case "if": return new Symbol(TokenType.IF, word, line); comme ça ou comme après
            case "free": System.out.println("free keyword"); return new Symbol(TokenType.KEYWORD, word, line);
            case "final": System.out.println("final keyword"); return new Symbol(TokenType.KEYWORD, word, line);
            case "rec": System.out.println("rec keyword");return new Symbol(TokenType.KEYWORD, word, line);
            case "fun": System.out.println("fun keyword");return new Symbol(TokenType.KEYWORD, word, line);
            case "for": System.out.println("for keyword");return new Symbol(TokenType.KEYWORD, word, line);
            case "while": System.out.println("while keyword");return new Symbol(TokenType.KEYWORD, word, line);
            case "if": System.out.println("if keyword");return new Symbol(TokenType.KEYWORD, word, line);
            case "else": System.out.println("else keyword");return new Symbol(TokenType.KEYWORD, word, line);
            case "return": System.out.println("return keyword");return new Symbol(TokenType.KEYWORD, word, line);
            default: System.out.println(word+" identifier" );return new Symbol(TokenType.IDENTIFIER, word, line);
        }
    }
}
