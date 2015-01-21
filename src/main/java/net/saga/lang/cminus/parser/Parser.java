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
package net.saga.lang.cminus.parser;

import java.util.Iterator;
import java.util.List;
import net.saga.lang.cminus.scanner.Token;
import net.saga.lang.cminus.scanner.TokenType;


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
        return statementList();
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
        throw new IllegalStateException("Not yet implemented");
    }

    public void match(TokenType expectedType) {
        if (token.getType().equals(expectedType)) {
            nextToken();
        } else {
            throw new IllegalStateException("Illegal token " + token);
        }
    }

    private Node expression() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Node statementList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

    
}
