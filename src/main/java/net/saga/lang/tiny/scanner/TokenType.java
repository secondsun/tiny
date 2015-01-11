
package net.saga.lang.tiny.scanner;

public enum TokenType {
    IF("if"), THEN("then"), ELSE("else"), END("end"), REPEAT("repeat"), UNTIL("until"), READ("read"), WRITE("write"), ADDITION("+"), SUBTRACTION("-"), MULTIPLICATION("*"), INT_DIVISION("/"), EQ("="), LT("<"), START_PAREN("("), END_PAREN(")"), SEMICOLON(";"), ASSIGNMENT(":="), NUMBER(""), IDENTIFIER("");
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
        throw new Scanner.UnknownTokenException("No such type " + test);
    }
    
}
