package com.adnan.bankapp.profile.specification;

import com.adnan.bankapp.profile.dto.ProfileFilter;
import com.adnan.bankapp.profile.entity.Profile;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProfileSpecification {

    public static Specification<Profile> build(ProfileFilter filter){

        return new Specification<Profile>() {
            @Override
            public Predicate toPredicate(@NonNull Root<Profile> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<>();

                if( filter.name() != null && !filter.name().isBlank()){
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.lower(root.get("name"))),"%"+filter.name().toLowerCase()+"%"));
                }

                if( filter.address() != null && !filter.address().isBlank()){
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")),"%"+filter.address().toLowerCase()+"%"));
                }

                if( filter.email() != null && !filter.email().isBlank()){
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),"%"+filter.email().toLowerCase()+"%"));
                }

                if( filter.branch() != null ){
                    predicates.add(criteriaBuilder.equal(root.get("branch"),filter.branch()));
                }

                if( filter.status() != null ){
                    predicates.add(criteriaBuilder.equal(root.get("status"),filter.status()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

}
