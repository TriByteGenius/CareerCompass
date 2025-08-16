package com.tribytegenius.CareerCompass.UserService.repository;


import com.tribytegenius.CareerCompass.UserService.model.AppRole;
import com.tribytegenius.CareerCompass.UserService.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
