package net.dirtcraft.dirtbot.data;

public class Appeal {

    private String username;
    private String server;
    private PunishmentType type;
    private String staff;
    private String explanation;
    private String channelID;

    public Appeal(String channelID) {
        this.channelID = channelID;
        this.username = null;
        this.server = null;
        this.type = null;
        this.staff = null;
        this.explanation = null;
    }

    public String getUsername() { return username; }
    public String getServer() { return server; }
    public PunishmentType getPunishmentType() { return type; }
    public String getStaff() { return staff; }
    public String getExplanation() { return explanation; }
    public String getChannelID() { return channelID; }

    public void setUsername(String username) { this.username = username; }
    public void setServer(String server) { this.server = server; }
    public void setPunishmentType(PunishmentType type) { this.type = type; }
    public void setStaff(String staff) { this.staff = staff; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setChannel(String channel) { this.channelID = channel; }

    public enum PunishmentType {
        MUTE,
        BAN
    }
}
