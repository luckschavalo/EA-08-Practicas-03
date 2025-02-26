/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Practica_3.Practica_3.service.impl;

import Practica_3.Practica_3.service.impl.*;
import com.google.auth.Credentials;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.SignUrlOption;
import com.google.cloud.storage.StorageOptions;
import Practica_3.Practica_3.service.FirebaseStorageService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    @Override
    public String cargaImagen(MultipartFile archivoLocalCliente, String carpeta, Long id) {
        try {
            // El nombre original del archivo local del cliente
            String extension = archivoLocalCliente.getOriginalFilename();

            // Se genera el nombre según el código del artículo
            String fileName = "img" + id + "." + extension;

            // Se convierte el archivo a un archivo temporal
            File file = this.converToFile(archivoLocalCliente);

            // Se sube el archivo a Firestore y se obtiene el URL de la imagen
            String URL = this.uploadFile(file, carpeta, fileName);

            // Se elimina el archivo temporal cargado desde el cliente
            file.delete();

            return URL;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String uploadFile(File file,
            String carpeta,
            String fileName) throws IOException {
        //Se define el lugar y acceso al archivo .jasper
        ClassPathResource json = new ClassPathResource(rutaJsonFile + File.separator + archivoJsonFile);
        BlobId blobId = BlobId.of(BucketName, rutaSuperiorStorage + "/" + carpeta + "/" + fileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("media").build();

        Credentials credentials = GoogleCredentials
                .fromStream(json.getInputStream());
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        String url = storage.signUrl(blobInfo,
                3650,
                TimeUnit.DAYS,
                SignUrlOption.signWith((ServiceAccountSigner) credentials))
                .toString();

        return url;
    }

    private File converToFile(MultipartFile archivoLocalCliente) throws IOException {
        File tempFile = File.createTempFile("img", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(archivoLocalCliente.getBytes());
            fos.close();
        }
        return tempFile;
    }

    private String sacaNumero(long id) {
        return String.format("%01d", id);
    }
}
