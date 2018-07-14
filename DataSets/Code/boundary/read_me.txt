"Boundary Extraction for 3D binary image by Distance Transformation"

・This program extracts boundary of 3D binary images.
In the first step, we can obtain coordinate data which are centers of voxels 
of inner and outer side of the boundaries.
In the second step, we assume that the common boundary surfaces of outer and inner voxels are the interface of 3D object. 


SPEC and Process of this program
********************************************************************************1.SPEC

input : 3D text data. 
        The values of x,y, and z coordinates are deliminated by space.

output: The boundary data for text and POV-RAY (http://www.povray.org).		
		(uinit time : sec)
********************************************************************************2.Definition of functions

― move(int stepx,int stepy,int stepz)
	This function actuates an object in the direction of 26 neighborhoods.
	Three arguments, stepx, stepy, and stepz are widths for the actuation.
	In our theory, they are 3 pattern, +1 or 0 ,or -1.
	
― move1()
	This function obtains the outer side of the boundary 'OB'.
	It implements the logical operation for input data 'X' and data 'Y' 
	calculated after "move()"  as follows,
	
	OB = Y - (X AND Y) .
	
― move2()
	This function obtains the inner side of boundary.
	It implements the logical operation for input data 'X' and data 'Y' 
	calculated after "move()"  as follows,
	
	OB = X - (X AND Y) .
	
― integrate()
	This function implements logical addition of the inner and outer boundaries.	
― main()
	This function calls and processes three functions noted above.
********************************************************************************How to use this program
	Write datafile's name on source and carry out this porgram.
	
	After this, you obtain two file types of the boundary data.
	(Please reffer to "1.SPEC")
	
	The program is developed by Atsushi Imiya & Ken Tatara.
	In the following, we also describe pseudo-code for the program.


	
********************************************************************************Reference	
	Pseud-code for 2D version 4-and 8-connected.(alg_2D.ps)
				   3D version (alg_3D.ps)
	
	Program (Boundary.c)     
********************************************************************************