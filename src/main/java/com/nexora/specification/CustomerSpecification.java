package com.nexora.specification;

import com.nexora.dto.request.CustomerFilterRequest;
import com.nexora.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CustomerSpecification {

    private CustomerSpecification() {
    }

    public static Specification<Customer> withFilters(
            CustomerFilterRequest filter
    ) {

        List<Specification<Customer>> specifications = new ArrayList<>();

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            specifications.add(search(filter.getSearch()));
        }

        if (filter.getFirstName() != null && !filter.getFirstName().isBlank()) {
            specifications.add(
                    containsIgnoreCase("firstName", filter.getFirstName())
            );
        }

        if (filter.getLastName() != null && !filter.getLastName().isBlank()) {
            specifications.add(
                    containsIgnoreCase("lastName", filter.getLastName())
            );
        }

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            specifications.add(
                    containsIgnoreCase("email", filter.getEmail())
            );
        }

        if (filter.getPhone() != null && !filter.getPhone().isBlank()) {
            specifications.add(
                    containsIgnoreCase("phone", filter.getPhone())
            );
        }

        return Specification.allOf(specifications);
    }

    private static Specification<Customer> search(String search) {

        return (root, query, cb) -> {

            String value =
                    "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(
                            cb.lower(root.get("firstName")),
                            value
                    ),
                    cb.like(
                            cb.lower(root.get("lastName")),
                            value
                    ),
                    cb.like(
                            cb.lower(root.get("email")),
                            value
                    ),
                    cb.like(
                            cb.lower(root.get("phone")),
                            value
                    )
            );
        };
    }

    private static Specification<Customer> containsIgnoreCase(
            String field,
            String value
    ) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get(field)),
                        "%" + value.trim().toLowerCase() + "%"
                );
    }
}