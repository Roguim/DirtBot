package net.dirtcraft.dirtbot.data;

public class Player {

    private String uniqueId;
    private String username;

    public Player(String uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.username = username;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getUsername() {
        return username;
    }
}
