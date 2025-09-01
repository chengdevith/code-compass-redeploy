package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Badge;
import kh.edu.istad.codecompass.dto.badge.BadgeRequest;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BadgeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromUpdateRequestToEntity(BadgeRequest badgeRequest, @MappingTarget Badge entity);

    Badge fromRequestToEntity(BadgeRequest badgeRequest);
    BadgesResponse toBadgeResponse(Badge badge);
}
