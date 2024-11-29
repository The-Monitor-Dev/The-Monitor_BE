package the_monitor.application.service;

import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.request.ClientUpdateRequest;
import the_monitor.application.dto.response.ClientGetResponse;
import the_monitor.application.dto.response.ClientResponse;
import the_monitor.domain.model.Client;

import java.util.List;

public interface ClientService {

    ClientResponse createClient(ClientRequest request, MultipartFile logo);

    List<ClientResponse> getClientsByAccountId();

    Client findClientById(Long clientId);

    ClientGetResponse getClient();

    String deleteClientById(Long clientId);

    String updateClient(Long clientId, ClientUpdateRequest request, MultipartFile logo);

    List<ClientResponse> searchClient(String searchText);
}