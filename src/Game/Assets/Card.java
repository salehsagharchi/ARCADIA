package Game.Assets;

import Game.BattleManaging.CanBeTarget;
import Game.Enums.CardType;
import Game.Enums.FieldType;
import Game.Enums.PlayerType;
import Game.PlayerClasses.*;
import MenuControllers.GeneralClasses.Menus;
import javafx.scene.control.ChoiceDialog;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Card implements CanBeTarget, Serializable {
    private String name;
    private int costMP;
    private CardType type;
    private int price;

    public Card(String name, int costMP, int price) {
        this.name = name.replaceAll(" ", "");
        this.costMP = costMP;
        this.price = price;
    }

    public static int getNumber(String str) {
        String snum = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
        return Integer.parseInt(snum);
    }


    protected ArrayList<Target> generateTargets(String cmd, Player player, String spellDet, FieldType fieldType) {
        return generateTargets(cmd, player, spellDet, fieldType, true);
    }

    protected ArrayList<Target> generateTargets(String cmd, Player player, String spellDet, FieldType fieldType, boolean showInfos) {

        ArrayList<Target> results = new ArrayList<>();
        String targetsString = "";
        String ttype = "";
        Pattern pattern = Pattern.compile("[=;?]");
        Matcher matcher = pattern.matcher(cmd);
        if (matcher.find()) {
            targetsString = (cmd.substring(matcher.start() + 1));
            ttype = cmd.substring(matcher.start(), matcher.start() + 1);
        }

        for (String target : targetsString.split("-")) {
            target = target.trim();
            if (target.equals("")) {
                continue;
            }
            String oneTarget = target.substring(1);

            MonsterField monsterField;
            SpellField spellField;
            Graveyard graveyard;
            Hand hand;
            Player targetPlayer;

            if (target.startsWith("f")) {

                // Friends Assets
                targetPlayer = player;

            } else {

                // Enemy Assets
                targetPlayer = player.getOpposite();

            }

            monsterField = targetPlayer.getBag().getMonsterField();
            spellField = targetPlayer.getBag().getSpellField();
            graveyard = targetPlayer.getBag().getGraveyard();
            hand = targetPlayer.getBag().getHand();

            if (oneTarget.equals("mon")) {
                if (fieldType == FieldType.PlayField) {
                    for (int i = 1; i <= monsterField.getLimit(); i++) {
                        MonsterCard card = (monsterField.getSlotCard(i));
                        if (card != null) {
                            results.add(new Target(card, targetPlayer));
                        }
                    }
                } else if (fieldType == FieldType.GraveYard) {
                    for (int i = 1; i <= graveyard.getLimit(); i++) {
                        Card card = (graveyard.getSlotCard(i));
                        if (card != null) {
                            if (card.getType() == CardType.Monster) {
                                results.add(new Target((MonsterCard) card, targetPlayer));
                            }
                        }
                    }
                } else if (fieldType == FieldType.Hand) {
                    for (int i = 1; i <= hand.getLimit(); i++) {
                        Card card = (hand.getSlotCard(i));
                        if (card != null) {
                            if (card.getType() == CardType.Monster) {
                                results.add(new Target((MonsterCard) card, targetPlayer));
                            }
                        }
                    }
                }
            } else if (oneTarget.equals("spl")) {
                if (fieldType == FieldType.PlayField) {
                    for (int i = 1; i <= spellField.getLimit(); i++) {
                        SpellCard card = (spellField.getSlotCard(i));
                        if (card != null) {
                            results.add(new Target(card, targetPlayer));
                        }
                    }
                } else if (fieldType == FieldType.GraveYard) {
                    for (int i = 1; i <= graveyard.getLimit(); i++) {
                        Card card = (graveyard.getSlotCard(i));
                        if (card != null) {
                            if (card.getType() == CardType.Spell) {
                                results.add(new Target((SpellCard) card, targetPlayer));
                            }
                        }
                    }
                } else if (fieldType == FieldType.Hand) {
                    for (int i = 1; i <= hand.getLimit(); i++) {
                        Card card = (hand.getSlotCard(i));
                        if (card != null) {
                            if (card.getType() == CardType.Spell) {
                                results.add(new Target((SpellCard) card, targetPlayer));
                            }
                        }
                    }
                }

            } else if (oneTarget.equals("ply")) {

                results.add(new Target(targetPlayer, targetPlayer));

            } else if (oneTarget.startsWith("(")) {

                if (fieldType == FieldType.PlayField) {
                    String type = oneTarget.substring(1, oneTarget.length() - 1);
                    for (int i = 1; i <= monsterField.getLimit(); i++) {
                        MonsterCard card = (monsterField.getSlotCard(i));
                        if (card != null) {
                            if (card.getTribe().toString().equalsIgnoreCase(type) || card.getMonsterType().toString().equalsIgnoreCase(type)) {
                                results.add(new Target(card, targetPlayer));
                            }
                        }
                    }
                } else if (fieldType == FieldType.GraveYard) {
                    String type = oneTarget.substring(1, oneTarget.length() - 1);
                    for (int i = 1; i <= graveyard.getLimit(); i++) {
                        Card card = (graveyard.getSlotCard(i));
                        if (card != null) {
                            if (card.getType() == CardType.Monster) {
                                MonsterCard mcard = (MonsterCard) card;
                                if (mcard.getTribe().toString().equalsIgnoreCase(type) || mcard.getMonsterType().toString().equalsIgnoreCase(type)) {
                                    results.add(new Target(mcard, targetPlayer));
                                }
                            }
                        }
                    }
                } else if (fieldType == FieldType.Hand) {
                    String type = oneTarget.substring(1, oneTarget.length() - 1);
                    for (int i = 1; i <= hand.getLimit(); i++) {
                        Card card = (hand.getSlotCard(i));
                        if (card != null) {
                            if (card.getType() == CardType.Monster) {
                                MonsterCard mcard = (MonsterCard) card;
                                if (mcard.getTribe().toString().equalsIgnoreCase(type) || mcard.getMonsterType().toString().equalsIgnoreCase(type)) {
                                    results.add(new Target(mcard, targetPlayer));
                                }
                            }
                        }
                    }
                }


            }


        }

        if (ttype.equals("=")) {

            // Point To ALL
            String message = "\n";
            message += Menus.CYAN + "\"" + getName() + "\" has cast a spell:";
            message += "\n" + spellDet + Menus.RESET + "\n\nList Of Targets :\n";
            message += Menus.BLUE + results.toString() + Menus.RESET + "\n";
            if (showInfos) {
                player.getBattle().getFormCon().log(message);
            }

            return results;

        } else if (ttype.equals(";")) {

            if (results.size() == 0) {
                return null;
            }

            // Point For Random
            Random random = new Random();
            int index = random.nextInt(results.size());
            ArrayList<Target> oneResult = new ArrayList<>();
            oneResult.add(results.get(index));

            String message = "\n";
            message += Menus.CYAN + "\"" + getName() + "\" has cast a spell:";
            message += "\n" + spellDet + Menus.RESET + "\n\nList Of Targets :\n";
            message += Menus.BLUE + oneResult.toString() + Menus.RESET + "\n";
            if (showInfos) {
                player.getBattle().getFormCon().log(message);
            }

            return oneResult;

        } else if (ttype.equals("?")) {

            // Point TO Select One

            if (player.getType() == PlayerType.Human) {
                Map<String, Target> choiceList = new LinkedHashMap<>();
                ArrayList<String> choices = new ArrayList<>();


                String message = "\n";
                message += Menus.CYAN + "\"" + getName() + "\" has cast a spell:";
                message += "\n" + spellDet + Menus.RESET + "\n\nList Of Targets :\n";
                int index = 1;
                for (Target entry : results) {
                    CanBeTarget oneT = entry.getObject();
                    message += Menus.BLUE;
                    if (oneT instanceof MonsterCard || oneT instanceof SpellCard) {
                        message += "\t" + index + ":\t" + oneT.getName() + " " + ((Card) oneT).getSummaryInfo(false);
                        choiceList.put(index + ":\t" + oneT.getName() + " " + ((Card) oneT).getSummaryInfo(false), entry);
                        choices.add(index + ":\t" + oneT.getName() + " " + ((Card) oneT).getSummaryInfo(false));
                    } else if (oneT instanceof Player) {
                        message += "\t" + index + ":\t" + oneT.getName() + " [PLAYER]";
                        choiceList.put(index + ":\t" + oneT.getName() + " [PLAYER]", entry);
                        choices.add(index + ":\t" + oneT.getName() + " [PLAYER]");
                    }
                    message += Menus.RESET + "\n";
                    index += 1;
                }

                if (results.size() == 0) {
                    return null;
                }
                player.getBattle().getFormCon().log(message);

                ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
                dialog.setTitle("Cast Spell");
                dialog.setHeaderText(getName() + "\" has cast a spell:");
                dialog.setContentText("Choose your Target :");
                Optional<String> result = dialog.showAndWait();
                String victim = null;
                if (result.isPresent()) {
                    victim = result.get();
                }
                if (victim != null) {
                    ArrayList<Target> oneResult = new ArrayList<>();
                    oneResult.add(choiceList.get(victim));
                    player.getBattle().getFormCon().log(Menus.GREEN + "\"" + oneResult.get(0).getObject().getName() + "\" has been Targeted!\n" + Menus.RESET);
                    return oneResult;
                }
                return null;


            } else {
                String message = "\n";
                message += Menus.CYAN + "\"" + getName() + "\" has cast a spell:";
                message += "\n" + spellDet + Menus.RESET + "\n";
                player.getBattle().getFormCon().log(message);
                int compResult = player.decideForSpellTarget(results);
                if (compResult == -1) {
                    return null;
                } else {
                    ArrayList<Target> oneResult = new ArrayList<>();
                    oneResult.add(results.get(compResult));
                    message = (Menus.GREEN + "\"" + oneResult.get(0).getObject().getName() + "\" has been Targeted By " + player.getName() + " !\n" + Menus.RESET);
                    player.getBattle().getFormCon().log(message);
                    return oneResult;
                }
            }


        }

        return null;

    }

    public abstract Card clone();

    public abstract String getSummaryInfo(boolean showColor);

    public abstract String getSummaryInfo();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCostMP() {
        return costMP;
    }

    public void setCostMP(int costMP) {
        this.costMP = costMP;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
