 

/** Class LCCElmt : 
    Computes the Local Convexity Criterion 
    wX/yz - D1 wA - D2 wB - D3 wC >= 0
*/
 

public class LCCElmt 
{
    /*--------------------------- Constants ---------------------------------*/
    public static final byte LCC_FALSE     = 0;
    public static final byte LCC_TRUE      = 1;
    public static final byte LCC_DONT_KNOW = 2;

    /*-- Fileds --*/
    /** Index of the point which weight will bi wX/yz */
    public int w0;
    /** Index of the point which weight will bi wA */
    public int w1;
    /** Index of the point which weight will bi wB */
    public int w2;
    /** Index of the point which weight will bi wC */
    public int w3;

    /** First coefficient (pre-calculated) */
    public int D1;
    /** Second coefficient (pre-calculated) */
    public int D2;
    /** Third coefficient (pre-calculated) */
    public int D3;
        
    /** Stores whether the LCC is verified or not :
	LCC_FALSE     -> false
	LCC_TRUE      -> true
	LCC_DONT_KNOW -> don't know yet
    */
    public byte flag = LCC_DONT_KNOW;

    LCCElmt() {
	w0 = 0;
	w1 = 0;
	w2 = 0;
	w3 = 0;
	D1 = 0;
	D2 = 0;
	D3 = 0;
	flag = LCC_DONT_KNOW;
    }

    LCCElmt(LCCElmt elmt) {
	this.w0 = elmt.w0;
	this.w1 = elmt.w1;
	this.w2 = elmt.w2;
	this.w3 = elmt.w3;
	this.D1 = elmt.D1;
	this.D2 = elmt.D2;
	this.D3 = elmt.D3;
	this.flag = elmt.flag;
    }


    private int getW0Factor() {
	if ((w0 == w1) || (w0 == w2) || (w0 == w3))
	    return 0;
	else 
	    return 1;
    }
    private int getW1Factor() {
	if (w0 == w1)
	    return (1 - D1);
	else 
	    return (-D1);
    }
    private int getW2Factor() {
	if (w0 == w2)
	    return (1 - D2);
	else 
	    return (-D2);
    }
    private int getW3Factor() {
	if (w0 == w3)
	    return (1 - D3);
	else 
	    return (-D3);
    }
    

    public boolean equals(LCCElmt elmt) {
	if ((this.w0 == elmt.w0) &&
	    (this.w1 == elmt.w1) &&
	    (this.w2 == elmt.w2) &&
	    (this.w3 == elmt.w3) &&
	    (this.D1 == elmt.D1) &&
	    (this.D2 == elmt.D2) &&
	    (this.D3 == elmt.D3))
	    return true;

	int w0f = getW0Factor();
	int w1f = getW1Factor();
	int w2f = getW2Factor();
	int w3f = getW3Factor();

	int w0fElmt = elmt.getW0Factor();
	int w1fElmt = elmt.getW1Factor();
	int w2fElmt = elmt.getW2Factor();
	int w3fElmt = elmt.getW3Factor();

	if (((w0f == 0) && (w0fElmt == 0)) || ((w0f ==w0fElmt) && (this.w0 == elmt.w0))) 
	    if (
		(  (w1f == w1fElmt) && ((w1f == 0) || (this.w1 == elmt.w1)) && 
		 (((w2f == w2fElmt) && ((w2f == 0) || (this.w2 == elmt.w2)) && (w3f == w3fElmt) && ((w3f == 0) || (this.w3 == elmt.w3))) ||
		  ((w2f == w3fElmt) && ((w2f == 0) || (this.w2 == elmt.w3)) && (w3f == w2fElmt) && ((w3f == 0) || (this.w3 == elmt.w2)))))
		||
		(  (w1f == w2fElmt) && ((w1f == 0) || (this.w1 == elmt.w2)) && 
		 (((w2f == w2fElmt) && ((w2f == 0) || (this.w2 == elmt.w1)) && (w3f == w3fElmt) && ((w3f == 0) || (this.w3 == elmt.w3))) ||
		  ((w2f == w3fElmt) && ((w2f == 0) || (this.w2 == elmt.w3)) && (w3f == w2fElmt) && ((w3f == 0) || (this.w3 == elmt.w1)))))
		||
		(  (w1f == w3fElmt) && ((w1f == 0) || (this.w1 == elmt.w3)) && 
		 (((w2f == w1fElmt) && ((w2f == 0) || (this.w2 == elmt.w1)) && (w3f == w2fElmt) && ((w3f == 0) || (this.w3 == elmt.w2))) ||
		  ((w2f == w2fElmt) && ((w2f == 0) || (this.w2 == elmt.w2)) && (w3f == w1fElmt) && ((w3f == 0) || (this.w3 == elmt.w1)))))
		)
		return true;
	
	return false;
    }


    /** Accessor :
     *	@return the largest index of weighted point
     */
    public int getLargestIndex() {
	return Math.max(w0, Math.max(w1, Math.max(w2, w3)));
    }


    public String toString() {
	/* Local Convexity Criterion : 
	   wX/yz - D1 wA - D2 wB - D3 wC >= 0
	*/
	int w0factor = getW0Factor();
	int w1factor = getW1Factor();
	int w2factor = getW2Factor();
	int w3factor = getW3Factor();

	String mult = "*";	
	String result = "";

// result += "w0factor: " + w0factor + " w0: " + w0 +
//         "\nw1factor: " + w1factor + " w1: " + w1 +
//         "\nw2factor: " + w2factor + " w2: " + w2 +
//         "\nw3factor: " + w3factor + " w3: " + w3 + "\n\n";


	if (w0factor < 0) {
	    if (w0factor != -1) {
		result += (-w0factor);
		result += mult;
	    }
	    result += "w" + w0 + " ";
	    if (w1factor < 0) {
		result += "+ ";
		if (w1factor != -1) {
		    result += (-w1factor);
		    result += mult;
		}
		result += "w" + w1 + " ";
	    }
	    if (w2factor < 0) {
		result += "+ ";
		if (w2factor != -1) {
		    result += (-w2factor);
		    result += mult;
		}
		result += "w" + w2 + " ";
	    }
	    if (w3factor < 0) {
		result += "+ ";
		if (w3factor != -1) {
		    result += (-w3factor);
		    result += mult;
		}
		result += "w" + w3 + " ";
	    }
	}
	else if (w1factor < 0) {
	    if (w1factor != -1) {
		result += (-w1factor);
		result += mult;
	    }
	    result += "w" + w1 + " ";
	    if (w2factor < 0) {
		result += "+ ";
		if (w2factor != -1) {
		    result += (-w2factor);
		    result += mult;
		}
		result += "w" + w2 + " ";
	    }
	    if (w3factor < 0) {
		result += "+ ";
		if (w3factor != -1) {
		    result += (-w3factor);
		    result += mult;
		}
		result += "w" + w3 + " ";
	    }
	}
	else if (w2factor < 0) {
	    if (w2factor != -1) {
		result += (-w2factor);
		result += mult;
	    }
	    result += "w" + w2 + " ";
	    if (w3factor < 0) {
		result += "+ ";
		if (w3factor != -1) {
		    result += (-w3factor);
		    result += mult;
		}
		result += "w" + w3 + " ";
	    }
	}
	else if (w3factor < 0) {
	    if (w3factor != -1) {
		result += (-w3factor);
		result += mult;
	    }
	    result += "w" + w3 + " ";
	}
	else
	    result += "0 ";
	
	result += "<= ";
	
	if (w0factor > 0) {
	    if (w0factor != 1) {
		result += w0factor;
		result += mult;
	    }
	    result += "w" + w0 + " ";
	    if (w1factor > 0) {
		result += "+ ";
		if (w1factor != 1) {
		    result += w1factor;
		    result += mult;
		}
		result += "w" + w1 + " ";
	    }
	    if (w2factor > 0) { 
		result += "+ ";
		if (w2factor != 1) {
		    result += w2factor;
		    result += mult;
		}
		result += "w" + w2 + " ";
	    }
	    if (w3factor > 0) {
		result += "+ ";
		if (w3factor != 1) {
		    result += w3factor;
		    result += mult;
		}
		result += "w" + w3 + " ";
	    }
	}
	else if (w1factor > 0) {
	    if (w1factor != 1) {
		result += w1factor;
		result += mult;
	    }
	    result += "w" + w1 + " ";
	    if (w2factor > 0) {
		result += "+ ";
		if (w2factor != 1) {
		    result += w2factor;
		    result += mult;
		}
		result += "w" + w2 + " ";
	    }
	    if (w3factor > 0) {
		result += "+ ";
		if (w3factor != 1) {
		    result += w3factor;
		    result += mult;
		}
		result += "w" + w3 + " ";
	    }
	}
	else if (w2factor > 0) {
	    if (w2factor != 1) {
		result += w2factor; 
		result += mult;
	    }
	    result += "w" + w2 + " ";
	    if (w3factor > 0) {
		result += "+ ";
		if (w3factor != 1) {
		    result += w3factor;
		    result += mult;
		}
		result += "w" + w3 + " ";
	    }
	}
	else if (w3factor > 0) {
	    if (w3factor != 1) {
		result += w3factor;
		result += mult;
	    }
	    result += "w" + w3 + " ";
	}
	else
	    result += "0 ";

	return result;
    }
}
