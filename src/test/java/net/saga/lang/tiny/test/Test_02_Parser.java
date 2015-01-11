
package net.saga.lang.tiny.test;

import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.tiny.parser.ExpressionKind;
import net.saga.lang.tiny.parser.Node;
import static net.saga.lang.tiny.parser.NodeKind.ExpressionNode;
import net.saga.lang.tiny.parser.Parser;
import net.saga.lang.tiny.scanner.Scanner;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.scanner.TokenType;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * The parse consumes a List of Tokens and produces the root node of a parse 
 * tree.
 */
public class Test_02_Parser {
   
    /**
     * This is a test of some internal methods of the Parser.
     * see pp 144
     */
    @Test
    public void testMatchMethod() {
        Parser parser = new Parser();
        List<Token> tokens = new Scanner().scan(wrap("(3)"));
        parser.setTokens(tokens);
        parser.nextToken();
        parser.match(TokenType.START_PAREN);
        parser.match(TokenType.NUMBER);
        parser.match(TokenType.END_PAREN);
    }
    
    /**
     * In TINY there are two types of structures : statement nodes and expression
     * nodes.  We will begin with parsing expression nodes because statement nodes 
     * often include expression nodes as their children.
     *
     * We are parsing the Grammar factor -> number
     */
    @Test 
    public void testParseConstant() {
    Node root = new Parser().parse(new Scanner().scan(wrap("3")));
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
    Node root = new Parser().parse(new Scanner().scan(wrap("(3)")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, root.getExpressionKind());
        assertEquals(TokenType.NUMBER, root.getOperationAttribute());
        assertEquals(3, root.getValue());
        assertEquals(1, root.getLineNumber());
    }
    
    
    /**
     * This test checks that our parser supports the grammar.
     * 
     * This is also the first time we are assembling a tree.
     * 
     * term -> factor mulop factor | factor
     * mulop -> *
     * factor -> ( factor ) | number
     */
    @Test
    public void testParseMultiplicationTerm() {
        Node root = new Parser().parse(new Scanner().scan(wrap("3 * 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.MULTIPLICATION, root.getOperationAttribute());
        
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
    
    /**
     * This test checks that our parser supports the grammar.
     * 
     * This is also the first time we are assembling a tree.
     * 
     * exp -> exp addop term | term
     * addop -> + | -
     * term -> term mulop factor | factor
     * mulop -> *
     * factor -> ( factor ) | number
     */
    @Test
    public void testParseAdditionExpression() {
        Node root = new Parser().parse(new Scanner().scan(wrap("3 + 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.ADDITION, root.getOperationAttribute());
        
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
    
    /**
     * If the previous test was implemented correctly, this should be implemented
     * as well.
     */
    @Test
    public void testParseComplexExpression() {
        Node root = new Parser().parse(new Scanner().scan(wrap("3 + 2 * 2")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.ADDITION, root.getOperationAttribute());
        
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
    
    
}
