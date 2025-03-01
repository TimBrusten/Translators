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
        assertEquals(TokenType.NATURAL_NUMBER, lexer.getNextSymbol().getType());
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
        assertEquals(TokenType.NATURAL_NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void testCommentHandling() throws IOException {
        String input = "$var x= 10\n int =";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        assertEquals(TokenType.TYPE, lexer.getNextSymbol().getType());
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
        assertEquals(TokenType.TYPE, lexer.getNextSymbol().getType());
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
        assertEquals(TokenType.FLOAT_NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void testFloatToken2() throws IOException {
        String input = " x = .2345";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());
        assertEquals(TokenType.FLOAT_NUMBER, lexer.getNextSymbol().getType());
    }

    @Test
    public void testFloatToken3() throws IOException {
        String input = " x = .234.5"; //should  not be accepted
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EQUAL, lexer.getNextSymbol().getType());
        //assertNotEquals(TokenType.FLOAT_NUMBER, lexer.getNextSymbol().getType());
    }


    //tests complets
    @Test
    public void testIdentifierComlete() throws IOException {
        String input = " abc abc123 _abc_ _12s";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.IDENTIFIER, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EOF, lexer.getNextSymbol().getType());
    }

    @Test
    public void testKeywordsComlete() throws IOException {
        String input = " free final   rec  fun for   while\n if else return"; //test white space aussi
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.KEYWORD, lexer.getNextSymbol().getType());
        assertEquals(TokenType.EOF, lexer.getNextSymbol().getType());
    }

    @Test
    public void testNumbersComlete() throws IOException {
        String input = " 123   00123 0 0.234 .234 0.34.5";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(new Symbol(TokenType.NATURAL_NUMBER, "123"), lexer.getNextSymbol());
        assertEquals(new Symbol(TokenType.NATURAL_NUMBER, "123"), lexer.getNextSymbol());
        assertEquals(new Symbol(TokenType.NATURAL_NUMBER, "0"), lexer.getNextSymbol());
        assertEquals(new Symbol(TokenType.FLOAT_NUMBER, "0.234"), lexer.getNextSymbol());
        assertEquals(new Symbol(TokenType.FLOAT_NUMBER, "0.234"), lexer.getNextSymbol());
        //assertNotEquals(TokenType.FLOAT_NUMBER, lexer.getNextSymbol().getType()); rejeter mauvais nombres
        //assertEquals(TokenType.EOF, lexer.getNextSymbol().getType());
    }

    @Test
    public void testStringsComlete() throws IOException {
        String input = "\"dfgs\" \"   ddg   d\" \"df gfdd \n \\ \" \"";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(new Symbol(TokenType.STRING_LITERAL, "dfgs"), lexer.getNextSymbol());
        assertEquals(new Symbol(TokenType.STRING_LITERAL, "   ddg   d"), lexer.getNextSymbol());
        //assertEquals(new Symbol(TokenType.STRING_LITERAL, "df gfdd \n \\ \" "), lexer.getNextSymbol()); pas sur

        //assertEquals(TokenType.EOF, lexer.getNextSymbol().getType());
    }

    @Test
    public void testBooleanComlete() throws IOException {
        String input = "true false";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(new Symbol(TokenType.BOOLEAN_LITERAL, "true"), lexer.getNextSymbol());
        assertEquals(new Symbol(TokenType.BOOLEAN_LITERAL, "false"), lexer.getNextSymbol());

    }



    @Test
    public void test_lang_complete() throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader("test_lang.txt"))) {
            Lexer lexer = new Lexer(reader);
            assertEquals(new Symbol(TokenType.KEYWORD, "final"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "message"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.TYPE, "string"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.EQUAL, "="), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.STRING_LITERAL, "Hello"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.SEMICOLON, ";"), lexer.getNextSymbol());

            assertEquals(new Symbol(TokenType.KEYWORD, "final"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "run"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.TYPE, "bool"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.EQUAL, "="), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.BOOLEAN_LITERAL, "true"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.SEMICOLON, ";"), lexer.getNextSymbol());

            assertEquals(new Symbol(TokenType.IDENTIFIER, "Point"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.KEYWORD, "rec"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.LEFT_BRACE, "{"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.TYPE, "int"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "x"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.SEMICOLON, ";"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.TYPE, "int"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "y"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.SEMICOLON, ";"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.RIGHT_BRACE, "}"), lexer.getNextSymbol());

            assertEquals(new Symbol(TokenType.IDENTIFIER, "a"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.TYPE, "int"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.EQUAL, "="), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.NATURAL_NUMBER, "3"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.SEMICOLON, ";"), lexer.getNextSymbol());

            assertEquals(new Symbol(TokenType.KEYWORD, "fun"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "square"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.LEFT_PAREN, "("), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.TYPE, "int"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "v"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.RIGHT_PAREN, ")"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.TYPE, "int"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.LEFT_BRACE, "{"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.KEYWORD, "return"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "v"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.STAR, "*"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.IDENTIFIER, "v"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.SEMICOLON, ";"), lexer.getNextSymbol());
            assertEquals(new Symbol(TokenType.RIGHT_BRACE, "}"), lexer.getNextSymbol());
        }
    }


    //ajouter des tests

}
