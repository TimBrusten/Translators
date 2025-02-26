package compiler.Lexer;

public class Symbol {
    private final TokenType type;
    private final String lexeme;
    private final int line;

    public Symbol(TokenType type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return String.format("Symbol{type=%s, lexeme='%s', line=%d}", type, lexeme, line);
    }
}

