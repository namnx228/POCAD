package uet.nam;

import java.util.ArrayList;



public class General {
	private int g_index = 0;
	private boolean isFirstNu = true;
	private int indexPacket = 0;
	private ArrayList<Boolean> pick = new ArrayList<Boolean>();
	
	private static General instance;
	public static General getInstance()
	
	{
		if (instance == null) instance = new General();
		return instance;
	}
	public void incIndex()
	{
		g_index+=160;
	}
	public int getIndex() 
	{
		return g_index;
	}
	
	public void notFirstAnyMore()
	{
		isFirstNu = false;
	}
	
	public boolean getIsFirstNu() {
		return isFirstNu;
		
	}
	
	public void increaseIndexPacket()
	{
		indexPacket++;
	}
	
	public void resetIndexPacket() {
		indexPacket = 0;
		
	}
	
	public int getIndexPacket()
	{
		return indexPacket;
	}
	
	public ArrayList<Boolean> getPick()
	{
		return pick;
	}

}
