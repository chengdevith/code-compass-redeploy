package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Hint;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.domain.UserHint;
import kh.edu.istad.codecompass.repository.HintRepository;
import kh.edu.istad.codecompass.repository.UserHintRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.HintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@RequiredArgsConstructor
@Service
@Slf4j
public class HintServiceImpl implements HintService {

    private final HintRepository hintRepository;
    private final UserRepository userRepository;
    private final UserHintRepository userHintRepository;

    @Override
    public Boolean unlockHint(long hintId, String username) {

        Hint hint = hintRepository.findById(hintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hint does not exist"));

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        UserHint userHint = userHintRepository.findByUserAndHint(user, hint)
                .orElse(new UserHint(user, hint, false));

        if (userHint.getIsUnlocked())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The hint is already unlocked");


        int coinsAfterUnlock = user.getCoin() - 10;
        if (coinsAfterUnlock < 0) {
            return false; // Not enough coins
        }

        user.setCoin(coinsAfterUnlock);
        userHint.setIsUnlocked(true);

        userRepository.save(user);
        userHintRepository.save(userHint);

        return true;
    }
}

