package mc.ajneb97;

import mc.ajneb97.api.MineChessAPI;
import mc.ajneb97.config.ConfigsManager;
import mc.ajneb97.database.MySQLConnection;
import mc.ajneb97.listener.OtherListenerNew;
import mc.ajneb97.manager.*;
import mc.ajneb97.manager.editinventory.EditInventoryManager;
import mc.ajneb97.tasks.*;
import mc.ajneb97.utils.ServerVersion;
import mc.ajneb97.versions.NMSItemManager;
import mc.ajneb97.api.ExpansionMineChess;
import mc.ajneb97.command.MainCommand;
import mc.ajneb97.listener.EditInventoryListener;
import mc.ajneb97.listener.PlayerListener;
import mc.ajneb97.manager.inventory.InventoryManager;
import mc.ajneb97.model.internal.UpdateCheckerResult;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class MineChess extends JavaPlugin {
    PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();
    public static String prefix;
    public static ServerVersion serverVersion;

    private ConfigsManager configsManager;
    private MessagesManager messagesManager;
    private ArenaManager arenaManager;
    private BoardManager boardManager;
    private PlayerDataManager playerDataManager;
    private CommonItemManager commonItemManager;
    private InventoryManager inventoryManager;
    private NMSItemManager nmsItemManager;
    private DependencyManager dependencyManager;
    private EditInventoryManager editInventoryManager;

    private ScoreboardManager scoreboardManager;
    private SignManager signManager;
    private ArenaTpLobbyTask arenaTpLobbyTask;
    private ArenaParticlesTask arenaParticlesTask;
    private PlayerDataSaveTask playerDataSaveTask;

    private MySQLConnection mySQLConnection;
    private VerifyManager verifyManager;
    private UpdateCheckerManager updateCheckerManager;

    public void onEnable(){
        setVersion();
        setPrefix();
        this.playerDataManager = new PlayerDataManager(this);
        this.commonItemManager = new CommonItemManager(this);
        this.arenaManager = new ArenaManager(this);
        this.boardManager = new BoardManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.nmsItemManager = new NMSItemManager(this);
        this.dependencyManager = new DependencyManager(this);
        this.editInventoryManager = new EditInventoryManager(this);

        this.scoreboardManager = new ScoreboardManager(this);
        this.signManager = new SignManager(this);
        this.arenaTpLobbyTask = new ArenaTpLobbyTask(this);
        this.arenaParticlesTask = new ArenaParticlesTask(this);

        this.configsManager = new ConfigsManager(this);
        this.configsManager.configure();

        registerEvents();
        registerCommands();

        this.scoreboardManager.start();
        this.signManager.start();
        this.arenaTpLobbyTask.start();
        this.arenaParticlesTask.start();
        reloadPlayerDataSaveTask();
        this.boardManager.cleanArenas();

        MineChessAPI api = new MineChessAPI(this);
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new ExpansionMineChess(this).register();
        }

        this.verifyManager = new VerifyManager(this);
        this.verifyManager.verify();

        if(configsManager.getMainConfigManager().isMySQL()){
            mySQLConnection = new MySQLConnection(this);
            mySQLConnection.setupMySql();
        }

        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eThanks for using my plugin!   &f~Ajneb97"));

        updateCheckerManager = new UpdateCheckerManager(version);
        updateMessage(updateCheckerManager.check());
    }

    public void onDisable(){
        arenaManager.endAllArenasFromServerStop();
        configsManager.getPlayersConfigManager().saveConfigs();
        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eHas been disabled! &fVersion: "+version));
    }
    public void registerCommands(){
        this.getCommand("minechess").setExecutor(new MainCommand(this));
    }

    public void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new EditInventoryListener(this), this);
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)){
            pm.registerEvents(new OtherListenerNew(this),this);
        }
    }

    public void setPrefix(){
        prefix = MessagesManager.getColoredMessage("&6[&fMineChess&6] ");
    }

    public void setVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch(bukkitVersion){
            case "1.20.5":
            case "1.20.6":
                serverVersion = ServerVersion.v1_20_R4;
                break;
            case "1.21":
            case "1.21.1":
                serverVersion = ServerVersion.v1_21_R1;
                break;
            case "1.21.2":
            case "1.21.3":
                serverVersion = ServerVersion.v1_21_R2;
                break;
            case "1.21.4":
                serverVersion = ServerVersion.v1_21_R3;
                break;
            case "1.21.5":
                serverVersion = ServerVersion.v1_21_R4;
                break;
            case "1.21.6":
                serverVersion = ServerVersion.v1_21_R5;
                break;
            default:
                serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
        }
    }

    public void reloadPlayerDataSaveTask() {
        if(playerDataSaveTask != null) {
            playerDataSaveTask.end();
        }
        playerDataSaveTask = new PlayerDataSaveTask(this);
        playerDataSaveTask.start(configsManager.getMainConfigManager().getPlayerDataSave());
    }

    public ConfigsManager getConfigsManager() {
        return configsManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public FileConfiguration getMessagesConfig(){
        return configsManager.getMessagesConfigManager().getConfigFile().getConfig();
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BoardManager getBoardManager() {
        return boardManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public CommonItemManager getCommonItemManager() {
        return commonItemManager;
    }

    public NMSItemManager getNmsItemManager() {
        return nmsItemManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public EditInventoryManager getEditInventoryManager() {
        return editInventoryManager;
    }

    public MySQLConnection getMySQLConnection() {
        return mySQLConnection;
    }

    public VerifyManager getVerifyManager() {
        return verifyManager;
    }

    public UpdateCheckerManager getUpdateCheckerManager() {
        return updateCheckerManager;
    }

    public void updateMessage(UpdateCheckerResult result){
        if(!result.isError()){
            String latestVersion = result.getLatestVersion();
            if(latestVersion != null){
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cThere is a new version available. &e(&7"+latestVersion+"&e)"));
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &fhttps://modrinth.com/plugin/minechess-minigame"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &cError while checking update."));
        }

    }
}
