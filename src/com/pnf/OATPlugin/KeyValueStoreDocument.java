package com.pnf.OATPlugin;

import java.util.ArrayList;
import java.util.List;

import com.pnf.OAT.OATFile;
import com.pnfsoftware.jeb.core.events.JebEventSource;
import com.pnfsoftware.jeb.core.output.table.ICellCoordinates;
import com.pnfsoftware.jeb.core.output.table.ITableDocument;
import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
import com.pnfsoftware.jeb.core.output.table.impl.Cell;
import com.pnfsoftware.jeb.core.output.table.impl.TableRow;

public class KeyValueStoreDocument extends JebEventSource implements ITableDocument {

    // View of the keyvalue store in the OAT's header

    List<TableRow> rows;

    OATFile oat;

    public KeyValueStoreDocument(OATFile oat) {
        this.oat = oat;
        rows = new ArrayList<>();

        String[] keyValueStore = oat.getKeyValueStore().split("\0");
        String key;
        String value;
        List<Cell> cells = new ArrayList<>();
        for(int index=0; index < keyValueStore.length / 2; index++) {
            cells = new ArrayList<>();
            key = keyValueStore[index * 2];
            value = keyValueStore[index * 2 + 1];
            // Create column for key and column for value
            cells.add(new Cell(key));
            cells.add(new Cell(value));
            rows.add(new TableRow(cells));
        }
    }

    @Override
    public List<String> getColumnLabels() {
        ArrayList<String> output = new ArrayList<>();
        output.add("Key");
        output.add("Value");
        return output;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public ITableDocumentPart getTable() {
        // Get all rows
        return getTablePart(0, rows.size());
    }

    @Override
    public ITableDocumentPart getTablePart(int start, int count) {
        return new KeyValueStoreDocumentPart(start, rows.subList(start, start+count));
    }


    @Override
    public ICellCoordinates addressToCoordinates(String address) {
        return null;
    }

    @Override
    public String coordinatesToAddress(ICellCoordinates coordinates) {
        return null;
    }

    @Override
    public void dispose() {
        
    }

}
