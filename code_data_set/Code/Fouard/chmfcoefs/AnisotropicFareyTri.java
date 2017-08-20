 

/** Class IsotropicChamferTri :
    Extends ChamferTriFarey, compute a chamfer mask generator for 
    isotropic images 
*/

 

import java.util.Vector;

public class AnisotropicFareyTri extends ChamferTriFarey
{
    /** Build a 3x3x3 anisotropic mask */
    AnisotropicFareyTri(int order, double dy, double dz)
    {
	init(order, dy, dz);
	
	ChamferTriElmt a = new ChamferTriElmt(1, 0, 0);
	ChamferTriElmt b = new ChamferTriElmt(1, 1, 0);
	ChamferTriElmt c = new ChamferTriElmt(1, 1, 1);
	
	maskPoints.add(a);
	maskPoints.add(b);
	maskPoints.add(c);


	ChamferConeProjX cone = new ChamferConeProjX(0,  1,  2, 
						     ChamferTriangulation.SYM_X_EQ_Y,
						     ChamferTriangulation.SYM_Y_EQ_Z,
						     ChamferTriangulation.SYM_Z_EQ_O,
						     dy, dz);

	maskCones.add(cone);

	if (order > 1)
	    fareyTriangulation(order);

 	Vector tmpVect = new Vector();
	// Add symmetrical in relation to the plan y = z
	tmpVect = makeSymetry(SYM_Y_EQ_Z);
	maskCones.addAll(tmpVect);
	// Symmetrical in relation to the plan x = y
	Vector tmpVectXY = makeSymetry(SYM_X_EQ_Y);
	// Symmetrical in relation to the plan x = z
	Vector tmpVectXZ = makeSymetry(SYM_X_EQ_Z);
	// Add Symmetricals
	maskCones.addAll(tmpVectXY);
	maskCones.addAll(tmpVectXZ);

	nbPts = maskPoints.size();

    }

    AnisotropicFareyTri(int order, int levelStop, double dy, double dz)
    {
	init(order, dy, dz);
	
	ChamferTriElmt aX  = new ChamferTriElmt(1, 0, 0);
	ChamferTriElmt aY  = new ChamferTriElmt(0, 1, 0); 
	ChamferTriElmt aZ  = new ChamferTriElmt(0, 0, 1);
	ChamferTriElmt bYZ = new ChamferTriElmt(0, 1, 1);
	ChamferTriElmt bXZ = new ChamferTriElmt(1, 0, 1);
	ChamferTriElmt bXY = new ChamferTriElmt(1, 1, 0);
	ChamferTriElmt c   = new ChamferTriElmt(1, 1, 1);
	
	maskPoints.add(aX);
	maskPoints.add(bXY);
	maskPoints.add(c);


	ChamferConeProjX cone = new ChamferConeProjX(0, 
						     1, 
						     2, 
						     ChamferTriangulation.SYM_X_EQ_Y,
						     ChamferTriangulation.SYM_Y_EQ_Z,
						     ChamferTriangulation.SYM_Z_EQ_O,
						     dy, dz);

	maskCones.add(cone);

	if (order > 1)
	    fareyTriangulation(order, levelStop);

 	Vector tmpVect = new Vector();
	// Add symmetrical in relation to the plan y = z
	tmpVect = makeSymetry(SYM_Y_EQ_Z);
	maskCones.addAll(tmpVect);

	// Symetrical in relation to the plan x = y
	Vector tmpVectXY = makeSymetry(SYM_X_EQ_Y);
	// Symetrical in relation to the plan x = z
	Vector tmpVectXZ = makeSymetry(SYM_X_EQ_Z);
	// Add symetrical
	maskCones.addAll(tmpVectXY);
	maskCones.addAll(tmpVectXZ);

	nbPts = maskPoints.size();
    }


    public float NInfinity(ChamferTriElmt elmt) {
	return (float) (Math.max(elmt.x, Math.max(dy*elmt.y, dz*elmt.z)));
    }

    public float N1(ChamferTriElmt elmt) {
	return (float) (elmt.x + dy*elmt.y + dz*elmt.z);
    }


    public int[][][] getFinalMask() {
	int[][][] finalMask = super.getFinalMask();
	ChamferTriElmt elmt;
	for (int i = 0; i < nbPts; i++) {
	    elmt = (ChamferTriElmt) maskPoints.get(i);
	    // For points 0 <= x, 0 <= y, 0 <= z
	    finalMask[elmt.x][elmt.y][elmt.z] = elmt.w;
	}
	return finalMask;
    }



    private Vector makeSymetry(int plane)
    {
	int index;
	Vector result = new Vector();
	ChamferCone cone;
	ChamferCone coneSym;
	ChamferTriElmt a, b, c;
	ChamferTriElmt aSym, bSym, cSym;
	int aSymIndex, bSymIndex, cSymIndex;
	int aSymFlag, bSymFlag, cSymFlag;

	for (index = 0; index < maskCones.size(); index++) {
	    cone = (ChamferCone) maskCones.get(index);
	    a = (ChamferTriElmt) maskPoints.get(cone.A);
	    b = (ChamferTriElmt) maskPoints.get(cone.B);
	    c = (ChamferTriElmt) maskPoints.get(cone.C);

	    // Point A
	    aSym = a.symetric(plane);
	    aSymIndex = isInSet(aSym);
	    if (aSymIndex == -1) {
		aSymIndex = maskPoints.size();
		maskPoints.add(aSym);
	    }
	    aSymFlag = makeSymetry(plane, cone.Abc);
	    if (aSymFlag == SYM_IN_GENERATOR)
		cone.Abc = SYM_IN_GENERATOR;

	    // Point B
	    bSym = b.symetric(plane);
	    bSymIndex = isInSet(bSym);
	    if (bSymIndex == -1) {
		bSymIndex = maskPoints.size();
		maskPoints.add(bSym);
	    }
	    bSymFlag = makeSymetry(plane, cone.Bac);
	    if (bSymFlag == SYM_IN_GENERATOR)
		cone.Bac = SYM_IN_GENERATOR;

	    // Point C
	    cSym = c.symetric(plane);
	    cSymIndex = isInSet(cSym);
	    if (cSymIndex == -1) {
		cSymIndex = maskPoints.size();
		maskPoints.add(cSym);
	    }
	    cSymFlag = makeSymetry(plane, cone.Cab);
	    if (cSymFlag == SYM_IN_GENERATOR)
		cone.Cab = SYM_IN_GENERATOR;
		
	    coneSym = cone.createSym(plane, 
				     cSymIndex, bSymIndex, aSymIndex,
				     cSymFlag,  bSymFlag,  aSymFlag);
	    
	    result.add(coneSym);
	}
	
	return result;
    }


    private int makeSymetry(int plane, int flag) 
    {
	int result;
	switch(plane) {
	case SYM_Y_EQ_Z:
	    switch(flag) {
	    case SYM_Z_EQ_O:
		result = SYM_Y_EQ_O;
		break;
	    case SYM_X_EQ_Y:
		result = SYM_X_EQ_Z;
		break;
	    case SYM_Y_EQ_Z:
		result = SYM_IN_GENERATOR;
		break;
	    case SYM_IN_GENERATOR:
		result = SYM_IN_GENERATOR;
		break;
	    default:
		result = SYM_DONT_KNOW;
		break;
	    }
	    break;
	case SYM_X_EQ_Y:
	    switch(flag){
	    case SYM_Z_EQ_O:
		result = SYM_X_EQ_O;
		break;
	    case SYM_Y_EQ_O:
		result = SYM_X_EQ_O;
		break;
	    case SYM_X_EQ_Z:
		result = SYM_Y_EQ_Z;
		break;
	    case SYM_X_EQ_Y:
		result = SYM_IN_GENERATOR;
		break;
	    case SYM_Y_EQ_Z:
		result = SYM_X_EQ_Z;
		break;
	    case SYM_IN_GENERATOR:
		result = SYM_IN_GENERATOR;
		break;
	    default:
		result = SYM_DONT_KNOW;
		break;
	    }
	    break;
	case SYM_X_EQ_Z:
	    switch(flag){
	    case SYM_Z_EQ_O:
		result = SYM_X_EQ_O;
		break;
	    case SYM_Y_EQ_O:
		result = SYM_Y_EQ_O;
		break;
	    case SYM_X_EQ_Z:
		result = SYM_IN_GENERATOR;
		break;
	    case SYM_X_EQ_Y:
		result = SYM_Y_EQ_Z;
		break;
	    case SYM_Y_EQ_Z:
		result = SYM_X_EQ_Y;
		break;
	    case SYM_IN_GENERATOR:
		result = SYM_IN_GENERATOR;
		break;
	    default:
		result = SYM_DONT_KNOW;
		break;
	    }
	    break;
	default:
	    result = SYM_DONT_KNOW;
	    break;
	}

	return result;
    }
}
