/**
 * Copyright (C) 2015 Summers Pittman (secondsun@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saga.lang.tiny.test;

import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.tiny.Scanner;
import net.saga.lang.tiny.Scanner.Token;
import net.saga.lang.tiny.Scanner.Token.TokenType;
import net.saga.lang.tiny.Scanner.UnknownTokenException;
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
 * Comments in Tiny are surrounded by { }
 *  ex. {This is a comment.}
 *  ex. {
 *          This is also a comment.
 *      }
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
    
    /**
     * At this point adding in more tokens should be easy.
     */
    @Test
    public void extractSymbols() {
        Token token;
        token = Scanner.nextToken(wrap("+"));
        assertEquals(TokenType.ADDITION, token.getType());
        token = Scanner.nextToken(wrap("-"));
        assertEquals(TokenType.SUBTRACTION, token.getType());
        token = Scanner.nextToken(wrap("*"));
        assertEquals(TokenType.MULTIPLICATION, token.getType());
        token = Scanner.nextToken(wrap("/"));
        assertEquals(TokenType.INT_DIVISION, token.getType());
        token = Scanner.nextToken(wrap("="));
        assertEquals(TokenType.EQ, token.getType());
        token = Scanner.nextToken(wrap("<"));
        assertEquals(TokenType.LT, token.getType());
        token = Scanner.nextToken(wrap("("));
        assertEquals(TokenType.START_PAREN, token.getType());
        token = Scanner.nextToken(wrap(")"));
        assertEquals(TokenType.END_PAREN, token.getType());
        token = Scanner.nextToken(wrap(";"));
        assertEquals(TokenType.SEMICOLON, token.getType());
        token = Scanner.nextToken(wrap(":="));
        assertEquals(TokenType.ASSIGNMENT, token.getType());
    }

    /**
     * Number Tokens can have values.
     * 
     * Numbers in TINY are currently only positive.
     * 
     */
    @Test
    public void extractNumbers() {
        Token token;
        token = Scanner.nextToken(wrap("42"));
        assertEquals(TokenType.NUMBER, token.getType());
        assertEquals(42, token.getValue());
    }
    
    @Test
    public void extractIdentifier() {
        Token token;
        token = Scanner.nextToken(wrap("x"));
        assertEquals(TokenType.IDENTIFIER, token.getType());
        assertEquals("x", token.getName());
    }
    
    @Test(expected = UnknownTokenException.class)
    public void illegalTokensThrowException() {
        Scanner.nextToken(wrap("x21"));
        fail();
    }
    
    /**
     * Now all tokens should be able to be scanned out.
     * 
     * We are going to move on to a `parse` method which will return a 
     * List of tokens.
     * 
     * 
     */
    @Test
    public void ignoreComments() {
        List<Token> tokens = Scanner.parse(wrap("{this is a comment} end"));
        assertEquals(TokenType.END, tokens.get(0).getType());
    }
//    
//    @Test
//    public void extractIfThenElseTokens() {
//        List<Token> token = Scanner.nextToken(wrap("if then else"));
//        assertEquals(TokenType.IF, token.get(0).getType());
//        assertEquals(TokenType.THEN, token.get(1).getType());
//        assertEquals(TokenType.ELSE, token.get(2).getType());
//        
//    }
//    
    
    
    
    
    
    
}
