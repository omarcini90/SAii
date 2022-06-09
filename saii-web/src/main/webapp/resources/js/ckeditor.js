CKEDITOR.editorConfig = function( config ) {
        // Define changes to default configuration here:
        config.contentsCss = '/resources/css/fonts.css';
        //the next line add the new font to the combobox in CKEditor        
        config.font_names = 'SoberanaSans Regular/SoberanaSans-Regular;' + config.font_names;        
        config.font_names = 'SoberanaSans Bold/SoberanaSans-Bold;' + config.font_names;
        config.font_names = 'SoberanaSans Italic/SoberanaSans-Italic;' + config.font_names;
        config.font_names = 'SoberanaSans Light/SoberanaSans-Light;' + config.font_names;
        config.font_names = 'SoberanaSans Ultra/SoberanaSans-Ultra;' + config.font_names;
        config.font_names = 'Soberana Titular Regular/SoberanaTitular-Regular;' + config.font_names;
        config.font_names = 'Soberana Texto Regular/SoberanaTexto-Regular;' + config.font_names;
};