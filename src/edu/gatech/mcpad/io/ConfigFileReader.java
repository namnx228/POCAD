/**
 ***************************************************************************
 * Copyright (C) 2007, Roberto Perdisci                                    *
 * roberto.perdisci@google.com                                             *
 *                                                                         *
 * Distributed under the GNU Public License                                *
 * http://www.gnu.org/licenses/gpl.txt                                     *   
 *                                                                         *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 *                                                                         *             
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 ***************************************************************************
 */

package edu.gatech.mcpad.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.HashMap;

import edu.gatech.mcpad.exceptions.BadConfigFileException;
import edu.gatech.mcpad.exceptions.NoSuchParameterException;

/**
 * 
 * Read a generic configuration file. <br>
 * The syntax is <br><br>
 * 
 * # Comment line 1
 * # Comment line 2
 * # ...
 * \<parameter1\> = \<value1\>
 * \<parameter1\> = \<value1\>
 * ...
 * 
 * @author Roberto Perdisci (roberto.perdisci@google.com)
 *
 */
public class ConfigFileReader {
	
	private static final char COMMENT_CHAR = '#';
	private static final String LIST_SEPARATOR = ",";
	private static final String RANGE_SEPARATOR = "-"; 

	private Logger log;
	
	private HashMap cfgMap = new HashMap(); 
	
	public ConfigFileReader(String cfgFile) throws Exception {
		
		log = Logger.getLogger(this.getClass().getName());
		
		try {
			readConfigFile(cfgFile);
		}
		catch(FileNotFoundException e) {
			log.info("In ConfigFileReader.ConfigFileReader() :: " + e);
			throw e;
		}
	}
	
	private void readConfigFile(String cfgFile) throws Exception {
		
		BufferedReader r = new BufferedReader(new FileReader(cfgFile));
		
		int count = 0;
		String line;
		while((line = r.readLine())!=null) {
			count++;
			
			if(line.trim().length() == 0) 
				continue;
			if(line.trim().charAt(0) == COMMENT_CHAR)
				continue;
			
			StringTokenizer strTok = new StringTokenizer(line,"=");
			String parameter = strTok.nextToken().trim();
			
			if(!strTok.hasMoreTokens()) {
				throw new BadConfigFileException("Error on line " + count);
			}
			
			String value = strTok.nextToken().trim();
			cfgMap.put(parameter,value);
		}
		r.close();
		
	}
	
	public int readInt(String parameter) throws NoSuchParameterException {
		return Integer.parseInt(retrieveParameterValue(parameter));
	}
	
	public double readDouble(String parameter) throws NoSuchParameterException {
		return Double.parseDouble(retrieveParameterValue(parameter));
	}
	
	public boolean readBoolean(String parameter) throws NoSuchParameterException{
		return Boolean.parseBoolean(retrieveParameterValue(parameter));
	}
	
	public String readString(String parameter) throws NoSuchParameterException {
		return retrieveParameterValue(parameter);
	}
	
	private String retrieveParameterValue(String parameter) throws NoSuchParameterException {
		String p = (String)cfgMap.get(parameter);
		if(p==null)
			throw new NoSuchParameterException("Queried parameter = " + parameter);
		
		return p;
	}
	
	public int[] readIntVector(String parameter) throws NoSuchParameterException,BadConfigFileException {
		Vector<Integer> values = new Vector<Integer>();
		
		String par = retrieveParameterValue(parameter);
		String[] valStr = par.split(LIST_SEPARATOR);
		for(int i=0; i<valStr.length; i++) {
			if(!valStr[i].contains(RANGE_SEPARATOR))
				values.add(Integer.parseInt(valStr[i]));
			else {
				String[] rangeStr = valStr[i].split(RANGE_SEPARATOR);
				if(rangeStr.length!=2)
					throw new BadConfigFileException("WRONG LIST FORMAT : " + par);
				
				for(int j=Integer.parseInt(rangeStr[0]); j<=Integer.parseInt(rangeStr[1]); j++)
					values.add(j);
			}
			
		}
		
		int[] v = new int[values.size()];
		for(int i=0; i<v.length; i++)
			v[i] = values.get(i);
		
		return v;
	}
	
}
