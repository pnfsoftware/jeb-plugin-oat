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

import java.nio.ByteBuffer;

import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.PluginInformation;
import com.pnfsoftware.jeb.core.Version;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.units.AbstractUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;

/**
 * Android OAT 
 * 
 * @author PNF Software
 *
 */
public class OATPlugin extends AbstractUnitIdentifier {
    static final String TYPE = "OAT";

    public OATPlugin() {
        super(TYPE, 0);
    }

    @Override
    public PluginInformation getPluginInformation() {
        return new PluginInformation("OAT File Unit",
                "Plugin to extract Dex files embedded in compiled OAT files that are generated by the ART",
                "PNF Software", Version.create(1, 0, 13));
    }

    @Override
    public void initialize(IPropertyDefinitionManager parent) {
        super.initialize(parent);
    }

    @Override
    public boolean canIdentify(IInput input, IUnitCreator parent) {
        ByteBuffer hdr = input.getHeader();
        if(hdr == null) {
            return false;
        }
        if(input.getCurrentSize() < 0x20) {
            return false;
        }
        if(!checkBytes(input, 0, (byte)'o', (byte)'a', (byte)'t', (byte)'\n')) {
            return false;
        }
        if(hdr.get(7) != 0) {
            return false;
        }
        int v1 = hdr.get(4);
        int v2 = hdr.get(5);
        int v3 = hdr.get(6);
        return v1 >= '0' && v1 <= '9' && v2 >= '0' && v2 <= '9' && v3 >= '0' && v3 <= '9';
    }

    @Override
    public IUnit prepare(String name, IInput input, IUnitProcessor unitProcessor, IUnitCreator parent) {
        OATUnit unit = new OATUnit(name, input, unitProcessor, parent, pdm);
        return unit;
    }
}
