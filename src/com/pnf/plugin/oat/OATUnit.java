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

import java.io.IOException;
import java.io.InputStream;

import com.pnf.plugin.oat.internal.DexFile;
import com.pnf.plugin.oat.internal.OATFile;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.input.BytesInput;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.output.AbstractUnitRepresentation;
import com.pnfsoftware.jeb.core.output.IGenericDocument;
import com.pnfsoftware.jeb.core.output.IUnitFormatter;
import com.pnfsoftware.jeb.core.output.UnitFormatterUtil;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.units.AbstractInteractiveBinaryUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.serialization.annotations.Ser;
import com.pnfsoftware.jeb.util.serialization.annotations.SerId;

@Ser
public class OATUnit extends AbstractInteractiveBinaryUnit {
    private static final ILogger logger = GlobalLog.getLogger(OATUnit.class);

    @SerId(1)
    private OATFile oat;

    public OATUnit(String name, IInput input, IUnitProcessor unitProcessor, IUnitCreator parent,
            IPropertyDefinitionManager pdm) {
        super(null, input, OATPlugin.TYPE, name, unitProcessor, parent, pdm);
    }

    @Override
    public boolean isProcessed() {
        return oat != null;
    }

    @Override
    public boolean process() {
        if(isProcessed()) {
            return true;
        }

        try(InputStream stream = getInput().getStream()) {
            byte[] data = IO.readInputStream(stream);

            oat = new OATFile(data);

            for(DexFile dex: oat.getDexFiles()) {
                addChild(getUnitProcessor().process(dex.getLocation(), new BytesInput(dex.getBytes()), this));
            }
        }
        catch(IOException e) {
            logger.catching(e);
            return false;
        }

        setProcessed(true);
        return true;
    }

    @Override
    public String getDescription() {
        String output = super.getDescription();
        output += "\nOAT information:\n";
        output += "- Version: " + oat.getVersion() + "\n";
        output += "- Target ISA: " + oat.getISAString() + "\n";
        output += "- Dex file count: " + oat.getDexFileCount() + "\n";
        output += "- Dex paths:\n";
        for(DexFile dex: oat.getDexFiles()) {
            output += "   - " + dex.getLocation() + "\n";
        }
        return output;
    }

    @Override
    public IUnitFormatter getFormatter() {
        IUnitFormatter formatter = super.getFormatter();
        if(UnitFormatterUtil.getPresentationByIdentifier(formatter, 1) == null) {
            formatter.addPresentation(new AbstractUnitRepresentation(1, "OAT KV-Store", true) {
                @Override
                public IGenericDocument getDocument() {
                    return new KeyValueStoreDocument(oat);
                }
            }, false);
        }
        return formatter;
    }
}
