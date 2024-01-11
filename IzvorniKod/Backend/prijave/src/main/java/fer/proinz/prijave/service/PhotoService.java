package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Photo;
import fer.proinz.prijave.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public Optional<Photo> getPhotoById(int photoId) {
        return photoRepository.findById(photoId);
    }

    public Photo createPhoto(Photo photo) {
        return photoRepository.save(photo);
    }

    public Photo updatePhoto(int photoId, Photo updatedPhoto) {
        return photoRepository.findById(photoId)
                .map(photo -> {
                    if (updatedPhoto.getPhotoData() != null) {
                        photo.setPhotoData(updatedPhoto.getPhotoData());
                    }
                    return photoRepository.save(photo);
                })
                .orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<String> deletePhoto(int photoId) {
        Optional<Photo> photoOptional = photoRepository.findById(photoId);
        if (photoOptional.isPresent()) {
            photoRepository.deleteById(photoId);
            return ResponseEntity.ok("Photo with id " + photoId + " is deleted.");
        } else {
            throw new RuntimeException("Photo with id " + photoId + " doesn't exists!");
        }
    }

}
