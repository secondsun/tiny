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
