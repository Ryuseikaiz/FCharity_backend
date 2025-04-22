package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.WalletDTO;
import fptu.fcharity.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletDTO toDTO(Wallet entity);
    Wallet toEntity(WalletDTO dto);
}
