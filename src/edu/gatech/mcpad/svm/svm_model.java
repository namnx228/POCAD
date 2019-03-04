	/* ============================================================ *
     * The code below has been imported from the libSVM code by     *
	 * Chih-Chung Chang and Chih-Jen Lin                            *
	 * (http://www.csie.ntu.edu.tw/~cjlin/libsvm/)                  *
	 * and is therefore covered by the licence of libSVM.           *
	 * A few changes may have been applied to integrated the        *
	 * imported code with the rest of the software.                 *
	 * ============================================================ */

//
// svm_model
//
package edu.gatech.mcpad.svm;
public class svm_model implements java.io.Serializable
{
	svm_parameter param;	// parameter
	int nr_class;		// number of classes, = 2 in regression/one class svm
	int l;			// total #SV
	svm_node[][] SV;	// SVs (SV[l])
	double[][] sv_coef;	// coefficients for SVs in decision functions (sv_coef[k-1][l])
	double[] rho;		// constants in decision functions (rho[k*(k-1)/2])
	double[] probA;         // pariwise probability information
	double[] probB;

	// for classification only

	int[] label;		// label of each class (label[k])
	int[] nSV;		// number of SVs for each class (nSV[k])
				// nSV[0] + nSV[1] + ... + nSV[k-1] = l
};
