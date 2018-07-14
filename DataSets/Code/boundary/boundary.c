

		/*Boundary Extraction for 3D binary image by Distance Transformation*/

#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<time.h>

#define	XMAX	200										/*maximumn range for x-coordinate*/
#define	YMAX	200										/*maximumn range for y-coordinate*/	
#define	ZMAX	200										/*maximumn range for z-coordinate*/
#define	Size	200										/*size of array*/
#define IMAGE   1										/*object data*/
#define BACK    0										/*background data*/

#define open_file "original_data.txt"							/*the name of input file*/
#define output_file0 "original_data.pov"				/*the name of output file about original-data for pov-ray*/
//#define output_file1 "boundary_out.pov"					/*the name of output file about outer-boundary for pov-ray*/
//#define output_file2 "boundary_half_out.pov"				/*the name of output file about outer-boundary for pov-ray(half size)*/
//#define output_file3 "boundary_in.pov"					/*the name of output file about inner-boundary for pov-ray*/
//#define output_file4 "boundary_half_in.pov"				/*the name of output file about inner-boundary for pov-ray(half size)*/
#define output_file5 "check_time_boundry.txt"			/*processing time*/
#define output_file6 "boundry.txt"					/*the name of output file about boundary*/ 
#define output_file7 "boundry.pov"					/*the name of output file about boundary for pov-ray*/


unsigned char	source[Size][Size][Size];				/*define "source" for storing the original data*/
unsigned char   midata[Size][Size][Size];				/*define "midata" for storing the processed data*/ 
unsigned char	lastdata1[Size][Size][Size];			/*define "lastdata1"for storing the outer data*/
unsigned char	lastdata2[Size][Size][Size];			/*define "lastdata2"for storing the inner data*/
unsigned char	lastdata3[2*Size][2*Size][2*Size];		/*define "lastdata3"for storing the boundary data*/


///move data///
void move(int stepx,int stepy,int stepz)	
{
	int a,b,c;

	for(a=0;a<Size;a++){
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){
				midata[a][b][c]=BACK;		
			}
		}
	}

	for(a=0;a<Size;a++){	
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){
					if (source[a][b][c] == IMAGE) {
						if ( 0 <= a+stepx && a+stepx < Size){
							if ( 0 <= b+stepy && b+stepy < Size){
								if ( 0 <= c+stepz && c+stepz < Size){
									midata[a+stepx][b+stepy][c+stepz] = IMAGE;
							}
						}
					}
				}
			}
		}
	}
}

void move1()
{
	int a,b,c;
	
	for (a=0;a<Size;a++){
		for (b=0;b<Size;b++){           
			for(c=0;c<Size;c++){     
				if( midata[a][b][c] == IMAGE && source[a][b][c] == BACK){ 
					lastdata1[a][b][c] = IMAGE;
				}
     		} 
		}
	}
}

void move2()
{
	int a,b,c;
	
	for (a=0;a<Size;a++){
		for (b=0;b<Size;b++){           
			for(c=0;c<Size;c++){     
			if( midata[a][b][c]== BACK && source[a][b][c] == IMAGE){ 
				lastdata2[a][b][c] = IMAGE;
				}
			}			
		}
	}
}

///integrate inner and outer boundary///
void integrate()
{
	int a,b,c;
	
	for (a=0;a<Size;a++){
		for (b=0;b<Size;b++){           
			for(c=0;c<Size;c++){     
				if(lastdata1[a][b][c]==IMAGE || lastdata2[a][b][c]==IMAGE) {
				lastdata3[a][b][c] = IMAGE;
				}
			}
		}
	}
}

///main function///
int	main(void)

{
	
	FILE	*fp,
			*fp_integrate1,*fp_integrate2,
			*fp_time;	

	int     a,b,c;
				
	char	x[Size],y[Size],z[Size];
	
	clock_t before;
	double elasped;

///read the original data///
	if ((fp = fopen(open_file,"rb")) == NULL) {
	printf("Can't open file\n");
	exit(0);
	}

///initialization///
	for(a=0;a<Size;a++){												
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){					
				source[a][b][c]=BACK;
				lastdata1[a][b][c]=BACK;
				lastdata2[a][b][c]=BACK;
				lastdata3[a][b][c]=BACK;	
			}			
		}
	}

	while(fscanf(fp,"%s%s%s",x,y,z) != EOF) {
		source[atoi(x)][atoi(y)][atoi(z)]=1;	

	}
	fclose(fp);	

///starting to mesure the processing time///
	before = clock();

///extract the inner & outer boundary///
	move(1,0,0);	move1();	move2();
   	
	move(-1,0,0);	move1();	move2();
   	   	
	move(0,0,1);	move1();	move2();

	move(0,0,-1);	move1();	move2();

	move(0,1,0);	move1();	move2();

	move(0,-1,0);	move1();	move2();
   		
	
	move(1,0,1);	move1();	move2();

   	move(1,0,-1);	move1();	move2();

   	move(1,1,0);	move1();	move2();
	
	move(1,-1,0);	move1();	move2();

	move(-1,0,1);	move1();	move2();

	move(-1,0,-1);	move1();	move2();

	move(-1,1,0);	move1();	move2();

	move(-1,-1,0);	move1();	move2();

	move(0,1,1);	move1();	move2();

	move(0,-1,1);	move1();	move2();

	move(0,1,-1);	move1();	move2();

	move(0,-1,-1);	move1();	move2();
	

   	move(1,1,1);	move1();	move2();

	move(1,-1,-1);	move1();	move2();

	move(1,1,-1);	move1();	move2();

	move(1,-1,1);	move1();	move2();

	move(-1,1,1);	move1();	move2();

	move(-1,-1,-1);	move1();	move2();

	move(-1,1,-1);	move1();	move2();

	move(-1,-1,1);	move1();	move2();

///extract the boundary///

	integrate();

///stop to measure the processing time///
	elasped = clock() - before;
	fp_time = fopen(output_file5,"wb");
	fprintf(fp_time,"processing time@%.3f sec\n",elasped/CLOCKS_PER_SEC);
	fclose(fp_time);

	
/// Following processes is to make a file of data to be visualized.///

///making the original data for pov-ray///
	fp = fopen(output_file0,"wb");

	fprintf(fp,"#include \"colors.inc\"\n");
	fprintf(fp,"#declare b=box{<0 0 0>,<1 1 1> texture{pigment{color rgb<0.8,0.8,1.0>}}}\n");
	fprintf(fp,"background { color rgb<0.5,0.5,0.5> }\n");
	fprintf(fp,"camera{location<30,160,-50> look_at<0,10,0>}\n");
	fprintf(fp,"light_source{ <60,60,-60> color rgb <1.0, 1.0, 1.0> }\n");
	fprintf(fp,"union{\n");
	for(a=0;a<Size;a++){
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){															
				if( source[a][b][c] == IMAGE){
				fprintf(fp,"object{b translate<%d,%d,%d>}\n",a,b,c);
				}
			}
		}
	}
	fprintf(fp,"rotate <0,-clock*360*6/6,0> ");
	fprintf(fp,"}");

	fclose(fp);

///make the boundary's data file///

	fp_integrate1 = fopen(output_file6,"w");			///text data///
	fp_integrate2 = fopen(output_file7,"w");			///data file for pov-ray///

	fprintf(fp_integrate2,"#include \"colors.inc\"\n");
	fprintf(fp_integrate2,"#declare b=box{<0 0 0>,<1 1 1> texture{pigment{color rgb<0.8,0.8,1.0>}}}\n");
	fprintf(fp_integrate2,"background { color rgb<0.5,0.5,0.5> }\n");
	fprintf(fp_integrate2,"camera{location<50,80,-80> look_at<0,10,0>}\n");
	fprintf(fp_integrate2,"light_source{ <60,60,-60> color rgb <1.0, 1.0, 1.0> }\n");
	fprintf(fp_integrate2,"union{\n");

	for(a=0;a<2*Size;a++){
		for(b=0;b<2*Size;b++){
			for(c=0;c<2*Size;c++){															
				if( lastdata3[a][b][c] == IMAGE){
					printf("%d %d %d\n",a,b,c);
					fprintf(fp_integrate1,"%d %d %d\n",a,b,c);
					fprintf(fp_integrate2,"object{b translate<%d,%d,%d>}\n",a,b,c);
				}
			}
		}
	}
	fprintf(fp_integrate2,"rotate <0,-clock*360*6/6,0> ");
	fprintf(fp_integrate2,"}");

	fclose(fp_integrate2);
	fclose(fp_integrate1);

	/*
	///make the outer boundary's data file for pov-ray///

	fp = fopen(output_file1,"w");

	fprintf(fp,"#include \"colors.inc\"\n");
	fprintf(fp,"#declare b=box{<0 0 0>,<1 1 1> texture{pigment{color rgb<0.8,0.8,1.0>}}}\n");
	fprintf(fp,"background { color rgb<0.5,0.5,0.5> }\n");
	fprintf(fp,"camera{location<50,80,-80> look_at<0,10,0>}\n");
	fprintf(fp,"light_source{ <60,60,-60> color rgb <1.0, 1.0, 1.0> }\n");
	fprintf(fp,"union{\n");

	for(a=0;a<Size;a++){
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){															
				if( lastdata1[a][b][c] == IMAGE){
					fprintf(fp,"object{b translate<%d,%d,%d>}\n",a,b,c);
				}
			}
		}
	}
	fprintf(fp,"rotate <0,-clock*360*6/6,0> ");
	fprintf(fp,"}");

	fclose(fp);

	///make the outer boundary's data file for pov-ray (half size)

	fp = fopen(output_file2,"w");
	fprintf(fp,"#include \"colors.inc\"\n");
	fprintf(fp,"#declare b=box{<0 0 0>,<1 1 1> texture{pigment{color rgb<0.8,0.8,1.0>}}}\n");
	fprintf(fp,"background { color rgb<0.5,0.5,0.5> }\n");
	fprintf(fp,"camera{location<50,65,-80> look_at<0,10,0>}\n");
	fprintf(fp,"light_source{ <60,60,-60> color rgb <1.0, 1.0, 1.0> }\n");
	fprintf(fp,"union{\n");

	for(a=0;a<Size;a++){
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){															
				if( lastdata1[a][b][c] == IMAGE){
					fprintf(fp,"object{b translate<%d,%f,%d>}\n",a,(0.5*b),c);
					
				}
			}
		}
	}

	fprintf(fp,"rotate <0,-clock*360*6/6,0> ");
	fprintf(fp,"}");

	fclose(fp);

	///make the inner boundary's data file for pov-ray

	fp = fopen(output_file3,"w");
			
	fprintf(fp,"#include \"colors.inc\"\n");
	fprintf(fp,"#declare b=box{<0 0 0>,<1 1 1> texture{pigment{color rgb<0.8,0.8,1.0>}}}\n");
	fprintf(fp,"background { color rgb<0.5,0.5,0.5> }\n");
	fprintf(fp,"camera{location<50,65,-80> look_at<0,10,0>}\n");
	fprintf(fp,"light_source{ <60,60,-60> color rgb <1.0, 1.0, 1.0> }\n");
	fprintf(fp,"union{\n");
	for(a=0;a<Size;a++){
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){															
				if( lastdata2[a][b][c] == IMAGE){
					fprintf(fp,"object{b translate<%d,%d,%d>}\n",a,b,c);
				}
			}
		}
	}

	fprintf(fp,"rotate <0,-clock*360*6/6,0> ");
	fprintf(fp,"}");

	fclose(fp);

	///make the inner boundary's data file for pov-ray (half size)

	fp = fopen(output_file4,"w");
	
	fprintf(fp,"#include \"colors.inc\"\n");
	fprintf(fp,"#declare b=box{<0 0 0>,<1 1 1> texture{pigment{color rgb<0.8,0.8,1.0>}}}\n");
	fprintf(fp,"background { color rgb<0.5,0.5,0.5> }\n");
	fprintf(fp,"camera{location<50,80,-80> look_at<0,10,0>}\n");
	fprintf(fp,"light_source{ <60,60,-60> color rgb <1.0, 1.0, 1.0> }\n");
	fprintf(fp,"union{\n");
	
	for(a=0;a<Size;a++){
		for(b=0;b<Size;b++){
			for(c=0;c<Size;c++){															
				if( lastdata2[a][b][c] == IMAGE){
				fprintf(fp,"object{b translate<%d,%f,%d>}\n",a,(0.5*b),c);

				}
			}
		}
	}
	fprintf(fp,"rotate <0,-clock*360*6/6,0> ");
	fprintf(fp,"}");

	fclose(fp);
*/	


return(0);
}