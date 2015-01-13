package net.saga.lang.tiny.parser;

import java.util.ArrayList;
import java.util.List;
import net.saga.lang.tiny.scanner.Token;
import net.saga.lang.tiny.scanner.TokenType;

public class Node {
    
    private final ExpressionKind expressionKind;
    private final StatementKind statementKind;
    private final NodeKind nodeKind;
    private final TokenType operationAttribute;
    private final int lineNumber;
    private final int value;
    private final String name;
    private final List<Node> children = new ArrayList<>(3);
    private Node next;
    
    public Node(ExpressionKind expressionKind, Token token) {
        this.expressionKind = expressionKind;
        this.statementKind = null;
        this.nodeKind = NodeKind.ExpressionNode;
        this.operationAttribute = token.getType();
        this.lineNumber = token.getLineNumber();
        this.value = token.getValue();
        this.name = token.getName();
    }

    public Node(StatementKind statementKind, Token token) {
        this.expressionKind = null;
        this.statementKind = statementKind;
        this.nodeKind = NodeKind.StatementNode;
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

    
    
}
