/**
 ***************************************************************************
 * Copyright (C) 2007, Davide Ariu                                         *
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

package edu.gatech.mcpad.utils;

import java.util.Arrays;
import java.io.File;
import java.io.IOException;

import edu.gatech.mcpad.svm.svm;
import edu.gatech.mcpad.svm.svm_model;
import edu.gatech.mcpad.svm.svm_node;

public class TestSVMHandler {

	private DirectoryHandler directoryHandler;

	public TestSVMHandler(DirectoryHandler directoryHandler) {
		this.directoryHandler = directoryHandler;
	}

	private static svm_model loadModel(String svmModelFile) throws IOException {
		svm_model model = svm.svm_load_model(svmModelFile);
		return model;
	}

	public svm_model[] loadModels() throws IOException {

		File[] modelFiles = directoryHandler.getModelsDir().listFiles();
		Arrays.sort(modelFiles);
		
		int numModels = modelFiles.length;
		
		svm_model[] models = new svm_model[numModels];

		for (int j = 0; j < numModels; j++) {

			System.out.println(modelFiles[j].toString());
			models[j] = loadModel(modelFiles[j].toString());
		}

		return models;
	}

	public static double predictLabel(double[] frequencies, svm_model model)
			throws IOException {
		int m = frequencies.length;
		svm_node[] x = new svm_node[m];
		for (int j = 0; j < m; j++) {
			x[j] = new svm_node();
			x[j].index = j + 1;
			x[j].value = frequencies[j];
		}

		return svm.svm_predict(model, x);
	}

	public static double predictProbability(double[] frequencies,
			svm_model model, int numOfFeatures) throws IOException {

		int m = frequencies.length;
		svm_node[] x = new svm_node[m];
		for (int j = 0; j < m; j++) {
			x[j] = new svm_node();
			x[j].index = j + 1;
			x[j].value = frequencies[j];
		}

		return svm.svm_predict_probabilities(model, x, numOfFeatures);
	}

}
