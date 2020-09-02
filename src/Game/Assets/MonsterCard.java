package Game.Assets;

import Game.BattleManaging.CanBeTarget;
import Game.Constants.Initializer_Statics;
import Game.Enums.*;
import Game.Exceptions.DeadPlayerException;
import Game.Exceptions.DeckIsEmptyException;
import Game.PlayerClasses.Player;
import Game.PlayerClasses.Target;
import MenuControllers.GeneralClasses.Menus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MonsterCard extends Card implements Cloneable, CanBeTarget, Serializable {
    private int defaultHP;
    private int defaultAP;
    private int HP;
    private int AP;
    private boolean defensive;
    private boolean nimble;

    private String spellDet;
    private String battleCryDet;
    private String willDet;
    private String description;
    private String magicCommand;
    private boolean usedSpell;
    private boolean canAttack;
    private MonsterType monsterType;
    private MonsterTribe tribe;

    private boolean sleeping;
    private DoAction doAction;

    public MonsterCard(MonsterTribe tribe, String name
            , int defaultHP, int defaultAP, int costMP
            , MonsterType monsterType, boolean defensive, boolean nimble
            , String battleCryDet, String spellDet, String willDet, String magicCommand, String description) {
        this(tribe, name, defaultHP, defaultAP, costMP, monsterType, defensive, nimble, battleCryDet, spellDet, willDet, magicCommand, description, calculatePrice(costMP, monsterType));
    }

    public MonsterCard(MonsterTribe tribe, String name
            , int defaultHP, int defaultAP, int costMP
            , MonsterType monsterType, boolean defensive, boolean nimble
            , String battleCryDet, String spellDet, String willDet, String magicCommand, String description, int price) {
        super(name, costMP, price);
        this.defaultHP = defaultHP;
        this.defaultAP = defaultAP;
        this.HP = defaultHP;
        this.AP = defaultAP;
        this.usedSpell = false;
        this.canAttack = true;
        this.defensive = defensive;
        this.nimble = nimble;
        this.spellDet = spellDet;
        this.battleCryDet = battleCryDet;
        this.willDet = willDet;
        this.description = description;
        this.magicCommand = magicCommand;
        this.monsterType = monsterType;
        this.tribe = tribe;
        setType(CardType.Monster);
    }

    public static int calculatePrice(int base, MonsterType type) {
        if (type == MonsterType.Normal) {
            return 300 * base;
        } else if (type == MonsterType.SpellCaster) {
            return 500 * base;
        } else if (type == MonsterType.General) {
            return 700 * base;
        } else {
            // Hero
            return 1000 * base;
        }

    }

    private boolean handleWasted(List<Target> targetList, Player player) {
        if (targetList == null) {
            player.getBattle().getFormCon().log(Menus.RED + "No Target. Such a waste ..." + Menus.RESET);
            return false;
        } else if (targetList.size() == 0) {
            player.getBattle().getFormCon().log(Menus.RED + "No Target. Such a waste ..." + Menus.RESET);
            return false;
        }
        return true;
    }

    public String executeMagicCommand(Player player, MonsterSpellType type) throws DeadPlayerException {
        if (!magicCommand.contains("*")) {
            return "";
        }
        if (type == MonsterSpellType.SpellByUser && magicCommand.split("\\*")[1].equals("")) {
            return "";
        }
        if (type == MonsterSpellType.BattleCry && magicCommand.split("\\*")[0].equals("")) {
            return "";
        }
        if (type == MonsterSpellType.Will && magicCommand.split("\\*")[2].equals("")) {
            return "";
        }

        if (type == MonsterSpellType.SpellByUser) {
            runMagicCommand(player, magicCommand.split("\\*")[1], spellDet);
            usedSpell = true;
        }
        if (type == MonsterSpellType.BattleCry) {
            runMagicCommand(player, magicCommand.split("\\*")[0], battleCryDet);
        }
        if (type == MonsterSpellType.Will) {
            runMagicCommand(player, magicCommand.split("\\*")[2], willDet);
        }

        return "";
    }

    private String runMagicCommand(Player player, String command, String detail) throws DeadPlayerException {
        Pattern addHP = Pattern.compile("\\+HP\\((\\d*)\\).*");
        Pattern reduceHP = Pattern.compile("-HP\\((\\d*)\\).*");
        Pattern addAP = Pattern.compile("\\+AP\\((\\d*)\\).*");
        Pattern reduceAP = Pattern.compile("-AP\\((\\d*)\\).*");
        Pattern fieldToGrave = Pattern.compile("GR.*");
        Pattern fieldToHand = Pattern.compile("HND.*");
        Pattern graveToHand = Pattern.compile("GHND.*");
        Pattern handToField = Pattern.compile("HDFL.*");
        Pattern deckToHand = Pattern.compile("DHND");


        for (String cmd : command.split(":")) {
            cmd = cmd.trim();
            if (cmd.equals("")) {
                continue;
            }

            if (Pattern.matches(addHP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.PlayField);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {
                            ((MonsterCard) oneT).addHP(val);
                        } else if (oneT instanceof Player) {
                            ((Player) oneT).addHP(val);
                        }
                    }
                }

            } else if (Pattern.matches(reduceHP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.PlayField);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {
                            ((MonsterCard) oneT).subtractHP(val, entry.getPlayer());

                        } else if (oneT instanceof Player) {
                            ((Player) oneT).subtractHP(val);

                        }
                    }
                }

            } else if (Pattern.matches(addAP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.PlayField);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {
                            ((MonsterCard) oneT).addAP(val);

                        }
                    }
                }

            } else if (Pattern.matches(reduceAP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.PlayField);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {
                            ((MonsterCard) oneT).subtractAP(val);

                        }
                    }
                }

            } else if (Pattern.matches(fieldToGrave.toString(), cmd)) {

                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.PlayField);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {

                            int slotIndx = entry.getPlayer().getBag().getMonsterField().findCardSlotByReference((MonsterCard) oneT);
                            if (slotIndx == -1) {
                                player.getBattle().getFormCon().log("ERROR");
                                return "";
                            }
                            ((MonsterCard) oneT).deleteFormFieldAndSendTOGraveyard(entry.getPlayer(), slotIndx);

                        } else if (oneT instanceof SpellCard) {

                            int slotIndx = entry.getPlayer().getBag().getSpellField().findCardSlotByReference((SpellCard) oneT);
                            if (slotIndx == -1) {
                                player.getBattle().getFormCon().log("ERROR");
                                return "";
                            }
                            ((SpellCard) oneT).deleteFormFieldAndSendTOGraveyard(entry.getPlayer(), slotIndx);

                        }
                    }
                }

            } else if (Pattern.matches(fieldToHand.toString(), cmd)) {

                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.PlayField);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {

                            int slotIndx = entry.getPlayer().getBag().getMonsterField().findCardSlotByReference((MonsterCard) oneT);
                            if (slotIndx == -1) {
                                player.getBattle().getFormCon().log("ERROR");
                                return "";
                            }
                            if (entry.getPlayer().getBag().getHand().addCard(((MonsterCard) oneT).clone()).equals("ok")) {
                                entry.getPlayer().getBag().getMonsterField().addCardToSlot(null, slotIndx, false);
                            } else {
                                player.getBattle().getFormCon().log(Menus.PURPLE + "Hand Is Full For " + oneT.getName() + " ." + Menus.RESET);
                            }

                        } else if (oneT instanceof SpellCard) {

                            int slotIndx = entry.getPlayer().getBag().getSpellField().findCardSlotByReference((SpellCard) oneT);
                            if (slotIndx == -1) {
                                player.getBattle().getFormCon().log("ERROR");
                                return "";
                            }
                            if (entry.getPlayer().getBag().getHand().addCard(((SpellCard) oneT).clone()).equals("ok")) {
                                entry.getPlayer().getBag().getSpellField().addCardToSlot(false, null, slotIndx, false);
                            } else {
                                player.getBattle().getFormCon().log(Menus.PURPLE + "Hand Is Full For " + oneT.getName() + " ." + Menus.RESET);
                            }

                        }
                    }
                }

            } else if (Pattern.matches(graveToHand.toString(), cmd)) {

                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.GraveYard);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard || oneT instanceof SpellCard) {

                            String tempName = entry.getPlayer().getBag().getGraveyard().getCardAndDeleteByName(oneT.getName());

                            if (tempName.contains("NULL")) {
                                player.getBattle().getFormCon().log("ERROR");
                                return "";
                            }

                            if (!entry.getPlayer().getBag().getHand().addCard(Initializer_Statics.getCloneOfCard(tempName)).equals("ok")) {
                                player.getBattle().getFormCon().log(Menus.PURPLE + "Hand Is Full For " + oneT.getName() + " ." + Menus.RESET);
                            }

                        }

                    }
                }

            } else if (Pattern.matches(deckToHand.toString(), cmd)) {
                String message = "\n";
                message += Menus.CYAN + "\"" + getName() + "\" has cast a spell:";
                message += "\n" + detail + Menus.RESET + "\n";
                player.getBattle().getFormCon().log(message);
                try {
                    String name = player.getBag().getDeck().getOneCardAndDelete();
                    if (!player.getBag().getHand().addCard(Initializer_Statics.getCloneOfCard(name)).equals("ok")) {
                        player.getBattle().getFormCon().log(Menus.PURPLE + "Hand Is Full For " + name + " ." + Menus.RESET);
                    }
                } catch (DeckIsEmptyException ex) {
                    player.getBattle().getFormCon().log(Menus.PURPLE + "Deck Is Empty" + Menus.RESET);
                }

            } else if (Pattern.matches(handToField.toString(), cmd)) {
                // "Call to Arms: Select and move a card from hand to play field"

                ArrayList<Target> targetList = generateTargets(cmd, player, detail, FieldType.Hand);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {

                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard || oneT instanceof SpellCard) {

                            int toSlot;

                            if (oneT instanceof MonsterCard) {
                                toSlot = entry.getPlayer().getBag().getMonsterField().findFirstEmptySlotIndex();
                            } else {
                                toSlot = entry.getPlayer().getBag().getSpellField().findFirstEmptySlotIndex();
                            }

                            String res;

                            if (toSlot != -1) {

                                res = entry.getPlayer().getBag().getHand().drawCardTo(
                                        entry.getPlayer(),
                                        (entry.getPlayer().getBag().getHand().findCardSlotByReference((Card) oneT)),
                                        (toSlot));
                                res += "\n";

                            } else {
                                res = "ERROR : Your PlayField isn't Empty!";
                            }


                            if (res.startsWith("ERROR")) {
                                res = Menus.RED + res + Menus.RESET;
                            } else {
                                res = Menus.CYAN + res + Menus.RESET;
                            }
                            player.getBattle().getFormCon().log(res);

                        }

                    }


                }


            } else if (cmd.equals("FUNC1")) {
                // "Sacrifice: Select and move a card from hand to graveyard"

                ArrayList<Target> targetList = generateTargets(cmd + "?fmon-fspl", player, detail, FieldType.Hand);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard || oneT instanceof SpellCard) {

                            int handIndex = entry.getPlayer().getBag().getHand().findCardSlotByReference((Card) oneT);
                            String name = oneT.getName();

                            if (handIndex == -1) {
                                player.getBattle().getFormCon().log("ERROR");
                                return "";
                            }

                            entry.getPlayer().getBag().getHand().removeCard(handIndex);
                            entry.getPlayer().getBag().getGraveyard().addCard(Initializer_Statics.getCloneOfCard(name));


                        }
                    }
                }

            } else {
                player.getBattle().getFormCon().log("Cannot Recognize Magic Command : " + cmd);
            }
        }

        player.getBattle().getFormCon().showPopup(detail, false, 4000);

        return "";
    }

    public void deleteFormFieldAndSendTOGraveyard(Player player, int slotOfMe) {
        if (getMonsterType() == MonsterType.General || getMonsterType() == MonsterType.Hero) {
            try {
                executeMagicCommand(player, MonsterSpellType.Will);
            } catch (DeadPlayerException e) {
                return;
            }
        }
        player.getBag().getGraveyard().addCard(this.clone());
        try {
            player.getBag().getMonsterField().addCardToSlot(null, slotOfMe, false);
        } catch (DeadPlayerException e) {

        }
        player.getBattle().getFormCon().animateCard(this, player.getBag().getMonsterField(), player.getBag().getGraveyard(), slotOfMe, 0, null);
        player.getBattle().setupForAnimate(this, (player == player.getBattle().getHumanPlayer() ? "me" : "enemy") + "-mon", (player == player.getBattle().getHumanPlayer() ? "me" : "enemy") + "-grave", slotOfMe, 0);
        player.getBattle().updateAndSendToServer(true);
    }

    @Override
    public String getSummaryInfo() {
        return getSummaryInfo(true);
    }

    public String getSummaryInfo(boolean showColor) {
        String res = "";
        res += "HP: ";
        if (showColor) {
            if (getHP() > getDefaultHP()) {
                res += Menus.GREEN + getHP() + Menus.RESET;
            } else if (getHP() < getDefaultHP()) {
                res += Menus.RED + getHP() + Menus.RESET;
            } else {
                res += getHP();
            }
        } else {
            res += getHP();
        }
        res += " , AP: ";
        if (showColor) {
            if (getAP() > getDefaultAP()) {
                res += Menus.GREEN + getAP() + Menus.RESET;
            } else if (getAP() < getDefaultAP()) {
                res += Menus.RED + getAP() + Menus.RESET;
            } else {
                res += getAP();
            }
        } else {
            res += getAP();
        }

        if (isDefensive()) {
            res += " [Defensive]";
        }
        if (isNimble()) {
            res += " [Nimble]";
        }
        res += " [" + getMonsterType().toString() + "]";
        if (!spellDet.equals("")) {
            if (!usedSpell) {
                res += " [HasSpell]";
            } else {
                res += " [UsedSpell]";
            }
        }
        return res;
    }

    public void addHP(int val) {
        setHP(getHP() + val);
    }

    public void subtractHP(int val, Player player) {
        setHP(getHP() - val);
        if (getHP() <= 0) {
            setHP(0);
            player.getBattle().getFormCon().log(Menus.PURPLE + "\n" + player.getName() + "'s Card : \"" + getName() + "\" was Dead!\n" + Menus.RESET);
            int slotIndx = player.getBag().getMonsterField().findCardSlotByReference(this);
            if (slotIndx == -1) {
                player.getBattle().getFormCon().log("ERROR");
                return;
            }
            deleteFormFieldAndSendTOGraveyard(player, slotIndx);
        }
    }

    public void addAP(int val) {
        setAP(getAP() + val);
    }

    public void subtractAP(int val) {
        setAP(getAP() - val);
        if (getAP() < 0) {
            setAP(0);
        }
    }

    public DoAction getDoAction() {
        return doAction;
    }

    public void setDoAction(DoAction doAction) {
        this.doAction = doAction;
    }

    @Override
    public String toString() {
        String res = "";
        //"Card Name" Info:
        //Name: "Card Name"
        //HP: #DefaultHP
        //AP: #DefaultAP
        //MP cost: #DefaultMPCost
        //Card Type: [Normal / SpellCaster / General / Hero]
        //Card Tribe: [Elves / Atlantian / DragonBreed]
        //Is Defensive: [True / False]
        //Is Nimble: [True / False]
        //[Spell Details] (If SpellCaster or Hero)
        //[BattleCry Details] (If General or Hero)
        //[Will Details] (If General or Hero)
        //(OPTIONAL)Description: [Card Story]
        res += "\"" + getName() + "\" Info:";
        res += "\nName: \"" + getName() + "\"";
        res += "\nHP: " + getDefaultHP();
        res += "\nAP: " + getDefaultAP();
        res += "\nMP cost: " + getCostMP();
        res += "\nCard Type: " + getMonsterType().name();
        res += "\nCard Tribe: " + getTribe().name();
        res += "\nIs Defensive: " + isDefensive();
        res += "\nIs Nimble: " + isNimble();
        if (!getSpellDet().equals("")) {
            res += "\nSpell Details: \n" + getSpellDet();
        }
        if (!getBattleCryDet().equals("")) {
            res += "\nBattleCry Details: \n" + getBattleCryDet();
        }
        if (!getWillDet().equals("")) {
            res += "\nWill Details: \n" + getWillDet();
        }
        if (!getDescription().equals("")) {
            res += "\nDescription: \n" + getDescription();
        }
        return res.trim();
    }

    public boolean isCanAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean isUsedSpell() {
        return usedSpell;
    }

    public void setUsedSpell(boolean usedSpell) {
        this.usedSpell = usedSpell;
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    public int getDefaultHP() {
        return defaultHP;
    }

    public void setDefaultHP(int defaultHP) {
        this.defaultHP = defaultHP;
    }

    public int getDefaultAP() {
        return defaultAP;
    }

    public void setDefaultAP(int defaultAP) {
        this.defaultAP = defaultAP;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getAP() {
        return AP;
    }

    public void setAP(int AP) {
        this.AP = AP;
    }

    public boolean isDefensive() {
        return defensive;
    }

    public void setDefensive(boolean defensive) {
        this.defensive = defensive;
    }

    public boolean isNimble() {
        return nimble;
    }

    public void setNimble(boolean nimble) {
        this.nimble = nimble;
    }

    public String getSpellDet() {
        return spellDet;
    }

    public void setSpellDet(String spellDet) {
        this.spellDet = spellDet;
    }

    public String getBattleCryDet() {
        return battleCryDet;
    }

    public void setBattleCryDet(String battleCryDet) {
        this.battleCryDet = battleCryDet;
    }

    public String getWillDet() {
        return willDet;
    }

    public void setWillDet(String willDet) {
        this.willDet = willDet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMagicCommand() {
        return magicCommand;
    }

    public void setMagicCommand(String magicCommand) {
        this.magicCommand = magicCommand;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(MonsterType monsterType) {
        this.monsterType = monsterType;
    }

    public MonsterTribe getTribe() {
        return tribe;
    }

    public void setTribe(MonsterTribe tribe) {
        this.tribe = tribe;
    }

    @Override
    public MonsterCard clone() {
        MonsterCard temp = new MonsterCard(getTribe(), getName(), getDefaultHP(), getDefaultAP(), getCostMP(), getMonsterType(), isDefensive(), isNimble(), getBattleCryDet(), getSpellDet(), getWillDet(), getMagicCommand(), getDescription());
        temp.setDoAction(null);
        return temp;
    }
}
