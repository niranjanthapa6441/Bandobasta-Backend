package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Package;
import com.example.BookEatNepal.Model.PackageHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageHallRepo extends JpaRepository<PackageHall, Integer> {
    PackageHall findByaPackage(Package aPackage);
}
