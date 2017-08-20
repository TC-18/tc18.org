#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>

#define IMAGE 0 		//Image pixel
#define BACK 255		//Back pixel
#define HOLE 20 		//Number of holes
#define BOUND 50		//Boundary pixel
#define PLUS 0			//curvature code +
#define MINUS 100		//curvature code -
#define ZERO 200		//curvature code 0
#define LEAVE 10		//leave pixel
#define CURV_BACK 150	//Image pixel eliminate Boundary pixel

/*** read ".pgm" file header ***/
void read_header (int *x_size, int *y_size, FILE *input);

/*** culculate curvature ***/
void curv_cul(int x_size,int y_size,unsigned char **data_bin,int **curv_data);

/*** assign curvature code ***/
void curv_num(int x_size,int y_size,unsigned char **data_bin,int **curv_data);

/*** boudary tracking ***/
int bound(int x_start,int y_start,unsigned char **data,unsigned char **watch,struct curv_sequence *curv_sequence);

/*** reverse boundary tracking (for inside boundray) ***/
int bound_rev(int x_start,int y_start,unsigned char **data,unsigned char **watch,struct curv_sequence *curv_sequence);

/*** move pixel according to curvature ***/
void moving(int h,unsigned char **move,struct curv_sequence *curv_sequence,FILE *fp);

/*** fill boundary after move processing ***/
void paint(int x,int y,int Dcol,unsigned char **move,int x_size,int y_size);

/*** get pixel color ***/
int point(int x,int y,unsigned char **move);

/*** set pixel color ***/
void pset(int x,int y,int Dcol,unsigned char **move);

/*** eliminate pixel connected by one pixel ***/
int cut(int x_size,int y_size,unsigned char **data);

/*** leave pixel ***/
void leave_pix(int x_size,int y_size,unsigned char **data,unsigned char **leave);

/*** dynamic allocation for data array ***/
void *galloc(unsigned size);
void **allocate_two(int rows, int columns, int elem_size);
void ***allocate_three(int rows, int columns, int hight, int elem_size);
void free_two(void **array, int rows);
void free_three(void ***array, int rows, int columns);

//structure for pixel curvature and coordinate
	struct curv_sequence{
		int sign;
		int x_coordinate;
		int y_coordinate;
	};

/***********************************************************/
/*					   main process 					   */
/***********************************************************/
// / / / / / / / / / / / / / / / / / / / / / / / / / / / / //
//														   //
// Input>>												   //
// ".pgm" file											   //
//														   //
// Output>> 											   //
// OUTNAME**.pgm : skeletonized data file				   //
// OUTNAME_curve**.pgm : boundary data with curvature code //
//		 -> BLACK:+ , DARK GRAY:- , GRAY:0				   //
// error.txt : error coordinate     					   //
//														   //
// / / / / / / / / / / / / / / / / / / / / / / / / / / / / //
void main(void)
{
	int x_size,y_size;						 /* Object size 								 */
	int hole_x[HOLE],hole_y[HOLE],hole_num=0;/* hole coordinate,number of holes 			 */
	int back,n,hole,hole_check,flow;		 /* for dialog									 */
	int a,b,i,j,k,y,step;					 /* increment									 */
	int move_num;							 /* number of boundary pixel					 */
	int index;								 /* for processing state display				 */
	int remo_check; 						 /* for check whether there is	removable pixel  */
	char file[15];							 /* input file name 							 */
	char file_out[20];						 /* output file name							 */
	char out_name[30];						 /* curvature file name 						 */
	char out_name2[30]; 					 /* output file name with index 				 */
	char num[2];							 /* index for output file						 */
	unsigned char **data;					 /* storage for object							 */
	unsigned char **data_bin;				 /* storage for binary data 					 */
	unsigned char **leave;					 /* storage for leave pixel 					 */
	unsigned char **move;					 /* storage for image after processing			 */
	int **curv_data;						 /* storage for curvature						 */
	FILE *fp0,*fp1,*fp2;					 /* fp0:Input fp1:Output fp2:error				 */

	struct curv_sequence curv_sequence[10000];		 /* structure for curvature sequence */


	printf("### thinning by curvature ###\n");
	printf("Input file name (with extension '.pgm') >>");
	scanf("%s",file);	
	printf("Back pixel is Black or White?  Black(1) : White(0) >>");
	scanf("%d",&back);
	printf("How many times of processing? >>");
	scanf("%d",&n);
	printf("Does Image have holes? y(1) : n(0) >>");
	scanf("%d",&hole);
	if(hole == 1)
	{
		hole_check=1;
		while(hole_check==1)
		{
			printf("Input hole x-coordinate >>");
			scanf("%d",&hole_x[hole_num]);
			printf("Input hole y-coordinate >>");
			scanf("%d",&hole_y[hole_num]);
			printf("Is there any holes ? y:(1) n:(0)>>");
			scanf("%d",&hole_check);
			hole_num++;
		}
	}
	printf("Do you want boundary files? y:(1) n:(0)>>");
	scanf("%d",&flow);
	printf("Output file name (without extension) >>");
	scanf("%s",&file_out);

	//display processing time
	if(n>9)
	{
		index = n/10;
		printf("\n\n *** processing ***\n| . . . . | . . . . |\n* ");
	}

	/********************************************************************************/
	/*									read data									*/
	/********************************************************************************/

	//open Input file
	if((fp0 = fopen(file,"rb")) == NULL)
	{
		printf("[%s] file can't open.\n",file);
		exit(1);
	}

	//read data
	read_header(&x_size,&y_size,fp0);

	data = (unsigned char**)allocate_two(y_size*2,x_size*2,sizeof(unsigned char));
	
	for (y = 0; y < y_size; y++) 
	{
	   if (fread(data[y], sizeof(unsigned char), x_size, fp0) !=(unsigned int) x_size)
	   {
			printf("can't read data.\n");
			exit(0);
	   }
	}

	fclose(fp0);

	//if Back pixel is Black,then reverse object
	if(back==1)
	{
		for(i=0;i<y_size;i++)
		{
			for(j=0;j<x_size;j++)
			{
				data[i][j]=(255-data[i][j]);
			}
		}
	}
	
	/********************************************************************************/
	/*								  main process									*/
	/********************************************************************************/

	//file for error coordinate
	fp2 = fopen("error.txt","w");

	for(step=0;step<n;step++)
	{
		//attach index to Output file name
		strcpy(out_name , file_out);
		strcpy(out_name2,file_out);
		strcat(out_name2,"_curv");
		itoa(step,num,10);
		if(step<10)
			strcat(out_name,"00");
		else
			strcat(out_name,"0");
		strcat(out_name,num);
		strcat(out_name2,num);
		strcat(out_name,".pgm");
		strcat(out_name2,".pgm");
		fprintf(fp2,"\n***%d times process***\n",step+1);//error.txt
		
	/********************************************************************************/
	/*								  pre-process									 */
	/********************************************************************************/

		//eliminate pixel connected by one pixel
		leave = (unsigned char**)allocate_two(y_size,x_size,sizeof(unsigned char));

		leave_pix(x_size,y_size,data,leave);
		remo_check = 1;
	
		while(remo_check != 0)
		{
			remo_check = cut(x_size,y_size,data);
		}

		data_bin = (unsigned char**)allocate_two(y_size*2,x_size*2,sizeof(unsigned char));
		curv_data = (int**)allocate_two(y_size*2,x_size*2,sizeof(int));

		//binarization 255 -> 0 , 0 -> 1 for calculating curvature code
		for(i=0;i<y_size;i++)
		{
			for(j=0;j<x_size;j++)
			{
				if(data[i][j]==IMAGE)
					data_bin[i][j]=1;
				else
					data_bin[i][j]=0;
			}
		}

	/********************************************************************************/
	/*							   thinning process 								*/
	/********************************************************************************/

		//culculate curvature
		curv_cul(x_size,y_size,data_bin,curv_data);

		//assign curvature sign
		curv_num(x_size,y_size,data_bin,curv_data);

		//open file for curvature write out
		if(flow == 1)
		{
			fp1 = fopen(out_name2, "wb");	
			fprintf(fp1, "P5\n");
			fprintf(fp1, "%d %d\n", x_size, y_size);
			fprintf(fp1, "255\n");

			for (y = 0; y < y_size; y++) 
			{
				if (fwrite(data_bin[y], sizeof(unsigned char), x_size, fp1) != (unsigned int) x_size)
				{
					printf("[%s] file writting error.\n",fp1);
				}
			}
			fclose(fp1);
		}
	
		free_two((void**)curv_data,y_size*2);

		//boundary copy > data:for reference , data_bin:for process
		for(i=0;i<y_size;i++)
			for(j=0;j<x_size;j++)
				data[i][j] = data_bin[i][j];

		move = (unsigned char**)allocate_two(y_size*2,x_size*2,sizeof(unsigned char));

		for(i=0;i<y_size*2;i++)
			for(j=0;j<x_size*2;j++)
				move[i][j] = BACK;

		//process while there is processing object
			if(hole == 1)
			{
				for(k=0;k<hole_num;k++)
				{
				for(j=hole_x[k];j<x_size;j++)
				{
					for(i=hole_y[k];i<y_size;i++)	
					{
						if(data[i][j] != BACK)
						{
							goto out_rev;
						}
					}
				}
out_rev:
			a = i;b = j;

		//if there is no object,then go out while roop.
			if(a == y_size && b == x_size)
			{
				goto finish;
			}

		//boundary tracking
				move_num = bound_rev(j,i,data_bin,data,curv_sequence);

			//thinning
			moving(move_num,move,curv_sequence,fp2);

			}

		while(1)
		{
				for(i=0;i<y_size;i++)
				{
					for(j=0;j<x_size;j++)		
					{
						if(data[i][j] != BACK)
						{
							goto out_a;
						}
					}
				}

		//set start coordinate
out_a:
			a = i;b = j;

		//if there is no object,then go out while roop.
			if(a == y_size && b == x_size)
			{
				goto finish;
			}

		//boundary tracking
				move_num = bound(j,i,data_bin,data,curv_sequence);

			//thinning
			moving(move_num,move,curv_sequence,fp2);
	
		}//end of while roop
		}
		else
		{	
			while(1)
					{
						for(i=0;i<y_size;i++)
						{
							for(j=0;j<x_size;j++)
							{
								if(data[i][j] != BACK)
								{
									goto out_b;
								}
							}
						}
						
						//set start coordinate
out_b:
						a = i;b = j;
						
						//if there is no object,then go out while roop.
						if(a == y_size && b == x_size)
						{
							goto finish;
						}
						
						//boundary tracking
						move_num = bound(j,i,data_bin,data,curv_sequence);
						
						//smootihng
						moving(move_num,move,curv_sequence,fp2);
						
					}
				}

finish:

	/********************************************************************************/
	/*					  process after moving boundary 							*/
	/********************************************************************************/

		//avoid inclining toward the upper left
		for(a=0;a<(y_size*2);a++)
		{
			for(b=0;b<(x_size*2);b++)
			{
				if(a % 2 == 1 && b % 2 == 1)
				{						
					if(move[a][b] == ZERO)
					{
						(step % 2 == 0 ) ? (move[a/2 + 1][b/2 + 1]=BOUND) : (move[a/2][b/2]=BOUND);
						move[a][b] = BACK;
					}
				}
				else
					move[a][b] = BACK;
			}
		}

		//fill boundary(fill background)
		paint(0,0,IMAGE,move,x_size,y_size);
		
		//leave hole
		if(hole==1)
		{
			for(k=0;k<hole_num;k++)
			{
				paint(hole_x[k],hole_y[k],IMAGE,move,x_size,y_size);
			}
		}

		//reverse the image
		for(i=0;i<y_size;i++)
		{
			for(j=0;j<x_size;j++)
			{
				if((move[i][j] != IMAGE) || (leave[i][j] == LEAVE))//bring back pixel leaved pre-process
				{
					move[i][j] = IMAGE;
				}
				else
				{
					move[i][j] = BACK;
				}
			}
		}

		for(a=1;a<y_size-1;a++)
			for(b=1;b<x_size-1;b++)
				if((move[a-1][b-1] == BACK) && (move[a][b-1] == BACK) && (move[a+1][b-1] == BACK)
					&& (move[a-1][b] == BACK) && (move[a][b] == IMAGE) && (move[a+1][b] == BACK)
					&& (move[a-1][b+1] == BACK) && (move[a][b+1] == BACK) && (move[a+1][b+1] == BACK))
				{
					move[a][b] = BACK;//remove isolate pixel
				}

	/********************************************************************************/
	/*								 out put result 								*/
	/********************************************************************************/

		fp1 = fopen(out_name, "wb");	
		fprintf(fp1, "P5\n");
		fprintf(fp1, "%d %d\n", x_size, y_size);
		fprintf(fp1, "255\n");

		for (y = 0; y < y_size; y++) 
		{
			if (fwrite(move[y], sizeof(unsigned char), x_size, fp1) != (unsigned int) x_size)
			{
				printf("[%s] file writting error.\n",fp1);
			}
		}

		fclose(fp1);

		free_two((void**)leave,y_size);
		free_two((void**)move,y_size*2);

	//display processing time
		if(n>9)
		{
			if(step % index == 0)
				printf("* ");
		}

	/********************************************************************************/
	/*								Next step start 								*/
	/********************************************************************************/

		if((fp0 = fopen(out_name,"rb")) == NULL)
		{
			printf("[%s] file can't open.\n",file);
			exit(1);
		}
	
		read_header(&x_size,&y_size,fp0);
	
		for (y = 0; y < y_size; y++) 
		{
			if (fread(data[y], sizeof(unsigned char), x_size, fp0) !=(unsigned int) x_size)
			{
				printf("can't read data.\n");
				exit(0);
			}
		}

		fclose(fp0);
	
	//check whether there is object
		for(a=0;a<y_size;a++)
		{
			for(b=0;b<x_size;b++)
			{
				if(data[a][b] != IMAGE)
				{
					goto out_2;
				}
			}
		}
out_2:
		if(a==y_size && b==x_size)
		{
			printf("there is no object.\n");
			exit(0);
		}

		free_two((void**)data_bin,y_size*2);

	}//end of main process

	fclose(fp2);

	free_two((void**)data,y_size*2);
}

/*******************************************/
/***	   read .pgm file header		 ***/
/*******************************************/
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// x_size,y_size:Image size 			   //
// input:".pgm" file					   //
// / / / / / / / / / / / / / / / / / / / / //
void read_header(int *x_size, int *y_size, FILE *input)
{
	unsigned char token[100], step;   //token:array for string

	fscanf(input,"%s", token);

	if ( token[0] != 'P' || token[1] != '5'){
		printf("incorrect file type.\n");
	}
	
	do {
		do {
			step = fgetc(input);
		}while (step != '\n');		

		fscanf(input,"%s", token);		
	}while (token[0] == '#')	;  //skip comment text

	*x_size = atoi(token);
	fscanf(input,"%s", token);
	*y_size = atoi(token);

	fscanf(input,"%s", token);	
	fgets(token, 50, input);		
}

/*******************************************/
/***		culculate curvature 		 ***/
/*******************************************/
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// x_size,y_size:Image size 			   //
// data_bin:storage for binarized data	   //
// curv_data:storage for curvature		   //
// / / / / / / / / / / / / / / / / / / / / //
void curv_cul(int x_size,int y_size,unsigned char **data_bin,int **curv_data)
{
	int i,j;				 //increment
	int tmp_1=0,tmp_2=0;	 //temporary curvature

	for(i=1;i<y_size-1;i++)
	{
		for(j=1;j<x_size-1;j++)
		{
			//culculate curvature for edge pixel
			if((data_bin[i][j]	!= 0) && !(data_bin[i+1][j] == 1 && data_bin[i+1][j+1] == 1 && data_bin[i+1][j-1] 
				&& data_bin[i][j+1] == 1 && data_bin[i][j-1] == 1 && data_bin[i-1][j] == 1
				&& data_bin[i-1][j-1] == 1 && data_bin[i-1][j+1] == 1))
			{
				tmp_1 = data_bin[i+1][j] + data_bin[i-1][j] + data_bin[i][j+1] + data_bin[i][j-1];
				tmp_2 = data_bin[i][j+1] * data_bin[i+1][j+1] * data_bin[i+1][j] 
					+ data_bin[i+1][j] * data_bin[i+1][j-1] * data_bin[i][j-1] 
					+ data_bin[i][j-1] * data_bin[i-1][j-1] * data_bin[i-1][j] 
					+ data_bin[i-1][j] * data_bin[i-1][j+1] * data_bin[i][j+1];
				if((1 - (tmp_1 / 2.0) + (tmp_2 / 4.0)) == 0)
					curv_data[i][j] = 0;
				else
					curv_data[i][j] = (1 - (tmp_1 / 2.0) + (tmp_2 / 4.0)) > 0 ? 1 : -1;
			}
			else
			{
				curv_data[i][j] = CURV_BACK;
			}
		}
	}

	//edge of image plane
	for(i=0;i<y_size;i++)
	{
		curv_data[i][0] = CURV_BACK;
		curv_data[i][x_size-1] = CURV_BACK;
	}
	for(j=0;j<x_size;j++)
	{
		curv_data[0][j] = CURV_BACK;
		curv_data[y_size-1][j] = CURV_BACK;
	}
}

/*******************************************/
/***	   assign curvature code		 ***/
/*******************************************/
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// x_size,y_size:Image size 			   //
// data_bin:storage for binirized data	   //
// curv_data:storage for curvature		   //
// / / / / / / / / / / / / / / / / / / / / //
void curv_num(int x_size,int y_size,unsigned char **data_bin,int **curv_data)
{
	int i,j;	 //increment

	for(i=0;i<y_size;i++)
		for(j=0;j<x_size;j++)
		{
			if(curv_data[i][j] > 0)
			{
				if(curv_data[i][j] == CURV_BACK)
				{
					data_bin[i][j] = BACK;
				}
				else
				{
					data_bin[i][j] = PLUS;
				}
			}
			else if(curv_data[i][j] < 0)
			{
				data_bin[i][j] = MINUS;
			}
			else if(curv_data[i][j] == 0)
			{
				data_bin[i][j] = ZERO;
			}
		}
}

/*******************************************/
/***		 boundary tracking			 ***/
/*******************************************/
/*										   */
/* this trace based on 4-connection 	   */
/*										   */
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// x_start,y_start:start pixel coordinate  //
// data:storage for processing data 	   //
// watch:storage for reference data 	   //
// curv_sequence:structure for curvature   //
// Output>> 							   //
// h:number of boundary pixels			   //
// / / / / / / / / / / / / / / / / / / / / //
int bound(int x_start,int y_start,unsigned char **data,unsigned char **watch,struct curv_sequence *curv_sequence)
{
	int h=0;
	int x_now = x_start;
	int y_now = y_start;
	int vect = 1;//search direction
	int end = 1;

	//set initial value
	curv_sequence[h].sign = data[y_now][x_now];
	curv_sequence[h].x_coordinate = x_now;
	curv_sequence[h].y_coordinate = y_now;
	watch[y_now][x_now] = BACK;
	h++;
	
	//tracking
		while( x_now != x_start || y_now != y_start || end == 1)
		{
			switch(vect)
			{
			case 1: //check right direction
				if(data[y_now][x_now+1] != BACK)
				{
					x_now = x_now + 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect= 4;
					end = 0;
					break;
				}
			case 2:  //check lower direction
				if(data[y_now+1][x_now] != BACK)
				{
					y_now = y_now + 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect= 1;
					end = 0;
					break;
				}
			case 3:  //check left direction
				if(data[y_now][x_now-1] != BACK)
				{
					x_now = x_now - 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect= 2;
					end = 0;
					break;
				}
			case 4:  //check upper direction
				if(data[y_now-1][x_now] != BACK)
				{
					y_now = y_now - 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect = 3;
					end = 0;
					break;
				}
			default:
				{
					end=0;
					vect=1;
					break;
				}
			}
		}

	return(h);
}

/*******************************************/
/***	 boundary tracking(reverse) 	 ***/
/*******************************************/
/*										   */
/* this trace based on 4-connection 	   */
/*										   */
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// x_start,y_start:start pixel coordinate  //
// data:storage for processing data 	   //
// watch:storage for reference data 	   //
// curv_sequence:structure for curvature   //
// Output>> 							   //
// h:number of boudary pixels			   //
// / / / / / / / / / / / / / / / / / / / / //
int bound_rev(int x_start,int y_start,unsigned char **data,unsigned char **watch,struct curv_sequence *curv_sequence)
{
	int h=0;
	int x_now = x_start;
	int y_now = y_start;
	int vect = 2;//search direction
	int end = 1;

	//set initial value
	curv_sequence[h].sign = data[y_now][x_now];
	curv_sequence[h].x_coordinate = x_now;
	curv_sequence[h].y_coordinate = y_now;
	watch[y_now][x_now] = BACK;
	h++;
	
	//tracking
	while( x_now != x_start || y_now != y_start || end == 1)
	{
		switch(vect)
			{
			case 1:  //check left direction
				if(data[y_now][x_now-1] != BACK)
				{
					x_now = x_now - 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect= 4;
					end = 0;
					break;
				}
			case 2:  //check upper direction
				if(data[y_now-1][x_now] != BACK)
				{
					y_now = y_now - 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect = 1;
					end = 0;
					break;
				}
			case 3: //check right direction
				if(data[y_now][x_now+1] != BACK)
				{
					x_now = x_now + 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect= 2;
					end = 0;
					break;
				}
			case 4:  //check lower direction
				if(data[y_now+1][x_now] != BACK)
				{
					y_now = y_now + 1;
					curv_sequence[h].sign = data[y_now][x_now];
					curv_sequence[h].x_coordinate = x_now;
					curv_sequence[h].y_coordinate = y_now;
					watch[y_now][x_now] = BACK;
					h++;
					vect= 3;
					end = 0;
					break;
				}
			default:
				{
					end=0;
					vect=1;
					break;
				}
			}
		}
	return(h);
}

/*******************************************/
/***  move pixel according to curvature  ***/
/*******************************************/
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// h:number of boundary pixels			   //
// move:storage for processing data 	   //
// curv_sequence:structure for curvature   //
// fp:error coordinate file	               //
// / / / / / / / / / / / / / / / / / / / / //
void moving(int h,unsigned char **move,struct curv_sequence *curv_sequence,FILE *fp)
{	
	int start[3000],end[3000];				  /* array for each end-point						   */
	int final;								  /* final end-point								   */
	int i,j = 0;							  /* number of segments 							   */
	int x_start,y_start,x_end,y_end;		  /* coordinate of each end-point					   */
	int i_now;								  /* process times									   */
	int x_last1,y_last1,x_next1,y_next1;	  /* coordinate of pixel connected to start pixel	   */
	int x_last2,y_last2,x_next2,y_next2;	  /* coordinate of pixel connected to end pixel 	   */
	int tmp_end;
	int last,next;

	//pick up pixel whose curvature is + or -.
	for(i=0;i<h;i++)
	{
		if(curv_sequence[i].sign != BACK && curv_sequence[i].sign != ZERO)
		{
			if(j==0)
			{
				start[j] = i;
				final = i;
				j++;
			}
			else
			{
				start[j] = i;
				end[j-1] = i;
				j++;
			}
		}
	}
	end[j-1] = final;

	tmp_end = j-1;	
		
	//move segment by moving end-point.
	for(i=0;i<j;i++)
	{
		//coordinate of start and end pixel
		int x_move1,y_move1,x_move2,y_move2;
		y_start = curv_sequence[start[i]].y_coordinate * 2;
		x_start = curv_sequence[start[i]].x_coordinate * 2;
		y_end = curv_sequence[end[i]].y_coordinate * 2;
		x_end = curv_sequence[end[i]].x_coordinate * 2;

		//if pixel is isolated,then skip process
		if(j==1)
			goto isolate;
				
		//check pixel which connected to start pixel and end pixel
		i_now = i;
		if(i_now == 0)
		{
			if(start[tmp_end]+1 >= h)
			{
				x_last1 = curv_sequence[start[tmp_end]-1].x_coordinate * 2;
				y_last1 = curv_sequence[start[tmp_end]-1].y_coordinate * 2;
			}
			else
			{
				x_last1 = curv_sequence[start[0]-1].x_coordinate * 2;
				y_last1 = curv_sequence[start[0]-1].y_coordinate * 2;
			}

			x_next1 = curv_sequence[start[0]+1].x_coordinate * 2;
			y_next1 = curv_sequence[start[0]+1].y_coordinate * 2;
			x_last2 = curv_sequence[end[0]-1].x_coordinate * 2;
			y_last2 = curv_sequence[end[0]-1].y_coordinate * 2;
			x_next2 = curv_sequence[end[0]+1].x_coordinate * 2;
			y_next2 = curv_sequence[end[0]+1].y_coordinate * 2;
		}
		else if(i_now == j-1)
		{
			x_last1 = curv_sequence[start[tmp_end]-1].x_coordinate * 2;
			y_last1 = curv_sequence[start[tmp_end]-1].y_coordinate * 2;
			if(start[tmp_end]+1 >= h)
			{					  //case that first pixel connect to final pixel 
				x_next1 = curv_sequence[start[0]+1].x_coordinate * 2;	  
				y_next1 = curv_sequence[start[0]+1].y_coordinate * 2;
			}
			else 
			{
				x_next1 = curv_sequence[start[tmp_end]+1].x_coordinate * 2;
				y_next1 = curv_sequence[start[tmp_end]+1].y_coordinate * 2;
			}
		
			x_last2 = curv_sequence[h-1].x_coordinate * 2;
			y_last2 = curv_sequence[h-1].y_coordinate * 2;
			x_next2 = curv_sequence[start[0]+1].x_coordinate * 2;
			y_next2 = curv_sequence[start[0]+1].y_coordinate * 2;
		}
		else 
		{
			x_last1 = curv_sequence[start[i_now]-1].x_coordinate * 2;
			y_last1 = curv_sequence[start[i_now]-1].y_coordinate * 2;
			x_next1 = curv_sequence[start[i_now]+1].x_coordinate * 2;
			y_next1 = curv_sequence[start[i_now]+1].y_coordinate * 2;
			x_last2 = curv_sequence[end[i_now]-1].x_coordinate * 2;
			y_last2 = curv_sequence[end[i_now]-1].y_coordinate * 2;
			if(end[i_now]+1 >= h)						
			{					  //case that end-point are continuing
				x_next2 = curv_sequence[0].x_coordinate * 2;
				y_next2 = curv_sequence[0].y_coordinate * 2;
			}
			else 
			{
				x_next2 = curv_sequence[end[i_now]+1].x_coordinate * 2;
				y_next2 = curv_sequence[end[i_now]+1].y_coordinate * 2;
			}
		}

		if(i_now == 0)
		{
			if(curv_sequence[start[0]].x_coordinate == curv_sequence[end[j-1]].x_coordinate && curv_sequence[start[0]].y_coordinate == curv_sequence[end[j-1]].y_coordinate) 
				last = curv_sequence[start[j-1]].sign;
			else
				last = curv_sequence[start[j-1]].sign;
			
			next = curv_sequence[end[1]].sign;
		}
		else if(i_now == j-1)
		{
			if(curv_sequence[start[0]].x_coordinate == curv_sequence[end[j-1]].x_coordinate && curv_sequence[start[0]].y_coordinate == curv_sequence[end[j-1]].y_coordinate) 
				next = curv_sequence[end[0]].sign;
			else
				next = curv_sequence[end[0]].sign;
				
			last = curv_sequence[start[j-2]].sign;
		}
		else
		{
			last = curv_sequence[start[i_now - 1]].sign;
			next = curv_sequence[end[i_now + 1]].sign;
		}
			
				
		if(x_start == x_end && y_start == y_end)
		{
			;
		}
		else if(curv_sequence[start[i_now]].sign == PLUS && curv_sequence[end[i_now]].sign == PLUS)
		{
			if(x_start == x_last1 && y_start == y_last1 - 2 && x_start == x_next1 - 2 && y_start == y_next1)
			{
				x_move1 = x_start + 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end + 1;
				move[y_start + 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end - 1] = ZERO;
			}
			else if(x_start == x_last1 + 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 - 2)
			{
				x_move1 = x_start - 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end - 1;
				move[y_start + 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end - 1] = ZERO;
			}
			else if(x_start == x_last1 && y_start == y_last1 + 2 && x_start == x_next1 + 2 && y_start == y_next1)
			{
				x_move1 = x_start - 1,y_move1 = y_start - 1;
				x_move2 = x_end + 1,y_move2 = y_end - 1;
				move[y_start - 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end + 1] = ZERO;
			}
			else if(x_start == x_last1 - 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 + 2)
			{
				x_move1 = x_start + 1,y_move1 = y_start - 1;
				x_move2 = x_end +1,y_move2 = y_end + 1;
				move[y_start - 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end + 1] = ZERO;
			}
			else if(x_last1 == x_next1 && y_last1 == y_next1)
			{
				fprintf(fp,"%d,%d,start pixel is identical with end pixel.\n",x_last1,y_last1);
			}
			else
			{
				x_move1 = y_move1 = x_move2 = y_move2 = 0;
				fprintf(fp,"ERROR!!(++)\n");
				fprintf(fp,"x:%d y:%d i:%d\n",x_start,y_start,i_now);
				fprintf(fp,"x_m:%d y_m:%d x_a:%d y_a:%d\n",x_last1,y_last1,x_next1,y_next1);
			}
		}
		else if(curv_sequence[start[i_now]].sign == MINUS && curv_sequence[end[i_now]].sign == MINUS)
		{
			if(x_start == x_last1 && y_start == y_last1 + 2 && x_start == x_next1 - 2 && y_start == y_next1)
			{
				x_move1 = x_start + 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end + 1;
				move[y_start + 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end - 1] = ZERO;
			}
			else if(x_start == x_last1 + 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 + 2)
			{
				x_move1 = x_start + 1,y_move1 = y_start - 1;
				x_move2 = x_end + 1,y_move2 = y_end + 1;
				move[y_start - 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end + 1] = ZERO;
			}
			else if(x_start == x_last1 - 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 - 2)
			{
				x_move1 = x_start - 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end - 1;
				move[y_start + 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end - 1] = ZERO;
			}
			else if(x_start == x_last1 && y_start == y_last1 - 2 && x_start == x_next1 + 2 && y_start == y_next1)
			{
				x_move1 = x_start - 1,y_move1 = y_start - 1;
				x_move2 = x_end + 1,y_move2 = y_end - 1;
				move[y_start - 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end + 1] = ZERO;
			}
			else
			{
				x_move1 = y_move1 = x_move2 = y_move2 = 0;
				fprintf(fp,"ERROR!!(--)\n");
				fprintf(fp,"x:%d y:%d i:%d\n",x_start,y_start,i_now);
				fprintf(fp,"x_m:%d y_m:%d x_a:%d y_a:%d\n",x_last1,y_last1,x_next1,y_next1);
			}
		}
		else if(curv_sequence[start[i_now]].sign == PLUS && curv_sequence[end[i_now]].sign == MINUS)
		{
			if(x_start == x_last1 + 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 - 2)
			{
				x_move1 = x_start - 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end - 1;
				move[y_start + 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end - 1] = ZERO;
				move[y_end + 1][x_end - 1] = ZERO;
			}
			else if(x_start == x_last1 && y_start == y_last1 + 2 && x_start == x_next1 + 2 && y_start == y_next1)
			{
				x_move1 = x_start - 1,y_move1 = y_start - 1;
				x_move2 = x_end + 1,y_move2 = y_end - 1;
				move[y_start - 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end + 1] = ZERO;
				move[y_end - 1][x_end - 1] = ZERO;
			}
			else if(x_start == x_last1 - 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 + 2)
			{
				x_move1 = x_start + 1,y_move1 = y_start - 1;
				x_move2 = x_end + 1,y_move2 = y_end + 1;
				move[y_start - 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end + 1] = ZERO;
				move[y_end - 1][x_end + 1] = ZERO;
			}
			else if(x_start == x_last1 && y_start == y_last1 - 2 && x_start == x_next1 - 2 && y_start == y_next1)
			{
				x_move1 = x_start + 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end + 1;
				move[y_start + 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end - 1] = ZERO;
				move[y_end + 1][x_end + 1] = ZERO;
			}
			else
			{
				x_move1 = y_move1 = x_move2 = y_move2 = 0;
				fprintf(fp,"ERROR!!(+-)\n");
				fprintf(fp,"x:%d y:%d i:%d\n",x_start,y_start,i_now);
				fprintf(fp,"x_m:%d y_m:%d x_a:%d y_a:%d\n",x_last1,y_last1,x_next1,y_next1);
			}
		}
		else if(curv_sequence[start[i_now]].sign == MINUS && curv_sequence[end[i_now]].sign == PLUS)
		{
			if(x_start == x_last1 - 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 - 2)
			{
				x_move1 = x_start - 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end - 1;
				move[y_start + 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end - 1] = ZERO;
				move[y_start - 1][x_start - 1] = ZERO;
			}
			else if(x_start == x_last1 && y_start == y_last1 - 2 && x_start == x_next1 + 2 && y_start == y_next1)
			{
				x_move1 = x_start - 1,y_move1 = y_start - 1;
				x_move2 = x_end + 1,y_move2 = y_end - 1;
				move[y_start - 1][x_start - 1] = ZERO;
				move[y_end - 1][x_end + 1] = ZERO;
				move[y_start - 1][x_start + 1] = ZERO;
			}
			else if(x_start == x_last1 + 2 && y_start == y_last1 && x_start == x_next1 && y_start == y_next1 + 2)
			{
				x_move1 = x_start + 1,y_move1 = y_start - 1;
				x_move2 = x_end + 1,y_move2 = y_end + 1;
				move[y_start - 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end + 1] = ZERO;
				move[y_start + 1][x_start + 1] = ZERO;
			}
			else if(x_start == x_last1 && y_start == y_last1 + 2 && x_start == x_next1 - 2 && y_start == y_next1)
			{
				x_move1 = x_start + 1,y_move1 = y_start + 1;
				x_move2 = x_end - 1,y_move2 = y_end + 1;
				move[y_start + 1][x_start + 1] = ZERO;
				move[y_end + 1][x_end - 1] = ZERO;
				move[y_start + 1][x_start - 1] = ZERO;
			}
			else
			{
				x_move1 = y_move1 = x_move2 = y_move2 = 0;
				fprintf(fp,"ERROR!!(-+)\n");
				fprintf(fp,"x:%d y:%d i:%d\n",x_start,y_start,i_now);
			}
		}
		else
		{
			x_move1 = y_move1 = x_move2 = y_move2 = 0;
			fprintf(fp,"ERROR!!(flow)\n");
			fprintf(fp,"x:%d y:%d i:%d\n",x_start,y_start,i_now);
		}

		//connect segment
		if(x_move1 == x_move2 && y_move1 == y_move2)
		{
			;
		}
		else if(x_move1 == x_move2)
		{
			if(y_move1 < y_move2)
			{
				y_move1++;
				for(y_move1;y_move1 < y_move2;y_move1++)
				{
					move[y_move1][x_move1] = ZERO;
				}
			}
			else
			{
				y_move2++;
				for(y_move2;y_move2 < y_move1;y_move2++)
				{
					move[y_move2][x_move1] = ZERO;
				}
			}
		}
		else if(y_move1 == y_move2)
		{
			if(x_move1 < x_move2)
			{
				x_move1++;
				for(x_move1;x_move1 < x_move2;x_move1++)
				{
					move[y_move1][x_move1] = ZERO;
				}
			}
			else
			{
				x_move2++;
				for(x_move2;x_move2 < x_move1;x_move2++)
				{
					move[y_move1][x_move2] = ZERO;
				}
			}
		}
		else 
		{
			fprintf(fp,"can't connect segment.\n");
			fprintf(fp,"x_s:%d y_s:%d x_e:%d y_e:%d\n",x_move1,y_move1,x_move2,y_move2);
		}
	}
isolate: ;
}

/*******************************************/
/*** fill boundary after move processing ***/
/*******************************************/
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// x,y:coordinate of starting pixel 	   //
// Dcol:pixel color 					   //
// move:storage for processing data 	   //
// x_size,y_size:Image size 			   //
// / / / / / / / / / / / / / / / / / / / / //
void paint(int x,int y,int Dcol,unsigned char **move,int x_size,int y_size)
{
	struct bufstr {
		int sx;
		int sy;
	};
	struct bufstr buffer[1024];
	int start_idx,end_idx;
	int LX,RX,UY,DY;
	int i;
	int col;

	start_idx = 0;
	end_idx = 1;
	buffer[start_idx].sx = x;
	buffer[start_idx].sy = y;
	col = point(x,y,move);

	do {
		LX = buffer[start_idx].sx;
		RX = buffer[start_idx].sx;
		UY = buffer[start_idx].sy;
		DY = buffer[start_idx].sy;
		start_idx++;
		if ( start_idx == x_size ) start_idx = 0;

		//if pixel was processed, disregard the pixel
		if ( point(LX,UY,move) == Dcol )
			continue;

		//serch for right side boundary
		while ( RX < x_size - 1 ) {
			RX++;
			if ( point(RX,UY,move) != col ) {
				RX--;
				break;
			}
		}
		//search for left side boundary
		while ( LX > 0) {
			LX--;
			if ( point(LX,UY,move) != col ) {
				LX++;
				break;
			}
		}
		// draw line LX-RX
		for( i = LX; i <= RX; i++ )
			pset(i,UY,Dcol,move);

		// scan over line
		if( --UY >= 0 )
		{
			i = LX;
			while( i <= RX )
			{
				// skip non-region color
				for( ; i < RX; i++ )
					if(point(i,UY,move) == col)
						break;
				if(point(i,UY,move) != col) 
					break;
				// skip region color
				for( ; i <= RX; i++)
					if( point(i,UY,move) != col)
						break;
				buffer[end_idx].sx = i - 1;
				buffer[end_idx].sy = UY;
				end_idx++;
				if(end_idx == x_size)
					end_idx = 0;
			}
		}

		// scan under line
		if( ++DY <= y_size )
		{
			i = LX;
			while( i <= RX )
			{
				// skip non-region color
				for( ; i < RX; i++ )
					if(point(i,DY,move) == col) 
						break;
				if(point(i,DY,move) != col)
					break;
				// skip region color
				for( ; i <= RX; i++ )
					if(point(i,DY,move) != col)
						break;
				buffer[end_idx].sx = i - 1;
				buffer[end_idx].sy = DY;
				end_idx++;
				if( end_idx == x_size )
					end_idx = 0;
			}
		}
	}while ( start_idx != end_idx );
}

/*******************************************/
/***		   get pixel color			 ***/
/*******************************************/
// / / / / / / / / / / / / / / / / / / / / //
// Input>>								   //
// x,y:pixel coordinate 				   //
// move:storage for object				   //
// Output>> 							   //
// color:pixel color					   //
// / / / / / / / / / / / / / / / / / / / / //
int point(int x,int y,unsigned char **move)
{
	int color;

	color = move[y][x];
	return(color);
}

/***********************************************/
/***			 set pixel color			 ***/
/***********************************************/
// / / / / / / / / / / / / / / / / / / / / / / //
// Input>>									   //
// x,y:pixel coordinate 					   //
// Dcol:pixel color 						   //
// move:storage for object					   //
// / / / / / / / / / / / / / / / / / / / / / / //
void pset(int x,int y,int Dcol,unsigned char **move)
{
	move[y][x] = Dcol;
}

/***********************************************/
/*** eliminate pixel connected by one pixel  ***/
/***********************************************/
// / / / / / / / / / / / / / / / / / / / / / / //
// Input>>									   //
// x_size,y_size:Image size 				   //
// move:storage for processing data 		   //
// Output>> 								   //
// 0:there is not removable pixel			   //
// 1:there is removable pixel				   //
// / / / / / / / / / / / / / / / / / / / / / / //
int cut(int x_size,int y_size,unsigned char **data)
{
	int a,b;//increment
	int check=0;//return value
	unsigned char **data_2;//storage for temporary data
	data_2 = (unsigned char**)allocate_two(y_size*2,x_size*2,sizeof(unsigned char));

	for(a=1;a<y_size-1;a++)
		for(b=1;b<x_size-1;b++)
		{
			data_2[a][b] = BACK;
			if(data[a][b] == IMAGE)
				{
				if((data[a][b-1] == BACK) && (data[a][b+1] == BACK))
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if((data[a-1][b] == BACK) && (data[a+1][b] == BACK))					
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a-1][b-1] == BACK && data[a-1][b] == IMAGE && data[a-1][b+1] == IMAGE
						&& data[a][b-1] == IMAGE && data[a][b+1] == BACK
						&& data[a+1][b-1] == IMAGE && data[a+1][b] == BACK && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a-1][b-1] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b+1] == BACK
						&& data[a][b-1] == BACK && data[a][b+1] == IMAGE
						&& data[a+1][b-1] == BACK && data[a+1][b] == BACK && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a-1][b-1] == IMAGE && data[a-1][b] == BACK && data[a-1][b+1] == BACK
						&& data[a][b-1] == IMAGE && data[a][b+1] == BACK
						&& data[a+1][b-1] == BACK && data[a+1][b] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a-1][b-1] == BACK && data[a-1][b] == BACK && data[a-1][b+1] == IMAGE
						&& data[a][b-1] == BACK && data[a][b+1] == IMAGE
						&& data[a+1][b-1] == IMAGE && data[a+1][b] == IMAGE && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}

				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == IMAGE && data[a+1][b-1] == IMAGE 
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == BACK && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == BACK && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == BACK && data[a+1][b-1] == IMAGE
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == IMAGE && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == IMAGE && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == BACK && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}

				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == BACK && data[a-1][b] == BACK && data[a-1][b+1] == IMAGE 
					&& data[a+1][b-1] == IMAGE && data[a+1][b] == IMAGE && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == IMAGE	&& data[a-1][b] == BACK && data[a-1][b+1] == BACK 
					&& data[a+1][b-1] == BACK && data[a+1][b] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == BACK && data[a-1][b] == IMAGE && data[a-1][b+1] == IMAGE 
					&& data[a+1][b-1] == IMAGE && data[a+1][b] == BACK && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == IMAGE	&& data[a-1][b] == IMAGE && data[a-1][b+1] == BACK 
					&& data[a+1][b-1] == BACK && data[a+1][b] == BACK && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}

				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == IMAGE && data[a+1][b-1] == IMAGE 
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == IMAGE && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == IMAGE && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == IMAGE && data[a+1][b-1] == IMAGE
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == IMAGE && data[a+1][b+1] == BACK)
				{
					data_2[a][b] = BACK;
					check=1;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == IMAGE && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					data_2[a][b] = BACK;
					check=1;
				}

				else
				{
					data_2[a][b] = IMAGE;
				}
			}
		}

		for(a=0;a<y_size;a++)
			for(b=0;b<x_size;b++)
			{
				data[a][b] = data_2[a][b];
			}

		free_two((void**)data_2,y_size*2);
		return(check);
}

/*************************************************/
/***			   leave pixel				   ***/
/*************************************************/
/*												 */
/* leave component composed one-pixel thickness  */
/*												 */
// / / / / / / / / / / / / / / / / / / / / / / / //
// Input>>										 //
// x_size,y_size:Image size 					 //
// data:storage for object						 //
// leave:storage for leave data 				 //
// / / / / / / / / / / / / / / / / / / / / / / / //
void leave_pix(int x_size,int y_size,unsigned char **data,unsigned char **leave)
{
	int a,b;

	for(a=2;a<y_size-2;a++)
		for(b=2;b<x_size-2;b++)
		{
			if(data[a][b] == IMAGE)
			{
				if(data[a-1][b-1] == BACK && data[a-1][b] == IMAGE && data[a-1][b+1] == IMAGE
						&& data[a][b-1] == IMAGE && data[a][b+1] == BACK
						&& data[a+1][b-1] == IMAGE && data[a+1][b] == BACK && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a-1][b-1] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b+1] == BACK
						&& data[a][b-1] == BACK && data[a][b+1] == IMAGE
						&& data[a+1][b-1] == BACK && data[a+1][b] == BACK && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a-1][b-1] == IMAGE && data[a-1][b] == BACK && data[a-1][b+1] == BACK
						&& data[a][b-1] == IMAGE && data[a][b+1] == BACK
						&& data[a+1][b-1] == BACK && data[a+1][b] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a-1][b-1] == BACK && data[a-1][b] == BACK && data[a-1][b+1] == IMAGE
						&& data[a][b-1] == BACK && data[a][b+1] == IMAGE
						&& data[a+1][b-1] == IMAGE && data[a+1][b] == IMAGE && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}

				else if((data[a][b-1] == BACK) && (data[a][b+1] == BACK))
				{
					leave[a][b] = LEAVE;
				}
				else if((data[a-1][b] == BACK) && (data[a+1][b] == BACK))					
				{
					leave[a][b] = LEAVE;
				}

				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == IMAGE && data[a+1][b-1] == IMAGE 
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == BACK && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == BACK && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == BACK && data[a+1][b-1] == IMAGE
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == IMAGE && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == IMAGE && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == BACK && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}

				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == BACK && data[a-1][b] == BACK && data[a-1][b+1] == IMAGE 
					&& data[a+1][b-1] == IMAGE && data[a+1][b] == IMAGE && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == IMAGE	&& data[a-1][b] == BACK && data[a-1][b+1] == BACK 
					&& data[a+1][b-1] == BACK && data[a+1][b] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == BACK && data[a-1][b] == IMAGE && data[a-1][b+1] == IMAGE 
					&& data[a+1][b-1] == IMAGE && data[a+1][b] == BACK && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a][b+1] == IMAGE && data[a][b-1] == IMAGE && data[a-1][b-1] == IMAGE	&& data[a-1][b] == IMAGE && data[a-1][b+1] == BACK 
					&& data[a+1][b-1] == BACK && data[a+1][b] == BACK && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}
				
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == IMAGE && data[a+1][b-1] == IMAGE 
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == IMAGE && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == IMAGE && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == BACK && data[a][b-1] == IMAGE && data[a+1][b-1] == IMAGE
					&& data[a-1][b+1] == IMAGE && data[a][b+1] == IMAGE && data[a+1][b+1] == BACK)
				{
					leave[a][b] = LEAVE;
				}
				else if(data[a+1][b] == IMAGE && data[a-1][b] == IMAGE && data[a-1][b-1] == IMAGE && data[a][b-1] == IMAGE && data[a+1][b-1] == BACK 
					&& data[a-1][b+1] == BACK && data[a][b+1] == IMAGE && data[a+1][b+1] == IMAGE)
				{
					leave[a][b] = LEAVE;
				}
			}
		}
}


/*******************************************/
/*	 dynamic allocation for data array	   */
/*******************************************/
void *galloc(unsigned size)
{
	void *p = malloc(size);
	
	if (p == NULL) {
		printf("insufficient memory\n");
		exit(-1);
	}
	return p;
}

void **allocate_two(int rows, int columns, int elem_size)
{
	int row;
	void **array;
	
	array = (void**)galloc(rows * sizeof(void *));
	
	for (row=0; row<rows; ++row) {
		array[row] = (void*)galloc(columns * elem_size);
	}
	
	return array;
}

void ***allocate_three(int rows, int columns, int hight, int elem_size)
{
	int row;
	void ***array;
	
	array = (void***)galloc(rows * sizeof(void *));
	
	for (row=0; row<rows; ++row) {
		array[row] = (void**)allocate_two(columns, hight, elem_size);
	}
	
	return array;
}

void free_two(void **array, int rows)
{
	int row;
	
	for (row = 0; row < rows; row++) {
		free(array[row]);
	}
}

void free_three(void ***array, int rows, int columns)
{
	int row;
	
	for (row=0; row<rows; ++row) {
		free_two(array[row], columns);
	}
}
