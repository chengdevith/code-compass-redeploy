package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.user.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.user.UserProfileResponse;
import kh.edu.istad.codecompass.dto.user.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUserPartially(UpdateUserProfileRequest request, @MappingTarget User user);

    UserResponse toUserResponse(User user);

//    UserProfileResponse toUserProfileResponse(User user);
}
