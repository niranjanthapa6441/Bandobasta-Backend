package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Package;
import com.example.BookEatNepal.Model.PackageMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageMenuRepo extends JpaRepository<PackageMenu, Integer> {
    PackageMenu findByaPackage(Package aPackage);
}
