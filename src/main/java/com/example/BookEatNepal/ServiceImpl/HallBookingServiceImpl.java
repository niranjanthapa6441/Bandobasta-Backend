package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.EventType;
import com.example.BookEatNepal.Enums.HallShift;
import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Payload.DTO.*;
import com.example.BookEatNepal.Payload.Request.BookingDateRequest;
import com.example.BookEatNepal.Payload.Request.UpdateBookingRequest;
import com.example.BookEatNepal.Repository.*;
import com.example.BookEatNepal.Payload.Request.BookingRequest;
import com.example.BookEatNepal.Service.EmailService;
import com.example.BookEatNepal.Service.HallBookingService;
import com.example.BookEatNepal.Util.CustomException;
import com.example.BookEatNepal.Util.Formatter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HallBookingServiceImpl implements HallBookingService {
    private static final String SUCCESS_MESSAGE = "successful";
    private final EmailService emailService;

    @Autowired
    private HallAvailabilityRepo hallAvailabilityRepo;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private HallBookingRepo hallBookingRepo;

    @Autowired
    private FoodRepo foodRepo;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private HallImageRepo hallImageRepo;

    @Autowired
    private HallRepo hallRepo;

    @Autowired
    private VenueRepo venueRepo;

    @Autowired
    private MenuItemRepo foodMenuRepo;

    @Autowired
    private BookingMenuItemRepo bookingMenuItemRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public String save(BookingRequest request) {
        HallAvailability hallAvailability = getHallAvailability(request.getId());

        Menu menu = getMenu(request.getMenuId());

        AppUser user = getUser(request.getUserId());

        if (request.getNumberOfGuests() == 0){
            throw  new CustomException(CustomException.Type.NUMBER_OF_GUESTS_SHOULD_NOT_BE_EMPTY);
        }
        if (hallAvailability.getStatus().equals(HallStatus.AVAILABLE)){
            HallBooking hallBooking=hallBookingRepo.save(toBooking(request, menu, hallAvailability, user));

            updateHallAvailabilityStatus(request.getId(), HallStatus.PENDING);

            toBookingMenuItem(request.getFoodIds(),hallBooking);
            emailService.sendEmail(user.getEmail(),"Thank You For Booking",
                    buildEmail(
                            String.valueOf(hallBooking.getId()),
                            user.getFirstName(), Formatter.convertDateToStr(hallBooking.getBookedForDate(), "yyyy-MM-dd"),
                            hallBooking.getHallAvailability().getHall().getVenue().getVenueName(),
                            Integer.toString(hallBooking.getNumberOfGuests()),
                            hallBooking.getHallAvailability().getHall().getName()
                    ));
            return SUCCESS_MESSAGE;
        }
        else throw  new CustomException(CustomException.Type.BOOKING_HAS_ALREADY_BEEN_MADE);
    }


    @Override
    public String delete(int id) {
        HallBooking hallBooking = getHallBooking(id);
        hallBooking.setStatus(BookingStatus.DELETED);

        hallBookingRepo.save(hallBooking);
        return SUCCESS_MESSAGE;
    }

    @Override
    public HallBookingDTO findBookingByUser(String userId, String startDate, String endDate, String bookingStatus, String sortBy, int page, int size) {
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

        if (bookingStatus != null && !bookingStatus.isBlank() ) {
            predicates.add(cb.equal(bookingRoot.get("status"), bookingStatus));
        }

        if ((startDate != null && !startDate.isEmpty())&&(endDate != null && !endDate.isEmpty())) {
            predicates.add(cb.between(bookingRoot.get("bookedForDate"), Formatter.convertStrToDate(startDate,"yyyy-MM-dd"),Formatter.convertStrToDate(endDate,"yyyy-MM-dd")));
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
    @Transactional
    public String update(UpdateBookingRequest request, int id) {
        HallBooking booking= getHallBooking(id);
        updateHallAvailabilityStatus(String.valueOf(booking.getHallAvailability().getId()), HallStatus.AVAILABLE);

        booking.setPrice(request.getPrice());
        booking.setMenu(getMenu(request.getMenuId()));
        booking.setHallAvailability(getHallAvailability(request.getHallAvailabilityId()));
        booking.setBookedForDate(getHallAvailability(request.getHallAvailabilityId()).getDate());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setStatus(BookingStatus.valueOf(request.getStatus()));
        booking.setConfirmedDate(LocalDate.now());
        booking.setConfirmedTime(LocalTime.now());

        HallBooking updatedBooking = hallBookingRepo.save(booking);

        updateHallAvailabilityStatus(String.valueOf(updatedBooking.getHallAvailability().getId()), HallStatus.PENDING);

        deleteBookingMenu(booking);
        toBookingMenuItem(request.getFoodIds(),booking);
        emailService.sendEmail(booking.getUser().getEmail(),"Your Booking has been updated",
                buildEmail(
                        String.valueOf(booking.getId()),
                        booking.getUser().getFirstName(), Formatter.convertDateToStr(booking.getBookedForDate(), "yyyy-MM-dd"),
                        booking.getHallAvailability().getHall().getVenue().getVenueName(),
                        Integer.toString(booking.getNumberOfGuests()),
                        booking.getHallAvailability().getHall().getName()
                ));
        return SUCCESS_MESSAGE;
    }

    private void deleteBookingMenu(HallBooking hallBooking) {
        bookingMenuItemRepo.deleteAllByBooking(hallBooking);
    }

    @Override
    @Transactional
    public String confirmBooking(int id) {
        HallBooking hallBooking = getHallBooking(id);
        hallBooking.setStatus(BookingStatus.BOOKED);
        hallBooking.setConfirmedDate(LocalDate.now());
        hallBooking.setConfirmedTime(LocalTime.now());
        hallBookingRepo.save(hallBooking);

        updateHallAvailabilityStatus(String.valueOf(hallBooking.getHallAvailability().getId()), HallStatus.BOOKED);

        emailService.sendEmail(hallBooking.getUser().getEmail(),"Your Booking has been Confirmed",
                buildEmail(
                        String.valueOf(hallBooking.getId()),
                        hallBooking.getUser().getFirstName(), Formatter.convertDateToStr(hallBooking.getBookedForDate(), "yyyy-MM-dd"),
                        hallBooking.getHallAvailability().getHall().getVenue().getVenueName(),
                        Integer.toString(hallBooking.getNumberOfGuests()),
                        hallBooking.getHallAvailability().getHall().getName()
                ));
        return SUCCESS_MESSAGE;
    }

    @Override
    @Transactional
    public String cancelBooking(int id) {
        HallBooking hallBooking = getHallBooking(id);
        hallBooking.setStatus(BookingStatus.CANCELLED);
        hallBooking.setConfirmedDate(LocalDate.now());
        hallBooking.setConfirmedTime(LocalTime.now());
        hallBookingRepo.save(hallBooking);

        updateHallAvailabilityStatus(String.valueOf(hallBooking.getHallAvailability().getId()), HallStatus.AVAILABLE);
        emailService.sendEmail(hallBooking.getUser().getEmail(),"Your Booking has been Cancelled",
                buildEmail(
                        String.valueOf(hallBooking.getId()),
                        hallBooking.getUser().getFirstName(), Formatter.convertDateToStr(hallBooking.getBookedForDate(), "yyyy-MM-dd"),
                        hallBooking.getHallAvailability().getHall().getVenue().getVenueName(),
                        Integer.toString(hallBooking.getNumberOfGuests()),
                        hallBooking.getHallAvailability().getHall().getName()
                ));

        return SUCCESS_MESSAGE;
    }

    @Override
    public String bookingDateRequest(BookingDateRequest bookingDateRequest) {
        Venue venue = venueRepo.findById(Integer.valueOf(bookingDateRequest.getVenueId())).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
        AppUser appUser = appUserRepo.findById(Integer.valueOf(bookingDateRequest.getUserId())).orElseThrow(() -> new CustomException(CustomException.Type.USER_NOT_FOUND));
        sendBookingDateRequestEmail(venue,appUser,bookingDateRequest);
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
                .hallAvailabilityId(hallBooking.getHallAvailability().getId())
                .venueName(hallBooking.getHallAvailability().getHall().getVenue().getVenueName())
                .hallDetail(toHallDetail(getHall(hallBooking.getHallAvailability().getHall().getId())))
                .menuDetail(findBookingMenu(hallBooking,hallBooking.getMenu()))
                .startTime(hallBooking.getHallAvailability().getStartTime())
                .endTime(hallBooking.getHallAvailability().getEndTime())
                .userId(String.valueOf(hallBooking.getUser().getId()))
                .requestedDate(Formatter.convertDateToStr(hallBooking.getRequestedDate(), "yyyy-MM-dd"))
                .confirmedDate(Formatter.convertDateToStr(hallBooking.getConfirmedDate(), "yyyy-MM-dd"))
                .requestedTime(hallBooking.getRequestedTime())
                .confirmedTime(hallBooking.getConfirmedTime())
                .bookedForDate(Formatter.convertDateToStr(hallBooking.getBookedForDate(), "yyyy-MM-dd"))
                .price(hallBooking.getPrice())
                .status(String.valueOf(hallBooking.getStatus()))
                .eventType(String.valueOf(hallBooking.getEventType()))
                .numberOfGuests(hallBooking.getNumberOfGuests())
                .build();
    }

    private MenuDetail findBookingMenu(HallBooking hallBooking, Menu menu) {
        List<Food> foods= bookingMenuItemRepo.findFoodsByBookingId(hallBooking);
        return MenuDetail.builder().
                id(String.valueOf(menu.getId())).
                menuType(String.valueOf(menu.getMenuType())).
                price(menu.getPrice()).
                description(menu.getDescription()).
                venueId(String.valueOf(menu.getVenue().getId())).
                status(String.valueOf(menu.getStatus())).
                foodDetails(toFoodDetails(foods))
                .build();
    }


    private Hall getHall(int id) {
        return hallRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
    }

    private HallDetail toHallDetail(Hall hall) {
        return HallDetail.builder()
                .name(hall.getName())
                .id(hall.getId())
                .venueId(String.valueOf(hall.getVenue().getId()))
                .capacity(hall.getCapacity())
                .floorNumber(hall.getFloorNumber())
                .description(hall.getDescription())
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

    public String updateHallAvailablityByShift(HallShift shift,String id,HallStatus status)
    {
        HallAvailability hallAvailability = getHallAvailability(id);
        hallAvailability.setShift(shift);
        hallAvailability.setStatus(status);
        hallAvailabilityRepo.save(hallAvailability);
        return SUCCESS_MESSAGE;
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

    private HallBooking toBooking(BookingRequest request, Menu menu, HallAvailability hallAvailability, AppUser user) {
        HallBooking hallBooking = new HallBooking();
        hallBooking.setHallAvailability(hallAvailability);
        hallBooking.setMenu(menu);
        hallBooking.setPrice(menu.getPrice() * request.getNumberOfGuests());
        hallBooking.setNumberOfGuests(request.getNumberOfGuests());
        hallBooking.setRequestedDate(LocalDate.now());
        hallBooking.setRequestedTime(LocalTime.now());
        hallBooking.setStatus(BookingStatus.PENDING);
        hallBooking.setBookedForDate(hallAvailability.getDate());
        hallBooking.setEventType(EventType.valueOf(request.getEventType()));
        hallBooking.setUser(user);
        return hallBooking;
    }

    private HallBooking getHallBooking(int id) {
        return hallBookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.BOOKING_NOT_FOUND));
    }

    private MenuDetail toMenuDetail(Menu menu) {
        List<MenuItem> foodMenus = foodMenuRepo.findByMenu(menu);
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

    private List<FoodDetail> toFoodDetail(List<MenuItem> foodMenus) {
        List<FoodDetail> foodDetails = new ArrayList<>();
        for (MenuItem foodMenu : foodMenus
        ) {
            foodDetails.add(FoodDetail.builder().
                    foodSubCategory(foodMenu.getFood().getSubCategory().getName()).
                    foodCategory(foodMenu.getFood().getSubCategory().getFoodCategory().getName()).
                    name(foodMenu.getFood().getName()).
                    description(foodMenu.getFood().getDescription()).
                    id(String.valueOf(foodMenu.getFood().getId())).
                    status(String.valueOf(foodMenu.getFood().getStatus())).
                    venueId(String.valueOf(foodMenu.getFood().getVenue().getId())).
                    build());
        }
        return foodDetails;
    }
    private List<FoodDetail> toFoodDetails(List<Food> foods) {
        List<FoodDetail> foodDetails = new ArrayList<>();
        for (Food food : foods
        ) {
            foodDetails.add(FoodDetail.builder().
                    foodSubCategory(food.getSubCategory().getName()).
                    foodCategory(food.getSubCategory().getFoodCategory().getName()).
                    name(food.getName()).
                    description(food.getDescription()).
                    id(String.valueOf(food.getId())).
                    status(String.valueOf(food.getStatus())).
                    venueId(String.valueOf(food.getVenue().getId())).
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
    private void toBookingMenuItem(List<String> foodIds,HallBooking hallBooking) {
        for (String id : foodIds){
            BookingMenuItem bookingMenuItem= new BookingMenuItem();
            bookingMenuItem.setBooking(hallBooking);
            bookingMenuItem.setFood(toFood(id));

            bookingMenuItemRepo.save(bookingMenuItem);
        }
    }

    private Food toFood(String id) {
        return foodRepo.findById(Integer.parseInt(id)).orElseThrow(() -> new CustomException(CustomException.Type.FOOD_NOT_FOUND));
    }
    private void sendBookingDateRequestEmail(Venue venue, AppUser customer, BookingDateRequest bookingDateRequest) {
        buildBookingDateRequestEmailCustomer(customer,venue,bookingDateRequest);
        buildBookingDateRequestEmailVenue(customer,venue,bookingDateRequest);
        buildBookingDateRequestEmailAdmin(customer,venue,bookingDateRequest);
    }

    private void buildBookingDateRequestEmailAdmin(AppUser customer, Venue venue,BookingDateRequest bookingDateRequest) {
        emailService.sendEmail("bandobastanepal@gmail.com","A booking Request has been made",buildEmail(bookingDateRequest,customer,venue));
    }

    private void buildBookingDateRequestEmailVenue(AppUser customer, Venue venue, BookingDateRequest bookingDateRequest) {
        emailService.sendEmail(venue.getEmail(), "A Booking Request has been made", buildEmailVenue(bookingDateRequest,customer,venue));
    }

    private void buildBookingDateRequestEmailCustomer(AppUser customer, Venue venue, BookingDateRequest bookingDateRequest) {
        emailService.sendEmail(customer.getEmail(),"Booking Request Update" ,buildEmailCustomer(customer,venue,bookingDateRequest));
    }

    private String buildEmailCustomer(AppUser customer, Venue venue, BookingDateRequest bookingDateRequest) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
                "<tbody><tr>" +
                "<td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">" +
                "<tbody><tr>" +
                "<td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">" +
                "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td style=\"padding-left:10px\"></td>" +
                "<td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">" +
                "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Thank you for booking</span>" +
                "</td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td width=\"10\" height=\"10\" valign=\"middle\"></td>" +
                "<td>" +
                "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\" height=\"10\"></td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "<td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + customer.getFirstName() + ",</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Thank you for requesting booking with Bandobasta Nepal. Below are your booking request details:</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Venue:</strong> " + venue.getVenueName() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Requested Date:</strong> " + bookingDateRequest.getRequestedDate() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Number of Guests:</strong> " + bookingDateRequest.getNumberOfGuests() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Time slot:</strong> " + bookingDateRequest.getTimeSlot() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">The venue will contact you soon with more details.</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">We look forward to serving you!</p>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "</tbody></table>" +
                "</div>";
    }

    private String buildEmail(String bookingId, String name,String bookedForDate, String venueName, String numberOfGuests, String hallName) {

        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
                "<tbody><tr>" +
                "<td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">" +
                "<tbody><tr>" +
                "<td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">" +
                "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td style=\"padding-left:10px\"></td>" +
                "<td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">" +
                "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Thank you for booking</span>" +
                "</td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td width=\"10\" height=\"10\" valign=\"middle\"></td>" +
                "<td>" +
                "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\" height=\"10\"></td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "<td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Thank you for booking with Bandobasta Nepal. Below are your booking details:</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Booking Id:</strong> " + bookingId + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Venue:</strong> " + venueName + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Hall:</strong> " + hallName + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Booking Date:</strong> " + bookedForDate + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Number of Guests:</strong> " + numberOfGuests + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">The venue will contact you soon with more details.</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">We look forward to serving you!</p>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "</tbody></table>" +
                "</div>";
    }

    private String buildEmail(BookingDateRequest bookingDateRequest,AppUser customer, Venue venue){
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
                "<tbody><tr>" +
                "<td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">" +
                "<tbody><tr>" +
                "<td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">" +
                "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td style=\"padding-left:10px\"></td>" +
                "<td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">" +
                "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Thank you for booking</span>" +
                "</td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td width=\"10\" height=\"10\" valign=\"middle\"></td>" +
                "<td>" +
                "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\" height=\"10\"></td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "<td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + "Bandobasta Nepal" + ",</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">A booking request has been made. Below are the booking request details:</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Customer:</strong> " + customer.getFirstName() + " "+customer.getMiddleName()+ " " +customer.getLastName() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Customer email:</strong> " + customer.getEmail() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Customer Phone number:</strong> " + customer.getPhoneNumber() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Venue:</strong> " + venue.getVenueName() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Requested Date:</strong> " + bookingDateRequest.getRequestedDate() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Number of Guests:</strong> " + bookingDateRequest.getNumberOfGuests() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Time slot:</strong> " + bookingDateRequest.getTimeSlot() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Customer's Message:</strong> " + bookingDateRequest.getMessage() + "</p>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "</tbody></table>" +
                "</div>";
    }

    private String buildEmailVenue(BookingDateRequest bookingDateRequest, AppUser customer, Venue venue) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
                "<tbody><tr>" +
                "<td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">" +
                "<tbody><tr>" +
                "<td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">" +
                "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td style=\"padding-left:10px\"></td>" +
                "<td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">" +
                "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Thank you for booking</span>" +
                "</td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td width=\"10\" height=\"10\" valign=\"middle\"></td>" +
                "<td>" +
                "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\" height=\"10\"></td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "<td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + venue.getVenueName() + ",</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">A booking request has been made. Below are the booking request details:</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Customer:</strong> " + customer.getFirstName() + " "+customer.getMiddleName()+ " " +customer.getLastName() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Customer email:</strong> " + customer.getEmail() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Venue:</strong> " + venue.getVenueName() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Requested Date:</strong> " + bookingDateRequest.getRequestedDate() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Number of Guests:</strong> " + bookingDateRequest.getNumberOfGuests() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Time slot:</strong> " + bookingDateRequest.getTimeSlot() + "</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">You can contact the customer via email.</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">We look forward to serving you!</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><strong>Customer's Message:</strong> " + bookingDateRequest.getMessage() + "</p>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "</tbody></table>" +
                "</div>";
    }
}