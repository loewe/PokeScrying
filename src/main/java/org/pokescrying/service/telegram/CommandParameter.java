package org.pokescrying.service.telegram;

import java.util.Base64;

public class CommandParameter {
	private Command command;
	
	private String[] parameters;
	
	private long chatId;
	
	private long raidId;

	public void parse(String string) {
		String param = new String(Base64.getDecoder().decode(string));
		parameters = param.split("|");
		int index = Integer.parseInt(parameters[0]);
		command = Command.values()[index];
		
		if (command.equals(Command.PRIV_ASK_TO_ACTIVATE_RAID)) {
			this.chatId = Long.parseLong(parameters[1]);
			this.raidId = Long.parseLong(parameters[2]);
		}
	}

	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

	public long getRaidId() {
		return raidId;
	}

	public void setRaidId(long raidId) {
		this.raidId = raidId;
	}

	public Command getCommand() {
		return command;
	}
}