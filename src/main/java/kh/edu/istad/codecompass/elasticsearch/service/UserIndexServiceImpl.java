//package kh.edu.istad.codecompass.elasticsearch.service;
//
//import kh.edu.istad.codecompass.domain.User;
//import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
//import kh.edu.istad.codecompass.elasticsearch.repository.UserElasticsearchRepository;
//import kh.edu.istad.codecompass.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserIndexServiceImpl implements UserIndexService {
//
//    private final UserRepository userRepository; // JPA Repo
//    private final UserElasticsearchRepository userElasticsearchRepository; //ES Repo
//
//    @Override
//    @Transactional
//    public User save(User user) {
//
//        User savedUser;
//
//        if (user.getId() != null) {
//            // Existing user → fetch from DB
//            savedUser = userRepository.findById(user.getId())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            // Update fields
//            savedUser.setUsername(user.getUsername());
//            savedUser.setEmail(user.getEmail());
//            savedUser.setGender(user.getGender());
//            savedUser.setLocation(user.getLocation());
//            savedUser.setGithub(user.getGithub());
//            savedUser.setLinkedin(user.getLinkedin());
//            savedUser.setImage_url(user.getImage_url());
//            savedUser.setLevel(user.getLevel());
//            savedUser.setRank(user.getRank());
//            savedUser.setTotal_problems_solved(user.getTotal_problems_solved());
//        } else {
//            // New user
//            savedUser = user;
//            savedUser.setIsDeleted(false);
//        }
//
//        // Save in Postgres
//        User saved  = userRepository.save(savedUser);
//
//        UserIndex index = UserIndex.builder()
//                .id(saved.getId().toString())
//                .username(saved.getUsername())
//                .email(saved.getEmail())
//                .gender(saved.getGender().name())
//                .location(saved.getLocation())
//                .github(saved.getGithub())
//                .linkedin(saved.getLinkedin())
//                .imageUrl(saved.getImage_url())
//                .level(saved.getLevel().name())
//                .rank(saved.getRank())
//                .totalProblemsSolved(saved.getTotal_problems_solved())
//                .build();
//
//        userElasticsearchRepository.save(index);
//
//        return saved;
//    }
//
//    @Override
//    public List<UserIndex> searchUsers(String keyword) {
//        return userElasticsearchRepository.findByUsernameContaining(keyword);
//    }
//}
