"use strict";

$(function() {
  var handleIn = function(){
    $(this).parent().siblings('i.fa.fa-play-circle-o.video-play').hide();
  };

  var handleOut = function(){
    $(this).parent().siblings('i.fa.fa-play-circle-o.video-play').show();
  };

  $('.home_menu').click(function() {
    var section = this.getAttribute("data-section");
    switch(section){
        case '.blog.section':
            window.history.pushState(null,null,window.location.pathname+'#blog');
            break;
        case '.press':
            window.history.pushState(null,null,window.location.pathname+'#prensa');
            break;
        case '.multimedia':
            window.history.pushState(null,null,window.location.pathname+'#multimedia');
            break;
        case '.reforms':
            window.history.pushState(null,null,window.location.pathname+'#reformas');
            break;
        case '.structure':
            window.history.pushState(null,null,window.location.pathname+'#estructura');
            break;
        case '.programs':
            window.history.pushState(null,null,window.location.pathname+'#acciones');
            break;
        case '.documents':
            window.history.pushState(null,null,window.location.pathname+'#documentos');
            break;
        case '.schedule':
            window.history.pushState(null,null,window.location.pathname+'#agenda');
            break;
    }
    $('#subenlaces').addClass('collapse');
    $('html,body').animate({
      scrollTop: $(section).offset().top - 100
    }, 500);
    return false;
  });


    // OPENDATA SHOW MORE
    $(".click_opendata").click(function(event){
        event.preventDefault();
        var counter=0;
        var lnk_visible=0;
        var lnk_len = $(".opendata_panel").length;
        $(".opendata_panel").each(function(index, element) {
          if (counter<3 && $(element).is(':hidden') ){
            counter++; $(element).show();
          }else{
            lnk_visible++;
          }
        });
        if (lnk_visible >= lnk_len){
          var current_org = window.location.pathname;
          window.location.href = "http://busca.datos.gob.mx/#/instituciones"+current_org;
        }
      });

  $('.gallery-overlay').hover(handleIn, handleOut);
  $('.most-recent-article').resizeAndCrop({
    center: true,
    width: 281,
    height: 178,
    forceResize: true
  });

  $('.featured-article').resizeAndCrop({
    center: true,
    width: 461,
    height: 266,
    forceResize: true
  });
});

//$('#pruebapop').bPopup('hide');

function enviaCorreo(org) {
    var valida = true;
    valida = valida && validaCampo( 'dNombre', $('#firstName').val().trim() ) ;
    valida = valida && validaCampo( 'dLast', $('#lastName').val().trim() ) ;
    valida = valida && validaMail(  'demail', $('#email').val().trim() );
    valida = valida && validaCampo( 'dTelefono', $('#phone').val().trim() );
    valida = valida && validaCampo( 'dState', $('#state').val().trim() );
    valida = valida && validaCampo( 'dCity', $('#city').val().trim() );
    valida = valida && validaCampo( 'dMun', $('#delmun').val().trim() );
    valida = valida && validaCampo( 'dAsunto', $('#asunto').val().trim() );
    valida = valida && validaCampo( 'dDesc', $('#descripcion').val().trim() );
    if (valida ) {
        $.ajax({
                url: org + '/contacto',
                type: 'GET',
                data: {
                    firstName: $('#firstName').val(),
                    lastName: $('#lastName').val(),
                    //secondLastName:   $('#secondLastName').val(),
                    email: $('#email').val(),
                    phone: $('#phone').val(),
                    state: $('#state').val(),
                    city: $('#city').val(),
                    delmun: $('#delmun').val(),
                    asunto: $('#asunto').val(),
                    descripcion: $('#descripcion').val()
                },
                success: function (data) {
                    $('#respuestaMail').html('<div class="alert alert-success" role="alert"> <strong>' + data.respuesta + '</strong></div>');
                },
                error: function() {
                    $('#respuestaMail').html('<div class="alert alert-success" role="alert"> <strong>Mensaje enviado</strong></div>');
                }
            }
        );
    }
}

function initialize() {
}

$(document).ready(function() {
  $(window).load(function() {
    var hash = location.hash.replace('#','');
    if(hash != '') {
      $('html, body').animate({ scrollTop: $('#contacto').offset().top}, 1000);
    }
  });
});

function validaCampo( campo, valor ) {
    if (valor.trim() == '') {
        $("#" + campo ).html(' <div class="alert alert-danger" role="alert"><strong>* Obligatorio</strong></div>')
        return false;
    } else {
        $("#" + campo ).html("")
        return true;
    }
}

function validaMail( campo, valor) {
    if ( valor.trim() != '') {
        var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
        if (!expr.test(valor)) {
            $("#" + campo).html(' <div class="alert alert-danger" role="alert"><strong>* Correo Invalido</strong></div>')
            return false
        }
    } else {
        $("#" + campo ).html(' <div class="alert alert-danger" role="alert"><strong>* Obligatorio</strong></div>')
        return false
    }
    $("#" + campo ).html("")
    return true
}
;
