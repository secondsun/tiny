package net.saga.lang.tiny.analyize;

import java.util.List;
import java.util.function.Function;
import net.saga.lang.tiny.parser.Node;
import net.saga.lang.tiny.parser.NodeType;
import static net.saga.lang.tiny.parser.NodeType.BOOLEAN;
import static net.saga.lang.tiny.parser.NodeType.INTEGER;

public class Analyizer {

    public static SymbolTable buildSymbolTable(Node programNode) {
        SymbolTable table = new SymbolTable();
        traverse(programNode,
                (Node node) -> {
                    String name = node.getName();
                    if (name != null && !name.isEmpty()) {
                        int memoryLoc = 0;

                        if (table.get(name) == null) {
                            memoryLoc = table.size();
                        } else {
                            memoryLoc = table.get(name).get(0).memoryLocation;
                        }
                        SymbolTableEntry entry = new SymbolTableEntry(node.getName(), memoryLoc, node.getLineNumber());
                        table.put(node.getName(), entry);
                    }
                    return null;
                },
                (Node node) -> null);
        return table;
    }

    public static void traverse(Node programNode, Function<Node, Void> preProc, Function<Node, Void> postProc) {
        if (programNode != null) {
            preProc.apply(programNode);
            for (int i = 0; i < Node.MAX_CHILDREN; i++) {
                traverse(programNode.getChild(i), preProc, postProc);
            }
            postProc.apply(programNode);
            traverse(programNode.getNext(), preProc, postProc);
        }
    }

    public static void typeCheck(Node programNode) {
        traverse(programNode,
                (Node node) -> null,
                (Node node) -> {
                    Node child1 = node.getChild(0);
                    Node child2 = node.getChild(1);
                    Node child3 = node.getChild(3);

                    switch (node.getNodeKind()) {

                        case StatementNode:
                            switch (node.getStatementKind()) {
                                case IF:
                                    if (child1.getNodeType() != BOOLEAN) {
                                        throw new SemanticException("If test is not boolean" + child1.toString());
                                    }
                                    break;
                                case REPEAT:
                                    if (child2.getNodeType() != BOOLEAN) {
                                        throw new SemanticException("Until test is not boolean" + child1.toString());
                                    }
                                    break;
                                case WRITE:
                                case ASSIGN:

                                    if (child1.getNodeType() != INTEGER) {
                                        throw new SemanticException("Child is not integers" + child1.toString());
                                    }
                                    break;
                                case READ:
                                    break;//variable is assumed to be integer
                                default:
                                    throw new AssertionError(node.getStatementKind().name());

                            }
                            break;
                        case ExpressionNode:
                            switch (node.getExpressionKind()) {
                                case OperatorExpression:

                                    switch (node.getOperationAttribute()) {

                                        case ADDITION:
                                        case SUBTRACTION:                                            
                                        case INT_DIVISION:
                                        case MULTIPLICATION:

                                            if (child1.getNodeType() != INTEGER
                                            || child2.getNodeType() != INTEGER) {
                                                throw new SemanticException("Operators are not integers" + node.toString());
                                            }
                                            node.setNodeType(INTEGER);
                                            break;
                                        case LT:
                                        case EQ:
                                            if (child1.getNodeType() != INTEGER
                                            || child2.getNodeType() != INTEGER) {
                                                throw new SemanticException("Operators are not integers" + node.toString());
                                            }
                                            node.setNodeType(BOOLEAN);
                                            break;
                                        default:
                                            throw new SemanticException("Whoopse@" + node.toString());
                                    }
                                    break;
                                case ConstantExpression:
                                case IdentifierExpression:
                                    node.setNodeType(NodeType.INTEGER);
                                    break;
                                default:
                                    throw new AssertionError(node.getExpressionKind().name());
                            }
                            break;
                        default:
                            throw new AssertionError(node.getNodeKind().name());

                    }
                    return null;
                }
        );

    }

}
