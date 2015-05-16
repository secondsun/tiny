package net.saga.lang.cminus.test;

import java.io.IOException;
import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.cminus.analyize.Analyizer;
import net.saga.lang.cminus.analyize.SemanticException;
import net.saga.lang.cminus.analyize.SymbolTable;
import net.saga.lang.cminus.parser.Node;
import net.saga.lang.cminus.parser.NodeType;
import static net.saga.lang.cminus.parser.NodeType.BOOLEAN;
import static net.saga.lang.cminus.parser.NodeType.INTEGER;
import net.saga.lang.cminus.parser.Parser;
import net.saga.lang.cminus.scanner.Scanner;
import net.saga.lang.cminus.scanner.Token;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class Test_04_SemanticAnalysis {
    @Test
    public void populateSimpleTable() {
        String program = "int x;";
        List<Token> tokens = new Scanner().scan(wrap(program));
        Node parseTree = new Parser().parseProgram(tokens);
        SymbolTable table = Analyizer.buildSymbolTable(parseTree);

        assertEquals(1, table.size());
        assertEquals(1, table.get("x").size());
        assertEquals((Integer) 1, table.get("x").get(0).lineNumber);
        assertEquals((Integer) 0, table.get("x").get(0).memoryLocation);
    }
//
//    @Test
//    public void populateSymbolTable() throws IOException {
//        String program = IOUtils.toString(net.saga.lang.tiny.test.Test_04_SemanticAnalysis.class.getClassLoader().getResourceAsStream("sample.tny"));
//        List<Token> tokens = new Scanner().scan(wrap(program));
//        Node parseTree = new Parser().parseProgram(tokens);
//        SymbolTable table = Analyizer.buildSymbolTable(parseTree);
//
//        assertEquals(2, table.size());
//        assertEquals(6, table.get("x").size());
//        assertEquals(4, table.get("fact").size());
//        assertEquals(4, table.get("fact").size());
//
//    }

    @Test
    public void testIntegerTypeAddition() {
        List<Token> tokens = new Scanner().scan(wrap("4 + 2;"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(INTEGER, parseTree.getNodeType());
    }

    @Test
    public void testIntegerTypeSubtraction() {
        List<Token> tokens = new Scanner().scan(wrap("4 - 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(INTEGER, parseTree.getNodeType());
    }

    @Test
    public void testIntegerTypeMultiplication() {
        List<Token> tokens = new Scanner().scan(wrap("4 * 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(INTEGER, parseTree.getNodeType());
    }

    @Test
    public void testIntegerTypeDivision() {
        List<Token> tokens = new Scanner().scan(wrap("4 / 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(INTEGER, parseTree.getNodeType());
    }

    @Test
    public void testBooleanTypeLessThan() {
        List<Token> tokens = new Scanner().scan(wrap("4 < 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getNodeType());
    }

    @Test
    public void testBooleanTypeGreaterThanEqual() {
        List<Token> tokens = new Scanner().scan(wrap("4 >= 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getNodeType());
    }

    @Test
    public void testBooleanTypeGreaterThan() {
        List<Token> tokens = new Scanner().scan(wrap("4 > 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getNodeType());
    }

    @Test
    public void testBooleanTypeLessThanEqual() {
        List<Token> tokens = new Scanner().scan(wrap("4 <= 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getNodeType());
    }

    @Test
    public void testBooleanTypeEquals() {
        List<Token> tokens = new Scanner().scan(wrap("4 == 2"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getNodeType());
    }

    @Test
    public void testIdentifierIsInteger() {
        List<Token> tokens = new Scanner().scan(wrap("x"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(INTEGER, parseTree.getNodeType());
    }

    @Test
    public void testIfTestIsBoolean() {
        List<Token> tokens = new Scanner().scan(wrap("if (0 < x) { x = 1; }\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getChild(0).getNodeType());
    }

    @Test
    public void testWhileTestIsBoolean() {
        List<Token> tokens = new Scanner().scan(wrap("while (0 < x) { x = 1; }\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getChild(0).getNodeType());
    }

    @Test
    public void testAnalyzeMainFunction() throws IOException {
        List<Token> tokens = new Scanner().scan(wrap("void main(void){int x; int y; \n"
                + "  x = 4;y = x;\n"
                + "}"));
        Node parseTree = new Parser().parseDeclaration(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(NodeType.VOID, parseTree.getChild(0).getNodeType());
    }

    @Test
    public void testAnalyzeProgram() throws IOException {
        String program = IOUtils.toString(Test_02_Parser.class.getClassLoader().getResourceAsStream("sample.cm"));
        List<Token> tokens = new Scanner().scan(wrap(program));
        Node node = new Parser().parseProgram(tokens);
        Analyizer.typeCheck(node);
    }

    @Test(expected = SemanticException.class)
    public void failOnBadIf() {
        List<Token> tokens = new Scanner().scan(wrap("if (0 + x) { x = 1; }\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        fail();
    }

    @Test(expected = SemanticException.class)
    public void failOnBadWhile() {
        List<Token> tokens = new Scanner().scan(wrap("while(x + 1) {1+1;}\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        fail();
    }

    @Test(expected = SemanticException.class)
    public void failOnBadInteger() {
        List<Token> tokens = new Scanner().scan(wrap("1 + (4 < 5);\n"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        fail();
    }

    @Test
    public void testCompoundStatement() {
        List<Token> tokens = new Scanner().scan(wrap("{int x; int y; \n"
                + "  x = 4;y = x;\n"
                + "}"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);

    }

    @Test(expected = SemanticException.class)
    public void onlyAssignInteger() {
        List<Token> tokens = new Scanner().scan(wrap("x = (4 < 5);\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
    }
}
