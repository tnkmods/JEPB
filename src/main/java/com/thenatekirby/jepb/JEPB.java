package com.thenatekirby.jepb;

import com.thenatekirby.babel.core.MutableResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// ====---------------------------------------------------------------------------====

@Mod("jepb")
public class JEPB {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Logger getLogger() {
        return LOGGER;
    }

    public static final String MOD_ID = "jepb";
    public static final MutableResourceLocation MOD = new MutableResourceLocation(MOD_ID);

    public JEPB() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
