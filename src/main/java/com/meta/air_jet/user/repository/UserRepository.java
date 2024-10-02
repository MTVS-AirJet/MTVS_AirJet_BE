package com.meta.air_jet.user.repository;

import com.meta.air_jet.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
