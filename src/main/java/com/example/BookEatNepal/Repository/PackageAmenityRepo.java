package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Package;
import com.example.BookEatNepal.Model.PackageAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageAmenityRepo extends JpaRepository<PackageAmenity, Integer> {
    List<PackageAmenity> findByaPackage(Package aPackage);
}
