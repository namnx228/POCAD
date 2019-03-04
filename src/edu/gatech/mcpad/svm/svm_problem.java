	/* ============================================================ *
     * The code below has been imported from the libSVM code by     *
	 * Chih-Chung Chang and Chih-Jen Lin                            *
	 * (http://www.csie.ntu.edu.tw/~cjlin/libsvm/)                  *
	 * and is therefore covered by the licence of libSVM.           *
	 * A few changes may have been applied to integrated the        *
	 * imported code with the rest of the software.                 *
	 * ============================================================ */

package edu.gatech.mcpad.svm;

public class svm_problem implements java.io.Serializable
{
	public int l;
	public double[] y;
	public svm_node[][] x;
}
