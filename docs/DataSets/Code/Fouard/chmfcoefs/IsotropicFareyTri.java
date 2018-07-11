 

/** Class IsotropicChamferTri :
    Extends ChamferTriFarey, compute a chamfer mask generator for 
    isotropic images 
*/
 

public class IsotropicFareyTri extends ChamferTriFarey
{

    IsotropicFareyTri(int order)
    {
	init(order);
	    
	ChamferTriElmt a = new ChamferTriElmt(1, 0, 0);
	ChamferTriElmt b = new ChamferTriElmt(1, 1, 0);
	ChamferTriElmt c = new ChamferTriElmt(1, 1, 1);
	
	maskPoints.add(a); maskPoints.add(b); maskPoints.add(c);
	
	ChamferConeProjX cone = new ChamferConeProjX(0, 
						     1, 
						     2, 
						     ChamferTriangulation.SYM_X_EQ_Y,
						     ChamferTriangulation.SYM_Y_EQ_Z,
						     ChamferTriangulation.SYM_Z_EQ_O);
	maskCones.add(cone);
	if (order > 1)
	    fareyTriangulation(order);
	nbPts = maskPoints.size();
    }

    IsotropicFareyTri(int order, int levelStop)
    {
	init(order);
	ChamferTriElmt a = new ChamferTriElmt(1, 0, 0);
	ChamferTriElmt b = new ChamferTriElmt(1, 1, 0);
	ChamferTriElmt c = new ChamferTriElmt(1, 1, 1);
	
	maskPoints.add(a); maskPoints.add(b); maskPoints.add(c);
	
	ChamferConeProjX cone = new ChamferConeProjX(0, 
						     1, 
						     2, 
						     ChamferTriangulation.SYM_X_EQ_Y,
						     ChamferTriangulation.SYM_Y_EQ_Z,
						     ChamferTriangulation.SYM_Z_EQ_O);
	maskCones.add(cone);
	if (order > 1)
	    fareyTriangulation(order, levelStop);
	nbPts = maskPoints.size();
	
    }


    public float NInfinity(ChamferTriElmt elmt) {
	return (float) (Math.max(elmt.x, Math.max(elmt.y, elmt.z)));
    }

    public float N1(ChamferTriElmt elmt) {
	return (float) (elmt.x + elmt.y + elmt.z);
    }


    public int[][][] getFinalMask() {
	int[][][] finalMask = super.getFinalMask();
	ChamferTriElmt elmt;
	for (int i = 0; i < nbPts; i++) {
	    elmt = (ChamferTriElmt) maskPoints.get(i);
	    // For points 0 <= x <= y <= z
	    finalMask[elmt.x][elmt.y][elmt.z] = elmt.w;
	    // Symetry in relation to P x = y
	    finalMask[elmt.x][elmt.z][elmt.y] = elmt.w;
	    // Symetry in relation to P x = z
	    finalMask[elmt.z][elmt.y][elmt.x] = elmt.w;
	}
	return finalMask;
    }
}
