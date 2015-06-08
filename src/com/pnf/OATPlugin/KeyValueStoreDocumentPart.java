package com.pnf.OATPlugin;

import java.util.ArrayList;
import java.util.List;

import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
import com.pnfsoftware.jeb.core.output.table.impl.TableRow;

public class KeyValueStoreDocumentPart implements ITableDocumentPart {
    private ArrayList<TableRow> rows;
    private int rowIndex;

    public KeyValueStoreDocumentPart(int rowIndex, List<TableRow> rows) {
        this.rowIndex = rowIndex;
        this.rows = new ArrayList<>(rows);
    }

    public int getFirstRowIndex() {
        return rowIndex;
    }

    public List<TableRow> getRows() {
        return rows;
    }
}
