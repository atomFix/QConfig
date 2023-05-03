package com.qc.server.discovery;

import com.qc.common.entity.ServiceDTO;

import java.util.List;

public interface DiscoveryService {

  /**
   * @param serviceId the service id
   * @return the service instance list for the specified service id, or an empty list if no service
   * instance available
   */
  List<ServiceDTO> getServiceInstances(String serviceId);
}
