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
package net.saga.lang.tiny.test;

import java.io.IOException;
import net.saga.lang.tiny.compiler.DynamicClassLoader;
import static java.nio.CharBuffer.wrap;
import java.util.List;
import me.qmx.jitescript.internal.org.objectweb.asm.Opcodes;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.IADD;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.ICONST_0;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.ICONST_1;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.InsnList;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.InsnNode;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.JumpInsnNode;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.LabelNode;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.LdcInsnNode;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.MethodInsnNode;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.VarInsnNode;
import net.saga.lang.tiny.analyize.Analyizer;
import net.saga.lang.tiny.analyize.SymbolTable;
import net.saga.lang.tiny.compiler.CompilerContext;
import net.saga.lang.tiny.compiler.TinyCompiler;
import net.saga.lang.tiny.parser.Node;
import net.saga.lang.tiny.parser.Parser;
import net.saga.lang.tiny.scanner.Scanner;
import net.saga.lang.tiny.scanner.Token;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Now we will compile tiny into actual Java bytecode. We will use JitaCode to
 * simplify a lot of bookkeeping.
 */

public class Test_05_Compiling {

    /**
     * This will test that a number node generates an instruction to push a
     * constant to the JVM stack
     */
    @Test
    @Ignore
    public void testCompileNumber() {
        InsnList insList = compileExpression("5");
        assertEquals(1, insList.size());
        assertTrue(insList.getFirst() instanceof LdcInsnNode);
        assertEquals(5, ((LdcInsnNode) insList.getFirst()).cst);
    }

    @Test
    @Ignore
    public void testCompileAddition() {
        InsnList insList = compileExpression("5 + 7");
        assertEquals(3, insList.size());
        
        assertTrue(insList.get(0) instanceof LdcInsnNode);
        assertEquals(5, ((LdcInsnNode) insList.get(0)).cst);
        
        assertTrue(insList.get(1) instanceof LdcInsnNode);
        assertEquals(7, ((LdcInsnNode) insList.get(1)).cst);
        
        assertTrue(insList.get(2) instanceof InsnNode);
        assertEquals(IADD, ((InsnNode) insList.get(2)).getOpcode());
    }

    @Test
    @Ignore
    public void testCompileMultiplication() {
        InsnList insList = compileExpression("9 * 12");
        assertEquals(3, insList.size());
        
        assertTrue(insList.get(0) instanceof LdcInsnNode);
        assertEquals(9, ((LdcInsnNode) insList.get(0)).cst);
        
        assertTrue(insList.get(1) instanceof LdcInsnNode);
        assertEquals(12, ((LdcInsnNode) insList.get(1)).cst);
        
        assertTrue(insList.get(2) instanceof InsnNode);
        assertEquals(Opcodes.IMUL, ((InsnNode) insList.get(2)).getOpcode());
    }

    @Test
    @Ignore
    public void testCompileSubtraction() {
        InsnList insList = compileExpression("15 - 8");
        assertEquals(3, insList.size());
        
        assertTrue(insList.get(0) instanceof LdcInsnNode);
        assertEquals(15, ((LdcInsnNode) insList.get(0)).cst);
        
        assertTrue(insList.get(1) instanceof LdcInsnNode);
        assertEquals(8, ((LdcInsnNode) insList.get(1)).cst);
        
        assertTrue(insList.get(2) instanceof InsnNode);
        assertEquals(Opcodes.ISUB, ((InsnNode) insList.get(2)).getOpcode());
    }

    @Test
    @Ignore
    public void testCompileIntDivision() {
        InsnList insList = compileExpression("2/1");
        assertEquals(3, insList.size());
        
        assertTrue(insList.get(0) instanceof LdcInsnNode);
        assertEquals(2, ((LdcInsnNode) insList.get(0)).cst);
        
        assertTrue(insList.get(1) instanceof LdcInsnNode);
        assertEquals(1, ((LdcInsnNode) insList.get(1)).cst);
        
        assertTrue(insList.get(2) instanceof InsnNode);
        assertEquals(Opcodes.IDIV, ((InsnNode) insList.get(2)).getOpcode());
    }

    @Test
    @Ignore
    public void testCompileEqComparison() {
        InsnList insList = compileExpression("1 = 2");
        assertEquals(8, insList.size());
        
        assertTrue(insList.get(0) instanceof LdcInsnNode);
        assertEquals(1, ((LdcInsnNode) insList.get(0)).cst);
        
        assertTrue(insList.get(1) instanceof LdcInsnNode);
        assertEquals(2, ((LdcInsnNode) insList.get(1)).cst);
        
        assertTrue(insList.get(2) instanceof JumpInsnNode);
        assertEquals(Opcodes.IF_ICMPNE, ((JumpInsnNode) insList.get(2)).getOpcode());
        
        assertTrue(insList.getLast() instanceof LabelNode);
        assertEquals(ICONST_0, ((InsnNode) insList.getLast().getPrevious()).getOpcode());
        assertTrue(insList.getLast().getPrevious().getPrevious() instanceof LabelNode);
        assertTrue(insList.getLast().getPrevious().getPrevious().getPrevious() instanceof JumpInsnNode);
        assertEquals((int)ICONST_1, ((InsnNode) insList.getLast().getPrevious().getPrevious().getPrevious().getPrevious()).getOpcode());
        
        
    }

    @Test
    @Ignore
    public void testCompileLTComparison() {
        InsnList insList = compileExpression("1 < 2");
        assertEquals(8, insList.size());
        
        assertTrue(insList.get(0) instanceof LdcInsnNode);
        assertEquals(1, ((LdcInsnNode) insList.get(0)).cst);
        
        assertTrue(insList.get(1) instanceof LdcInsnNode);
        assertEquals(2, ((LdcInsnNode) insList.get(1)).cst);
        
        assertTrue(insList.get(2) instanceof JumpInsnNode);
        assertEquals(Opcodes.IF_ICMPGE, ((JumpInsnNode) insList.get(2)).getOpcode());
        
        assertTrue(insList.getLast() instanceof LabelNode);
        assertEquals(ICONST_0, ((InsnNode) insList.getLast().getPrevious()).getOpcode());
        assertTrue(insList.getLast().getPrevious().getPrevious() instanceof LabelNode);
        assertTrue(insList.getLast().getPrevious().getPrevious().getPrevious() instanceof JumpInsnNode);
        assertEquals((int)ICONST_1, ((InsnNode) insList.getLast().getPrevious().getPrevious().getPrevious().getPrevious()).getOpcode());

    }

    @Test
    @Ignore
    public void testCompileAssignment() {
        InsnList insList = compileStatement("x := 1");
        assertEquals(2, insList.size());
        
        assertTrue(insList.get(0) instanceof LdcInsnNode);
        assertEquals(1, ((LdcInsnNode) insList.get(0)).cst);
        
        assertTrue(insList.get(1) instanceof VarInsnNode);
        assertEquals(0, ((VarInsnNode) insList.get(1)).var);
        
    }

    @Test
    @Ignore
    public void testCompileRead() {
        InsnList insList = compileStatement("read x");
        assertEquals(4, insList.size());
        
        assertTrue(insList.get(0) instanceof MethodInsnNode);
        assertEquals("console", ((MethodInsnNode) insList.get(0)).name);
        
        assertTrue(insList.get(1) instanceof MethodInsnNode);
        assertEquals("readLine", ((MethodInsnNode) insList.get(1)).name);
        
        assertTrue(insList.get(2) instanceof MethodInsnNode);
        assertEquals("parseInt", ((MethodInsnNode) insList.get(2)).name);
        
        assertTrue(insList.getLast() instanceof VarInsnNode);
        assertEquals(0, ((VarInsnNode) insList.getLast()).var);
        
    }

//    @Test
//    public void testCompileWrite() {
//        InsnList insList = compileStatement("write x");
//        assertEquals(3, insList.size());
//        
//        assertTrue(insList.get(0) instanceof FieldInsnNode);
//        assertEquals("out", ((FieldInsnNode) insList.get(0)).name);
//        
//        assertTrue(insList.get(1) instanceof FieldInsnNode);
//        assertEquals("x", ((FieldInsnNode) insList.get(1)).name);
//        
//        assertTrue(insList.getLast() instanceof MethodInsnNode);
//        assertEquals("println", ((MethodInsnNode) insList.getLast()).name);
//    }
//
//    @Test
//    public void testCompileRepeat() {
//        InsnList insList = compileStatement("repeat \n"
//                                            + "x:=1 \n"
//                                            + "until x = 1;");
//        fail();
//    }
//
//    @Test
//    public void testCompileIf() {
//        fail();
//    }
//
//    @Test
//    public void testCompileIfThen() {
//        fail();
//    }
//
//    @Test
//    public void testEchoProgram() {
//        String program = "read x; write x";
//        Class<?> klass = compileProgram(program);
//    }
//
//    
    @Test
    public void testCompileProgram() throws IOException {
        String program = IOUtils.toString(Test_05_Compiling.class.getClassLoader().getResourceAsStream("sample.tny"));
        Class<?> klass = compileProgram(program);
//        fail();
    }

    private Class<?> compileProgram(String program) {
        List<Token> scanned = new Scanner().scan(wrap(program));
        
        Node parseTree = new Parser().parseStatement(scanned);
        SymbolTable table = Analyizer.buildSymbolTable(parseTree);
        
        CompilerContext context = TinyCompiler.compileProgram(parseTree, new CompilerContext(), table);
        
        Class<?> compiledClass = new DynamicClassLoader().define(context.jiteClass);

        return compiledClass;
        
    }
    
    private InsnList compileExpression(String expression) {
        List<Token> scanned = new Scanner().scan(wrap(expression));
        
        Node parseTree = new Parser().parseExpression(scanned);
        CompilerContext context = TinyCompiler.compileExpression(parseTree, new CompilerContext(), new SymbolTable());

        InsnList insList = context.currentBlock().getInstructionList();
        return insList;
    }

    private InsnList compileStatement(String expression) {
        List<Token> scanned = new Scanner().scan(wrap(expression));
        
        Node parseTree = new Parser().parseStatement(scanned);
        SymbolTable table = Analyizer.buildSymbolTable(parseTree);
        
        CompilerContext context = TinyCompiler.compileStatement(parseTree, new CompilerContext(), table);

        InsnList insList = context.currentBlock().getInstructionList();
        return insList;
    }

    
}
