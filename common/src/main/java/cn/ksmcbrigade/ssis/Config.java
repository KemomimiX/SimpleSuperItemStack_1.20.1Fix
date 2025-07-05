package cn.ksmcbrigade.ssis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Config {
    public static JsonObject data = new JsonObject();
    public static File file = new File("config/"+SSISMod.MOD_ID+"-config.json");

    public static boolean init = false;

    public static void init() throws IOException {
        if(init) return;
        reload();
        init = true;
    }

    private static void createOrOverwrite(boolean overwrite) throws IOException {
        if(!file.exists() || overwrite){
            JsonObject object = new JsonObject();
            object.addProperty("max",64);
            FileUtils.writeStringToFile(file,object.toString());
        }
    }

    public static void reload() throws IOException {
        createOrOverwrite(false);
        if(file.exists()){
            data = JsonParser.parseString(FileUtils.readFileToString(file)).getAsJsonObject();
        }
    }

    public static int get(){
        return data.get("max").getAsInt();
    }

    public static void set(int max) throws IOException {
        data.addProperty("max",max);
        createOrOverwrite(true);
    }
}
