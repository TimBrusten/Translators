package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;

import static java.rmi.server.LogStream.log;

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
            log("Reading character: " + currentChar);

            // Ignore whitespace
            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') {
                    line++;
                    log("New line detected, line number: " + line);
                }
                continue;
            }
            // Ignore comments
            if (currentChar == '$') {
                log("Comment detected, line number: " + line);
                while ((current = input.read()) != -1 && (char) current != '\n') {
                    // Skip until end of line
                }
                line++;
                continue;
            }
            // String management
            if (currentChar == '"') {
                log("Starting string literal");
                return stringLiteral();
            }

            // Symbol operator
            switch (currentChar) {
                case '(':
                    log("Left parenthesis detected");
                    return new Symbol(TokenType.LEFT_PAREN, "(", line);
                case ')':
                    log("Right parenthesis detected");
                    return new Symbol(TokenType.RIGHT_PAREN, ")", line);
                case '{':
                    log("Left brace detected");
                    return new Symbol(TokenType.LEFT_BRACE, "{", line);
                case '}':
                    log("Right brace detected");
                    return new Symbol(TokenType.RIGHT_BRACE, "}", line);
                case '[':
                    log("Left bracket detected");
                    return new Symbol(TokenType.LEFT_BRACKET, "[", line);
                case ']':
                    log("Right bracket detected");
                    return new Symbol(TokenType.RIGHT_BRACKET, "]", line);
                case ',':
                    log("Comma detected");
                    return new Symbol(TokenType.COMMA, ",", line);
                case ';':
                    log("Semicolon detected");
                    return new Symbol(TokenType.SEMICOLON, ";", line);
                case '+':
                    log("Plus sign detected");
                    return new Symbol(TokenType.PLUS, "+", line);
                case '-':
                    log("Minus sign detected");
                    return new Symbol(TokenType.MINUS, "-", line);
                case '*':
                    log("Star detected");
                    return new Symbol(TokenType.STAR, "*", line);
                case '/':
                    log("Slash detected");
                    return new Symbol(TokenType.SLASH, "/", line);
                case '=':
                    input.mark(1);
                    int next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Equal equal (==) detected");
                        return new Symbol(TokenType.EQUAL_EQUAL, "==", line);
                    } else {
                        input.reset();
                        log("Equal detected");
                        return new Symbol(TokenType.EQUAL, "=", line);
                    }
                case '!':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Not equal (!=) detected");
                        return new Symbol(TokenType.NOT_EQUAL, "!=", line);
                    } else {
                        input.reset();
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                case '<':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Less than or equal (<=) detected");
                        return new Symbol(TokenType.LESS_EQUAL, "<=", line);
                    } else {
                        input.reset();
                        log("Less than (<) detected");
                        return new Symbol(TokenType.LESS_THAN, "<", line);
                    }
                case '>':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Greater than or equal (>=) detected");
                        return new Symbol(TokenType.GREATER_EQUAL, ">=", line);
                    } else {
                        input.reset();
                        log("Greater than (>) detected");
                        return new Symbol(TokenType.GREATER_THAN, ">", line);
                    }
                case '&':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '&') {
                        log("Logical AND (&&) detected");
                        return new Symbol(TokenType.AND, "&&", line);
                    } else {
                        input.reset();
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                case '|':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '|') {
                        log("Logical OR (||) detected");
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
                        log("Starting number token with dot");
                        return number(currentChar);
                    } else {
                        input.reset();
                        log("Dot detected");
                        return new Symbol(TokenType.DOT, ".", line);
                    }
                default:
                    break;
            }

            // If it's not a recognized symbol, handle numbers and identifiers
            if (Character.isDigit(currentChar)) {
                log("Starting number token");
                return number(currentChar);
            } else if (Character.isLetter(currentChar) || currentChar == '_') {
                log("Starting identifier/keyword token");
                return identifier(currentChar);
            }

            throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
        }
        log("End of input reached");
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
        log("Number token complete: " + value);
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
        log("Identifier/keyword token complete: " + word);

        // boolean handling
        if (word.equals("true") || word.equals("false")) {
            log("Boolean literal detected: " + word);
            return new Symbol(TokenType.BOOLEAN_LITERAL, word, line);
        }

        // keyword handling
        switch (word) {
            case "free":
                log("free keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "final":
                log("final keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "rec":
                log("rec keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "fun":
                log("fun keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "for":
                log("for keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "while":
                log("while keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "if":
                log("if keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "else":
                log("else keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            case "return":
                log("return keyword");
                return new Symbol(TokenType.KEYWORD, word, line);
            default:
                log(word + " identifier");
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
        log("String literal complete: " + value);
        return new Symbol(TokenType.STRING_LITERAL, value.toString(), line);
    }
}