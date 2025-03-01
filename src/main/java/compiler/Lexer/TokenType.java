package compiler.Lexer;

public enum TokenType {
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET,
    PLUS, MINUS, STAR, SLASH,
    EQUAL, EQUAL_EQUAL,
    NOT_EQUAL,
    LESS_THAN, LESS_EQUAL,
    GREATER_THAN, GREATER_EQUAL,
    AND, OR,
    COMMA, DOT,
    NUMBER, STRING_LITERAL, BOOLEAN_LITERAL, IDENTIFIER,
    KEYWORD, // Ex : free, final, rec, fun, for, while, if, else, return
    EOF,
    SEMICOLON
}