package Game.PlayerClasses;

import Game.Assets.MonsterCard;
import Game.BattleManaging.CanBeTarget;
import Game.Enums.MonsterSpellType;
import Game.Enums.MonsterType;
import Game.Enums.PlayerType;
import Game.Exceptions.DeadPlayerException;
import MenuControllers.GeneralClasses.Menus;
import javafx.scene.control.ChoiceDialog;

import java.io.Serializable;
import java.util.*;

public class MonsterField implements Field, Serializable {
    private static int limit = 5;
    private Map<Integer, MonsterCard> slots;
    private Player player;

    MonsterField(Player player) {
        this.player = player;
        slots = new LinkedHashMap<>();
        for (int i = 1; i <= limit; i++) {
            slots.put(i, null);
        }

    }

    public String addCardToSlot(MonsterCard card, int slot, boolean drawFuncs) throws DeadPlayerException {
        slots.put(slot, card);
        if (drawFuncs) {
            if (card.isNimble()) {
                card.setSleeping(false);
            } else {
                card.setSleeping(true);
            }
            if (card.getMonsterType() == MonsterType.General || card.getMonsterType() == MonsterType.Hero) {
                card.executeMagicCommand(player, MonsterSpellType.BattleCry);
            }

        }
        return "ok";
    }

    public void wakeUpCards() {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) != null) {
                slots.get(i).setSleeping(false);
            }
        }
    }

    public void canAttackCards() {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) != null) {
                slots.get(i).setCanAttack(true);
            }
        }
    }

    public String useCard(String num) throws DeadPlayerException {
        return useCard(num, "");
    }

    //using = cast / attack
    public String useCard(String num, String using) throws DeadPlayerException {
        try {
            if (Integer.parseInt(num) <= 0 || Integer.parseInt(num) > limit) {
                player.getBattle().getFormCon().log(Menus.RED + "ERROR : Index is not Valid" + Menus.RESET);
                return "";
            }
        } catch (Exception e) {
            player.getBattle().getFormCon().log(Menus.RED + "ERROR : Index Cannot Parse" + Menus.RESET);
            return "";
        }

        int index = Integer.parseInt(num);
        MonsterCard card = getSlotCard(index);

        if (card == null) {
            player.getBattle().getFormCon().log(Menus.RED + "ERROR : Slot " + index + " Of Your MonsterField is Empty!" + Menus.RESET);
            return "";
        }

        if (player.getType() == PlayerType.Human) {

            /*String nowkey = player.getMyMenu().getNowController().getMyKey();
            CardUseCon con = (CardUseCon) player.getMyMenu().findController(CardUseCon.myKey);

            con.setCard(card);
            con.setEnemyField(this.player.getOpposite().getBag().getMonsterField());
*/


            String message = "\n";
            message += Menus.CYAN + "Using \"" + card.getName() + "\":\n" + Menus.RESET;
            message += "HP: ";
            if (card.getHP() > card.getDefaultHP()) {
                message += Menus.GREEN + card.getHP() + Menus.RESET;
            } else if (card.getHP() < card.getDefaultHP()) {
                message += Menus.RED + card.getHP() + Menus.RESET;
            } else {
                message += card.getHP();
            }
            message += " , AP: ";
            if (card.getAP() > card.getDefaultAP()) {
                message += Menus.GREEN + card.getAP() + Menus.RESET;
            } else if (card.getAP() < card.getDefaultAP()) {
                message += Menus.RED + card.getAP() + Menus.RESET;
            } else {
                message += card.getAP();
            }


            message += "\nIs Sleeping : " + (card.isSleeping() ? Menus.RED : Menus.GREEN) + String.valueOf(card.isSleeping()).toUpperCase() + Menus.RESET + "\n";
            message += "Can Attack : " + ((card.isSleeping() || !card.isCanAttack()) ? Menus.RED : Menus.GREEN) + String.valueOf((!card.isSleeping()) && card.isCanAttack()).toUpperCase() + Menus.RESET + "\n";
            if (card.getMonsterType() == MonsterType.Hero || card.getMonsterType() == MonsterType.SpellCaster) {
                message += "Can Cast : " + ((card.isUsedSpell() || card.isSleeping()) ? Menus.RED : Menus.GREEN) + String.valueOf(!card.isUsedSpell() && !card.isSleeping()).toUpperCase() + Menus.RESET + "\n";
            }

            player.getBattle().logToAll(message);



            if (using.equalsIgnoreCase("cast")) {

                card.executeMagicCommand(player, MonsterSpellType.SpellByUser);

            } else if (using.equalsIgnoreCase("attack")) {

                Map<String, CanBeTarget> results = new LinkedHashMap<>();
                ArrayList<String> choices = new ArrayList<>();
                int targetIndex = 1;
                results.put(targetIndex + " : " + player.getOpposite().getName(), player.getOpposite());
                choices.add(targetIndex + " : " + player.getOpposite().getName());
                for (int i = 1; i <= player.getOpposite().getBag().getMonsterField().getLimit(); i++) {
                    MonsterCard target = player.getOpposite().getBag().getMonsterField().getSlotCard(i);
                    if (target != null) {
                        targetIndex += 1;
                        results.put(targetIndex + " : " + target.getName() + " " + target.getSummaryInfo(), target);
                        choices.add(targetIndex + " : " + target.getName() + " " + target.getSummaryInfo());
                    }
                }

                ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
                dialog.setTitle("Attack");
                dialog.setHeaderText("Using Card : " + card.getName());
                dialog.setContentText("Choose your Target :");
                Optional<String> result = dialog.showAndWait();
                String victim = null;
                if (result.isPresent()) {
                    victim = result.get();
                }
                if (victim != null) {

                    String rstr = player.getOpposite().getAttack(card, results.get(victim));
                    player.getBattle().logToAll(Menus.CYAN + "\nAttacker \"" + card.getName() + "\" clashed with \"" + rstr + "\"\n" + Menus.RESET);

                }
            }

            return "";

        } else { // not reach this block in network gaming
            String message = "\n";
            message += Menus.CYAN + "Using \"" + card.getName() + "\":\n" + Menus.RESET;
            message += "HP: ";
            if (card.getHP() > card.getDefaultHP()) {
                message += Menus.GREEN + card.getHP() + Menus.RESET;
            } else if (card.getHP() < card.getDefaultHP()) {
                message += Menus.RED + card.getHP() + Menus.RESET;
            } else {
                message += card.getHP();
            }
            message += " , AP: ";
            if (card.getAP() > card.getDefaultAP()) {
                message += Menus.GREEN + card.getAP() + Menus.RESET;
            } else if (card.getAP() < card.getDefaultAP()) {
                message += Menus.RED + card.getAP() + Menus.RESET;
            } else {
                message += card.getAP();
            }


            message += "\nIs Sleeping : " + (card.isSleeping() ? Menus.RED : Menus.GREEN) + String.valueOf(card.isSleeping()).toUpperCase() + Menus.RESET + "\n";
            message += "Can Attack : " + ((card.isSleeping() || !card.isCanAttack()) ? Menus.RED : Menus.GREEN) + String.valueOf((!card.isSleeping()) && card.isCanAttack()).toUpperCase() + Menus.RESET + "\n";
            if (card.getMonsterType() == MonsterType.Hero || card.getMonsterType() == MonsterType.SpellCaster) {
                message += "Can Cast : " + ((card.isUsedSpell() || card.isSleeping()) ? Menus.RED : Menus.GREEN) + String.valueOf(!card.isUsedSpell() && !card.isSleeping()).toUpperCase() + Menus.RESET + "\n";
            }


            boolean cast;

            if (card.getMonsterType() == MonsterType.Normal || card.getMonsterType() == MonsterType.General) {
                if (!card.isSleeping() && card.isCanAttack()) {
                    cast = false;
                } else {
                    return "";
                }
            } else {

                if (!card.isUsedSpell() && !card.isSleeping() && card.isCanAttack()) {
                    cast = player.decideForCastOrAttack();
                } else if (!card.isSleeping() && !card.isUsedSpell()) {
                    cast = true;
                } else if (!card.isSleeping() && card.isCanAttack()) {
                    cast = false;
                } else {
                    return "";
                }

            }

            player.getBattle().getFormCon().log(message + "\n");

            if (cast) {
                card.executeMagicCommand(player, MonsterSpellType.SpellByUser);
                return "ok";
            } else {
                ArrayList<CanBeTarget> targets = new ArrayList<>();
                targets.add(this.player.getOpposite());
                MonsterField enemyField = this.player.getOpposite().getBag().getMonsterField();
                for (int i = 1; i <= enemyField.getLimit(); i++) {
                    MonsterCard monsterCard = enemyField.getSlotCard(i);
                    if (monsterCard != null) {
                        targets.add(monsterCard);
                    }
                }
                int attackTarget = player.decideForAttackTarget(targets);
                if (attackTarget == -1) {
                    return "";
                }


                player.getBattle().getFormCon().log(Menus.GREEN + "\"" + targets.get(attackTarget).getName() + "\" has been Targeted By " + player.getName() + " !\n" + Menus.RESET);

                String result = player.getOpposite().getAttack(card, targets.get(attackTarget));
                player.getBattle().getFormCon().log(Menus.CYAN + "\nAttacker \"" + card.getName() + "\" clashed with \"" + result + "\"\n" + Menus.RESET);
                return "ok";
            }

        }

    }

    public int findCardSlotByReference(MonsterCard card) {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == card) {
                return i;
            }
        }
        return -1;
    }


    public String showList() {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i <= limit; i++) {
            res.append("Slot ").append(i).append(":\t");
            if (getSlotCard(i) != null) {
                if (getSlotCard(i).isSleeping()) {
                    res.append(Menus.BLUE + "Zzz" + Menus.RESET + "\t");
                }
            }
            MonsterCard temp = getSlotCard(i);

            if (temp != null) {
                res.append(Menus.GREEN).append(getSlotName(i)).append(Menus.RESET);
                res.append(" ").append(temp.getSummaryInfo());
            } else {
                res.append(Menus.RED).append(getSlotName(i)).append(Menus.RESET);
            }
            res.append("\n");
        }
        return res.toString();
    }

    public int findFirstEmptySlotIndex() {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public int getLimit() {
        return limit;
    }

    public MonsterCard getSlotCard(int index) {
        return slots.getOrDefault(index, null);
    }

    public String getSlotName(int index) {
        if (index >= 1 && index <= limit) {
            if (slots.get(index) == null) {
                return "#EMPTY#";
            }
            return slots.get(index).getName();
        }
        return "index out of range";
    }

    public String addCard(MonsterCard card) {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                slots.put(i, card);
                return "ok";
            }
        }
        return "mfield full";
    }

    public Player getPlayer() {
        return player;
    }

    public String removeCard(int index) {
        if (index >= 1 && index <= limit) {
            slots.put(index, null);
            return "ok";
        }
        return "index out of range";
    }


    public int emptySlots() {
        int res = 0;
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                res++;
            }
        }
        return res;
    }
}
