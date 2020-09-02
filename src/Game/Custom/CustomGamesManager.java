package Game.Custom;

import Game.Assets.*;
import Game.Constants.Initializer_Statics;
import Game.Enums.CardType;
import Game.GameManager;
import Game.PlayerClasses.Deck;
import Game.PlayerClasses.Inventory;
import Game.PlayerClasses.Player;
import Game.Shops.AmuletShop;
import Game.Shops.CardShop;
import Game.Shops.ItemShop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang.SerializationUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class CustomGamesManager {
    private GameManager gameManager;
    private SpellsManager spellsManager;
    private ArrayList<GameClass> gameClasses;

    public CustomGamesManager(GameManager gameManager) {
        this.gameManager = gameManager;
        gameClasses = new ArrayList<>();
        initialize();
    }

    public void initialize() {
        File customDir = new File("CustomGames");
        if (!customDir.exists()) {
            customDir.mkdir();
        }

        initializeSpells();

        makeSinglePlayer();

        loadGameClasses();

        System.out.println("Game Class Counts : " + gameClasses.size());
    }

    public String deleteGame(String gameName) {
        GameClass todelete = null;
        for (GameClass game : gameClasses) {
            if (game.getGameName().trim().equals(gameName.trim())) {
                todelete = game;
                break;
            }
        }
        if (todelete != null && !todelete.getGameName().equals("Single Player")) {
            File gameFile = new File("CustomGames/" + todelete.getFileName());
            gameFile.delete();
            gameClasses.remove(todelete);
            getGameManager().getMainController().getCustomCon().refreshList();
            return "OK";
        } else {
            return "Cannot Delete :)";
        }
    }

    public void playGame(String gameName) {
        GameClass toPlay = null;
        for (GameClass game : gameClasses) {
            if (game.getGameName().trim().equals(gameName.trim())) {
                toPlay = game;
                break;
            }
        }
        if (toPlay != null) {
            getGameManager().initializeCustomGame(toPlay);
            getGameManager().getMainController().singlePlayer();
        }
    }

    public void makeNewGame(GameClass newGame) {
        try {
            if (!gameClasses.contains(newGame)) {
                gameClasses.add(newGame);
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("CustomGames/" + newGame.getFileName()), false));
            objectOutputStream.writeUnshared(newGame);
            objectOutputStream.close();
            getGameManager().getMainController().getCustomCon().refreshList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newGame(String coreGame) {
        GameClass toNew = null;
        for (GameClass game : gameClasses) {
            if (game.getGameName().trim().equals(coreGame.trim())) {
                toNew = game;
                break;
            }
        }
        if (toNew != null) {
            getGameManager().getMainController().getNewGameCon().setToCreate((GameClass) SerializationUtils.clone(toNew));
            getGameManager().getMainController().startNewGame();
        }
    }

    public ObservableList<String> getGameListItems() {
        ObservableList<String> resultList = FXCollections.observableArrayList();
        for (GameClass gameClass : gameClasses) {
            resultList.add(gameClass.toString());
        }
        return resultList;
    }

    public String getMagicCommandOfSpell(String det) {
        for (Spell spell : spellsManager.getSpellList()) {
            if (spell.getDescription().trim().equals(det.trim())) {
                return spell.getMagicCommand();
            }
        }
        return "";
    }

    public String getOwnerOfSpell(String det) {
        for (Spell spell : spellsManager.getSpellList()) {
            if (spell.getDescription().trim().equals(det.trim())) {
                return spell.getOwner().toString();
            }
        }
        return "";
    }

    public ObservableList<String> getSpellsListItems() {
        ObservableList<String> resultList = FXCollections.observableArrayList();
        for (Spell spell : spellsManager.getSpellList()) {
            resultList.add(spell.getDescription());
        }
        return resultList;
    }

    public ArrayList<String> getSpellsListFor(SpellOwner owner) {
        ArrayList<String> resultList = new ArrayList<>();
        for (Spell spell : spellsManager.getSpellList()) {
            if (spell.getOwner().equals(owner)) {
                resultList.add(spell.getDescription());
            }
        }
        return resultList;
    }

    private void loadGameClasses() {
        gameClasses.clear();
        try {
            File customDir = new File("CustomGames");
            Files.walk(Paths.get("CustomGames/")).forEach(file -> {
                try {
                    if (!file.toFile().isDirectory()) {
                        if (file.getFileName().toString().endsWith(".game")) {
                            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file.toFile()));
                            gameClasses.add((GameClass) objectInputStream.readUnshared());
                            objectInputStream.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeSinglePlayer() {
        long prices = 0;
        try {
            File singlePlayer = new File("CustomGames/single_player.game");
            if (!singlePlayer.exists()) {
                GameClass gameClassSingle = new GameClass("Single Player");
                for (Map.Entry<String, Card> card : Initializer_Statics.getCardList().entrySet()) {
                    gameClassSingle.addCard(card.getValue());
                    prices += card.getValue().getPrice();
                }
                for (Map.Entry<String, Item> item : Initializer_Statics.getItemList().entrySet()) {
                    gameClassSingle.addItem(item.getValue());
                    prices += item.getValue().getPrice();
                }
                for (Map.Entry<String, Amulet> amulet : Initializer_Statics.getAmuletList().entrySet()) {
                    gameClassSingle.addAmulet(amulet.getValue());
                    prices += amulet.getValue().getPrice();
                }
                gameClassSingle.setAmuletShop((AmuletShop) SerializationUtils.clone(getGameManager().getAmuletShop()));
                gameClassSingle.setCardShop((CardShop) SerializationUtils.clone(getGameManager().getCardShop()));
                gameClassSingle.setItemShop((ItemShop) SerializationUtils.clone(getGameManager().getItemShop()));
                gameClassSingle.setPlayer((Player) SerializationUtils.clone(getGameManager().getHumanPlayer()));

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(singlePlayer, false));
                objectOutputStream.writeUnshared(gameClassSingle);
                objectOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeSpells() {
        try {
            File spells = new File("CustomGames/spells.spl");
            if (!spells.exists()) {
                try {
                    new FileWriter(spells, false);
                } catch (IOException e) {
                }
                spellsManager = new SpellsManager();
                for (Map.Entry<String, Card> card : Initializer_Statics.getCardList().entrySet()) {
                    if (card.getValue().getType() == CardType.Spell) {
                        SpellCard spellCard = (SpellCard) card.getValue();
                        spellsManager.addSpell(new Spell(spellCard.getMagicCommand(), spellCard.getSpellDet(), SpellOwner.ForCard));
                    } else {
                        MonsterCard monsterCard = (MonsterCard) card.getValue();
                        if (!monsterCard.getMagicCommand().trim().equals("")) {
                            String[] magics = monsterCard.getMagicCommand().trim().split("\\*");
                            if (!magics[0].equals("")) {
                                spellsManager.addSpell(new Spell(magics[0], monsterCard.getBattleCryDet(), SpellOwner.ForCard));
                            }
                            if (!magics[1].equals("")) {
                                spellsManager.addSpell(new Spell(magics[1], monsterCard.getSpellDet(), SpellOwner.ForCard));
                            }
                            try {
                                if (!magics[2].equals("")) {
                                    spellsManager.addSpell(new Spell(magics[2], monsterCard.getWillDet(), SpellOwner.ForCard));
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                for (Map.Entry<String, Item> item : Initializer_Statics.getItemList().entrySet()) {
                    if (!item.getValue().getMagicCommand().equals("")) {
                        spellsManager.addSpell(new Spell(item.getValue().getMagicCommand(), item.getValue().getDescription(), SpellOwner.ForItem));
                    }
                }

                for (Map.Entry<String, Amulet> amulet : Initializer_Statics.getAmuletList().entrySet()) {
                    if (!amulet.getValue().getMagicCommand().equals("")) {
                        spellsManager.addSpell(new Spell(amulet.getValue().getMagicCommand(), amulet.getValue().getDescription(), SpellOwner.ForAmulet));
                    }
                }

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(spells, false));
                objectOutputStream.writeUnshared(spellsManager);
                objectOutputStream.close();

            } else {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(spells));
                spellsManager = (SpellsManager) objectInputStream.readUnshared();
                objectInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshSpellsFile() {
        try {
            File spells = new File("CustomGames/spells.spl");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(spells, false));
            objectOutputStream.writeUnshared(spellsManager);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public SpellsManager getSpellsManager() {
        return spellsManager;
    }

    public ArrayList<GameClass> getGameClasses() {
        return gameClasses;
    }
}
