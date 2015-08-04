package com.pnf.OATPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pnf.OAT.DexFile;
import com.pnf.OAT.OATFile;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.actions.ActionContext;
import com.pnfsoftware.jeb.core.actions.IActionData;
import com.pnfsoftware.jeb.core.input.BytesInput;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.input.IInputLocationInformation;
import com.pnfsoftware.jeb.core.output.AbstractUnitRepresentation;
import com.pnfsoftware.jeb.core.output.IGenericDocument;
import com.pnfsoftware.jeb.core.output.IUnitFormatter;
import com.pnfsoftware.jeb.core.output.UnitFormatterAdapter;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.units.AbstractBinaryUnit;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.util.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.serialization.annotations.SerConstructor;

public class OATUnit extends AbstractBinaryUnit implements IInteractiveUnit {
    private static final ILogger logger = GlobalLog.getLogger(OATUnit.class);
    private OATFile oat;
    private byte[] data;
    private String status = "Unprocessed";

    public OATUnit(String name, IInput input, IUnitProcessor unitProcessor,
            IUnitCreator parent, IPropertyDefinitionManager pdm) {
        super(null, input, OATPlugin.TYPE, name, unitProcessor, parent, pdm);
        try (InputStream stream = input.getStream()) {
            data = IO.readInputStream(stream);
        } catch (IOException e) {
            logger.catching(e);
        }

    }

    @SerConstructor
    public OATUnit() {
    }

    @Override
    public boolean process() {
        if (isProcessed()) {
            return true;
        }
        oat = new OATFile(data);

        for (DexFile dex : oat.getDexFiles()) {
            children.add(unitProcessor.process(dex.getLocation(),
                    new BytesInput(dex.getBytes()), this));
        }
        processed = true;
        status = "Processed";

        return true;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getDescription() {
        // Put together info about the opened OAT file
        String output = "- Notes:\n";
        output += "  - " + "OAT Version: " + oat.getVersion() + "\n";
        output += "  - " + "Dex File Count: " + oat.getDexFileCount() + "\n";
        output += "  - " + "Dex File Paths:\n";
        for (DexFile dex : oat.getDexFiles()) {
            output += "    - " + dex.getLocation() + "\n";
        }
        return output;
    }

    @Override
    public boolean isProcessed() {
        return oat != null;
    }

    @Override
    public IUnitFormatter getFormatter() {
        UnitFormatterAdapter formatter = new UnitFormatterAdapter();

        // Add key value store view
        formatter.addDocumentPresentation(new AbstractUnitRepresentation(
                "Key Value Store", false) {
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

    @Override
    public String getComment(String address) {
        return null;
    }

    @Override
    public Map<String, String> getComments() {
        return null;
    }

    @Override
    public String getAddressLabel(String address) {
        return null;
    }

    @Override
    public Map<String, String> getAddressLabels() {
        return null;
    }

    @Override
    public String locationToAddress(IInputLocationInformation location) {
        return null;
    }

    @Override
    public IInputLocationInformation addressToLocation(String address) {
        return null;
    }
}
