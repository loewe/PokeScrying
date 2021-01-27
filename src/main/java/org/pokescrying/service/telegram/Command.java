package org.pokescrying.service.telegram;

public enum Command {
	PRIV_ASK_TO_ACTIVATE_RAID,
	PRIV_CONFIRM_RAID_ACTIVE,
	PRIV_ASK_SLOT_N_TYPE,
	PRIV_SELECT_SLOT_N_TYPE,
	PRIV_CONFIRM_SLOT,
	RAID_SELECT_SLOT_N_TYPE,
	RAID_ADD_SLOT,
	RAID_CANCEL_SLOT;
	
	public static final String SEPARATOR = "|";
}