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
package net.saga.lang.tiny.scanner;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import net.saga.lang.tiny.scanner.TokenType;

public class Scanner {

    private int lineNumber = 1;
    
    /**
     * Given the a input buffer, find the next token.
     * 
     * Then buffer will be left at the first token after a token is found
     * 
     * @param buffer a charbuffer
     * @return a token
     */
    public Token nextToken(CharBuffer buffer) {
        
        String token = getNextTokenString(buffer);
        
        if (token.isEmpty()) {
            return null;
        }
        
        TokenType type = TokenType.fromString(token);
        
        switch (type) {
            case IF:
            case THEN:
            case ELSE:
            case END:
            case REPEAT:    
            case UNTIL:
            case READ:
            case WRITE:
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case INT_DIVISION:
            case EQ:
            case LT:
            case START_PAREN:
            case END_PAREN:
            case SEMICOLON:
            case ASSIGNMENT:
                return Token.newInstance(type, lineNumber);
            case NUMBER:
                return Token.newInstance(Integer.parseInt(token), lineNumber);
            case IDENTIFIER:
                return Token.newInstance(token, lineNumber);
            default:
                throw new AssertionError(type.name());
               
        }

    }

    public List<Token> scan(CharBuffer buffer) {
        ArrayList<Token> tokens = new ArrayList<>(100);
        Token token = nextToken(buffer);
        while (token != null) {
            tokens.add(token);
            token = nextToken(buffer);
        }
        return tokens;
    }

    private String getNextTokenString(CharBuffer buffer) {
        StringBuilder tokenBuilder = new StringBuilder(40);
        if (!buffer.hasRemaining()) {
            return "";
        }
        buffer.mark();
        char character = buffer.get();
        
        if (character == '{') {
            handleComment(buffer);
        } else if (Character.isWhitespace(character)) {
            if (character == '\n') {
                lineNumber++;
            }
            handleWhiteSpace(buffer);
        } else if (Character.isDigit(character)){
            tokenBuilder.append(character);
            handleDigit(tokenBuilder, buffer);
        } else if (Character.isAlphabetic(character)){
            tokenBuilder.append(character);
            handleIdentifier(tokenBuilder, buffer);
        } else {
            tokenBuilder.append(character);
            if (character == ':') {
                character = buffer.get();
                tokenBuilder.append(character);
            }

        }

        if (tokenBuilder.length() == 0 && buffer.hasRemaining()) {
            //This will happen with the end of a comment or whitespace
            return getNextTokenString(buffer);
        }
        
        return tokenBuilder.toString();
        
    }

    private void handleComment(CharBuffer buffer) {
        if (!buffer.hasRemaining()) {
            throw new IllegalStateException("Unexpected end of file");
        }
        buffer.mark();
        char character = buffer.get();
        if (character == '{') {
            throw new IllegalStateException("Nested comments");
        } else if (character != '}') {
            if (character == '\n') {
                lineNumber++;
            }
            handleComment(buffer);
        }
        
    }

    private void handleWhiteSpace(CharBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return;
        }
        buffer.mark();
        char character = buffer.get();
        if (Character.isWhitespace(character)) {
            if (character == '\n') {
                lineNumber++;
            }
            handleWhiteSpace(buffer);
        } else {
            buffer.reset();
        }
    }

    private void handleDigit(StringBuilder builder, CharBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return;
        }
        buffer.mark();
        char character = buffer.get();
        if (Character.isDigit(character)) {
            builder.append(character);
            handleDigit(builder, buffer);
        } else {
            buffer.reset();
        }
    }

    private void handleIdentifier(StringBuilder builder, CharBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return;
        }
        buffer.mark();
        char character = buffer.get();
        if (Character.isAlphabetic(character)) {
            builder.append(character);
            handleIdentifier(builder, buffer);
        } else {
            buffer.reset();
        }
    }


    public static class UnknownTokenException extends RuntimeException {
        public UnknownTokenException(String message) {
            super(message);
        }
    }
    
}
