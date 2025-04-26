package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.ToOrganizationDonationDTO;
import fptu.fcharity.entity.ToOrganizationDonation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface ToOrganizationDonationMapper {
    ToOrganizationDonationDTO toDTO(ToOrganizationDonation entity);
    ToOrganizationDonation toEntity(ToOrganizationDonationDTO dto);
}
