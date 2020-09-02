package MenuControllers.GeneralClasses;

public class MenuItem {
    private String command;
    private String description;
    private RunAction exec;

    public MenuItem(String command, String description, RunAction exec) {
        this.command = command.trim().replaceAll(" Name\"", "Name\"");
        this.description = description.trim();
        this.exec = exec;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public RunAction getExec() {
        return exec;
    }

    @Override
    public String toString() {
        String res = command;
        /*int index = 0;
        while (command.contains("*")) {
            try {
                command = command.replaceFirst("\\*", "#" + fileds.get(index));
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            index++;
        }*/
        res = command + (description.equals("") ? "" : ": ") + description;
        return res;
    }
}
