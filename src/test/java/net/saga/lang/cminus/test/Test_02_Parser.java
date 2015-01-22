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
package net.saga.lang.cminus.test;


import java.io.IOException;
import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.cminus.parser.DeclarationKind;
import net.saga.lang.cminus.parser.ExpressionKind;
import net.saga.lang.cminus.parser.Node;
import net.saga.lang.cminus.parser.NodeKind;
import static net.saga.lang.cminus.parser.NodeKind.ExpressionNode;
import static net.saga.lang.cminus.parser.NodeKind.StatementNode;
import net.saga.lang.cminus.parser.Parser;
import net.saga.lang.cminus.parser.StatementKind;
import net.saga.lang.cminus.parser.TypeSpecifier;
import net.saga.lang.cminus.scanner.Scanner;
import net.saga.lang.cminus.scanner.Token;
import net.saga.lang.cminus.scanner.TokenType;
import static net.saga.lang.cminus.scanner.TokenType.PLUS;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * The parse consumes a List of Tokens and produces the root node of a parse
 * tree.
 */
public class Test_02_Parser {

    /**
     * This is a test of some internal methods of the Parser. see pp 144
     */
    @Test
    public void testMatchMethod() {
        Parser parser = new Parser();
        List<Token> tokens = new Scanner().scan(wrap("(3)"));
        parser.setTokens(tokens);
        parser.nextToken();
        parser.match(TokenType.LPAREN);
        parser.match(TokenType.NUMBER);
        parser.match(TokenType.RPAREN);
    }

    /**
     * In TINY there are two types of structures : statement nodes and
     * expression nodes. We will begin with parsing expression nodes because
     * statement nodes often include expression nodes as their children.
     *
     * We are parsing the Grammar factor -> number
     */
    @Test
    public void testParseConstant() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, root.getExpressionKind());
        assertEquals(TokenType.NUMBER, root.getOperationAttribute());
        assertEquals(3, root.getValue());
        assertEquals(1, root.getLineNumber());
    }

    /**
     * We are parsing the Grammar factor -> ( factor ) | number
     */
    @Test
    public void testParseFactor() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("(3)")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, root.getExpressionKind());
        assertEquals(TokenType.NUMBER, root.getOperationAttribute());
        assertEquals(3, root.getValue());
        assertEquals(1, root.getLineNumber());
    }

    @Test
    public void parseIdentifier() {

        Node root = new Parser().parseExpression(new Scanner().scan(wrap("x")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.IdentifierExpression, root.getExpressionKind());
        assertEquals("x", root.getName());

        root = new Parser().parseExpression(new Scanner().scan(wrap("x[4]")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.IdentifierExpression, root.getExpressionKind());
        assertEquals("x", root.getName());

        
        Node expression = root.getChild(0);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(4, expression.getValue());

    }
    
    /**
     * This test checks that our parser supports the grammar.
     *
     * This is also the first time we are assembling a tree.
     *
     * term -> factor mulop factor | factor mulop -> * factor -> ( factor ) |
     * number
     */
    @Test
    public void testParseMultiplicationTerm() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3 * 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.MULTIPLY, root.getOperationAttribute());

        Node multiplicand = root.getChild(0);
        Node multiplier = root.getChild(1);

        assertEquals(ExpressionNode, multiplicand.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, multiplicand.getExpressionKind());
        assertEquals(TokenType.NUMBER, multiplicand.getOperationAttribute());
        assertEquals(3, multiplicand.getValue());

        assertEquals(ExpressionNode, multiplier.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, multiplier.getExpressionKind());
        assertEquals(TokenType.NUMBER, multiplier.getOperationAttribute());
        assertEquals(4, multiplier.getValue());

    }
    
    @Test
    public void testParseMultiplicationTermMultipleTerms() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3 * 4 * 5")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.MULTIPLY, root.getOperationAttribute());

        Node multiplicand = root.getChild(0);
        Node multiplier = root.getChild(1);

        assertEquals(ExpressionNode, multiplicand.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, multiplicand.getExpressionKind());
        assertEquals(TokenType.MULTIPLY, multiplicand.getOperationAttribute());
        assertEquals(3, multiplicand.getChild(0).getValue());
        assertEquals(4, multiplicand.getChild(1).getValue());

        assertEquals(ExpressionNode, multiplier.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, multiplier.getExpressionKind());
        assertEquals(TokenType.NUMBER, multiplier.getOperationAttribute());
        assertEquals(5, multiplier.getValue());

    }

    /**
     * This test checks that our parser supports the grammar.
     *
     * This is also the first time we are assembling a tree.
     *
     * exp -> exp addop term | term addop -> + | - term -> term mulop factor |
     * factor mulop -> * factor -> ( factor ) | number
     */
    @Test
    public void testParseAdditionExpression() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3 + 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.PLUS, root.getOperationAttribute());

        Node augend = root.getChild(0);
        Node addend = root.getChild(1);

        assertEquals(ExpressionNode, augend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, augend.getExpressionKind());
        assertEquals(TokenType.NUMBER, augend.getOperationAttribute());
        assertEquals(3, augend.getValue());

        assertEquals(ExpressionNode, addend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, addend.getExpressionKind());
        assertEquals(TokenType.NUMBER, addend.getOperationAttribute());
        assertEquals(4, addend.getValue());

    }

    @Test
    public void testParseMultipleAdditionExpression() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("2 + 3 + 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.PLUS, root.getOperationAttribute());

        Node augend = root.getChild(0);
        Node addend = root.getChild(1);

        assertEquals(ExpressionNode, augend.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, augend.getExpressionKind());
        assertEquals(TokenType.PLUS, augend.getOperationAttribute());
        assertEquals(3, augend.getChild(1).getValue());
        assertEquals(2, augend.getChild(0).getValue());

        assertEquals(ExpressionNode, addend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, addend.getExpressionKind());
        assertEquals(TokenType.NUMBER, addend.getOperationAttribute());
        assertEquals(4, addend.getValue());

    }
    
    /**
     * If the previous test was implemented correctly, this should be
     * implemented as well.
     */
    @Test
    public void testParseComplexExpression() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3 + 2 * 2")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.PLUS, root.getOperationAttribute());

        Node augend = root.getChild(0);
        Node addend = root.getChild(1);

        assertEquals(ExpressionNode, augend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, augend.getExpressionKind());
        assertEquals(TokenType.NUMBER, augend.getOperationAttribute());
        assertEquals(3, augend.getValue());

        assertEquals(ExpressionNode, addend.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, addend.getExpressionKind());

        Node multiplicand = addend.getChild(0);
        Node multiplier = addend.getChild(1);

        assertEquals(ExpressionNode, multiplicand.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, multiplicand.getExpressionKind());
        assertEquals(TokenType.NUMBER, multiplicand.getOperationAttribute());
        assertEquals(2, multiplicand.getValue());

        assertEquals(ExpressionNode, multiplier.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, multiplier.getExpressionKind());
        assertEquals(TokenType.NUMBER, multiplier.getOperationAttribute());
        assertEquals(2, multiplier.getValue());

    }

    /**
     *
     * Now we can / should begin testing comparison expressions.
     *
     * We need to nor push down the old expression into a new node type, simple
     * expression. We will also create a comparison operation node type.
     *
     * This will expand our grammar to
     *
     * exp -> simple-exp comparison-op simple-exp | simple-exp comparison-op ->
     * &lt; | = simple-exp -> simple-exp addop term | term addop -> + | - term
     * -> term mulop factor | factor mulop -> * factor -> ( factor ) | number
     *
     */
    @Test
    public void parseComparisonLT() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3 < 2")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.LT, root.getOperationAttribute());

        Node lhs = root.getChild(0);
        Node rhs = root.getChild(1);

        assertEquals(ExpressionNode, lhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, lhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, lhs.getOperationAttribute());
        assertEquals(3, lhs.getValue());

        assertEquals(ExpressionNode, rhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, rhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, rhs.getOperationAttribute());
        assertEquals(2, rhs.getValue());

    }

    @Test
    public void parseComparisonEQ() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("4 == 16")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.EQ, root.getOperationAttribute());

        Node lhs = root.getChild(0);
        Node rhs = root.getChild(1);

        assertEquals(ExpressionNode, lhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, lhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, lhs.getOperationAttribute());
        assertEquals(4, lhs.getValue());

        assertEquals(ExpressionNode, rhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, rhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, rhs.getOperationAttribute());
        assertEquals(16, rhs.getValue());
    }

    @Test
    public void parseComparisonAllTheRest() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3 <= 2")));
        
        assertEquals(TokenType.LTE, root.getOperationAttribute());
        
        root = new Parser().parseExpression(new Scanner().scan(wrap("3 > 2")));
        
        assertEquals(TokenType.GT, root.getOperationAttribute());
        
        root = new Parser().parseExpression(new Scanner().scan(wrap("3 >= 2")));
        
        assertEquals(TokenType.GTE, root.getOperationAttribute());
        
        root = new Parser().parseExpression(new Scanner().scan(wrap("3 != 2")));
        assertEquals(TokenType.NE, root.getOperationAttribute());
        
    }

    
    
    @Test
    public void parseComplexComparison() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("4 == (3 + 1)")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.EQ, root.getOperationAttribute());

        Node lhs = root.getChild(0);
        Node rhs = root.getChild(1);

        assertEquals(ExpressionNode, lhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, lhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, lhs.getOperationAttribute());
        assertEquals(4, lhs.getValue());

        assertEquals(ExpressionNode, rhs.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, rhs.getExpressionKind());
        assertEquals(TokenType.PLUS, rhs.getOperationAttribute());

        Node augend = rhs.getChild(0);
        Node addend = rhs.getChild(1);

        assertEquals(ExpressionNode, augend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, augend.getExpressionKind());
        assertEquals(TokenType.NUMBER, augend.getOperationAttribute());
        assertEquals(3, augend.getValue());

        assertEquals(ExpressionNode, addend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, addend.getExpressionKind());
        assertEquals(TokenType.NUMBER, addend.getOperationAttribute());
        assertEquals(1, addend.getValue());

    }

    /**
     * Now we can incorporate statements. For this we will change our grammar.
     *
     * Let's begin with ASSIGNMENT.
     *
     * The grammar should now be
     *
     * stmt-sequence -> stmt-sequence; statement | statement statement ->
     * assign-stmt assign-stmt -> identifier | := exp exp -> simple-exp
     * comparison-op simple-exp | simple-exp comparison-op -> &lt; | =
     * simple-exp -> simple-exp addop term | term addop -> + | - term -> term
     * mulop factor | factor mulop -> * factor -> ( factor ) | number |
     * identifier
     *
     */
    @Test
    public void parseAssignment() {

        Node root = new Parser().parseExpression(new Scanner().scan(wrap("x = 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, root.getExpressionKind());
        assertEquals("x", root.getChild(0).getName());

        Node expression = root.getChild(1);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(4, expression.getValue());

        
        root = new Parser().parseExpression(new Scanner().scan(wrap("x[5] = 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, root.getExpressionKind());
        assertEquals("x", root.getChild(0).getName());

        Node index = root.getChild(0).getChild(0);
        assertEquals(ExpressionNode, index.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, index.getExpressionKind());
        assertEquals(TokenType.NUMBER, index.getOperationAttribute());
        assertEquals(5, index.getValue());
        
         expression = root.getChild(1);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(4, expression.getValue());
        
    }


    @Test
    public void parseCall() throws IOException {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("x()")));
        assertEquals(ExpressionKind.CallExpression, root.getExpressionKind());
        assertNull(root.getChild(0));//noParams
        
        root = new Parser().parseExpression(new Scanner().scan(wrap("x(foo, bar, 3+1)")));
        assertEquals(ExpressionKind.CallExpression, root.getExpressionKind());
        Node params = root.getChild(0);
        assertEquals(ExpressionKind.IdentifierExpression, params.getExpressionKind());
        assertEquals("foo", params.getName());
        
        params = params.getNext();
        assertEquals(ExpressionKind.IdentifierExpression, params.getExpressionKind());
        assertEquals("bar", params.getName());
        
        params = params.getNext();
        assertEquals(ExpressionKind.OperatorExpression, params.getExpressionKind());
        assertEquals(TokenType.PLUS, params.getOperationAttribute());
        
    }    
    
    @Test
    public void parseComplexAssignment() {

        Node root = new Parser().parseExpression(new Scanner().scan(wrap("x = (3 + 4)")));
        
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, root.getExpressionKind());
        assertEquals("x", root.getChild(0).getName());

        Node expression = root.getChild(1);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, expression.getExpressionKind());
        assertEquals(TokenType.PLUS, expression.getOperationAttribute());

        Node augend = expression.getChild(0);
        Node addend = expression.getChild(1);

        assertEquals(ExpressionNode, augend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, augend.getExpressionKind());
        assertEquals(TokenType.NUMBER, augend.getOperationAttribute());
        assertEquals(3, augend.getValue());

        assertEquals(ExpressionNode, addend.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, addend.getExpressionKind());
        assertEquals(TokenType.NUMBER, addend.getOperationAttribute());
        assertEquals(4, addend.getValue());

    }

    @Test
    public void parseEmptyStatement() {    
        Node root = new Parser().parseStatement(new Scanner().scan(wrap(";")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.EMPTY, root.getStatementKind());
        
        root = new Parser().parseStatement(new Scanner().scan(wrap(";;;")));
        
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.EMPTY, root.getStatementKind());
        
        root = root.getNext();
        
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.EMPTY, root.getStatementKind());
        
        root = root.getNext();
        
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.EMPTY, root.getStatementKind());
        
        root = root.getNext();
        
        assertNull(root);
        
        
    }
    
    
    @Test
    public void parseMultipleAssignment() {

        Node root = new Parser().parseStatement(new Scanner().scan(wrap("x = 4;\n"
                + "y=7;")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, root.getExpressionKind());
        assertEquals("x", root.getChild(0).getName());

        Node expression = root.getChild(1);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(4, expression.getValue());

        root = root.getNext();
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, root.getExpressionKind());
        assertEquals("y", root.getChild(0).getName());

        expression = root.getChild(1);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(7, expression.getValue());

    }

    @Test
    public void parseCompoundStatement() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("{42;18;33;}")));
        assertEquals(StatementKind.COMPOUND, root.getStatementKind());
        
        assertEquals(42, root.getChild(0).getValue());//We know that Constant Expressions already work so skip to value
        assertEquals(18, root.getChild(0).getNext().getValue());
        assertEquals(33, root.getChild(0).getNext().getNext().getValue());
        
        //Test with declarations
         root = new Parser().parseStatement(new Scanner().scan(wrap("{int x; int y; int z[10];42;18;33;}")));
        assertEquals(StatementKind.COMPOUND, root.getStatementKind());
        
        Node decNode = root.getChild(0);
        assertEquals("x", decNode.getName());
        assertEquals(DeclarationKind.VARIABLE, decNode.getDeclarationKind());
        assertEquals(TypeSpecifier.INT, decNode.getTypeSpecifier());
        
         decNode = decNode.getNext();
        assertEquals("y", decNode.getName());
        assertEquals(DeclarationKind.VARIABLE, decNode.getDeclarationKind());
        assertEquals(TypeSpecifier.INT, decNode.getTypeSpecifier());
        
         decNode = decNode.getNext();
        assertEquals("z", decNode.getName());
        assertEquals(DeclarationKind.VARIABLE, decNode.getDeclarationKind());
        assertEquals(TypeSpecifier.INT_ARRAY, decNode.getTypeSpecifier());
        
        assertEquals(42, root.getChild(1).getValue());//We know that Constant Expressions already work so skip to value
        assertEquals(18, root.getChild(1).getNext().getValue());
        assertEquals(33, root.getChild(1).getNext().getNext().getValue());
        

    }


    /**
     * Now we can incorporate statements. Let's begin with IF.
     *
     * The grammar should now be
     *
     * stmt-sequence -> stmt-sequence; statement | statement statement ->
     * assign-stmt | read-stmt | write-stmt | repeate-stmt | if stmt assign-stmt
     * -> identifier | := exp read-stmt -> read identifier write-stmt -> write
     * exp
     *
     * if-stmt > if exp then stmt-sequence end | if exp then stmt-sequence else
     * stmt-sequence end | exp -> simple-exp comparison-op simple-exp |
     * simple-exp comparison-op -> &lt; | = simple-exp -> simple-exp addop term
     * | term addop -> + | - term -> term mulop factor | factor mulop -> *
     * factor -> ( factor ) | number
     *
     */
    @Test
    public void parseIfExpression() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("if (3 < 4) x = 5;")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.IF, root.getStatementKind());

        Node testExpression = root.getChild(0);
        Node thenStatement = root.getChild(1);

        Node lhs = testExpression.getChild(0);
        Node rhs = testExpression.getChild(1);

        assertEquals(ExpressionNode, lhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, lhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, lhs.getOperationAttribute());
        assertEquals(3, lhs.getValue());

        assertEquals(ExpressionNode, rhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, rhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, rhs.getOperationAttribute());
        assertEquals(4, rhs.getValue());

        assertEquals(ExpressionNode, testExpression.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, testExpression.getExpressionKind());
        assertEquals(TokenType.LT, testExpression.getOperationAttribute());

        assertEquals(ExpressionNode, thenStatement.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, thenStatement.getExpressionKind());
        assertEquals("x", thenStatement.getChild(0).getName());

        Node assignmentValue = thenStatement.getChild(1);
        assertEquals(ExpressionNode, assignmentValue.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, assignmentValue.getExpressionKind());
        assertEquals(TokenType.NUMBER, assignmentValue.getOperationAttribute());
        assertEquals(5, assignmentValue.getValue());

    }

    
    
    @Test
    public void parseIfThenElse() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("if (3 < 4) x = 5; else x = 6; ")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.IF, root.getStatementKind());

        Node testExpression = root.getChild(0);
        Node elseStatement = root.getChild(2);

        Node lhs = testExpression.getChild(0);
        Node rhs = testExpression.getChild(1);

        assertEquals(ExpressionNode, lhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, lhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, lhs.getOperationAttribute());
        assertEquals(3, lhs.getValue());

        assertEquals(ExpressionNode, rhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, rhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, rhs.getOperationAttribute());
        assertEquals(4, rhs.getValue());

        assertEquals(ExpressionNode, testExpression.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, testExpression.getExpressionKind());
        assertEquals(TokenType.LT, testExpression.getOperationAttribute());

        assertEquals(ExpressionNode, elseStatement.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, elseStatement.getExpressionKind());
        assertEquals("x", elseStatement.getChild(0).getName());

        Node assignmentValue = elseStatement.getChild(1);
        assertEquals(ExpressionNode, assignmentValue.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, assignmentValue.getExpressionKind());
        assertEquals(TokenType.NUMBER, assignmentValue.getOperationAttribute());
        assertEquals(6, assignmentValue.getValue());

    }

    
    @Test
    public void parseIfThenElseCompountStatement() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("if (3 < 4) {x = 5;} else {x = 6;} ")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.IF, root.getStatementKind());

        Node thenStatement = root.getChild(1);
        Node elseStatement = root.getChild(2);

        assertEquals(StatementKind.COMPOUND, thenStatement.getStatementKind());
        assertNull(thenStatement.getChild(1));
        
        assertEquals(StatementKind.COMPOUND, elseStatement.getStatementKind());
        assertNull(elseStatement.getChild(1));
        
    }
    
    @Test
    public void testWhileStatement() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("while (3 < 4) x = 5;  ")));
        Node testExpression = root.getChild(0);
        Node bodyStatement = root.getChild(1);

        Node lhs = testExpression.getChild(0);
        Node rhs = testExpression.getChild(1);

        assertEquals(StatementKind.WHILE, root.getStatementKind());
        
        assertEquals(ExpressionNode, lhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, lhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, lhs.getOperationAttribute());
        assertEquals(3, lhs.getValue());

        assertEquals(ExpressionNode, rhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, rhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, rhs.getOperationAttribute());
        assertEquals(4, rhs.getValue());

        assertEquals(ExpressionNode, testExpression.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, testExpression.getExpressionKind());
        assertEquals(TokenType.LT, testExpression.getOperationAttribute());

        assertEquals(ExpressionNode, bodyStatement.getNodeKind());
        assertEquals(ExpressionKind.AssignmentExpression, bodyStatement.getExpressionKind());
        assertEquals("x", bodyStatement.getChild(0).getName());

        Node assignmentValue = bodyStatement.getChild(1);
        assertEquals(ExpressionNode, assignmentValue.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, assignmentValue.getExpressionKind());
        assertEquals(TokenType.NUMBER, assignmentValue.getOperationAttribute());
        assertEquals(5, assignmentValue.getValue());
        
    }
    
    @Test
    public void testParseReturnStatement() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("return;")));
        
        assertEquals(StatementKind.RETURN, root.getStatementKind());
        
        root = new Parser().parseStatement(new Scanner().scan(wrap("return x + 1;")));
        
        assertEquals(ExpressionKind.OperatorExpression, root.getChild(0).getExpressionKind());
        assertEquals(PLUS, root.getChild(0).getOperationAttribute());
    }
    
    @Test
    public void testParseVariableDeclaration() {
        Node root = new Parser().parseDeclaration(new Scanner().scan(wrap("int x;")));
        
        assertEquals(NodeKind.DeclarationNode, root.getNodeKind());
        assertEquals(DeclarationKind.VARIABLE, root.getDeclarationKind());
        assertEquals(TypeSpecifier.INT, root.getTypeSpecifier());
        assertEquals("x", root.getName());
        
        root = new Parser().parseDeclaration(new Scanner().scan(wrap("void x;")));
        assertEquals(NodeKind.DeclarationNode, root.getNodeKind());
        assertEquals(DeclarationKind.VARIABLE, root.getDeclarationKind());
        assertEquals(TypeSpecifier.VOID, root.getTypeSpecifier());
        assertEquals("x", root.getName());
        
        root = new Parser().parseDeclaration(new Scanner().scan(wrap("int x[14];")));
        assertEquals(NodeKind.DeclarationNode, root.getNodeKind());
        assertEquals(DeclarationKind.VARIABLE, root.getDeclarationKind());
        assertEquals(TypeSpecifier.INT_ARRAY, root.getTypeSpecifier());
        assertEquals("x", root.getName());
        assertEquals(14, root.getSize());
        
    }
    
    @Test
    public void testParseFunctionDeclaration() {
        Node root = new Parser().parseDeclaration(new Scanner().scan(wrap("int foo( void ) {return 0;}")));
        
        assertEquals(NodeKind.DeclarationNode, root.getNodeKind());
        assertEquals(DeclarationKind.FUNCTION, root.getDeclarationKind());
        assertEquals(TypeSpecifier.INT, root.getTypeSpecifier());
        assertEquals("foo", root.getName());
        
        Node params = root.getChild(0);
        Node body = root.getChild(1);
        
        assertEquals(DeclarationKind.PARAMS, params.getDeclarationKind());
        assertEquals(TypeSpecifier.VOID, params.getTypeSpecifier());
        
        
        assertEquals(StatementKind.COMPOUND, body.getStatementKind());
        
        root = new Parser().parseDeclaration(new Scanner().scan(wrap("int foo( int x, int y[] ) {return 0;}")));
        params = root.getChild(0);
        assertEquals(DeclarationKind.PARAMS, params.getDeclarationKind());
        assertEquals(TypeSpecifier.INT, params.getTypeSpecifier());
        assertEquals("x", params.getName());
        
        params = params.getNext();
        assertEquals(DeclarationKind.PARAMS, params.getDeclarationKind());
        assertEquals(TypeSpecifier.INT_ARRAY, params.getTypeSpecifier());
        assertEquals("y", params.getName());
        assertNull(params.getNext());
        
        
    }
    
    
    @Test
    public void parseProgram() throws IOException {
        String program = IOUtils.toString(Test_02_Parser.class.getClassLoader().getResourceAsStream("sample.cm"));
        List<Token> tokens = new Scanner().scan(wrap(program));
        Node node = new Parser().parseProgram(tokens);
        
        assertEquals(DeclarationKind.FUNCTION, node.getDeclarationKind());
        assertEquals("gcd", node.getName());
        
        node = node.getNext();
        assertEquals(DeclarationKind.FUNCTION, node.getDeclarationKind());
        assertEquals("main", node.getName());
        
        System.out.println(node);
        
        
    }

}
