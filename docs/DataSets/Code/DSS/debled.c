/*****************************************************************/
/** C Implementation of the Discrete Straight Line Segmentation **/
/*********** with the Isabelle Debled's algorithm ****************/
/*                                                               */
/* David Coeurjolly March, 2002 (dcoeurjo@univ-lyon2.fr)         */
/*****************************************************************/


/*Main reference : Debeld-Rennesson and Reveillès, "A linear algorithm for segmentation
of digital curves", Int. Journ. Patt. Recogn. and Artif. Intell., vol 9, p635-662, 1995
*/

//Compilation : standart C language

//Warning  : The curve must be 8-connected...

#include <stdio.h>
#include <stdlib.h>
#include <math.h>



void exchange_axis(int *alpha,int *beta,int *gama,int *delta,int *phi,int *psi)
{
  int temp;

  temp = *alpha;
  *alpha = *gama;
  *gama = temp;
  temp = *beta;
  *beta = *delta;
  *delta = temp;
  temp = *phi;
  *phi = *psi;
  *psi = temp;
}

void change_horizontal_axis(int *alpha,int *beta,int *gama,int *delta,int *phi,int *psi)
{
   if (*alpha != 0)
    {
      *alpha *= -1;
      *phi *= -1;
    }
  else 
    if (*gama != 0)
      {
        *gama *= -1;
        *psi *= -1;
      }
}

void change_vertical_axis(int *alpha,int *beta,int *gama,int *delta,int *phi,int *psi)
{
  if (*delta != 0)
    {
      *delta *= -1;
      *psi *= -1;
    }
  else 
    if (*beta != 0)
      {
        *beta *= -1;
        *phi *= -1;
      }
}

void adjust_axis(int dx,int dy,int ddx,int ddy,int *alpha,int *beta,int *gama,int *delta,int *phi,int *psi)
{
  if (((dy == 0) && (ddx == dx)) && (ddy == -dx))
    change_vertical_axis(alpha,beta,gama,delta,phi,psi);
  else 
    if (((dx == 0) && (ddx == dy)) && (ddy == dy))
      change_horizontal_axis(alpha,beta,gama,delta,phi,psi);
    else 
      if ((((dx == dy) && (ddx == 0)) && (ddy == 0)) || (((dx == -dy) && (ddx
 == dx)) && (ddy == 0)))
        exchange_axis(alpha,beta,gama,delta,phi,psi);
}

void rotation(int xh,int yh,int dx,int dy,int *alpha,int *beta,int *gama,int *delta,int *phi,int *psi)
{
  if ((dx == 1) && (0 <= dy))
    {
      *alpha = 1;
      *beta = 0;
      *phi = -xh;
      *gama = 0;
      *delta = 1;
      *psi = -yh;
    }
  else 
    if ((dx <= 0) && (dy == 1))
      {
        *alpha = 0;
        *beta = 1;
        *phi = -yh;
        *gama = -1;
        *delta = 0;
        *psi = xh;
      }
    else 
      if ((dx == -1) && (dy <= 0))
        {
          *alpha = -1;
          *beta = 0;
          *phi = xh;
          *gama = 0;
          *delta = -1;
          *psi = yh;
        }
      else 
        if ((0 <= dx) && (dy == -1))
          {
            *alpha = 0;
            *beta = -1;
            *phi = yh;
            *gama = 1;
            *delta = 0;
            *psi = -xh;
          }
}
void recognize_segment(int N,int dMx[],int dMy[],int alpha,int beta,int gama,int delta,int phi,int psi,int *Ux,int *Uy,int *Lx,int *Ly,int *k,int *xk,int *yk,int *a,int *b,int *mu) 
{
  int  r,ddx,ddy,ddx_loc,ddy_loc,xx,yy,x_loc,y_loc;
   while ((*k)<N) 
     {
       ddx=dMx[*k+1];
       ddy=dMy[*k+1];
       ddx_loc=alpha*ddx+beta*ddy;
       ddy_loc=gama*ddx+delta*ddy;
       if ((ddx_loc <=0) || (ddy_loc <0)) return;
       xx=*xk+ddx;
       yy=*yk+ddy;
       x_loc=alpha*xx+beta*yy+phi;
       y_loc=gama*xx+delta*yy+psi;
       r=(*a)*x_loc-(*b)*y_loc;
       if ((r< *mu -1) || (r> *mu+*b))  return;
       *k=*k+1;
       *xk=xx;
       *yk=yy;
       if (r==*mu+*b) 
	 {
	 *Ux=x_loc-*b;
	 *Uy=y_loc+1-*a;
	 *a=y_loc-*Ly;
	 *b=x_loc-*Lx;
	 *mu=(*a)*(*Lx)-(*b)*(*Ly)-(*b)+1;
	 }
     else
       if (r==*mu-1)
	 {
	   *Lx=x_loc-*b;
	   *Ly=y_loc-1-*a;
	   *a=y_loc-*Uy;
	   *b=x_loc-*Ux;
	   *mu=(*a)*(*Ux)-(*b)*(*Uy);
	 }
     }
}

void moving_frame(int h,int xh,int yh,int N,int dMx[],int dMy[],int *k,int *xk,int *yk,int *alpha,int *beta,int *gama,int *delta,int *phi,int *psi,int *a,int *b,int *mu,int *Ux,int *Uy,int *Lx,int *Ly)
{
  int dx,dy;
   *k=h;
   *xk=xh;
   *yk=yh;
   dx=dMx[h+1];
   dy=dMy[h+1];
   while (((*k)<N) && (dMx[*k+1]==dx) && (dMy[*k+1]==dy))
     {
       *k=*k+1;
       *xk=*xk+dMx[*k];
       *yk=*yk+dMy[*k];
     }
   rotation(xh,yh,dx,dy,alpha,beta,gama,delta,phi,psi);
   if (*k<N) 
     adjust_axis(dx,dy,dMx[*k+1],dMy[*k+1],alpha,beta,gama,delta,phi,psi);
   
   if ((dx==0) || (dy==0))  
     *Lx=*k-h; 
   else
     *Lx=0;
   
   *a=1;
   *b=*Lx+1;
   *mu=0;
   *Ly=0;
   *Ux=0;
   *Uy=0;
}


//We compute the Discrete Straight Segment segmenation starting from
// the point (x0,y0)
// The discrete curve is coded using displacement ex: dMx[i]=X[i] - X[i-1]
//    (N is the length of the boundary)
// Warning the discrete curve must be 8-connected !!!
//
// We also return the length of the curve...
void segmentation(int x0,int y0,int dMx[],int dMy[],int N,double *length)
{
  int h,k,xk,yk,alpha,beta,gama,delta,phi,psi,a,b,mu,Ux,Uy,Lx,Ly,xh,yh;
      
  *length=0;
  h=0;
  xh=x0;
  yh=y0;
  
  printf("I start the segmentation from (%d,%d)\n",x0,y0);
  while (h<N) 
    {
      moving_frame(h,xh,yh,N,dMx,dMy,&k,&xk,&yk,&alpha,&beta,&gama,&delta,&phi,&psi,&a,&b,&mu,&Ux,&Uy,&Lx,&Ly);
      recognize_segment(N,dMx,dMy,alpha,beta,gama,delta,phi,psi,&Ux,&Uy,&Lx,&Ly,&k,&xk,&yk,&a,&b,&mu);  
      
      *length=*length+sqrt((xk-xh)*(xk-xh)+(yk-yh)*(yk-yh));
      
      if (k==N-1)  
      	{
	  h=N; 
	  xh=xk;
	  yh=yk;
	}
      else 
      	{ 
      	  h=k+1; 
      	  xh=xk+dMx[h]; 
      	  yh=yk+dMy[h];  
      	} 

      printf("New vertex  (%d %d)\n",xh,yh);
    }
}


void example()
{
  int DMX[32]={1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
  int DMY[32]={0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0,0, 0, 0, -1, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, 0};
  int DDMX[16]={1,1,1,1,1,1,1,1,1,1};
  int DDMY[16]={1,1,1,1,1,-1,-1,-1,-1};
  double length;

  segmentation(0,0,DMX,DMY,32,&length);

}


int main()
{
  example();
}
