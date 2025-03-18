package fptu.fcharity.utils.mapper;


public interface BaseMapper<E, D> {
    D toDTO(E entity);
    E toEntity(D dto);
}
