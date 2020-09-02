package MenuControllers.GeneralClasses;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Menus {
/*    public static final String RESET = "\033[0m";  // Text Reset
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE*/

    public static final String RESET = "";//= "\033[0m";  // Text Reset
    public static final String BLACK = "";// = "\033[0;30m";   // BLACK
    public static final String RED = "";// = "\033[0;31m";     // RED
    public static final String GREEN = "";// = "\033[0;32m";   // GREEN
    public static final String YELLOW = "";// = "\033[0;33m";  // YELLOW
    public static final String BLUE = "";//= "\033[0;34m";    // BLUE
    public static final String PURPLE = "";// = "\033[0;35m";  // PURPLE
    public static final String CYAN = "";// = "\033[0;36m";    // CYAN
    public static final String WHITE = "";//= "\033[0;37m";   // WHITE


    public static String lastPrinted;
    private Map<String, MainController> controllers;
    private MainController firstController;
    private MainController nowController;

    public Menus() {
        this.controllers = new LinkedHashMap<>();
    }

    public static void print(String toPrint) {
        System.out.println(toPrint);
        lastPrinted = toPrint;
    }

    public MainController findController(String key) {
        return controllers.getOrDefault(key, null);
    }

    public Map<String, MainController> getControllers() {
        return controllers;
    }

    public void addController(String key, MainController controller, boolean isFirst) {
        controllers.put(key, controller);

        if (isFirst) {
            firstController = controller;
        }
    }

    public MainController getFirstController() {
        return firstController;
    }

    public void setFirstController(MainController firstController) {
        this.firstController = firstController;
    }

    public MainController getNowController() {
        return nowController;
    }

    public void setNowController(MainController nowController) {
        this.nowController = nowController;
    }

    public String execute(String CMD) {
        String temp = "";
        CMD = CMD.trim();
        String[] CMDParts = CMD.split("\\s");

        // Summary Execute
        {
            ArrayList<MenuItem> possible = new ArrayList<>();
            for (MenuItem item : nowController.getMenuItems()) {
                if (item.getCommand().trim().toLowerCase().replaceAll("\\s", "").contains(CMD.toLowerCase().replaceAll("\\s", ""))) {
                    if (!item.getCommand().contains("\"") && !item.getCommand().contains("#")) {
                        possible.add(item);
                    }
                }
            }
            if (possible.size() == 1) {
                temp = possible.get(0).getExec().run(new ArrayList<String>().toArray(new String[0]));
                if (!temp.equals("")) {
                    lastPrinted = temp;
                }
                return temp;
            }
        }


        ArrayList<String> args = new ArrayList<>();
        nextitem:
        for (MenuItem item : nowController.getMenuItems()) {
            args.clear();
            String[] itemParts = item.getCommand().split("\\s");

            if (itemParts.length == CMDParts.length) {

                for (int i = 0; i < CMDParts.length; i++) {

                    if (itemParts[i].startsWith("#")) {
                        args.add(CMDParts[i]);
                    } else if (itemParts[i].startsWith("\"") && itemParts[i].endsWith("\"")) {
                        if (CMDParts[i].startsWith("\"")) {
                            args.add(CMDParts[i].substring(1, CMDParts[i].length() - 1));
                        } else {
                            args.add(CMDParts[i]);
                        }
                    } else {
                        if (!CMDParts[i].equalsIgnoreCase(itemParts[i])) {
                            continue nextitem;
                        }
                    }


                }

                // RUN COMMAND

                temp = item.getExec().run(args.toArray(new String[0]));
                if (!temp.equals("")) {
                    lastPrinted = temp;
                }
                return temp;
            }
        }

        if (CMD.equalsIgnoreCase("again")) {
            System.out.println(lastPrinted);
            return "";
        }

        return "COMMAND CANNOT RECOGNIZE !";
    }
}
