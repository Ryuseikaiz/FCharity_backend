package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.ArticleLikeDTO;
import fptu.fcharity.entity.ArticleLike;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ArticleMapper.class, UserMapper.class})
public interface ArticleLikeMapper {
    ArticleLikeDTO toDTO(ArticleLike entity);
    ArticleLike toEntity(ArticleLikeDTO dto);
}
