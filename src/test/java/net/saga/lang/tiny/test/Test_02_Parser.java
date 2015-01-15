package net.saga.lang.tiny.test;

import java.io.IOException;
import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.tiny.parser.ExpressionKind;
import net.saga.lang.tiny.parser.Node;
import static net.saga.lang.tiny.parser.NodeKind.ExpressionNode;
import static net.saga.lang.tiny.parser.NodeKind.StatementNode;
import net.saga.lang.tiny.parser.Parser;
import net.saga.lang.tiny.parser.StatementKind;
import net.saga.lang.tiny.scanner.Scanner;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.scanner.TokenType;
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
        parser.match(TokenType.START_PAREN);
        parser.match(TokenType.NUMBER);
        parser.match(TokenType.END_PAREN);
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
    
    @Test
    public void testParseMultiplicationTermMultipleTerms() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("3 * 4 * 5")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.MULTIPLICATION, root.getOperationAttribute());

        Node multiplicand = root.getChild(0);
        Node multiplier = root.getChild(1);

        assertEquals(ExpressionNode, multiplicand.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, multiplicand.getExpressionKind());
        assertEquals(TokenType.MULTIPLICATION, multiplicand.getOperationAttribute());
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

    @Test
    public void testParseMultipleAdditionExpression() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("2 + 3 + 4")));
        assertEquals(ExpressionNode, root.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, root.getExpressionKind());
        assertEquals(TokenType.ADDITION, root.getOperationAttribute());

        Node augend = root.getChild(0);
        Node addend = root.getChild(1);

        assertEquals(ExpressionNode, augend.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, augend.getExpressionKind());
        assertEquals(TokenType.ADDITION, augend.getOperationAttribute());
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
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("4 = 16")));
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
    public void parseComplexCompatison() {
        Node root = new Parser().parseExpression(new Scanner().scan(wrap("4 = (3 + 1)")));
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
        assertEquals(TokenType.ADDITION, rhs.getOperationAttribute());

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

        Node root = new Parser().parseStatement(new Scanner().scan(wrap("x := 4")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.ASSIGN, root.getStatementKind());
        assertEquals("x", root.getName());

        Node expression = root.getChild(0);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(4, expression.getValue());

    }

    @Test
    public void parseComplexAssignment() {

        Node root = new Parser().parseStatement(new Scanner().scan(wrap("x := (3 + 4)")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.ASSIGN, root.getStatementKind());
        assertEquals("x", root.getName());

        Node expression = root.getChild(0);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, expression.getExpressionKind());
        assertEquals(TokenType.ADDITION, expression.getOperationAttribute());

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
    public void parseMultipleAssignment() {

        Node root = new Parser().parseStatement(new Scanner().scan(wrap("x := 4;\n"
                + "y:=7")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.ASSIGN, root.getStatementKind());
        assertEquals("x", root.getName());

        Node expression = root.getChild(0);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(4, expression.getValue());

        root = root.getNext();
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.ASSIGN, root.getStatementKind());
        assertEquals("y", root.getName());

        expression = root.getChild(0);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(7, expression.getValue());

    }

    /**
     *
     *
     * Let's add read and write statements.
     *
     * The grammar should now be
     *
     * stmt-sequence -> stmt-sequence; statement | statement statement ->
     * assign-stmt | read-stmt | write-stmt assign-stmt -> identifier | := exp
     * read-stmt -> read identifier write-stmt -> write exp exp -> simple-exp
     * comparison-op simple-exp | simple-exp comparison-op -> &lt; | =
     * simple-exp -> simple-exp addop term | term addop -> + | - term -> term
     * mulop factor | factor mulop -> * factor -> ( factor ) | number |
     * identifier
     *
     */
    @Test
    public void parseRead() {

        Node root = new Parser().parseStatement(new Scanner().scan(wrap("read x")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.READ, root.getStatementKind());
        assertEquals("x", root.getName());

    }

    @Test
    public void parseWrite() {

        Node root = new Parser().parseStatement(new Scanner().scan(wrap("write 4")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.WRITE, root.getStatementKind());

        Node expression = root.getChild(0);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, expression.getExpressionKind());
        assertEquals(TokenType.NUMBER, expression.getOperationAttribute());
        assertEquals(4, expression.getValue());

    }

    @Test
    public void parseWriteExpression() {

        Node root = new Parser().parseStatement(new Scanner().scan(wrap("write (4* 6)")));
        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.WRITE, root.getStatementKind());

        Node expression = root.getChild(0);
        assertEquals(ExpressionNode, expression.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, expression.getExpressionKind());
        assertEquals(TokenType.MULTIPLICATION, expression.getOperationAttribute());

    }

    /**
     *
     *
     * Let's add repeate statements
     *
     * The grammar should now be
     *
     * stmt-sequence -> stmt-sequence; statement | statement statement ->
     * assign-stmt | read-stmt | write-stmt | repeate-stmt assign-stmt ->
     * identifier | := exp read-stmt -> read identifier write-stmt -> write exp
     * exp -> simple-exp comparison-op simple-exp | simple-exp comparison-op ->
     * &lt; | = simple-exp -> simple-exp addop term | term addop -> + | - term
     * -> term mulop factor | factor mulop -> * factor -> ( factor ) | number |
     * identifier
     *
     */
    @Test
    public void parseRepeat() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("repeat \n"
                + "x:=1 \n"
                + "until x = 1;")));

        assertEquals(StatementNode, root.getNodeKind());
        assertEquals(StatementKind.REPEAT, root.getStatementKind());

        Node body = root.getChild(0);

        assertEquals(StatementNode, body.getNodeKind());
        assertEquals(StatementKind.ASSIGN, body.getStatementKind());
        assertEquals("x", body.getName());

        Node value = body.getChild(0);
        assertEquals(ExpressionNode, value.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, value.getExpressionKind());
        assertEquals(1, value.getValue());

        Node until = root.getChild(1);

        assertEquals(ExpressionNode, until.getNodeKind());
        assertEquals(ExpressionKind.OperatorExpression, until.getExpressionKind());
        assertEquals(TokenType.EQ, until.getOperationAttribute());

        Node lhs = until.getChild(0);
        Node rhs = until.getChild(1);

        assertEquals(ExpressionNode, lhs.getNodeKind());
        assertEquals(ExpressionKind.IdentifierExpression, lhs.getExpressionKind());
        assertEquals(TokenType.IDENTIFIER, lhs.getOperationAttribute());
        assertEquals("x", lhs.getName());

        assertEquals(ExpressionNode, rhs.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, rhs.getExpressionKind());
        assertEquals(TokenType.NUMBER, rhs.getOperationAttribute());
        assertEquals(1, rhs.getValue());

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
    public void parseIf() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("if 3 < 4 then x := 5 end")));
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

        assertEquals(StatementNode, thenStatement.getNodeKind());
        assertEquals(StatementKind.ASSIGN, thenStatement.getStatementKind());
        assertEquals("x", thenStatement.getName());

        Node assignmentValue = thenStatement.getChild(0);
        assertEquals(ExpressionNode, assignmentValue.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, assignmentValue.getExpressionKind());
        assertEquals(TokenType.NUMBER, assignmentValue.getOperationAttribute());
        assertEquals(5, assignmentValue.getValue());

    }

    @Test
    public void parseIfThenElse() {
        Node root = new Parser().parseStatement(new Scanner().scan(wrap("if 3 < 4 then x := 5 else x := 6 end")));
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

        assertEquals(StatementNode, elseStatement.getNodeKind());
        assertEquals(StatementKind.ASSIGN, elseStatement.getStatementKind());
        assertEquals("x", elseStatement.getName());

        Node assignmentValue = elseStatement.getChild(0);
        assertEquals(ExpressionNode, assignmentValue.getNodeKind());
        assertEquals(ExpressionKind.ConstantExpression, assignmentValue.getExpressionKind());
        assertEquals(TokenType.NUMBER, assignmentValue.getOperationAttribute());
        assertEquals(6, assignmentValue.getValue());

    }

    @Test
    public void parseProgram() throws IOException {
        String program = IOUtils.toString(Test_02_Parser.class.getClassLoader().getResourceAsStream("sample.tny"));
        List<Token> tokens = new Scanner().scan(wrap(program));
        Node parseTree = new Parser().parseProgram(tokens);
    }

}
