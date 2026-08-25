package com.nexora.service.impl;

import com.nexora.dto.request.CustomerRequest;
import com.nexora.dto.response.CustomerResponse;
import com.nexora.entity.Customer;
import com.nexora.exception.DuplicateEmailException;
import com.nexora.mapper.CustomerMapper;
import com.nexora.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
 * EN: Enables Mockito support for JUnit 5.
 * TR: JUnit 5 testlerinde Mockito desteğini aktif eder.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    /*
     * EN: Mock repository. No real database connection is used.
     * TR: Sahte repository. Gerçek veritabanı bağlantısı kullanılmaz.
     */
    @Mock
    private CustomerRepository customerRepository;

    /*
     * EN: Mock mapper used to isolate the service layer.
     * TR: Service katmanını izole etmek için kullanılan sahte mapper.
     */
    @Mock
    private CustomerMapper customerMapper;

    /*
     * EN: Injects the mocks above into CustomerServiceImpl.
     * TR: Yukarıdaki mock nesnelerini CustomerServiceImpl içine enjekte eder.
     */
    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequest customerRequest;
    private Customer customer;
    private CustomerResponse customerResponse;

    /*
     * EN: Creates fresh test data before each test.
     * TR: Her testten önce test verilerini yeniden hazırlar.
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
     * EN: Tests successful customer creation.
     * TR: Başarılı müşteri oluşturma senaryosunu test eder.
     */
    @Test
    void shouldCreateCustomerSuccessfully() {

        // ARRANGE / HAZIRLIK
        // EN: Simulate that the email does not already exist.
        // TR: Email adresinin sistemde bulunmadığını simüle eder.
        when(customerRepository.existsByEmailIgnoreCase(customerRequest.getEmail()))
                .thenReturn(false);

        when(customerMapper.toEntity(customerRequest))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(customerResponse);

        // ACT / ÇALIŞTIR
        // EN: Execute the method under test.
        // TR: Test edilen service metodunu çalıştır.
        CustomerResponse result =
                customerService.createCustomer(customerRequest);

        // ASSERT / DOĞRULA
        // EN: Verify returned customer information.
        // TR: Dönen müşteri bilgilerinin doğru olduğunu kontrol et.
        assertEquals(1L, result.getId());
        assertEquals("Büşra", result.getFirstName());
        assertEquals("busra@test.com", result.getEmail());

        // VERIFY / ETKİLEŞİM KONTROLÜ
        verify(customerRepository)
                .existsByEmailIgnoreCase(customerRequest.getEmail());

        verify(customerRepository)
                .save(customer);

        verify(customerMapper)
                .toResponse(customer);
    }

    /*
     * EN: Tests duplicate email protection.
     * TR: Aynı email adresiyle müşteri oluşturulmasını engelleyen senaryoyu test eder.
     */
    @Test
    void shouldThrowDuplicateEmailExceptionWhenEmailAlreadyExists() {

        // ARRANGE / HAZIRLIK
        when(customerRepository.existsByEmailIgnoreCase(customerRequest.getEmail()))
                .thenReturn(true);

        // ACT + ASSERT / ÇALIŞTIR + DOĞRULA
        assertThrows(
                DuplicateEmailException.class,
                () -> customerService.createCustomer(customerRequest)
        );

        // VERIFY / ETKİLEŞİM KONTROLÜ
        verify(customerRepository)
                .existsByEmailIgnoreCase(customerRequest.getEmail());

        // EN: save() must never be called for a duplicate email.
        // TR: Duplicate email durumunda save() kesinlikle çağrılmamalıdır.
        verify(customerRepository, never())
                .save(any(Customer.class));
    }
}