 

/** Class ChamferCone : 
    Defines the projection of a cone of the chamfer mask generator on a plan
    x = 1, or y = 1, or z = 1.
*/
 

import java.util.Vector;

public abstract class ChamferCone
{
    /*--------------------------- Constants ---------------------------------*/
    public static final double DEFAULT_DY = 1.0;
    public static final double DEFAULT_DZ = 1.0;
    
    /*--------------------------- Fields ------------------------------------*/
    /** Index of the first  summit of the cone */
    public int A;
    /** Index of the second summit of the cone */
    public int B;
    /** Index of the third  summit of the cone */
    public int C;
    
    /** Symetrics of th points A, B and C in the triangulation.
	if 0 <= Xyz < nbPts : the symetric is determined 
	and is in the chamfer mask generator
	else if Xyz >= nbPts : the symetric is determined but 
	is not in the chamfer mask generator
	if Xyz < 0 : the symetric is not yet determined.
	Xyz = SYM_IN_GENERATOR : the symetric is in the generator
	Xyz = SYM_X_EQ_O       : symetrie in relation to P : x = 0
	Xyz = SYM_Y_EQ_O       : symetrie in relation to P : y = 0
	Xyz = SYM_Z_EQ_O       : symetrie in relation to P : z = 0
	Xyz = SYM_X_EQ_Y       : symetrie in relation to P : x = y
	Xyz = SYM_Y_EQ_Z       : symetrie in relation to P : y = z
    */	
    /** Index of the symetric of A (/BC) (may be > nbPts) */
    public int Abc;
    /** Index of the symetric of B (/AC) (may be > nbPts) */
    public int Bac;
    /** Index of the symetric of C (/AB) (may be > nbPts) */
    public int Cab;
    
    /** alpha = wA*(yB*zC - yC*zB) + wB*(yC*zA - yA*zC) + wC*(yA*zB - yB*zA);
	beta  = wA*(xC*zB - xB*zC) + wB*(xA*zC - xC*zA) + wC*(xB*zA - xA*zB);
	gamma = wA*(xB*yC - xC*yB) + wB*(xC*yA - xA*yC) + wC*(xA*yB - xB*yA);
	=> nC[0][0] = yB*zC - yC*zB, nC[0][1] = yC*zA - yA*zC, nC[0][2] = yA*zB - yB*zA
	=> nC[1][0] = xC*zB - xB*zC, nC[1][1] = xA*zC - xC*zA, nC[1][2] = xB*zA - xA*zB
	=> nC[2][0] = xB*yC - xC*yB, nC[2][1] = xC*yA - xA*yC, nC[2][2] = xA*yB - xB*yA 
    */
    public int[][] nC;
    protected int alpha;
    protected int beta;
    protected int gamma;

    protected double dy;
    protected double dz;
    protected double dy2;
    protected double dz2;
    
    protected double normeA;
    protected double normeB;
    protected double normeC;

    /*--------------------------- Initialization ----------------------------*/
    public void init(int a, int b, int c, int Abc, int Bac, int Cab, double dy, double dz) {
	A = a; 
	B = b; 
	C = c; 
	this.Abc = Abc; 
	this.Bac = Bac; 
	this.Cab = Cab;
	
	this.dy = dy;
	this.dy2 = dy*dy;
	this.dz = dz;
	this.dz2 = dz*dz;

	nC = new int[3][3];
	alpha = 0;
	beta  = 0;
	gamma = 0;

    }
    /*--------------------------- Accessors ---------------------------------*/

    /*--------------------------- Abstract Methods --------------------------*/
    public abstract void relativeError(RelErrorElmt errElmt,
				       Vector setOfPoints);


    public abstract ChamferCone createNew(int a, int b, int c, 
					  int Abc, int Bac, int Cab, 
					  double dy, double dz);
    public abstract ChamferCone createSym(int plane,
					  int a, int b, int c, 
					  int Abc, int Bac, int Cab);
 
    /*--------------------------- Other Methods -----------------------------*/
    public void initPreCalculation(Vector setOfPoints) throws Exception {
	ChamferTriElmt a = (ChamferTriElmt) setOfPoints.get(this.A);
	ChamferTriElmt b = (ChamferTriElmt) setOfPoints.get(this.B);
	ChamferTriElmt c = (ChamferTriElmt) setOfPoints.get(this.C);
	
	/* alpha = wA*(yB*zC - yC*zB) + wB*(yC*zA - yA*zC) + wC*(yA*zB - yB*zA);
	   beta  = wA*(xC*zB - xB*zC) + wB*(xA*zC - xC*zA) + wC*(xB*zA - xA*zB);
	   gamma = wA*(xB*yC - xC*yB) + wB*(xC*yA - xA*yC) + wC*(xA*yB - xB*yA);
	   => nC[0][0] = yB*zC - yC*zB, nC[0][1] = yC*zA - yA*zC, nC[0][2] = yA*zB - yB*zA
	   => nC[1][0] = xC*zB - xB*zC, nC[1][1] = xA*zC - xC*zA, nC[1][2] = xB*zA - xA*zB
	   => nC[2][0] = xB*yC - xC*yB, nC[2][1] = xC*yA - xA*yC, nC[2][2] = xA*yB - xB*yA 
	*/
	nC[0][0] = b.y*c.z - c.y*b.z; 
	nC[0][1] = c.y*a.z - a.y*c.z;
	nC[0][2] = a.y*b.z - b.y*a.z;
	
	nC[1][0] = c.x*b.z - b.x*c.z;
	nC[1][1] = a.x*c.z - c.x*a.z;
	nC[1][2] = b.x*a.z - a.x*b.z;
	
	nC[2][0] = b.x*c.y - c.x*b.y;
	nC[2][1] = c.x*a.y - a.x*c.y;
	nC[2][2] = a.x*b.y - b.x*a.y;

    }

}
