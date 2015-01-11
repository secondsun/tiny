package net.saga.lang.tiny.scanner;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author summers
 */
public class Token {

    private static ConcurrentHashMap<Token, Token> tokens = new ConcurrentHashMap<>(100);
    
    private final TokenType mType;
    private final int mValue;
    private final String mName;

    private Token(TokenType type) {
        mType = type;
        mValue = 0;
        mName = "";
    }

    private Token(TokenType type, String name) {
        mType = type;
        mValue = 0;
        mName = name;
    }

    private Token(TokenType type, int value) {
        mType = type;
        mValue = value;
        mName = "";
    }

    public Object getType() {
        return mType;
    }

    public int getValue() {
        return mValue;
    }

    public String getName() {
        return mName;
    }
    
    public static Token newInstance(TokenType type) {
        Token newToken = new Token(type);
        tokens.putIfAbsent(newToken, newToken);
        return tokens.get(newToken);
    }
    
    
    public static Token newInstance(String name) {
        Token newToken = new Token(TokenType.IDENTIFIER, name);
        tokens.putIfAbsent(newToken, newToken);
        return tokens.get(newToken);
    }
    
    public static Token newInstance(int value) {
        Token newToken = new Token(TokenType.NUMBER, value);
        tokens.putIfAbsent(newToken, newToken);
        return tokens.get(newToken);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.mType);
        hash = 73 * hash + this.mValue;
        hash = 73 * hash + Objects.hashCode(this.mName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (this.mType != other.mType) {
            return false;
        }
        if (this.mValue != other.mValue) {
            return false;
        }
        if (!Objects.equals(this.mName, other.mName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Token{" + "mType=" + mType + ", mValue=" + mValue + ", mName=" + mName + '}';
    }

    
    
}
