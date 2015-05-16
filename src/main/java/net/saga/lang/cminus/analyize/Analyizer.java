/**
 * Copyright (C) 2015 Summers Pittman (secondsun@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.saga.lang.cminus.analyize;

import java.util.function.Function;
import net.saga.lang.cminus.parser.DeclarationKind;
import net.saga.lang.cminus.parser.Node;
import net.saga.lang.cminus.parser.NodeType;
import static net.saga.lang.cminus.parser.NodeType.BOOLEAN;
import static net.saga.lang.cminus.parser.NodeType.INTEGER;
import net.saga.lang.cminus.parser.TypeSpecifier;

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
                                case WHILE:
                                    if (child1.getNodeType() != BOOLEAN) {
                                        throw new SemanticException("If test is not boolean" + child1.toString());
                                    }
                                    break;
                                case COMPOUND:
                                    break;//??
                                default:
                                    throw new AssertionError(node.getStatementKind().name());

                            }
                            break;
                        case ExpressionNode:
                            switch (node.getExpressionKind()) {
                                case OperatorExpression:

                                    switch (node.getOperationAttribute()) {

                                        case PLUS:
                                        case MINUS:
                                        case MULTIPLY:
                                        case DIVIDE:
                                        case ASSIGN:
                                            if (child1.getNodeType() != INTEGER
                                            || child2.getNodeType() != INTEGER) {
                                                throw new SemanticException("Operators are not integers" + node.toString());
                                            }
                                            node.setNodeType(INTEGER);
                                            break;
                                        case LT:
                                        case GT:
                                        case LTE:
                                        case GTE:
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
                                    case AssignmentExpression: 
                                        if (child2.getNodeType() != INTEGER) {
                                            throw new SemanticException("Only interger assignment is supported");
                                        }
                                case ConstantExpression:
                                case IdentifierExpression:
                                    node.setNodeType(NodeType.INTEGER);
                                    break;
                                default:
                                    throw new AssertionError(node.getExpressionKind().name());
                            }
                            break;
                        case DeclarationNode:
                            if (node.getDeclarationKind().equals(DeclarationKind.VARIABLE)) {
                                //??
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
