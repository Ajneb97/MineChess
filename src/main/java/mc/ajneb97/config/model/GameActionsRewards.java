package mc.ajneb97.config.model;

import java.util.List;

public class GameActionsRewards {
    private boolean afterTeleport;
    private List<String> endByTimeActions;
    private List<String> endByPlayerTimeActions;
    private List<String> endByTimeTieActions;
    private List<String> endByStalemateTieActions;
    private List<String> endByCheckmateActions;
    private List<String> endByLeaveActions;

    public GameActionsRewards(boolean afterTeleport, List<String> endByTimeActions, List<String> endByPlayerTimeActions,
                              List<String> endByTimeTieActions, List<String> endByStalemateTieActions, List<String> endByCheckmateActions,
                              List<String> endByLeaveActions) {
        this.afterTeleport = afterTeleport;
        this.endByTimeActions = endByTimeActions;
        this.endByPlayerTimeActions = endByPlayerTimeActions;
        this.endByTimeTieActions = endByTimeTieActions;
        this.endByStalemateTieActions = endByStalemateTieActions;
        this.endByCheckmateActions = endByCheckmateActions;
        this.endByLeaveActions = endByLeaveActions;
    }

    public boolean isAfterTeleport() {
        return afterTeleport;
    }

    public void setAfterTeleport(boolean afterTeleport) {
        this.afterTeleport = afterTeleport;
    }

    public List<String> getEndByTimeActions() {
        return endByTimeActions;
    }

    public void setEndByTimeActions(List<String> endByTimeActions) {
        this.endByTimeActions = endByTimeActions;
    }

    public List<String> getEndByPlayerTimeActions() {
        return endByPlayerTimeActions;
    }

    public void setEndByPlayerTimeActions(List<String> endByPlayerTimeActions) {
        this.endByPlayerTimeActions = endByPlayerTimeActions;
    }

    public List<String> getEndByTimeTieActions() {
        return endByTimeTieActions;
    }

    public void setEndByTimeTieActions(List<String> endByTimeTieActions) {
        this.endByTimeTieActions = endByTimeTieActions;
    }

    public List<String> getEndByStalemateTieActions() {
        return endByStalemateTieActions;
    }

    public void setEndByStalemateTieActions(List<String> endByStalemateTieActions) {
        this.endByStalemateTieActions = endByStalemateTieActions;
    }

    public List<String> getEndByCheckmateActions() {
        return endByCheckmateActions;
    }

    public void setEndByCheckmateActions(List<String> endByCheckmateActions) {
        this.endByCheckmateActions = endByCheckmateActions;
    }

    public List<String> getEndByLeaveActions() {
        return endByLeaveActions;
    }

    public void setEndByLeaveActions(List<String> endByLeaveActions) {
        this.endByLeaveActions = endByLeaveActions;
    }
}
