package com.nexora.controller;


import com.nexora.dto.request.CustomerFilterRequest;
import com.nexora.dto.request.CustomerRequest;
import com.nexora.dto.response.CustomerResponse;
import com.nexora.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(
        name = "Customer Management",
        description = "Customer CRUD, search, filtering, pagination and sorting operations"
)
public class CustomerController {


    private final CustomerService customerService;

    @Operation(
            summary = "List customers",
            description = "Returns customers with dynamic filtering, search, pagination and sorting support."
    )
    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @ParameterObject
            @ModelAttribute CustomerFilterRequest filter,

            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {

        return ResponseEntity.ok(
                customerService.getAllCustomers(filter, pageable)
        );
    }

    @Operation(
            summary = "Create customer",
            description = "Creates a new customer after validation and duplicate email checks."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Returns a customer using the given unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(Long id){
        return ResponseEntity.ok(
                customerService.getCustomerById(id)
        );
    }

    @Operation(
            summary = "Update customer",
            description = "Updates an existing customer's information."
    )
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request
    ) {

        return ResponseEntity.ok(
                customerService.updateCustomer(id, request)
        );
    }

    @Operation(
            summary = "Delete customer",
            description = "Deletes an existing customer."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id){
            customerService.deleteCustomer(id);

            return ResponseEntity.noContent().build();
    }
}
