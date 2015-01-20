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
    private final int mLineNumber;

    private Token(TokenType type, int lineNumber) {
        mType = type;
        mValue = 0;
        mName = "";
        mLineNumber = lineNumber;
    }

    private Token(TokenType type, String name, int lineNumber) {
        mType = type;
        mValue = 0;
        mName = name;
        mLineNumber = lineNumber;
    }

    private Token(TokenType type, int value, int lineNumber) {
        mType = type;
        mValue = value;
        mName = "";
        mLineNumber = lineNumber;
    }

    public TokenType getType() {
        return mType;
    }

    public int getValue() {
        return mValue;
    }

    public String getName() {
        return mName;
    }
    
    public static Token newInstance(TokenType type, int lineNumber) {
        Token newToken = new Token(type, lineNumber);
        tokens.putIfAbsent(newToken, newToken);
        return tokens.get(newToken);
    }
    
    
    public static Token newInstance(String name, int lineNumber) {
        Token newToken = new Token(TokenType.IDENTIFIER, name, lineNumber);
        tokens.putIfAbsent(newToken, newToken);
        return tokens.get(newToken);
    }
    
    public static Token newInstance(int value, int lineNumber) {
        Token newToken = new Token(TokenType.NUMBER, value, lineNumber);
        tokens.putIfAbsent(newToken, newToken);
        return tokens.get(newToken);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.mType);
        hash = 73 * hash + this.mValue;
        hash = 73 * hash + Objects.hashCode(this.mName);
        hash = 73 * hash + this.mLineNumber;
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
        if (this.mLineNumber != other.mLineNumber) {
            return false;
        }
        if (!Objects.equals(this.mName, other.mName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return mLineNumber + ":{" + "mType=" + mType + ", mValue=" + mValue + ", mName=" + mName + '}';
    }

    public int getLineNumber() {
        return mLineNumber;
    }

    
    
}
