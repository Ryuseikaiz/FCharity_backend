package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WalletMapper.class})
public interface UserMapper {
    @Mapping(source = "walletAddress", target = "walletAddress")
    UserDTO toDTO(User entity);

    @Mapping(source = "walletAddress", target = "walletAddress")
    User toEntity(UserDTO dto);
}
