package fptu.fcharity.dao;

import fptu.fcharity.entity.Request;

import java.util.List;
import java.util.UUID;

public interface RequestDAO {
    List<Request> getAll();
    Request getById(UUID id);
    Request save(Request request);
    Request update(Request request);
    void delete(UUID id);
}
