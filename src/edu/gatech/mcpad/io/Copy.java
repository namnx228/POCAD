/**
 ***************************************************************************
 * Copyright (C) 2006, Davide Ariu                                         *
 * davide.ariu@diee.unica.it                                               *
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Copy {
	public static void fileCopy(String inputFileString, String outputFileString)
			throws IOException {
		File inputFile = new File(inputFileString);
		File outputFile = new File(outputFileString);

		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;

		try {
			inputStream = new FileInputStream(inputFile);
			outputStream = new FileOutputStream(outputFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1)
				outputStream.write(buffer, 0, bytesRead); // write
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					;
				}
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					;
				}
		}
	}
}