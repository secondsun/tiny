package net.saga.lang.tiny.test;

import java.io.IOException;
import java.nio.CharBuffer;
import static java.nio.CharBuffer.wrap;
import java.util.List;
import java.util.concurrent.Semaphore;
import net.saga.lang.tiny.analyize.Analyizer;
import net.saga.lang.tiny.analyize.SemanticException;
import net.saga.lang.tiny.parser.Node;
import net.saga.lang.tiny.parser.Parser;
import net.saga.lang.tiny.scanner.Scanner;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.analyize.SymbolTable;
import static net.saga.lang.tiny.parser.NodeType.BOOLEAN;
import static net.saga.lang.tiny.parser.NodeType.INTEGER;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * TINY is a very simple language.
 *
 * There is only one Scope. There are no functions, object or methods. Variables
 * are declared by use. There are only Integer and Boolean types Boolean Types
 * can only come from Boolean Expressions Variables can only have Integer values
 * A boolean value cannot be output using a write statement
 *
 * @author summers
 */
public class Test_04_SemanticAnalysis {

    @Test
    public void populateSimpleTable() {
        String program = "write x;";
        List<Token> tokens = new Scanner().scan(wrap(program));
        Node parseTree = new Parser().parseProgram(tokens);
        SymbolTable table = Analyizer.buildSymbolTable(parseTree);

        assertEquals(1, table.size());
        assertEquals(1, table.get("x").size());
        assertEquals((Integer) 1, table.get("x").get(0).lineNumber);
        assertEquals((Integer) 0, table.get("x").get(0).memoryLocation);
    }

    @Test
    public void populateSymbolTable() throws IOException {
        String program = IOUtils.toString(Test_04_SemanticAnalysis.class.getClassLoader().getResourceAsStream("sample.tny"));
        List<Token> tokens = new Scanner().scan(wrap(program));
        Node parseTree = new Parser().parseProgram(tokens);
        SymbolTable table = Analyizer.buildSymbolTable(parseTree);

        assertEquals(2, table.size());
        assertEquals(6, table.get("x").size());
        assertEquals(4, table.get("fact").size());
        assertEquals(4, table.get("fact").size());

    }

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
    public void testBooleanTypeEquals() {
        List<Token> tokens = new Scanner().scan(wrap("4 = 2"));
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
        List<Token> tokens = new Scanner().scan(wrap("if 0 < x then x := 1 end\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getChild(0).getNodeType());
    }

    @Test
    public void testUntilTestIsBoolean() {
        List<Token> tokens = new Scanner().scan(wrap("repeat x := 1 until 0 < x \n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        assertEquals(BOOLEAN, parseTree.getChild(1).getNodeType());
    }

    
    @Test
    public void testAnalyzeProgram() throws IOException {
        String program = IOUtils.toString(Test_04_SemanticAnalysis.class.getClassLoader().getResourceAsStream("sample.tny"));
        List<Token> tokens = new Scanner().scan(wrap(program));
        Node parseTree = new Parser().parseProgram(tokens);
        Analyizer.typeCheck(parseTree);
    }

    @Test(expected = SemanticException.class)
    public void failOnBadIf() {
        List<Token> tokens = new Scanner().scan(wrap("if 0 + x then x := 1 end\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        fail();
    }

    @Test(expected = SemanticException.class)
    public void failOnBadUntil() {
        List<Token> tokens = new Scanner().scan(wrap("repeat x := 1 until 0 + x \n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        fail();
    }

    @Test(expected = SemanticException.class)
    public void failOnBadInteger() {
        List<Token> tokens = new Scanner().scan(wrap("1 + (4 < 5)\n"));
        Node parseTree = new Parser().parseExpression(tokens);
        Analyizer.typeCheck(parseTree);
        fail();
    }
    
    @Test(expected = SemanticException.class)
    public void onlyAssignInteger() {
        List<Token> tokens = new Scanner().scan(wrap("x := (4 < 5)\n"));
        Node parseTree = new Parser().parseStatement(tokens);
        Analyizer.typeCheck(parseTree);
        fail();
    }
    
    
}
