package net.dirtcraft.dirtbot.commands.tickets;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandClass(TicketModule.class)
public class PopulateTicket extends CommandTicketStaff {

    public PopulateTicket(TicketModule module) {
        super(module);
    }
    
    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
    	boolean useBetterPopulation = false;
    	for(String arg : args) {
    		if(arg.contains("u:") || arg.contains("s:") || arg.contains("n:") || arg.contains("l:")) {
    			useBetterPopulation = true;
    		}
    	}
    	
    	if(!useBetterPopulation) {
    		useWorsePopulation(event, args);
    		return true;
    	}
    	
    	boolean updateLevel = false;
    	
    	//Initialize each variable
    	String user = null;
    	String server = null;
    	String level = null;
    	String name = null;
    	
    	//Populate all applicable strings
    	for(String arg : args) {
    		//Get username
    		if(user == null) {
    			if(arg.contains("u:")) {
    				user = args.stream().filter((u) -> u.startsWith("u:")).collect(Collectors.toList()).get(0);
        			user = user.substring(2);
    			}
    			//Remove Indicator
    		}
    		
    		//Get server
    		if(server == null) {
    			if(arg.contains("s:")) {
    				server = args.stream().filter((s) -> s.startsWith("s:")).collect(Collectors.toList()).get(0);
    				//Remove indicator
    				server = server.substring(2);
    				//Make sure server is valid
    				boolean serverCheck = false;
    		        for(List<String> serverInfo : DirtBot.getConfig().servers) {
    		            if(serverInfo.get(1).equalsIgnoreCase(server)) {
    		                serverCheck = true;
    		            }
    		        }
    		        if(!serverCheck) server = null;
    			}
    			
    		}
    		
    		//Get level
    		if(level == null) {
    			if(arg.contains("l:")) {
    				level = args.stream().filter((l) -> l.startsWith("l:")).collect(Collectors.toList()).get(0);
    				//Remove Indicator
    				level = level.substring(2);
    				//Make sure level is valid
    				try {
    					@SuppressWarnings("unused")
						Ticket.Level tl = Ticket.Level.valueOf(level.toUpperCase());
    				} catch (Exception e) {
    					level = null;
    					continue;
    				}
    			}
    		}
    		
    		//Get name
    		if(name == null) {
    			
    			name = "";
    	    	for(String string : args.stream().filter((n) -> !n.contains("l:") && !n.contains("u:") && !n.contains("s:")).collect(Collectors.toList())) {
    	    		name += string + "-";
    	    	}
    	    	if(name.length() >= 2) {
    	    		name = name.substring(2, name.length()-1);
    	    	} else {
    	    		name = null;
    	    	}
    		}
    	}
    	
    	//Get the ticket
    	Ticket ticket = getTicket(event);
    	
    	//Create the info string
    	StringBuilder ticketInfo = new StringBuilder();
    	
    	//Update user and add to info
    	if(user != null) {
    		ticketInfo.append("**Username:** " + user + "\n");
    		ticket.setUsername(user);
    	}
    	
    	//Update server and add to info
    	if(server != null) {
    		ticketInfo.append("**Server:** " + server.toUpperCase() + "\n");
            getModule().getTicketUtils().setTicketServer(ticket, server);
    	}
    	
    	//Update level and add to info
    	if(level != null) {
    		ticketInfo.append("**Level:** " + level.toUpperCase() + "\n");
    		ticket.setLevel(Ticket.Level.valueOf(level.toUpperCase()));
    		updateLevel = true;
    	}
    	
    	//Update name and add to info
    	if(name != null) {
    		ticketInfo.append("**Ticket Name:** " + name);
    		event.getTextChannel().getManager().setName(name + "-" + ticket.getId()).queue();
    	}
    	
    	//Finish up
    	getModule().getDatabaseHelper().modifyTicket(ticket);
    	getModule().getEmbedUtils().updateTicketHeaderMessage(ticket);
        getModule().getEmbedUtils().sendResponse(getModule().getEmbedUtils().getEmptyEmbed().addField("__Ticket Populated__", ticketInfo.toString(), false).build(), event.getTextChannel());
        getModule().getEmbedUtils().sendLog("Ticket Populated", ticketInfo.toString(), ticket, event.getMember());
        
        //Ping subscribers
        String pingMessage = "";
        for(User users : getModule().getTicketUtils().getNotificationSubscribers(ticket, getModule())) {
            pingMessage += "<@" + users.getId() + "> ";
        }
        if(updateLevel) {
            event.getTextChannel().sendMessage(pingMessage).queue();
        }
        
        return true;
    }
    
    public void useWorsePopulation(MessageReceivedEvent event, List<String> args) {
    	boolean serverValid = false;
        for(List<String> serverInfo : DirtBot.getConfig().servers) {
            if(serverInfo.get(1).toLowerCase().equals(args.get(1).toLowerCase())) {
                serverValid = true;
            }
        }
        if(serverValid) {
            Ticket ticket = getTicket(event);
            ticket.setUsername(args.get(0));
            getModule().getTicketUtils().setTicketServer(ticket, args.get(1).toLowerCase());
            getModule().getDatabaseHelper().modifyTicket(ticket);
            getModule().getEmbedUtils().updateTicketHeaderMessage(ticket);
            String newName = String.join("-", args.subList(2, args.size())) + "-" + ticket.getId();
            event.getTextChannel().getManager().setName(newName).queue();

            String eventInfo = "**Username:** " + ticket.getUsername(false) + "\n" +
                    "**Server::** " + ticket.getServer(false).toUpperCase() + "\n" +
                    "**Ticket Name:** " + newName;

            getModule().getEmbedUtils().sendResponse(getModule().getEmbedUtils().getEmptyEmbed().addField("__Ticket Populated__", eventInfo, false).build(), event.getTextChannel());
            getModule().getEmbedUtils().sendLog("Ticket Populated", eventInfo, ticket, event.getMember());

            String pingMessage = "";
            for(User user : getModule().getTicketUtils().getNotificationSubscribers(ticket, getModule())) {
                pingMessage += "<@" + user.getId() + "> ";
            }
            event.getTextChannel().sendMessage(pingMessage).queue();
        }
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("populate", "pop", "ticket"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Arrays.asList(new CommandArgument("indicators", "User Server Name... OR u:user s:server n:name l:level", 1, 0)));
    }
}
