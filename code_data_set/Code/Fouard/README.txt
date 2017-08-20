SYSTEMATIZED CALCULATION OF OPTIMAL COEFFICIENTS OF 3D-CHAMFER NORMS

This programm computes optimal chamfer mask coefficients for large 3D anisotropic masks.
See article "Systematized calculation of optimal coefficients of 3-D chamfer norms".

This application is made with Java: You will need j2sdk to run it.
(You can download freely j2sdk at: http://java.sun.com/downloads/ )
USAGE
	* Linux : "java -jar chmfcoefs.jar [Arguments]"
		If it doesn't work, go into the chmfcoefs directory:
		"cd chmfcoefs"
		compile everything:
		"javac *.java"
		Apply the following command:
		"java CoefCalc [Arguments]"
	* Windows : Open a DOS prompt
		Tape the following command line:
		"java -jar chmfcoefs.jar [Arguments]"
		If it doesn't work, go into the chmfcoefs directory:
		"cd chmfcoefs"
		compile everything:
		"javac *.java"
		Apply the following command:
		"java CoefCalc [Arguments]"

NB: As the programm writes quite a lot of things on the standard output 
you maybe should redirect the stantdard output into a file.
Example "java -jar chmfcoefs.jar [Arguments] > outputFilename.txt"

ARGUMENTS
  Where [Arguments] are optional arguments among :
	-order %d:     order of the Farey triangulation
	    '-order 1' means a 3x3x3 chamfer masks
    	    '-order 2' means a 5x5x5 chamfer masks, etc
	-subdivide %d: allows to obtain intermediate mask generators between 2 orders.
    	    For instance '-order 1' generates a full 3x3x3 mask generator while
    	    '-order 2' generates a full 5x5x5 mask generator. Intermediate masks
    	    can be generated with '-order 2 -subdivide %d'.
      	    '-subdivide %d' controls the number of subdivisions that are needed
    	    to build the maks generator of order (n) from the mask generator of
    	    order (n-1). For instance, '-order 2 -subdivide 1' means that only one
    	    subdivision is done from the 3x3x3 mask generator, i.e. only one
    	    point is added to the 3x3x3 mask generator, '-order 2 -subdivide 2'
    	    means that 2 points are added to the 3x3x3 mask generator, and
    	    '-order 2 -subdivide 3' means that 3 points are added to the 3x3x3 mask
    	    generator. This last case results in the full 5x5x5 mask generator and
    	    is thus equivalent to simply '-order 2'.
      	    This subdivision number applies to isotropic mask generator. In
    	    anisotropic cases, the number of points that are added is larger due
    	    to symmetry considerations.
	-maxw %d:      maximal weight of the unit horizontal displacement
    	    the default value of maxw is 255
	-minw %d:      minimal weight of the unit horizontal displacement
    	    the default value of miw is 1
	-dy %f:        voxel anisotropy in the Y direction
	    It is equal to (voxel size along Y)/(voxel size along X)
	-dz %f:        voxel anisotropy in the Z direction
	    It is equal to (voxel size along Z)/(voxel size along X)
	-print-cones:  print the cones defined by Farey triangulation
	-print-syms:   print the symmetric cones used to define the local convexity
	    criteria
	-print-lccs:   print the local convexity criteria
	-v:            let see the progression of the computation
	-w:            display intermediary computational time
	-help:         display the usage of the function

	

RESULTS
	The file Sample_3x3x5_dy=1.5_dz=3.0.txt shows an example of what can be 
	optained by this program. It was launch under Linux with the following arguments:

	java -jar chmfcoefs.jar -order 2 -subdivide 2 -dy 1.5 -dz 3 -maxw 10 -print-cones -print-syms -print-lccs > Sample_3x3x5_dy=1.5_dz=3.0.txt 

	It has partially built a Farey triangulation of order 2, leading to a 
	3x5x5 chamfer mask.
	It first displays chamfer mask generator points:

		Initialisation stage
		--------------------

		Mask Points:
		w0: [(1, 0, 0), 0]
		w1: [(1, 1, 0), 0]
		w2: [(1, 1, 1), 0]
		w3: [(2, 1, 1), 0]
		w4: [(2, 1, 0), 0]
		w5: [(1, 0, 1), 0]
		w6: [(2, 0, 1), 0]
		w7: [(1, 2, 1), 0]
		w8: [(0, 1, 0), 0]
		w9: [(1, 2, 0), 0]
		w10: [(0, 1, 1), 0]
		w11: [(0, 2, 1), 0]
		w12: [(1, 1, 2), 0]
		w13: [(0, 0, 1), 0]
		w14: [(0, 1, 2), 0]
		w15: [(1, 0, 2), 0]

	Then, it displays mask cones:
		Mask Cones:
		{[(2, 1, 1), 0], [(1, 1, 0), 0], [(1, 1, 1), 0]}
		{[(1, 0, 0), 0], [(2, 1, 0), 0], [(2, 1, 1), 0]}
		{[(2, 1, 0), 0], [(1, 1, 0), 0], [(2, 1, 1), 0]}
		{[(1, 1, 1), 0], [(1, 0, 1), 0], [(2, 1, 1), 0]}
		{[(2, 1, 1), 0], [(2, 0, 1), 0], [(1, 0, 0), 0]}
		{[(2, 1, 1), 0], [(1, 0, 1), 0], [(2, 0, 1), 0]}
		{[(1, 1, 1), 0], [(1, 1, 0), 0], [(1, 2, 1), 0]}
		{[(1, 2, 1), 0], [(1, 2, 0), 0], [(0, 1, 0), 0]}
		{[(1, 2, 1), 0], [(1, 1, 0), 0], [(1, 2, 0), 0]}
		{[(1, 2, 1), 0], [(0, 1, 1), 0], [(1, 1, 1), 0]}
		{[(0, 1, 0), 0], [(0, 2, 1), 0], [(1, 2, 1), 0]}
		{[(0, 2, 1), 0], [(0, 1, 1), 0], [(1, 2, 1), 0]}
		{[(1, 1, 1), 0], [(0, 1, 1), 0], [(1, 1, 2), 0]}
		{[(1, 1, 2), 0], [(0, 1, 2), 0], [(0, 0, 1), 0]}
		{[(1, 1, 2), 0], [(0, 1, 1), 0], [(0, 1, 2), 0]}
		{[(1, 1, 2), 0], [(1, 0, 1), 0], [(1, 1, 1), 0]}
		{[(0, 0, 1), 0], [(1, 0, 2), 0], [(1, 1, 2), 0]}
		{[(1, 0, 2), 0], [(1, 0, 1), 0], [(1, 1, 2), 0]}


	Which are the cones of the Farey Triangulation. For each cone (A,B,C), 
	it displays the symmetric cone (Abc, Bac, Cab) used to compute the 
	Local Convexity Criteria, where 
	Abc is symetric of A in relation to BC,
	Bac is symetric of B in relation to AC,
	Cab is symetric of A in relation to AB.
		Symetric Cones:
		{[(1, 2, 1), 0], [(1, 0, 1), 0], [(2, 1, 0), 0]}
		{[(1, 1, 0), 0], [(2, 0, 1), 0], [(2, 1, -1), 0]}
		{[(1, 1, 1), 0], [(1, 0, 0), 0], [(2, 1, -1), 0]}
		{[(2, 0, 1), 0], [(1, 1, 0), 0], [(1, 1, 2), 0]}
		{[(2, -1, 1), 0], [(2, 1, 0), 0], [(1, 0, 1), 0]}
		{[(2, -1, 1), 0], [(1, 0, 0), 0], [(1, 1, 1), 0]}
		{[(1, 2, 0), 0], [(0, 1, 1), 0], [(2, 1, 1), 0]}
		{[(-1, 2, 1), 0], [(0, 2, 1), 0], [(1, 1, 0), 0]}
		{[(-1, 2, 1), 0], [(0, 1, 0), 0], [(1, 1, 1), 0]}
		{[(1, 1, 2), 0], [(1, 1, 0), 0], [(0, 2, 1), 0]}
		{[(0, 1, 1), 0], [(1, 2, 0), 0], [(-1, 2, 1), 0]}
		{[(1, 1, 1), 0], [(0, 1, 0), 0], [(-1, 2, 1), 0]}
		{[(0, 1, 2), 0], [(1, 0, 1), 0], [(1, 2, 1), 0]}
		{[(-1, 1, 2), 0], [(1, 0, 2), 0], [(0, 1, 1), 0]}
		{[(-1, 1, 2), 0], [(0, 0, 1), 0], [(1, 1, 1), 0]}
		{[(2, 1, 1), 0], [(0, 1, 1), 0], [(1, 0, 2), 0]}
		{[(1, 0, 1), 0], [(0, 1, 2), 0], [(1, -1, 2), 0]}
		{[(1, 1, 1), 0], [(0, 0, 1), 0], [(1, -1, 2), 0]}


	Then, it displays the corresponding Local Convexity Criteria:
	(The "i" of wi corresonds to the index of the point given by "Mas Points:")
		Local Convexity Criteria:
		w1 + 2*w2 <= w7 + w3 
		w3 <= w5 + w1 
		w3 + w1 <= w4 + w2 
		w4 <= w1 + w0 
		2*w0 + w3 <= w6 + w4 
		2*w4 <= 2*w3 
		2*w4 <= 2*w3 
		w5 + w3 <= w6 + w2 
		2*w2 + w5 <= w12 + w3 
		2*w6 <= 2*w3 
		w6 <= w5 + w0 
		w1 + w7 <= w9 + w2 
		w7 <= w10 + w1 
		4*w8 <= 2*w9 
		w7 + 2*w8 <= w11 + w9 
		w9 <= w1 + w8 
		2*w9 <= 4*w1 
		w10 + 2*w2 <= 2*w7 
		w7 + w10 <= w11 + w2 
		w11 <= w10 + w8 
		2*w11 <= 2*w7 
		2*w11 <= 2*w7 
		w10 + w12 <= w14 + w2 
		w12 <= w5 + w10 
		2*w2 + w10 <= w7 + w12 
		2*w14 <= 2*w12 
		w12 + 2*w13 <= w15 + w14 
		w14 <= w10 + w13 
		w12 + w5 <= w15 + w2 
		w15 <= w5 + w13 
		2*w15 <= 2*w12 
		2*w15 <= 2*w12 


	At last, gives the points with their corresponding weights and the optimal error rate:
		Computation stage
		-----------------

		Mask Points:
		w0: [(1, 0, 0), 1]
		w1: [(1, 1, 0), 2]
		w2: [(1, 1, 1), 3]
		w3: [(2, 1, 1), 3]
		w4: [(2, 1, 0), 2]
		w5: [(1, 0, 1), 3]
		w6: [(2, 0, 1), 3]
		w7: [(1, 2, 1), 5]
		w8: [(0, 1, 0), 2]
		w9: [(1, 2, 0), 4]
		w10: [(0, 1, 1), 3]
		w11: [(0, 2, 1), 5]
		w12: [(1, 1, 2), 6]
		w13: [(0, 0, 1), 3]
		w14: [(0, 1, 2), 6]
		w15: [(1, 0, 2), 6]

		TauOpt: 0.30307363113758884
		EpsilonOpt: 1.3030736311375888
		Time: 0 h 0 mn 0 s i.e.: 6 ms
		
		....