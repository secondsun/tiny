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

package net.saga.lang.cminus.scanner;

public enum TokenType {
    //Keywords
        //Control
        IF("if"), WHILE("while"), ELSE("else"),
        //TYPE
        VOID("void"), INT("int"),
        //functions
        RETURN("return"),
        //constants and identifiers
        NUMBER(""), IDENTIFIER(""),
    //Symbols
        //Math
        PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/"),
        //Comparison
        LT("<"), LTE("<="), GT(">"), GTE(">="), EQ("=="), NE("!="), ASSIGN("="),
        //punctuation
        SEMI_COLON(";"), COMMA(","),LPAREN("("), RPAREN(")"), L_BRACKET("["), R_BRACKET("]"), L_BRACE("{"), R_BRACE("}");
    
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
    
    public String getTokenString() {
        return mStringToken;
    }
    
}
