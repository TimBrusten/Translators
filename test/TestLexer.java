import compiler.Lexer.Symbol;
import compiler.Lexer.TokenType;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;

import static org.junit.Assert.*;

public class TestLexer {
    
    @Test
    public void test() throws IOException {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNotNull(lexer.getNextSymbol());

    }

    @Test
    public void testComplete() throws IOException {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        int tokenCount = 0;

        Symbol symbol;
        while ((symbol = lexer.getNextSymbol()).getType() != TokenType.EOF) {
            System.out.println("<Symbol: " + symbol.getType() + ", Lexeme: " + symbol.getLexeme()+">");
            tokenCount++;
        }

        assertEquals(6, tokenCount); // var, x, int, =, 2, ;
    }

    @Test
    public void testNumberToken() throws IOException {
        String input = "12345";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void testSpecialCharacters() throws IOException {
        String input = "+-*/";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        assertEquals(TokenType.PLUS, lexer.getNextSymbol().getType());
        assertEquals(TokenType.MINUS, lexer.getNextSymbol().getType());
        assertEquals(TokenType.STAR, lexer.getNextSymbol().getType());
        assertEquals(TokenType.SLASH, lexer.getNextSymbol().getType());
    }

    @Test
    public void testWhitespaceHandling() throws IOException {
        String input = "var    x    =    10";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());
        assertEquals(TokenType.NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void testCommentHandling() throws IOException {
        String input = "$var x= 10\n int =";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());

    }

    @Test
    public void testIdentifierHandling() throws IOException {
        String input = "var_x123 = 10\n int =";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());

    }

    @Test
    public void testKeyWordHandling() throws IOException {
        String input = "final int x = 10\n int =";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());

    }

    @Test
    public void testKeyWordHandling2() throws IOException {
        String input = "final rec fun";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());

    }

    @Test
    public void testFloatToken1() throws IOException {
        String input = " x = 0.2345";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());
        assertEquals(TokenType.NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void testFloatToken2() throws IOException {
        String input = " x = .2345";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());
        assertEquals(TokenType.NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void testFloatToken3() throws IOException {
        String input = " x = .234.5"; //should  not be accepted
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());
        assertEquals(TokenType.NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void test_lang_complete() throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader("test_lang.txt"))) {
            Lexer lexer = new Lexer(reader);
            Symbol symbol;
            do {
                symbol = lexer.getNextSymbol();
                System.out.println(symbol);
            } while (symbol.getType() != TokenType.EOF);
        }
    }


    //ajouter des tests

}
