 

/** Class ChamferConeProjZ :
    Computes a cone of a chamfer mask which will be projected into the plan z = cste.
*/
 

import java.util.Vector;

public class ChamferConeProjZ extends ChamferCone
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
    ChamferConeProjZ(int a, int b, int c, int Abc, int Bac, int Cab, double dy, double dz) {
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

    ChamferConeProjZ(int a, int b, int c, int Abc, int Bac, int Cab) {
	this(a, b, c, Abc, Bac, Cab, DEFAULT_DY, DEFAULT_DZ);
    }

    ChamferConeProjZ(int a, int b, int c, double dy, double dz) {
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
	return new ChamferConeProjZ(a, b, c, Abc, Bac, Cab, dy, dz);
    }

    public ChamferCone createSym(int plane,
				 int a, int b, int c, 
				 int Abc, int Bac, int Cab) 
    {
	ChamferCone result;
	switch(plane)
	    {
	    case ChamferTriangulation.SYM_Y_EQ_Z:
		result = (ChamferConeProjY) new ChamferConeProjY(a, b, c, Abc, Bac, Cab, dy, dz);
		break;		
	    default:
	    case ChamferTriangulation.SYM_X_EQ_Y:
		result = (ChamferConeProjZ) new ChamferConeProjZ(a, b, c, Abc, Bac, Cab, dy, dz);
		break;
	    case ChamferTriangulation.SYM_X_EQ_Z:
		result = (ChamferConeProjX) new ChamferConeProjX(a, b, c, Abc, Bac, Cab, dy, dz);
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
	if ((a.z == 0) || (b.z == 0) || (c.z == 0))
	    throw (new Exception("You cannot project this cone on a plan z = cste")); 
	xA = (double) a.x / a.z;
	yA = (double) a.y / a.z;
	zA = 1.0;

	xB = (double) b.x / b.z;
	yB = (double) b.y / b.z;
	zB = 1.0;

	xC = (double) c.x / c.z;
	yC = (double) c.y / c.z;
	zC = 1.0;


	insideDeno = xA*(yB - yC) + xB*(yC - yA) + xC*(yA - yB);
	insideCoefs[0][0] = yB - yC; insideCoefs[0][1] = xC - xB; insideCoefs[0][2] = xB*yC - xC*yB;
	insideCoefs[1][0] = yC - yA; insideCoefs[1][1] = xA - xC; insideCoefs[1][2] = xC*yA - xA*yC;

	denoAB = yB - yA;
	if (denoAB != 0) {
	    PAB = (xB - xA) / denoAB;
	    QAB = (xA*yB - xB*yA) / denoAB;
	}
	else {
	    PAB = 0.0;
	    QAB = yA;
	}

	denoBC = yC - yB;
	if (denoBC != 0) {
	    PBC = (xC - xB) / denoBC;
	    QBC = (xB * yC - xC * yB) / denoBC;
	}
	else {
	    PBC = 0.0;
	    QBC = yB;
	}

	denoAC = yC - yA;
	if (denoAC != 0) {
	    PAC = (xC - xA) / denoAC;
	    QAC = (xA*yC - xC*yA) / denoAC;
	}
	else {
	    PAC = 0.0;
	    QAC = yA;
	}

    }

    private boolean isInTri(double Pty, double Ptx) {
	double alpha = (insideCoefs[0][0]*Ptx + insideCoefs[0][1]*Pty + insideCoefs[0][2]) / insideDeno;
	double beta  = (insideCoefs[1][0]*Ptx + insideCoefs[1][1]*Pty + insideCoefs[1][2]) / insideDeno;
	double gamma = 1 - alpha - beta;
	
	return ((alpha >= 0) && (alpha <= 1) &&
		(beta  >= 0) && (beta  <= 1) &&
		(gamma >= 0) && (gamma <= 1));

    }

    private boolean isInAB(double Pty) {
	if (denoAB != 0)
	    return (((Pty > yA) && (Pty < yB)) ||
		    ((Pty < yA) && (Pty > yB)));
	else
	    return (((Pty > xA) && (Pty < xB)) ||
		    ((Pty < xA) && (Pty > xB)));
    }
    private boolean isInBC(double Pty) {
	if (denoAB != 0)
	    return (((Pty > yB) && (Pty < yC)) ||
		    ((Pty < yB) && (Pty > yC)));
	else
	    return (((Pty > xB) && (Pty < xC)) ||
		    ((Pty < xB) && (Pty > xC)));
    }
    private boolean isInAC(double Pty) {
	if (denoAB != 0)
	    return (((Pty > yA) && (Pty < yC)) ||
		    ((Pty < yA) && (Pty > yC)));
	else
	    return (((Pty > xA) && (Pty < xC)) ||
		    ((Pty < xA) && (Pty > xC)));
    }

    private double errorFunction(double x, double y, double epsilon)
    {
	return ((alpha*x + beta*y + gamma) / 
		(epsilon*Math.sqrt(x*x + dy2*y*y + dz2)) - 1);
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
	double xMax, yMax;
	double extremum = 0;

	/*---------------------------------------------------------------------*/
	/* (a) Global extremum                                                 */
	/*---------------------------------------------------------------------*/
	double betaDy  =  beta / dy2;
	double gammaDz = gamma / dz2;
	xMax = alpha  / gammaDz;
	yMax = betaDy / gammaDz;
	if (isInTri(xMax, yMax)) {
	    extremum = errorFunction(xMax, yMax, epsilon);
	    errElmt.modify(extremum);
	}
	
	/*---------------------------------------------------------------------*/
	/* (b) Edges                                                           */
	/*---------------------------------------------------------------------*/
	/*---- Edge AB --------------------------------------------------------*/
	if (denoAB != 0) { // x = PAB*y + QAB 
	    yMax = ((alpha*PAB + beta) *dz2 + QAB*(beta*QAB  - gamma*PAB)) /
		   ((alpha*QAB + gamma)*dy2 + PAB*(gamma*PAB - beta*QAB));
	    if (isInAB(yMax)) {
		xMax = PAB*yMax + QAB;
		extremum = errorFunction(xMax, yMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // y = QAB 
	    xMax = alpha*(dy2*QAB*QAB + dz2) / (beta*QAB + gamma);
	    if (isInAB(xMax)) {
		yMax = QAB;
		extremum = errorFunction(xMax, yMax, epsilon);
		errElmt.modify(extremum);
	    }
	}

	/*---- Edge BC --------------------------------------------------------*/
	if (denoBC != 0) { // x = PBC*y + QBC 
	    yMax = ((alpha*PBC + beta) *dz2 + QBC*(beta*QBC  - gamma*PBC)) /
		   ((alpha*QBC + gamma)*dy2 + PBC*(gamma*PBC - beta*QBC));
	    if (isInBC(yMax)) {
		xMax = PBC*yMax + QBC;
		extremum = errorFunction(xMax, yMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // y = QBC 
	    xMax = alpha*(dy2*QBC*QBC + dz2) / (beta*QBC + gamma);
	    if (isInBC(xMax)) {
		yMax = QBC;
		extremum = errorFunction(xMax, yMax, epsilon);
		errElmt.modify(extremum);
	    }
	}

	/*---- Edge AC --------------------------------------------------------*/
	if (denoAC != 0) { // x = PAC*y + QAC 
	    yMax = ((alpha*PAC + beta) *dz2 + QAC*(beta*QAC  - gamma*PAC)) /
		   ((alpha*QAC + gamma)*dy2 + PAC*(gamma*PAC - beta*QAC));
	    if (isInAC(yMax)) {
		xMax = PAC*yMax + QAC;
		extremum = errorFunction(xMax, yMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // y = QAC 
	    xMax = alpha*(dy2*QAC*QAC + dz2) / (beta*QAC + gamma);
	    if (isInAC(xMax)) {
		yMax = QAC;
		extremum = errorFunction(xMax, yMax, epsilon);
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
