
$(document).on("ready", function () {
    cargando(getContexto());
});

function reloadCaptcha(){
    cargando(getContexto());
}
function cargando(contexto) {   
    var data = getData(contexto);  
    return;
}
function getContexto(){
    var contexto  =  $('#contexto').val();
    return contexto;
}


function getData(contexto) {
    
    var nombreServicio = "/captchaReload";
    var urlContextualServicio = "/" +  contexto + nombreServicio;    

    
    $.ajax( {
        type : "POST", 
        url : urlContextualServicio, 
        dataType: "text",
        cache: false,
        success: function (data) {
            $('#captchaSession').attr('src', "data:image/png;base64," + data);
			try{
				var elements = document.getElementsByName("captchaSession");
				for(var i=0; i<elements.length; i++) {
					elements[i].src =  "data:image/png;base64," + data;
				}
			}catch(error){}
        },
        error: function (){
            console.log('error');
        }
        
    }); 
}
