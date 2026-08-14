package com.nexora.service.impl;

import com.nexora.dto.request.CustomerFilterRequest;
import com.nexora.dto.request.CustomerRequest;
import com.nexora.dto.response.CustomerResponse;
import com.nexora.entity.Customer;
import com.nexora.exception.CustomerNotFoundException;
import com.nexora.mapper.CustomerMapper;
import com.nexora.repository.CustomerRepository;
import com.nexora.service.CustomerService;
import com.nexora.specification.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.nexora.exception.DuplicateEmailException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;


    @Override
    public Page<CustomerResponse> getAllCustomers(
            CustomerFilterRequest filter,
            Pageable pageable
    ) {

        return customerRepository
                .findAll(
                        CustomerSpecification.withFilters(filter),
                        pageable
                )
                .map(customerMapper::toResponse);
    }
   /* public Page<CustomerResponse> getAllCustomers(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return customerRepository.findAll(pageable)
                    .map(customerMapper::toResponse);
        }

        return customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search,
                        search,
                        search,
                        pageable
                )
                .map(customerMapper::toResponse);
    }*/


    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(Long id){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(id)
                );

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void  deleteCustomer(Long id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(
                        ()-> new CustomerNotFoundException(id)
                );
        customerRepository.delete(customer);
    }

}