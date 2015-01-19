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

        switch(statementNode.getStatementKind()){
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
                block.istore(symbols.getAddress(statementNode.getName()));
                break;
            case READ:
                block.invokestatic(p(System.class), "console", sig(Console.class));
                block.invokevirtual(p(Console.class), "readLine", sig(String.class));
                block.invokestatic(p(Integer.class), "parseInt", sig(int.class, String.class));
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
                ctx.currentBlock().iload(symbols.getAddress(expressionNode.getName()));
                return ctx;
            default:
                throw new AssertionError(expressionNode.getExpressionKind().name());
        
        }
        
        
    }

    private static void addOperator(Node expressionNode, CompilerContext ctx) {
        CodeBlock block = ctx.currentBlock();
        switch(expressionNode.getOperationAttribute()) {
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
                throw new RuntimeException("Wrong operation:"+expressionNode.getOperationAttribute());
        }
        
    }

    public static CompilerContext compileProgram(Node parseTree, CompilerContext compilerContext, SymbolTable symbols) {

        compileStatement(parseTree, compilerContext, symbols); //Compile expression
        compilerContext.currentBlock().aconst_null();
        compilerContext.currentBlock().areturn(); // return value of expression

        compilerContext.blockToMethod("main");

        return compilerContext;
        
        
    }

    
    
    
}
