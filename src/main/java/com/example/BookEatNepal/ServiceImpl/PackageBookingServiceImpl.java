package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.*;
import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.EventType;
import com.example.BookEatNepal.Enums.PackageStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Model.Package;
import com.example.BookEatNepal.Repository.*;
import com.example.BookEatNepal.Request.PackageBookingRequest;
import com.example.BookEatNepal.Service.PackageBookingService;
import com.example.BookEatNepal.Util.CustomException;
import com.example.BookEatNepal.Util.Formatter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PackageBookingServiceImpl implements PackageBookingService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private PackageAvailabilityRepo packageAvailabilityRepo;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private PackageBookingRepo packageBookingRepo;
    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private HallImageRepo hallImageRepo;

    @Autowired
    private HallRepo hallRepo;

    @Autowired
    private FoodMenuRepo foodMenuRepo;

    @Autowired
    private PackageAmenityRepo packageAmenityRepo;

    @Autowired
    private EntityManager entityManager;
    @Override
    public String save(PackageBookingRequest request) {
        PackageAvailability packageAvailability = getPackageAvailability(request.getPackageAvailabilityId());

        AppUser user = getUser(request.getUserId());

        packageBookingRepo.save(toBooking(request, packageAvailability, user));

        updatePackageAvailabilityStatus(request.getPackageAvailabilityId(), PackageStatus.PENDING);

        return SUCCESS_MESSAGE;
    }

    @Override
    public String delete(int id) {
        PackageBooking packageBooking = getPackageBooking(id);
        return null;
    }

    private PackageBooking getPackageBooking(int id) {
        return packageBookingRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.BOOKING_NOT_FOUND));
    }

    @Override
    public PackageBookingDTO findBookingByUser(String userId, String bookingDate, int page, int size) {
        int id = Integer.parseInt(userId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PackageBooking> query = cb.createQuery(PackageBooking.class);
        Root<PackageBooking> bookingRoot = query.from(PackageBooking.class);
        Join<PackageBooking, AppUser> packageBookingAppUserJoin = bookingRoot.join("user");

        query.select(bookingRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0 ) {
            predicates.add(cb.equal(packageBookingAppUserJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<PackageBooking> bookings = entityManager.createQuery(query).getResultList();

        TypedQuery<PackageBooking> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<PackageBooking> pagedBookings = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = bookings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toBookingDTO(pagedBookings, currentPage, totalElements, totalPages);
    }
    @Override
    public PackageBookingDTO findBookingByVenue(String venueId, String bookingDate, String hallId, int page, int size) {
        int id = Integer.parseInt(venueId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PackageBooking> query = cb.createQuery(PackageBooking.class);

        Root<PackageBooking> bookingRoot = query.from(PackageBooking.class);
        Join<PackageBooking, PackageAvailability> packageBookingPackageAvailabilityJoin = bookingRoot.join("packageAvailability");
        Join<PackageAvailability, Package> packageAvailabilityPackageJoin = packageBookingPackageAvailabilityJoin.join("package");
        Join<Package, Venue> packageVenueJoin = packageAvailabilityPackageJoin.join("venue");

        query.select(bookingRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0 ) {
            predicates.add(cb.equal(packageVenueJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<PackageBooking> bookings = entityManager.createQuery(query).getResultList();

        TypedQuery<PackageBooking> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<PackageBooking> pagedBookings = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = bookings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toBookingDTO(pagedBookings, currentPage, totalElements, totalPages);
    }

    @Override
    public PackageBookingDetail findById(int id) {
        return null;
    }

    @Override
    public String update(PackageBookingRequest request, int id) {
        return SUCCESS_MESSAGE;
    }

    @Override
    public String confirmBooking(int id) {
        PackageBooking packageBooking = getPackageBooking(id);
        packageBooking.setStatus(BookingStatus.BOOKED);
        packageBookingRepo.save(packageBooking);

        updatePackageAvailabilityStatus(String.valueOf(packageBooking.getPackageAvailability().getId()), PackageStatus.BOOKED);

        return SUCCESS_MESSAGE;
    }
    private void updatePackageAvailabilityStatus(String packageAvailabilityId, PackageStatus packageStatus) {
        PackageAvailability packageAvailability = getPackageAvailability(packageAvailabilityId);
        packageAvailability.setStatus(packageStatus);
    }

    private PackageBooking toBooking(PackageBookingRequest request, PackageAvailability packageAvailability, AppUser user) {
        PackageBooking packageBooking = new PackageBooking();
        packageBooking.setPackageAvailability(packageAvailability);
        packageBooking.setUser(user);
        packageBooking.setStatus(BookingStatus.IN_PROGRESS);
        packageBooking.setEventType(EventType.valueOf(request.getEventType()));
        packageBooking.setRequestedDate(LocalDate.now());
        packageBooking.setRequestedTime(LocalTime.now());
        return packageBooking;
    }

    private AppUser getUser(String id) {
        return appUserRepo.findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.USER_NOT_FOUND));
    }
    private PackageAvailability getPackageAvailability(String id) {
        return packageAvailabilityRepo
                .findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.PACKAGE_AVAILABILITY_NOT_FOUND));
    }
    private PackageBookingDTO toBookingDTO(List<PackageBooking> bookings, int currentPage, int totalElements, int totalPages) {
        List<PackageBookingDetail> bookingDetails = getBookingDetails(bookings);
        return  PackageBookingDTO.builder()
                .bookings(bookingDetails)
                .currentPage(currentPage)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    private List<PackageBookingDetail> getBookingDetails(List<PackageBooking> bookings) {
        List<PackageBookingDetail> bookingDetails = new ArrayList<>();
        for (PackageBooking packageBooking: bookings
        ) {
            bookingDetails.add(convertToBookingDetail(packageBooking));
        }
        return bookingDetails;
    }

    private PackageBookingDetail convertToBookingDetail(PackageBooking packageBooking) {
        return PackageBookingDetail.builder()
                .id(packageBooking.getId())
                .packageDetail(convertToPackageDetail(packageBooking.getPackageAvailability().getAPackage()))
                .userId(String.valueOf(packageBooking.getUser().getId()))
                .requestedDate(Formatter.convertDateToStr(packageBooking.getRequestedDate(), "yyyy-MM-dd"))
                .confirmedDate(Formatter.convertDateToStr(packageBooking.getConfirmedDate(), "yyyy-MM-dd"))
                .requestedTime(packageBooking.getRequestedTime())
                .confirmedTime(packageBooking.getConfirmedTime())
                .price(packageBooking.getPackageAvailability().getAPackage().getPrice())
                .status(String.valueOf(packageBooking.getStatus()))
                .eventType(String.valueOf(packageBooking.getEventType()))
                .build();
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
                .menuDetail(toMenuDetail(aPackage.getMenu()))
                .hallDetail(toHallDetail(aPackage.getHall()))
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
