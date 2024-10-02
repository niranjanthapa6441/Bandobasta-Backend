package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.*;
import com.example.BookEatNepal.Enums.EventType;
import com.example.BookEatNepal.Enums.MenuType;
import com.example.BookEatNepal.Enums.PackageStatus;
import com.example.BookEatNepal.Enums.PackageType;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Model.Package;
import com.example.BookEatNepal.Repository.*;
import com.example.BookEatNepal.Request.PackageRequest;
import com.example.BookEatNepal.Service.PackageService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PackageServiceImpl implements PackageService {
    private final static String SUCCESS_MESSAGE = "successful";
    @Autowired
    private AmenityRepo amenityRepo;
    @Autowired
    private VenueRepo venueRepo;
    @Autowired
    private PackageRepo packageRepo;
    @Autowired
    private PackageAmenityRepo packageAmenityRepo;
    @Autowired
    private PackageHallRepo packageHallRepo;
    @Autowired
    private PackageMenuRepo packageMenuRepo;
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private HallRepo hallRepo;

    @Autowired
    private HallImageRepo hallImageRepo;
    @Autowired
    private FoodMenuRepo foodMenuRepo;
    @Autowired
    private EntityManager entityManager;

    @Override
    public String save(PackageRequest request) {
        Venue venue = findVenueById(request.getVenueId());
        Hall hall = findHallById(request.getHallId());
        Menu menu = findMenuById(request.getMenuId());
        List<Amenity> amenities = findAmenitiesById(request.getAmenityIds());
        Package aPackage = packageRepo.save(convertToPackage(request, venue));
        savePackageAmenities(aPackage, amenities);
        savePackageHall(aPackage, hall);
        savePackageMenu(aPackage, menu);
        return SUCCESS_MESSAGE;
    }

    @Override
    public String delete(int id) {
        Package aPackage = packageRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.PACKAGE_NOT_FOUND));
        aPackage.setStatus(PackageStatus.DELETE);
        return SUCCESS_MESSAGE;
    }

    @Override
    public PackageDTO findPackageByVenue(String venueId, String packageType, String eventType, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Package> query = cb.createQuery(Package.class);
        Root<Package> packageRoot = query.from(Package.class);
        Join<Package, Venue> packageVenueJoin = packageRoot.join("venue");

        query.select(packageRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (venueId != null && !venueId.isEmpty()) {
            try {
                int id = Integer.parseInt(venueId);
                predicates.add(cb.equal(packageVenueJoin.get("id"), id));
            } catch (NumberFormatException e) {
                throw new CustomException(CustomException.Type.INVALID_VENUE_ID);
            }
        }

        if (packageType != null && !packageType.isEmpty()) {
            try {
                PackageType enumPackageType = PackageType.valueOf(packageType.toUpperCase());
                predicates.add(cb.equal(packageRoot.get("packageType"), enumPackageType));
            } catch (IllegalArgumentException e) {
                throw new CustomException(CustomException.Type.INVALID_MENU_TYPE);
            }
        }
        if (eventType != null && !eventType.isEmpty()) {
            try {
                EventType enumEventType = EventType.valueOf(eventType.toUpperCase());
                predicates.add(cb.equal(packageRoot.get("eventType"), enumEventType));
            } catch (IllegalArgumentException e) {
                throw new CustomException(CustomException.Type.INVALID_EVENT_TYPE);
            }
        }

        query.where(predicates.toArray(new Predicate[0]));

        List<Package> packages = entityManager.createQuery(query).getResultList();

        TypedQuery<Package> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<Package> pagedPackages = typedQuery.getResultList();

        int totalElements = packages.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return convertToPackageDTO(pagedPackages, page, totalElements, totalPages);
    }

    @Override
    public PackageDetail findById(int id) {
        Package aPackage = packageRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.PACKAGE_NOT_FOUND));
        return convertToPackageDetail(aPackage);
    }

    @Override
    public String update(PackageRequest request, int id) {
        Package aPackage = packageRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.PACKAGE_NOT_FOUND));
        aPackage.setName(request.getName());
        aPackage.setDescription(request.getDescription());
        aPackage.setPrice(request.getPrice());
        aPackage.setPackageType(PackageType.valueOf(request.getPackageType()));
        aPackage.setStatus(PackageStatus.valueOf(request.getStatus()));
        aPackage.setVenue(findVenueById(String.valueOf(id)));
        aPackage.setEventType(EventType.valueOf(request.getEventType()));
        return SUCCESS_MESSAGE;
    }

    private void savePackageMenu(Package aPackage, Menu menu) {
        packageMenuRepo.save(convertToPackageMenu(aPackage, menu));
    }

    private PackageMenu convertToPackageMenu(Package aPackage, Menu menu) {
        PackageMenu packageMenu = new PackageMenu();
        packageMenu.setAPackage(aPackage);
        packageMenu.setMenu(menu);
        return packageMenu;
    }

    private void savePackageHall(Package aPackage, Hall hall) {
        packageHallRepo.save(convertToPackageHall(aPackage, hall));
    }

    private PackageHall convertToPackageHall(Package aPackage, Hall hall) {
        PackageHall packageHall = new PackageHall();
        packageHall.setAPackage(aPackage);
        packageHall.setHall(hall);
        return packageHall;
    }

    private void savePackageAmenities(Package aPackage, List<Amenity> amenities) {
        for (Amenity amenity : amenities
        ) {
            packageAmenityRepo.save(convertToPackageAmenity(aPackage, amenity));
        }
    }

    private PackageAmenity convertToPackageAmenity(Package aPackage, Amenity amenity) {
        PackageAmenity packageAmenity = new PackageAmenity();
        packageAmenity.setAmenity(amenity);
        packageAmenity.setAPackage(aPackage);
        return packageAmenity;
    }

    private Package convertToPackage(PackageRequest request, Venue venue) {
        Package aPackage = new Package();
        aPackage.setName(request.getName());
        aPackage.setDescription(request.getDescription());
        aPackage.setPrice(request.getPrice());
        aPackage.setPackageType(PackageType.valueOf(request.getPackageType()));
        aPackage.setStatus(PackageStatus.valueOf(request.getStatus()));
        aPackage.setVenue(venue);
        aPackage.setEventType(EventType.valueOf(request.getEventType()));
        return aPackage;
    }

    private List<Amenity> findAmenitiesById(List<String> amenityIds) {
        List<Amenity> amenities = new ArrayList<>();
        for (String id : amenityIds
        ) {
            amenities.add(findAmenityById(id));
        }
        return amenities;
    }

    private Amenity findAmenityById(String id) {
        return amenityRepo.findById(Integer.parseInt(id)).orElseThrow(() -> new CustomException(CustomException.Type.AMENITY_NOT_FOUND));
    }

    private Menu findMenuById(String menuId) {
        return menuRepo.findById(Integer.parseInt(menuId)).orElseThrow(() -> new CustomException(CustomException.Type.MENU_NOT_FOUND));
    }

    private Hall findHallById(String hallId) {
        return hallRepo.findById(Integer.parseInt(hallId)).orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
    }

    private Venue findVenueById(String venueId) {
        return venueRepo.findById(Integer.parseInt(venueId)).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
    }

    private PackageDTO convertToPackageDTO(List<Package> pagedPackages, int page, int totalElements, int totalPages) {
        List<PackageDetail> packages = convertToPackages(pagedPackages);
        return PackageDTO.builder()
                .packages(packages)
                .currentPage(page)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    private List<PackageDetail> convertToPackages(List<Package> packages) {
        List<PackageDetail> packageDetails = new ArrayList<>();
        for (Package aPackage : packages
        ) {
            packageDetails.add(convertToPackageDetail(aPackage));
        }
        return packageDetails;
    }

    private PackageDetail convertToPackageDetail(Package aPackage) {
        return PackageDetail.builder()
                .id(String.valueOf(aPackage.getId()))
                .venueId(String.valueOf(aPackage.getVenue().getId()))
                .name(aPackage.getName())
                .packageType(String.valueOf(aPackage.getPackageType()))
                .eventType(String.valueOf(aPackage.getEventType()))
                .description(aPackage.getDescription())
                .price(aPackage.getPrice())
                .status(String.valueOf(aPackage.getStatus()))
                .menuDetail(findMenuByPackage(aPackage))
                .hallDetail(findHallByPackage(aPackage))
                .amenities(findAmenitiesByPackage(aPackage))
                .build();
    }

    private List<AmenityDetail> findAmenitiesByPackage(Package aPackage) {
        List<PackageAmenity> packageAmenities = packageAmenityRepo.findByaPackage(aPackage);
        List<AmenityDetail> amenityDetails = new ArrayList<>();
        for (PackageAmenity packageAmenity : packageAmenities
        ) {
            amenityDetails.add(toAmenityDetail(packageAmenity.getAmenity()));
        }
        return amenityDetails;
    }

    private AmenityDetail toAmenityDetail(Amenity amenity) {
        return AmenityDetail.builder()
                .name(amenity.getName())
                .venueId(String.valueOf(amenity.getVenue().getId()))
                .description(amenity.getDescription())
                .id(String.valueOf(amenity.getId()))
                .status(String.valueOf(amenity.getStatus()))
                .imageUrl(amenity.getImageUrl())
                .price(amenity.getPrice())
                .build();
    }

    private HallDetail findHallByPackage(Package aPackage) {
        PackageHall packageHall = packageHallRepo.findByaPackage(aPackage);
        return toHallDetail(packageHall.getHall());
    }

    private HallDetail toHallDetail(Hall hall) {
        return HallDetail.builder()
                .name(hall.getName())
                .id(hall.getId())
                .venueId(String.valueOf(hall.getVenue().getId()))
                .capacity(hall.getCapacity())
                .floorNumber(hall.getFloorNumber())
                .description(hall.getDescription().toString())
                .status(String.valueOf(hall.getStatus()))
                .hallImagePaths(getHallImagePaths(hall.getId()))
                .build();
    }

    private List<String> getHallImagePaths(int id) {
        List<String> hallImagePaths = new ArrayList<>();
        List<HallImage> hallImages = hallImageRepo.findByHallId(id);
        for (HallImage image : hallImages
        ) {
            hallImagePaths.add(image.getImageUrl());
        }
        return hallImagePaths;
    }

    private MenuDetail findMenuByPackage(Package aPackage) {
        PackageMenu packageMenu = packageMenuRepo.findByaPackage(aPackage);
        return toMenuDetail(packageMenu.getMenu());
    }

    private MenuDetail toMenuDetail(Menu menu) {
        List<FoodMenu> foodMenus = foodMenuRepo.findByMenu(menu);
        return MenuDetail.builder().
                id(String.valueOf(menu.getId())).
                menuType(String.valueOf(menu.getMenuType())).
                price(menu.getPrice()).
                description(menu.getDescription()).
                venueId(String.valueOf(menu.getVenue().getId())).
                status(String.valueOf(menu.getStatus())).
                foodDetails(convertToFoodDetail(foodMenus))
                .build();
    }

    private List<FoodDetail> convertToFoodDetail(List<FoodMenu> foodMenus) {
        List<FoodDetail> foodDetails = new ArrayList<>();
        for (FoodMenu foodMenu : foodMenus
        ) {
            foodDetails.add(FoodDetail.builder().
                    foodCategory(String.valueOf(foodMenu.getFood().getCategory())).
                    name(foodMenu.getFood().getName()).
                    description(foodMenu.getFood().getDescription()).
                    imageUrl(foodMenu.getFood().getImageUrl()).
                    id(String.valueOf(foodMenu.getFood().getId())).
                    status(String.valueOf(foodMenu.getFood().getStatus())).
                    venueId(String.valueOf(foodMenu.getFood().getVenue().getId())).
                    build());
        }
        return foodDetails;
    }
}
