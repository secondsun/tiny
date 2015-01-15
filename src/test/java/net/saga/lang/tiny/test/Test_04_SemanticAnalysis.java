package net.saga.lang.tiny.test;

import java.io.IOException;
import java.nio.CharBuffer;
import static java.nio.CharBuffer.wrap;
import java.util.List;
import net.saga.lang.tiny.analyize.Analyizer;
import net.saga.lang.tiny.parser.Node;
import net.saga.lang.tiny.parser.Parser;
import net.saga.lang.tiny.scanner.Scanner;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.analyize.SymbolTable;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;


/**
 * TINY is a very simple language.
 * 
 * There is only one Scope.
 * There are no functions, object or methods.
 * Variables are declared by use.
 * There are only Integer and Boolean types
 * Boolean Types can only come from Boolean Expressions
 * Variables can only have Integer values
 * A boolean value cannot be output using a write statement
 * 
 * @author summers
 */
public class Test_04_SemanticAnalysis {

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
    public void testBooleanType(){
        fail();
    }
    
    @Test
    public void useOfUninitializedVariableIsAnError(){
        fail();
    }
    
    //assign types
    //allocatevariables
    //fold constants
    //Illegal numbers cause errors
    //Dependency graph
    //symbol table
}
