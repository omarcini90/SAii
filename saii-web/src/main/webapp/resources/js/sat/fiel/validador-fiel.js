//************************************ FIRMADO INDIVIDUAL ************************************************************//
/**
 * Método que se utiliza en el aplicativo para validar la FIEL con el ARA.
 *
 * @param llavePrivadaUser              El elemento input del tipo "file" de la llave privada.
 * @param passwordUser                  String con la contraseña de la llave privada.
 * @param cadenaOriginalAplicativo      String con la cadena a firmar
 * @param fileCertificado               FIEL que se desea validar, tipo cer(X509).
 * @returns
 */
function validarFiel(cadenaOriginalAplicativo, llavePrivadaUser, passwordUser, fileCertificado) {
    console.info('Validando el estado de la Fiel...');
    PKI.SAT.FielUtil.validaStatusFielX509(llavePrivadaUser, passwordUser, fileCertificado, function (error_code, certificado) {
        if (error_code === 0) {
            mostrarVigenciaCert(certificado.getNotBefore(), certificado.getNotAfter());
            leerCertX509(fileCertificado, function (error_code_1, certificado_1) {
                var rfcSubject = certificado_1.getRFC();
                signedIndividualCertificadoState1(rfcSubject, certificado.getSerialNumberHex(), '...', passwordUser, llavePrivadaUser);
                resetShowProgressBarSI();
            });
        } else {
            resetShowProgressBarSI();
            mapaErrores.execute(error_code);
            //alert('Ocurrió un error al validar la FIEL: ' + PKI.SAT.FielUtil.obtenMensajeError(error_code));
        }
    });
}