package net.saga.lang.tiny.parser;

import java.util.Iterator;
import java.util.List;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.scanner.TokenType;
import static net.saga.lang.tiny.scanner.TokenType.ADDITION;
import static net.saga.lang.tiny.scanner.TokenType.END_PAREN;
import static net.saga.lang.tiny.scanner.TokenType.EQ;
import static net.saga.lang.tiny.scanner.TokenType.LT;
import static net.saga.lang.tiny.scanner.TokenType.MULTIPLICATION;
import static net.saga.lang.tiny.scanner.TokenType.NUMBER;
import static net.saga.lang.tiny.scanner.TokenType.SEMICOLON;
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

    public Node parseProgram(List<Token> tokens) {
        tokensIter = tokens.iterator();
        nextToken();
        return parse();
    }

    public Node parseStatement(List<Token> tokens) {
        tokensIter = tokens.iterator();
        nextToken();
        return statementSequence();
    }
    
    public Node parseExpression(List<Token> tokens) {
        tokensIter = tokens.iterator();
        nextToken();
        return expression();
    }
    
    
    /**
     * Called after setTokens and nextToken has moved to first token.
     * @return 
     */
    public Node parse() {
        return statementSequence();
    }

    public void match(TokenType expectedType) {
        if (token.getType().equals(expectedType)) {
            nextToken();
        } else {
            throw new IllegalStateException("Illegal token " + token);
        }
    }

    private Node statementSequence() {
        Node firstNode = statement();
        if (token == null || token.getType() != SEMICOLON) {
            return firstNode;
        }
        
        match(SEMICOLON);
        if (token != null)
            firstNode.setNext(statement());
        return firstNode;
        
    }
    
    private Node factor() {
        Node factorNode;
        switch(token.getType()) {
            case START_PAREN:
                match(START_PAREN);
                factorNode = simpleExpression();
                match(END_PAREN);
                break;
            case NUMBER:
                factorNode = new Node(ExpressionKind.ConstantExpression, token);
                nextToken();
                break;
            case IDENTIFIER:
                factorNode = new Node(ExpressionKind.IdentifierExpression, token);
                nextToken();
                break;
            default:
                throw new IllegalStateException("Not a factor" + token);
        }
        return factorNode;
    }

    private Node expression() {
        Node firstExpression = simpleExpression();
        if (token == null) {
            return firstExpression;
        }
        Node parentNode;
        Token lhsToken = token;
        switch(token.getType()) {
            case LT: 
                match(LT);
                break;
            case EQ: 
                match(EQ);
                break;
            default:
                return firstExpression;    
                        
        }
        parentNode = new Node(ExpressionKind.OperatorExpression, lhsToken);
        parentNode.setChild(0, firstExpression);
        parentNode.setChild(1, simpleExpression());
        return parentNode;
        
    }
    
    private Node simpleExpression() {
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

    private Node statement() {
        
        switch (token.getType()) {
            case IDENTIFIER:
                Node assignmentNode = new Node(StatementKind.ASSIGN, token);
                match(TokenType.IDENTIFIER);
                match(TokenType.ASSIGNMENT);
                assignmentNode.setChild(0, expression());
                return assignmentNode;
            case WRITE:
                Node writeNode = new Node(StatementKind.WRITE, token);
                match(TokenType.WRITE);
                writeNode.setChild(0, expression());
                return writeNode;
            
            case READ:
                
                match(TokenType.READ);
                Token readName  = token;
                match(TokenType.IDENTIFIER);
                Node readNode = new Node(StatementKind.READ, readName);
                return readNode;
            case REPEAT:
                Node repeatNode = new Node(StatementKind.REPEAT, token);
                match(TokenType.REPEAT);
                repeatNode.setChild(0, statementSequence());
                match(TokenType.UNTIL);
                repeatNode.setChild(1, expression());
                return repeatNode;                
            case IF:
                Node ifNode = new Node(StatementKind.IF, token);
                match(TokenType.IF);
                ifNode.setChild(0, expression());
                match(TokenType.THEN);
                ifNode.setChild(1, statementSequence());
                switch(token.getType()){
                    case ELSE:
                        match(TokenType.ELSE);
                        ifNode.setChild(2, statementSequence());
                        match(TokenType.END);
                        break;
                    case END:
                        match(TokenType.END);
                        break;
                    default:
                        throw new IllegalStateException(" Expecting if or end " + token);
                }
                
                return ifNode;                  
                
            default:
                        throw new IllegalStateException("unsupported statement token:" + token);
        }
    }
    

    
}
