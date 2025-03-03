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
                    return new Symbol(TokenType.LEFT_PAREN, "(");
                case ')':
                    log("Right parenthesis detected");
                    return new Symbol(TokenType.RIGHT_PAREN, ")");
                case '{':
                    log("Left brace detected");
                    return new Symbol(TokenType.LEFT_BRACE, "{");
                case '}':
                    log("Right brace detected");
                    return new Symbol(TokenType.RIGHT_BRACE, "}");
                case '[':
                    log("Left bracket detected");
                    return new Symbol(TokenType.LEFT_BRACKET, "[");
                case ']':
                    log("Right bracket detected");
                    return new Symbol(TokenType.RIGHT_BRACKET, "]");
                case ',':
                    log("Comma detected");
                    return new Symbol(TokenType.COMMA, ",");
                case ';':
                    log("Semicolon detected");
                    return new Symbol(TokenType.SEMICOLON, ";");
                case '+':
                    log("Plus sign detected");
                    return new Symbol(TokenType.PLUS, "+");
                case '-':
                    log("Minus sign detected");
                    return new Symbol(TokenType.MINUS, "-");
                case '*':
                    log("Star detected");
                    return new Symbol(TokenType.STAR, "*");
                case '/':
                    log("Slash detected");
                    return new Symbol(TokenType.SLASH, "/");
                case '=':
                    input.mark(1);
                    int next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Equal equal (==) detected");
                        return new Symbol(TokenType.EQUAL_EQUAL, "==");
                    } else {
                        input.reset();
                        log("Equal detected");
                        return new Symbol(TokenType.EQUAL, "=");
                    }
                case '!':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Not equal (!=) detected");
                        return new Symbol(TokenType.NOT_EQUAL, "!=");
                    } else {
                        input.reset();
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                case '<':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Less than or equal (<=) detected");
                        return new Symbol(TokenType.LESS_EQUAL, "<=");
                    } else {
                        input.reset();
                        log("Less than (<) detected");
                        return new Symbol(TokenType.LESS_THAN, "<");
                    }
                case '>':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '=') {
                        log("Greater than or equal (>=) detected");
                        return new Symbol(TokenType.GREATER_EQUAL, ">=");
                    } else {
                        input.reset();
                        log("Greater than (>) detected");
                        return new Symbol(TokenType.GREATER_THAN, ">");
                    }
                case '&':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '&') {
                        log("Logical AND (&&) detected");
                        return new Symbol(TokenType.AND, "&&");
                    } else {
                        input.reset();
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                case '|':
                    input.mark(1);
                    next = input.read();
                    if (next != -1 && (char) next == '|') {
                        log("Logical OR (||) detected");
                        return new Symbol(TokenType.OR, "||");
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
                        return new Symbol(TokenType.DOT, ".");
                    }
                default:
                    break;
            }

            // If it's not a recognized symbol, handle numbers and identifiers
            if (Character.isDigit(currentChar)&&currentChar != '0') {
                log("Starting number token");
                return number(currentChar);
            } else if (currentChar == '0') {
                return numberStartingWithZero(currentChar);

            } else if (Character.isLetter(currentChar) || currentChar == '_') {
                log("Starting identifier/keyword token");
                return identifier(currentChar);
            }

            throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
        }
        log("End of input reached");
        return new Symbol(TokenType.EOF, "EOF");
    }

    private Symbol numberStartingWithZero(char first) throws IOException {
        input.mark(1);
        int next = input.read();

        if (next == '.') {
            // Float starting with 0, like 0.234
            return number('.');
        } else if (Character.isDigit((char) next)) {
            // Start capturing the number, skipping leading zeros
            while (next == '0') {
                input.mark(1);
                next = input.read();
            }

            if (!Character.isDigit((char) next) && next != '.') {
                // It was just a single zero, like "0"
                input.reset();
                return new Symbol(TokenType.NATURAL_NUMBER, "0");
            }

            // We found a non-zero digit or a decimal point
            StringBuilder value = new StringBuilder();
            if (Character.isDigit((char) next)) {
                value.append((char) next);
            }

            return finishNumber(value, true); // true -> we skipped leading zeroes
        } else {
            // Just a single zero
            input.reset();
            return new Symbol(TokenType.NATURAL_NUMBER, "0");
        }
    }

    private Symbol finishNumber(StringBuilder value, boolean hadLeadingZero) throws IOException {
        input.mark(1);
        int next;

        boolean hasDot = false;
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

        // If we skipped leading zeroes and then found only digits (like 0234), convert it properly
        if (!hasDot && hadLeadingZero && value.length() > 0) {
            return new Symbol(TokenType.NATURAL_NUMBER, value.toString());
        }

        if (hasDot) return new Symbol(TokenType.FLOAT_NUMBER, value.toString());
        return new Symbol(TokenType.NATURAL_NUMBER, value.toString());
    }


    private Symbol number(char first) throws IOException {
        StringBuilder value = new StringBuilder();
        boolean hasDot = false;
        if (first == '.') {
            hasDot = true;
            value.append('0');
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

        if (!hasDot && first == 0){

        }

        if (hasDot) return new Symbol(TokenType.FLOAT_NUMBER, value.toString());
        return new Symbol(TokenType.NATURAL_NUMBER, value.toString());
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
            return new Symbol(TokenType.BOOLEAN_LITERAL, word);
        }

        if (Character.isUpperCase(first)){
            return new Symbol(TokenType.RECORD_IDENTIFIER, word);
        }

        // keyword and type handling
        switch (word) {
            case "free":
                log("free keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "final":
                log("final keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "rec":
                log("rec keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "fun":
                log("fun keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "for":
                log("for keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "while":
                log("while keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "if":
                log("if keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "else":
                log("else keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "return":
                log("return keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "of":
                log("of keyword");
                return new Symbol(TokenType.KEYWORD, word);
            case "array":
                log("array keyword");
                return new Symbol(TokenType.KEYWORD, word);


            case "string":
                log("string type");
                return new Symbol(TokenType.TYPE, word);
            case "int":
                log("int type");
                return new Symbol(TokenType.TYPE, word);

            case "bool":
                log("boolean type");
                return new Symbol(TokenType.TYPE, word);

            default:
                log(word + " identifier");
                return new Symbol(TokenType.IDENTIFIER, word);
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
        return new Symbol(TokenType.STRING_LITERAL, value.toString());
    }
}