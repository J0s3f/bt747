/**
 * starts a seperate window with the given url
 */
function startWindow(url, separate)
{
	if (separate == true)
	{
		window.open(url, "myLieu", "width=640,height=480,fullscreen=no,scrollbars=no,toolbar=no,status=no,resizable=no,menubar=no,location=no,directories=no");
	}
	else
	{
		top.location.href=url;
	}
}

/* tries to get the height of the brower-window
 */
function getWindowHeight() 
{
    var myHeight = 0;
    if( typeof( window.innerHeight ) == 'number' ) 
    {
        //Non-IE
        myHeight = window.innerHeight;
    } 
    else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) 
    {
        //IE 6+ in 'standards compliant mode'
        myHeight = document.documentElement.clientHeight;
    } 
    else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) 
    {
        //IE 4 compatible
        myHeight = document.body.clientHeight;
    }
     
    return myHeight;
}

/* tries to get the width of the brower-window
 */
function getWindowWidth() 
{
    var myWidth = 0;
    if( typeof( window.innerWidth ) == 'number' ) 
    {
        //Non-IE
        myWidth = window.innerWidth;
    } 
    else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) 
    {
        //IE 6+ in 'standards compliant mode'
        myWidth = document.documentElement.clientWidth;
    } 
    else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) 
    {
        //IE 4 compatible
        myWidth = document.body.clientHeight;
    }
     
    return myWidth;
}

/* tries to get the size of the brower-window
 */
function getWindowSize() 
{
    var myWidth = 0, myHeight = 0;
    if( typeof( window.innerWidth ) == 'number' ) 
    {
        //Non-IE
        myWidth = window.innerWidth;
        myHeight = window.innerHeight;
    } 
    else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) 
    {
        //IE 6+ in 'standards compliant mode'
        myWidth = document.documentElement.clientWidth;
        myHeight = document.documentElement.clientHeight;
    } 
    else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) 
    {
        //IE 4 compatible
        myWidth = document.body.clientWidth;
        myHeight = document.body.clientHeight;
    }
 
    return [ myWidth , myHeight ];
}


function resizeArea(myArea)
{
    var myContentArea = $(myArea);
    
    if (myContentArea != null)
    {
        var myNewHeight = getWindowHeight() + "px";
        myContentArea.style.height = myNewHeight;
        var myNewWidth = getWindowWidth() + "px";
        myContentArea.style.width = myNewWidth;
    }
}


function tFix(wert,ds)
{
       var wert=(wert.toFixed)?wert.toFixed(ds):
        Math.floor(wert)+"."+
	(Math.pow(10,ds)+Math.round((wert-Math.floor(wert))*
           Math.pow(10,ds))+"").substr(1,ds);
	return wert;
}

function setCookie(c_name,value,expiredays)
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate()+expiredays);
	document.cookie=c_name+ "=" +escape(value)+ ((expiredays==null) ? "" : ";expires="+exdate.toGMTString());
}

function getCookie(c_name)
{
	if (document.cookie.length > 0)
	{
		c_start=document.cookie.indexOf(c_name + "=");
		if (c_start != -1)
		{ 
			c_start=c_start + c_name.length+1; 
			c_end = document.cookie.indexOf(";",c_start);
			if (c_end == -1) 
				c_end=document.cookie.length;
    			return unescape(document.cookie.substring(c_start,c_end));
		} 
	}

	return "";
}