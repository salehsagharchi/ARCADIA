package Game.Custom;

import java.io.Serializable;

public class Spell implements Serializable {
    private String magicCommand;
    private String description;
    private SpellOwner owner;

    public Spell(String magicCommand, String description, SpellOwner owner) {
        this.magicCommand = magicCommand;
        this.description = description;
        this.owner = owner;
    }

    public String getMagicCommand() {
        return magicCommand;
    }

    public void setMagicCommand(String magicCommand) {
        this.magicCommand = magicCommand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SpellOwner getOwner() {
        return owner;
    }

    public void setOwner(SpellOwner owner) {
        this.owner = owner;
    }
}
