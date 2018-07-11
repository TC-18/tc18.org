 

/** Class ChamferConeProjX :
    Computes a cone of a chamfer mask which will be projected into the plan x = cste.
*/
 

import java.util.Vector;

public class ChamferConeProjX extends ChamferCone
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
    double insideCoefs[][];
    double insideDeno;

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
    ChamferConeProjX(int a, int b, int c, int Abc, int Bac, int Cab, double dy, double dz) {
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

    ChamferConeProjX(int a, int b, int c, int Abc, int Bac, int Cab) {
	this(a, b, c, Abc, Bac, Cab, DEFAULT_DY, DEFAULT_DZ);
    }

    ChamferConeProjX(int a, int b, int c, double dy, double dz) {
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
	return new ChamferConeProjX(a, b, c, Abc, Bac, Cab, dy, dz);
    }

    public ChamferCone createSym(int plane,
				 int a, int b, int c, 
				 int Abc, int Bac, int Cab)
    {
	ChamferCone result;
	switch(plane)
	    {
	    default:
	    case ChamferTriangulation.SYM_Y_EQ_Z:
		result = (ChamferConeProjX) new ChamferConeProjX(a, b, c, Abc, Bac, Cab, dy, dz);
		break;		
	    case ChamferTriangulation.SYM_X_EQ_Y:
		result = (ChamferConeProjY) new ChamferConeProjY(a, b, c, Abc, Bac, Cab, dy, dz);
		break;
	    case ChamferTriangulation.SYM_X_EQ_Z:
		result = (ChamferConeProjZ) new ChamferConeProjZ(a, b, c, Abc, Bac, Cab, dy, dz);
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
	if ((a.x == 0) || (b.x == 0) || (c.x == 0))
	    throw (new Exception("You cannot project this cone on a plan x = cste")); 
	xA = 1.0;
	yA = (double) a.y / a.x;
	zA = (double) a.z / a.x;

	xB = 1.0;
	yB = (double) b.y / b.x;
	zB = (double) b.z / b.x;

	xC = 1.0;
	yC = (double) c.y / c.x;
	zC = (double) c.z / c.x;


	insideDeno = yA*(zB - zC) + yB*(zC - zA) + yC*(zA - zB);
	insideCoefs[0][0] = zB - zC; insideCoefs[0][1] = yC - yB; insideCoefs[0][2] = yB*zC - yC*zB;
	insideCoefs[1][0] = zC - zA; insideCoefs[1][1] = yA - yC; insideCoefs[1][2] = yC*zA - yA*zC;

	denoAB = zB - zA;
	if (denoAB != 0) {
	    PAB = (yB - yA) / denoAB;
	    QAB = (yA*zB - yB*zA) / denoAB;
	}
	else {
	    PAB = 0.0;
	    QAB = zA;
	}

	denoBC = zC - zB;
	if (denoBC != 0) {
	    PBC = (yC - yB) / denoBC;
	    QBC = (yB * zC - yC * zB) / denoBC;
	}
	else {
	    PBC = 0.0;
	    QBC = zB;
	}

	denoAC = zB - zC;
	if (denoAC != 0) {
	    PAC = (yC - yA) / denoAC;
	    QAC = (yA*zC - yC*zA) / denoAC;
	}
	else {
	    PAC = 0.0;
	    QAC = zA;
	}
    
    }

    private boolean isInTri(double Pty, double Ptz) {
	double alpha = (insideCoefs[0][0]*Pty + insideCoefs[0][1]*Ptz + insideCoefs[0][2]) / insideDeno;
	double beta  = (insideCoefs[1][0]*Pty + insideCoefs[1][1]*Ptz + insideCoefs[1][2]) / insideDeno;
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
	    return (((Ptz > yA) && (Ptz < yB)) ||
		    ((Ptz < yA) && (Ptz > yB)));
    }
    private boolean isInBC(double Ptz) {
	if (denoAB != 0)
	    return (((Ptz > zB) && (Ptz < zC)) ||
		    ((Ptz < zB) && (Ptz > zC)));
	else
	    return (((Ptz > yB) && (Ptz < yC)) ||
		    ((Ptz < yB) && (Ptz > yC)));
    }
    private boolean isInAC(double Ptz) {
	if (denoAB != 0)
	    return (((Ptz > zA) && (Ptz < zC)) ||
		    ((Ptz < zA) && (Ptz > zC)));
	else
	    return (((Ptz > yA) && (Ptz < yC)) ||
		    ((Ptz < yA) && (Ptz > yC)));
    }


    private double errorFunction(double y, double z, double epsilon)
    {
	return ((alpha + beta*y + gamma*z) / 
		(epsilon*Math.sqrt(1 + dy2*y*y + dz2*z*z)) - 1);
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
	double yMax, zMax;
	double extremum = 0;

	/*---------------------------------------------------------------------*/
	/* (a) Global extremum                                                 */
	/*---------------------------------------------------------------------*/
	double betaDy = beta / dy2;
	yMax = betaDy  / alpha;
	zMax = gamma / (alpha*dz2);
	if (isInTri(yMax, zMax)) {
	    extremum = errorFunction(yMax, zMax, epsilon);
	    errElmt.modify(extremum);
	}
	
	/*---------------------------------------------------------------------*/
	/* (b) Edges                                                           */
	/*---------------------------------------------------------------------*/
	/*---- Edge AB --------------------------------------------------------*/
	if (denoAB != 0) { // y = PAB*z + QAB 
	    zMax = (QAB*(gamma*QAB - alpha*PAB)*dy2 + (beta*PAB + gamma)) / 
		   (PAB*(alpha*PAB - gamma*QAB)*dy2 + (alpha + beta*QAB)*dz2);
	    if (isInAB(zMax)) {
		yMax = PAB*zMax + QAB;
		extremum = errorFunction(yMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // z = QAB 
	    yMax = betaDy * (1 + dz2*QAB*QAB) / (alpha + gamma*QAB);
	    if (isInAB(yMax)) {
		zMax = QAB;
		extremum = errorFunction(yMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}

	/*---- Edge BC --------------------------------------------------------*/
	if (denoBC != 0) { // y = PBC*z + QBC 
	    zMax = (QBC*(gamma*QBC - alpha*PBC)*dy2 + (beta*PBC + gamma)) / 
		   (PBC*(alpha*PBC - gamma*QBC)*dy2 + (alpha + beta*QBC)*dz2);
	    if (isInBC(zMax)) {
		yMax = PBC*zMax + QBC;
		extremum = errorFunction(yMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // z = QBC 
	    yMax = betaDy * (1 + dz2*QBC*QBC) / (alpha + gamma*QBC);
	    if (isInBC(yMax)) {
		zMax = QBC;
		extremum = errorFunction(yMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}


	/*---- Edge AC --------------------------------------------------------*/
	if (denoAC != 0) { // y = PAC*z + QAC 
	    zMax = (QAC*(gamma*QAC - alpha*PAC)*dy2 + (beta*PAC + gamma)) / 
		   (PAC*(alpha*PAC - gamma*QAC)*dy2 + (alpha + beta*QAC)*dz2);
	    if (isInAC(zMax)) {
		yMax = PAC*zMax + QAC;
		extremum = errorFunction(yMax, zMax, epsilon);
		errElmt.modify(extremum);
	    }
	}
	else {             // z = QAC 
	    yMax = betaDy * (1 + dz2*QAC*QAC) / (alpha + gamma*QAC);
	    if (isInAC(yMax)) {
		zMax = QAC;
		extremum = errorFunction(yMax, zMax, epsilon);
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
