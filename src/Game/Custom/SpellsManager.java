package Game.Custom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpellsManager implements Serializable {
    private ArrayList<Spell> spellList;

    public SpellsManager() {
        this.spellList = new ArrayList<>();
    }

    public void addSpell(Spell spell) {
        if (!spell.getMagicCommand().contains("FUNC") && !spell.getMagicCommand().contains("NOTUSE")) {
            spellList.add(spell);
        }
    }

    public List<Spell> getSpellList() {
        return spellList;
    }

    public void setSpellList(ArrayList<Spell> spellList) {
        this.spellList = spellList;
    }
}
