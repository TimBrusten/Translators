package compiler.Lexer;

import java.util.Objects;

public class Symbol {
    private final TokenType type;
    private final String lexeme;
    //private final int line; usefull or not ?

    public Symbol(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
        //this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    //public int getLine() {
      //  return line;
    //}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return type == symbol.type && Objects.equals(lexeme, symbol.lexeme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme);
    }

    @Override
    public String toString() {
        return String.format("Symbol{type=%s, lexeme='%s'}", type, lexeme);
    }
}

