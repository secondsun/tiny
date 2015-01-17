package net.saga.lang.tiny.analyize;

import java.util.List;
import java.util.function.Function;
import net.saga.lang.tiny.parser.Node;

public class Analyizer {

    public static SymbolTable buildSymbolTable(Node programNode) {
        SymbolTable table = new SymbolTable();
        traverse(programNode,
                (Node node) -> {
                    String name = node.getName();
                    if (name != null && !name.isEmpty()) {
                        int memoryLoc = 0;
                        
                        if (table.get(name) == null ) {
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
            for (int i =0; i < Node.MAX_CHILDREN; i++) {
                traverse(programNode.getChild(i), preProc, postProc);
            }
            postProc.apply(programNode);
            traverse(programNode.getNext(), preProc, postProc);
        }
    }
    
}
