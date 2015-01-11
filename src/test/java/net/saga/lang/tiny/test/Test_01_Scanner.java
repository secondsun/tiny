/**
 * Copyright (C) 2015 Summers Pittman (secondsun@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.saga.lang.tiny.test;

import java.io.IOException;
import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.tiny.scanner.Scanner;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.scanner.TokenType;
import static net.saga.lang.tiny.scanner.TokenType.ASSIGNMENT;
import static net.saga.lang.tiny.scanner.TokenType.END;
import static net.saga.lang.tiny.scanner.TokenType.EQ;
import static net.saga.lang.tiny.scanner.TokenType.IF;
import static net.saga.lang.tiny.scanner.TokenType.LT;
import static net.saga.lang.tiny.scanner.TokenType.MULTIPLICATION;
import static net.saga.lang.tiny.scanner.TokenType.READ;
import static net.saga.lang.tiny.scanner.TokenType.REPEAT;
import static net.saga.lang.tiny.scanner.TokenType.SEMICOLON;
import static net.saga.lang.tiny.scanner.TokenType.SUBTRACTION;
import static net.saga.lang.tiny.scanner.TokenType.THEN;
import static net.saga.lang.tiny.scanner.TokenType.UNTIL;
import static net.saga.lang.tiny.scanner.TokenType.WRITE;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * pp 75
 *
 * Tiny has 8 reserved words if, then, else end, repeat, until, read, write Tiny
 * has 10 Special Symbols +, -, *, /, =, &lt;, (, ), ;, := Tiny has two other
 * token types number (1 or more digits) identifier (1 or more letters) Comments
 * in Tiny are surrounded by { } ex. {This is a comment.} ex. { This is also a
 * comment. }
 */
public class Test_01_Scanner {

    public Test_01_Scanner() {
    }

    @Test
    public void extractIfToken() {
        Token token = new Scanner().nextToken(wrap("if"));
        assertEquals(TokenType.IF, token.getType());
    }

    @Test
    public void extractThenToken() {
        Token token = new Scanner().nextToken(wrap("then"));
        assertEquals(TokenType.THEN, token.getType());
    }

    @Test
    public void extractElseToken() {
        Token token = new Scanner().nextToken(wrap("else"));
        assertEquals(TokenType.ELSE, token.getType());
    }

    @Test
    public void extractEndToken() {
        Token token = new Scanner().nextToken(wrap("end"));
        assertEquals(TokenType.END, token.getType());
    }

    @Test
    public void extractRepeatToken() {
        Token token = new Scanner().nextToken(wrap("repeat"));
        assertEquals(TokenType.REPEAT, token.getType());
    }

    @Test
    public void extractUntilToken() {
        Token token = new Scanner().nextToken(wrap("until"));
        assertEquals(TokenType.UNTIL, token.getType());
    }

    @Test
    public void extractReadToken() {
        Token token = new Scanner().nextToken(wrap("read"));
        assertEquals(TokenType.READ, token.getType());
    }

    @Test
    public void extractWriteToken() {
        Token token = new Scanner().nextToken(wrap("write"));
        assertEquals(TokenType.WRITE, token.getType());
    }

    /**
     * At this point adding in more tokens should be easy.
     */
    @Test
    public void extractSymbols() {
        Token token;
        token = new Scanner().nextToken(wrap("+"));
        assertEquals(TokenType.ADDITION, token.getType());
        token = new Scanner().nextToken(wrap("-"));
        assertEquals(TokenType.SUBTRACTION, token.getType());
        token = new Scanner().nextToken(wrap("*"));
        assertEquals(TokenType.MULTIPLICATION, token.getType());
        token = new Scanner().nextToken(wrap("/"));
        assertEquals(TokenType.INT_DIVISION, token.getType());
        token = new Scanner().nextToken(wrap("="));
        assertEquals(TokenType.EQ, token.getType());
        token = new Scanner().nextToken(wrap("<"));
        assertEquals(TokenType.LT, token.getType());
        token = new Scanner().nextToken(wrap("("));
        assertEquals(TokenType.START_PAREN, token.getType());
        token = new Scanner().nextToken(wrap(")"));
        assertEquals(TokenType.END_PAREN, token.getType());
        token = new Scanner().nextToken(wrap(";"));
        assertEquals(TokenType.SEMICOLON, token.getType());
        token = new Scanner().nextToken(wrap(":="));
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
        token = new Scanner().nextToken(wrap("42"));
        assertEquals(TokenType.NUMBER, token.getType());
        assertEquals(42, token.getValue());
    }

    @Test
    public void extractIdentifier() {
        Token token;
        token = new Scanner().nextToken(wrap("x"));
        assertEquals(TokenType.IDENTIFIER, token.getType());
        assertEquals("x", token.getName());
    }

    /**
     * Now all tokens should be able to be scanned out.
     *
     * First lets make sure next token handles comments and white space
     */
    @Test
    public void ignoreComments() {
        Token token = new Scanner().nextToken(wrap("{this is a comment} end"));
        assertEquals(TokenType.END, token.getType());
    }

    @Test(expected = IllegalStateException.class)
    public void errorOnUnterminatedComment() {
        new Scanner().nextToken(wrap("{this is a comment"));
        fail();
    }

    /**
     * Tokens should include line info because that will be used later on
     * in compilation.
     */
    @Test
    public void testNewline() {
        Token token = new Scanner().nextToken(wrap("{this is a comment}\n end"));
        assertEquals(TokenType.END, token.getType());
        assertEquals(2, token.getLineNumber());
    }

    
    /**
     * Now we will test that we can extract multiple tokens in an input.
     */
    @Test
    public void whenMultipleTokensExtractFirst() {
        Token token = new Scanner().nextToken(wrap("if then else"));
        assertEquals(TokenType.IF, token.getType());
    }

    @Test
    public void whenMultipleTokensExtractFirstWithoutWhitespace() {
        Token token = new Scanner().nextToken(wrap("if+then+else"));
        assertEquals(TokenType.IF, token.getType());
    }

    @Test
    public void extractIfThenElseTokens() {
        List<Token> token = new Scanner().scan(wrap("if then else"));
        assertEquals(TokenType.IF, token.get(0).getType());
        assertEquals(TokenType.THEN, token.get(1).getType());
        assertEquals(TokenType.ELSE, token.get(2).getType());

    }

    /**
     * Now we SHOULD be able to parse a longer file.
     *
     * @throws java.io.IOException this won't happen
     */
    @Test
    public void parseSampleDotTny() throws IOException {
        String program = IOUtils.toString(Test_01_Scanner.class.getClassLoader().getResourceAsStream("sample.tny"));
        List<Token> tokens = new Scanner().scan(wrap(program));
        int index = 0;
        //line 5
        assertEquals(Token.newInstance(READ, 5), tokens.get(index++));
        assertEquals(Token.newInstance("x", 5), tokens.get(index++));
        assertEquals(Token.newInstance(SEMICOLON, 5), tokens.get(index++));
        //line 6
        assertEquals(Token.newInstance(IF, 6), tokens.get(index++));
        assertEquals(Token.newInstance(0, 6), tokens.get(index++));
        assertEquals(Token.newInstance(LT, 6), tokens.get(index++));
        assertEquals(Token.newInstance("x", 6), tokens.get(index++));
        assertEquals(Token.newInstance(THEN, 6), tokens.get(index++));
        //line 7
        assertEquals(Token.newInstance("fact", 7), tokens.get(index++));
        assertEquals(Token.newInstance(ASSIGNMENT, 7), tokens.get(index++));
        assertEquals(Token.newInstance(1, 7), tokens.get(index++));
        assertEquals(Token.newInstance(SEMICOLON, 7), tokens.get(index++));
        //line 8
        assertEquals(Token.newInstance(REPEAT, 8), tokens.get(index++));
        //line 9
        assertEquals(Token.newInstance("fact", 9), tokens.get(index++));
        assertEquals(Token.newInstance(ASSIGNMENT, 9), tokens.get(index++));
        assertEquals(Token.newInstance("fact", 9), tokens.get(index++));
        assertEquals(Token.newInstance(MULTIPLICATION, 9), tokens.get(index++));
        assertEquals(Token.newInstance("x", 9), tokens.get(index++));
        assertEquals(Token.newInstance(SEMICOLON, 9), tokens.get(index++));
        //line 10
        assertEquals(Token.newInstance("x", 10), tokens.get(index++));
        assertEquals(Token.newInstance(ASSIGNMENT, 10), tokens.get(index++));
        assertEquals(Token.newInstance("x", 10), tokens.get(index++));
        assertEquals(Token.newInstance(SUBTRACTION, 10), tokens.get(index++));
        assertEquals(Token.newInstance(1, 10), tokens.get(index++));
        //line 11
        assertEquals(Token.newInstance(UNTIL, 11), tokens.get(index++));
        assertEquals(Token.newInstance("x", 11), tokens.get(index++));
        assertEquals(Token.newInstance(EQ, 11), tokens.get(index++));
        assertEquals(Token.newInstance(0, 11), tokens.get(index++));
        assertEquals(Token.newInstance(SEMICOLON, 11), tokens.get(index++));
        //line 12
        assertEquals(Token.newInstance(WRITE, 12), tokens.get(index++));
        assertEquals(Token.newInstance("fact", 12), tokens.get(index++));
        //line 13
        assertEquals(Token.newInstance(END, 13), tokens.get(index++));
        
    }

}
