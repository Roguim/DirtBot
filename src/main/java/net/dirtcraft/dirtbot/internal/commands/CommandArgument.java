package net.dirtcraft.dirtbot.internal.commands;

public class CommandArgument {

    private String name;
    private String description;
    private int minLength;
    private int maxLength;

    public CommandArgument(String name, String description, int minLength, int maxLength) {
        this.name = name;
        this.description = description;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMinLength() { return minLength; }
    public int getMaxLength() { return maxLength;}

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setMinLength(int minLength) { this.minLength = minLength; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }

    public boolean validArgument(String arg) {
        if(maxLength > 0) {
            if(arg.length() >= minLength && arg.length() <= maxLength) return true;
            else return false;
        } else if(arg.length() >= minLength) return true;
        return false;
    }
}
