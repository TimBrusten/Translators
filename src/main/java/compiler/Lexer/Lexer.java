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
        while ((current = input.read()) != -1) {
            char currentChar = (char) current;
            System.out.println("Reading character: " + currentChar);

            // Ignore whitespace
            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') {
                    line++;
                    System.out.println("New line detected, line number: " + line);
                }
                continue;
            }
            // Ignore comments
            if (currentChar == '$') {
                System.out.println("Comment detected, line number: " + line);
                while ((current = input.read()) != -1 && (char) current != '\n') {
                    // Skip until end of line
                }
                line++;
                continue;
            }
            // Gestion de la chaîne de caractères
            if (currentChar == '"') {
                System.out.println("Starting string literal");
                return stringLiteral();
            }

            // Opérateurs et symboles
            switch (currentChar) {
                case '(':
                    System.out.println("Left parenthesis detected");
                    return new Symbol(TokenType.LEFT_PAREN, "(", line);
                case ')':
                    System.out.println("Right parenthesis detected");
                    return new Symbol(TokenType.RIGHT_PAREN, ")", line);
                case '{':
                    System.out.println("Left brace detected");
                    return new Symbol(TokenType.LEFT_BRACE, "{", line);
                case '}':
                    System.out.println("Right brace detected");
                    return new Symbol(TokenType.RIGHT_BRACE, "}", line);
                case '[':
                    System.out.println("Left bracket detected");
                    return new Symbol(TokenType.LEFT_BRACKET, "[", line);
                case ']':
                    System.out.println("Right bracket detected");
                    return new Symbol(TokenType.RIGHT_BRACKET, "]", line);
                case ',':
                    System.out.println("Comma detected");
                    return new Symbol(TokenType.COMMA, ",", line);
                case ';':
                    System.out.println("Semicolon detected");
                    return new Symbol(TokenType.SEMICOLON, ";", line);
                case '+':
                    System.out.println("Plus sign detected");
                    return new Symbol(TokenType.PLUS, "+", line);
                case '-':
                    System.out.println("Minus sign detected");
                    return new Symbol(TokenType.MINUS, "-", line);
                case '*':
                    System.out.println("Star detected");
                    return new Symbol(TokenType.STAR, "*", line);
                case '/':
                    System.out.println("Slash detected");
                    return new Symbol(TokenType.SLASH, "/", line);
                case '=':
                    input.mark(1);
                    int next = input.read();
                    if (next != -1 && (char) next == '=') {
                        System.out.println("Equal equal (==) detected");
                        return new Symbol(TokenType.EQUAL_EQUAL, "==", line);
                    } else {
                        input.reset();
                        System.out.println("Equal detected");
                        return new Symbol(TokenType.EQUAL, "=", line);
                    }
                case '!':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        System.out.println("Not equal (!=) detected");
                        return new Symbol(TokenType.NOT_EQUAL, "!=", line);
                    } else {
                        input.reset();
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                case '<':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        System.out.println("Less than or equal (<=) detected");
                        return new Symbol(TokenType.LESS_EQUAL, "<=", line);
                    } else {
                        input.reset();
                        System.out.println("Less than (<) detected");
                        return new Symbol(TokenType.LESS_THAN, "<", line);
                    }
                case '>':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        System.out.println("Greater than or equal (>=) detected");
                        return new Symbol(TokenType.GREATER_EQUAL, ">=", line);
                    } else {
                        input.reset();
                        System.out.println("Greater than (>) detected");
                        return new Symbol(TokenType.GREATER_THAN, ">", line);
                    }
                case '&':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '&') {
                        System.out.println("Logical AND (&&) detected");
                        return new Symbol(TokenType.AND, "&&", line);
                    } else {
                        input.reset();
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                case '|':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '|') {
                        System.out.println("Logical OR (||) detected");
                        return new Symbol(TokenType.OR, "||", line);
                    } else {
                        input.reset();
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                case '.':
                    // Vérifier si le point fait partie d'un nombre ou s'il s'agit d'un opérateur DOT
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && Character.isDigit((char) next)) {
                        input.reset();
                        System.out.println("Starting number token with dot");
                        return number(currentChar);
                    } else {
                        input.reset();
                        System.out.println("Dot detected");
                        return new Symbol(TokenType.DOT, ".", line);
                    }
                default:
                    break;
            }

            // Si ce n'est pas un symbole reconnu, gérer les nombres et identifiants
            if (Character.isDigit(currentChar)) {
                System.out.println("Starting number token");
                return number(currentChar);
            } else if (Character.isLetter(currentChar) || currentChar == '_') {
                System.out.println("Starting identifier/keyword token");
                return identifier(currentChar);
            }

            throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
        }
        System.out.println("End of input reached");
        return new Symbol(TokenType.EOF, "EOF", line);
    }

    private Symbol number(char first) throws IOException {
        StringBuilder value = new StringBuilder();
        boolean hasDot = false;
        if (first == '.') {
            hasDot = true;
            value.append(first);
        } else {
            value.append(first);
        }
        input.mark(1);
        int next;
        while ((next = input.read()) != -1) {
            char c = (char) next;
            if (Character.isDigit(c)) {
                value.append(c);
            } else if (c == '.' && !hasDot) {
                hasDot = true;
                value.append(c);
            } else {
                input.reset();
                break;
            }
            input.mark(1);
        }
        System.out.println("Number token complete: " + value);
        return new Symbol(TokenType.NUMBER, value.toString(), line);
    }

    private Symbol identifier(char first) throws IOException {
        StringBuilder value = new StringBuilder();
        value.append(first);
        input.mark(1);
        int next;
        while ((next = input.read()) != -1 && (Character.isLetter((char) next) || Character.isDigit((char) next) || ((char) next) == '_')) {
            value.append((char) next);
            input.mark(1);
        }
        input.reset();
        String word = value.toString();
        System.out.println("Identifier/keyword token complete: " + word);

        // Gestion des booléens
        if (word.equals("true") || word.equals("false")) {
            System.out.println("Boolean literal detected: " + word);
            return new Symbol(TokenType.BOOLEAN_LITERAL, word, line);
        }

        // Gestion des mots-clés
        switch (word) {
            case "free":
                System.out.println("free keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "final":
                System.out.println("final keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "rec":
                System.out.println("rec keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "fun":
                System.out.println("fun keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "for":
                System.out.println("for keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "while":
                System.out.println("while keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "if":
                System.out.println("if keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "else":
                System.out.println("else keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "return":
                System.out.println("return keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            default:
                System.out.println(word + " identifier");
                return new Symbol(TokenType.IDENTIFIER, word, line);
        }
    }

    private Symbol stringLiteral() throws IOException {
        StringBuilder value = new StringBuilder();
        while (true) {
            int next = input.read();
            if (next == -1) {
                throw new RuntimeException("Unterminated string literal at line " + line);
            }
            char c = (char) next;
            if (c == '"') {
                break;
            }
            if (c == '\\') {
                int escape = input.read();
                if (escape == -1) {
                    throw new RuntimeException("Unterminated escape sequence in string literal at line " + line);
                }
                char escapeChar = (char) escape;
                switch (escapeChar) {
                    case 'n': value.append('\n'); break;
                    case '\\': value.append('\\'); break;
                    case '"': value.append('"'); break;
                    default: value.append(escapeChar); break;
                }
            } else {
                value.append(c);
            }
        }
        System.out.println("String literal complete: " + value);
        return new Symbol(TokenType.STRING_LITERAL, value.toString(), line);
    }
}
