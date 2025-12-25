package com.scalar.events_log_tool.application.security;


import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class SecurityBeans {

    private final UserRepository userRepository;

    private final DistributedTransactionManager transactionManager;

    public SecurityBeans(UserRepository userRepository, DistributedTransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    public static void commitTransaction(DistributedTransaction transaction) {

        try {
            transaction.commit();
        } catch (TransactionException te) {
            log.info("Error While committing transaction");
            te.printStackTrace();
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException("Unable to Commit Transaction..");

        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            log.info("UserEmail:" + username);
            DistributedTransaction tx = getNewTransaction();
            User user = userRepository.getByUserEmail(username, tx);
            if (user == null) {
                throw new GenericException("User not found");
            }
            UserDetails userDetails = new UserInfoDetails(user);
            commitTransaction(tx);
            return userDetails;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    public DistributedTransaction getNewTransaction() {
        try {
            return transactionManager.start();
        } catch (TransactionException te) {
            te.printStackTrace();
            throw new GenericException("Unable to Start Transaction..");
        }
    }

}
