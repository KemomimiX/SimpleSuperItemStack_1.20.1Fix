package cn.ksmcbrigade.ssis;

import java.io.IOException;

public final class SSISMod {
    public static final String MOD_ID = "ssis";

    public static void init() {
        try{
            Config.init();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

    }
}
