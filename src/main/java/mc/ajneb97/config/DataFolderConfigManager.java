package mc.ajneb97.config;

import mc.ajneb97.MineChess;
import mc.ajneb97.config.model.CommonConfig;

import java.io.File;

public abstract class DataFolderConfigManager {
    protected String folderName;
    protected MineChess plugin;

    public DataFolderConfigManager(MineChess plugin, String folderName){
        this.plugin = plugin;
        this.folderName = folderName;
    }

    public void configure() {
        createFolder();
        loadConfigs();
    }

    public void createFolder(){
        File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + folderName);
            if(!folder.exists()){
                folder.mkdirs();
                createFiles();
            }
        } catch(SecurityException e) {
            folder = null;
        }
    }

    public CommonConfig getConfigFile(String pathName) {
        CommonConfig commonConfig = new CommonConfig(pathName, plugin, folderName, true);
        commonConfig.registerConfig();
        return commonConfig;
    }

    public abstract void createFiles();

    public abstract void loadConfigs();

    public abstract void saveConfigs();
}
