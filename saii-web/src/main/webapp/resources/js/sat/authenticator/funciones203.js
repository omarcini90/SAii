var isDHTML = 0;
var isID = 0;
var isAll = 0;
var isLayers = 0;
var req;
var which;
var urport;

    function cambio(){
		toggleVisibility('forma');
		toggleVisibility('error');
    };
	
	function inicio(){
		setVisibility('error','hidden');
		setVisibility('forma','visible');
	};

  function checkRFC(url,urlp){
	urport = urlp;
    var rfc = document.getElementById('rfc').value;
    var password = document.getElementById('Ecom_Password').value;
    var enye = '\321';
    var regexp = new RegExp("^[A-Z" + enye + "&]{3,4}[0-9]{6}[A-Z0-9" + enye + "]{3}$");
    var regexpPass = new RegExp("[A-Z0-9a-z]{8}");
    
    //var boolean = rfc.indexOf('~');
    if(!rfc.match(regexp)||password.length!=8||!password.match(regexpPass)){
    	document.getElementById('iniciaSesion').disabled = false;
	    document.getElementById('rfc').disabled = false; //M
	   	document.getElementById('Ecom_Password').disabled = false; //M
	   	document.getElementById('rfc').value='';
	   	document.getElementById('Ecom_Password').value='';
	   	cambio();
	   	if(!rfc.match(regexp)){
	   		if(rfc==null||rfc.length==0){
	    		document.getElementById('errorText').innerHTML = 'Es necesario proporcionar el RFC';
	    	}else{
	    		document.getElementById('errorText').innerHTML = 'El RFC que introdujo no es v&aacute;lido';
	    	}
	    }else {
	    	if(password==null||password.length==0){
	    		document.getElementById('errorText').innerHTML = 'Es necesario proporcionar su clave';
	    	}else if(password.length!=8){
	    		document.getElementById('errorText').innerHTML = 'Su clave debe ser de 8 caracteres';
	    	}else{
	    		document.getElementById('errorText').innerHTML = 'La clave que introdujo no es v&aacute;lida';
	    	}
	    }
    }else{
    	retrieveURL(url);
    }
   }
  
  function retrieveURL(url) {
  	document.getElementById('iniciaSesion').disabled = true;
  	var rfc = document.getElementById('rfc').value.replace(/&/g, "~");
  	var password = document.getElementById('Ecom_Password').value;
  	var params='rfc='+rfc+'&password='+password;
    if (window.XMLHttpRequest) { // Non-IE browsers
      req = new XMLHttpRequest();
      req.onreadystatechange = processStateChange;
      try {
        req.open("POST", url, true);
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		req.setRequestHeader("Content-length", params.length);
		req.setRequestHeader("Connection", "close");
      } catch (e) {
        alert(e);
      }
      req.send(params);
    } else if (window.ActiveXObject) { // IE
      req = new ActiveXObject("Microsoft.XMLHTTP");
      if (req) {
        req.onreadystatechange = processStateChange;
        req.open("POST", url, true);
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		req.setRequestHeader("Content-length", params.length);
		req.setRequestHeader("Connection", "close");
        req.send(params);
      }
    }
  }
  
  
  function processStateChange() {
    if (req.readyState == 4) { // Complete
      if (req.status == 200) { // OK response
      	var temp = req.responseText;
      	temp = temp.substring(2, temp.length);
      	var array = temp.split("|",2);
      	var url;
      	var error;
      	if(array[0]==null||array[0]==''){
			url = '';	
      	}else{
      		url = array[0];
      	}
      	
      	if(array[1]==null||array[1]==''){
      		error = '';
      	}else{
      		error = array[1];
      	}
      	
	    if(error==null||error==''){
	    	if(url==urport){
	    		var usernametmp = document.getElementById('rfc').value.replace(/&/g, "_");
	    		document.getElementById('Ecom_User_ID').value = usernametmp;
	    		document.login.getAttributeNode('target').value='_top';
      			document.login.action=url;
      			document.login.submit();
      		}else if(url!=null&&url!=''){
      			var w = 640+32;
		  		var h = 310+96;
		  		wleft = (screen.width - w) / 2;
		  		wtop = (screen.height - h) / 2;
				window.open(url,null,'width='+w+',height='+h+',left='+wleft+',top='+wtop+',screenX=0,screenY=100');
      		}
	    }else{
	    	document.getElementById('iniciaSesion').disabled = false;
	    	document.getElementById('rfc').disabled = false; //M
	    	document.getElementById('Ecom_Password').disabled = false; //M
	    	document.getElementById('rfc').value='';
	    	document.getElementById('Ecom_Password').value='';
	    	cambio();
	    	document.getElementById('errorText').innerHTML = error;
	    }
      } else {
        alert("Problem: " + req.statusText);
      }
    }
  }


if (document.getElementById) {isID = 1; isDHTML = 1;}
else {
  if (document.all) {isAll = 1; isDHTML = 1;}
  else {
       browserVersion = parseInt(navigator.appVersion);
  if ((navigator.appName.indexOf('Netscape') != -1) && (browserVersion == 4)) {isLayers = 1; isDHTML = 1;}
}}

function findDOM(objectID,withStyle) {
	if (withStyle == 1) {
		if (isID) { return (document.getElementById(objectID).style) ; }
		else { 
			if (isAll) { return (document.all[objectID].style); }
		else {
			if (isLayers) { return (document.layers[objectID]); }
		};}
	}
	else {
		if (isID) { return (document.getElementById(objectID)) ; }
		else { 
			if (isAll) { return (document.all[objectID]); }
		else {
			if (isLayers) { return (document.layers[objectID]); 
			}
		};}
   }
}

function toggleVisibility(objectID) {
  var dom = findDOM(objectID,1);
  state = dom.visibility;
  if (state == 'hidden' || state == 'hide' )
    dom.visibility = 'visible';
  else if (state == 'visible' || state=='show') 
    dom.visibility = 'hidden';	 
  else 
	dom.visibility = 'visible';
}

function setVisibility(objectID,state) {
  var dom = findDOM(objectID,1);
  dom.visibility = state;
}
 
