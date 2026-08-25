package com.nexora.service.impl;

import com.nexora.dto.request.CustomerFilterRequest;
import com.nexora.dto.request.CustomerRequest;
import com.nexora.dto.response.CustomerResponse;
import com.nexora.entity.Customer;
import com.nexora.exception.CustomerNotFoundException;
import com.nexora.exception.DuplicateEmailException;
import com.nexora.mapper.CustomerMapper;
import com.nexora.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
 * Enables Mockito support for JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    /*
     * Mock repository. No real database connection is used.
     */
    @Mock
    private CustomerRepository customerRepository;

    /*
     * Mock mapper used to isolate the service layer.
     */
    @Mock
    private CustomerMapper customerMapper;

    /*
     * Injects the mocks above into CustomerServiceImpl.
     */
    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequest customerRequest;
    private Customer customer;
    private CustomerResponse customerResponse;

    /*
     * Creates fresh test data before each test.
     */
    @BeforeEach
    void setUp() {

        customerRequest = new CustomerRequest();
        customerRequest.setFirstName("Büşra");
        customerRequest.setLastName("İshakoğlu");
        customerRequest.setEmail("busra@test.com");
        customerRequest.setPhone("5551112233");

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Büşra");
        customer.setLastName("İshakoğlu");
        customer.setEmail("busra@test.com");
        customer.setPhone("5551112233");

        customerResponse = CustomerResponse.builder()
                .id(1L)
                .firstName("Büşra")
                .lastName("İshakoğlu")
                .email("busra@test.com")
                .phone("5551112233")
                .build();
    }

    /*
     * Tests successful customer creation.
     */
    @Test
    void shouldCreateCustomerSuccessfully() {

        // EN: Simulate that the email does not already exist.
        when(customerRepository.existsByEmailIgnoreCase(customerRequest.getEmail()))
                .thenReturn(false);

        when(customerMapper.toEntity(customerRequest))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(customerResponse);

        // EN: Execute the method under test.
        CustomerResponse result =
                customerService.createCustomer(customerRequest);

        // Verify returned customer information.
        assertEquals(1L, result.getId());
        assertEquals("Büşra", result.getFirstName());
        assertEquals("busra@test.com", result.getEmail());

        verify(customerRepository)
                .existsByEmailIgnoreCase(customerRequest.getEmail());

        verify(customerRepository)
                .save(customer);

        verify(customerMapper)
                .toResponse(customer);
    }

    /*
     * Tests duplicate email protection.
     */
    @Test
    void shouldThrowDuplicateEmailExceptionWhenEmailAlreadyExists() {

        when(customerRepository.existsByEmailIgnoreCase(customerRequest.getEmail()))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> customerService.createCustomer(customerRequest)
        );

        verify(customerRepository)
                .existsByEmailIgnoreCase(customerRequest.getEmail());

        // EN: save() must never be called for a duplicate email.
        verify(customerRepository, never())
                .save(any(Customer.class));
    }


    @Test
    void shouldReturnCustomerWhenCustomerExists() {

        // simulate an existing customer.
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponse(customer))
                .thenReturn(customerResponse);

        CustomerResponse result = customerService.getCustomerById(1L);


        assertEquals(1L, result.getId());
        assertEquals("Büşra", result.getFirstName());
        assertEquals("busra@test.com", result.getEmail());

        //Verify repository and mapper interactions
        verify(customerRepository).findById(1L);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        //simulate a missing center
        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        //assert
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(999L)
        );

        //verify that o mapping occurs when the customer does not exist
        verify(customerRepository).findById(999L);
        verify(customerMapper, never())
                .toResponse(any(Customer.class));
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        //simulate an existing customer
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(customerResponse);

        CustomerResponse result = customerService
                .updateCustomer(1L, customerRequest);

        assertEquals(1L, result.getId());
        assertEquals("Büşra", result.getFirstName());
        assertEquals("busra@test.com", result.getEmail());

        //verify repository and mapper interactions
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenUpdatingMissingCustomer() {

        //simulte a missing customer
        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(999L, customerRequest));

        //verify that persistence is not attempted
        verify(customerRepository).findById(999L);
        verify(customerRepository, never())
                .save(any(Customer.class));
        verify(customerMapper, never())
                .toResponse(any(Customer.class));
    }

    /*Test that an existing customer can be successfully.
    * Verifies that the service delegates the delete operation
    * to CustomerRepository with the correct customerID
    */
    @Test
    void shouldDeleteCustomerSuccessfully() {
        Long customerId = 1L;

        when(customerRepository.findById(customerId))
        .thenReturn(Optional.of(customer));

        //Calls the service method that performs the delete operation
        customerService.deleteCustomer(customerId);

        verify(customerRepository,times(1))
                .findById(customerId);

        verify(customerRepository, times(1))
                .delete(customer);
    }

    // Tests that an existing customer can be retrieved successfully by ID

    @Test
    void shouldGetCustomerByIdSuccessfully() {

        Long customerId = 1L;

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponse(customer))
                .thenReturn(customerResponse);

        CustomerResponse result = customerService.getCustomerById(customerId);

        assertNotNull(result);
        assertEquals(customerResponse.getId(), result.getId());
        assertEquals(customerResponse.getFirstName(), result.getFirstName());
        assertEquals(customerResponse.getEmail(), result.getEmail());

        verify(customerMapper, times(1))
                .toResponse(customer);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenGettingMissingCustomer() {
        Long customerId = 999L;


        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(customerId));

        verify(customerRepository, times(1))
                .findById(customerId);

        verify(customerMapper, never())
                .toResponse(any(Customer.class));
    }

    // Tests that customers are returned successfully with pagination
    @Test
    void shouldGetAllCustomerWithPaginationSuccessfully() {

        CustomerFilterRequest filter = new CustomerFilterRequest();

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        List<Customer> customers = List.of(customer);

        Page<Customer> customerPage =
                new PageImpl<>(customers, pageable, customers.size());

        when(customerRepository.findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        )).thenReturn(customerPage);

        when(customerMapper.toResponse(customer))
                .thenReturn(customerResponse);

        Page<CustomerResponse> result =
                customerService.getAllCustomers(filter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("Büşra", result.getContent().get(0).getFirstName());

        verify(customerRepository, times(1))
                .findAll(
                        ArgumentMatchers.<Specification<Customer>>any(),
                        eq(pageable)
                );

        verify(customerMapper, times(1))
                .toResponse(customer);
    }


    // Test dynamic customer filtering by first name together with pagination
    @Test
    void shouldGetAllCustomerWithFirstNameFilterq() {
        CustomerFilterRequest filter = new CustomerFilterRequest();
        filter.setFirstName("Büşra");

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        List<Customer> customers = List.of(customer);
        Page<Customer> customerPage =
                new PageImpl<>(customers, pageable, customers.size());

        //Simulates repository result for a dynamically created Specificstion
        when(customerRepository.findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        )).thenReturn( customerPage);

        when(customerMapper.toResponse(customer))
        .thenReturn(customerResponse);

        Page<CustomerResponse> result =
                customerService.getAllCustomers(filter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(
                "Büşra",
                result.getContent().get(0).getFirstName()
        );

        verify(customerRepository, times(1))
        .findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        );

        verify(customerMapper, times(1))
        .toResponse(customer);

    }
    // Tests customer filtering with multiple criteria together with pagination
    @Test
    void shouldGetAllCustomersWithMultipleFiltersSuccessfully() {
        CustomerFilterRequest filter = new CustomerFilterRequest();

        filter.setFirstName("Büşra");
        filter.setEmail("busra@test.com");

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        List<Customer> customers = List.of(customer);

        Page<Customer> customerPage =
                new PageImpl<>(customers, pageable, customers.size());

        when(customerRepository.findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        )).thenReturn( customerPage);

        when(customerMapper.toResponse(customer))
        .thenReturn(customerResponse);

        Page<CustomerResponse> result =
                customerService.getAllCustomers(filter, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        CustomerResponse customerResponse = result.getContent().get(0);

        assertEquals("Büşra", customerResponse.getFirstName());
        assertEquals("busra@test.com", customerResponse.getEmail());

        verify(customerRepository, times(1))
        .findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        );

        verify(customerMapper, times(1))
        .toResponse(customer);
    }

    //Tests that an empty page is returned when no customer matches the given filter
    @Test
    void shouldReturnEmptyPageWhenNoCustomerMatchesFilter() {

        CustomerFilterRequest filter = new CustomerFilterRequest();

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<Customer> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        when(customerRepository.findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        )).thenReturn(emptyPage);

        Page<CustomerResponse> result =
                customerService.getAllCustomers(filter, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(customerRepository, times(1))
                .findAll(
                        ArgumentMatchers.<Specification<Customer>>any(),
                        eq(pageable)
                );

        verify(customerMapper, never())
                .toResponse(any(Customer.class));
    }

    //Tests that customers can be returned
    // successfully when a dynamic firstName filter is provided

    @Test
    void shouldReturnFilteredCustomersSuccessfully() {
        CustomerFilterRequest filter = new CustomerFilterRequest();
        filter.setFirstName("Büşra");

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        List<Customer> customers = List.of(customer);
        Page<Customer> customerPage =
                new PageImpl<>(customers, pageable, customers.size());

        //Simulates repository response for a dynamically generated Specification
        when(customerRepository.findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        )).thenReturn( customerPage);

        when(customerMapper.toResponse(customer))
        .thenReturn(customerResponse);

        Page<CustomerResponse> result =
                customerService.getAllCustomers(filter, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Büşra", result.getContent().get(0).getFirstName());
        assertEquals("busra@test.com", result.getContent().get(0).getEmail());

        verify(customerRepository, times(1))
        .findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        );
        verify(customerMapper, times(1))
        .toResponse(customer);
    }

}