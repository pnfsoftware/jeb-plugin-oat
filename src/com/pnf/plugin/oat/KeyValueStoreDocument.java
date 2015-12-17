/*
 * JEB Copyright PNF Software, Inc.
 * 
 *     https://www.pnfsoftware.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pnf.plugin.oat;

import java.util.ArrayList;
import java.util.List;

import com.pnf.plugin.oat.internal.OATFile;
import com.pnfsoftware.jeb.core.events.JebEventSource;
import com.pnfsoftware.jeb.core.output.table.ICellCoordinates;
import com.pnfsoftware.jeb.core.output.table.ITableDocument;
import com.pnfsoftware.jeb.core.output.table.ITableDocumentPart;
import com.pnfsoftware.jeb.core.output.table.impl.Cell;
import com.pnfsoftware.jeb.core.output.table.impl.TableRow;

/**
 * View of the keyvalue store in the OAT's header.
 * 
 */
public class KeyValueStoreDocument extends JebEventSource implements ITableDocument {
    List<TableRow> rows;
    OATFile oat;

    public KeyValueStoreDocument(OATFile oat) {
        this.oat = oat;
        rows = new ArrayList<>();

        String[] keyValueStore = oat.getKeyValueStore().split("\0");
        String key;
        String value;
        List<Cell> cells = new ArrayList<>();
        for(int index = 0; index < keyValueStore.length / 2; index++) {
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
        return new KeyValueStoreDocumentPart(start, rows.subList(start, start + count));
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
