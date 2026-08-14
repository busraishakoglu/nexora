package com.nexora.service;

import com.nexora.dto.request.CustomerFilterRequest;
import com.nexora.dto.request.CustomerRequest;
import com.nexora.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    Page<CustomerResponse> getAllCustomers(CustomerFilterRequest filter, Pageable pageable);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    void deleteCustomer(Long id);
}