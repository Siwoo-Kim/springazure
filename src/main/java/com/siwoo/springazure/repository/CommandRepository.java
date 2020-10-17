package com.siwoo.springazure.repository;

import com.siwoo.springazure.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository extends JpaRepository<Command, Long> {

}
