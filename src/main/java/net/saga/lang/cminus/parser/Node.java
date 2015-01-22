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
package net.saga.lang.cminus.parser;

import java.util.ArrayList;
import java.util.List;
import static java.util.stream.IntStream.range;
import static net.saga.lang.cminus.parser.NodeType.VOID;
import net.saga.lang.cminus.scanner.Token;
import net.saga.lang.cminus.scanner.TokenType;

public class Node {

    public static final int MAX_CHILDREN = 3;

    private final ExpressionKind expressionKind;
    private final StatementKind statementKind;
    private final DeclarationKind declarationKind;
    private final NodeKind nodeKind;
    private NodeType nodeType = VOID;
    private final TokenType operationAttribute;
    private final int lineNumber;
    private final int value;
    private final String name;
    private final List<Node> children = new ArrayList<>(3);
    private TypeSpecifier typeSpecifier;
    private int size = -1;

    public TypeSpecifier getTypeSpecifier() {
        return typeSpecifier;
    }

    public void setTypeSpecifier(TypeSpecifier typeSpecifier) {
        this.typeSpecifier = typeSpecifier;
    }
    private Node next;

    public Node(ExpressionKind expressionKind, Token token) {
        this.expressionKind = expressionKind;
        this.statementKind = null;
        this.declarationKind = null;
        this.nodeKind = NodeKind.ExpressionNode;
        this.operationAttribute = token.getType();
        this.lineNumber = token.getLineNumber();
        this.value = token.getValue();
        this.name = token.getName();

    }

    public Node(StatementKind statementKind, Token token) {
        this.expressionKind = null;
        this.declarationKind = null;
        this.statementKind = statementKind;
        this.nodeKind = NodeKind.StatementNode;
        this.operationAttribute = token.getType();
        this.lineNumber = token.getLineNumber();
        this.value = token.getValue();
        this.name = token.getName();
    }

    public Node(DeclarationKind declarationKind, Token token) {
        this.expressionKind = null;
        this.declarationKind = declarationKind;
        this.statementKind = null;
        this.nodeKind = NodeKind.DeclarationNode;
        this.operationAttribute = token.getType();
        this.lineNumber = token.getLineNumber();
        this.value = token.getValue();
        this.name = token.getName();
    }

    public ExpressionKind getExpressionKind() {
        return expressionKind;
    }

    public NodeKind getNodeKind() {
        return nodeKind;
    }

    public TokenType getOperationAttribute() {
        return operationAttribute;
    }

    public Node getChild(int i) {
        if (i >= children.size()) {
            return null;
        }
        return children.get(i);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getValue() {
        return value;
    }

    public void setChild(int index, Node childNode) {
        children.add(index, childNode);
    }

    public StatementKind getStatementKind() {
        return statementKind;
    }

    public String getName() {
        return name;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public String toString() {
        return toString(0);
    }

    private String toString(int indent) {
        StringBuilder builder = new StringBuilder();
        range(0, indent).forEach((i) -> builder.append("    "));

        builder.append(lineNumber).append(":{");
        switch (nodeKind) {
            case StatementNode:
                builder.append(this.statementKind);

                for (Node child : children) {
                    builder.append('\n');
                    builder.append(child.toString(indent + 1));
                }
                break;
            case ExpressionNode:
                switch (expressionKind) {
                    case OperatorExpression:
                        builder.append(operationAttribute);
                        for (Node child : children) {
                            builder.append('\n');
                            builder.append(child.toString(indent + 1));
                        }
                        break;
                    case ConstantExpression:
                        builder.append(value);
                        break;
                    case IdentifierExpression:
                        builder.append(name);
                        break;
                    case AssignmentExpression:
                        builder.append(getChild(0).name).append("=").append(getChild(1).toString());
                        break;
                    case CallExpression:
                        builder.append(name).append("( ");
                        for (Node child : children) {
                            builder.append('\n');
                            builder.append(child.toString(indent + 1));
                        }
                        builder.append(" ) ");
                        break;
                    default:
                        throw new AssertionError(expressionKind.name());

                }
                break;
            case DeclarationNode:
                builder.append(typeSpecifier.name()).append(" ").append(name);
                for (Node child : children) {
                    builder.append('\n');
                    builder.append(child.toString(indent + 1));
                }
                break;
            default:
                throw new AssertionError(nodeKind.name());

        }

        builder.append("}\n");
        if (next != null) {
            builder.append(next.toString(indent));
        }

        return builder.toString();
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public DeclarationKind getDeclarationKind() {
        return declarationKind;
    }

    public void setSize(int value) {
        this.size = value;
    }

    public int getSize() {
        return this.size;
    }

}
