package MenuControllers.BattleCons;

import Game.Assets.MonsterCard;
import Game.BattleManaging.CanBeTarget;
import Game.PlayerClasses.MonsterField;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;

public class CardUseCon extends MainController {
    public static String myKey = "cardUseCon";
    private boolean hasSpell;
    private String startText;
    private MonsterCard card;
    private UseCardResult cardResult;
    private CanBeTarget attackTarget;
    private MonsterField enemyField;

    public CardUseCon(Menus myMenu) {
        super(myMenu);

    }

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Attack \"EnemyMonsterSlot/Player\"", "To attack the card on that slot of enemy MonsterField or the enemy player", args -> {
            if (card.isSleeping()) {
                return Menus.RED + "Your Card Slot Is Sleeping! " + Menus.RESET + Menus.BLUE + "Zzz" + Menus.RESET;
            }
            if (!card.isCanAttack()) {
                return Menus.RED + "Your Card Cannot Attack! " + Menus.RESET;
            }
            if (args[0].trim().equalsIgnoreCase("Player")) {
                attackTarget = enemyField.getPlayer();
                cardResult = UseCardResult.Attack;
                return "END USE";
            } else {

                try {
                    int index = Integer.parseInt(args[0]);
                    if (index >= 1 && index <= enemyField.getLimit()) {
                        if (enemyField.getSlotCard(index) == null) {
                            return Menus.RED + "Your Selected Slot Is Empty!" + Menus.RESET;
                        }
                        attackTarget = enemyField.getSlotCard(index);
                        cardResult = UseCardResult.Attack;
                        return "END USE";
                    } else {
                        return Menus.RED + "Your Number Is Out Of Range" + Menus.RESET;
                    }
                } catch (NumberFormatException e) {
                    return Menus.RED + "Cannot Parse Your Number" + Menus.RESET;
                }

            }

        }));

        if (hasSpell) {
            addMenuItem(new MenuItem("Cast Spell", "To cast the card's spell", args -> {
                if (card.isSleeping()) {
                    return Menus.RED + "Your Card Slot Is Sleeping! " + Menus.RESET + Menus.BLUE + "Zzz" + Menus.RESET;
                }
                if (card.isUsedSpell()) {
                    return Menus.RED + "Spell Of Card is Used!" + Menus.RESET;
                } else {
                    cardResult = UseCardResult.CastSpell;
                    return "END USE";
                }
            }));
        }

        addMenuItem(new MenuItem("Info", "To get full information on card", args -> card.toString()));

        addMenuItem(new MenuItem("View Enemy MonsterField", "To view the cards in enemy player's monster fields", args -> {
            String res = "";
            res += Menus.CYAN + "Enemy MonsterField :\n" + Menus.RESET;
            res += enemyField.showList();
            return res;
        }));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To go back to Play Menu", args -> {
            cardResult = UseCardResult.Exit;
            return "END USE";
        }));
    }


    public UseCardResult getCardResult() {
        return cardResult;
    }

    public CanBeTarget getAttackTarget() {
        return attackTarget;
    }

    public void setCard(MonsterCard card) {
        this.card = card;
    }

    @Override
    public String help() {
        return getStartText() + "\n\n" + super.help();
    }

    @Override
    public void started() {
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }

    public void setEnemyField(MonsterField enemyField) {
        this.enemyField = enemyField;
    }

    public void setHasSpell(boolean hasSpell) {
        this.hasSpell = hasSpell;
    }

    public String getStartText() {
        return startText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }
}