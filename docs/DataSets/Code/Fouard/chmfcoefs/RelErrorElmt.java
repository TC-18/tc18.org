 

/** Class RelErrorElmt : 
    Computes the error rates needed.
*/
 

public class RelErrorElmt
{
    /*--------------------------- Constants ---------------------------------*/
    public static final double DEFAULT_TAU_MIN =  1.0;
    public static final double DEFAULT_TAU_MAX = -1.0;
    public static final double DEFAULT_EPSILON =  1.0;

    /*--------------------------- Fields ------------------------------------*/
    /** Global minimum of the error function */
    private double tauMin;
    private double tauMinAbs;
    /** Global maximum of the error function */
    private double tauMax;
    private double tauMaxAbs;
    /** chamfer map scale factor */
    private double epsilon;

    /*--------------------------- Constructors ------------------------------*/
    RelErrorElmt() {
	this(DEFAULT_EPSILON);
    }

    RelErrorElmt(double epsilon) {
	this(DEFAULT_TAU_MIN, DEFAULT_TAU_MAX, epsilon);
    }

    RelErrorElmt(double tauMin, double tauMax, double epsilon) {
	this.tauMin = tauMin;
	this.tauMax = tauMax;
	this.tauMinAbs = Math.abs(this.tauMin);
	this.tauMaxAbs = Math.abs(this.tauMax);
	this.epsilon   = epsilon;
    }


    /*--------------------------- Accessors ---------------------------------*/
    public double getEpsilon() {
	return this.epsilon;
    }

    public double getTauOpt() {
	return ((tauMinAbs + tauMaxAbs) / 2);
    }

    public double getEpsilonOpt() {
	return (epsilon * (getTauOpt() + 1));
    }

    /*--------------------------- Modificators ------------------------------*/
    public void modify(double errorExtremum) {
	if (errorExtremum < tauMin) {
	    tauMin = errorExtremum;
	    tauMinAbs = Math.abs(tauMin);
	}
	if (errorExtremum > tauMax) {
	    tauMax = errorExtremum;
	    tauMaxAbs = Math.abs(tauMax);
	}
    }
    
}
