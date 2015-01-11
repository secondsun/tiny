package net.saga.lang.tiny.parser;

import java.util.Iterator;
import java.util.List;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.scanner.TokenType;
import static net.saga.lang.tiny.scanner.TokenType.END_PAREN;
import static net.saga.lang.tiny.scanner.TokenType.MULTIPLICATION;
import static net.saga.lang.tiny.scanner.TokenType.NUMBER;
import static net.saga.lang.tiny.scanner.TokenType.START_PAREN;

public class Parser {
    
    private Iterator<Token> tokensIter;
    private Token token;
    
    public void setTokens(List<Token> tokens) {
        
        tokensIter = tokens.iterator();
    }

    public Token nextToken() {
        if (tokensIter.hasNext()) {
            return token = tokensIter.next();
        } else {
            return token = null;
        }
    }

    public Node parse(List<Token> tokens) {
        tokensIter = tokens.iterator();
        nextToken();
        return parse();
    }

    
    /**
     * Called after setTokens and nextToken has moved to first token.
     * @return 
     */
    public Node parse() {
        return term();
    }

    public void match(TokenType expectedType) {
        if (token.getType().equals(expectedType)) {
            nextToken();
        } else {
            throw new IllegalStateException("Illegal token " + token);
        }
    }

    private Node factor() {
        Node factorNode;
        switch(token.getType()) {
            case START_PAREN:
                match(START_PAREN);
                factorNode = factor();
                match(END_PAREN);
                break;
            case NUMBER:
                factorNode = new Node(ExpressionKind.ConstantExpression, token);
                nextToken();
                break;
            default:
                throw new IllegalStateException("Not a factor" + token);
        }
        return factorNode;
    }

    private Node exp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Node term() {
        Node firstFactor = factor();
        if (token == null) {
            return firstFactor;
        } else {
            switch(token.getType()) {
                case MULTIPLICATION:
                    match(MULTIPLICATION);
                    Node parentNode = new Node(ExpressionKind.OperatorExpression, token);
                    parentNode.setChild(0, firstFactor);
                    parentNode.setChild(1, term());
                    return parentNode;
                default:
                throw new IllegalStateException("Not a factor" + token);
            }
        }
    }
    

    
}
