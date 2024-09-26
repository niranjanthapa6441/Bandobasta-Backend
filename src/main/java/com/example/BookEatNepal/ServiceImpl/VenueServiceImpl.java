package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.VenueDTO;
import com.example.BookEatNepal.Enums.VenueStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenueImage;
import com.example.BookEatNepal.Repository.VenueImageRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Request.VenueRequest;
import com.example.BookEatNepal.Service.VenueService;
import com.example.BookEatNepal.Util.CustomException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {
    @Autowired
    private VenueRepo venueRepo;
    @Autowired
    private VenueImageRepo venueImageRepo;

    @Override
    public String save(VenueRequest request) {
        request.setLicenseImagePath(getLicenseImagePath(request));
        request.setPanImagePath(getPanImagePath(request));
        Venue venue = venueRepo.save(toVenue(request));
        saveVenueImages(request.getVenueImages(), venue);
        return "successful";
    }

    @Override
    public String delete(int id) {
        return null;
    }

    @Override
    public VenueDTO findAll(String venue, int page, int size) {
        return null;
    }

    @Override
    public Venue findById(int id) {
        return null;
    }

    @Override
    public String update(VenueRequest request, int id) {
        return null;
    }

    private Venue toVenue(VenueRequest request) {
        Venue venue = new Venue();
        venue.setVenueName(request.getName());
        venue.setEmail(request.getEmail());
        venue.setPrimaryPhoneNumber(request.getPrimaryPhoneNumber());
        venue.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        venue.setDescription(request.getDescription());
        venue.setCountryCode(request.getCountryCode());
        venue.setRegistrationNumber(request.getRegistrationNumber());
        venue.setLicenseNumber(request.getLicenseNumber());
        venue.setVerified(false);
        venue.setPanImagePath(request.getPanImagePath());
        venue.setLicenseImagePath(request.getLicenseImagePath());
        venue.setStatus(VenueStatus.PENDING_APPROVAL);
        venue.setPermanentAccountNumber(request.getPermanentAccountNumber());
        venue.setAddress(toAddress(request));
        return venue;
    }

    private String toAddress(VenueRequest request) {
        String address = request.getStreetNumber() + "-" +
                request.getStreetName() + "," +
                request.getCity() + "," +
                request.getDistrict() +
                request.getState() +
                request.getCountry();
        return address;
    }

    private void validate(MultipartFile multipartFile) {
        if (multipartFile.getSize() > 3000000)
            throw new CustomException(CustomException.Type.INVALID_FILE_SIZE);
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (!checkFileExtension(extension))
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        checkMimeType(multipartFile);
        if (!checkMimeType(multipartFile))
            throw new CustomException(CustomException.Type.INVALID_MIME_TYPE);
    }

    private boolean checkFileExtension(String extension) {
        return (extension != null && (extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg")));
    }

    private boolean checkMimeType(MultipartFile multipartFile) {
        Tika tika = new Tika();
        String mimeType;
        try {
            mimeType = tika.detect(multipartFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (mimeType.equals("image/png") || mimeType.equals("image/jpg") || mimeType.equals("image/jpeg"));

    }

    private String getImagePath(MultipartFile multipartFile, String venueName, String fileName) {
        String uploadDirectory = "./images/venues/" + venueName.replaceAll("\\s", "");
        Path path = Paths.get(uploadDirectory);
        Path filePath = path.resolve(fileName);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filePath.toString().replace("./", "/").trim();
    }

    private void saveVenueImages(List<MultipartFile> venueImages, Venue venue) {
        for (MultipartFile image : venueImages) {
            try {
                validate(image);
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());

                if (fileName.contains(".php%00.")) {
                    throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
                }

                VenueImage venueImage = new VenueImage();
                venueImage.setVenue(venue);
                venueImage.setImageUrl(getImagePath(image, venue.getVenueName(), fileName));

                venueImageRepo.save(venueImage);
            } catch (CustomException e) {
                System.err.println("Error processing image: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private String getLicenseImagePath(VenueRequest request) {
        validate(request.getLicenseImage());
        String fileName = StringUtils.cleanPath(request.getLicenseImage().getOriginalFilename());
        if (fileName.contains(".php%00.")) {
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        }
        String licenseImagePath = getImagePath(request.getLicenseImage(), request.getName(), fileName);
        return licenseImagePath;
    }

    private String getPanImagePath(VenueRequest request) {
        validate(request.getPanImage());
        String fileName = StringUtils.cleanPath(request.getPanImage().getOriginalFilename());
        if (fileName.contains(".php%00.")) {
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        }
        String panImagePath = getImagePath(request.getPanImage(), request.getName(), fileName);
        return panImagePath;
    }

}
