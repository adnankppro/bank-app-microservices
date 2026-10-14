package com.adnan.bankapp.banking.transaction.specification;

import com.adnan.bankapp.banking.account.entity.BankAccount;
import com.adnan.bankapp.banking.transaction.dto.TransactionFilter;
import com.adnan.bankapp.banking.transaction.entity.Transaction;
import com.adnan.bankapp.banking.transaction.enums.TransactionType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> build(TransactionFilter filter, String accountNumber){


        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Transaction, BankAccount> sender = root.join("sender", JoinType.LEFT);
            Join<Transaction, BankAccount> receiver = root.join("receiver", JoinType.LEFT);

            if (filter.type() != null){
                switch (filter.type()){
                    case SEND -> {
                        predicates.add(
                                cb.equal(sender.get("accountNumber"), accountNumber)
                        );
                    }
                    case RECEIVE, DEPOSIT -> {
                        predicates.add(
                                cb.equal(receiver.get("accountNumber"), accountNumber)
                        );
                    }
                }
            }else {
                predicates.add(cb.or(
                        cb.equal(sender.get("accountNumber"), accountNumber),
                        cb.equal(receiver.get("accountNumber"), accountNumber)
                ));
            }


            if (filter.amountGreaterThan() != null){
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("amount"), filter.amountGreaterThan()
                        )
                );
            }

            if (filter.amountLessThan() != null){
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("amount"), filter.amountLessThan())
                );
            }

            if (filter.counterPartyAccountNumber() != null){
                predicates.add(cb.or(
                        cb.equal(sender.get("accountNumber"), filter.counterPartyAccountNumber()),
                        cb.equal(receiver.get("accountNumber"), filter.counterPartyAccountNumber())
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
