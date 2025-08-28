package kh.edu.istad.codecompass.mapper;


import kh.edu.istad.codecompass.domain.Package;
import kh.edu.istad.codecompass.dto.packageDTO.PackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.PackageResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PackageMapper {

    PackageResponse mappackageToPackageResponse(Package pack);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePackagePartially(PackageRequest packageRequest, @MappingTarget Package pack);
}
