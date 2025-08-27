package kh.edu.istad.codecompass.elasticsearch.service;

import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;

import java.util.List;

/**
 * @author Cheng Devith
 */

public interface UserIndexService {
    User save(User user);
    List<UserIndex> searchUsers(String keyword);
}
