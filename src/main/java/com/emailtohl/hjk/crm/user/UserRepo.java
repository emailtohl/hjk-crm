package com.emailtohl.hjk.crm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.User;

@Repository
interface UserRepo extends JpaRepository<User, Long>, UserRepoCust {
	User findByEmail(String email);
}
