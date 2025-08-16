package com.tribytegenius.CareerCompass.UserJobService.repository;

import com.tribytegenius.CareerCompass.UserJobService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
