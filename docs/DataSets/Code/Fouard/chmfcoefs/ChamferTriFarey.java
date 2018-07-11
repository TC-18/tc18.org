 

/** Class ChamferTri3x3x3 :
    Defines an isotropic 3x3x3 chamfer mask generator and its triangulation.
*/
 

public abstract class ChamferTriFarey extends ChamferTriangulation 
{
    private static final byte EDGE_AB = 0;
    private static final byte EDGE_BC = 1;
    private static final byte EDGE_AC = 2;

    private int elmtIt = 0;


    protected void fareyTriangulation(int order) {
	if (order == 1) 
	    return;
	
	fareyTriangulation(order - 1);
	
	try {
	    elmtIt = maskPoints.size();
	    for (int i = 0; i < maskCones.size(); i++) {
		ChamferCone cone = (ChamferCone) maskCones.get(i);
		ChamferTriElmt a = (ChamferTriElmt) maskPoints.get(cone.A);
		ChamferTriElmt b = (ChamferTriElmt) maskPoints.get(cone.B);
		ChamferTriElmt c = (ChamferTriElmt) maskPoints.get(cone.C);
		
		double ab = (b.x - a.x)*(b.x - a.x) + (b.y - a.y)*(b.y - a.y) + (b.z - a.z)*(b.z - a.z);
		double bc = (c.x - b.x)*(c.x - b.x) + (c.y - b.y)*(c.y - b.y) + (c.z - b.z)*(c.z - b.z);
		double ac = (c.x - a.x)*(c.x - a.x) + (c.y - a.y)*(c.y - a.y) + (c.z - a.z)*(b.z - a.z);
		
		// The largest edge is AB
		if ((ab > bc) && (ab > ac)) {
		    // Try to add a point on AB
		    if (addPointOnEdge(order, cone, EDGE_AB))
			i--;
		    else { // we couln't add a point on AB
			if (bc > ac) { // Try to add a point on BC
			    if (addPointOnEdge(order, cone, EDGE_BC)) 
				i--;
			    else       // and then on AC
				if (addPointOnEdge(order, cone, EDGE_AC))
				    i--;
				
			}
			else {         // Try to add a point on AC
			    if (addPointOnEdge(order, cone, EDGE_AC)) 
				i--;
			    else       // and then on BC
				if (addPointOnEdge(order, cone, EDGE_BC))
				    i--;
				
			}
		    }
		}
		// The largest edge is BC
		else if ((bc > ab) && (bc > ac)) {
		    // Try to add a point on BC
		    if (addPointOnEdge(order, cone, EDGE_BC))
			i--;
		    else { // we couln't add a point on BC
			if (ab > ac) { // Try to add a point on AB
			    if (addPointOnEdge(order, cone, EDGE_AB)) 
				i--;
			    else       // and then on AC
				if (addPointOnEdge(order, cone, EDGE_AC))
				    i--;
				
			}
			else {         // Try to add a point on AC
			    if (addPointOnEdge(order, cone, EDGE_AC)) 
				i--;
			    else       // and then on AB
				if (addPointOnEdge(order, cone, EDGE_AB))
				    i--;
			}
		    }
		}
		// The largest edge is AC
		else {
		    // Try to add a point on AC
		    if (addPointOnEdge(order, cone, EDGE_AC))
			i--;
		    else { // we couln't add a point on AC
			if (ab > bc) { // Try to add a point on AB
			    if (addPointOnEdge(order, cone, EDGE_AB)) 
				i--;
			    else       // and then on BC
				if (addPointOnEdge(order, cone, EDGE_BC))
				    i--;
			}
			else {         // Try to add a point on BC
			    if (addPointOnEdge(order, cone, EDGE_BC)) 
				i--;
			    else       // and then on AB
				if (addPointOnEdge(order, cone, EDGE_AB))
				    i--;
			}
		    }
		}

	    } // end for (int i = 0; i < maskCones.size(); i++)
	} catch(Exception e) {System.out.println(e);}
    }

    protected void fareyTriangulation(int order, int levelStop)  
    {
	if (order == 1) 
	    return;
	
	fareyTriangulation(order - 1);

	try {
	    int i = 0;
	    int nbAddedPts = 0;
	    elmtIt = maskPoints.size();
	    while ((i < maskCones.size()) && (nbAddedPts < levelStop)) {
		ChamferCone cone = (ChamferCone) maskCones.get(i);
		ChamferTriElmt a = (ChamferTriElmt) maskPoints.get(cone.A);
		ChamferTriElmt b = (ChamferTriElmt) maskPoints.get(cone.B);
		ChamferTriElmt c = (ChamferTriElmt) maskPoints.get(cone.C);
		
		double ab = (b.x - a.x)*(b.x - a.x) + (b.y - a.y)*(b.y - a.y) + (b.z - a.z)*(b.z - a.z);
		double bc = (c.x - b.x)*(c.x - b.x) + (c.y - b.y)*(c.y - b.y) + (c.z - b.z)*(c.z - b.z);
		double ac = (c.x - a.x)*(c.x - a.x) + (c.y - a.y)*(c.y - a.y) + (c.z - a.z)*(b.z - a.z);
		
		// The largest edge is AB
		if ((ab > bc) && (ab > ac)) {
		    // Try to add a point on AB
		    if (addPointOnEdge(order, cone, EDGE_AB)) {
			i--;
			nbAddedPts ++;
		    }
		    else { // we couln't add a point on AB
			if (bc > ac) { // Try to add a point on BC
			    if (addPointOnEdge(order, cone, EDGE_BC)) {
				i--;
				nbAddedPts ++;
			    }
			    else       // and then on AC
				if (addPointOnEdge(order, cone, EDGE_AC)) {
				    i--;
				    nbAddedPts ++;
				}
				
			}
			else {         // Try to add a point on AC
			    if (addPointOnEdge(order, cone, EDGE_AC)) {
				i--;
				nbAddedPts ++;
			    }
			    else       // and then on BC
				if (addPointOnEdge(order, cone, EDGE_BC)) {
				    i--;
				    nbAddedPts ++;
				}
			}
		    }
		}
		// The largest edge is BC
		else if ((bc > ab) && (bc > ac)) {
		    // Try to add a point on BC
		    if (addPointOnEdge(order, cone, EDGE_BC)) {
			i--;
			nbAddedPts ++;
		    }
		    else { // we couln't add a point on BC
			if (ab > ac) { // Try to add a point on AB
			    if (addPointOnEdge(order, cone, EDGE_AB)) {
				i--;
				nbAddedPts ++;
			    }
			    else       // and then on AC
				if (addPointOnEdge(order, cone, EDGE_AC)) {
				    i--;
				    nbAddedPts ++;
				}

			}
			else {         // Try to add a point on AC
			    if (addPointOnEdge(order, cone, EDGE_AC)) {
				i--;
				nbAddedPts ++;
			    }
			    else       // and then on AB
				if (addPointOnEdge(order, cone, EDGE_AB)) {
				    i--;
				    nbAddedPts ++;
				}
			}
		    }
		}
		// The largest edge is AC
		else {
		    // Try to add a point on AC
		    if (addPointOnEdge(order, cone, EDGE_AC)) {
			i--;
			nbAddedPts ++;
		    }
		    else { // we couln't add a point on AC
			if (ab > bc) { // Try to add a point on AB
			    if (addPointOnEdge(order, cone, EDGE_AB)) {
				i--;
				nbAddedPts ++;
			    }
			    else       // and then on BC
				if (addPointOnEdge(order, cone, EDGE_BC)) {
				    i--;
				    nbAddedPts ++;
				}
			}
			else {         // Try to add a point on BC
			    if (addPointOnEdge(order, cone, EDGE_BC)) {
				i--;
				nbAddedPts ++;
			    }
			    else       // and then on AB
				if (addPointOnEdge(order, cone, EDGE_AB)) {
				    i--;
				    nbAddedPts ++;
				}
			}
		    }
		}
		i++;
	    } // end while ((i < maskCones.size()) && (nbAddedPts < levelStop))
	} catch(Exception e) {System.out.println(e);}
    }


    private boolean addPointOnEdge(int order, ChamferCone cone, byte whichEdge) 
    throws Exception {
	ChamferTriElmt a = (ChamferTriElmt) maskPoints.get(cone.A);
	ChamferTriElmt b = (ChamferTriElmt) maskPoints.get(cone.B);
	ChamferTriElmt c = (ChamferTriElmt) maskPoints.get(cone.C);
	int newIndex = 0;
	ChamferTriElmt newPoint;
	ChamferCone newCone1, newCone2;

	switch(whichEdge) {
	case (EDGE_AB) :
	    newPoint = new ChamferTriElmt(a.x + b.x, a.y + b.y, a.z + b.z);
	    break;
	case (EDGE_BC) :
	    newPoint = new ChamferTriElmt(b.x + c.x, b.y + c.y, b.z + c.z);
	    break;
	case (EDGE_AC) :
	    newPoint = new ChamferTriElmt(a.x + c.x, a.y + c.y, a.z + c.z);
	    break;
	default :
	    throw (new Exception("Bad edge of triangle"));
	    //	    break;
	}
	
	if (newPoint.x == order) {
	    newIndex = isInSet(newPoint);
	    if (newIndex == -1) {
		maskPoints.add(newPoint);
		newIndex = elmtIt;
		elmtIt++;
	    }
	    
	    switch(whichEdge) {
	    case (EDGE_AB) :
		newCone1 = cone.createNew(cone.A, newIndex, cone.C,
					  SYM_IN_GENERATOR, cone.Bac, cone.Cab, dy, dz);
		newCone2 = cone.createNew(newIndex, cone.B, cone.C,
					  cone.Abc, SYM_IN_GENERATOR, cone.Cab, dy, dz);
		break;
	    case (EDGE_BC) :
		newCone1 = cone.createNew(cone.A, cone.B, newIndex,
					  cone.Abc, SYM_IN_GENERATOR, cone.Cab, dy, dz);
		newCone2 = cone.createNew(cone.A, newIndex, cone.C,
					  cone.Abc, cone.Bac, SYM_IN_GENERATOR, dy, dz);
		break;
	    case (EDGE_AC) :
		newCone1 = cone.createNew(cone.A, cone.B, newIndex,
					  SYM_IN_GENERATOR, cone.Bac, cone.Cab, dy, dz);
		newCone2 = cone.createNew(newIndex, cone.B, cone.C,
					  cone.Abc, cone.Bac, SYM_IN_GENERATOR, dy, dz);
		break;
	    default :
		throw (new Exception("Bad edge of triangle"));
		//		break;
	    }
	    
	    maskCones.add(newCone1);
	    maskCones.add(newCone2);
	    maskCones.remove(cone);

	    return true;
	}
	
	return false;
    }
}
