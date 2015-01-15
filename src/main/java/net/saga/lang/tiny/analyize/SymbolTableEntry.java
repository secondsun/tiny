/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.lang.tiny.analyize;

import java.util.Objects;

public class SymbolTableEntry implements Comparable<SymbolTableEntry>{

    public final String variableName;
    public final Integer memoryLocation;
    public final Integer lineNumber;

    public SymbolTableEntry(String variableName, int memoryLocation, int lineNumber) {
        this.variableName = variableName;
        this.memoryLocation = memoryLocation;
        this.lineNumber = lineNumber;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.variableName);
        hash = 41 * hash + this.memoryLocation;
        hash = 41 * hash + Objects.hashCode(this.lineNumber);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SymbolTableEntry other = (SymbolTableEntry) obj;
        if (!Objects.equals(this.variableName, other.variableName)) {
            return false;
        }
        if (this.memoryLocation != other.memoryLocation) {
            return false;
        }
        if (!Objects.equals(this.lineNumber, other.lineNumber)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return variableName + "@" + memoryLocation + "#L" + lineNumber;
    }

    @Override
    public int compareTo(SymbolTableEntry o) {
        if (o == null) {
            return 1;
        }
        
        if (variableName.equals(o.variableName)) {
            return lineNumber.compareTo(o.lineNumber);
        } else {
            return variableName.compareTo(o.variableName);
        }
        
    }
    
    
    
}
