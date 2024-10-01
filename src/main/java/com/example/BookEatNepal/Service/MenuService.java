package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.DTO.*;
import com.example.BookEatNepal.Request.MenuRequest;
import org.springframework.stereotype.Service;


@Service
public interface MenuService {
    String save(MenuRequest request);

    String delete(int id);


    MenuDTO findAll(String venueId, int page, int size);

    MenuDetail findById(int id);

    String update(MenuRequest request, int id );
}
