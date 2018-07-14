 

/** Class ChamferConeProjY :
    Computes a cone of a chamfer mask which will be projected into the plan y = cste.
*/
 

import java.util.Vector;

public class ChamferConeProjY extends ChamferCone
{
    /*--------------------------- Fields ------------------------------------*/
    /** For the error calculation, we use a projection of the triangle
	on a plan x = cst or y = cste or z = cste
    */
    private double xA;
    private double xB;
    private double xC;
    private double yA;
    private double zA;
    private double yB;
    private double zB;
    private double yC;
    private double zC;

    // For quick error calculation
    private double insideCoefs[][];
    private double insideDeno;

    private double denoAB;
    private double PAB;
    private double QAB;

    private double denoBC;
    private double PBC;
    private double QBC;

    private double denoAC;
    private double PAC;
    private double QAC;


    /*--------------------------- Constructors ------------------------------*/
    ChamferConeProjY(int a, int b, int c, int Abc, int Bac, int Cab, double dy, double dz) {
	// Initialization 
	super.init(a, b, c, Abc, Bac, Cab, dy, dz);

	// Pre-calculations to fasten the error calculation.
	xA = 0; xB = 0; xC = 0;
	yA = 0; yB = 0; yC = 0;
	zA = 0; zB = 0; zC = 0;

	insideCoefs = new double[2][3];
	insideDeno  = 1.0;

	denoAB = 0; PAB = 0; QAB = 0;
	denoBC = 0; PBC = 0; QBC = 0;
	denoAC = 0; PAC = 0; PBC = 0;

    }

    ChamferConeProjY(int a, int b, int c, int Abc, int Bac, int Cab) {
	this(a, b, c, Abc, Bac, Cab, DEFAULT_DY, DEFAULT_DZ);
    }

    ChamferConeProjY(int a, int b, int c, double dy, double dz) {
	this(a, b, c, 
	     ChamferTriangulation.SYM_DONT_KNOW,
	     ChamferTriangulation.SYM_DONT_KNOW,
	     ChamferTriangulation.SYM_DONT_KNOW,
	     dy, dz);
    }

    public ChamferCone createNew(int a, int b, int c, 
					  int Abc, int Bac, int Cab, 
					  double dy, double dz)
    {
	return new ChamferConeProjY(a, b, c, Abc, Bac, Cab, dy, dz);
    }

    public ChamferCone createSym(int plane,
				 int a, int b, int c, 
				 int Abc, int Bac, int Cab)
    {
	ChamferCone result;
	switch(plane)
	    {
	    case ChamferTriangulation.SYM_Y_EQ_Z:
		result = (ChamferConeProjZ) new ChamferConeProjZ(a, b, c, Abc, Bac, Cab, dy, dz);
		break;		
	    case ChamferTriangulation.SYM_X_EQ_Y:
		result = (ChamferConeProjX) new ChamferConeProjX(a, b, c, Abc, Bac, Cab, dy, dz);
		break;
	    default:
	    case ChamferTriangulation.SYM_X_EQ_Z:
		result = (ChamferConeProjY) new ChamferConeProjY(a, b, c, Abc, Bac, Cab, dy, dz);
		break;
	    }
	return result;
    }



    /*--------------------------- Other Methods -----------------------------*/
    public void initPreCalculation(Vector setOfPoints) throws Exception {
	super.initPreCalculation(setOfPoints);
	
	ChamferTriElmt a = (ChamferTriElmt) setOfPoints.get(this.A);
	ChamferTriElmt b = (ChamferTriElmt) setOfPoints.get(this.B);
	ChamferTriElmt c = (ChamferTriElmt) setOfPoints.get(this.C);

	normeA = Math.sqrt(a.x*a.x + dy2*a.y*a.y + dz2*a.z*a.z);
	normeB = Math.sqrt(b.x*b.x + dy2*b.y*b.y + dz2*b.z*b.z);
	normeC = Math.sqrt(c.x*c.x + dy2*c.y*c.y + dz2*c.z*c.z);
	
	// Projection of the points on the plan x = 1
	if ((a.y == 0) || (b.y == 0) || (c.y == 0))
	    throw (new Exception("You cannot project this cone on a plan y = cste")); 
	xA = (double) a.x / a.y;
	yA = (double) 1.0;
	zA = (double) a.z / a.y;

	xB = (double) b.x / b.y;
	yB = 1.0;
	zB = (double) b.z / b.y;

	xC = (double) c.x / c.y;
	yC = 1.0;
	zC = (double) c.z / c.y;


	insideDeno = zA*(xB - xC) + zB*(xC - xA) + zC*(xA - xB);
	insideCoefs[0][0] = xB - xC; insideCoefs[0][1] = zC - zB; insideCoefs[0][2] = zB*xC - zC*xB;
	insideCoefs[1][0] = xC - xA; insideCoefs[1][1] = zA - zC; insideCoefs[1][2] = zC*xA - zA*xC;

	denoAB = zB - zA;
	if (denoAB != 0) {
	    PAB = (xB - xA) / denoAB;
	    QAB = (xA*zB - xB*zA) / denoAB;
	}
	else {
	    PAB = 0.0;
	    QAB = zA;
	}

	denoBC = zC - zB;
	if (denoBC != 0) {
	    PBC = (xC - xB) / denoBC;
	    QBC = (xB * zC - xC * zB) / denoBC;
	}
	else {
	    PBC = 0.0;
	    QBC = zB;
	}

	denoAC = zC - zA;
	if (denoAC != 0) {
	    PAC = (xC - xA) / denoAC;
	    QAC = (xA*zC - xC*zA) / denoAC;
	}
	else {
	    PAC = 0.0;
	    QAC = zA;
	}

    }

    private boolean isInTri(double Ptx, double Ptz) {
	double alpha = (insideCoefs[0][0]*Ptz + insideCoefs[0][1]*Ptx + insideCoefs[0][2]) / insideDeno;
	double beta  = (insideCoefs[1][0]*Ptz + insideCoefs[1][1]*Ptx + insideCoefs[1][2]) / insideDeno;
	double gamma = 1 - alpha - beta;
	
	return ((alpha >= 0) && (alpha <= 1) &&
		(beta  >= 0) && (beta  <= 1) &&
		(gamma >= 0) && (gamma <= 1));

    }

    private boolean isInAB(double Ptz) {
	if (denoAB != 0)
	    return (((Ptz > zA) && (Ptz < zB)) ||
		    ((Ptz < zA) && (Ptz > zB)));
	else
	    return (((Ptz > xA) && (Ptz < xB)) ||
		    ((Ptz < xA) && (Ptz > xB)));
    }
    private boolean isInBC(double Ptz) {
	if (denoAB != 0)
	    return (((Ptz > zB) && (Ptz < zC)) ||
		    ((Ptz < zB) && (Ptz > zC)));
	else
	    return (((Ptz > xB) && (Ptz < xC)) ||
		    ((Ptz < xB) && (Ptz > xC)));
    }
    private boolean isInAC(double Ptz) {
	if (denoAB != 0)
	    return (((Ptz > zA) && (Ptz < zC)) ||
		    ((Ptz < zA) && (Ptz > zC)));
	else
	    return (((Ptz > xA) && (Ptz < xC)) ||
		    ((Ptz < xA) && (Ptz > xC)));
    }

    private double errorFunction(double x, double z, double epsilon)
    {
	return ((alpha*x + beta + gamma*z) / 
		(epsilon*Math.sqrt(x*x + dy2 + dz2*z*z)) - 1);
    }

    public void relativeError(RelErrorElmt errElmt, 
			      Vector setOfPoints) {

	ChamferTriElmt a = (ChamferTriElmt) setOfPoints.get(A);
	ChamferTriElmt b = (ChamferTriElmt) setOfPoints.get(B);
	ChamferTriElmt c = (ChamferTriElmt) setOfPoints.get(C);

	alpha = a.w * nC[0][0] + b.w * nC[0][1] + c.w * nC[0][2];
	beta  = a.w * nC[1][0] + b.w * nC[1][1] + c.w * nC[1][2];
	gamma = a.w * nC[2][0] + b.w * nC[2][1] + c.w * nC[2][2];

	double epsilon = errElmt.getEpsilon();
	double xMax, zMax;
	double extremum = 0;

	/*---------------------------------------------------------------------*/
	/* (a) Global extremum                                                 */
	/*---------------------------------------------------------------------*/
	double betaDy = beta / dy2;
	xMax = alpha / betaDy;
	zMax = gamma / betaDy;
	if (isInTri(xMax, zMax)) {
	    extremum = errorFunction(xMax, zMax, epsilon);
	    errElmt.modify(extremum);
	}
	
	/*---------------------------------------------------------------------*/
	/* (b) Edges                                                           */
	/*---------------------------------------------------------------------*/
	/*---- Edge AB --------------------------------------------------------*/
	if (denoAB != 0) { // x = PAB*z + QAB 
	    zMax = ((alpha*PAB + gamma)*dy2 + QAB*(gamma*QAB - beta*PAB)) /
		   ((alpha*QAB + beta)*dz2 + PAB*(beta*PAB - gamma*QAB));
	    if (isInAB(zMax)) {
		xMax = PAB*zMax + QAB;
		extremum = errorFunction(xMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // z = QAB 
	    xMax = alpha*(dy2 + dz2*QAB*QAB) / (beta + gamma*QAB);
	    if (isInAB(xMax)) {
		zMax = QAB;
		extremum = errorFunction(xMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}

	/*---- Edge BC --------------------------------------------------------*/
	if (denoBC != 0) { // x = PBC*z + QBC 
	    zMax = ((alpha*PBC + gamma)*dy2 + QBC*(gamma*QBC - beta*PBC)) /
		   ((alpha*QBC + beta)*dz2 + PBC*(beta*PBC - gamma*QBC));
	    if (isInBC(zMax)) {
		xMax = PBC*zMax + QBC;
		extremum = errorFunction(xMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // z = QBC 
	    xMax = alpha*(dy2 + dz2*QBC*QBC) / (beta + gamma*QBC);
	    if (isInBC(xMax)) {
		zMax = QBC;
		extremum = errorFunction(xMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}


	/*---- Edge AC --------------------------------------------------------*/
	if (denoAC != 0) { // x = PAC*z + QAC 
	    zMax = ((alpha*PAC + gamma)*dy2 + QAC*(gamma*QAC - beta*PAC)) /
		   ((alpha*QAC + beta)*dz2 + PAC*(beta*PAC - gamma*QAC));
	    if (isInAC(zMax)) {
		xMax = PAC*zMax + QAC;
		extremum = errorFunction(xMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // z = QAC 
	    xMax = alpha*(dy2 + dz2*QAC*QAC) / (beta + gamma*QAC);
	    if (isInAC(xMax)) {
		zMax = QAC;
		extremum = errorFunction(xMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}


	/*---------------------------------------------------------------------*/
	/* (c) Summits                                                         */
	/*---------------------------------------------------------------------*/
	/*---- Summit A -------------------------------------------------------*/
	extremum = a.w / (epsilon*normeA) - 1;
	errElmt.modify(extremum);
	/*---- Summit B -------------------------------------------------------*/
	extremum = b.w / (epsilon*normeB) - 1;
	errElmt.modify(extremum);
	/*---- Summit C -------------------------------------------------------*/
	extremum = c.w / (epsilon*normeC) - 1;
	errElmt.modify(extremum);

    }
    
    
}
