package com.nexora.specification;

import com.nexora.dto.request.CustomerFilterRequest;
import com.nexora.entity.Customer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class CustomerSpecificationTest {

    /*Tests that the firstName filter creates a case-insensitive LIKE predicate correctly*/

    @Test
    void shouldCreateFirstNamePredicateSuccessfully() {
        CustomerFilterRequest customerFilterRequest = new CustomerFilterRequest();
        customerFilterRequest.setFirstName("Büşra");

        Root<Customer> root = mock(Root.class);
        CriteriaQuery<?> criteriaQuery = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<String> firstNamePath = mock(Path.class);
        Expression<String> lowerExpression = mock(Expression.class);
        Predicate predicate = mock(Predicate.class);

        //Simulates access to the Customer.firstName field
        when(root.<String>get("firstName")).thenReturn(firstNamePath);

        //Simulate converting the field value to lowercase
        when(criteriaBuilder.lower(firstNamePath))
                .thenReturn(lowerExpression);

        //Expects a case-insensitive contains search:"%büşra%"
        when(criteriaBuilder.like(lowerExpression, "%büşra%"))
                .thenReturn(predicate);

        Specification<Customer> specification =
                CustomerSpecification.withFilters(customerFilterRequest);

        Predicate result = specification
                .toPredicate(root, criteriaQuery, criteriaBuilder);

        assertSame(predicate, result);

        verify(root, times(1)).get("firstName");
        verify(criteriaBuilder, times(1))
                .lower(firstNamePath);
        verify(criteriaBuilder, times(1))
                .like(lowerExpression, "%büşra%");
    }

    // Test that the general search filter creates an OR predicate across
    //firstName, lastName, email and phone
    @Test
    void shouldCreateGeneralPredicateSuccessfully() {
        CustomerFilterRequest customerFilterRequest = new CustomerFilterRequest();
        customerFilterRequest.setSearch("Büşra");

        Root<Customer> root = mock(Root.class);
        CriteriaQuery<?> criteriaQuery = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<String> firstNamePath = mock(Path.class);
        Path<String> lastNamePath = mock(Path.class);
        Path<String> emailPath = mock(Path.class);
        Path<String> phonePath = mock(Path.class);

        Expression<String> firstNameLower = mock(Expression.class);
        Expression<String> lastNameLower = mock(Expression.class);
        Expression<String> emailLower = mock(Expression.class);
        Expression<String> phoneLower = mock(Expression.class);

        Predicate firstNamePredicate = mock(Predicate.class);
        Predicate lastNamePredicate = mock(Predicate.class);
        Predicate emailPredicate = mock(Predicate.class);
        Predicate phonePredicate = mock(Predicate.class);

        Predicate expectedOrPredicate = mock(Predicate.class);

        when(root.<String>get("firstName")).thenReturn(firstNamePath);
        when(root.<String>get("lastName")).thenReturn(lastNamePath);
        when(root.<String>get("email")).thenReturn(emailPath);
        when(root.<String>get("phone")).thenReturn(phonePath);

        when(criteriaBuilder.lower(firstNamePath)).thenReturn(firstNameLower);
        when(criteriaBuilder.lower(lastNamePath)).thenReturn(lastNameLower);
        when(criteriaBuilder.lower(emailPath)).thenReturn(emailLower);
        when(criteriaBuilder.lower(phonePath)).thenReturn(phoneLower);

        when(criteriaBuilder.like(
                firstNameLower, "%büşra%"
        )).thenReturn(firstNamePredicate);

        when(criteriaBuilder.like(lastNameLower, "%büşra%"))
                .thenReturn(lastNamePredicate);

        when(criteriaBuilder.like(emailLower, "%büşra%"))
                .thenReturn(emailPredicate);

        when(criteriaBuilder.like(phoneLower, "%büşra%"))
                .thenReturn(phonePredicate);

        when(criteriaBuilder.or(
                firstNamePredicate,
                lastNamePredicate,
                emailPredicate,
                phonePredicate
        )).thenReturn(expectedOrPredicate);

        Specification<Customer> specification =
                CustomerSpecification.withFilters(customerFilterRequest);
        Predicate result = specification
                .toPredicate(root, criteriaQuery, criteriaBuilder);
        assertSame(expectedOrPredicate, result);

        verify(criteriaBuilder).or(
                firstNamePredicate,
                lastNamePredicate,
                emailPredicate,
                phonePredicate
        );
    }

    // Tests that multiple field filters are combined using AND
    @Test
    void shouldCombineMultipleFiltersWithAndSuccessfully() {
        CustomerFilterRequest customerFilterRequest = new CustomerFilterRequest();
        customerFilterRequest.setFirstName("Büşra");
        customerFilterRequest.setEmail("gmail");

        Root<Customer> root = mock(Root.class);
        CriteriaQuery<?> criteriaQuery = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<String> firstNamePath = mock(Path.class);
        Path<String> emailPath = mock(Path.class);

        Expression<String> firstNameLower = mock(Expression.class);
        Expression<String> emailLower = mock(Expression.class);

        Predicate firstNamePredicate = mock(Predicate.class);
        Predicate emailPredicate = mock(Predicate.class);

        Predicate expectedAndPredicate = mock(Predicate.class);

        //Mock Customer.firstName and Customer.email paths
        when(root.<String>get("firstName")).thenReturn(firstNamePath);
        when(root.<String>get("email")).thenReturn(emailPath);

        //Simulates lowercase conversion
        when(criteriaBuilder.lower(firstNamePath)).thenReturn(firstNameLower);
        when(criteriaBuilder.lower(emailPath)).thenReturn(emailLower);

        // Creates case-insensitive contains predicates
        when(criteriaBuilder.like(firstNameLower, "%büşra%"))
        .thenReturn(firstNamePredicate);

        when(criteriaBuilder.like(emailLower, "%gmail%"))
                .thenReturn(emailPredicate);
        //Multiple specifications must be combined using AND
        when(criteriaBuilder.and(
                firstNamePredicate,
                emailPredicate
        )).thenReturn(expectedAndPredicate);
        Specification<Customer> specification =
                CustomerSpecification.withFilters(customerFilterRequest);
        Predicate result = specification
                .toPredicate(root, criteriaQuery, criteriaBuilder);
        assertSame(expectedAndPredicate, result);

        verify(criteriaBuilder, times(1))
                .like(firstNameLower, "%büşra%");
        verify(criteriaBuilder, times(1))
        .like(emailLower, "%gmail%");
        verify(criteriaBuilder, times(1))
                .and(firstNamePredicate, emailPredicate);



    }

    //Tests that an empty filter does not add
    @Test
    void shouldCreateSpecificationSuccessfullyWhenFilterIsEmpty() {
        CustomerFilterRequest customerFilterRequest = new CustomerFilterRequest();

        Root<Customer> root = mock(Root.class);
        CriteriaQuery<?> criteriaQuery = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Specification<Customer> specification =
                CustomerSpecification.withFilters(customerFilterRequest);

        Predicate result = specification
                .toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(specification);

        //No Customer field should be accessed because no filtering condition was provided
        verify(root, never()).get(anyString());

    }
}
