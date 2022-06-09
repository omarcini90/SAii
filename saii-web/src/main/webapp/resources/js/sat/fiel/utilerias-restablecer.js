function limpiaRFC() {
    try {
        $("input[id$='txtvalidaRFC']").val("");
    } catch (e) {
    }
}

function validaMoral(str_rfc_current) {
    var rfc_pattern_pm = "^([A-Z\u00d1\u0026]{1}[A-Z\u00d1\u0026]{1}[A-Z\u00d1\u0026]{1}[0-9]{6}[A-Z0-9]{3})$";
    if (str_rfc_current.match(rfc_pattern_pm)) {
        return true;
    } else {
        return false;
    }
}

function validaRFC() {
    try {
        var rfc = $('#txtvalidaRFC').val();
        try {
            if (validaMoral(rfc)) {
                $('#div_rfc_invalido').css('display', 'block');
                $('#span_msg_rfc_error').text('El servicio es exclusivo para personas físicas.');
                $('#txtvalidaRFC').val("");
                return false;
            } else {
                $('#div_rfc_invalido').css('display', 'none');
            }
        } catch (e) {
            console.log("Ocurrió un error general. Intentes más tarde.", e);
        }

        var rfcValido = isRFC(rfc);
        if (rfcValido) {
            resetErrorsValidateRfc();
            $("input[id$='txtSubject']").val(rfc);
            $("button[id$='sbmConsulta']").click();

            statusDialogRFC.show();
        }
        if (rfcValido != true) {
            showErrorsValidateRfc();
            $('#txtvalidaRFC').val("");
        }
    } catch (e) {
        alert(e);
    }
}

function cancelar() {
    try {
        $("button[id$='sbmCancelar']").click()
    } catch (e) {
    }
}

function limpiaContrasenia() {
    try {
        $("input[id$='v_newPwd']").value("");
        $("input[id$='v_newPwdC']").value("");
    } catch (e) {
        printOutput('Error');
    }
}

function limpiaContraseniaMail() {
    try {
        $("input[id$='rc_contrasenia_nueva']").value("");
        $("input[id$='rc_contrasenia_confirmacion']").value("");
    } catch (e) {
        printOutput('Error');
    }
}

function enviaConsultaIDC() {

}

function showErrorsValidateRfc() {
    try {
        $('#div_rfc_invalido').css('display', 'block');
        $('#span_msg_rfc_error').text('El formato del RFC no es correcto');

    } catch (er) {
    }
}


function resetErrorsValidateRfc() {
    try {

        $('#div_rfc_invalido').css('display', 'none');
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }

}

function resetEMessages() {
    $('#div_rfc_invalido').css('display', 'none');
    $("#div_fiel_resultado_invalido").css('display', 'none');
}


function validaLongPwd(pwc,event){       
               
        var e= event|| window.event;                    
        var charCode = (e.which) ? e.which : e.keyCode;
                                    
        if (!(charCode == 0 || charCode == 8 || charCode == 9 || charCode == 13)) {
             var valor_actual_pass = pwc.value;
           if (valor_actual_pass.length >= 8) {
                alertaContrasenaDesbordadaWV.show();
                var alertPwOverflow = document.getElementById('alertPwOverflow:alertPwOverflowForm:alertPwOverflowButtom');
                setTimeout(function(){alertPwOverflow.focus();}, 100);
                //alert('La contraseña debe ser de 8 caracteres, contener números y/o letras, sin espacios y sin caracteres especiales.');
           }                    
        }
 }
                                
                                
function validaLongPwdMail(pwcM,event){       
               
        var e= event || window.event;                    
        var charCode = (e.which) ? e.which : e.keyCode;
                                    
        if (!(charCode == 0 || charCode == 8 || charCode == 9 || charCode == 13)) {
             var valor_actual_pass = pwcM.value;
           if (valor_actual_pass.length >= 8) {
                alertaContrasenaDesbordadaWV.show();
                var alertPwOverflow = document.getElementById('alertPwOverflow:alertPwOverflowForm:alertPwOverflowButtom');
                setTimeout(function(){alertPwOverflow.focus();}, 100);
                //alert('La contraseña debe ser de 8 caracteres, contener números y/o letras, sin espacios y sin caracteres especiales.');
           }                    
        }
 }                
                                            
                
function validaLongPwdC(pwdC,event){
        var e= event || window.event;                    
        var charCode = (e.which) ? e.which : e.keyCode;
                                    
        if (!(charCode == 0 || charCode == 8 || charCode == 9 || charCode == 13)) {
          var valor_actual_pass = pwdC.value;
          if (valor_actual_pass.length >= 8) {
                alertaContrasenaDesbordadaWV.show();
                var alertPwOverflow = document.getElementById('alertPwOverflow:alertPwOverflowForm:alertPwOverflowButtom');
                setTimeout(function(){alertPwOverflow.focus();}, 100);
                //alert('La contraseña debe ser de 8 caracteres, contener números y/o letras, sin espacios y sin caracteres especiales.');
          }
        }
}

function validaLongPwdCMail(pwdCM,event){
        var e= event || window.event;                    
        var charCode = (e.which) ? e.which : e.keyCode;
                                    
        if (!(charCode == 0 || charCode == 8 || charCode == 9 || charCode == 13)) {
          var valor_actual_pass = pwdCM.value;
          if (valor_actual_pass.length >= 8) {
                alertaContrasenaDesbordadaWV.show();
                var alertPwOverflow = document.getElementById('alertPwOverflow:alertPwOverflowForm:alertPwOverflowButtom');
                setTimeout(function(){alertPwOverflow.focus();}, 100);
                //alert('La contraseña debe ser de 8 caracteres, contener números y/o letras, sin espacios y sin caracteres especiales.');
          }
        }
}




function validarEnter() {
    v_rfc.onkeypress = function (e) {
        if (e.which == 13) {

            try {
                if (validaMoral(v_rfc.val())) {
                    $('#div_rfc_invalido').css('display', 'block');
                    $('#span_msg_rfc_error').text('El servicio es exclusivo para personas físicas.');
                    $('#txtvalidaRFC').val("");
                    return false;
                } else {
                    $('#div_rfc_invalido').css('display', 'none');
                }
            } catch (e) {
                alert("Error ", e);
            }

            var de = isRFC(v_rfc.val());
            if (!de) {
                showErrorsValidateRfc();
                v_rfc.val("");
                e.preventDefault();
            } else {
                e.preventDefault();
                resetErrorsValidateRfc();
                $("input[id$='txtSubject']").val(v_rfc.val());
                $("button[id$='sbmConsulta']").click();
                statusDialogRFC.show();
            }
        }
    }
}

function handleChange(panel) {
    try{
    event.stopPropagation();
    }catch(er){}
    
    
    resetEMessages();
}

function executeValidate(){
    $("button[id$='button_captcha_01']" ).click();    
}


   