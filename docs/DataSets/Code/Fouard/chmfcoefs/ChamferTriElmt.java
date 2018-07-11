 

/** Class ChamferTriElmt : 
    Fields defining the weighted vector of a chamfer mask generator 
*/
 

public class ChamferTriElmt
{
    /*--------------------------- Fields ------------------------------------*/
    /** X-coordinate of the vector */
    public int x;
    /** Y-coordinate of the vector */
    public int y;
    /** Z-coordinate of the vector */
    public int z;
    /** weight of the vector */
    public int w;

    
    /*--------------------------- Constructors ------------------------------*/
    ChamferTriElmt(int x, int y, int z, int w) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.w = w;
    }
    ChamferTriElmt(int x, int y, int z) {
	this(x, y, z, 0);
    }
    ChamferTriElmt() {
	this(0, 0, 0, 0);
    }

    /** Copy */
    ChamferTriElmt(ChamferTriElmt elmt) {
	this(elmt.x, elmt.y, elmt.z, elmt.w);
    }
    
    /** Symetrics */
    ChamferTriElmt symetric(int symFlag) {
	ChamferTriElmt result;
	switch (symFlag)
	    {
	    case ChamferTriangulation.SYM_X_EQ_Y :
		result = new ChamferTriElmt(this.y, this.x, this.z, this.w);
		break;
	    case ChamferTriangulation.SYM_X_EQ_Z :
		result = new ChamferTriElmt(this.z, this.y, this.x, this.w);
		break;
	    case ChamferTriangulation.SYM_Y_EQ_Z :
		result = new ChamferTriElmt(this.x, this.z, this.y, this.w);
		break;
	    case ChamferTriangulation.SYM_X_EQ_O :
		result = new ChamferTriElmt(- this.x, this.y, this.z, this.w);
		break;
	    case ChamferTriangulation.SYM_Y_EQ_O :
		result = new ChamferTriElmt(this.x, - this.y, this.z, this.w);
		break;
	    case ChamferTriangulation.SYM_Z_EQ_O :
		result = new ChamferTriElmt(this.x, this.y, - this.z, this.w);
		break;
	    default :
		result = new ChamferTriElmt(this);
		break;
	    }
	return result;
    }
    

    /*--------------------------- Other Methods -----------------------------*/
    public boolean samePoint(ChamferTriElmt elmt) {
	return ((x == elmt.x) && (y == elmt.y) && (z == elmt.z));
    }

    public String toString() {
	return ("[(" + x + ", " + y + ", " + z + "), " + w + "]");
    }
}
