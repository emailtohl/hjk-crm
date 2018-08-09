package com.emailtohl.hjk.crm.user;

import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.SearchRepository;

@Repository
class UserRepoImpl extends SearchRepository<User, Long> implements UserRepoCust {


}
