var documentosSAT = [];
var llavePrivadaGlobal;
var cadenaReporteFirmado = '';
var catalogoErrores = [];


//**************** CATALOGO DE ERRORES QUE INTERACTUAN CON LA LIBRERIA FIEL Y LA VISTA *******************************//
catalogoErrores[11] = {
    msg_vista: 'Debe seleccionar la llave privada de su firma electr\u00F3nica.', mensaje: 'No se ha pasado un valor para filePrivateKey para llave privada', accion: function (parametro1) {
        showErrorsPrivateKey(parametro1);
    }
};
catalogoErrores[12] = {
    msg_vista: 'Debe seleccionar la llave privada de su firma electr\u00F3nica.', mensaje: 'No se ha pasado un input del tipo file para llave privada', accion: function (parametro1) {
        showErrorsPrivateKey(parametro1);
    }
};
catalogoErrores[13] = {
    msg_vista: 'La contrase\u00F1a de la clave privada no es correcta.', mensaje: 'No se ha pasado un valor para contraseña', accion: function (parametro1) {
        showErrorsPassword(parametro1);
    }
};
catalogoErrores[14] = {
    msg_vista: 'La cadena de entrada para firmar no es v\u00e1lida.', mensaje: 'No se ha pasado un valor para la cadena a firmar', accion: function (parametro1) {
        // Programar acción a ejecutar
    }
};
catalogoErrores[15] = {
    msg_vista: 'Debe seleccionar el certificado de su firma electr\u00F3nica.', mensaje: 'No se ha pasado un valor para fileCertificado para Certificado', accion: function (parametro1) {
        showErrorsCertificate(parametro1);
    }
};
catalogoErrores[16] = {
    msg_vista: 'Debe seleccionar el certificado de su firma electr\u00F3nica.', mensaje: 'No se ha pasado un input del tipo file para Certificado', accion: function (parametro1) {
        showErrorsCertificate(parametro1);
    }
};

catalogoErrores[21] = {
    msg_vista: 'Debe seleccionar la llave privada de su firma electr\u00F3nica.', mensaje: 'No se ha seleccionado un archivo de llave privada', accion: function (parametro1) {
        // Programar la acción a ejecutar.
    }
};
catalogoErrores[22] = {
    msg_vista: 'La contrase\u00F1a de la clave privada no es correcta.', mensaje: 'La contraseña es vacía', accion: function (parametro1) {
        showErrorsPassword(parametro1);
    }
};
catalogoErrores[23] = {
    msg_vista: 'Introduzca una cadena para firmar.', mensaje: 'La cadena a firmar es vacía', accion: function (parametro1) {
        // Programar la acción a ejecutar
    }
};
catalogoErrores[24] = {
    msg_vista: 'El archivo de la llave privada no es v\u00E1lido.', mensaje: 'El archivo no es una llave privada', accion: function (parametro1) {
        showErrorsPrivateKey(parametro1);
    }
};
catalogoErrores[25] = {
    msg_vista: 'La contrase\u00F1a de la clave privada no es correcta.', mensaje: 'La contraseña es incorrecta', accion: function (parametro1) {
        showErrorsPassword(parametro1);
    }
};
catalogoErrores[26] = {
    msg_vista: 'Debe seleccionar el certificado de su firma electr\u00F3nica.', mensaje: 'No se ha seleccionado un archivo de Certificado', accion: function (parametro1) {
        showErrorsCertificate(parametro1);
    }
};
catalogoErrores[27] = {
    msg_vista: 'El archivo del certificado no es v\u00E1lido.', mensaje: 'El archivo no es un Certificado', accion: function (parametro1) {
        showErrorsCertificate(parametro1);
    }
};
catalogoErrores[28] = {
    msg_vista: 'El certificado no corresponde con la llave privada.', mensaje: 'El certificado no corresponde con la llave privada', accion: function (parametro1) {
        showErrors(parametro1);
    }
};

catalogoErrores[41] = {
    msg_vista: 'No se pudo leer el certificado. Error general.', mensaje: 'Error de programa. No se pudo ller el tipo de certificado.', accion: function (parametro1) {
        showErrors(parametro1);
    }
};

catalogoErrores[50] = {
    msg_vista: 'El certificado se encuentra revocado.', mensaje: 'El certificado no se encuentra activo.', accion: function (parametro1) {
        showErrors(parametro1);
    }
};

catalogoErrores[60] = {
    msg_vista: 'El usuario no se encuentra en el directorio institucional.', mensaje: 'Error al momento de validar estatus del usuario con LDAP.', accion: function (parametro1) {
        showErrors(parametro1);
    }
};

// Mensajes que agrega el applet
catalogoErrores[70] = {
    msg_vista: 'Ingrese un RFC v\u00E1lido.', mensaje: 'El campo RFC no es válido.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[71] = {
    msg_vista: 'Certificado no emitido por el SAT. Archivo inv\u00e1lido', mensaje: 'Certificado no emitido por el SAT: Archivo inv\u00e1lido', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[72] = {
    msg_vista: 'Certificado no emitido por el SAT: Debe usar un certificado de FIEL', mensaje: 'Certificado no emitido por el SAT: Debe usar un certificado de FIEL', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[73] = {
    msg_vista: 'Certificado no emitido por el SAT:', mensaje: 'Certificado no emitido por el SAT:', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[74] = {
    msg_vista: 'El certificado no contiene un RFC', mensaje: 'El certificado no contiene un RFC', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[75] = {
    msg_vista: 'El RFC del certificado no coincide con el RFC de la sesi\u00f3n', mensaje: 'El RFC del certificado no coincide con el RFC de la sesi\u00f3n', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[76] = {
    msg_vista: 'El certificado no corresponde con la clave privada.', mensaje: 'El certificado no corresponde con la clave privada.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

// mensajes js - applet

catalogoErrores[77] = {
    msg_vista: 'Certificado, clave privada o contrase\u00f1a de clave privada inv\u00e1lidos, int\u00e9ntelo nuevamente.', mensaje: 'Certificado, clave privada o contrase\u00f1a de clave privada inv\u00e1lidos, int\u00e9ntelo nuevamente.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[78] = {
    msg_vista: 'Certificado no emitido por el SAT.', mensaje: 'Certificado no emitido por el SAT.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[79] = {
    msg_vista: 'Clave privada inv\u00e1lida.', mensaje: 'Clave privada inv\u00e1lida.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[80] = {
    msg_vista: 'Clave privada o contrase\u00f1a de clave privada inv\u00e1lida.', mensaje: 'Clave privada o contrase\u00f1a de clave privada inv\u00e1lida.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[81] = {
    msg_vista: 'Su navegador no tiene Java instalado. Esta aplicaci\u00f3n no funcionar\u00e1.', mensaje: 'Su navegador no tiene Java instalado. Esta aplicaci\u00f3n no funcionar\u00e1.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};

catalogoErrores[82] = {
    msg_vista: 'Los datos introducidos no corresponden, favor de verificar su contrase\u00f1a de clave privada, clave privada y/o certificado nuevamente.', mensaje: 'Los datos introducidos no corresponden, favor de verificar su contrase\u00f1a de clave privada, clave privada y/o certificado nuevamente.', accion: function (parametro1) {
        //showErrors(parametro1);
    }
};


//*********************** MAPEO DE ERRORES CON ACCIONES A EJECUTAR ***************************************************//
var mapaErrores = {
    getValue: function (numMsg) {
        var msgErrorUTF8 = catalogoErrores[numMsg].mensaje;
        if (document.characterSet.toUpperCase() === 'ISO-8859-1' || //IE y Chrome
                document.characterSet.toUpperCase() === 'WINDOWS-1252') {
            //Firefox
            var msgErrorISO = decodeURIComponent(escape(msgErrorUTF8));
            return msgErrorISO;
        }
        else {
            //if (document.characterSet.toUpperCase() === 'UTF-8') {
            return msgErrorUTF8;
        }
    },
    execute: function (numMsg) {
        catalogoErrores[numMsg].accion(catalogoErrores[numMsg].msg_vista);
    }
};


/**
 * Setea en el input el rfc del certificado(FIEL)
 * @param 
 * @returns
 */
function setearRFC(sujeto) {
    try {
        ///var rfcSujeto = sujeto.split('/')[5].split('=')[1];
        $('#txtRFC').val(sujeto);
        try {
            $("input[id$='txtSubject']").val(sujeto);
        } catch (ex) {
            alert(ex);
        }
    }
    catch (e) {
        printOutput('No se pudo obtener RFC de usuario.');
    }
}


/**
 * Obiene el rfc a partir de un FIEL(input tipo File)
 * @param 
 * @returns
 */
function obtenerRFC(fileCertificado) {
    leerCertX509(fileCertificado, function (error_code, certificado) {
        if (certificado) {
            setearRFC(certificado.getRFC());
        }
    });
}

/**
 * Construye un objecto X509 a partir de un archivo .cer
 * @param 
 * @returns
 */
function leerCertX509(fileCertificado, callback) {
    var readerCertificado = new FileReader();
    readerCertificado.onload = function (e) {
        var certificado = new X509();
        console.info('Construyendo objeto x509 a partir del certificado');
        certificado.readCertPEM(e.target.result.split("base64,")[1]);
        console.info('RFC del objeto x509: ' , certificado.getRFC());
        console.info('Num. Serie del objeto x509: ' , certificado.getSerialNumberHex());
        callback(0, certificado);//Exito
    };//fin reader.onload
    readerCertificado.readAsDataURL(fileCertificado.files[0]);
}

function leerCertificado(fileCertificado, callback) {
    var readerCertificado = new FileReader();

    readerCertificado.onload = function (e) {

        //Convierte a un arreglo de enteros sin signo
        var bytes = new Uint8Array(readerCertificado.result);

        var binary = "";
        for (var i = 0; i < bytes.byteLength; i++) {
            //Convierte un numero Unicode a su correspondiente caracter
            binary += String.fromCharCode(bytes[i]);
        }

        //Se convierte binario a hex
        var hex = rstrtohex(binary);

        //Convierte el certificado DER (en hexadecimal) y la convierte a PEM
        var pemString = KJUR.asn1.ASN1Util.getPEMStringFromHex(hex, 'CERTIFICATE');

        var certificado = new X509();
        certificado.readCertPEM(pemString);

        var modulusCertificado = certificado.subjectPublicKeyRSA.n;

        if (typeof (modulusCertificado) === 'undefined') {
            callback(27); //Error (usuario): El archivo no es una Certificado
            return;
        }
        callback(0, certificado);//Exito
    };//fin reader.onload
    readerCertificado.readAsArrayBuffer(fileCertificado.files[0]);
}

// functiones que trabajan con campos de fiel.jsf
function mostrarMensajeJSFExitoValFIEL() {
    try {
        $('#div_fiel_resultado_valido').css('display', 'block');
        $('#div_fiel_resultado_invalido').css('display', 'none');
        $('#span_msg_exito').text('Estado del Certificado: Activo');
    } catch (er) {
        alert('Ocurrió un error el momento de mostrar mensaje en JSF FIEL.');
    }
}

// functiones que trabajan con campos de fiel.jsf
function mostrarMensajeJSFErrorValFIEL() {
    try {
        $('#div_fiel_resultado_invalido').css('display', 'block');
        $('#div_fiel_resultado_valido').css('display', 'none');
        $('#span_msg_error').text('Su certificado debe ser Renovado, por lo que puede acudir a la Administración Desconcentrada de Servicios al Contribuyente de su elección con previa cita.');
    } catch (er) {
        alert('Ocurrió un error el momento de mostrar mensaje en JSF FIEL.');
    }
}


// functiones que trabajan con campos de fiel.jsf
function inicializarMensajeJSFErrorExitoValFIEL() {
    try {
        $('#div_fiel_resultado_invalido').css('display', 'none');
        $('#div_fiel_resultado_valido').css('display', 'none');
    } catch (er) {
        alert('Ocurrió un error el momento de inicializar mensajes en JSF FIEL.');
    }
}

//// Funciones comunes
function toHex(str) {
    var result = '';
    for (var i = 0; i < str.length; i++) {
        result += str.charCodeAt(i).toString(16);
    }
    return result;
}

function h2d(h) {
    return parseInt(h, 16);
}

function hexToString(tmp) {
    var arr = tmp.split(' '), str = '', i = 0, arr_len = arr.length, c;

    for (; i < arr_len; i += 1) {
        c = String.fromCharCode(h2d(arr[i]));
        str += c;
    }
    return str;
}


//*********************** FUNCIONES PARA OBTENER RFC DEL CERTIFICADO *************************************************//
function buildRFCFromHex(subject_hex_cert) {
    try {


        //Convierte a un arreglo de enteros sin signo
        //var tdfd = encodeURIComponent( hex);

        var rfc_current = convertRFCHexToString(subject_hex_cert);



        var rfc_str = readHexToStringRFC(rfc_current);
        var rfc_contribuyente = verifyCaseRFC(rfc_str);

        setearRFC(rfc_contribuyente);

        return rfc_contribuyente;
    }
    catch (er2) {
        alert('No se pudo leer el RFC.');
    }
    return 'undefined';
}

function ab2str(buf) {
    return String.fromCharCode.apply(null, new Uint16Array(buf));
}

function str2ab(str) {
    var buf = new ArrayBuffer(str.length * 2); // 2 bytes for each char
    var bufView = new Uint16Array(buf);
    for (var i = 0, strLen = str.length; i < strLen; i++) {
        bufView[i] = str.charCodeAt(i);
    }
    return buf;
}

function verifyCaseRFC(rfc_str) {
    var aucx_f = rfc_str.substring(16, 25);
    var aucx_m = rfc_str.substring(15, 24);

    if (rfc_str.indexOf('/') > -1) {
        var res = rfc_str.substring(0, rfc_str.lastIndexOf("/"));

        return res.replace(/(\r\n|\n|\r)/gm, '').trim();
    }

    if (aucx_m.indexOf("0") == 1) {

        return processRFCMoral(rfc_str);
    }

    if (aucx_f.indexOf("0") == 1) {

        return processRFCFisica(rfc_str);
    }
    alert('No se pudo leer el RFC de la vista.');
}

function convertRFCHexToString(subject_hex_cert) {
    //return hextorstr(subject_hex_cert);
    return hextorstr(subject_hex_cert);
}

function readHexToStringRFC(rfc_current) {
    return rfc_current.split('-')[1];
}

function processRFCMoral(cad_read_subject_rfc) {
    try {
        return cad_read_subject_rfc.substring(1, 14).replace(/(\r\n|\n|\r)/gm, '').trim();
    }
    catch (er) {
        alert('Error al momento de procesar RRFC Moral.');
    }
    return 'undefined';
}

function processRFCFisica(cad_read_subject_rfc) {
    try {
        return cad_read_subject_rfc.substring(1, 15).replace(/(\r\n|\n|\r)/gm, '').trim();
    }
    catch (er) {
        alert('Error al momento de procesar RRFC Fisico.');
    }
    return 'undefined';
}

//*********************** FUNCIONES OBETENER VIGENCIA DEL CERTIFICADO ***********************************************//
function mostrarVigenciaCert(before_time_cert, after_time_cert) {
    try {
        var date_not_before = getDateFromUTCTime(before_time_cert);
        var date_not_after = getDateFromUTCTime(after_time_cert);
        var msg_vig = 'Vigente desde ' + date_not_before.dia + '/' + date_not_before.mes + '/' + date_not_before.anio + ' hasta ' + date_not_after.dia + '/' + date_not_after.mes + '/' + date_not_after.anio;
        $('#span_date').text(msg_vig);
    }
    catch (ed) {
        alert('No se pudo obtener la vigencia.');
    }
}


function getDateFromUTCTime(strTimeUTC) {
    var anio_cert, mes_cert, dia_cert;

    if (Number(strTimeUTC.substring(0, 2)) < 50) {
        //le agrego 20 al inicio
        anio_cert = '20' + strTimeUTC.substring(0, 2);
        mes_cert = strTimeUTC.substring(2, 4);
        dia_cert = strTimeUTC.substring(4, 6);
    }
    else if (Number(strTimeUTC.substring(0, 2)) >= 50) {
        // le agrego 19 al inicio
        anio_cert = '19' + strTimeUTC.substring(0, 2);
        mes_cert = strTimeUTC.substring(2, 4);
        dia_cert = strTimeUTC.substring(4, 6);
    }

    return {anio: anio_cert, mes: mes_cert, dia: dia_cert}
}


//***********************FUNCIONES QUE TRABAJAN CON CAMPOS DE FORMULARIO *********************************************//
function printOutput(msg01) {
    if (navigator.userAgent.indexOf("MSIE") > 0) {
        alert(msg01);
    } else {
        console.log(msg01);
    }
}

function resetShowProgressBarSI() {
    try {
        PF('statusDialog').hide();
    }
    catch (e) {
        printOutput('No existe progress bar : statusDialog !');
    }
}

function resetShowProgressBarSM() {
    try {
        statusDialog_mas.hide();
    }
    catch (e) {
        printOutput('No existe progress bar mass: statusDialog !');
    }
}

function showErrorsPrivateKey(msg_error) {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("lblErrorValidacionKey").innerHTML = msg_error;
         document.getElementById("lblErrorValidacionKey").style.visibility = "visible";
         document.getElementById("lblErrorValidacionKey").style.color = "red";*/
        mostrarMensajesJSFErrorGeneral(msg_error);
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

function showErrorsPassword(msg_error) {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("lblErrorValidacionPwd").innerHTML = msg_error;
         document.getElementById("lblErrorValidacionPwd").style.visibility = "visible";
         document.getElementById("lblErrorValidacionPwd").style.color = "red";*/
        mostrarMensajesJSFErrorGeneral(msg_error);
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

function showErrorsCertificate(msg_error) {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("lblErrorValidacion").innerHTML = msg_error;
         document.getElementById("lblErrorValidacion").style.visibility = "visible";
         document.getElementById("lblErrorValidacion").style.color = "red";*/
        mostrarMensajesJSFErrorGeneral(msg_error);
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

function showErrors(msg_error) {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("lblErrorValidacion").innerHTML = msg_error;
         document.getElementById("lblErrorValidacion").style.visibility = "visible";
         document.getElementById("lblErrorValidacion").style.color = "red";*/
        mostrarMensajesJSFErrorGeneral(msg_error);
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

function resetErrors() {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("lblErrorValidacion").style.visibility = "hidden";
         document.getElementById("lblErrorValidacion").innerHTML = "";*/

        resetMensajesJSFErrorGeneral();
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

function resetErrorsPrivateKey() {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("file_revocacions").style.borderColor = '#e2e2e2 1px solid';
         document.getElementById("lblErrorValidacionKey").innerHTML = "";
         document.getElementById("lblErrorValidacionKey").style.visibility = "hidden";*/

        resetMensajesJSFErrorGeneral();
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

function resetErrorsPassword() {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("pwdLlavePriv").style.borderColor = '#e2e2e2 1px solid';
         document.getElementById("lblErrorValidacionPwd").innerHTML = "";
         document.getElementById("lblErrorValidacionPwd").style.visibility = "hidden";*/

        resetMensajesJSFErrorGeneral();
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

function resetErrorsCertificate() {
    try {
        /** Requerimiento de negocio : Mostrar mensajes genéricos.
         document.getElementById("certificate").style.borderColor = '#e2e2e2 1px solid';
         document.getElementById("lblErrorValidacion").style.visibility = "hidden";*/
        resetMensajesJSFErrorGeneral();
    }
    catch (e) {
        printOutput('No se encontró el input html.');
    }
}

//Estado del Certificado: Activo
//Su certificado debe ser Renovado, por lo que puede acudir a la Administración Desconcentrada de Servicios al Contribuyente de su elección con previa cita.
function mostrarMensajesJSFErrorGeneral(strMensaje) {
    try {
        $('#div_fiel_resultado_invalido').css('display', 'block');
        $('#span_msg_error').text(strMensaje);

        $("#div_fiel_resultado_valido").css('display', 'none');
    } catch (ert) {
        alert('No se pudo mostrar mensaje error');
    }
}

function resetMensajesJSFErrorGeneral() {
    try {
        $("#div_fiel_resultado_invalido").css('display', 'none');
        $("#span_msg_error").text("");
    } catch (ert) {
        alert('No se pudo inicializar mensaje error');
    }
}

function mostrarMensajesJSFExitoGeneral(strMensaje) {
    try {
        $("#div_fiel_resultado_valido").css('display', 'block');
        $("#span_msg_exito").text(strMensaje);

        $("#div_fiel_resultado_invalido").css('display', 'none');
    } catch (ert) {
        alert('No se pudo mostrar mensaje error');
    }
}

function listenerEnterFIELJS() {
    document.getElementById('certificate').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitFIELJS();
        }
    }

    document.getElementById('privateKey').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitFIELJS();
        }
    }

    document.getElementById('pwdLlavePriv').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitFIELJS();
        }
    }

    document.getElementById('txtRFC').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitRFCJS();
        }
    }

    // cargado de captcha
    getDataPTSCAuth();

}

function getDataPTSCAuth() {
    var nombreServicio = "/captchaReload";
    var urlContextualServicio = "/" + "PTSC/auth" + nombreServicio;
    $.ajax({
        type: "POST",
        url: urlContextualServicio,
        dataType: "text",
        cache: false,
        success: function (data) {
            try {
                $('#captchaSession').attr('src', "data:image/png;base64," + data);
                
                var elements = document.getElementsByName("captchaSession");
		for(var i=0; i<elements.length; i++) {
                    elements[i].src =  "data:image/png;base64," + data;
                }
            } catch (e) {
            }
        },
        error: function () {
            //console.log('error');
        }
    });
}

function onclickButtonSubmitFIELJS() {
    document.getElementById("btnEnviarForm").click();
}
function onclickButtonSubmitRFCJS() {
    document.getElementById("btnValidaRFC").click();
}
function onclickButtonSubmitFIELApplet() {
    document.getElementById("btnEnviarForm").click();
}


function listenerEnterFIELApplet() {
    document.getElementById('certificateJ').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitFIELApplet();
        }
    }
    document.getElementById('privateKeyJ').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitFIELApplet();
        }
    }
    document.getElementById('privateKeyPasswordJ').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitFIELApplet();
        }
    }
    document.getElementById('rfcJ').onkeypress = function (e) {
        if (e.which == 13) {
            e.preventDefault();
            onclickButtonSubmitFIELApplet();
        }
    }
}