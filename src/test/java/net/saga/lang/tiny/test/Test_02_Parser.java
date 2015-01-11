
package net.saga.lang.tiny.test;

import static java.nio.CharBuffer.wrap;
import net.saga.lang.tiny.parser.ExpressionKind;
import net.saga.lang.tiny.parser.Node;
import net.saga.lang.tiny.parser.NodeKind;
import static net.saga.lang.tiny.parser.NodeKind.ExpressionNode;
import static net.saga.lang.tiny.parser.NodeKind.StatementNode;
import net.saga.lang.tiny.parser.Parser;
import net.saga.lang.tiny.scanner.Scanner;
import net.saga.lang.tiny.scanner.TokenType;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * The parse consumes a List of Tokens and produces the root node of a parse 
 * tree.
 */
public class Test_02_Parser {
   
    @Test 
    public void testParseConstant() {
    Node root = Parser.parse(new Scanner().scan(wrap("3")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, root.getExpressionKind());
        assertEquals(TokenType.NUMBER, root.getOperationAttribute());
        assertEquals(3, root.getValue());
        assertEquals(1, root.getLineNumber());
    }
    
    /**
     * In TINY there are two types of structures : statement nodes and expression
     * nodes.  We will begin with parsing expression nodes as statement nodes 
     * often include expression nodes as their children.
     */ 
    @Test
    public void testParseExpression() {
        Node root = Parser.parse(new Scanner().scan(wrap("3 + 4")));
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
    
}
