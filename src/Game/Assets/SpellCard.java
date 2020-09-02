package Game.Assets;

import Game.BattleManaging.CanBeTarget;
import Game.Constants.Initializer_Statics;
import Game.Enums.CardType;
import Game.Enums.FieldType;
import Game.Enums.SpellType;
import Game.Exceptions.DeadPlayerException;
import Game.Exceptions.DeckIsEmptyException;
import Game.PlayerClasses.Player;
import Game.PlayerClasses.Target;
import MenuControllers.GeneralClasses.Menus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SpellCard extends Card implements Cloneable, CanBeTarget, Serializable {
    private String spellDet;
    private String description;
    private String magicCommand;
    private SpellType spellType;

    private DoAction doAction;
    private ArrayList<CanBeTarget> underMySpell;
    private boolean firstSpell;

    public SpellCard(String name, int costMP, SpellType spellType, String spellDet, String magicCommand, String description) {
        this(name, costMP, spellType, spellDet, magicCommand, description, calculatePrice(costMP));
    }

    public SpellCard(String name, int costMP, SpellType spellType, String spellDet, String magicCommand, String description, int price) {
        super(name, costMP, price);

        this.spellDet = spellDet;
        this.description = description;
        this.magicCommand = magicCommand;
        this.spellType = spellType;
        underMySpell = new ArrayList<>();
        firstSpell = true;
        setType(CardType.Spell);
    }

    public static int calculatePrice(int base) {
        return 700 * base;
    }

    private boolean handleWasted(List<Target> targetList, Player player) {
        if (targetList == null) {
            if (spellType == SpellType.Instant) {
                player.getBattle().getFormCon().log(Menus.RED + "No Target. Such a waste ..." + Menus.RESET);
            }
            return false;
        } else if (targetList.size() == 0) {
            if (spellType == SpellType.Instant) {
                player.getBattle().getFormCon().log(Menus.RED + "No Target. Such a waste ..." + Menus.RESET);
            }
            return false;
        }
        return true;
    }

    public String executeMagicCommand(Player player) throws DeadPlayerException {
        Pattern addHP = Pattern.compile("\\+HP\\((\\d*)\\).*");
        Pattern reduceHP = Pattern.compile("-HP\\((\\d*)\\).*");
        Pattern addAP = Pattern.compile("\\+AP\\((\\d*)\\).*");
        Pattern reduceAP = Pattern.compile("-AP\\((\\d*)\\).*");
        Pattern fieldToGrave = Pattern.compile("GR.*");
        Pattern fieldToHand = Pattern.compile("HND.*");
        Pattern deckToHand = Pattern.compile("DHND");
        Pattern graveToHand = Pattern.compile("GHND.*");
        Pattern handToField = Pattern.compile("HDFL.*");

        for (String cmd : magicCommand.split(":")) {
            cmd = cmd.trim();
            if (cmd.equals("")) {
                continue;
            }

            if (Pattern.matches(addHP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.PlayField, firstSpell);
                if (handleWasted(targetList, player)) {

                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {


                            if (spellType == SpellType.Aura) {
                                if (!underMySpell.contains(oneT)) {
                                    ((MonsterCard) oneT).addHP(val);
                                    underMySpell.add(oneT);
                                    ((MonsterCard) oneT).setDoAction(new DoAction() {
                                        @Override
                                        public void run(CanBeTarget target) {
                                            ((MonsterCard) target).subtractHP(val, entry.getPlayer());
                                        }
                                    });
                                }
                            } else {
                                ((MonsterCard) oneT).addHP(val);
                            }

                        } else if (oneT instanceof Player) {


                            if (spellType == SpellType.Aura) {
                                if (!underMySpell.contains(oneT)) {
                                    ((Player) oneT).addHP(val);
                                    underMySpell.add(oneT);
                                    ((Player) oneT).setDoAction(new DoAction() {
                                        @Override
                                        public void run(CanBeTarget target) throws DeadPlayerException {
                                            ((Player) target).subtractHP(val);
                                        }
                                    });
                                }

                            } else {
                                ((Player) oneT).addHP(val);
                            }
                        }
                    }
                }

            } else if (Pattern.matches(reduceHP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.PlayField, firstSpell);
                if (handleWasted(targetList, player)) {
                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {


                            if (spellType == SpellType.Aura) {
                                if (!underMySpell.contains(oneT)) {
                                    ((MonsterCard) oneT).subtractHP(val, entry.getPlayer());
                                    underMySpell.add(oneT);
                                    ((MonsterCard) oneT).setDoAction(new DoAction() {
                                        @Override
                                        public void run(CanBeTarget target) {
                                            ((MonsterCard) target).addHP(val);
                                        }
                                    });
                                }
                            } else {
                                ((MonsterCard) oneT).subtractHP(val, entry.getPlayer());
                            }

                        } else if (oneT instanceof Player) {


                            if (spellType == SpellType.Aura) {
                                if (!underMySpell.contains(oneT)) {
                                    ((Player) oneT).subtractHP(val);
                                    underMySpell.add(oneT);
                                    ((Player) oneT).setDoAction(new DoAction() {
                                        @Override
                                        public void run(CanBeTarget target) {
                                            ((Player) target).addHP(val);
                                        }
                                    });
                                }
                            } else {
                                ((Player) oneT).subtractHP(val);
                            }

                        }
                    }
                }

            } else if (Pattern.matches(addAP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.PlayField, firstSpell);
                if (handleWasted(targetList, player)) {
                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {
                            if (spellType == SpellType.Aura) {
                                if (!underMySpell.contains(oneT)) {
                                    ((MonsterCard) oneT).addAP(val);
                                    underMySpell.add(oneT);
                                    ((MonsterCard) oneT).setDoAction(new DoAction() {
                                        @Override
                                        public void run(CanBeTarget target) {
                                            ((MonsterCard) target).subtractAP(val);
                                        }
                                    });
                                }
                            } else {
                                ((MonsterCard) oneT).addAP(val);
                            }
                        }
                    }
                }

            } else if (Pattern.matches(reduceAP.toString(), cmd)) {

                int val = getNumber(cmd);
                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.PlayField, firstSpell);
                if (handleWasted(targetList, player)) {
                    for (Target entry : targetList) {
                        CanBeTarget oneT = entry.getObject();
                        if (oneT instanceof MonsterCard) {


                            if (spellType == SpellType.Aura) {
                                if (!underMySpell.contains(oneT)) {

                                    ((MonsterCard) oneT).subtractAP(val);
                                    underMySpell.add(oneT);
                                    ((MonsterCard) oneT).setDoAction(new DoAction() {
                                        @Override
                                        public void run(CanBeTarget target) {
                                            ((MonsterCard) target).addAP(val);
                                        }
                                    });

                                }
                            } else {
                                ((MonsterCard) oneT).subtractAP(val);
                            }
                        }
                    }
                }

            } else if (Pattern.matches(fieldToGrave.toString(), cmd)) {

                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.PlayField, firstSpell);
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

                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.PlayField, firstSpell);
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

            } else if (Pattern.matches(deckToHand.toString(), cmd)) {
                String message = "\n";
                message += Menus.CYAN + "\"" + getName() + "\" has cast a spell:";
                message += "\n" + getSpellDet() + Menus.RESET + "\n";
                player.getBattle().getFormCon().log(message);
                try {
                    String name = player.getBag().getDeck().getOneCardAndDelete();
                    if (!player.getBag().getHand().addCard(Initializer_Statics.getCloneOfCard(name)).equals("ok")) {
                        player.getBattle().getFormCon().log(Menus.PURPLE + "Hand Is Full For " + name + " ." + Menus.RESET);
                    }
                } catch (DeckIsEmptyException ex) {
                    player.getBattle().getFormCon().log(Menus.PURPLE + "Deck Is Empty" + Menus.RESET);
                }


            } else if (Pattern.matches(graveToHand.toString(), cmd)) {

                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.GraveYard, firstSpell);
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

            } else if (Pattern.matches(handToField.toString(), cmd)) {
                // "Call to Arms: Select and move a card from hand to play field"

                ArrayList<Target> targetList = generateTargets(cmd, player, getSpellDet(), FieldType.Hand, firstSpell);
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


            } else {
                player.getBattle().getFormCon().log("Cannot Recognize Magic Command : " + cmd);
            }
        }

        if (firstSpell) {
            player.getBattle().getFormCon().showPopup(getSpellDet(), false, 4000);
        }

        if (getSpellType() == SpellType.Aura) {
            firstSpell = false;
        }


        return "";
    }

    public void deleteFormFieldAndSendTOGraveyard(Player player, int slotOfMe) {
        if (this.getSpellType() == SpellType.Aura) {
            for (CanBeTarget oneT : underMySpell) {

                try {
                    if (oneT instanceof MonsterCard) {

                        ((MonsterCard) oneT).getDoAction().run(oneT);
                        ((MonsterCard) oneT).setDoAction(null);

                    } else if (oneT instanceof SpellCard) {

                        ((SpellCard) oneT).getDoAction().run(oneT);
                        ((SpellCard) oneT).setDoAction(null);

                    } else if (oneT instanceof Player) {

                        ((Player) oneT).getDoAction().run(oneT);
                        ((Player) oneT).setDoAction(null);

                    }

                } catch (Exception ignored) {

                }

            }
            underMySpell = new ArrayList<>();
        }


        player.getBag().getGraveyard().addCard(this.clone());
        try {
            player.getBag().getSpellField().addCardToSlot(false, null, slotOfMe, false);
        } catch (DeadPlayerException e) {

        }
        player.getBattle().getFormCon().animateCard(this, player.getBag().getSpellField(), player.getBag().getGraveyard(), slotOfMe, 0, null);
        player.getBattle().setupForAnimate(this, (player == player.getBattle().getHumanPlayer() ? "me" : "enemy") + "-spl", (player == player.getBattle().getHumanPlayer() ? "me" : "enemy") + "-grave", slotOfMe, 0);
        player.getBattle().updateAndSendToServer(true);
    }

    public String getSummaryInfo(boolean showColor) {
        return getSummaryInfo();
    }

    @Override
    public String getSummaryInfo() {
        return "[" + spellType + "] [" + spellDet + "]";
    }

    @Override
    public String toString() {
        String res = "";
        //"Card Name" Info:
        //Name: "Card Name"
        //MP cost: #DefaultMPCost
        //Card Type: [Instant / Continuous / Aura]
        //[Spell Details]
        //(OPTIONAL)Description: [Card Story]

        res += "\"" + getName() + "\" Info:";
        res += "\nName: \"" + getName() + "\"";
        res += "\nMP cost: " + getCostMP();
        res += "\nCard Type: " + getSpellType().name();
        if (!getSpellDet().equals("")) {
            res += "\nSpell Details: \n" + getSpellDet();
        }
        if (!getDescription().equals("")) {
            res += "\nDescription: \n" + getDescription();
        }
        return res.trim();
    }

    public ArrayList<CanBeTarget> getUnderMySpell() {
        return underMySpell;
    }

    public void setUnderMySpell(ArrayList<CanBeTarget> underMySpell) {
        this.underMySpell = underMySpell;
    }

    public DoAction getDoAction() {
        return doAction;
    }

    public void setDoAction(DoAction doAction) {
        this.doAction = doAction;
    }

    public String getSpellDet() {
        return spellDet;
    }

    public void setSpellDet(String spellDet) {
        this.spellDet = spellDet;
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

    public SpellType getSpellType() {
        return spellType;
    }

    public void setSpellType(SpellType spellType) {
        this.spellType = spellType;
    }

    @Override
    public SpellCard clone() {
        SpellCard temp = new SpellCard(getName(), getCostMP(), getSpellType(), getSpellDet(), getMagicCommand(), getDescription());
        temp.setDoAction(null);
        return temp;
    }
}


