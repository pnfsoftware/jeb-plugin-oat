package com.pnf.OATPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.pnf.OAT.DexFile;
import com.pnf.OAT.OATFile;
import com.pnfsoftware.jeb.core.actions.ActionContext;
import com.pnfsoftware.jeb.core.actions.IActionData;
import com.pnfsoftware.jeb.core.input.BytesInput;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.output.AbstractUnitRepresentation;
import com.pnfsoftware.jeb.core.output.IGenericDocument;
import com.pnfsoftware.jeb.core.output.IUnitFormatter;
import com.pnfsoftware.jeb.core.output.UnitFormatterAdapter;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.units.AbstractBinaryUnit;
import com.pnfsoftware.jeb.core.units.IBinaryFrames;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.util.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;




public class OATUnit extends AbstractBinaryUnit implements IInteractiveUnit {
    private static final ILogger logger = GlobalLog.getLogger(OATUnit.class);
    private OATFile oat;
    private byte[] data;

    public OATUnit(String name, IInput input, IUnitProcessor unitProcessor, IUnit parent, IPropertyDefinitionManager pdm) {
        super("", input, "OAT_file", name, unitProcessor, parent, pdm);
        try(InputStream stream = input.getStream()) {
            data = IO.readInputStream(stream);
        }
        catch(IOException e) {
            logger.catching(e);
        }

    }
    public OATUnit(IBinaryFrames serializedData, IUnitProcessor unitProcessor, IUnit parent, IPropertyDefinitionManager pdm) {
        super(serializedData, unitProcessor, parent, pdm);
    }

    @Override
    public boolean process() {
        oat = new OATFile(data);

        for(DexFile dex : oat.getDexFiles()) {
            children.add(unitProcessor.process("test" + dex, new BytesInput(dex.getBytes()), this));
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
            public IGenericDocument getDocument() {
                return new KeyValueStoreDocument(oat);
            }
        });
        return formatter;
    }

    // Currently actions are not supported, ignore all.
    @Override
    public boolean executeAction(ActionContext context, IActionData data) {
        return false;
    }

    @Override
    public boolean prepareExecution(ActionContext context, IActionData data) {
        return false;
    }
    @Override
    public boolean canExecuteAction(ActionContext context) {
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

