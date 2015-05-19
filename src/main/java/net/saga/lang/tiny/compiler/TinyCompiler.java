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
package net.saga.lang.tiny.compiler;

import java.io.Console;
import java.io.PrintStream;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.LabelNode;
import static me.qmx.jitescript.util.CodegenUtils.ci;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import net.saga.lang.tiny.analyize.SymbolTable;
import net.saga.lang.tiny.parser.Node;

public class TinyCompiler {

    public static CompilerContext compileStatement(Node statementNode, CompilerContext compilerContext, SymbolTable symbols) {
        CodeBlock block = compilerContext.currentBlock();
        if (compilerContext.shouldMarkLine(statementNode.getLineNumber())) {
            LabelNode instructionLabel = new LabelNode();
            block.label(instructionLabel);
            block.line(statementNode.getLineNumber(), instructionLabel);
            compilerContext.markLine(statementNode.getLineNumber());
        }
        switch (statementNode.getStatementKind()) {
            case IF:
                LabelNode elseLabel = new LabelNode();
                LabelNode afterElseLabel = new LabelNode();

                compileExpression(statementNode.getChild(0), compilerContext, symbols);
                block.iconst_1();
                block.if_icmpne(elseLabel);
                compileStatement(statementNode.getChild(1), compilerContext, symbols);
                block.go_to(afterElseLabel);
                block.label(elseLabel);
                if (statementNode.getChild(2) != null) {
                    compileStatement(statementNode.getChild(2), compilerContext, symbols);
                }
                block.label(afterElseLabel);

                break;
            case REPEAT:
                LabelNode startRepeat = new LabelNode();
                block.label(startRepeat);
                compileStatement(statementNode.getChild(0), compilerContext, symbols);
                compileExpression(statementNode.getChild(1), compilerContext, symbols);
                LabelNode exit = new LabelNode();
                block.iconst_1();
                block.if_icmpeq(exit);
                block.go_to(startRepeat);
                block.label(exit);
                break;
            case ASSIGN:
                compileExpression(statementNode.getChild(0), compilerContext, symbols);
                if (statementNode.getLineNumber() == symbols.get(statementNode.getName()).get(0).lineNumber) {
                    LabelNode varLabel = new LabelNode();
                    compilerContext.currentBlock().label(varLabel);
                    compilerContext.currentBlock().visitLocalVariable(statementNode.getName(), org.objectweb.asm.Type.getDescriptor(int.class), sig(int.class), varLabel, compilerContext.endLabel, symbols.getAddress(statementNode.getName()));
                }
                block.istore(symbols.getAddress(statementNode.getName()));
                break;
            case READ:
                block.invokestatic(p(System.class), "console", sig(Console.class));
                block.invokevirtual(p(Console.class), "readLine", sig(String.class));
                block.invokestatic(p(Integer.class), "parseInt", sig(int.class, String.class));
                if (statementNode.getLineNumber() == symbols.get(statementNode.getName()).get(0).lineNumber) {
                    LabelNode varLabel = new LabelNode();
                    compilerContext.currentBlock().label(varLabel);
                    compilerContext.currentBlock().visitLocalVariable(statementNode.getName(), org.objectweb.asm.Type.getDescriptor(int.class), sig(int.class), varLabel, compilerContext.endLabel, symbols.getAddress(statementNode.getName()));
                }
                block.istore(symbols.getAddress(statementNode.getName()));
                break;
            case WRITE:
                block.getstatic(p(System.class), "out", ci(PrintStream.class));
                compileExpression(statementNode.getChild(0), compilerContext, symbols);
                block.invokevirtual(p(PrintStream.class), "println", sig(void.class, int.class));
                break;
            default:
                throw new AssertionError(statementNode.getStatementKind().name());
        }

        if (statementNode.getNext() != null) {
            return compileStatement(statementNode.getNext(), compilerContext, symbols);
        }

        return compilerContext;
    }

    public static CompilerContext compileExpression(Node expressionNode, CompilerContext ctx, SymbolTable symbols) {
        if (ctx.shouldMarkLine(expressionNode.getLineNumber())) {
            LabelNode instructionLabel = new LabelNode();
            ctx.currentBlock().label(instructionLabel);
            ctx.currentBlock().line(expressionNode.getLineNumber(), instructionLabel);
            ctx.markLine(expressionNode.getLineNumber());
        }

        switch (expressionNode.getExpressionKind()) {
            case OperatorExpression:

                compileExpression(expressionNode.getChild(0), ctx, symbols);
                compileExpression(expressionNode.getChild(1), ctx, symbols);
                addOperator(expressionNode, ctx);
                return ctx;
            case ConstantExpression:
                ctx.currentBlock().ldc(expressionNode.getValue());
                return ctx;

            case IdentifierExpression:
                if (expressionNode.getLineNumber() == symbols.get(expressionNode.getName()).get(0).lineNumber) {
                    LabelNode varLabel = new LabelNode();
                    ctx.currentBlock().label(varLabel);
                    ctx.currentBlock().visitLocalVariable(expressionNode.getName(), org.objectweb.asm.Type.getDescriptor(int.class), sig(int.class), varLabel, ctx.endLabel, symbols.getAddress(expressionNode.getName()));
                }
                ctx.currentBlock().iload(symbols.getAddress(expressionNode.getName()));
                return ctx;
            default:
                throw new AssertionError(expressionNode.getExpressionKind().name());

        }

    }

    private static void addOperator(Node expressionNode, CompilerContext ctx) {
        CodeBlock block = ctx.currentBlock();
        if (ctx.shouldMarkLine(expressionNode.getLineNumber())) {
            LabelNode instructionLabel = new LabelNode();
            ctx.currentBlock().label(instructionLabel);
            ctx.currentBlock().line(expressionNode.getLineNumber(), instructionLabel);
            ctx.markLine(expressionNode.getLineNumber());
        }
        switch (expressionNode.getOperationAttribute()) {
            case MULTIPLICATION:
                block.imul();
                return;
            case ADDITION:
                block.iadd();
                return;
            case SUBTRACTION:
                block.isub();
                return;
            case INT_DIVISION:
                block.idiv();
                return;
            case EQ: {
                LabelNode isNotEqual = new LabelNode();
                LabelNode afterEqual = new LabelNode();
                block.if_icmpne(isNotEqual)
                        .iconst_1()
                        .go_to(afterEqual)
                        .label(isNotEqual)
                        .iconst_0()
                        .label(afterEqual);
                return;
            }
            case LT: {
                LabelNode isNotEqual = new LabelNode();
                LabelNode afterEqual = new LabelNode();
                block.if_icmpge(isNotEqual)
                        .iconst_1()
                        .go_to(afterEqual)
                        .label(isNotEqual)
                        .iconst_0()
                        .label(afterEqual);
                return;
            }
            default:
                throw new RuntimeException("Wrong operation:" + expressionNode.getOperationAttribute());
        }

    }

    public static CompilerContext compileProgram(Node parseTree, CompilerContext compilerContext, SymbolTable symbols) {

        compileStatement(parseTree, compilerContext, symbols); //Compile expression
        compilerContext.currentBlock().label(compilerContext.endLabel);
        compilerContext.currentBlock().voidreturn(); // return value of expression

        compilerContext.blockToMain();

        return compilerContext;

    }

}
