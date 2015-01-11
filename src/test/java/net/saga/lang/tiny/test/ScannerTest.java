package net.saga.lang.tiny.test;

import static java.nio.CharBuffer.wrap;
import net.saga.lang.tiny.Scanner;
import net.saga.lang.tiny.Scanner.Token;
import net.saga.lang.tiny.Scanner.Token.TokenType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * pp 75 
 * 
 * Tiny has 8 reserved words
 *  if, then, else end, repeat, until, read, write
 * Tiny has 10 Special Symbols
 *  +, -, *, /, =, &lt;, (, ), ;, :=
 * Tiny has two other token types
 *  number (1 or more digits)
 *  identifier (1 or more letters)
 */
public class ScannerTest {
    
    public ScannerTest() {
    }
    
    @Test
    public void extractIfToken() {
        Token token = Scanner.nextToken(wrap("if"));
        assertEquals(TokenType.IF, token.getType());
    }
    
    @Test
    public void extractThenToken() {
        Token token = Scanner.nextToken(wrap("then"));
        assertEquals(TokenType.THEN, token.getType());
    }
    
    @Test
    public void extractElseToken() {
        Token token = Scanner.nextToken(wrap("else"));
        assertEquals(TokenType.ELSE, token.getType());
    }
    
    @Test
    public void extractEndToken() {
        Token token = Scanner.nextToken(wrap("end"));
        assertEquals(TokenType.END, token.getType());
    }
    
    
    @Test
    public void extractRepeatToken() {
        Token token = Scanner.nextToken(wrap("repeat"));
        assertEquals(TokenType.REPEAT, token.getType());
    }
    
    @Test
    public void extractUntilToken() {
        Token token = Scanner.nextToken(wrap("until"));
        assertEquals(TokenType.UNTIL, token.getType());
    }
    
    @Test
    public void extractReadToken() {
        Token token = Scanner.nextToken(wrap("read"));
        assertEquals(TokenType.READ, token.getType());
    }
    
    @Test
    public void extractWriteToken() {
        Token token = Scanner.nextToken(wrap("write"));
        assertEquals(TokenType.WRITE, token.getType());
    }
    
}
