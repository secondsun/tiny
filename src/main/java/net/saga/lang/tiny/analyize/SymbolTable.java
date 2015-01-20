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

    public int getAddress(String name) {
        return get(name).get(0).memoryLocation;
    }
    
    
    
}
