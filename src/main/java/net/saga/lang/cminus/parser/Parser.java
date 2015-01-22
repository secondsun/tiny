/**
 * Copyright (C) 2015 Summers Pittman (secondsun@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.saga.lang.cminus.parser;

import java.util.Iterator;
import java.util.List;
import me.qmx.jitescript.internal.org.objectweb.asm.Opcodes;
import static net.saga.lang.cminus.parser.ExpressionKind.CallExpression;
import net.saga.lang.cminus.scanner.Token;
import net.saga.lang.cminus.scanner.TokenType;
import static net.saga.lang.cminus.parser.ExpressionKind.ConstantExpression;
import static net.saga.lang.cminus.parser.ExpressionKind.IdentifierExpression;
import static net.saga.lang.cminus.parser.ExpressionKind.OperatorExpression;
import static net.saga.lang.cminus.parser.Parser.FactorToken.ID;
import static net.saga.lang.cminus.scanner.TokenType.*;

public class Parser {

    private static final TokenType[] RELOP = {LTE, LT, GT, GTE, EQ, NE};

    private static final TokenType[] STATEMENT = {SEMI_COLON, IDENTIFIER, LPAREN, NUMBER,//Expression
        L_BRACE,//Compound
        IF,//selection
        WHILE, //iteration
        RETURN//return
    };

   


    public enum FactorToken {

        L_PAREN, NUMBER, ID;

        public static FactorToken fromToken(Token token) {
            switch (token.getType()) {
                case LPAREN:
                    return L_PAREN;
                case NUMBER:
                    return NUMBER;
                case IDENTIFIER:
                    return ID;
                default:
                    throw new IllegalStateException("Expecting a factor token, found " + token);
            }
        }

    }

    public enum StatementToken {

        SEMI_COLON, IDENTIFIER, LPAREN, NUMBER, L_BRACE, IF, WHILE, RETURN;

        public static StatementToken fromToken(Token token) {
            switch (token.getType()) {
                case SEMI_COLON:
                    return SEMI_COLON;
                case IDENTIFIER:
                    return IDENTIFIER;
                case LPAREN:
                    return LPAREN;
                case NUMBER:
                    return NUMBER;
                case L_BRACE:
                    return L_BRACE;
                case IF:
                    return IF;
                case WHILE:
                    return WHILE;
                case RETURN:
                    return RETURN;
                default:
                    throw new IllegalStateException("Expecting a factor token, found " + token);
            }
        }
    }

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
        return statementList();
    }

    public Node parseExpression(List<Token> tokens) {
        tokensIter = tokens.iterator();
        nextToken();
        return expression();
    }

    public Node parseDeclaration(List<Token> tokens) {
        tokensIter = tokens.iterator();
        nextToken();
        return declarationList();
    }

    /**
     * Called after setTokens and nextToken has moved to first token.
     *
     * @return
     */
    public Node parse() {
        return declarationList();
    }

    public void match(TokenType expectedType) {
        if (token.getType().equals(expectedType)) {
            nextToken();
        } else {
            throw new IllegalStateException("Illegal token " + token);
        }
    }

    private Node declaration() {
        Node declNode;
        TypeSpecifier spec;
        switch (token.getType()) {
            case INT:
            case VOID:
                TokenType type = token.getType();
                match(type);
                Token nameToken = token;
                match(IDENTIFIER);
                switch (token.getType()) {
                    case LPAREN:
                        match(LPAREN);
                        declNode = new Node(DeclarationKind.FUNCTION, nameToken);
                        declNode.setTypeSpecifier(type == VOID ? TypeSpecifier.VOID : TypeSpecifier.INT);
                        declNode.setChild(0, params());
                        match(RPAREN);
                        declNode.setChild(1, compoundStatement());
                        break;
                    case L_BRACKET:
                        if (type == VOID) {
                            throw new IllegalStateException("Void array not supported");
                        }
                        match(L_BRACKET);

                        declNode = new Node(DeclarationKind.VARIABLE, nameToken);
                        declNode.setTypeSpecifier(TypeSpecifier.INT_ARRAY);
                        declNode.setSize(token.getValue());
                        match(NUMBER);
                        match(R_BRACKET);
                        match(SEMI_COLON);
                        break;
                    case SEMI_COLON:
                        declNode = new Node(DeclarationKind.VARIABLE, nameToken);
                        declNode.setTypeSpecifier(type == VOID ? TypeSpecifier.VOID : TypeSpecifier.INT);
                        match(SEMI_COLON);
                        break;
                    //done
                    default:
                        throw new IllegalStateException("Expected [, ;, or ( but was " + token);
                }
                break;
            default:
                throw new IllegalStateException("Expected int or void but was " + token);
        }
        return declNode;
    }

    
    private Node params() {
        if (isToken(VOID)) {
            Node voidParams = new Node(DeclarationKind.PARAMS, token);
            voidParams.setTypeSpecifier(TypeSpecifier.VOID);
            match(VOID);
            return voidParams;
        } else {
            Node paramNode = param();
            Node head = paramNode;
            while (isToken(COMMA)) {
                match(COMMA);
                paramNode.setNext(param());
                paramNode = paramNode.getNext();
            }
            return head;
        }
    }

    private Node param() {
        match(INT);
        Node paramNode = new Node(DeclarationKind.PARAMS, token);
        paramNode.setTypeSpecifier(TypeSpecifier.INT);
        match(IDENTIFIER);
        if (isToken(L_BRACKET)) {
            match(L_BRACKET);
            match(R_BRACKET);
            paramNode.setTypeSpecifier(TypeSpecifier.INT_ARRAY);
        }
        return paramNode;
    }
    
    private Node declarationList() {
        Node declList = declaration();
        Node currentNode = declList;
        while (token != null) {
            currentNode.setNext(declaration());
            currentNode = currentNode.getNext();
        }
        return declList;
    }

    private Node expression() {
        Node expressionNode = simpleExpression();
        if (isToken(ASSIGN)) {
            Node parentNode = new Node(ExpressionKind.AssignmentExpression, token);
            match(token.getType());
            parentNode.setChild(0, expressionNode);
            parentNode.setChild(1, expression());
            expressionNode = parentNode;
        }
        return expressionNode;
    }

    private Node statementList() {
        Node statementNode = statement();
        Node listNode = statementNode;
        while (isToken(STATEMENT)) {
            statementNode.setNext(statement());
            statementNode = statementNode.getNext();
        }

        return listNode;

    }

    private Node statement() {
        Node statementNode = null;
        switch (StatementToken.fromToken(token)) {
            case SEMI_COLON:
                statementNode = new Node(StatementKind.EMPTY, token);
                match(SEMI_COLON);
                break;
            case IDENTIFIER:
            case LPAREN:
            case NUMBER:
                statementNode = expressionStatement();
                break;
            case L_BRACE:
                statementNode = compoundStatement();
                break;
            case IF:
                statementNode = ifStatement();
                break;
            case WHILE:
                statementNode = whileStatement();
                break;
            case RETURN:
                statementNode = returnStatement();
                break;
            default:
                throw new AssertionError(StatementToken.fromToken(token).name());
        }
        return statementNode;
    }

    public Node compoundStatement() {
        match(L_BRACE);
        Node compoundStatementList = new Node(StatementKind.COMPOUND, token);
        Node localDeclarationsNode = localDeclarations();
        if (localDeclarationsNode != null) {
            compoundStatementList.setChild(0, localDeclarationsNode);
            compoundStatementList.setChild(1, statementList());
        } else {
            compoundStatementList.setChild(0, statementList());
        }
        match(R_BRACE);
        return compoundStatementList;
    }

     private Node localDeclarations() {
        Node localHead = null;
        Node currentDec = null;

        while (isToken(INT, VOID)) {
            if (currentDec == null) {
            currentDec = declaration();
            } else {
                currentDec.setNext(declaration());
                currentDec = currentDec.getNext();
            }
            if (!currentDec.getDeclarationKind().equals(DeclarationKind.VARIABLE)) {
                throw new IllegalStateException("Expectign a variable declaration@" + currentDec.getLineNumber());
            }
            
            if (localHead == null) {
                localHead = currentDec;
                
            }
            
            
            
        }
        return localHead;
    }

    
    private Node returnStatement() {
        Node returnStatement = new Node(StatementKind.RETURN, token);
        match(RETURN);

        if (!isToken(SEMI_COLON)) {
            returnStatement.setChild(0, expression());
        }

        match(SEMI_COLON);

        return returnStatement;
    }

    private Node whileStatement() {
        Node whileNode = new Node(StatementKind.WHILE, token);
        match(WHILE);

        whileNode.setChild(0, expression());
        whileNode.setChild(1, statement());

        return whileNode;

    }

    private Node ifStatement() {
        Node ifStatementNode = new Node(StatementKind.IF, token);
        match(IF);
        ifStatementNode.setChild(0, expression());
        ifStatementNode.setChild(1, statement());
        if (isToken(ELSE)) {
            match(ELSE);
            ifStatementNode.setChild(2, statement());
        }
        return ifStatementNode;
    }

    private Node expressionStatement() {
        Node expressionNode = expression();
        match(SEMI_COLON);
        return expressionNode;
    }

    private Node simpleExpression() {
        Node simpleExpressionNode = additiveExpression();
        if (isToken(RELOP)) {
            Node parentNode = new Node(OperatorExpression, token);
            match(token.getType());
            parentNode.setChild(0, simpleExpressionNode);
            parentNode.setChild(1, additiveExpression());
            simpleExpressionNode = parentNode;
        }
        return simpleExpressionNode;
    }

    private Node additiveExpression() {
        Node termNode = term();
        while (isToken(PLUS, MINUS)) {
            Node parentNode = new Node(OperatorExpression, token);
            match(token.getType());
            parentNode.setChild(0, termNode);
            parentNode.setChild(1, term());
            termNode = parentNode;
        }
        return termNode;
    }

    private Node term() {
        Node termNode = factor();
        while (isToken(MULTIPLY, DIVIDE)) {
            Node parentNode = new Node(OperatorExpression, token);
            match(token.getType());
            parentNode.setChild(0, termNode);
            parentNode.setChild(1, factor());
            termNode = parentNode;
        }
        return termNode;
    }

    private Node factor() {
        Node factorNode;
        switch (FactorToken.fromToken(token)) {
            case L_PAREN:
                match(TokenType.LPAREN);
                factorNode = expression();
                match(TokenType.RPAREN);
                break;
            case NUMBER:
                factorNode = new Node(ConstantExpression, token);
                match(NUMBER);
                break;
            case ID:
                Token idToken = token;
                factorNode = new Node(IdentifierExpression, idToken);
                match(IDENTIFIER);
                if (isToken(L_BRACKET)) {
                    match(L_BRACKET);
                    factorNode.setChild(0, expression());
                    match(R_BRACKET);
                } else if (isToken(LPAREN)) {
                    factorNode = new Node(CallExpression, idToken);
                    match(LPAREN);
                    if (!isToken(RPAREN)) {
                        factorNode.setChild(0, argList());
                    }
                    match(RPAREN);
                }
                break;
            default:
                throw new AssertionError(FactorToken.fromToken(token).name());
        }
        return factorNode;
    }

    
    private Node argList() {
        Node headNode = expression();
        Node expressioNode = headNode;
        while(isToken(COMMA)){
            match(COMMA);
            expressioNode.setNext(expression());
            expressioNode = expressioNode.getNext();
        }
        return headNode;
    }
    
    private boolean isToken(TokenType... tokenType) {
        if (token == null) {
            return false;
        }
        for (TokenType type : tokenType) {
            if (token.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

}
