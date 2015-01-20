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
package net.saga.lang.cminus.scanner;

import java.io.PushbackReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import static net.saga.lang.cminus.scanner.TokenType.*;

public class Scanner {

    private int lineNumber = 1;

    public Token nextToken(CharBuffer buffer) {
        String token = getNextTokenString(buffer);

        if (token.isEmpty()) {
            return null;
        }

        TokenType type = TokenType.fromString(token);

        switch (type) {

            case NUMBER:
                return Token.newInstance(Integer.parseInt(token), lineNumber);
            case IDENTIFIER:
                return Token.newInstance(token, lineNumber);
            case IF:
            case WHILE:
            case ELSE:
            case VOID:
            case INT:
            case RETURN:
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case LT:
            case LTE:
            case GT:
            case GTE:
            case EQ:
            case NE:
            case ASSIGN:
            case SEMI_COLON:
            case COMMA:
            case LPAREN:
            case RPAREN:
            case L_BRACKET:
            case R_BRACKET:
            case L_BRACE:
            case R_BRACE:
                return Token.newInstance(type, lineNumber);
            default:
                throw new AssertionError(type.name());

        }
    }

    public List<Token> scan(CharBuffer buffer) {
        ArrayList<Token> tokens = new ArrayList<>(buffer.length() / 7);//Return is the longest token so lets assume that the number of tokens is around 1/7 the length of the input.
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
        char character2 = buffer.hasRemaining() ? peek(buffer):'\n';
        
        switch (character) {
            case '<':
                tokenBuilder.append(character);
                if (character2 == '=') {
                    tokenBuilder.append(character2);
                    buffer.get();
                }
                break;
            case '>':
                tokenBuilder.append(character);
                if (character2 == '=') {
                    tokenBuilder.append(character2);
                    buffer.get();
                }
                break;
            case '=':
                tokenBuilder.append(character);
                if (character2 == '=') {
                    tokenBuilder.append(character2);
                    buffer.get();
                }
                break;
            case '!':
                tokenBuilder.append(character);
                if (character2 == '=') {
                    tokenBuilder.append(character2);
                    buffer.get();
                }
                break;
            case '/':
                
                if (character2 == '*') {
                    buffer.get();
                    handleComment(buffer);
                } else {
                    tokenBuilder.append(character);
                }
                break;
            default:
                if (Character.isWhitespace(character)) {
                    if (character == '\n') {
                        lineNumber++;
                    }
                    handleWhiteSpace(buffer);
                } else if (Character.isDigit(character)) {
                    tokenBuilder.append(character);
                    handleDigit(tokenBuilder, buffer);
                } else if (Character.isAlphabetic(character)) {
                    tokenBuilder.append(character);
                    handleIdentifier(tokenBuilder, buffer);

                } else {
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
        if (!buffer.hasRemaining() || buffer.remaining() == 1) {
            throw new IllegalStateException("Unexpected end of file");
        }
        buffer.mark();
        char character = buffer.get();
        char character2 = peek(buffer);
        if (character == '/' && character2 == '*') {
            throw new IllegalStateException("Nested comments");
        } else if ((character == '*' && character2 == '/')) {
            buffer.get();//consume the / from */
        } else {
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

    private char peek(CharBuffer buffer) {
        buffer.mark();
        char toReturn = buffer.get();
        buffer.reset();
        return toReturn;
    }

}
