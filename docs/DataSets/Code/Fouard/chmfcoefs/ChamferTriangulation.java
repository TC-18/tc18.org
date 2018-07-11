 

/** Class ChamferTriangulation :
    Defines the organisation of the chamfer mask generator into several cones.
*/
 

import java.util.Vector;
import java.util.Date;

public abstract class ChamferTriangulation
{
    /*--------------------------- Constants ---------------------------------*/
    public static final int SYM_IN_GENERATOR = -1;
    public static final int SYM_DONT_KNOW = -2;
    public static final int SYM_X_EQ_O = -10;
    public static final int SYM_Y_EQ_O = -11;
    public static final int SYM_Z_EQ_O = -12;
    public static final int SYM_X_EQ_Y = -13;
    public static final int SYM_X_EQ_Z = -14;
    public static final int SYM_Y_EQ_Z = -15;

    public static final int DEFAULT_MIN_WEIGHT = 1;
    public static final int DEFAULT_MAX_WEIGHT = 255;

    public static final double DEFAULT_DY = 1.0;
    public static final double DEFAULT_DZ = 1.0;
    
    /*--------------------------- Fields ------------------------------------*/
    /** Points of the chamfer mask generator */
    protected Vector maskPoints;
    /** Triangulation of the chamfer mask generator */
    protected Vector maskCones;
    
    /** Number of points of the chamfer mask generator.
	maskPoints can contain more points than nbPts: 
	the points strored after nbPts correspond to the symetric points
	of the chamfer mask generator points, used to compute local 
	convexity criterion.
    */
    protected int nbPts;

    /** width of the mask 
	(for Farey triangulation, oder of the triangulation)
    */
    protected int order;
    /** size of the final mask generator */
    protected int maskSize;
    

    /** Set of Local Convexity Conditions */
    protected Vector maskLCC;

    /** */
    protected double dy;
    protected double dz;

    /** */
    protected double defaultEpsilon;

    /** */
    private RelErrorElmt errElmtBest;


    private Date hBegin;

    /*--------------------------- Initialization ----------------------------*/
    protected void init(int order)
    {
	this.init(order, DEFAULT_DY, DEFAULT_DZ);
    }

    protected void init(int order, double dy, double dz)
    {
	this.maskPoints = new Vector();
	this.maskCones  = new Vector();
	this.maskLCC    = new Vector();
	this.nbPts = 0;
	this.order = order;
	this.maskSize = 0;
	this.defaultEpsilon = 1;
	this.dy = dy;
	this.dz = dz;
	this.errElmtBest = new RelErrorElmt(defaultEpsilon);

    }

    /*--------------------------- Accessors ---------------------------------*/
    /** */
    int getNbPts() {
	return this.nbPts;
    }
    /** */
    int getNbCones() {
	return maskCones.size();
    }


    /*--------------------------- Abstract Methods --------------------------*/
    public abstract float NInfinity(ChamferTriElmt elmt);
    public abstract float N1(ChamferTriElmt elmt);

    /*--------------------------- Other Methods -----------------------------*/
    public ChamferCone deleteCone(int index) {
	return ((ChamferCone) maskCones.remove(index));
    }
    
    public int[][][] getFinalMask() {
	int[][][] finalMask = new int[order+1][order+1][order+1];
	for (int k = 0; k < order+1; k++) {
	    for (int j = 0; j < order+1; j++) {
		for (int i = 0; i < order+1; i++) {
		    finalMask[i][j][k] = 0;
		}
	    }
	}
	return finalMask;
    }


    /** */
    protected int isInSet(ChamferTriElmt elmt) {
	for (int i = 0; i < maskPoints.size(); i++)
	    if (elmt.samePoint((ChamferTriElmt) maskPoints.get(i)))
		return i;
	return -1;
    }
    /** */
    private int isInSet(LCCElmt elmt) {
	for (int i = 0; i < maskLCC.size(); i++)
	    if (elmt.equals((LCCElmt) maskLCC.get(i)))
		return i;
	return -1;
    }
    /** */
    private int delta(ChamferTriElmt a, ChamferTriElmt b, ChamferTriElmt c) {
	return ((a.x * b.y * c.z) + (b.x * c.y * a.z) + (c.x * a.y * b.z)
		- (c.x * b.y * a.z) - (b.x * a.y * c.z) - (a.x * c.y * b.z));
    }

    /** Finds the symetrical points Abc, Bac, and Cab, and computes the
	Local Convexity Conditions coefficients.
	
	For each point Xyz (Abc, Bac, or Cab), 
	* if Xyz = SYM_IN_GENERATOR 
	finds the direct triangle (corresponding to a chamfer cone 
	projection) which share an edge with the considered cone.
	* if Xyz = SYM_X_EQ_O, SYM_Y_EQ_O, SYM_Z_EQ_O, SYM_X_EQ_Y or SYM_Y_EQ_Z
	computes the correct symetric point and add it to maskPoints if
	it is not already. The number of chamfer poitns nbPts doesn't change.
	* @throws an exception if Xyz = SYM_DONT_KNOW.
     */
    public void initLCC() throws Exception {
	ChamferCone cone, coneSym;
	ChamferTriElmt pt, ptSym;
	int aSym, bSym, cSym;
	int newIndex;
	LCCElmt lccElmt;
	ChamferTriElmt a, b, c, aBC, bAC, cAB;

	for (int i = 0; i < maskCones.size(); i++) {
	    cone = (ChamferCone) maskCones.get(i);
	    a   = (ChamferTriElmt) maskPoints.get(cone.A);
	    b   = (ChamferTriElmt) maskPoints.get(cone.B);
	    c   = (ChamferTriElmt) maskPoints.get(cone.C);

	    /*-- Symetric A / (BC) --*/
	    if (cone.Abc < 0) { 
		// if (Abc >= 0), Abc is already determined in the triangulation
		lccElmt = new LCCElmt();
		if (cone.Abc == SYM_DONT_KNOW)
		    throw (new Exception("Cannot find the symetry"));
		else if (cone.Abc == SYM_IN_GENERATOR) { 
		    // look for the symetric in the set of points
		    for (int j = 0; j < maskCones.size(); j++) {
			if (j != i) {
			    coneSym = (ChamferCone) maskCones.get(j);
			    // 3 possible commune edge
			    if ((cone.B == coneSym.A) && (cone.C == coneSym.C)) {
				cone.Abc = coneSym.B;
				coneSym.Bac = cone.A;
			    }
			    if ((cone.B == coneSym.B) && (cone.C == coneSym.A)) {
				cone.Abc = coneSym.C;
				coneSym.Cab = cone.A;
			    }
			    if ((cone.B == coneSym.C) && (cone.C == coneSym.B)) {
				cone.Abc = coneSym.A;
				coneSym.Abc = cone.A;
			    }
			}
		    }
		    if (cone.Abc == SYM_IN_GENERATOR)
			throw (new Exception("Symetric not in generator"));
		    lccElmt.w0 = cone.Abc;
		}
		else { 
		    // Add to the set of points
		    cone = (ChamferCone) maskCones.get(i);
		    ptSym = a.symetric(cone.Abc);
		    newIndex = isInSet(ptSym);
		    if (newIndex == -1) {
			newIndex = maskPoints.size();
			maskPoints.add(ptSym);
		    }
		    cone.Abc = newIndex;
		    lccElmt.w0 = cone.A;
		}
		aBC = (ChamferTriElmt) maskPoints.get(cone.Abc);
		lccElmt.w1 = cone.A;
		lccElmt.w2 = cone.B;
		lccElmt.w3 = cone.C;
		lccElmt.D1 = delta(aBC, b, c);
		lccElmt.D2 = delta(a, aBC, c);
		lccElmt.D3 = delta(a, b, aBC);
		if(isInSet(lccElmt) < 0)
		    maskLCC.add(lccElmt);
	    }

	    /*-- Symetric B / (AC) --*/
	    if (cone.Bac < 0) { 
		// if (Abc >= 0), Abc is already determined in the triangulation
		lccElmt = new LCCElmt();
		if (cone.Bac == SYM_DONT_KNOW)
		    throw (new Exception("Cannot find the symetry"));
		else if (cone.Bac == SYM_IN_GENERATOR) { 
		    // look for the symetric in the set of points
		    for (int j = 0; j < maskCones.size(); j++) {
			if (j != i) {
			    coneSym = (ChamferCone) maskCones.get(j);
			    // 3 possible commune edge
			    if ((cone.A == coneSym.A) && (cone.C == coneSym.B)) {
				cone.Bac = coneSym.C;
				coneSym.Cab = cone.B;
			    }
			    if ((cone.A == coneSym.B) && (cone.C == coneSym.C)) {
				cone.Bac = coneSym.A;
				coneSym.Abc = cone.B;
			    }
			    if ((cone.A == coneSym.C) && (cone.C == coneSym.A)) {
				cone.Bac = coneSym.B;
				coneSym.Bac = cone.B;
			    }
			}
		    }
		    if (cone.Bac == SYM_IN_GENERATOR)
			throw (new Exception("Symetric not in generator"));
		    lccElmt.w0 = cone.Bac;
		}
		else { 
		    // Add to the set of points
		    ptSym = b.symetric(cone.Bac);
		    newIndex = isInSet(ptSym);
		    if (newIndex == -1) {
			newIndex = maskPoints.size();
			maskPoints.add(ptSym);
		    }
		    cone.Bac = newIndex;
		    lccElmt.w0 = cone.B;
		}
		
		bAC = (ChamferTriElmt) maskPoints.get(cone.Bac);
		lccElmt.w1 = cone.A;
		lccElmt.w2 = cone.B;
		lccElmt.w3 = cone.C;
		lccElmt.D1 = delta(bAC, b, c);
		lccElmt.D2 = delta(a, bAC, c);
		lccElmt.D3 = delta(a, b, bAC);
		if(isInSet(lccElmt) < 0)
		    maskLCC.add(lccElmt);
	    }
		
	    /*-- Symetric C / (AB) --*/
	    if (cone.Cab < 0) { 
		// if (Cab >= 0), Cab is already determined in the triangulation
		lccElmt = new LCCElmt();
		if (cone.Cab == SYM_DONT_KNOW)
		    throw (new Exception("Can not find the symmetry"));
		else if (cone.Cab == SYM_IN_GENERATOR) { 
		    // look for the symetric in the set of points
		    for (int j = 0; j < maskCones.size(); j++) {
			if (j != i) {
			    coneSym = (ChamferCone) maskCones.get(j);
			    // 3 possible commune edge
			    if ((cone.A == coneSym.A) && (cone.B == coneSym.C)) {
				cone.Cab = coneSym.B;
				coneSym.Bac = cone.C;
			    }
			    if ((cone.A == coneSym.B) && (cone.B == coneSym.A)) {
				cone.Cab = coneSym.C;
				coneSym.Cab = cone.C;
			    }
			    if ((cone.A == coneSym.C) && (cone.B == coneSym.B)) {
				cone.Cab = coneSym.A;
				coneSym.Abc = cone.C;
			    }
			}
		    }
		    if (cone.Cab == SYM_IN_GENERATOR)
			throw (new Exception("Symmetric cone not in generator"));
		    lccElmt.w0 = cone.Cab;
		}
		else { 
		    // Add to the set of points
		    ptSym = c.symetric(cone.Cab);
		    newIndex = isInSet(ptSym);
		    if (newIndex == -1) {
			newIndex = maskPoints.size();
			maskPoints.add(ptSym);
		    }
		    cone.Cab = newIndex;
		    lccElmt.w0 = cone.C;
		}
	    
		cAB = (ChamferTriElmt) maskPoints.get(cone.Cab);
		lccElmt.w1 = cone.A;
		lccElmt.w2 = cone.B;
		lccElmt.w3 = cone.C;
		lccElmt.D1 = delta(cAB, b, c);
		lccElmt.D2 = delta(a, cAB, c);
		lccElmt.D3 = delta(a, b, cAB);
		
		if (isInSet(lccElmt) < 0)
		    maskLCC.add(lccElmt);
	    }
	}
    }
    
    
    /** Check the Local Convexity Criterion 
     * We test LCC only on the edges whose summit weight has already been determined
     * @param k : index of the last point which weight wk has been determined :
     *   the weight wi, i> k are not set yet.
     * @return false if one of the LCC tested condition is false
     *         true otherwise
     */
    public boolean verifiesLCC(int k) throws Exception{
	int largerIndex;
	LCCElmt lccElmt;
	for (int i = 0; i < maskLCC.size(); i++) {
	    lccElmt = (LCCElmt) maskLCC.get(i);
	    largerIndex = lccElmt.getLargestIndex();
	    
	    /* If the index i of the point is > k, the weight wi has not been
	     * determined yet. 
	     */
	    if (largerIndex  > k)
		lccElmt.flag = 2;
	   
	    /* every coefficients are smaller or equal to k
	     * if one is equal to k, the kst coefficient has changed
	     * ==> the condition must be re-tested
	     */
 	    else if (largerIndex == k) {
		ChamferTriElmt PtW0 = (ChamferTriElmt) maskPoints.get(lccElmt.w0);
		ChamferTriElmt PtW1 = (ChamferTriElmt) maskPoints.get(lccElmt.w1);
		ChamferTriElmt PtW2 = (ChamferTriElmt) maskPoints.get(lccElmt.w2);
		ChamferTriElmt PtW3 = (ChamferTriElmt) maskPoints.get(lccElmt.w3);
		if ((PtW0.w) >= (lccElmt.D1*PtW1.w + lccElmt.D2*PtW2.w + lccElmt.D3*PtW3.w))
		    lccElmt.flag = LCCElmt.LCC_TRUE;
		else {
		    lccElmt.flag = LCCElmt.LCC_FALSE;
		    return false;
		}
	    }

	    /* every coefficients are strictly smaller than k
	     * this condition must have been already tested, and must be true.
	     */
	    else
		if (lccElmt.flag != LCCElmt.LCC_TRUE)
		    throw (new Exception("The calculation goes on despite a false norm condition"));
	}
	
	return true; 
    }

    /** Computes pre-calculated coefficients to fasten error calculation */
    public void initPreCalculation() throws Exception {
	ChamferCone cone;
	try {
	    for (int i = 0; i < maskCones.size(); i++) {
		cone = (ChamferCone) maskCones.get(i);
		cone.initPreCalculation(maskPoints);
	    }
	} catch (Exception e) {throw e;}
    }

    public RelErrorElmt relativeError() {
	RelErrorElmt errElmt = new RelErrorElmt(defaultEpsilon);
	ChamferCone cone;
	for (int i = 0; i < maskCones.size(); i++) {
	    cone = (ChamferCone) maskCones.get(i);
	    cone.relativeError(errElmt, maskPoints);
	}
	return errElmt;
    }


    /** Computes chamfer mask generator coefficients 
     * @param minWeight : first weight of unit local displacement
     * @param maxWeight : last  weight of unit local displacement
     */
    public void chamferCoefCalculation(int minWeight, int maxWeight, int verbose) {
	Date hEnd;
	long duration, durationMS, h, m, s;

	try {
	    this.initLCC();
	    this.initPreCalculation();
	    hBegin = new Date();
	    for (int weight = minWeight; weight <= maxWeight; weight++) {
		defaultEpsilon = (double) weight;
		((ChamferTriElmt) maskPoints.get(0)).w = weight;
		recursiveChamferCoefCalculation(1);

		hEnd = new Date();
		durationMS = hEnd.getTime() - hBegin.getTime(); 
		duration = durationMS / 1000;
		h = duration / 3600;
		m = (duration % 3600) / 60;
		s = (duration % 60);
		if ( verbose > 1 ) 
		    System.out.println("End for w0 = " + weight + " Time: " + h + " h " + m + " mn " + s + " s i.e.: " + durationMS + " ms");
		else if ( verbose == 1 ) 
		    System.out.print( "w0 = " + weight + " done\r" );
		
	    }
	} catch(Exception e) {System.out.println(e);}

    }

    /** Computes the weight of the kiest point of the triangulation.
     *  If all the points of the calculation have been set (k = nbPts - 1), 
     *  computes the extrema of the error of the mask.
     *  keeps the mask wich optimal error is minimal.
     */ 
    public void recursiveChamferCoefCalculation(int level) throws Exception {
	ChamferTriElmt elmt = (ChamferTriElmt) maskPoints.get(level);
	int minW = (int) Math.round(defaultEpsilon * NInfinity(elmt));
	int maxW = (int) Math.round(defaultEpsilon * N1(elmt));
	//	int maxW = (int) Math.round(Math.min((defaultEpsilon * N1(elmt)), DEFAULT_MAX_WEIGHT));
	
	/* Ending case */
	if (level == nbPts - 1) {
	    Date hEnd;
	    long duration, durationMS, h, m, s;
	    for (int weight = minW; weight <= maxW; weight++) {
		elmt.w = weight;
		if (verifiesLCC(level)) {
		    RelErrorElmt errElmt = relativeError();
		    if (errElmt.getTauOpt() < errElmtBest.getTauOpt()) {
			errElmtBest = errElmt;
			System.out.println(this.printPoints());
			hEnd = new Date();
			durationMS = hEnd.getTime() - hBegin.getTime(); 
			duration = durationMS / 1000;
			h = duration / 3600;
			m = (duration % 3600) / 60;
			s = (duration % 60);
			System.out.println("TauOpt: " + errElmtBest.getTauOpt() + "\nEpsilonOpt: " + errElmtBest.getEpsilonOpt() + "\nTime: " + h + " h " + m + " mn " + s + " s i.e.: " + durationMS + " ms\n");
		    }
		}
	    }
	}
	/* Other cases */
	else {
	    for (int weight = minW; weight <= maxW; weight++) {
	       elmt.w = weight;
	       if (verifiesLCC(level))
		   recursiveChamferCoefCalculation(level + 1);
	    }
	}
    }
	


    public String printPoints() {
	ChamferTriElmt pt;
	String result = "\nMask Points:\n";
	for (int i = 0; i < nbPts; i++) {
	    pt = (ChamferTriElmt) maskPoints.get(i);
	    result += "w"+i + ": " + pt.toString() + "\n";
	}
	return result;
    }

    public String printCones() {
	ChamferTriElmt pt;
	ChamferCone cone;
	String result = "\nMask Cones:\n";
	for (int i = 0; i < maskCones.size(); i++) {
	    result += "{";
	    cone = (ChamferCone) maskCones.get(i);
	    pt = (ChamferTriElmt) maskPoints.get(cone.A);
	    result += pt.toString() + ", ";
	    pt = (ChamferTriElmt) maskPoints.get(cone.B);
	    result += pt.toString() + ", ";
	    pt = (ChamferTriElmt) maskPoints.get(cone.C);
	    result += pt.toString();
	    result += "}\n";
	}
	return result;
    }

    public String toString() {
	String result = "";
	result += this.printPoints();
	result += this.printCones();
	return result;
    }

    public String printSymetricCones() {
	ChamferTriElmt pt;
	ChamferCone cone;
	String result = "\nSymetric Cones:\n";
	for (int i = 0; i < maskCones.size(); i++) {
	    result += "{";
	    cone = (ChamferCone) maskCones.get(i);
	    pt = (ChamferTriElmt) maskPoints.get(cone.Abc);
	    result += pt.toString() + ", ";
	    pt = (ChamferTriElmt) maskPoints.get(cone.Bac);
	    result += pt.toString() + ", ";
	    pt = (ChamferTriElmt) maskPoints.get(cone.Cab);
	    result += pt.toString();
	    result += "}\n";
	}
	return result;
    }

    public String printLocalConvexityCriteria() {
	String result = "\nLocal Convexity Criteria:\n";
	for (int i = 0; i < maskLCC.size(); i++) 
	    result += ((LCCElmt) maskLCC.get(i)).toString() + "\n";

	return result;
	
    }

    public String printLCCConds() {
	String result = "";
	ChamferCone cone;
	ChamferTriElmt pt;
	result += "\nCones Symetrics\n";
	for (int i = 0; i < maskCones.size(); i++) {
	    result += "{";
	    cone = (ChamferCone) maskCones.get(i);
	    pt = (ChamferTriElmt) maskPoints.get(cone.Abc);
	    result += pt.toString() + ", ";
	    pt = (ChamferTriElmt) maskPoints.get(cone.Bac);
	    result += pt.toString() + ", ";
	    pt = (ChamferTriElmt) maskPoints.get(cone.Cab);
	    result += pt.toString();
	    result += "} ";
	}

	result += "\nLocal Convexity Criteria\n";
	for (int i = 0; i < maskLCC.size(); i++) 
	    result += ((LCCElmt) maskLCC.get(i)).toString() + "\n";

	return result;
    }

}
