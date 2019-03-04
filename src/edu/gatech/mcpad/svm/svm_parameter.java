	/* ============================================================ *
     * The code below has been imported from the libSVM code by     *
	 * Chih-Chung Chang and Chih-Jen Lin                            *
	 * (http://www.csie.ntu.edu.tw/~cjlin/libsvm/)                  *
	 * and is therefore covered by the licence of libSVM.           *
	 * A few changes may have been applied to integrated the        *
	 * imported code with the rest of the software.                 *
	 * ============================================================ */

package edu.gatech.mcpad.svm;

import java.io.BufferedWriter;
import java.io.FileWriter;

import edu.gatech.mcpad.io.ConfigFileReader;

public class svm_parameter implements Cloneable,java.io.Serializable
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final int DEFAULT_CACHE_SIZE = 100;
	private static final double DEFAULT_EPSILON = 0.001;

	/* svm_type */
	public static final int C_SVC = 0;
	public static final int NU_SVC = 1;
	public static final int ONE_CLASS = 2;
	public static final int EPSILON_SVR = 3;
	public static final int NU_SVR = 4;

	/* kernel_type */
	public static final int LINEAR = 0;
	public static final int POLY = 1;
	public static final int RBF = 2;
	public static final int SIGMOID = 3;
	public static final int PRECOMPUTED = 4;

	public int svm_type;
	public int kernel_type;
	public int degree;	// for poly
	public double gamma;	// for poly/rbf/sigmoid
	public double coef0;	// for poly/sigmoid

	// these are for training only
	public double cache_size; // in MB
	public double eps;	// stopping criteria
	public double C;	// for C_SVC, EPSILON_SVR and NU_SVR
	public int nr_weight;		// for C_SVC
	public int[] weight_label;	// for C_SVC
	public double[] weight;		// for C_SVC
	public double nu;	// for NU_SVC, ONE_CLASS, and NU_SVR
	public double p;	// for EPSILON_SVR
	public int shrinking;	// use the shrinking heuristics
	public int probability; // do probability estimates

	public Object clone() 
	{
		try 
		{
			return super.clone();
		} catch (CloneNotSupportedException e) 
		{
			return null;
		}
	}

	public svm_parameter() {

	}

	public svm_parameter(String cfgFile) {
		try {
			ConfigFileReader cfr = new ConfigFileReader(cfgFile);

			svm_type = cfr.readInt("SVM_TYPE");
			kernel_type = cfr.readInt("KERNEL_TYPE");
			gamma = cfr.readDouble("GAMMA");
			nu = cfr.readDouble("FP_RATE");

			cache_size = DEFAULT_CACHE_SIZE;
			eps = DEFAULT_EPSILON;
		}
		catch(Exception e) {
			System.err.println("EXCEPTION in svm_parameter :: " + e);
			System.exit(1);
		}
	}

	public void print(){
		System.out.println("SVM_TYPE = " + svm_type);
		System.out.println("KERNEL_TYPE = " + kernel_type);
		System.out.println("GAMMA = " + gamma);
		System.out.println("SVM False Positive Rate = " + nu);

	}

	public void save(String cfgFileName){

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(cfgFileName));

			w.write("SVM_TYPE = " + svm_type);
			w.write(LINE_SEPARATOR);
			w.write("KERNEL_TYPE = " + kernel_type);
			w.write(LINE_SEPARATOR);
			w.write("GAMMA = " + gamma);
			w.write(LINE_SEPARATOR);
			w.write("FP_RATE = " + nu);
			w.write(LINE_SEPARATOR);
			w.close();

		}
		catch(Exception e) {
			System.err.println("EXCEPTION in ClusteringConfiguration::save -> " + e);
			System.exit(1);
		}


	}

}
