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

    @Override
    public int getFirstRowIndex() {
        return rowIndex;
    }

    @Override
    public List<TableRow> getRows() {
        return rows;
    }
}
