package com.pnf.OAT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.pnfsoftware.jeb.core.actions.InformationForActionExecution;
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
        return true;
    }

    @Override
    public IBinaryFrames serialize() {
        return null;
    }
    @Override
    public IUnitFormatter getFormatter() {
        return new UnitFormatterAdapter();
    }

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

    public String readStringFromBytes(byte[] data, int offset) {
        String output = "";
        int index=0;
        while(offset + index < data.length) {
            if(data[offset + index] == 0) {
                break;
            }
            output = output + (char)data[offset + index];
            index++;
        }
        return output;
    }
    private int readLInt(byte[] data, int offset) {
        return ByteBuffer.wrap(new byte[]{data[offset+3], data[offset+2], data[offset+1], data[offset]}).getInt();
    } 
    private short readLShort(byte[] data, int offset) {
        return ByteBuffer.wrap(new byte[]{data[offset+1], data[offset]}).getShort();
    }
}

