package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.*;
import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.EventType;
import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Repository.*;
import com.example.BookEatNepal.Request.HallBookingRequest;
import com.example.BookEatNepal.Service.HallBookingService;
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
public class HallBookingServiceImpl implements HallBookingService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private HallAvailabilityRepo hallAvailabilityRepo;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private HallBookingRepo hallBookingRepo;
    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private HallImageRepo hallImageRepo;

    @Autowired
    private HallRepo hallRepo;

    @Autowired
    private FoodMenuRepo foodMenuRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String save(HallBookingRequest request) {
        HallAvailability hallAvailability = getHallAvailability(request.getHallAvailabilityId());

        Menu menu = getMenu(request.getMenuId());

        AppUser user = getUser(request.getUserId());

        hallBookingRepo.save(toBooking(request, menu, hallAvailability, user));

        updateHallAvailabilityStatus(request.getHallAvailabilityId(), HallStatus.PENDING);

        return SUCCESS_MESSAGE;
    }

    @Override
    public String delete(int id) {
        HallBooking hallBooking = getHallBooking(id);
        hallBooking.setStatus(BookingStatus.DELETED);

        hallBookingRepo.save(hallBooking);
        return SUCCESS_MESSAGE;
    }

    @Override
    public HallBookingDTO findBookingByUser(String userId, String bookingDate, int page, int size) {
        int id = Integer.parseInt(userId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HallBooking> query = cb.createQuery(HallBooking.class);
        Root<HallBooking> bookingRoot = query.from(HallBooking.class);
        Join<HallBooking, AppUser> hallBookingAppUserJoin = bookingRoot.join("user");

        query.select(bookingRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0 ) {
            predicates.add(cb.equal(hallBookingAppUserJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<HallBooking> bookings = entityManager.createQuery(query).getResultList();

        TypedQuery<HallBooking> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<HallBooking> pagedBookings = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = bookings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toBookingDTO(pagedBookings, currentPage, totalElements, totalPages);
    }

    @Override
    public HallBookingDTO findBookingByVenue(String venueId, String bookingDate, String hallId, int page, int size) {
        int id = Integer.parseInt(venueId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HallBooking> query = cb.createQuery(HallBooking.class);

        Root<HallBooking> bookingRoot = query.from(HallBooking.class);
        Join<HallBooking, HallAvailability> hallBookingHallAvailabilityJoin = bookingRoot.join("hallAvailability");
        Join<HallAvailability, Hall> hallAvailabilityHallJoin = hallBookingHallAvailabilityJoin.join("hall");
        Join<Hall, Venue> hallVenueJoin = hallAvailabilityHallJoin.join("venue");

        query.select(bookingRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0 ) {
            predicates.add(cb.equal(hallVenueJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<HallBooking> bookings = entityManager.createQuery(query).getResultList();

        TypedQuery<HallBooking> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<HallBooking> pagedBookings = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = bookings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toBookingDTO(pagedBookings, currentPage, totalElements, totalPages);
    }

    @Override
    public String update(HallBookingRequest request, int id) {
        return SUCCESS_MESSAGE;
    }

    @Override
    public String confirmBooking(int id) {
        HallBooking hallBooking = getHallBooking(id);
        hallBooking.setStatus(BookingStatus.BOOKED);
        hallBookingRepo.save(hallBooking);

        updateHallAvailabilityStatus(String.valueOf(hallBooking.getHallAvailability().getId()), HallStatus.BOOKED);

        return SUCCESS_MESSAGE;
    }

    @Override
    public HallBookingDetail findById(int id) {
        HallBooking hallBooking = getHallBooking(id);
        return convertToBookingDetail(hallBooking);
    }

    private HallBookingDetail convertToBookingDetail(HallBooking hallBooking) {
        return HallBookingDetail.builder()
                .id(hallBooking.getId())
                .hallDetail(toHallDetail(getHall(hallBooking.getHallAvailability().getHall().getId())))
                .menuDetail(toMenuDetail(hallBooking.getMenu()))
                .userId(String.valueOf(hallBooking.getUser().getId()))
                .requestedDate(Formatter.convertDateToStr(hallBooking.getRequestedDate(), "yyyy-MM-dd"))
                .confirmedDate(Formatter.convertDateToStr(hallBooking.getConfirmedDate(), "yyyy-MM-dd"))
                .requestedTime(hallBooking.getRequestedTime())
                .confirmedTime(hallBooking.getConfirmedTime())
                .price(hallBooking.getPrice())
                .status(String.valueOf(hallBooking.getStatus()))
                .eventType(String.valueOf(hallBooking.getEventType()))
                .build();
    }

    private Hall getHall(int id) {
        Hall hall = hallRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
        return hall;
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

    private void updateHallAvailabilityStatus(String id, HallStatus status) {
        HallAvailability hallAvailability = getHallAvailability(id);
        hallAvailability.setStatus(status);
        hallAvailabilityRepo.save(hallAvailability);
    }

    private AppUser getUser(String id) {
        return appUserRepo.findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.USER_NOT_FOUND));
    }

    private Menu getMenu(String id) {
        return menuRepo.findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.MENU_NOT_FOUND));
    }

    private HallAvailability getHallAvailability(String id) {
        return hallAvailabilityRepo
                .findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.HALL_AVAILABILITY_NOT_FOUND));
    }

    private HallBooking toBooking(HallBookingRequest request, Menu menu, HallAvailability hallAvailability, AppUser user) {
        HallBooking hallBooking = new HallBooking();
        hallBooking.setHallAvailability(hallAvailability);
        hallBooking.setMenu(menu);
        hallBooking.setRequestedDate(LocalDate.now());
        hallBooking.setRequestedTime(LocalTime.now());
        hallBooking.setStatus(BookingStatus.PENDING);
        hallBooking.setBookedForDate(Formatter.convertStrToDate(request.getBookedForDate(),"yyyy-MM-dd"));
        hallBooking.setEventType(EventType.valueOf(request.getEventType()));
        hallBooking.setUser(user);
        return hallBooking;
    }

    private HallBooking getHallBooking(int id) {
        return hallBookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.BOOKING_NOT_FOUND));
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
                foodDetails(toFoodDetail(foodMenus))
                .build();
    }

    private List<FoodDetail> toFoodDetail(List<FoodMenu> foodMenus) {
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
    private HallBookingDTO toBookingDTO(List<HallBooking> bookings, int currentPage, int totalElements, int totalPages) {
        List<HallBookingDetail> bookingDetails = getBookingDetails(bookings);
        return  HallBookingDTO.builder()
                .bookings(bookingDetails)
                .currentPage(currentPage)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    private List<HallBookingDetail> getBookingDetails(List<HallBooking> bookings) {
        List<HallBookingDetail> bookingDetails = new ArrayList<>();
        for (HallBooking hallBooking: bookings
        ) {
            bookingDetails.add(convertToBookingDetail(hallBooking));
        }
        return bookingDetails;
    }
}
