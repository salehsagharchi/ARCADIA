package Views.FXControllers;

import Game.Custom.Spell;
import Game.Custom.SpellOwner;
import Game.Enums.MonsterTribe;
import Game.Enums.MonsterType;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewSpellCon extends FormCon {
    public Button newSpell;
    public TextField mgcCmd;
    public TextField ownerTxt;
    public TextField splName;

    // CardSpell
    public ChoiceBox<String> workCard;
    public TextField value;
    public ChoiceBox<String> targetType;
    public CheckBox fplyCard;
    public CheckBox eplyCard;
    public CheckBox fmonCard;
    public TextField fmonTarget;
    public CheckBox fsplCard;
    public CheckBox emonCard;
    public TextField emonTarget;
    public CheckBox esplCard;
    public Button addSpell;
    public AnchorPane targetGroup;
    public Button clearMgc;
    // item Spell
    public CheckBox plyHPItemCheck;
    public CheckBox plyMPItemCheck;
    public TextField playerHPItem;
    public TextField playerMPItem;
    // Amu Spell
    public CheckBox plyHPAmuCheck;
    public CheckBox plyMPAmuCheck;
    public TextField playerHPAmu;
    public TextField playerMPAmu;
    public TabPane tabPane;
    public Tab forCardTab;
    public Tab forItemTab;
    public Tab forAmuTab;

    private String nowMagicCommand = "";
    private Map<String, String> commands;

    @Override
    public void start() {
        commands = new LinkedHashMap<>();
        commands.put("Increase HP", ":+HP(#)*");
        commands.put("Decrease HP", ":-HP(#)*");
        commands.put("Increase AP", ":+AP(#)*");
        commands.put("Decrease AP", ":-AP(#)*");
        commands.put("Send Card From Field To GraveYard", ":GR*");
        commands.put("Send Card From Field To Hand", ":HND*");
        commands.put("Send Card From GraveYard To Hand", ":GHND*");
        commands.put("Send Card From Deck To Hand", ":DHND");
        commands.put("Send Card From Hand To Field", ":HDFL*");
        nowMagicCommand = "";
        mgcCmd.setText("");
        ownerTxt.setText("");
        splName.setText("");
        tabChangeRequest();
        forCardTab.setOnSelectionChanged(event -> tabChangeRequest());
        forItemTab.setOnSelectionChanged(event -> tabChangeRequest());
        forAmuTab.setOnSelectionChanged(event -> tabChangeRequest());


        newSpell.setOnAction(event -> {
            newSpellMake();
        });



        initializeNewCard();
        fplyCard.setOnAction(event -> cardControlsChange());
        eplyCard.setOnAction(event -> cardControlsChange());
        fmonCard.setOnAction(event -> cardControlsChange());
        fsplCard.setOnAction(event -> cardControlsChange());
        emonCard.setOnAction(event -> cardControlsChange());
        esplCard.setOnAction(event -> cardControlsChange());
        fmonTarget.textProperty().addListener((observable, oldValue, newValue) -> cardControlsChange());
        emonTarget.textProperty().addListener((observable, oldValue, newValue) -> cardControlsChange());
        value.textProperty().addListener((observable, oldValue, newValue) -> cardControlsChange());
        workCard.setOnAction(event -> cardControlsChange());
        targetType.setOnAction(event -> cardControlsChange());
        addSpell.setOnAction(event -> addSpellPart());
        clearMgc.setOnAction(event -> mgcCmd.setText(""));

        plyHPAmuCheck.setOnAction(event -> amuletControlsChange());
        plyMPAmuCheck.setOnAction(event -> amuletControlsChange());
        playerHPAmu.textProperty().addListener((observable, oldValue, newValue) -> amuletControlsChange());
        playerMPAmu.textProperty().addListener((observable, oldValue, newValue) -> amuletControlsChange());

        plyHPItemCheck.setOnAction(event -> itemControlsChange());
        plyMPItemCheck.setOnAction(event -> itemControlsChange());
        playerHPItem.textProperty().addListener((observable, oldValue, newValue) -> itemControlsChange());
        playerMPItem.textProperty().addListener((observable, oldValue, newValue) -> itemControlsChange());

    }

    private void addSpellPart() {
        if (!nowMagicCommand.equals("")) {
            if (!nowMagicCommand.contains("()") && !nowMagicCommand.contains("#")) {
                if (!nowMagicCommand.endsWith(";") && !nowMagicCommand.endsWith("=") && !nowMagicCommand.endsWith("?")) {
                    mgcCmd.setText(mgcCmd.getText() + nowMagicCommand);
                }
            }
        }
    }

    private void cardControlsChange() {
        if (workCard.getValue() == null) {
            return;
        }
        String command = commands.get(workCard.getValue().trim());
        if (command.contains("#")) {
            value.setVisible(true);
            String hpValue = value.getText().trim();
            try {
                int val = Integer.parseInt(hpValue);
                if (val > 0) {
                    command = command.replace("#", String.valueOf(val));
                }
            } catch (NumberFormatException e) {
            }
        } else {
            value.setVisible(false);
        }
        if (command.endsWith("*")) {
            targetGroup.setVisible(true);
            command = command.substring(0, command.length() - 1);
        } else {
            targetGroup.setVisible(false);
        }
        String trg = targetType.getValue();
        if (trg == null) {
            trg = "All";
        }
        trg = trg.trim();
        if (trg.startsWith("User")) {
            trg = "?";
        } else if (trg.startsWith("Random")) {
            trg = ";";
        } else {
            trg = "=";
        }
        if (fplyCard.isSelected()) {
            trg += "fply-";
        }
        if (eplyCard.isSelected()) {
            trg += "eply-";
        }
        if (fsplCard.isSelected()) {
            trg += "fspl-";
        }
        if (esplCard.isSelected()) {
            trg += "espl-";
        }
        if (fmonCard.isSelected()) {
            String monTarget = fmonTarget.getText().trim();
            if (monTarget.equals("")) {
                trg += "fmon-";
            } else {
                String tribesT = "";
                boolean error = false;
                for (String oneT : monTarget.split("-")) {
                    String oneTC = oneT.trim();
                    if (oneTC.equals("")) {
                        continue;
                    } else {
                        try {
                            tribesT += "f(" + MonsterTribe.valueOf(oneTC) + ")-";
                        } catch (Exception e) {
                            try {
                                tribesT += "f(" + MonsterType.valueOf(oneTC) + ")-";
                            } catch (Exception e1) {
                                error = true;
                            }
                        }
                    }
                }
                if (error || tribesT.equals("")) {
                    trg += "fmon-";
                } else {
                    trg += tribesT;
                }
            }
        }
        if (emonCard.isSelected()) {
            String monTarget = emonTarget.getText().trim();
            if (monTarget.equals("")) {
                trg += "emon-";
            } else {
                String tribesT = "";
                boolean error = false;
                for (String oneT : monTarget.split("-")) {
                    String oneTC = oneT.trim();
                    if (oneTC.equals("")) {
                        continue;
                    } else {
                        try {
                            tribesT += "e(" + MonsterTribe.valueOf(oneTC) + ")-";
                        } catch (Exception e) {
                            try {
                                tribesT += "e(" + MonsterType.valueOf(oneTC) + ")-";
                            } catch (Exception e1) {
                                error = true;
                            }
                        }
                    }
                }
                if (error || tribesT.equals("")) {
                    trg += "emon-";
                } else {
                    trg += tribesT;
                }
            }
        }
        if (trg.endsWith("-")) {
            trg = trg.substring(0, trg.length() - 1);
        }
        if (!targetGroup.isVisible()) {
            trg = "";
        }
        nowMagicCommand = (command + trg).trim();
        System.out.println(nowMagicCommand);
    }

    private void initializeNewCard() {
        workCard.getItems().clear();
        for (Map.Entry<String, String> entry : commands.entrySet()) {
            workCard.getItems().add(entry.getKey());
        }
        targetType.getItems().clear();
        targetType.getItems().add("User Selection");
        targetType.getItems().add("Random Selection");
        targetType.getItems().add("All Affected");
        value.setText("");
        value.setVisible(false);
    }

    private void amuletControlsChange() {
        mgcCmd.setText("");
        if (plyHPAmuCheck.isSelected()) {
            String hpValue = playerHPAmu.getText().trim();
            try {
                int val = Integer.parseInt(hpValue);
                if (val > 0) {
                    mgcCmd.setText(mgcCmd.getText() + ":+MHP(" + hpValue + ")");
                }
            } catch (NumberFormatException e) {
            }
        }
        if (plyMPAmuCheck.isSelected()) {
            String mpValue = playerMPAmu.getText().trim();
            try {
                int val = Integer.parseInt(mpValue);
                if (val > 0) {
                    mgcCmd.setText(mgcCmd.getText() + ":+MMP(" + mpValue + ")");
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    private void itemControlsChange() {
        mgcCmd.setText("");
        if (plyHPItemCheck.isSelected()) {
            String hpValue = playerHPItem.getText().trim();
            try {
                int val = Integer.parseInt(hpValue);
                if (val > 0) {
                    mgcCmd.setText(mgcCmd.getText() + ":+HP(" + hpValue + ")");
                }
            } catch (NumberFormatException e) {
            }
        }
        if (plyMPItemCheck.isSelected()) {
            String mpValue = playerMPItem.getText().trim();
            try {
                int val = Integer.parseInt(mpValue);
                if (val > 0) {
                    mgcCmd.setText(mgcCmd.getText() + ":+MP(" + mpValue + ")");
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    private void newSpellMake() {
        String name = splName.getText().trim();
        String mgc = mgcCmd.getText().trim();
        String owner = ownerTxt.getText().trim();
        if (!owner.equals("") && !mgc.equals("") && !name.equals("")) {
            boolean error = false;
            List<Spell> spells = getGameManager().getCustomGamesManager().getSpellsManager().getSpellList();
            for (Spell spl : spells) {
                if (spl.getDescription().trim().toLowerCase().equals(name.toLowerCase())) {
                    error = true;
                }
            }
            if (!error) {
                Spell newSpell = new Spell(mgc, name, SpellOwner.valueOf(owner));
                getGameManager().getCustomGamesManager().getSpellsManager().addSpell(newSpell);
                getGameManager().getCustomGamesManager().refreshSpellsFile();
                getMainController().getNewSpellStage().close();
                getMainController().getEditSpellsCon().refreshList();
            }
        }
    }

    private void tabChangeRequest() {
        mgcCmd.setText("");
        ownerTxt.setText("");
        if (forCardTab.isSelected()) {
            ownerTxt.setText("ForCard");
        }
        if (forItemTab.isSelected()) {
            ownerTxt.setText("ForItem");
        }
        if (forAmuTab.isSelected()) {
            ownerTxt.setText("ForAmulet");
        }
    }
}
