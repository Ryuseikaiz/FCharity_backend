package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.ArticleDTO;
import fptu.fcharity.entity.Article;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrganizationMapper.class, UserMapper.class})
public interface ArticleMapper {
    ArticleDTO toDTO(Article entity);
    Article toEntity(ArticleDTO dto);
}
