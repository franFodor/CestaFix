package fer.proinz.prijave.controller;

import fer.proinz.prijave.exception.NonExistingPhotoException;
import fer.proinz.prijave.model.Photo;
import fer.proinz.prijave.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class PhotoController {

    private final PhotoService photoService;

    @Operation(summary = "Get all photos")
    @GetMapping("/advanced/photo/getAll")
    public List<Photo> getAllPhotos() {
        return photoService.getAllPhotos();
    }

    @Operation(summary = "Get a photo by its id")
    @GetMapping("/advanced/photo/{photoId}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable("photoId") int photoId)
            throws NonExistingPhotoException {
        return ResponseEntity.ok(photoService
                .getPhotoById(photoId)
                .orElseThrow(NonExistingPhotoException::new)
        );
    }

    @Operation(summary = "Create a photo")
    @PostMapping("/advanced/photo")
    public ResponseEntity<Photo> createPhoto(@RequestBody Photo photo) {
        return ResponseEntity.ok(photoService.createPhoto(photo));
    }

    @Operation(summary = "Change the photo")
    @PatchMapping("/advanced/photo/{photoId}")
    public ResponseEntity<Photo> updatePhoto(
            @PathVariable("photoId") int photoId,
            @RequestBody Photo updatedPhoto
    ) {
        return ResponseEntity.ok(photoService.updatePhoto(photoId, updatedPhoto));
    }

    @Operation(summary = "Delete a photo")
    @DeleteMapping("/advanced/photo/{photoId}")
    public ResponseEntity<String> deletePhoto(@PathVariable("photoId") int photoId)
            throws NonExistingPhotoException {
        return photoService.deletePhoto(photoId);
    }

}
