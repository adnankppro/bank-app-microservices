package com.adnan.bankapp.banking.account.repository;

import com.adnan.bankapp.banking.account.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {

    boolean existsByAccountTypeName(String accountTypeName);
}
