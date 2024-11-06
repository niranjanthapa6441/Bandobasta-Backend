package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Model.Menu;
import com.example.BookEatNepal.Payload.DTO.MenuDTO;
import com.example.BookEatNepal.Payload.DTO.MenuDetail;
import com.example.BookEatNepal.Payload.Request.MenuItemSelectionRangeRequest;
import com.example.BookEatNepal.Payload.Request.MenuRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface MenuService {
    String save(MenuRequest request);

    String delete(int id);

    MenuDTO findMenuByVenue(String venueId, String menuType, int page, int size);

    MenuDetail findById(int id);

    String update(MenuRequest request, int id );
}
