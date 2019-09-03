package net.dirtcraft.dirtbot.internal.commands;

public class CommandArgument {

    private String name;
    private String description;
    private int minLength;
    private int maxLength;
    private boolean optional;

    public CommandArgument(String name, String description, int minLength, int maxLength) {
        this.name = name;
        this.description = description;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.optional = false;
    }

    public CommandArgument(String name, String description, int minLength, int maxLength, boolean optional) {
        this.name = name;
        this.description = description;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.optional = optional;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean validArgument(String arg) {
        if (isOptional()) return true;
        if (getMaxLength() > 0) {
            return arg.length() >= getMinLength() && arg.length() <= getMaxLength();
        } else return arg.length() >= getMinLength();
    }
}
