
<?php include("header.html") ?>
    
    <div id="site_content">
      <div id="sidebar_container">
        <div class="gallery">
          <ul class="images">
            <li class="show"><img width="450" src="http://www.tc18.org/images/general/neige4.jpg" alt="photo_one" /></li>
            <li><img width="450" src="http://www.tc18.org//images/general/neige3.jpg" alt="photo_two" /></li>
            <li><img width="450" src="http://www.tc18.org/images/general/foam_final.jpg" alt="photo_three" /></li>
            <li><img width="450" src="http://www.tc18.org/images/general/illustration_dgci2009_dupas.jpg" alt="photo_four" /></li>
            <li><img width="450" src="http://www.tc18.org/images/general/neptune_final.jpg" alt="photo_five" /></li>
          </ul> 
        </div>
      </div>
      


      <div id="content">
        <h1>Contact</h1>
        
	<FORM NAME="mail" ACTION="mailto:tc18@tc18.org?subject=Contact from TC18 website" METHOD="post" ENCTYPE ="text/plain" >
	  
	  <!--<INPUT TYPE = "hidden" NAME = "info"> -->
	  <table>
	    <tr>
	      <td>
		name
	      </td>
	      <td>
		<INPUT type="text" name="name" size="40"> 
	      </td>
	    </tr>
	    <tr>
	      <td>
		e-mail address
	      </td>
	      <td>
		<INPUT type="text" name="email" size="40"> 
	      </td>
	    </tr>
	    
	  </table>
	  
	  <textarea rows="8" cols="60" name="comments"></textarea><br />
	  
	  <input type="submit" value="Send" />
	  
	  <INPUT type="reset">
	  
	</form>
      </div>
      

<?php include("footer.html") ?>
