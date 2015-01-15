
package net.saga.lang.tiny.analyize;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.saga.lang.tiny.parser.Node;

public class SymbolTable extends AbstractMap<String, List<SymbolTableEntry>> {

    private final HashMap<String, List<SymbolTableEntry>> map;


    public SymbolTable() {
        this.map = new HashMap<>(100);
    }
    
    
    
    @Override
    public Set<Entry<String, List<SymbolTableEntry>>> entrySet() {
        return map.entrySet();
    }

    public synchronized List<SymbolTableEntry> put(String key, SymbolTableEntry value) {
        List<SymbolTableEntry> entries = map.get(key);
        if (entries == null) {
            entries = new ArrayList<>();
            map.put(key, entries);
        }
        
        entries.add(value);
        Collections.sort(entries);

        return new ArrayList<>(entries);
        
    }

    private void populateMap(Node parseTree) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
