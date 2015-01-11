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
package net.saga.lang.tiny;

import java.nio.CharBuffer;
import java.util.List;
import net.saga.lang.tiny.Scanner.Token.TokenType;

public class Scanner {

    /**
     * Given the a input buffer, find the next token.
     * 
     * Then buffer will be left at the first token after a token is found
     * 
     * @param buffer a charbuffer
     * @return a token
     */
    public static Token nextToken(CharBuffer buffer) {
        StringBuilder tokenBuilder = new StringBuilder(40);
        
        while (buffer.hasRemaining()) {
            char character = buffer.get();
            if (character == '{') {
                while((character = buffer.get()) != '}'){
                    if (character == '{') {
                        throw new IllegalStateException("Nest comments");
                    }
                }
            } else if (character == ' ') {
            } else {
                tokenBuilder.append(character);
            }
            
        }
        String token = tokenBuilder.toString();
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
                return new Token(type);
            case NUMBER:
                return new Token(type, Integer.parseInt(token));
            case IDENTIFIER:
                return new Token(type, token);
            default:
                throw new AssertionError(type.name());
               
        }

    }

    public static List<Token> parse(CharBuffer wrap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class Token {

        private final TokenType mType;
        private final int mValue;
        private final String mName;

        public Token(TokenType type) {
            mType = type;
            mValue = 0;
            mName = null;
        }

        public Token(TokenType type, String name) {
            mType = type;
            mValue = 0;
            mName = name;
        }
        
        public Token(TokenType type, int value) {
            mType = type;
            mValue = value;
            mName = null;
        }
        
        public Object getType() {
            return mType;
        }

        public int getValue() {
            return mValue;
        }

        public Object getName() {
            return mName;
        }

        public enum TokenType {

            IF("if"), 
            THEN("then"), 
            ELSE("else"), 
            END("end"), 
            REPEAT("repeat"), 
            UNTIL("until"), 
            READ("read"), 
            WRITE("write"), 
            ADDITION("+"), 
            SUBTRACTION("-"), 
            MULTIPLICATION("*"), 
            INT_DIVISION("/"), 
            EQ("="), 
            LT("<"), 
            START_PAREN("("), 
            END_PAREN(")"), 
            SEMICOLON(";"), 
            ASSIGNMENT(":="), 
            NUMBER(""), 
            IDENTIFIER("");
            private final String mStringToken;
            
            private TokenType(String token) {
                this.mStringToken = token;
            }
            
            public static TokenType fromString(String test) {
                for (TokenType type : TokenType.values()) {
                    if (test.equalsIgnoreCase(type.mStringToken)) {
                        return type;
                    }
                }
                
                if (test.matches("^[\\d]+$")) {
                    return NUMBER;
                }
                
                if (test.matches("^[a-zA-Z]+$")) {
                    return IDENTIFIER;
                }
                throw new UnknownTokenException("No such type " + test);
            }
            
        }

    }

    public static class UnknownTokenException extends RuntimeException {
        public UnknownTokenException(String message) {
            super(message);
        }
    }
    
}
