package com.pnf.OATPlugin;


import com.pnfsoftware.jeb.core.PluginInformation;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.AbstractUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IBinaryFrames;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;




public class OATPlugin extends AbstractUnitIdentifier {
    private static final ILogger logger = GlobalLog.getLogger(OATPlugin.class);

    public OATPlugin() {
        super("OAT_file", 0);
    }

    @Override
    public PluginInformation getPluginInformation() {
        return new PluginInformation("OAT File Unit", "", "1.0", "PNF Software");
    }

    @Override
    public void initialize(IPropertyDefinitionManager parent, IPropertyManager pm) {
        super.initialize(parent, pm);
    }
    

    @Override
    public boolean canIdentify(IInput input, IUnit parent) {
        return checkBytes(input, 0, (byte)'o', (byte)'a', (byte)'t', (byte)'\n');
    }
    @Override
    public IUnit prepare(String name, IInput input, IUnitProcessor unitProcessor, IUnit parent) {
        OATUnit unit = new OATUnit(name, input, unitProcessor, parent, pdm);
        unit.process();
        return unit;
    }

    // No support for saving yet
    @Override
    public IUnit reload(IBinaryFrames serializedData, IUnitProcessor unitProcessor, 
            IUnit parent) {
        return null;
    }

} 
