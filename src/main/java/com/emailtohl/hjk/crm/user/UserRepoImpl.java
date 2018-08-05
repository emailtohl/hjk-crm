package com.emailtohl.hjk.crm.user;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.SearchInterface;
import com.github.emailtohl.lib.jpa.SearchRepository;

class UserRepoImpl extends SearchRepository<User, Long> implements SearchInterface<User, Long> {

}
