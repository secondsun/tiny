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

public class Scanner {

    public static Token nextToken(CharBuffer buffer) {
        StringBuilder tokenBuilder = new StringBuilder(40);
        while (buffer.hasRemaining()) {
            tokenBuilder.append(buffer.get());
        }

        switch (tokenBuilder.toString()) {
            case "if":
                return new Token(Token.TokenType.IF);

            case "then":
                return new Token(Token.TokenType.THEN);
            case "else":
                return new Token(Token.TokenType.ELSE);
            case "end":
                return new Token(Token.TokenType.END);
            case "repeat":
                return new Token(Token.TokenType.REPEAT);
            case "until":
                return new Token(Token.TokenType.UNTIL);
            case "read":
                return new Token(Token.TokenType.READ);
            case "write":
                return new Token(Token.TokenType.WRITE);
            default:
                throw new IllegalStateException("not supported");
        }

    }

    public static class Token {

        private final TokenType mType;

        public Token(TokenType type) {
            mType = type;
        }

        public Object getType() {
            return mType;
        }

        public enum TokenType {

            IF, THEN, ELSE, END, REPEAT, UNTIL, READ, WRITE
        }

    }

}
