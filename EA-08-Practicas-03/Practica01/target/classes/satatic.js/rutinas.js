
function readURL(input) {
    // Verifica si se ha seleccionado un archivo y si el archivo existe
    if (input.files && input.files[0]){
        // Crea una instancia de FileReader para leer el archivo
        var reader = new FileReader();
        
        // Define una función que se ejecutará cuando el archivo se haya cargado
        reader.onload = function(e){
            // Cambia la fuente (src) de la imagen con id 'blah' 
            // al resultado de la lectura del archivo (la imagen en base64)
            $('#blah').attr('src', e.target.result)
            
            // Ajusta la altura de la imagen a 200 píxeles
            .height(200);
        };
        
        // Inicia la lectura del archivo seleccionado y lo convierte en una URL de datos
        reader.readAsDataURL(input.files[0]);
    }
}

