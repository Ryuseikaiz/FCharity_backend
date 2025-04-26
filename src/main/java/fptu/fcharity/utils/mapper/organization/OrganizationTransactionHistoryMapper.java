package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.OrganizationTransactionHistoryDTO;
import fptu.fcharity.entity.OrganizationTransactionHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface OrganizationTransactionHistoryMapper {
    OrganizationTransactionHistoryDTO toDTO(OrganizationTransactionHistory entity);
    OrganizationTransactionHistory toEntity(OrganizationTransactionHistoryDTO dto);
}
