package net.saga.lang.tiny.parser;

import java.util.Iterator;
import java.util.List;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.scanner.TokenType;
import static net.saga.lang.tiny.scanner.TokenType.ADDITION;
import static net.saga.lang.tiny.scanner.TokenType.END_PAREN;
import static net.saga.lang.tiny.scanner.TokenType.MULTIPLICATION;
import static net.saga.lang.tiny.scanner.TokenType.NUMBER;
import static net.saga.lang.tiny.scanner.TokenType.START_PAREN;
import static net.saga.lang.tiny.scanner.TokenType.SUBTRACTION;

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
        return expression();
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
                factorNode = expression();
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

    private Node expression() {
        Node firstTerm = term();
        if (token == null) {
            return firstTerm;
        }
        switch(token.getType()) {
            case ADDITION:{
                Token addToken = token;
                    match(ADDITION);
                    Node parentNode = new Node(ExpressionKind.OperatorExpression, addToken);
                    parentNode.setChild(0, firstTerm);
                    parentNode.setChild(1, term());
                    return parentNode;
            }
            case SUBTRACTION: {
                    Token subToken = token;
                    match(SUBTRACTION);
                    Node parentNode = new Node(ExpressionKind.OperatorExpression, subToken);
                    parentNode.setChild(0, firstTerm);
                    parentNode.setChild(1, term());
                    return parentNode;
            }
            default:
                return firstTerm;
        }
    }

    private Node term() {
        Node firstFactor = factor();
        if (token == null) {
            return firstFactor;
        } else {
            switch(token.getType()) {
                case MULTIPLICATION:
                    Token multToken = token;
                    match(MULTIPLICATION);
                    Node parentNode = new Node(ExpressionKind.OperatorExpression, multToken);
                    parentNode.setChild(0, firstFactor);
                    parentNode.setChild(1, factor());
                    return parentNode;
                default:
                    return firstFactor;
            }
        }
    }
    

    
}
