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
package net.saga.lang.cminus.test;

import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.cminus.scanner.Scanner;
import net.saga.lang.cminus.scanner.Token;
import net.saga.lang.cminus.scanner.TokenType;
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
    public void extractTokens() {

        Token token = new Scanner().nextToken(wrap(TokenType.IF.getTokenString()));
        assertEquals(TokenType.IF, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.WHILE.getTokenString()));
        assertEquals(TokenType.WHILE, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.ELSE.getTokenString()));
        assertEquals(TokenType.ELSE, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.VOID.getTokenString()));
        assertEquals(TokenType.VOID, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.INT.getTokenString()));
        assertEquals(TokenType.INT, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.RETURN.getTokenString()));
        assertEquals(TokenType.RETURN, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.PLUS.getTokenString()));
        assertEquals(TokenType.PLUS, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.MINUS.getTokenString()));
        assertEquals(TokenType.MINUS, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.MULTIPLY.getTokenString()));
        assertEquals(TokenType.MULTIPLY, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.DIVIDE.getTokenString()));
        assertEquals(TokenType.DIVIDE, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.LT.getTokenString()));
        assertEquals(TokenType.LT, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.LTE.getTokenString()));
        assertEquals(TokenType.LTE, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.GT.getTokenString()));
        assertEquals(TokenType.GT, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.GTE.getTokenString()));
        assertEquals(TokenType.GTE, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.EQ.getTokenString()));
        assertEquals(TokenType.EQ, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.NE.getTokenString()));
        assertEquals(TokenType.NE, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.ASSIGN.getTokenString()));
        assertEquals(TokenType.ASSIGN, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.SEMI_COLON.getTokenString()));
        assertEquals(TokenType.SEMI_COLON, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.COMMA.getTokenString()));
        assertEquals(TokenType.COMMA, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.LPAREN.getTokenString()));
        assertEquals(TokenType.LPAREN, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.RPAREN.getTokenString()));
        assertEquals(TokenType.RPAREN, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.L_BRACKET.getTokenString()));
        assertEquals(TokenType.L_BRACKET, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.R_BRACKET.getTokenString()));
        assertEquals(TokenType.R_BRACKET, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.L_BRACE.getTokenString()));
        assertEquals(TokenType.L_BRACE, token.getType());

        token = new Scanner().nextToken(wrap(TokenType.R_BRACE.getTokenString()));
        assertEquals(TokenType.R_BRACE, token.getType());

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
        Token token = new Scanner().nextToken(wrap("/*this is a comment*/ ;"));
        assertEquals(TokenType.SEMI_COLON, token.getType());
    }

    @Test(expected = IllegalStateException.class)
    public void errorOnUnterminatedComment() {
        new Scanner().nextToken(wrap("/*this is a comment"));
        fail();
    }

    /**
     * Tokens should include line info because that will be used later on in
     * compilation.
     */
    @Test
    public void testNewline() {
        Token token = new Scanner().nextToken(wrap("/**/\n ;"));
        assertEquals(TokenType.SEMI_COLON, token.getType());
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
        List<Token> token = new Scanner().scan(wrap("if while else"));
        assertEquals(TokenType.IF, token.get(0).getType());
        assertEquals(TokenType.WHILE, token.get(1).getType());
        assertEquals(TokenType.ELSE, token.get(2).getType());

    }


}
