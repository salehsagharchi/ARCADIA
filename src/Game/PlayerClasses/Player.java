package Game.PlayerClasses;

import Game.Assets.Card;
import Game.Assets.DoAction;
import Game.Assets.MonsterCard;
import Game.Assets.SpellCard;
import Game.BattleManaging.Battle;
import Game.BattleManaging.CanBeTarget;
import Game.Custom.GameClass;
import Game.Enums.CardType;
import Game.Enums.PlayerType;
import Game.Enums.SpellType;
import Game.Exceptions.DeadPlayerException;
import MenuControllers.GeneralClasses.Menus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class Player implements CanBeTarget, Serializable {
    private Bag bag;
    private PlayerType type;
    private Player opposite;
    private int maxMaxMP;
    private int maxMP;
    private int MP;
    private DoAction doAction;
    private GameClass playingGameClass;

    private int HP;
    private int maxHP;
    private String name;
    private int gil;
    private Inventory inventory;
    private transient Battle battle;
    private float damageEffect;

    private long sleepTime = 1500;

    public Player(PlayerType type, String name, int gil) {
        this.type = type;
        this.maxMaxMP = 10;
        this.HP = maxHP;
        this.name = name;
        this.gil = gil;
        this.damageEffect = 1.0f;
        inventory = new Inventory(type == PlayerType.Human, this);
        bag = new Bag(this);
    }

    private ArrayList<Integer> randomIndexes(int count) {
        ArrayList<Integer> randomIndex = new ArrayList<>();
        Random random = new Random();
        int rnd;
        while (randomIndex.size() < count) {
            rnd = random.nextInt(count) + 1;
            if (!randomIndex.contains(rnd)) {
                randomIndex.add(rnd);
            }
        }
        return randomIndex;
    }

    public void decide() {
        new Thread(() -> {
            decideThread();
        }).start();
    }

    public void decideThread() {


        battle.getFormCon().log(Menus.BLUE + getName() + " Decides !\n" + Menus.RESET);

        Thread.yield();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 1 : draw monster cards --> mp , emptySlot
        // 2 : draw spell cards
        // 3 : clash by player
        // 4 : done
        ArrayList<Integer> randomIndex = randomIndexes(getBag().getHand().getLimit());

        for (int i : randomIndex) {
            if (getOpposite() == null) {
                return;
            }
            Card card = getBag().getHand().getSlotCard(i);
            if (card != null) {
                if (card.getType() == CardType.Monster && card.getCostMP() <= getMP() && getBag().getMonsterField().emptySlots() > 0) {
                    int to = getBag().getMonsterField().findFirstEmptySlotIndex();
                    String res = getBag().getHand().drawCardTo(this, i, to);

                    if (!res.startsWith("ERROR")) {
                        CountDownLatch latch = new CountDownLatch(1);
                        battle.getFormCon().animateCard(card, getBag().getHand(), getBag().getMonsterField(), i, to, new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                                battle.getFormCon().log(Menus.CYAN + "\"" + card.getName() + "\" drew To Play Field By " + getName() + "\n" + Menus.RESET);
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Thread.yield();
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
                if (card.getType() == CardType.Spell && card.getCostMP() <= getMP() && ((SpellCard) card).getSpellType() == SpellType.Instant) {
                    String res = getBag().getHand().drawCardTo(this, i, 1);
                    if (!res.startsWith("ERROR")) {
                        CountDownLatch latch = new CountDownLatch(1);
                        battle.getFormCon().animateCard(card, getBag().getHand(), getBag().getGraveyard(), i, 0, new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Thread.yield();
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (card.getType() == CardType.Spell && card.getCostMP() <= getMP() && ((SpellCard) card).getSpellType() != SpellType.Instant
                        && getBag().getSpellField().emptySlots() > 0) {
                    int to = getBag().getSpellField().findFirstEmptySlotIndex();
                    String res = getBag().getHand().drawCardTo(this, i, to);
                    if (!res.startsWith("ERROR")) {
                        CountDownLatch latch = new CountDownLatch(1);
                        battle.getFormCon().animateCard(card, getBag().getHand(), getBag().getSpellField(), i, to, new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                                battle.getFormCon().log(Menus.CYAN + "\"" + card.getName() + "\" drew To Play Field By " + getName() + "\n" + Menus.RESET);
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Thread.yield();
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        randomIndex = randomIndexes(getBag().getMonsterField().getLimit());

        for (int i : randomIndex) {
            if (getOpposite() == null) {
                return;
            }
            MonsterCard card = getBag().getMonsterField().getSlotCard(i);
            if (card != null) {
                try {
                    if (getBag().getMonsterField().useCard(String.valueOf(i)).equals("ok")) {
                        Thread.yield();
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (DeadPlayerException e) {

                }
            }
        }

        battle.getFormCon().log("\n" + Menus.BLUE + getName() + " Turn End !\n" + Menus.RESET);

        battle.nextPlayer();
    }

    public int decideForAttackTarget(List<CanBeTarget> results) {
        if (results.size() == 0) {
            return -1;
        }
        Thread.yield();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        return random.nextInt(results.size());
    }

    public boolean decideForCastOrAttack() {
        Thread.yield();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextBoolean();
    }

    public int decideForSpellTarget(List<Target> results) {
        if (results.size() == 0) {
            return -1;
        }
        Thread.yield();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        return random.nextInt(results.size());
    }

    public void turnStarted() {
        bag.getMonsterField().wakeUpCards();
        bag.getMonsterField().canAttackCards();
        try {
            bag.getSpellField().doContinuousSpells();
        } catch (DeadPlayerException e) {

        }
    }

    public String getAttack(MonsterCard fromCard, CanBeTarget toTarget) throws DeadPlayerException {
        int damage = (int) (((float) fromCard.getAP()) * damageEffect);

        fromCard.setCanAttack(false);

        if (toTarget instanceof Player) {
            // Attack To Me :(

            ArrayList<MonsterCard> defensive = new ArrayList<>();
            for (int i = 1; i <= getBag().getMonsterField().getLimit(); i++) {
                if (getBag().getMonsterField().getSlotCard(i) != null) {
                    if (getBag().getMonsterField().getSlotCard(i).isDefensive()) {
                        defensive.add(getBag().getMonsterField().getSlotCard(i));
                    }
                }
            }

            if (defensive.size() > 0) {
                MonsterCard mySheild = decideFromDefensives(defensive);
                battle.getFormCon().log(Menus.GREEN + "Defensive Card \"" + mySheild.getName() + "\" Defends " + getName() + Menus.RESET);

                int mySheildAP = mySheild.getAP();
                String sheildName = mySheild.getName();
                mySheild.subtractHP(damage, this);
                fromCard.subtractHP(mySheildAP, this.getOpposite());

                return "Defender Card : " + sheildName;

            } else {
                // Attack To Me Really :-(

                subtractHP(damage);
                return "Player : " + getName();

            }


        }
        if (toTarget instanceof MonsterCard) {
            // Attack To My Card :(
            MonsterCard victimCard = (MonsterCard) toTarget;

            if (victimCard.isDefensive()) {

                int victimAP = victimCard.getAP();
                String victimName = victimCard.getName();

                victimCard.subtractHP(damage, this);
                fromCard.subtractHP(victimAP, this.getOpposite());

                return "Target Defender Card : " + victimName;

            } else {

                ArrayList<MonsterCard> defensive = new ArrayList<>();
                for (int i = 1; i <= getBag().getMonsterField().getLimit(); i++) {
                    if (getBag().getMonsterField().getSlotCard(i) != null) {
                        if (getBag().getMonsterField().getSlotCard(i).isDefensive()) {
                            defensive.add(getBag().getMonsterField().getSlotCard(i));
                        }
                    }
                }

                if (defensive.size() > 0) {
                    MonsterCard mySheild = decideFromDefensives(defensive);
                    battle.getFormCon().log(Menus.GREEN + "Defensive Card \"" + mySheild.getName() + "\" Defends " + victimCard.getName() + Menus.RESET);

                    int mySheildAP = mySheild.getAP();
                    String sheildName = mySheild.getName();
                    mySheild.subtractHP(damage, this);
                    fromCard.subtractHP(mySheildAP, this.getOpposite());

                    return "Defender Card : " + sheildName;

                } else {
                    // Attack To My Card Really :-(


                    int victimAP = victimCard.getAP();
                    String victimName = victimCard.getName();

                    victimCard.subtractHP(damage, this);
                    fromCard.subtractHP(victimAP, this.getOpposite());
                    return "Target Card : " + victimName;

                }

            }
        }

        return "";
    }

    private MonsterCard decideFromDefensives(List<MonsterCard> dCards) {
        Random random = new Random();
        return dCards.get(random.nextInt(dCards.size()));
    }

    public void executeAmulets() {
        if (getBag().getAmulet() == null) {
            return;
        }
        Pattern addMaxHP = Pattern.compile("\\+MHP\\((\\d*)\\)");
        Pattern addMaxMP = Pattern.compile("\\+MMP\\((\\d*)\\)");

        for (String cmd : getBag().getAmulet().getMagicCommand().split(":")) {
            cmd = cmd.trim();
            if (cmd.equals("")) {
                continue;
            }

            if (Pattern.matches(addMaxHP.toString(), cmd)) {
                int val = Card.getNumber(cmd);
                setMaxHP(getMaxHP() + val);
                setHP(getMaxHP());

            } else if (Pattern.matches(addMaxMP.toString(), cmd)) {
                int val = Card.getNumber(cmd);
                setMaxMP(getMaxMP() + val);
                equalizeMP();

            } else if (cmd.equals("FUNC1")) {
                this.damageEffect = 0.8f;

            } else {
                battle.getFormCon().log("Cannot Recognize Magic Command : " + cmd);
            }

        }

        battle.getFormCon().log("\n" + Menus.CYAN + getBag().getAmulet().toString() + "\n" + Menus.RESET);
    }

    public void addHP(int val) {
        setHP(getHP() + val);
        if (getHP() > getMaxHP()) {
            setHP(getMaxHP());
        }
    }

    public void subtractHP(int val) throws DeadPlayerException {
        setHP(getHP() - val);
        if (getHP() <= 0) {
            battle.IAmDead(this);
        }

    }

    public void equalizeMP() {
        setMP(getMaxMP());
    }

    public void subtractMP(int val) {
        setMP(getMP() - val);
        if (getMP() < 0) {
            setMP(0);
        }
    }

    public void addMP(int val) {
        setMP(getMP() + val);
        if (getMP() > getMaxMP()) {
            setMP(getMaxMP());
        }
    }

    public void subtractMaxMP(int val) {
        setMaxMP(getMaxMP() - val);
        if (getMaxMP() < 0) {
            setMaxMP(0);
        }
    }

    public void addMaxMP(int val) {
        setMaxMP(getMaxMP() + val);
        if (getMaxMP() > getMaxMaxMP()) {
            setMaxMP(getMaxMaxMP());
        }
    }

    public DoAction getDoAction() {
        return doAction;
    }

    public void setDoAction(DoAction doAction) {
        this.doAction = doAction;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public Player getOpposite() {
        return opposite;
    }

    public void setOpposite(Player opposite) {
        this.opposite = opposite;
    }

    public int getMaxMaxMP() {
        return maxMaxMP;
    }

    public void setMaxMaxMP(int maxMaxMP) {
        this.maxMaxMP = maxMaxMP;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
    }

    public int getMP() {
        return MP;
    }

    public void setMP(int MP) {
        this.MP = MP;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGil() {
        return gil;
    }

    public void setGil(int gil) {
        this.gil = gil;
    }

    public void subtractGil(int value) {
        this.gil = this.gil - value;
    }

    public void addGil(int value) {
        this.gil = this.gil + value;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getDefaultMaxMP() {
        return 1;
    }

    public int getDefaultMaxHP() {
        return 4000;
    }

    public GameClass getPlayingGameClass() {
        return playingGameClass;
    }

    public void setPlayingGameClass(GameClass playingGameClass) {
        this.playingGameClass = playingGameClass;
    }
}
