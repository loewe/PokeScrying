package org.pokescrying.service.telegram;

import java.util.Base64;

public class CommandParameter {
	private Command command;
	
	private long chatId;
	
	private long raidId;
	
	private long option;

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

	public long getOption() {
		return option;
	}

	public void setOption(long option) {
		this.option = option;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}

	public static String createPrivAskToActivateRaidString(Long chatId, Long raidId) {
		CommandParameter parameter = new CommandParameter();
		parameter.setCommand(Command.PRIV_ASK_TO_ACTIVATE_RAID);
		parameter.setChatId(chatId);
		parameter.setRaidId(raidId);
		return parameter.marshall();
	}

	public void unmarshall(String string) {
		String param = new String(Base64.getDecoder().decode(string));
		String[] parameters = param.split("\\|");
		int index = Integer.parseInt(parameters[0]);
		try {
			command = Command.values()[index];
		}
		catch (Exception e) {
			return;
		}
		
		if (command.equals(Command.PRIV_ASK_TO_ACTIVATE_RAID)) {
			this.chatId = Long.parseLong(parameters[1]);
			this.raidId = Long.parseLong(parameters[2]);
		}
		else if (command.equals(Command.PRIV_CONFIRM_RAID_ACTIVE)) {
			this.chatId = Long.parseLong(parameters[1]);
			this.raidId = Long.parseLong(parameters[2]);
			this.option = Long.parseLong(parameters[3]);
		}
	}

	public String marshall() {
		if(command.equals(Command.PRIV_ASK_TO_ACTIVATE_RAID)) {
			String join = String.join("|", Integer.toString(this.command.ordinal()), Long.toString(this.chatId), Long.toString(this.raidId));
			return Base64.getEncoder().encodeToString(join.getBytes());
		}
		else if(command.equals(Command.PRIV_CONFIRM_RAID_ACTIVE)) {
			String join = String.join("|", 
					Integer.toString(this.command.ordinal()), 
					Long.toString(this.chatId), 
					Long.toString(this.raidId), 
					Long.toString(this.option));
			return Base64.getEncoder().encodeToString(join.getBytes());
		}
		else
			return "";
	}

	@Override
	public String toString() {
		return "CommandParameter [command=" + command + ", chatId=" + chatId + ", raidId=" + raidId + ", option="
				+ option + "]";
	}
}