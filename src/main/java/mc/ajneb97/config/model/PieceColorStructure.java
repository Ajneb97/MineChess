package mc.ajneb97.config.model;

import java.util.List;

public class PieceColorStructure {
    private List<String> blocks;
    private PieceHologramsValues hologramsValues;

    public PieceColorStructure(List<String> blocks, PieceHologramsValues hologramsValues) {
        this.blocks = blocks;
        this.hologramsValues = hologramsValues;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<String> blocks) {
        this.blocks = blocks;
    }

    public PieceHologramsValues getHologramsValues() {
        return hologramsValues;
    }

    public void setHologramsValues(PieceHologramsValues hologramsValues) {
        this.hologramsValues = hologramsValues;
    }
}
