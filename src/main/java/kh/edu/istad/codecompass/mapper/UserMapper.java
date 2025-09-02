package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUserPartially(
            UpdateUserProfileRequest request,
            @MappingTarget User user);

    UserResponse toUserResponse(User user);


}
