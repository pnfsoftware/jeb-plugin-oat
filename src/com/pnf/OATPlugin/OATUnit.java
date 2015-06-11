package com.pnf.OATPlugin;

import java.util.ArrayList;
import java.util.List;

import com.pnf.OAT.DexFile;
import com.pnf.OAT.OATFile;
import com.pnfsoftware.jeb.core.actions.InformationForActionExecution;
import com.pnfsoftware.jeb.core.output.AbstractUnitRepresentation;
import com.pnfsoftware.jeb.core.output.IInfiniDocument;
import com.pnfsoftware.jeb.core.output.IUnitFormatter;
import com.pnfsoftware.jeb.core.output.UnitFormatterAdapter;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.units.AbstractBinaryUnit;
import com.pnfsoftware.jeb.core.units.IBinaryFrames;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;




public class OATUnit extends AbstractBinaryUnit implements IInteractiveUnit {
    private static final ILogger logger = GlobalLog.getLogger(OATUnit.class);
    private OATFile oat;

    public OATUnit(String name, byte[] data, IUnitProcessor unitProcessor, IUnit parent, IPropertyDefinitionManager pdm) {
        super("", data, "OAT_file", name, unitProcessor, parent, pdm);
    }
    public OATUnit(IBinaryFrames serializedData, IUnitProcessor unitProcessor, IUnit parent, IPropertyDefinitionManager pdm) {
        super(serializedData, unitProcessor, parent, pdm);
    }

    @Override
    public boolean process() {
        oat = new OATFile(data);

        for(DexFile dex : oat.getDexFiles()) {
            children.add(unitProcessor.process("test" + dex, dex.getBytes(), this));
        }
        processed = true;
        return true;
    }


    @Override
    public String getDescription() {
        return super.getDescription() + getNotes();
    }
    @Override
    public String getNotes() {
        // Put together info about the opened OAT file
        String output = "- Notes:\n";
        output += "  - " + "OAT Version: " + oat.getVersion() + "\n";
        output += "  - " + "Dex File Count: " + oat.getDexFileCount() + "\n";

        output += "  - " + "Dex File Paths:\n";
        for(DexFile dex : oat.getDexFiles()) {
            output += "    - " + dex.getLocation() + "\n";
        }
        output += "  - " + "Key Value Store:\n";
        String[] keyValueStore = oat.getKeyValueStore().split("\0");
        String key;
        String value;
        for(int index=0; index < keyValueStore.length / 2; index++) {
            key = keyValueStore[index * 2];
            value = keyValueStore[index * 2 + 1];
            output += "    - " + key + " : " + value + "\n";
        }
        return output;
    }


    // No support for saving
    @Override
    public IBinaryFrames serialize() {
        return null;
    }

    @Override
    public IUnitFormatter getFormatter() {
        UnitFormatterAdapter formatter = new UnitFormatterAdapter();

        // Add key value store view
        formatter.addDocumentPresentation(new AbstractUnitRepresentation("Key Value Store", false) {
            @Override
            public IInfiniDocument getDocument() {
                return new KeyValueStoreDocument(oat);
            }
        });
        return formatter;
    }

    // Currently actions are not supported, ignore all.
    @Override
    public boolean executeAction(InformationForActionExecution info) {
        return false;
    }

    @Override
    public boolean prepareExecution(InformationForActionExecution info) {
        return false;
    }

    @Override
    public List<Integer> getItemActions(long id) {
        return new ArrayList<>();
    }

    @Override
    public long getItemAtAddress(String address) {
        
        return 1L;
    }
    @Override
    public String getAddressOfItem(long id) {
        return null;
    }
    @Override
    public List<Integer> getGlobalActions() {
        return new ArrayList<>();
    }
    @Override
    public List<Integer> getAddressActions(String address) {
        return new ArrayList<>();
    }

}

