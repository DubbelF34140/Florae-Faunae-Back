package com.dubbelf.aqualapin.controller;

import com.dubbelf.aqualapin.dto.ImageDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@CrossOrigin(
        origins = {"https://www.floraefaunae.fr","http://localhost", "https://localhost", "capacitor://localhost" },
        allowCredentials = "true"
)
public class ImageController {

    @Value("${cloudflare.account.id}")
    private String cloudflareAccountId;

    @Value("${cloudflare.api.token}")
    private String cloudflareApiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping()
    public ResponseEntity<ImageDTO> uploadImage(@RequestParam("image") MultipartFile file) {
        ImageDTO imageUrl = new ImageDTO();
        if (file.isEmpty()) {
            imageUrl.setImageUrl("Veuillez sélectionner un fichier");
            return ResponseEntity.badRequest().body(imageUrl);
        }
        try {
            // Generate a unique file name using UUID
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            // Prepare the request to upload the file to Cloudflare
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + cloudflareApiToken);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
            body.add("metadata", "{\"key\":\"value\"}");
            body.add("requireSignedURLs", "false");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String cloudflareUploadUrl = "https://api.cloudflare.com/client/v4/accounts/" + cloudflareAccountId + "/images/v1";

            // Send the request to Cloudflare
            ResponseEntity<String> response = restTemplate.postForEntity(cloudflareUploadUrl, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response body to extract the public URL
                String responseBody = response.getBody();
                System.err.println(responseBody);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String publicUrl = jsonNode.path("result").path("variants").get(0).asText(); // Extract the public URL

                imageUrl.setImageUrl(publicUrl);
                return ResponseEntity.ok(imageUrl);
            } else {
                imageUrl.setImageUrl("Erreur lors du téléchargement de l'image sur Cloudflare");
                return ResponseEntity.status(response.getStatusCode()).body(imageUrl);
            }

        } catch (IOException e) {
            e.printStackTrace();
            imageUrl.setImageUrl("Erreur lors du téléchargement de l'image");
            return ResponseEntity.badRequest().body(imageUrl);
        }
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String imageName) {
        String imageUrl = "https://api.cloudflare.com/client/v4/accounts/" + cloudflareAccountId + "/images/v1/" + imageName;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + cloudflareApiToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(imageUrl, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // You can dynamically determine the MIME type if needed
                        .body(new InputStreamResource(new ByteArrayInputStream(response.getBody())));
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(null);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> getListImage() {
        String listUrl = "https://api.cloudflare.com/client/v4/accounts/" + cloudflareAccountId + "/images/v1";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + cloudflareApiToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<String>> response = restTemplate.exchange(listUrl, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<String>>() {});

            System.err.println(response);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok().body(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(null);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
