 

/** Classe Test */
 

import java.util.Date;

public class CoefCalc {
    
    public static final int DEFAULT_ORDER      = 1;
    public static final int DEFAULT_MAX_W      = 255;
    public static final int DEFAULT_MIN_W      = 1;
    public static final int DEFAULT_LEVEL_STOP = 0;

    public static final double DEFAULT_DY      = 1.0;
    public static final double DEFAULT_DZ      = 1.0;

    public static final String USAGE  = "java CoefCalc [-order %d] [-subdivide %d] ...\n       [-dy %f] [-dz %f] [-maxw %d] [-minw %d] [-help]";
    public static final String DETAIL = "-order %d:     order of the Farey triangulation\n    '-order 1' means a 3x3x3 chamfer masks\n    '-order 2' means a 5x5x5 chamfer masks, etc\n-subdivide %d: allows to obtain intermediate mask generators between 2 orders.\n    For instance '-order 1' generates a full 3x3x3 mask generator while\n    '-order 2' generates a full 5x5x5 mask generator. Intermediate masks\n    can be generated with '-order 2 -subdivide %d'.\n      '-subdivide %d' controls the number of subdivisions that are needed\n    to build the maks generator of order (n) from the mask generator of\n    order (n-1). For instance, '-order 2 -subdivide 1' means that only one\n    subdivision is done from the 3x3x3 mask generator, i.e. only one\n    point is added to the 3x3x3 mask generator, '-order 2 -subdivide 2'\n    means that 2 points are added to the 3x3x3 mask generator, and\n    '-order 2 -subdivide 3' means that 3 points are added to the 3x3x3 mask\n    generator. This last case results in the full 5x5x5 mask generator and\n    is thus equivalent to simply '-order 2'.\n      This subdivision number applies to isotropic mask generator. In\n    anisotropic cases, the number of points that are added is larger due\n    to symmetry considerations.\n-maxw %d:      maximal weight of the unit horizontal displacement\n    the default value of maxw is 255\n-minw %d:      minimal weight of the unit horizontal displacement\n    the default value of miw is 1\n-dy %f:        voxel anisotropy in the Y direction\n    It is equal to (voxel size along Y)/(voxel size along X)\n-dz %f:        voxel anisotropy in the Z direction\n    It is equal to (voxel size along Z)/(voxel size along X)\n-print-cones:  print the cones defined by Farey triangulation\n-print-syms:   print the symmetric cones used to define the local convexity\n    criteria\n-print-lccs:   print the local convexity criteria\n-v:            let see the progression of the computation\n-w:            display intermediary computational time\n-help:         display this message";

    private static void errorMsg(String str, int flag) {
	System.out.println("Usage: " + USAGE);
	if (flag == 1) {
	    System.out.println("\nDetails:" );
	    System.out.println(DETAIL);
	}
	System.out.println("\nError: " + str);
	System.exit(1);
    }
    public static void main(String[] args)
    {
	int order     = DEFAULT_ORDER;
	int maxWeight = DEFAULT_MAX_W;
	int minWeight = DEFAULT_MIN_W;
	int levelStop = DEFAULT_LEVEL_STOP;
	double dy = DEFAULT_DY;
	double dz = DEFAULT_DZ;
	
	int print_cones = 0;
	int print_symmetric_cones = 0;
	int print_lcc = 0;

	int verbose = 0;

	/* Parsing arguments */
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-order")) {
		i++;
		if (i >= args.length)
		    errorMsg("Missing argument for '-order'", 0);
		order = (Integer.valueOf(args[i])).intValue();
		if ( order < 1 )
		    errorMsg("Bad Farey triangulation order", 0);
		if ( order > 10 )
		    errorMsg("Too large Farey triangulation order", 0);
	    }
	    else if ( args[i].equals("-subdivide") || args[i].equals("-levelStop") ) {
		i++;
		if (i >= args.length)
		    errorMsg("Missing argument for '-subdivide'", 0);
		levelStop = (Integer.valueOf(args[i])).intValue();
		if (levelStop < 0)
		    levelStop = DEFAULT_LEVEL_STOP;
	    }
	    else if (args[i].equals("-maxw")) {
		i++;
		if (i >= args.length)
		    errorMsg("Missing argument for '-maxw'", 0);
		maxWeight = (Integer.valueOf(args[i])).intValue();
		if ((maxWeight < 1) || (maxWeight > 255))
		    errorMsg("Bad argument for '-maxw': should be between 1 and 255", 0);
	    }
	    else if (args[i].equals("-minw")) {
		i++;
		if (i >= args.length)
		    errorMsg("Missing argument for '-minw'", 0);
		minWeight = (Integer.valueOf(args[i])).intValue();
		if ((minWeight < 1) || (minWeight > 255))
		    errorMsg("Bad argument for '-minw': should be between 1 and 255", 0);
	    }
	    else if (args[i].equals("-dy")) {
		i++;
		if (i >= args.length)
		    errorMsg("Missing argument for '-dy'", 0);
		dy = Double.parseDouble(args[i]);
		if ( dy < 1.0 )
		    errorMsg("Bad argument for '-dy': should be greater than 1", 0 );
	    }
	    else if (args[i].equals("-dz")) {
		i++;
		if (i >= args.length)
		    errorMsg("Missing argument for '-dz'", 0);
		dz = Double.parseDouble(args[i]);
		if ( dz < 1.0 )
		    errorMsg("Bad argument for '-dz': should be greater than 1", 0 );
	    }
	    else if ( args[i].equals("-print-cones" ) )
		print_cones = 1;
	    else if ( args[i].equals("-print-syms" ) )
		print_symmetric_cones = 1;
	    else if ( args[i].equals("-print-lccs" ) )
		print_lcc = 1;

	    else if ( args[i].equals("-v" ) )
		verbose = 1;
	    else if ( args[i].equals("-w" ) )
		verbose = 2;
	    
	    else if (args[i].equals("-help")  || 
		args[i].equals("--help") ||
		args[i].equals("-h"))
		errorMsg("help message\n", 1);
	    else {
		System.out.println("unknown option: " + args[i]);
	    }
	}
	
	
	Date hBegin, hEnd;
	long duration, h, m, s;

	try {
	    hBegin = new Date();

	    ChamferTriangulation myTri;

 	    if ((dy == 1.0) && (dz == 1.0)) {
 		if (levelStop == 0)
 		    myTri = (IsotropicFareyTri) new IsotropicFareyTri(order);
 		else 
 		    myTri = (IsotropicFareyTri) new IsotropicFareyTri(order, levelStop);
 	    }
 	    else {
		if (levelStop == 0)
		    myTri = (AnisotropicFareyTri) new AnisotropicFareyTri(order, dy, dz);
		else 
		    myTri = (AnisotropicFareyTri) new AnisotropicFareyTri(order, levelStop, dy, dz);
	    }

	    myTri.initLCC();
	    
	    /** write information about mask
		- mask points
		- mask cones
	     */
	    System.out.println( "\nInitialisation stage\n--------------------" );
	    System.out.println( myTri.printPoints() );
	    if ( print_cones > 0 )
		System.out.println( myTri.printCones() );
	    if ( print_symmetric_cones > 0 )
		System.out.println( myTri.printSymetricCones() );
	    if ( print_lcc > 0 )
		System.out.println( myTri.printLocalConvexityCriteria() );


	    System.out.println( "Computation stage\n-----------------" );
	    myTri.chamferCoefCalculation(minWeight, maxWeight, verbose );
	    
	    hEnd = new Date();    

	    duration = (hEnd.getTime() - hBegin.getTime()) / 1000; 
	    h = duration / 3600;
	    m = (duration % 3600) / 60;
	    s = (duration % 60);
	    System.out.println("\nTotal duration:" + h + " h " + m + " mn " + s + " s");
	}catch (Exception e) {System.out.println(e);}
    }
}
