package fer.proinz.prijave.service;

import fer.proinz.prijave.exception.NonExistingPhotoException;
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
                    if (updatedPhoto.getReport() != null) {
                        photo.setReport(updatedPhoto.getReport());
                    }
                    return photoRepository.save(photo);
                })
                .orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<String> deletePhoto(int photoId) throws NonExistingPhotoException {
        photoRepository.findById(photoId)
                .orElseThrow(NonExistingPhotoException::new);

        photoRepository.deleteById(photoId);
        return ResponseEntity.ok("Photo with id " + photoId + " is deleted.");
    }

}
