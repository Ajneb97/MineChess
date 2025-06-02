package mc.ajneb97.config;


import mc.ajneb97.MineChess;

public class ConfigsManager {

	private MineChess plugin;
	private MainConfigManager mainConfigManager;
	private MessagesConfigManager messagesConfigManager;
	private ArenasConfigManager arenasConfigManager;
	private InventoryConfigManager inventoryConfigManager;
	private PlayersConfigManager playersConfigManager;
	private SignsConfigManager signsConfigManager;
	
	public ConfigsManager(MineChess plugin) {
		this.plugin = plugin;
		this.mainConfigManager = new MainConfigManager(plugin);
		this.messagesConfigManager = new MessagesConfigManager(plugin);
		this.arenasConfigManager = new ArenasConfigManager(plugin);
		this.inventoryConfigManager = new InventoryConfigManager(plugin);
		this.signsConfigManager = new SignsConfigManager(plugin);
		this.playersConfigManager = new PlayersConfigManager(plugin,"players");
	}
	
	public void configure() {
		mainConfigManager.configure();
		messagesConfigManager.configure();
		arenasConfigManager.configure();
		inventoryConfigManager.configure();
		signsConfigManager.configure();
		playersConfigManager.configure();
	}

	public MainConfigManager getMainConfigManager() {
		return mainConfigManager;
	}

	public MessagesConfigManager getMessagesConfigManager() {
		return messagesConfigManager;
	}

	public ArenasConfigManager getArenasConfigManager() {
		return arenasConfigManager;
	}

	public PlayersConfigManager getPlayersConfigManager() {
		return playersConfigManager;
	}

	public InventoryConfigManager getInventoryConfigManager() {
		return inventoryConfigManager;
	}

	public SignsConfigManager getSignsConfigManager() {
		return signsConfigManager;
	}

	public boolean reload(){
		if(!messagesConfigManager.reloadConfig()){
			return false;
		}
		if(!mainConfigManager.reloadConfig()){
			return false;
		}
		if(!inventoryConfigManager.reloadConfig()){
			return false;
		}

		plugin.reloadPlayerDataSaveTask();
		plugin.getVerifyManager().verify();

		return true;
	}
}
