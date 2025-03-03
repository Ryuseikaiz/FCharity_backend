package fptu.fcharity.service.request;

import fptu.fcharity.dao.RequestDAO;
import fptu.fcharity.entity.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestDAO requestDAO;

    @Autowired
    public RequestServiceImpl(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }

    @Override
    public List<Request> getAll() {
        return requestDAO.getAll();
    }

    @Override
    public Request getById(UUID id) {
        return requestDAO.getById(id);
    }

    @Override
    @Transactional
    public Request save(Request request) {
        return requestDAO.save(request);
    }

    @Override
    @Transactional
    public Request update(Request request) {
        return requestDAO.update(request);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        requestDAO.delete(id);
    }
}
