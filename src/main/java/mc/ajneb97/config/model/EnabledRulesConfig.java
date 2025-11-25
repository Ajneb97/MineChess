package mc.ajneb97.config.model;

public class EnabledRulesConfig {

    private boolean endByMovementsWithoutProgressEnabled;
    private boolean endByInsufficientMaterialEnabled;

    public EnabledRulesConfig(boolean endByMovementsWithoutProgressEnabled, boolean endByInsufficientMaterialEnabled) {
        this.endByMovementsWithoutProgressEnabled = endByMovementsWithoutProgressEnabled;
        this.endByInsufficientMaterialEnabled = endByInsufficientMaterialEnabled;
    }

    public boolean isEndByMovementsWithoutProgressEnabled() {
        return endByMovementsWithoutProgressEnabled;
    }

    public void setEndByMovementsWithoutProgressEnabled(boolean endByMovementsWithoutProgressEnabled) {
        this.endByMovementsWithoutProgressEnabled = endByMovementsWithoutProgressEnabled;
    }

    public boolean isEndByInsufficientMaterialEnabled() {
        return endByInsufficientMaterialEnabled;
    }

    public void setEndByInsufficientMaterialEnabled(boolean endByInsufficientMaterialEnabled) {
        this.endByInsufficientMaterialEnabled = endByInsufficientMaterialEnabled;
    }
}
