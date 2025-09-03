package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Badge;
import kh.edu.istad.codecompass.dto.badge.request.BadgeRequest;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BadgeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromUpdateRequestToEntity(BadgeRequest badgeRequest, @MappingTarget Badge entity);

    @Mapping(target = "author", ignore = true)
    Badge fromRequestToEntity(BadgeRequest badgeRequest);

    BadgesResponse toBadgeResponse(Badge badge);
}
