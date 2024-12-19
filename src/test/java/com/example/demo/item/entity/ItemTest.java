package com.example.demo.item.entity;
import com.example.demo.item.repository.ItemRepository;
import com.example.demo.user.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getDefaultStatusIsNull() {
        User owner = new User();
        User manager = new User();

        Item item = Item.builder()
                .owner(owner)
                .manager(manager)
                .description("test")
                .status(null)  // 명시적으로 null 지정
                .build();

        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            itemRepository.save(item);
            itemRepository.flush();  // 즉시 데이터베이스 동기화
        });
    }
}