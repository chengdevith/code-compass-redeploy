package kh.edu.istad.codecompass.elasticsearch.service;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.UserElasticsearchRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserIndexServiceImpl implements UserIndexService {

    private final UserRepository userRepository; // JPA Repo
    private final UserElasticsearchRepository userElasticsearchRepository; //ES Repo

    @Override
    public List<UserIndex> searchUsers(String keyword) {
        return userElasticsearchRepository.findByUsernameContaining(keyword);
    }
}
