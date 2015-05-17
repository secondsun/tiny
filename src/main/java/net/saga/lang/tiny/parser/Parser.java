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
            token = tokensIter.next();
            while (token.getType() == TokenType.COMMENT) {
                token = tokensIter.next();
            }
            return token;
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
        if (token == null) {
            return firstNode;
        }
        
        Node node = firstNode;
        
        while (token != null && (token.getType() != TokenType.ELSE) && token.getType() != TokenType.UNTIL && token.getType() != TokenType.END){
            
            match(SEMICOLON);
            if (token != null) {
                node.setNext(statement());
                node = node.getNext();
            }
        } 
        
        return firstNode;
        
    }
    
    private Node factor() {
        Node factorNode;
        switch(token.getType()) {
            case NUMBER:
                factorNode = new Node(ExpressionKind.ConstantExpression, token);
                nextToken();
                break;
            case START_PAREN:
                match(START_PAREN);
                factorNode = expression();
                match(END_PAREN);
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
        Node parentNode = firstExpression;
        if (token.getType() == LT || token.getType() == EQ) {
            parentNode = new Node(ExpressionKind.OperatorExpression, token);
            parentNode.setChild(0, firstExpression);
            firstExpression = parentNode;
            match(token.getType());
            firstExpression.setChild(1, simpleExpression());
        }
        
        
        
        return firstExpression;
        
    }
    
    private Node simpleExpression() {
        Node firstTerm = term();
        if (token == null) {
            return firstTerm;
        }
        Node parentNode = firstTerm;
        
        while (token != null && (token.getType() == ADDITION || token.getType() == SUBTRACTION)) {
            Token addToken = token;
                    match(token.getType());
                    parentNode = new Node(ExpressionKind.OperatorExpression, addToken);
                    parentNode.setChild(0, firstTerm);
                    parentNode.setChild(1, term());
                    firstTerm = parentNode;
        }
        
        return parentNode;
    }

    private Node term() {
        Node firstFactor = factor();
        if (token == null) {
            return firstFactor;
        } else {
            Node parentNode = null;
            while (token != null && (token.getType() == MULTIPLICATION || token.getType() == TokenType.INT_DIVISION)) {
                    Token opToken = token;
                    parentNode = new Node(ExpressionKind.OperatorExpression, opToken);
                    parentNode.setChild(0, firstFactor);
                    match(token.getType());
                    parentNode.setChild(1, factor());
                    firstFactor = parentNode;
                    
            }
            return firstFactor;
        
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
