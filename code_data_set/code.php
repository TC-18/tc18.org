<?php include("/home-projets/tc18/public_html/header.html") ?>


<div id="site_content">



<h1> Code</h1>
<br>
<h2> Libraries</h2>
<br>
<table style="width: 80%;" border="1">
  <tbody>
    <tr>
      <td bgcolor="#ffff9c" width="50%">Description</td>
      <td style="text-align: left; vertical-align: middle;"
 bgcolor="#ffff9c" width="50%">Main Reference</td>
      <td bgcolor="#ffff9c"> Language </td>
      <td bgcolor="#ffff9c">Download</td>
    </tr>
    <tr>
      <td width="50%"><font color="green">DGtal</font> Digital Geometry Tools and Algorithms </td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C++</td>
      <td><a href="http://www.dgtal.org/">go</a></td>      
    </tr>
    <tr>
      <td width="50%"><font color="green">OLENA</font>Image processing, image recognition, and artificial vision</td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C++</td>
      <td><a href="http://www.lrde.epita.fr/cgi-bin/twiki/view/Olena/">go</a></td>      
    </tr>
    <tr>
      <td width="50%"><font color="green">ORFEO</font> Optical and Radar Federated Earth Observation</td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C++</td>
      <td><a href="http://www.orfeo-toolbox.org/">go</a></td>      
    </tr>
    <tr>
      <td width="50%"><font color="green">VIGRA</font> Vision with Generic Algorithms </td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C++</td>
      <td><a href="https://ukoethe.github.io/vigra/">go</a></td>      
    </tr>
    <tr>
      <td width="50%"><font color="green">PINK</font> PINK Image Processing Library</td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C++</td>
      <td><a href="https://pinkhq.com/joomla/">go</a></td>      
    </tr>
  </tbody>
</table>



<h2> Grid, cells, structures and topology</h2>
<a name="grids">
<i> and basic librairies to handle discrete objects...</i>
</a><a name="grids"><br>
</a>
<table style="width: 80%;" border="1">
  <tbody>
    <tr>
      <td bgcolor="#ffff9c" width="50%">Description</td>
      <td style="text-align: left; vertical-align: middle;"
 bgcolor="#ffff9c" width="50%">Main Reference</td>
      <td bgcolor="#ffff9c"> Language </td>
      <td bgcolor="#ffff9c">Download</td>
      <td bgcolor="#ffff9c">Snapshots</td>
      <td style="white-space: nowrap;" bgcolor="#ffff9c">submitted by</td>
    </tr>
    <tr>
      <td width="50%"><font color="green">Simplevol</font> a simple and
powerful library to manipulate 3D images </td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C++</td>
      <td><a href="http://liris.cnrs.fr/david.coeurjolly/Code/SimpleVol/">go</a></td>
      <td>see <a href="3D_images.html">3D data set web page</a></td>
      <td style="white-space: nowrap;">
      <a href="mailto:dcoeurjo@liris.cnrs.fr">D.Coeurjolly</a></td>
    </tr>
<!--     <tr> -->
<!--       <td width="50%"><font color="green">Voltools</font> a set of -->
<!-- utilities to handle <font color="green">vol</font> files </td> -->
<!--       <td style="text-align: left; vertical-align: middle;" width="30%">---</td> -->
<!--       <td>C++</td> -->
<!--       <td><a href="http://liris.cnrs.fr/david.coeurjolly/Code/SimpleVol/">go</a></td> -->
<!--       <td>see <a href="3D_images.html">3D data set web page</a></td> -->
<!--       <td style="white-space: nowrap;"> -->
<!--       <a href="mailto:dcoeurjo@liris.cnrs.fr">D.Coeurjolly</a></td> -->
<!--     </tr> -->
    <tr>
      <td width="50%">The Npic library provides types and functions in C language to manipulate bitmap images of dimension 2 to 6,
and command line tools that give access to the library functions (drawing, distance transforms, medial axis,
converting file formats, and more). Some computations are multi-threaded with OpenMP.</td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C</td>
      <td><a href="http://pageperso.lif.univ-mrs.fr/~edouard.thiel/npic/index.html">go</a></td>
      <td>&nbsp;</td>
      <td style="white-space: nowrap;"><a href="mailto:Edouard.Thiel@lif.univ-mrs.fr">E. Thiel</a></td>
    </tr>
<!--     <tr> -->
<!--       <td style="text-align: left; vertical-align: middle;"><font -->
<!--  color="green">LibLongVol</font>, a simple and -->
<!-- powerful library to manipulate 3D images in which voxel are long -->
<!-- integers</td> -->
<!--       <td style="text-align: left; vertical-align: middle;">&nbsp;</td> -->
<!--       <td style="text-align: left; vertical-align: middle;">C++<br> -->
<!--       </td> -->
<!--       <td style="text-align: left; vertical-align: middle;"><a -->
<!--  href="http://liris.cnrs.fr/david.coeurjolly/Code/SimpleVol/">go</a><br> -->
<!--       </td> -->
<!--       <td style="text-align: left; vertical-align: middle;">&nbsp; </td> -->
<!--       <td -->
<!--  style="text-align: left; vertical-align: middle; white-space: nowrap;"><a -->
<!--  href="mailto:dcoeurjo@liris.cnrs.fr">D. Coeurjolly</a> </td> -->
<!--     </tr> -->
    <tr>
      <td width="50%">Skeletonization for 2D binary data by curvature</td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C</td>
      <td><a href="Code/skeleton_by_curvature/">go</a></td>
      <td>&nbsp;</td>
      <td style="white-space: nowrap;">A. Imiya</td>
    </tr>
    <tr>
      <td width="50%">Boundary Extraction for 3D binary image by
Distance Transformation</td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C</td>
      <td><a href="Code/boundary/">go</a></td>
      <td><img src="Code/boundary/sphere1.gif" width="100"></td>
      <td style="white-space: nowrap;">A. Imiya</td>
    </tr>
  </tbody>
</table>
<font color="red"><b> See also:</b></font>
<ul>
  <li> QVox in the <a href="#misc">Miscellaneous</a> section
(implementation of thinning algorithms)
  </li>
</ul>
<h2> nD objects and recognition</h2>
<a name="objects">
</a>
<table style="width: 80%;" border="1">
  <tbody>
    <tr>
      <td bgcolor="#ffff9c" width="50%">Description</td>
      <td style="text-align: left; vertical-align: middle;"
 bgcolor="#ffff9c" width="50%">Main Reference</td>
      <td bgcolor="#ffff9c"> Language </td>
      <td bgcolor="#ffff9c">Download</td>
      <td bgcolor="#ffff9c">Snapshots</td>
      <td style="white-space: nowrap;" bgcolor="#ffff9c">submitted by</td>
    </tr>
    <tr>
      <td width="50%">Multigrid discrete volume generator with
signature (area, curvature...) based on <font color="green">Libvol</font>
      </td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C++</td>
      <td><a href="http://liris.cnrs.fr/david.coeurjolly/Code/SimpleVol/">go</a></td>
      <td>see <a href="3D_images.html">3D data set web page</a></td>
      <td style="white-space: nowrap;">
      <a href="mailto:dcoeurjo@liris.cnrs.fr">D.Coeurjolly</a></td>
    </tr>
    <tr>
      <td width="50%">Discrete Straight Line Segmentation based on
Debled and Reveill&egrave;s DSL </td>
      <td style="text-align: left; vertical-align: middle;" width="30%">
      <a href="Bibliography/tc18_bib.html#DEBL_1995">[DEBLED 1995]</a></td>
      <td>C</td>
      <td><a href="Code/DSS/debled.c">debled.c</a></td>
      <td><a href="Code/DSS/cercle_segmentation.gif"><img
 src="Code/DSS/cercle_segmentation.gif" width="100"></a></td>
      <td style="white-space: nowrap;"><a
 href="mailto:dcoeurjo@liris.cnrs.fr">D.Coeurjolly</a>
      </td>
    </tr>
    <tr>
      <td width="50%">Digital plane recognition and digital plane
preimage computation </td>
      <td style="text-align: left; vertical-align: middle;" width="30%">---</td>
      <td>C</td>
      <td><a href="Code/Preimage/preimage.html">go</a></td>
      <td><a href="Code/Preimage/poly_axis.gif"><img
 src="Code/Preimage/poly_axis.gif" width="100"></a></td>
      <td style="white-space: nowrap;"><a
 href="mailto:dcoeurjo@liris.cnrs.fr">D.Coeurjolly</a></td>
    </tr>
    <tr>
      <td style="text-align: left; vertical-align: middle;">Fast
Digital Plane Recognition<br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="Bibliography/tc18_bib.html#GERARD_2003">[GERARD 2003]</a></td>
      <td style="text-align: left; vertical-align: middle;">C<br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="http://webloria.loria.fr/%7Edebled/plane/index.html">go</a><br>
      </td>
      <td style="text-align: left; vertical-align: middle;">&nbsp; </td>
      <td
 style="text-align: left; vertical-align: middle; white-space: nowrap;">Y.
Gerard, <br>
I. Debled-Rennesson,<br>
P. Zimmermann </td>
    </tr>
  </tbody>
</table>

<h2><a name="objects"> Discrete distances and skeletons </a></h2>
<a name="distances">
</a><a name="disctances"><br>
</a>
<table style="width: 90%;" border="1">
  <tbody>
    <tr>
      <td bgcolor="#ffff9c">Description</td>
      <td style="text-align: left; vertical-align: middle;"
 bgcolor="#ffff9c" width="50%">Main Reference</td>
      <td bgcolor="#ffff9c"> Language </td>
      <td bgcolor="#ffff9c">Download</td>
      <td bgcolor="#ffff9c">Snapshots</td>
      <td style="white-space: nowrap;" bgcolor="#ffff9c">submitted by</td>
    </tr>
 <tr>
      <td     style="text-align:        left;          vertical-align:
      middle;">hLutChamfer3D is a two-phase  method that determines if
      a G-symmetrical chamfer mask  induces a norm  and in  this case,
      computes the  lookup tables and the  test neighborhood  based on
      geometric    properties  of     the    chamfer  balls.</td>  <td
      style="text-align:     left;     vertical-align:     middle;"><a
      href="Bibliography/tc18_bib.html#normand2008pr">[Normand_2008]</a></td>
      <td style="text-align: left; vertical-align: middle;">C<br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="http://www.irccyn.ec-nantes.fr/~normand/hLutChamfer3D/">go</a><br>
      </td>
      <td style="text-align: left; vertical-align: middle;">&nbsp; </td>
      <td
 style="text-align: left; vertical-align: middle; white-space: nowrap;"><a
 href="mailto:Nicolas.Normand@polytech.univ-nantes.fr">N. Normand</a></td>
    </tr>

<tr>
      <td colspan="1" rowspan="2"
 style="text-align: left; vertical-align: middle;">Program to
compute optimal coefficients of 3-D chamfer norms.<br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="Bibliography/tc18_bib.html#FOUARD_2005">[FOUARD 2005]</a></td>

      <td style="text-align: left; vertical-align: middle;">J<small>AVA</small><br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="Code/Fouard/">go</a><br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><br>
      </td>
      <td
 style="text-align: left; vertical-align: middle; white-space: nowrap;"><a
 href="mailto:Celine.Fouard@sophia.inria.fr">C. Fouard</a><br>

      <a href="mailto:Gregoire.Malandain@sophia.inria.fr">G. Malandain</a><br>
      </td>
    </tr>
    <tr>
      <td style="vertical-align: middle;"><a
 href="Bibliography/tc18_bib.html#FOUARD_2005">[FOUARD 2005]</a><br>
      <a href="Bibliography/tc18_bib.html#MALANDAIN_2005">[MALANDAIN
2005]</a><br>
      </td>

      <td style="vertical-align: middle;">C<br>
      </td>
      <td style="vertical-align: middle;"><a
 href="http://www-sop.inria.fr/epidaure/personnel/Gregoire.Malandain/codes/chamfer-coefficients.html">go</a><br>
      </td>
      <td style="vertical-align: middle;"><br>
      </td>
      <td style="vertical-align: middle;"><a
 href="mailto:Gregoire.Malandain@sophia.inria.fr">G. Malandain</a><br>

      <a href="mailto:Celine.Fouard@sophia.inria.fr">C. Fouard</a><br>
      </td>
    </tr>
    <tr>
      <td style="text-align: left; vertical-align: middle;">3D Squared
Euclidean Distance Transform in optimal time</td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="Bibliography/tc18_bib.html#SAITO_1994">[SAITO_1994]</a>, <a
 href="Bibliography/tc18_bib.html#HIRATA_1996">[HIRATA_1996]</a> and <a
 href="Bibliography/tc18_bib.html#MEIJSTER_2000">[MEIJSTER_2000]</a></td>
      <td style="text-align: left; vertical-align: middle;">C++<br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="Code/SEDT/index.html">go</a><br>
      </td>
      <td style="text-align: left; vertical-align: middle;">&nbsp; </td>
      <td
 style="text-align: left; vertical-align: middle; white-space: nowrap;"><a
 href="mailto:dcoeurjo@liris.cnrs.fr">D. Coeurjolly</a></td>
    </tr>
 <tr>
      <td style="text-align: left; vertical-align: middle;">3D
	    Discrete Voronoi diagram computation</td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="Bibliography/tc18_bib.html#SAITO_1994">[SAITO_1994]</a>, <a
 href="Bibliography/tc18_bib.html#HIRATA_1996">[HIRATA_1996]</a> and <a
 href="Bibliography/tc18_bib.html#MEIJSTER_2000">[MEIJSTER_2000]</a></td>
      <td style="text-align: left; vertical-align: middle;">C++<br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="Code/SEDTVoronoi/index.html">go</a><br>
      </td>
      <td style="text-align: left; vertical-align: middle;">&nbsp; </td>
      <td
 style="text-align: left; vertical-align: middle; white-space: nowrap;"><a
 href="mailto:dcoeurjo@liris.cnrs.fr">D. Coeurjolly</a> </td>
    </tr> </tbody>
</table>

<a name="features">
<font color="red"><b> See also:</b></font>
</a>
<ul>
  <a name="features"> <li> The Npic library </li>
  </a><a href="#grids">Grid, cells, structures and topology</a> section.
</ul>


<h2> Features extraction and similarity measures</h2>
<a name="features">
<font color="red"><b> See also:</b></font>
</a>
<ul>
  <a name="features"> <li> The multigrid discrete volume generator
with signature (area, curvature...) in the </li>
  </a><a href="#objects">nD objects and recognition</a> section.
</ul>
<h2> Miscellaneous</h2>
<a name="misc">
</a>
<table style="width: 80%;" border="1">
  <tbody>
    <tr>
      <td bgcolor="#ffff9c" width="50%">Description</td>
      <td style="text-align: left; vertical-align: middle;"
 bgcolor="#ffff9c" width="50%">Main Reference</td>
      <td bgcolor="#ffff9c"> Language </td>
      <td bgcolor="#ffff9c">Download</td>
      <td bgcolor="#ffff9c">Snapshots</td>
      <td style="white-space: nowrap;" bgcolor="#ffff9c">submitted by</td>
    </tr>
    <tr>
      <td style="text-align: left; vertical-align: middle;">QVox: A 3d
volumetric data visualizer with some homotopic thinning algorithms.<br>
      </td>
      <td style="text-align: left; vertical-align: middle;">&nbsp;</td>
      <td style="text-align: left; vertical-align: middle;">C<br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><a
 href="http://qvox.sourceforge.net/">go</a><br>
      </td>
      <td style="text-align: left; vertical-align: middle;"><img
 src="Code/Fourey/mvox_skull.gif" width="100"> </td>
      <td
 style="text-align: left; vertical-align: middle; white-space: nowrap;"><a
 href="mailto:Sebastien.Fourey@greyc.ensicaen.fr">S. Fourey</a> </td>
    </tr>
  </tbody>
</table>
<a name="misc"><br>
<i>Go to </i></a><i><a href="data_code.html">Data sets main page</a>
or <a href="http://www.cb.uu.se/%7Etc18/">TC18 main page</a>
<br>
<br>
<i>To submit a code, see the <a href="data_code.html">Data sets main
page</a></i>
<br>
</i>
<?php include("/home-projets/tc18/public_html/footer.html") ?>

